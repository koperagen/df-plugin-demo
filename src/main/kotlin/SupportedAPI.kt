package org.jetbrains.kotlinx.dataframe

import java.time.LocalDateTime
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.annotations.DisableInterpretation
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.append
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.dropNulls
import org.jetbrains.kotlinx.dataframe.api.explode
import org.jetbrains.kotlinx.dataframe.api.group
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.join
import org.jetbrains.kotlinx.dataframe.api.remove
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.ungroup
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.io.readJson

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
        val df: DataFrame<Rows> = dataFrameOf(Rows(1, 2))
        df.a

        val df1 = df.append(Rows(3, 4))
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

    fun read() {
        val df = DataFrame.readCSV("https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv")
        val df1 = DataFrame.readJson("https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains.json")
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

    fun disableInterpretation() {
        // Plugin will ignore readCSV call
        val df = @DisableInterpretation DataFrame.readCSV("https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv")
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
        val df = df.add("a") { 42.takeIf { false } }.dropNulls { a }
        val nonNullValues: DataColumn<Int> = df.a
    }
}

class Test1(val a: Int, val b: String)

class NestedRecord(val c: Double)

class Record(val a: Int, val b: String, val nestedRecord: NestedRecord, val preserveProperty: Test1)