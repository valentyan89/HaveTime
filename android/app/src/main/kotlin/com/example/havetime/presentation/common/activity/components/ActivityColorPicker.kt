package com.example.havetime.presentation.common.activity.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun ActivityColorPicker(
    colorRows: List<List<Color>>,
    selectedColor: Color,
    onColorSelected: (Color) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        colorRows.forEach { row ->
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) {
                row.forEach { colorItem ->
                    Box(
                        modifier =
                        Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(colorItem)
                            .clickable { onColorSelected(colorItem) }
                            .padding(4.dp)
                    ) {
                        if (selectedColor == colorItem) {
                            Box(
                                Modifier.fillMaxSize().background(
                                    Color.White.copy(alpha = 0.3f),
                                    RoundedCornerShape(4.dp)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
