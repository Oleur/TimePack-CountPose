package com.timepack.countpose.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.coil.rememberCoilPainter
import com.timepack.countpose.theme.dark
import com.timepack.countpose.theme.green700
import com.timepack.countpose.theme.greenLight700
import com.timepack.countpose.theme.red700
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SmileyFaceCanvas(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier.size(200.dp),
        onDraw = {
            // Head
            drawCircle(
                Brush.linearGradient(
                    colors = listOf(greenLight700, green700)
                ),
                radius = size.width / 2,
                center = center,
                style = Stroke(width = size.width * 0.075f)
            )

            // Smile
            val smilePadding = size.width * 0.15f
            drawArc(
                color = red700,
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(smilePadding, smilePadding),
                size = Size(size.width - (smilePadding * 2f), size.height - (smilePadding * 2f))
            )

            // Left eye
            drawRect(
                color = dark,
                topLeft = Offset(size.width * 0.25f, size.height / 4),
                size = Size(smilePadding, smilePadding)
            )

            // Right eye
            drawRect(
                color = dark,
                topLeft = Offset((size.width * 0.75f) - smilePadding, size.height / 4),
                size = Size(smilePadding, smilePadding)
            )
        }
    )
}

@Composable
fun TicketComposable(modifier: Modifier = Modifier) {
    Text(
        text = "Hello London\n🎉 FREE TICKET 🎉",
        style = TextStyle(
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
        ),
        textAlign = TextAlign.Center,
        modifier = modifier
            .wrapContentSize()
            .graphicsLayer {
                shadowElevation = 8.dp.toPx()
                shape = TicketShape(24.dp.toPx())
                clip = true
            }
            .background(color = dark)
            .drawBehind {
                scale(scale = 0.9f) {
                    drawPath(
                        path = drawTicketPath(size = size, cornerRadius = 24.dp.toPx()),
                        color = red700,
                        style = Stroke(
                            width = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                        )
                    )
                }
            }
            .padding(start = 32.dp, top = 64.dp, end = 32.dp, bottom = 64.dp)
    )
}

@Composable
fun TicketImageComposable(modifier: Modifier = Modifier) {
    Image(
        painter = rememberCoilPainter(
            request = "https://images.unsplash.com/photo-1574267432553-4b4628081c31?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=2389&q=80",
        ),
        contentDescription = "Awesome Image",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(width = 300.dp, height = 200.dp)
            .graphicsLayer {
                shadowElevation = 8.dp.toPx()
                shape = TicketShape(24.dp.toPx())
                clip = true
            }
    )
}

@Composable
fun TicketWaveComposable(modifier: Modifier = Modifier) {
    val deltaXAnim = rememberInfiniteTransition()
    val dx by deltaXAnim.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing)
        )
    )

    Image(
        painter = rememberCoilPainter(
            request = "https://images.unsplash.com/photo-1574267432553-4b4628081c31?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=2389&q=80"
        ),
        contentDescription = "Awesome Image",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(width = 300.dp, height = 200.dp)
            .graphicsLayer {
                val animDx = dx
                shadowElevation = 0.dp.toPx()
                shape = GenericShape { size: Size, _: LayoutDirection ->
                    wavePath(size, dx, 50.dp.toPx())
                }
                clip = true
            }
    )
}

@Composable
fun PolygonImageComposable(modifier: Modifier = Modifier) {
    val deltaXAnim = rememberInfiniteTransition()
    val dx by deltaXAnim.animateFloat(
        initialValue = 3f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Image(
        painter = rememberCoilPainter(
            request = "https://images.unsplash.com/photo-1574267432553-4b4628081c31?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=2389&q=80"
        ),
        contentDescription = "Awesome Image",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(width = 300.dp, height = 200.dp)
            .graphicsLayer {
                shadowElevation = 4.dp.toPx()
                shape = PolyShape(dx.toInt(), 100.dp.toPx())
                clip = true
            }
    )
}

private fun Path.wavePath(size: Size, dx: Float, originalY: Float) {
    val waveWidth = 200
    reset()
    val halfWaveWidth = waveWidth / 2
    moveTo(-waveWidth + (waveWidth * dx), originalY)

    for (i in -waveWidth..(size.width.toInt() + waveWidth) step waveWidth) {
        relativeQuadraticBezierTo(
            halfWaveWidth.toFloat() / 2,
            -125f,
            halfWaveWidth.toFloat(),
            0f
        )
        relativeQuadraticBezierTo(
            halfWaveWidth.toFloat() / 2,
            125f,
            halfWaveWidth.toFloat(),
            0f
        )
    }

    lineTo(size.width, size.height)
    lineTo(0f, size.height)
    close()
}

class TicketShape(private val cornerRadius: Float) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            path = drawTicketPath(size = size, cornerRadius = cornerRadius)
        )
    }
}

fun drawTicketPath(size: Size, cornerRadius: Float): Path {
    return Path().apply {
        reset()
        // Top left arc
        arcTo(
            rect = Rect(
                left = -cornerRadius,
                top = -cornerRadius,
                right = cornerRadius,
                bottom = cornerRadius
            ),
            startAngleDegrees = 90.0f,
            sweepAngleDegrees = -90.0f,
            forceMoveTo = false
        )
        lineTo(x = size.width - cornerRadius, y = 0f)
        // Top right arc
        arcTo(
            rect = Rect(
                left = size.width - cornerRadius,
                top = -cornerRadius,
                right = size.width + cornerRadius,
                bottom = cornerRadius
            ),
            startAngleDegrees = 180.0f,
            sweepAngleDegrees = -90.0f,
            forceMoveTo = false
        )
        lineTo(x = size.width, y = size.height - cornerRadius)
        // Bottom right arc
        arcTo(
            rect = Rect(
                left = size.width - cornerRadius,
                top = size.height - cornerRadius,
                right = size.width + cornerRadius,
                bottom = size.height + cornerRadius
            ),
            startAngleDegrees = 270.0f,
            sweepAngleDegrees = -90.0f,
            forceMoveTo = false
        )
        lineTo(x = cornerRadius, y = size.height)
        // Bottom left arc
        arcTo(
            rect = Rect(
                left = -cornerRadius,
                top = size.height - cornerRadius,
                right = cornerRadius,
                bottom = size.height + cornerRadius
            ),
            startAngleDegrees = 0.0f,
            sweepAngleDegrees = -90.0f,
            forceMoveTo = false
        )
        lineTo(x = 0f, y = cornerRadius)
        close()
    }
}

fun Path.polygon(sides: Int, radius: Float, center: Offset) {
    val angle = 2.0 * Math.PI / sides
    moveTo(
        x = center.x + (radius * cos(0.0)).toFloat(),
        y = center.y + (radius * sin(0.0)).toFloat()
    )
    for (i in 1 until sides) {
        lineTo(
            x = center.x + (radius * cos(angle * i)).toFloat(),
            y = center.y + (radius * sin(angle * i)).toFloat()
        )
    }
    close()
}

class PolyShape(private val sides: Int, private val radius: Float) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            path = Path().apply {
                polygon(sides, radius, size.center)
            }
        )
    }

}