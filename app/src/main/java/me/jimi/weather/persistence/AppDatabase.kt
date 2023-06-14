package me.jimi.weather.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import me.jimi.weather.model.city.CityBean
import me.jimi.weather.model.weather.Weather
import me.jimi.weather.persistence.dao.CityDao
import me.jimi.weather.persistence.dao.WeatherDao


/**
 * 数据库
 */
@Database(entities = [CityBean::class, Weather::class], version = 8, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cityDao(): CityDao

    abstract fun weatherDao(): WeatherDao
}
