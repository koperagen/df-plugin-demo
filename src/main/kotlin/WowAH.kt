package org.jetbrains.kotlinx.dataframe

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.Temporal
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
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
 * let's try to find bots among these players. say, players with uninterrupted play session of >24 hours?
 */

fun main() {
    val df = DataFrame.read("wowah_data_100K.csv").cast<ActivePlayer>()

    val format = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm:ss")

    val df1 = df
        .convert { timestamp }.with { LocalDateTime.parse(it, format) }
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

    df1.groupBy { session }.print()

    val sessions = df1.groupBy { session and char and charclass }.aggregate {
        first().timestamp into "start"
        last().timestamp into "end"
        ChronoUnit.MINUTES.between(first().timestamp, last().timestamp) into "playtime"
    }
        .add("longSession") { playtime > 24 * 60 }

    println("Number of uninterrupted >24 hours play sessions")
    println(sessions.count { longSession })

    println("Sessions sorted by most uninterrupted playtime")
    sessions.sortByDesc { playtime }.print()

    val playersInfo = sessions.groupBy { char and charclass }.aggregate {
        count { longSession } into "longSessions"
        count() into "totalSessions"
    }.sortByDesc { longSessions and totalSessions }

    println("Player info")
    playersInfo.print()
}