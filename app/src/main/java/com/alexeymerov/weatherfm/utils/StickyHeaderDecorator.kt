package com.alexeymerov.weatherfm.utils

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Thanks to
 * https://github.com/edubarr/header-decor
 */

class HeaderDecorator<T>(val isHeader: Boolean, val entity: T)

class StickyHeaderDecorator<T : RecyclerView.ViewHolder>(
    private val adapter: StickyHeaderAdapter<T>,
    private val renderInline: Boolean = false
) : RecyclerView.ItemDecoration() {

    private val headerCache: MutableMap<Long, RecyclerView.ViewHolder> = HashMap()

    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {

        val position = parent.getChildAdapterPosition(view)
        var headerHeight = 0

        if (position != RecyclerView.NO_POSITION
            && hasHeader(position)
            && showHeaderAboveItem(position)
        ) {
            val header = getHeader(parent, position)!!.itemView
            headerHeight = getHeaderHeightForLayout(header)
        }

        outRect.set(0, headerHeight, 0, 0)
    }

    private fun showHeaderAboveItem(itemAdapterPosition: Int) = when (itemAdapterPosition) {
        0 -> true
        else -> adapter.getHeaderId(itemAdapterPosition - 1) != adapter.getHeaderId(itemAdapterPosition)
    }

    /**
     * Clears the header view cache. Headers will be recreated and
     * rebound on list scroll after this method has been called.
     */
    fun clearHeaderCache() = headerCache.clear()

    fun findHeaderViewUnder(x: Float, y: Float): View? {
        for (holder in headerCache.values) {
            val child = holder.itemView
            val translationX = child.translationX
            val translationY = child.translationY

            if (x >= child.left + translationX
                && x <= child.right + translationX
                && y >= child.top + translationY
                && y <= child.bottom + translationY
            ) return child
        }

        return null
    }

    private fun hasHeader(position: Int): Boolean = adapter.getHeaderId(position) != NO_HEADER_ID

    private fun getHeader(parent: RecyclerView, position: Int): RecyclerView.ViewHolder? {
        val key = adapter.getHeaderId(position)

        return when (headerCache.containsKey(key)) {
            true -> headerCache[key]
            else -> {
                val holder = adapter.onCreateHeaderViewHolder(parent)
                val header = holder.itemView

                adapter.onBindHeaderViewHolder(holder, position)

                val widthSpec = View.MeasureSpec.makeMeasureSpec(
                    parent.measuredWidth,
                    View.MeasureSpec.EXACTLY
                )
                val heightSpec = View.MeasureSpec.makeMeasureSpec(
                    parent.measuredHeight,
                    View.MeasureSpec.UNSPECIFIED
                )

                val childWidth = ViewGroup.getChildMeasureSpec(
                    widthSpec,
                    parent.paddingLeft + parent.paddingRight,
                    header.layoutParams.width
                )
                val childHeight = ViewGroup.getChildMeasureSpec(
                    heightSpec,
                    parent.paddingTop + parent.paddingBottom,
                    header.layoutParams.height
                )

                header.measure(childWidth, childHeight)
                header.layout(0, 0, header.measuredWidth, header.measuredHeight)

                headerCache[key] = holder

                holder
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {

        val count = parent.childCount
        var previousHeaderId: Long = -1

        for (layoutPos in 0 until count) {
            val child = parent.getChildAt(layoutPos)
            val adapterPos = parent.getChildAdapterPosition(child)

            if (adapterPos != RecyclerView.NO_POSITION && hasHeader(adapterPos)) {
                val headerId = adapter.getHeaderId(adapterPos)

                if (headerId != previousHeaderId) {
                    previousHeaderId = headerId
                    val header = getHeader(parent, adapterPos)!!.itemView
                    canvas.save()

                    val left = child.left.toFloat()
                    val top = getHeaderTop(parent, child, header, adapterPos, layoutPos).toFloat()
                    canvas.translate(left, top)

                    header.translationX = left
                    header.translationY = top
                    header.draw(canvas)
                    canvas.restore()
                }
            }
        }
    }

    private fun getHeaderTop(
        parent: RecyclerView, child: View,
        header: View, adapterPos: Int, layoutPos: Int
    ): Int {

        val headerHeight = getHeaderHeightForLayout(header)
        var top = child.y.toInt() - headerHeight
        if (layoutPos == 0) {
            val count = parent.childCount
            val currentId = adapter.getHeaderId(adapterPos)
            // find next view with header and compute the offscreen push if needed
            for (i in 1 until count) {
                val adapterPosHere = parent.getChildAdapterPosition(parent.getChildAt(i))
                if (adapterPosHere != RecyclerView.NO_POSITION) {
                    val nextId = adapter.getHeaderId(adapterPosHere)
                    if (nextId != currentId) {
                        val next = parent.getChildAt(i)
                        val offset =
                            next.y.toInt() - (headerHeight + getHeader(parent, adapterPosHere)!!.itemView.height)
                        if (offset < 0) return offset
                        else break
                    }
                }
            }
            top = Math.max(0, top)
        }

        return top
    }

    private fun getHeaderHeightForLayout(header: View) = if (renderInline) 0 else header.height

    companion object {
        const val NO_HEADER_ID = -1L
    }

    interface StickyHeaderAdapter<T : RecyclerView.ViewHolder> {
        /**
         * Returns the header id for the item at the given position.
         *
         * @param position the item position
         * @return the header id
         */
        fun getHeaderId(position: Int): Long

        /**
         * Creates a new header ViewHolder.
         *
         * @param parent the header's view parent
         * @return a view holder for the created view
         */

        fun onCreateHeaderViewHolder(parent: ViewGroup): T

        /**
         * Updates the header view to reflect the header data for the given position
         *
         * @param viewHolder the header view holder
         * @param position the header's item position
         */
        fun onBindHeaderViewHolder(viewHolder: T, position: Int)
    }
}