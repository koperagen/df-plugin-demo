@file:Suppress("UnusedImport")

import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.*

fun main() {
    // YourData can be generated in "GenerateSchema.kt"
    val df = readData()
        //.cast<YourData>(verify = true)
}