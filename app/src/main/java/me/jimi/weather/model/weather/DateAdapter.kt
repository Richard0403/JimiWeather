package me.jimi.weather.model.weather

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.util.*


@Suppress("unused")
object DateAdapter {

    @ToJson
    fun dateToLong(date: Date) = date.time


    @FromJson
    fun longToDate(timeL: Long) = Date().apply {
        time = timeL
    }
}