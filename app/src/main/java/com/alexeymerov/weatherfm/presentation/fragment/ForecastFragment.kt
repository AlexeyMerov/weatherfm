package com.alexeymerov.weatherfm.presentation.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexeymerov.weatherfm.R
import com.alexeymerov.weatherfm.data.database.entity.ForecastEntity
import com.alexeymerov.weatherfm.presentation.adapter.recycler.ForecastRecyclerAdapter
import com.alexeymerov.weatherfm.presentation.adapter.recycler.HeaderItem
import com.alexeymerov.weatherfm.presentation.base.BaseFragment
import com.alexeymerov.weatherfm.utils.OnLoadingEvent
import com.alexeymerov.weatherfm.utils.RxBus
import com.alexeymerov.weatherfm.utils.StickyHeaderDecorator
import com.alexeymerov.weatherfm.utils.extensions.isVisible
import com.alexeymerov.weatherfm.utils.extensions.makeVisible
import com.alexeymerov.weatherfm.viewmodel.contract.IForecastViewModel
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.fragment_forecast.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class ForecastFragment : BaseFragment() {

    companion object {
        fun newInstance() = ForecastFragment()
    }

    private val viewModel by viewModel<IForecastViewModel>()
    private val recyclerAdapter: ForecastRecyclerAdapter by lazy { initRecyclerAdapter() }
    private val linerLayoutManager by lazy { initLayoutManager() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_forecast, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initItemsList()
        viewModel.requestForecast()
    }

    private fun initItemsList() {
        initRecyclerView()
        viewModel.forecastList.observe(this, Observer { it?.setOrUpdateItems() })
    }

    private fun initRecyclerView() {
        with(forecast_recycler_view) {
            layoutManager = linerLayoutManager
            adapter = recyclerAdapter
            hasFixedSize()
            addItemDecoration(StickyHeaderDecorator(recyclerAdapter, true))
        }
    }

    private fun List<ForecastEntity>.setOrUpdateItems() {
        runDisposable {
            Single
                .just(this)
                .map { convertToHeaderList() }
                .compose(singleTransformer())
                .subscribe(Consumer {
                    empty_state_view.makeVisible(isEmpty())
                    loading_progress_view.makeVisible(false)
                    forecast_recycler_view.makeVisible(isNotEmpty())
                    recyclerAdapter.items = it
                })
        }
    }

    private fun List<ForecastEntity>.convertToHeaderList(): MutableList<HeaderItem> {
        val headerList: MutableList<HeaderItem> = mutableListOf()
        this.forEachIndexed { index, forecastEntity ->
            val isHeader = index == 0 || this[index - 1].dateDay != this[index].dateDay
            headerList.add(HeaderItem(isHeader, forecastEntity))
        }
        return headerList
    }

    private fun initLayoutManager() = LinearLayoutManager(activity!!).apply {
        isSmoothScrollbarEnabled = true
        isMeasurementCacheEnabled = true
        isItemPrefetchEnabled = true
    }

    private fun initRecyclerAdapter(): ForecastRecyclerAdapter = ForecastRecyclerAdapter().apply {
        setHasStableIds(true)
    }

    private var disposableEvent: Disposable = RxBus
        .listen(OnLoadingEvent::class.java)
        .subscribe { showLoader() }

    private fun showLoader() {
        if (empty_state_view.isVisible()) {
            loading_progress_view.makeVisible()
            empty_state_view.makeVisible(false)
        }
    }

    override fun onDestroy() {
        if (!disposableEvent.isDisposed) disposableEvent.dispose()
        super.onDestroy()
    }

}
