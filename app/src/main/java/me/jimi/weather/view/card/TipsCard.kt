package me.jimi.weather.view.card

import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import me.jimi.weather.common.getThemeColor
import me.jimi.weather.databinding.CardLifeIndexBinding
import me.jimi.weather.model.weather.Weather
import me.jimi.weather.tools.doOnMainThreadIdle
import me.jimi.weather.tools.getColorWithAlpha
import me.jimi.weather.ui.weather.TipAdapter


/**
 * 生活指数卡
 */
class TipsCard : RelativeLayout, SpicaWeatherCard {

    private val tipAdapter = TipAdapter()

    private val binding = CardLifeIndexBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override var animatorView: View = this

    override var enterAnim: AnimatorSet = AnimatorSet()
    override var index: Int = 4
    override var hasInScreen: Boolean = false

    init {
        resetAnim()
        binding.rvTip.adapter = tipAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun bindData(weather: Weather) {
        val themeColor = weather.getWeatherType().getThemeColor()

        val bgDrawable = binding.root.background
        bgDrawable.colorFilter = PorterDuffColorFilter(
            getColorWithAlpha(.08f, themeColor), PorterDuff.Mode.SRC_IN
        )
        binding.root.background = bgDrawable

        tipAdapter.themeColor = themeColor
        binding.cardName.setTextColor(themeColor)
        val items = weather.lifeIndexes
        tipAdapter.items.clear()
        tipAdapter.items.addAll(items)
        doOnMainThreadIdle({
            tipAdapter.notifyDataSetChanged()
        })
    }


}
