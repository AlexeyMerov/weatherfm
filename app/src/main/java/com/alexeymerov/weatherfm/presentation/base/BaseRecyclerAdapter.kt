package com.alexeymerov.weatherfm.presentation.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.alexeymerov.weatherfm.utils.extensions.AutoUpdatableAdapter
import kotlinx.android.extensions.LayoutContainer
import kotlin.properties.Delegates


abstract class BaseRecyclerAdapter<T : Any, VH : BaseViewHolder<T>> : RecyclerView.Adapter<VH>(),
    AutoUpdatableAdapter<T> {

    open var items: List<T> by Delegates.observable(emptyList()) { _, oldList, newList -> autoNotify(oldList, newList) }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])

    override fun onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>) = when {
        payloads.isEmpty() -> onBindViewHolder(holder, position)
        else -> proceedPayloads(payloads, holder, position)
    }

    open fun proceedPayloads(payloads: MutableList<Any>, holder: VH, position: Int) {}
}

abstract class BaseViewHolder<in T : Any>(override val containerView: View) : RecyclerView.ViewHolder(containerView),
    LayoutContainer {
    abstract fun bind(currentItem: T)
}
