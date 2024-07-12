import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.annotations.ColumnName
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.annotations.Import
import org.jetbrains.kotlinx.dataframe.io.*

private val sample = @Import DataFrame.readCSV("jetbrains_repositories.csv")

fun printInfo(raw: AnyFrame) {
    val df = raw.castTo(sample)
    df.stargazers_count.print()

    df.filter { stargazers_count > 50 }.print()

    println(df.count { stargazers_count > 50 })
    println(df.count { stargazers_count == 0 })

    // let's try to parse topics
    val df1 = parseTopics(df)

    df1.sortByDesc { stargazers_count }.print(rowsLimit = 10)

    df1.explode { topicsList }.groupBy { topicsList }.sortByGroupDesc { it.rowsCount() }.print()
}

private fun parseTopics(raw: AnyFrame) = raw.castTo(sample)
    .add("topicsList") { topics.removeSurrounding("[", "]").split(", ").filter { it.isNotEmpty() } }
    .add("topicsSize") { topicsList.size }

fun main() {
    // How can you create a function when types are implicit?
    // 1: castTo + https://kotlinlang.org/docs/functions.html#single-expression-functions
    val df = DataFrame.readCSV("https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv")
    printInfo(df)

    // 2: generate code
    df.generateDataClasses("Repositories").print()
    val list = readRepositories()
    display(list)
}

@DataSchema
data class Repositories(
    @ColumnName("full_name")
    val fullName: String,
    @ColumnName("html_url")
    val htmlUrl: java.net.URL,
    @ColumnName("stargazers_count")
    val stargazersCount: Int,
    val topics: String,
    val watchers: Int
)

fun readRepositories(): List<Repositories> =
    DataFrame.readCSV("https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv").toListOf<Repositories>()

fun display(repositories: List<Repositories>) {
    println(repositories.take(10).joinToString("\n"))
}
