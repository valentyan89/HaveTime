package com.example.havetime.presentation.common.utils

enum class TextAdaptability(val maxScale: Float) {
    /**
     * Полная свобода: текст увеличивается вслед за системой без ограничений
     */
    SYSTEM(maxScale = Float.MAX_VALUE),

    /**
     * Умеренное ограничение: для обычного контента, списков и описаний (максимум +30%)
     */
    STANDARD(maxScale = 1.3f),

    /**
     * Строгое ограничение: для кнопок, табов и элементов шапки (максимум +10%)
     */
    STRICT(maxScale = 1.1f)
}