package me.jimi.weather.model.city

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Provinces(
    val provinces: List<Province>
)
