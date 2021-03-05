/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.timepack.countpose.home

import android.graphics.Typeface
import android.text.format.DateUtils
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.timepack.countpose.R
import com.timepack.countpose.TimePackViewModel
import com.timepack.countpose.theme.blue700
import com.timepack.countpose.theme.green200
import com.timepack.countpose.theme.red700
import com.timepack.countpose.theme.typography

@ExperimentalAnimationApi
@Composable
fun HomeScreen(timePackViewModel: TimePackViewModel) {
    val context = LocalContext.current

    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (topBar, wave, playButton, resetButton) = createRefs()

        TopAppBar(
            elevation = 4.dp,
            modifier = Modifier
                .constrainAs(topBar) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 12.dp),
                textAlign = TextAlign.Center,
                text = context.getString(R.string.app_name),
                style = typography.h5
            )
        }

        val playPauseState = remember { mutableStateOf(true) }

        TimeWave(
            timeSpec = timePackViewModel.timeState.value * DateUtils.SECOND_IN_MILLIS,
            playPauseState.value,
            timePackViewModel,
            modifier = Modifier
                .constrainAs(wave) {
                    top.linkTo(topBar.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
        )

        AnimatedVisibility(
            modifier = Modifier
                .constrainAs(playButton) {
                    end.linkTo(parent.end, margin = 32.dp)
                    bottom.linkTo(parent.bottom, margin = 32.dp)
                },
            visible = playPauseState.value,
            enter = slideInHorizontally(
                initialOffsetX = { fullWidth ->
                    fullWidth + fullWidth / 2
                }
            ) + fadeIn(),
            exit = slideOutHorizontally(
                targetOffsetX = { fullWidth ->
                    fullWidth + fullWidth / 2
                }
            ) + fadeOut()
        ) {
            Button(
                shape = RoundedCornerShape(36.dp),
                onClick = {
                    playPauseState.value = false
                    timePackViewModel.startTimer(10)
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = blue700),
                modifier = Modifier.size(72.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_play_24),
                    contentDescription = "Play Icon",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        AnimatedVisibility(
            visible = !playPauseState.value,
            modifier = Modifier
                .constrainAs(resetButton) {
                    start.linkTo(parent.start, margin = 32.dp)
                    bottom.linkTo(parent.bottom, margin = 32.dp)
                }
        ) {
            Button(
                shape = RoundedCornerShape(36.dp),
                onClick = {
                    playPauseState.value = true
                    timePackViewModel.stop(10)
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = blue700),
                modifier = Modifier.size(72.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_reset),
                    contentDescription = "Pause Icon",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun TimeWave(
    timeSpec: Long,
    init: Boolean,
    timePackViewModel: TimePackViewModel,
    modifier: Modifier
) {
    val animColor by animateColorAsState(
        targetValue = if (init) green200 else red700,
        animationSpec = TweenSpec(if (init) 0 else timeSpec.toInt(), easing = LinearEasing)
    )

    val deltaXAnim = rememberInfiniteTransition()
    val dx by deltaXAnim.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing)
        )
    )

    val screenWidthPx = with(LocalDensity.current) {
        LocalConfiguration.current.screenHeightDp * density
    }
    val animTranslate by animateFloatAsState(
        targetValue = if (init) 0f else screenWidthPx - 350f,
        animationSpec = TweenSpec(if (init) 0 else timeSpec.toInt(), easing = LinearEasing)
    )

    val waveHeight by animateFloatAsState(
        targetValue = if (init) 125f else 0f,
        animationSpec = TweenSpec(if (init) 0 else timeSpec.toInt(), easing = LinearEasing)
    )

    val infiniteScale = rememberInfiniteTransition()
    val animAlertScale by infiniteScale.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val waveWidth = 200
    val originalY = 350f
    val path = Path()
    val textPaint = Paint().asFrameworkPaint()

    Canvas(
        modifier = Modifier.fillMaxSize(),
        onDraw = {
            translate(top = animTranslate) {
                drawPath(path = path, color = animColor)
                path.reset()
                val halfWaveWidth = waveWidth / 2
                path.moveTo(-waveWidth + (waveWidth * dx), originalY)

                for (i in -waveWidth..(size.width.toInt() + waveWidth) step waveWidth) {
                    path.relativeQuadraticBezierTo(
                        halfWaveWidth.toFloat() / 2,
                        -waveHeight,
                        halfWaveWidth.toFloat(),
                        0f
                    )
                    path.relativeQuadraticBezierTo(
                        halfWaveWidth.toFloat() / 2,
                        waveHeight,
                        halfWaveWidth.toFloat(),
                        0f
                    )
                }

                path.lineTo(size.width, size.height)
                path.lineTo(0f, size.height)
                path.close()

                scale(scale = if (timePackViewModel.alertState.value) animAlertScale else 1f) {
                    drawIntoCanvas {
                        textPaint.apply {
                            isAntiAlias = true
                            textSize = 48.sp.toPx()
                            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                        }
                        it.nativeCanvas.drawText(
                            DateUtils.formatElapsedTime(timePackViewModel.timeState.value),
                            (size.width / 2) - 200,
                            250f,
                            textPaint
                        )
                    }
                }
            }
        }
    )
}
