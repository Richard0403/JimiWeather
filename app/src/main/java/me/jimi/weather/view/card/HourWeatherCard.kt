package me.jimi.weather.view.card

import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import me.jimi.weather.common.getThemeColor
import me.jimi.weather.databinding.CardHourlyWeatherBinding
import me.jimi.weather.model.weather.Weather
import me.jimi.weather.tools.doOnMainThreadIdle
import me.jimi.weather.tools.getColorWithAlpha
import me.jimi.weather.tools.hide
import me.jimi.weather.tools.show
import me.jimi.weather.ui.weather.HourWeatherAdapter


/**
 * 小时级的天气信息卡片
 */
class HourWeatherCard : CardLinearlayout, SpicaWeatherCard {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val binding = CardHourlyWeatherBinding.inflate(LayoutInflater.from(context), this, true)


    private val hourWeatherAdapter by lazy {
        HourWeatherAdapter()
    }


    override var animatorView: View = this

    override var enterAnim: AnimatorSet = AnimatorSet()
    override var index: Int = 1
    override var hasInScreen: Boolean = false

    init {
        resetAnim()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun bindData(weather: Weather) {
        val items = weather.hourlyWeather

        val themeColor = weather.getWeatherType().getThemeColor()

        val bgDrawable = binding.root.background
        bgDrawable.colorFilter = PorterDuffColorFilter(
            getColorWithAlpha(.08f, themeColor), PorterDuff.Mode.SRC_IN
        )
        binding.root.background = bgDrawable

        binding.cardName.setTextColor(themeColor)

        binding.layoutLoading.hide()
        weather.hourlyWeather.toList().sortedBy {
            it.temp
        }.apply {
            binding.hourForecastView.setData(weather)
        }

        doOnMainThreadIdle({
            binding.tipDesc.text = weather.descriptionForToday
            if (weather.descriptionForToday.isNullOrEmpty()) {
                binding.tipDesc.hide()
            } else {
                binding.tipDesc.show()
            }
        })
        hourWeatherAdapter.items.clear()
        hourWeatherAdapter.items.addAll(items)
        hourWeatherAdapter.sortList()

    }

    override fun startEnterAnim() {
        super.startEnterAnim()
        binding.hourForecastView.startAnim(150)
    }
}
