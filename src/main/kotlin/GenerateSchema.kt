@file:Suppress("UnusedImport")

import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.*
import org.jetbrains.kotlinx.dataframe.codeGen.generateInterfaces

fun readData(): AnyFrame = DataFrame.readExcel("vending.xlsx")

fun main() {
    val df = readData()
    val code = df.generateInterfaces("YourData")
    // Copy this code to the project
    println(code)
}