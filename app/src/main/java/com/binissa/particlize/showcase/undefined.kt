package com.binissa.particlize.showcase

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val circle: ImageVector
	get() {
		if (_Circle != null) {
			return _Circle!!
		}
		_Circle = ImageVector.Builder(
            name = "Circle",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
			path(
    			fill = SolidColor(Color.Black),
    			fillAlpha = 1.0f,
    			stroke = null,
    			strokeAlpha = 1.0f,
    			strokeLineWidth = 1.0f,
    			strokeLineCap = StrokeCap.Butt,
    			strokeLineJoin = StrokeJoin.Miter,
    			strokeLineMiter = 1.0f,
    			pathFillType = PathFillType.NonZero
			) {
				moveTo(480f, 880f)
				quadToRelative(-83f, 0f, -156f, -31.5f)
				reflectiveQuadTo(197f, 763f)
				reflectiveQuadToRelative(-85.5f, -127f)
				reflectiveQuadTo(80f, 480f)
				reflectiveQuadToRelative(31.5f, -156f)
				reflectiveQuadTo(197f, 197f)
				reflectiveQuadToRelative(127f, -85.5f)
				reflectiveQuadTo(480f, 80f)
				reflectiveQuadToRelative(156f, 31.5f)
				reflectiveQuadTo(763f, 197f)
				reflectiveQuadToRelative(85.5f, 127f)
				reflectiveQuadTo(880f, 480f)
				reflectiveQuadToRelative(-31.5f, 156f)
				reflectiveQuadTo(763f, 763f)
				reflectiveQuadToRelative(-127f, 85.5f)
				reflectiveQuadTo(480f, 880f)
				moveToRelative(0f, -80f)
				quadToRelative(134f, 0f, 227f, -93f)
				reflectiveQuadToRelative(93f, -227f)
				reflectiveQuadToRelative(-93f, -227f)
				reflectiveQuadToRelative(-227f, -93f)
				reflectiveQuadToRelative(-227f, 93f)
				reflectiveQuadToRelative(-93f, 227f)
				reflectiveQuadToRelative(93f, 227f)
				reflectiveQuadToRelative(227f, 93f)
				moveToRelative(0f, -320f)
			}
		}.build()
		return _Circle!!
	}

private var _Circle: ImageVector? = null


public val puzzle: ImageVector
	get() {
		if (_Puzzle != null) {
			return _Puzzle!!
		}
		_Puzzle = ImageVector.Builder(
			name = "Puzzle",
			defaultWidth = 24.dp,
			defaultHeight = 24.dp,
			viewportWidth = 24f,
			viewportHeight = 24f
		).apply {
			path(
				fill = null,
				fillAlpha = 1.0f,
				stroke = SolidColor(Color(0xFF000000)),
				strokeAlpha = 1.0f,
				strokeLineWidth = 2f,
				strokeLineCap = StrokeCap.Round,
				strokeLineJoin = StrokeJoin.Round,
				strokeLineMiter = 1.0f,
				pathFillType = PathFillType.NonZero
			) {
				moveTo(19.439f, 7.85f)
				curveToRelative(-0.049f, 0.322f, 0.059f, 0.648f, 0.289f, 0.878f)
				lineToRelative(1.568f, 1.568f)
				curveToRelative(0.47f, 0.47f, 0.706f, 1.087f, 0.706f, 1.704f)
				reflectiveCurveToRelative(-0.235f, 1.233f, -0.706f, 1.704f)
				lineToRelative(-1.611f, 1.611f)
				arcToRelative(0.98f, 0.98f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.837f, 0.276f)
				curveToRelative(-0.47f, -0.07f, -0.802f, -0.48f, -0.968f, -0.925f)
				arcToRelative(2.501f, 2.501f, 0f, isMoreThanHalf = true, isPositiveArc = false, -3.214f, 3.214f)
				curveToRelative(0.446f, 0.166f, 0.855f, 0.497f, 0.925f, 0.968f)
				arcToRelative(0.979f, 0.979f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.276f, 0.837f)
				lineToRelative(-1.61f, 1.61f)
				arcToRelative(2.404f, 2.404f, 0f, isMoreThanHalf = false, isPositiveArc = true, -1.705f, 0.707f)
				arcToRelative(2.402f, 2.402f, 0f, isMoreThanHalf = false, isPositiveArc = true, -1.704f, -0.706f)
				lineToRelative(-1.568f, -1.568f)
				arcToRelative(1.026f, 1.026f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.877f, -0.29f)
				curveToRelative(-0.493f, 0.074f, -0.84f, 0.504f, -1.02f, 0.968f)
				arcToRelative(2.5f, 2.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, -3.237f, -3.237f)
				curveToRelative(0.464f, -0.18f, 0.894f, -0.527f, 0.967f, -1.02f)
				arcToRelative(1.026f, 1.026f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.289f, -0.877f)
				lineToRelative(-1.568f, -1.568f)
				arcTo(2.402f, 2.402f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1.998f, 12f)
				curveToRelative(0f, -0.617f, 0.236f, -1.234f, 0.706f, -1.704f)
				lineTo(4.23f, 8.77f)
				curveToRelative(0.24f, -0.24f, 0.581f, -0.353f, 0.917f, -0.303f)
				curveToRelative(0.515f, 0.077f, 0.877f, 0.528f, 1.073f, 1.01f)
				arcToRelative(2.5f, 2.5f, 0f, isMoreThanHalf = true, isPositiveArc = false, 3.259f, -3.259f)
				curveToRelative(-0.482f, -0.196f, -0.933f, -0.558f, -1.01f, -1.073f)
				curveToRelative(-0.05f, -0.336f, 0.062f, -0.676f, 0.303f, -0.917f)
				lineToRelative(1.525f, -1.525f)
				arcTo(2.402f, 2.402f, 0f, isMoreThanHalf = false, isPositiveArc = true, 12f, 1.998f)
				curveToRelative(0.617f, 0f, 1.234f, 0.236f, 1.704f, 0.706f)
				lineToRelative(1.568f, 1.568f)
				curveToRelative(0.23f, 0.23f, 0.556f, 0.338f, 0.877f, 0.29f)
				curveToRelative(0.493f, -0.074f, 0.84f, -0.504f, 1.02f, -0.968f)
				arcToRelative(2.5f, 2.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, 3.237f, 3.237f)
				curveToRelative(-0.464f, 0.18f, -0.894f, 0.527f, -0.967f, 1.02f)
				close()
			}
		}.build()
		return _Puzzle!!
	}

private var _Puzzle: ImageVector? = null
