@file:Suppress("UnusedImport")

import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.*

data class Person(val firstName: String, val lastName: String, val age: Int, val city: String?)

data class Group(val id: String, val participants: List<Person>)

@DataSchema
class WikiData(val name: String, val paradigms: List<String>)

fun main() {
    // Possible to create DataFrame from a "flat" object
    val languages = dataFrameOf(
        WikiData("Kotlin", listOf("object-oriented", "functional", "imperative")), // Kotlin
        WikiData("Haskell", listOf("Purely functional")), // Haskell
        WikiData("C", listOf("imperative")), // C
        WikiData("Pascal", listOf("imperative")), // Pascal
        WikiData("Idris", listOf("functional")), // Idris
        WikiData("OCaml", listOf("functional", "imperative", "modular", "object-oriented")) // OCaml
    )

    languages.name.print()
    languages.paradigms.print()

    // Same here but with an arbitrary level of nesting
    val df = listOf(
        Group("1", listOf(
            Person("Alice", "Cooper", 15, "London"),
            Person("Bob", "Dylan", 45, "Dubai")
        )),
        Group("2", listOf(
            Person("Charlie", "Daniels", 20, "Moscow"),
            Person("Charlie", "Chaplin", 40, "Milan"),
        )),
    ).toDataFrame(maxDepth = 2)

    df.participants[0].age.print()
}