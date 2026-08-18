package com.educalab.atumanera.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import com.educalab.atumanera.domain.model.InfraCategory
import com.educalab.atumanera.ui.TileVisual
import kotlin.math.floor

private val GRASS_A = Color(0xFFBFE6A0)
private val GRASS_B = Color(0xFFAEDB8C)

/**
 * Pinta la ciudad como una cuadrícula isométrica simplificada: cada casilla
 * es un rombo. Las casillas ocupadas muestran además el icono temático de su
 * infraestructura. Los toques se traducen a la fila/columna correspondiente.
 */
@Composable
fun CityGridCanvas(
    tiles: List<TileVisual>,
    rows: Int,
    cols: Int,
    modifier: Modifier = Modifier,
    highlightCategory: InfraCategory? = null,
    onTileTap: (row: Int, col: Int) -> Unit
) {
    val tileByPos = remember(tiles) { tiles.associateBy { it.tile.row to it.tile.col } }
    val density = LocalDensity.current
    val currentOnTileTap = rememberUpdatedState(onTileTap)

    BoxWithConstraints(modifier = modifier) {
        val boardWidthPx = constraints.maxWidth.toFloat()
        val tileW = boardWidthPx / (rows + cols) * 2f
        val tileH = tileW / 2f
        val originX = boardWidthPx / 2f
        val originY = tileH / 2f

        fun screenCenter(row: Int, col: Int): Offset {
            val x = originX + (col - row) * (tileW / 2f)
            val y = originY + (col + row) * (tileH / 2f)
            return Offset(x, y)
        }

        val boardHeightPx = originY + (rows + cols) * (tileH / 2f) + tileH

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(with(density) { boardHeightPx.toDp() })
                .pointerInput(rows, cols, tileW, tileH) {
                    detectTapGestures { tapOffset ->
                        val relX = tapOffset.x - originX
                        val relY = tapOffset.y - originY
                        val colMinusRow = relX / (tileW / 2f)
                        val colPlusRow = relY / (tileH / 2f)
                        val col = floor((colMinusRow + colPlusRow) / 2f).toInt()
                        val row = floor((colPlusRow - colMinusRow) / 2f).toInt()
                        if (row in 0 until rows && col in 0 until cols) currentOnTileTap.value(row, col)
                    }
                }
        ) {
            for (row in 0 until rows) {
                for (col in 0 until cols) {
                    val center = screenCenter(row, col)
                    val visual = tileByPos[row to col]
                    val category = visual?.infraType?.category?.let { InfraCategory.valueOf(it) }
                    val baseColor = if (category != null) categoryVisual(category).color.copy(alpha = 0.85f)
                    else if ((row + col) % 2 == 0) GRASS_A else GRASS_B

                    val highlighted = highlightCategory != null && category == highlightCategory
                    val path = Path().apply {
                        moveTo(center.x, center.y - tileH / 2f)
                        lineTo(center.x + tileW / 2f, center.y)
                        lineTo(center.x, center.y + tileH / 2f)
                        lineTo(center.x - tileW / 2f, center.y)
                        close()
                    }
                    drawPath(path, color = baseColor)
                    drawPath(
                        path,
                        color = if (highlighted) Color(0xFFF6A623) else Color(0x33143355),
                        style = Stroke(width = if (highlighted) 4f else 1.2f)
                    )
                }
            }
        }

        // Capa de iconos superpuesta sobre las casillas ocupadas.
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val visual = tileByPos[row to col] ?: continue
                val infraCode = visual.infraType?.code ?: continue
                val center = screenCenter(row, col)
                val iconSizeDp = with(density) { (tileW * 0.55f).toDp() }
                Image(
                    painter = painterResource(infraIconRes(infraCode)),
                    contentDescription = visual.infraType?.name,
                    modifier = Modifier
                        .size(iconSizeDp)
                        .offset(
                            x = with(density) { (center.x - tileW * 0.275f).toDp() },
                            y = with(density) { (center.y - tileW * 0.42f).toDp() }
                        )
                )
            }
        }
    }
}
