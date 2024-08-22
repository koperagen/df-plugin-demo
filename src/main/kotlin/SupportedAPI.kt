import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.annotations.Import
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.*
import java.io.File
import java.time.LocalDateTime

@DataSchema
interface Marker {
    val a: String
    val b: Int
}

@DataSchema
interface Explode {
    val primitives: List<Int>
    val frameColumn: DataFrame<Marker>
}

@DataSchema
interface Convert {
    val timestamp: String
}

@DataSchema
interface Join1 {
    val a: Int
    val b: Int
}

@DataSchema
interface JoinLeaf {
    val something: Int
    val somethingElse: String
}

@DataSchema
interface Join2 {
    val c: DataRow<JoinLeaf>
}

@DataSchema
interface Cast {
    val a: Int
    val b: String
    val c: Int
}

@DataSchema
data class Rows(val a: Int, val b: Int)

fun main() {
    fun dataFrameOf() {
        // Special constructor for classes annotated with @DataSchema
        // Convenient way to create dataframe from rows
        val df: DataFrame<Rows> = dataFrameOf(Rows(1, 2))
        df.a

        val df1 = df.append(Rows(3, 4))
    }

    fun dataFrameOfInvoke() {
        val df = dataFrameOf("a", "b")(1, listOf(2, 3, 4))
        df[0].a
        df[0].b.size
    }

    fun explode(df: DataFrame<Explode>) {
        val df1 = df.explode { primitives and frameColumn }
    }

    fun explode1(df: DataFrame<Explode>) = df.explode { primitives and frameColumn }

    fun ungroup(df: DataFrame<Explode>) {
        val df1 = df
            .explode { frameColumn }
            .ungroup { frameColumn }

        df1.a
    }

    fun group(df: DataFrame<Explode>) {
        val df1 = df.group { primitives and frameColumn }.into("group")
        df1.group.primitives
    }

    fun parse(s: String): LocalDateTime = error("materialize LocalDateTime")

    fun convert(df: DataFrame<Convert>) {
        df.convert { timestamp }.with { parse(it) }.timestamp
    }

    fun join(df1: DataFrame<Join1>, df2: DataFrame<Join2>) {
        val res = df1.join(df2) { a.match(right.c.something) }

        df1
            .add("key") { a }
            .join(
                df2.add("key") { c.something }
            ) {
                key.match(right.key)
            }
    }

    fun add(df: AnyFrame) = df
        .add("a") { 42 }
        .add {
            "b" from { "" }
            a into "c"
        }

    fun safeCast(df: DataFrame<*>) {
        // try removing a column or commenting out a line in the add function and see the error
        add(df).cast<Cast>()
    }

    fun cast(df: DataFrame<*>) {
        // DataFrame<*> can be cast to anything
        df.cast<Explode>().frameColumn
    }

    fun read() {
        // Argument be either absolute path or path relative to project directory.
        val df = @Import DataFrame.readCSV("jetbrains_repositories.csv")
        df.full_name
        // Execute `assemble` task to "cache" schema from this URL. Works for readJson
        val df1 = @Import DataFrame.readJson("https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains.json")
        df1.repos
    }

    fun toDataFrame() {
        val df = listOf(Record(1, "ab", NestedRecord(3.0), Test1(1, "2"))).toDataFrame(maxDepth = 1)
        df.nestedRecord.c
    }

    fun toDataFrameDsl() {
        val df = listOf(Record(1, "ab", NestedRecord(3.0), Test1(1, "2"))).toDataFrame {
            properties(maxDepth = 2) {
                preserve(NestedRecord::class)
                preserve(Record::preserveProperty)
            }
        }
        val nestedRecord: DataColumn<NestedRecord> = df.nestedRecord
        val nestedRecord1: DataColumn<Test1> = df.preserveProperty
    }

    fun toDataFrameColumn() {
        val df = listOf(File("")).toDataFrame(columnName = "file")
        df.file
    }

    fun remove(list: List<Record>) {
        list.toDataFrame().remove { a }
    }

    fun select(list: List<Record>) {
        val df = list.toDataFrame().select { a and b }
        df.a
        df.b
    }

    fun dropNulls(df: DataFrame<*>) {
        val nullableInt: Int? = 42
        val df = df.add("a") { nullableInt }.dropNulls { a }
        val nonNullValues: DataColumn<Int> = df.a
    }

    fun fillNulls(df: DataFrame<*>) {
        val nullableInt: Int? = 42
        val df = df.add("a") { nullableInt }.fillNulls { a }.with { 0 }
        val nonNullValues: DataColumn<Int> = df.a
    }

    fun rename() {
        val df = listOf(Record(1, "ab", NestedRecord(3.0), Test1(1, "2"))).toDataFrame(maxDepth = 1)
        df.nestedRecord.c
        val df1 = df.rename { nestedRecord.c and nestedRecord }.into("group", "abc")
        df1.group.abc
    }

    fun groupBy_aggregate(df: DataFrame<ActivePlayer>) {
        val df1 = df.groupBy { race and expr { 12 } }.aggregate {
            count() into "count"
            1 into "i"
        }
        df1.count
        df1.race
    }

    fun groupBy_toDataFrame(df: DataFrame<ActivePlayer>) {
        val df1 = df.groupBy { race and expr { 12 } }.toDataFrame("grouped")
        df1.race
        df1.grouped[0].timestamp
    }

    fun castTo(organizations: List<String>) {
        val sample = @Import DataFrame.readCSV("jetbrains_repositories.csv")
        organizations.forEach { organization ->
            val df = DataFrame.readCSV(organization).castTo(sample)
            println(organizations)
            println("Repositories: ${df.count()}")
            println("Top 10:")
            df.sortBy { stargazers_count.desc() }.take(10).print()
        }
    }

    fun selectionDsl(df: DataFrame<Join2>) {
        df.select { colsAtAnyDepth().colsOf<Int>() }.something
        df.ungroup { c }.select { colsOf<String>() }.somethingElse
        df.add("a") { 42 }.select { colsOf<Int>() }.a
    }

    fun flatten() {
        val df = dataFrameOf("a", "b", "c", "d")(1, 2, 3, 4)
        val grouped = df
            .group { a and b }.into("e")
            .group { e and c }.into("f")

        val flattened = grouped.flatten { f.e }
        flattened.f.a

        val flattened1 = grouped.flatten { f }
        flattened1.a
    }
}

class Test1(val a: Int, val b: String)

class NestedRecord(val c: Double)

class Record(val a: Int, val b: String, val nestedRecord: NestedRecord, val preserveProperty: Test1)