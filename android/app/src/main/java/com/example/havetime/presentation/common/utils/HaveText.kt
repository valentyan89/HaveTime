package com.example.havetime.presentation.common.utils

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit

/**
 * Обёртка для выбора скалирования контента
 *
 * @param strategy тип адаптивности
 * @param content контент поддерживающий адаптивность
 */
@Composable
fun AdaptabilityWrapper(
    strategy: TextAdaptability,
    content: @Composable () -> Unit
) {
    val currentDensity = LocalDensity.current

    val targetDensity = if (currentDensity.fontScale > strategy.maxScale) {
        Density(
            density = currentDensity.density,
            fontScale = strategy.maxScale
        )
    } else {
        currentDensity
    }

    CompositionLocalProvider(LocalDensity provides targetDensity) {
        content()
    }
}

/**
 * Compose-компонент для отображения текста с поддержкой
 * адаптивного масштабирования системного размера шрифта.
 *
 * Является обёрткой над [Text] и предоставляет те же основные
 * параметры форматирования текста, дополнительно позволяя
 * ограничивать масштаб системного шрифта через [adaptability].
 *
 * @param text текст для отображения.
 * @param modifier модификатор компонента.
 * @param color цвет текста.
 * @param fontSize размер шрифта.
 * @param fontWeight начертание шрифта.
 * @param letterSpacing межсимвольный интервал.
 * @param textDecoration декорация текста, например подчёркивание.
 * @param textAlign горизонтальное выравнивание текста.
 * @param lineHeight высота строки.
 * @param minLines минимальное количество строк.
 * @param maxLines максимальное количество строк.
 * @param onTextLayout callback, вызываемый после расчёта расположения текста.
 * @param overflow способ обработки текста, выходящего за заданное количество строк.
 * @param softWrap определяет, разрешён ли перенос текста по словам.
 * @param style стиль текста.
 * @param adaptability стратегия адаптивности системного размера шрифта.
 */
@Composable
fun HaveText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    style: TextStyle = LocalTextStyle.current,
    adaptability: TextAdaptability = TextAdaptability.STANDARD
) {
    AdaptabilityWrapper(strategy = adaptability) {
        Text(
            text = text,
            modifier = modifier,
            color = color,
            fontSize = fontSize,
            fontWeight = fontWeight,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            minLines = minLines,
            onTextLayout = onTextLayout,
            style = style
        )
    }
}
