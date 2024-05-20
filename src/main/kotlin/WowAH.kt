package org.jetbrains.kotlinx.dataframe

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.Temporal
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.annotations.DisableInterpretation
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.io.read
import org.jetbrains.kotlinx.dataframe.api.*

fun <T, V : Temporal> DataRow<T>.diff(unit: ChronoUnit, expression: RowExpression<T, V>): Long? = prev()?.let { p -> unit.between(expression(p, p), expression(this, this)) }

/**
char,level,race,charclass,zone,guild,timestamp
59425,1,Orc,Rogue,Orgrimmar,165,01/01/08 00:02:04
65494,9,Orc,Hunter,Durotar,-1,01/01/08 00:02:04
 */
@DataSchema
interface ActivePlayer {
    val char: Int
    val level: Int
    val race: String
    val charclass: String
    val zone: String
    val guild: Int
    val timestamp: String
}

/**
 * let's try to find bots among these players. say, players with uniterrutped play session of >24 hrs?
 */

fun main() {
    val dfq = DataFrame.read("wowah_data_100K.csv")
    val df = dfq.cast<ActivePlayer>()

    val format = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm:ss")

    df.add("") { 12 }

    val df123 = df.add("aa") { 123 }
    df123.aa

    val df2 = df
        .convert { timestamp }.with { LocalDateTime.parse(it, format)!! }
        .sortBy { char and timestamp }

    df
    val df1 = df
        .convert { timestamp }.with { LocalDateTime.parse(it, format)!! }
        .sortBy { char and timestamp }
        .add("tsDiffValue") {
            diff(ChronoUnit.MINUTES) { timestamp }
        }
        .add("tsDiff") { tsDiffValue?.let { it > 20  } ?: true }
        .add("charDiff") { diffOrNull { char }?.let { it != 0 } ?: true }
        .add {
            expr { tsDiff or charDiff }.convertTo<Int>().cumSum() into "session"
        }

    df1.distinctBy { session }.print()

    df1.groupBy { session }.filter { group.rowsCount() != 1 }.let {
        it.print()
        it.concat().print()
    }

    val res = df1.groupBy { session }.aggregate {
        count() into "count"
        first().timestamp into "start"
        last().timestamp into "end"
        ChronoUnit.MINUTES.between(first().timestamp, last().timestamp) into "diff"
    }
        .add("bot") { diff > 24 * 60 }

    println(res.bot.count { it })
    println(res.bot.count { it })

    res.sortByDesc { diff }.print()
}