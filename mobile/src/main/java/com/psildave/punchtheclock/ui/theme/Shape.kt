package com.psildave.punchtheclock.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val PunchShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

val ShapeChip = RoundedCornerShape(8.dp)
val ShapeCard = RoundedCornerShape(24.dp)
val ShapeDialog = RoundedCornerShape(28.dp)
val ShapeBottomSheet = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
val ShapeFAB = RoundedCornerShape(16.dp)
val ShapeFullPill = CircleShape
val ShapeTextField = RoundedCornerShape(4.dp)
