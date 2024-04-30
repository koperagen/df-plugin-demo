import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.io.*

fun main() {
    val df = DataFrame.readCSV("https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv")
    df.stargazers_count.print()

    df.filter { stargazers_count > 50 }.print()

    println(df.count { stargazers_count > 50 })
//    println("Zero stars")
    println(df.count { stargazers_count == 0 })

    // let's try to parse topics
    val df1 = df
        .add("topicsList") { topics.removeSurrounding("[", "]").split(", ").filter { it.isNotEmpty() } }
        .add("topicsSize") { topicsList.size }

    df1
        .sortByDesc { stargazers_count }.print(rowsLimit = 10)

    df1.explode { topicsList }.groupBy { topicsList }.sortByGroupDesc { it.rowsCount() }.print()
}