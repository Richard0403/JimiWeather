package me.jimi.weather.work

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.skydoves.sandwich.getOrNull
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.jimi.weather.model.weather.AlertBean
import me.jimi.weather.model.weather.CaiyunExtendBean
import me.jimi.weather.model.weather.Weather
import me.jimi.weather.network.hefeng.HeClient
import me.jimi.weather.persistence.dao.CityDao
import me.jimi.weather.persistence.dao.WeatherDao
import me.jimi.weather.repository.HeRepository
import javax.inject.Inject


@AndroidEntryPoint
class SyncService : Service() {


    private val job: Job = SupervisorJob()

    private val scope = CoroutineScope(Dispatchers.IO + job)


    @Inject
    lateinit var heClient: HeClient

    @Inject
    lateinit var repository: HeRepository

    @Inject
    lateinit var cityDao: CityDao

    @Inject
    lateinit var weatherDao: WeatherDao


    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        scope.launch {
            val ds: ArrayList<Deferred<Boolean>> = arrayListOf()

            cityDao.getAllList().forEach { cityBean ->
                val res: Deferred<Boolean> = async {
//                    val weatherResponse = heClient.getAllWeather(cityBean.lon, cityBean.lat).getOrNull()
                    var weatherResponse: Weather? = null
                    val minuteBaseResponse = heClient.getMinute(cityBean.lon, cityBean.lat).getOrNull()
                    val weatherFlow = repository.fetchWeatherCombination(
                        cityBean.lon, cityBean.lat,
                        onError = { message ->

                        }
                    ).collect{ weatherData->
                        weatherResponse = weatherData
                    }

                    var caixunExtBean: CaiyunExtendBean? = null

                    if (minuteBaseResponse != null) {
                        caixunExtBean = CaiyunExtendBean(
                            alerts = minuteBaseResponse.result.alert.content.map {
                                AlertBean(title = it.title, description = it.description, status = it.status, code = it.code, source = it.source)
                            },
                            description = minuteBaseResponse.result.hourly.description,
                            forecastKeypoint = minuteBaseResponse.result.forecastKeypoint
                        )
                    }

                    weatherResponse?.let { weather ->
                        weather.descriptionForToday = caixunExtBean?.forecastKeypoint ?: ""
                        weather.descriptionForToWeek = caixunExtBean?.description ?: ""
                        weather.alerts = caixunExtBean?.alerts ?: listOf()
                        weather.cityName = cityBean.cityName
                        weatherDao.insertWeather(weather)
                        return@async true
                    }

                    return@async false
                }
                ds.add(res)
            }

            ds.forEach { deferred ->
                deferred.await()
            }

            withContext(Dispatchers.Main) {
                stopSelf()
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }


}