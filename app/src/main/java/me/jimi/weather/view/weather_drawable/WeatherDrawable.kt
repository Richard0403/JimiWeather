package me.jimi.weather.view.weather_drawable

import android.graphics.Canvas

abstract class WeatherDrawable {


    abstract fun doOnDraw(canvas: Canvas, width: Int, height: Int)

}