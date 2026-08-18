package com.educalab.atumanera.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.animation.core.Animatable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import com.educalab.atumanera.domain.model.InfraCategory
import com.educalab.atumanera.ui.TileVisual
import kotlin.math.floor
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

private val GRASS_A = Color(0xFFBFE6A0)
private val GRASS_B = Color(0xFFAEDB8C)

private fun Color.darken(factor: Float): Color = Color(
    red = (red * factor).coerceIn(0f, 1f),
    green = (green * factor).coerceIn(0f, 1f),
    blue = (blue * factor).coerceIn(0f, 1f),
    alpha = alpha
)

private fun Color.lighten(factor: Float): Color = Color(
    red = (red + (1f - red) * factor).coerceIn(0f, 1f),
    green = (green + (1f - green) * factor).coerceIn(0f, 1f),
    blue = (blue + (1f - blue) * factor).coerceIn(0f, 1f),
    alpha = alpha
)

/**
 * Pinta la ciudad como una cuadrícula isométrica: cada casilla es un rombo de
 * césped y, si tiene una construcción, se dibuja un pequeño bloque en 3D con
 * caras laterales sombreadas (las calles quedan planas, como pavimento, para
 * que se vean conectadas entre sí). Los toques se traducen a fila/columna.
 * Si se provee [onLineDrag], además de tocar una casilla se puede arrastrar
 * en línea recta para construir varias casillas seguidas (solo Modo Libre).
 */
