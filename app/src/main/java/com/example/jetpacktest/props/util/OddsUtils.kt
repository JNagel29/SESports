package com.example.jetpacktest.props.util

import kotlin.math.roundToInt

/**
 * Convert decimal odds (e.g. 1.89) to American odds (e.g. -112).
 * @return an Int like -112 or +150
 */
fun decimalToAmerican(decimal: Double): Int =
    if (decimal >= 2.0) {
        ((decimal - 1) * 100).roundToInt()        // => +150
    } else {
        (-100 / (decimal - 1)).roundToInt()       // => -112
    }

/**
 * Format the raw int with a leading sign,
 * e.g. -112 or +150
 */
fun formatAmerican(decimal: Double): String {
    val am = decimalToAmerican(decimal)
    return if (am > 0) "+$am" else "$am"
}
