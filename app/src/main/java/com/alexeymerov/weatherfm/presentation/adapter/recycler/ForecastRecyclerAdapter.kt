package com.alexeymerov.weatherfm.presentation.adapter.recycler

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import com.alexeymerov.weatherfm.R
import com.alexeymerov.weatherfm.data.database.entity.ForecastEntity
import com.alexeymerov.weatherfm.presentation.base.BaseRecyclerAdapter
import com.alexeymerov.weatherfm.presentation.base.BaseViewHolder
import com.alexeymerov.weatherfm.utils.HeaderDecorator
import com.alexeymerov.weatherfm.utils.StickyHeaderDecorator
import com.alexeymerov.weatherfm.utils.extensions.inflate
import com.alexeymerov.weatherfm.utils.getDayMonthShortString
import com.alexeymerov.weatherfm.utils.getHourMinuteString
import kotlinx.android.synthetic.main.item_day_forecast.*
import kotlinx.android.synthetic.main.item_header_date.*
import java.util.*


typealias HeaderItem = HeaderDecorator<ForecastEntity>

class ForecastRecyclerAdapter :
    BaseRecyclerAdapter<HeaderItem, ForecastRecyclerAdapter.ViewHolder>(),
    StickyHeaderDecorator.StickyHeaderAdapter<ForecastRecyclerAdapter.ViewHolder> {

    private val headerPositionMap: MutableMap<Int, Long> = mutableMapOf()

    override var items: List<HeaderItem>
        get() = super.items
        set(value) {
            super.items = value
            initHeadersMap(value)
        }

    override fun getHeaderId(position: Int) = headerPositionMap[items[position].entity.dateDay] ?: 0

    override fun onCreateHeaderViewHolder(parent: ViewGroup) =
        HeaderViewHolder(parent.inflate(R.layout.item_header_date))

    override fun onBindHeaderViewHolder(viewHolder: ViewHolder, position: Int) = viewHolder.bind(items[position])

    override fun getItemId(position: Int) = items[position].entity.dateMillis

    private enum class ChangeType {
        TEMP, WIND, WEATHER, CLOUDS, HUMIDITY, PRESSURE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ForecastViewHolder(parent.inflate(R.layout.item_day_forecast))

    private fun initHeadersMap(list: List<HeaderItem>) {
        list.asSequence()
            .filter { it.isHeader }
            .map { it.entity }
            .toList()
            .forEachIndexed { index, entity -> headerPositionMap[entity.dateDay] = index.toLong() }
    }

    override fun compareItems(old: HeaderItem, new: HeaderItem) =
        old.entity.dateMillis == new.entity.dateMillis

    override fun compareContent(old: HeaderItem, new: HeaderItem): Any? {
        val changesList = mutableListOf<ChangeType>()
        val first = old.entity
        val second = new.entity
        if (first.temp != second.temp) changesList.add(ChangeType.TEMP)
        if (first.windSpeed != second.windSpeed) changesList.add(ChangeType.WIND)
        if (first.weatherType != second.weatherType) changesList.add(ChangeType.WEATHER)
        if (first.cloudsPercent != second.cloudsPercent) changesList.add(ChangeType.CLOUDS)
        if (first.humidity != second.humidity) changesList.add(ChangeType.HUMIDITY)
        if (first.pressure != second.pressure) changesList.add(ChangeType.PRESSURE)
        return changesList
    }

    override fun proceedPayloads(payloads: MutableList<Any>, holder: ViewHolder, position: Int) {
        val payload = payloads[0] as? List<*> ?: return
        payload
            .asSequence()
            .filter { it is ChangeType }
            .filter { holder is ForecastViewHolder }
            .map { it as ChangeType }
            .toList()
            .forEach {
                (holder as ForecastViewHolder).apply {
                    when (it) {
                        ChangeType.TEMP -> setTemp(items[position].entity)
                        ChangeType.WIND -> setWind(items[position].entity)
                        ChangeType.WEATHER -> setWeather(items[position].entity)
                        ChangeType.CLOUDS -> setClouds(items[position].entity)
                        ChangeType.HUMIDITY -> setHumidity(items[position].entity)
                        ChangeType.PRESSURE -> setPressure(items[position].entity)
                    }
                }
            }
    }

    abstract inner class ViewHolder(containerView: View) : BaseViewHolder<HeaderItem>(containerView)

    inner class HeaderViewHolder(containerView: View) : ViewHolder(containerView) {
        override fun bind(currentItem: HeaderItem) {
            header_date_text_view.text = Date(currentItem.entity.dateMillis)
                .getDayMonthShortString()
                .replace(" ", "\n")
        }
    }

    inner class ForecastViewHolder(containerView: View) : ViewHolder(containerView) {
        override fun bind(currentItem: HeaderItem) {
            currentItem.entity.apply {
                date_text_view.text = Date(dateMillis).getHourMinuteString()
                setTemp(this)
                setWind(this)
                setWeather(this)
                setClouds(this)
                setPressure(this)
                setHumidity(this)
            }
        }

        fun setClouds(entity: ForecastEntity) =
            cloud_percent_text_view.formattedString(R.string.clouds_percent, entity.cloudsPercent)

        fun setWind(entity: ForecastEntity) {
            val windDirection = wind_text_view.getWindDirection(entity.windDegree)
            wind_text_view.formattedWind(R.string.wind, entity.windSpeed, windDirection)
        }

        fun setWeather(entity: ForecastEntity) =
            weather_type_text_view.formattedString(R.string.weather_type, entity.weatherType)

        fun setHumidity(entity: ForecastEntity) = humidity_text_view.formattedString(R.string.humidity, entity.humidity)

        fun setPressure(entity: ForecastEntity) = pressure_text_view.formattedString(R.string.pressure, entity.pressure)

        fun setTemp(entity: ForecastEntity) = current_temp_text_view.formattedString(R.string.temp, entity.temp)

        private fun View.getWindDirection(windDegree: Int) = context.getString(
            when (windDegree) {
                in 30..60 -> R.string.north_east
                in 60..120 -> R.string.east
                in 120..150 -> R.string.east_west
                in 150..210 -> R.string.west
                in 210..240 -> R.string.west_south
                in 240..300 -> R.string.south
                in 300..330 -> R.string.north_south
                else -> R.string.north
            }
        )

        private inline fun <reified T> TextView.formattedString(@StringRes stringId: Int, param: T) {
            text = String.format(context.getString(stringId), param)
        }

        private fun TextView.formattedWind(@StringRes stringId: Int, param: Int, param2: String) {
            text = String.format(context.getString(stringId), param, param2)
        }
    }

}