package com.example.jetpacktest

import java.text.Normalizer

//Niko: Helper function to remove accents from playerName, since API we use needs raw
//Got it from https://stackoverflow.com/a/3322174
fun removeAccents(input: String): String {
    return Normalizer.normalize(input, Normalizer.Form.NFD)
        .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
}

fun dropZeroBeforeDecimal(input: Float): String {
    val inputStr = input.toString()
    return if (inputStr.startsWith("0.")) {
        inputStr.drop(1)
    } else {
        inputStr
    }
}