@Composable
fun CityGridCanvas(
    tiles: List<TileVisual>,
    rows: Int,
    cols: Int,
    modifier: Modifier = Modifier,
    highlightCategory: InfraCategory? = null,
    onTileTap: (row: Int, col: Int) -> Unit,
    onLineDrag: ((rowStart: Int, colStart: Int, rowEnd: Int, colEnd: Int) -> Unit)? = null
) {
    val tileByPos = remember(tiles) { tiles.associateBy { it.tile.row to it.tile.col } }
    val density = LocalDensity.current
    val currentOnTileTap = rememberUpdatedState(onTileTap)
    val currentOnLineDrag = rememberUpdatedState(onLineDrag)

    BoxWithConstraints(modifier = modifier) {
        val boardWidthPx = constraints.maxWidth.toFloat()
        val tileW = boardWidthPx / (rows + cols) * 2f
        val tileH = tileW / 2f
        val originX = boardWidthPx / 2f
        val originY = tileH / 2f
        val blockHeightPx = tileH * 0.2f
        val blockScale = 0.58f

        fun screenCenter(row: Int, col: Int): Offset {
            val x = originX + (col - row) * (tileW / 2f)
            val y = originY + (col + row) * (tileH / 2f)
            return Offset(x, y)
        }

        fun resolveTile(point: Offset): Pair<Int, Int> {
            val relX = point.x - originX
            val relY = point.y - originY
            val colMinusRow = relX / (tileW / 2f)
            val colPlusRow = relY / (tileH / 2f)
            val col = floor((colMinusRow + colPlusRow) / 2f).toInt()
            val row = floor((colPlusRow - colMinusRow) / 2f).toInt()
            return row to col
        }

        val boardHeightPx = originY + (rows + cols) * (tileH / 2f) + tileH

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(with(density) { boardHeightPx.toDp() })
                .pointerInput(rows, cols, tileW, tileH, onLineDrag != null) {
                    // Se ejecutan dos detectores en paralelo: uno de toque
                    // (para colocar una sola casilla, incluso si hay arrastre
                    // activo) y, si corresponde, uno de arrastre en línea recta
                    // (que solo actúa cuando el gesto supera el umbral de
                    // movimiento; un toque simple nunca dispara el arrastre,
                    // así que ambos conviven sin pisarse).
                    coroutineScope {
                        launch {
                            detectTapGestures { tapOffset ->
                                val (row, col) = resolveTile(tapOffset)
                                if (row in 0 until rows && col in 0 until cols) currentOnTileTap.value(row, col)
                            }
                        }
                        if (currentOnLineDrag.value != null) {
                            launch {
                                var startRow = -1
                                var startCol = -1
                                var lastRow = -1
                                var lastCol = -1
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        val (r, c) = resolveTile(offset)
                                        startRow = r; startCol = c; lastRow = r; lastCol = c
                                    },
                                    onDragEnd = {
                                        if (startRow in 0 until rows && startCol in 0 until cols &&
                                            lastRow in 0 until rows && lastCol in 0 until cols &&
                                            !(startRow == lastRow && startCol == lastCol)
                                        ) {
                                            currentOnLineDrag.value?.invoke(startRow, startCol, lastRow, lastCol)
                                        }
                                    }
                                ) { change, _ ->
                                    val (r, c) = resolveTile(change.position)
                                    lastRow = r; lastCol = c
                                }
                            }
                        }
                    }
                }
        ) {
            for (row in 0 until rows) {
                for (col in 0 until cols) {
                    val center = screenCenter(row, col)
                    val visual = tileByPos[row to col]
                    val category = visual?.infraType?.category?.let { InfraCategory.valueOf(it) }
                    val isRoad = category == InfraCategory.ROAD
                    val groundColor = if ((row + col) % 2 == 0) GRASS_A else GRASS_B
                    val highlighted = highlightCategory != null && category == highlightCategory

                    // Rombo de suelo: césped, o pavimento plano y continuo si es calle.
                    val groundFill = if (isRoad) categoryVisual(InfraCategory.ROAD).color.copy(alpha = 0.9f) else groundColor
                    val groundPath = Path().apply {
                        moveTo(center.x, center.y - tileH / 2f)
                        lineTo(center.x + tileW / 2f, center.y)
                        lineTo(center.x, center.y + tileH / 2f)
                        lineTo(center.x - tileW / 2f, center.y)
                        close()
                    }
                    drawPath(
                        groundPath,
                        brush = Brush.linearGradient(
                            colors = listOf(groundFill.lighten(0.18f), groundFill.darken(0.92f)),
                            start = Offset(center.x - tileW / 2f, center.y - tileH / 2f),
                            end = Offset(center.x + tileW / 2f, center.y + tileH / 2f)
                        )
                    )
                    drawPath(
                        groundPath,
                        color = if (highlighted) Color(0xFFF6A623) else Color(0x33143355),
                        style = Stroke(width = if (highlighted) 4f else 1.2f)
                    )

                    // Bloque elevado en 3D para construcciones que no son calles.
                    if (category != null && !isRoad) {
                        val blockColor = categoryVisual(category).color
                        val topN = Offset(center.x, center.y - (tileH * blockScale) / 2f)
                        val topE = Offset(center.x + (tileW * blockScale) / 2f, center.y)
                        val topS = Offset(center.x, center.y + (tileH * blockScale) / 2f)
                        val topW = Offset(center.x - (tileW * blockScale) / 2f, center.y)

                        // Sombra suave en el césped, bajo el bloque.
                        drawPath(
                            Path().apply {
                                moveTo(topN.x, topN.y + 3f)
                                lineTo(topE.x, topE.y + 3f)
                                lineTo(topS.x, topS.y + 3f)
                                lineTo(topW.x, topW.y + 3f)
                                close()
                            },
                            color = Color(0x22143355)
                        )

                        // Cara izquierda (más oscura).
                        drawPath(
                            Path().apply {
                                moveTo(topW.x, topW.y)
                                lineTo(topS.x, topS.y)
                                lineTo(topS.x, topS.y + blockHeightPx)
                                lineTo(topW.x, topW.y + blockHeightPx)
                                close()
                            },
                            color = blockColor.darken(0.55f)
                        )
                        // Cara derecha (tono medio).
                        drawPath(
                            Path().apply {
                                moveTo(topS.x, topS.y)
                                lineTo(topE.x, topE.y)
                                lineTo(topE.x, topE.y + blockHeightPx)
                                lineTo(topS.x, topS.y + blockHeightPx)
                                close()
                            },
                            color = blockColor.darken(0.75f)
                        )
                        // Techo (color base, con leve brillo).
                        drawPath(
                            Path().apply {
                                moveTo(topN.x, topN.y)
                                lineTo(topE.x, topE.y)
                                lineTo(topS.x, topS.y)
                                lineTo(topW.x, topW.y)
                                close()
                            },
                            brush = Brush.linearGradient(
                                colors = listOf(blockColor.lighten(0.25f), blockColor),
                                start = topN,
                                end = topS
                            )
                        )
                    }
                }
            }
        }

        // Capa de iconos superpuesta sobre las casillas ocupadas, con una
        // pequeña animación de aparición cuando se coloca una construcción.
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val visual = tileByPos[row to col] ?: continue
                val infraCode = visual.infraType?.code ?: continue
                val category = visual.infraType?.category?.let { InfraCategory.valueOf(it) }
                val isRoad = category == InfraCategory.ROAD
                val center = screenCenter(row, col)
                val iconSizeDp = with(density) { (tileW * 0.5f).toDp() }
                val liftPx = if (isRoad) 0f else blockHeightPx

                key(row, col, infraCode) {
                    val scale = remember { Animatable(0.2f) }
                    LaunchedEffect(Unit) {
                        scale.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
                    }
                    Image(
                        painter = painterResource(infraIconRes(infraCode)),
                        contentDescription = visual.infraType?.name,
                        modifier = Modifier
                            .size(iconSizeDp)
                            .offset(
                                x = with(density) { (center.x - tileW * 0.25f).toDp() },
                                y = with(density) { (center.y - tileW * 0.4f - liftPx).toDp() }
                            )
                            .graphicsLayer(
                                scaleX = scale.value,
                                scaleY = scale.value,
                                transformOrigin = TransformOrigin(0.5f, 1f)
                            )
                    )
                }
            }
        }
    }
}
