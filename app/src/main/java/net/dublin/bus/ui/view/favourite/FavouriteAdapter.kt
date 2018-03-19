package net.dublin.bus.ui.view.favourite

import android.graphics.drawable.AnimationDrawable
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_list_favourite_with_real.view.*
import net.dublin.bus.R
import net.dublin.bus.data.realtime.repository.RealTimeRepository
import net.dublin.bus.model.Favourite
import net.dublin.bus.model.StopData
import net.dublin.bus.ui.utilities.inflate
import java.util.*

internal class FavouriteAdapter(private val mListener: ItemClickListener) : RecyclerView.Adapter<FavouriteAdapter.LocalViewHolder>() {
    private val mDataSet: MutableList<Favourite> = ArrayList()
    private val dataReal: HashMap<String, List<StopData>> = HashMap()

    companion object {
        private const val ITEM_WITH_REAL = 1

        private const val ITEM = 2

        private const val ITEM_TOP = 3
        private const val ITEM_MIDDLE = 4
        private const val ITEM_BOTTOM = 5
    }

    internal interface ItemClickListener {
        fun onItemClick(item: Favourite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalViewHolder {
        return LocalViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: LocalViewHolder, position: Int) {
        holder.bind(mDataSet[position], position < 4)
    }

    override fun getItemCount(): Int {
        return mDataSet.size
    }

    fun replaceData(dataSet: List<Favourite>) {
        setList(dataSet)
        notifyDataSetChanged()
    }

    private fun setList(dataSet: List<Favourite>) {
        mDataSet.clear()
        mDataSet.addAll(dataSet)
    }

    fun updateRealData() {
        if (dataReal.size > 0) {
            notifyDataSetChanged()
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position >= 4) {
            return when {
                mDataSet.size == 5 -> ITEM
                position == 4 -> ITEM_TOP
                position == mDataSet.size - 1 -> ITEM_BOTTOM
                else -> ITEM_MIDDLE
            }
        }

        return ITEM_WITH_REAL
    }

    internal inner class LocalViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder(inflate(parent, viewType)) {
        private var mItem: Favourite? = null
        private var realTimes: ArrayList<RealTime> = arrayListOf()

        init {
            if (itemView.findViewById<View>(R.id.favourite_real_time_1_view) != null) {
                realTimes.add(realTime(itemView.findViewById<View>(R.id.favourite_real_time_1_view)))
                realTimes.add(realTime(itemView.findViewById<View>(R.id.favourite_real_time_2_view)))
                realTimes.add(realTime(itemView.findViewById<View>(R.id.favourite_real_time_3_view)))
            }
        }

        private fun realTime(view: View): RealTime {
            return RealTime(
                    view,
                    view.findViewById(R.id.favourite_real_route_view),
                    view.findViewById(R.id.favourite_real_description_view),
                    view.findViewById(R.id.favourite_real_time_view),
                    view.findViewById(R.id.favourite_real_blip_view)
            )
        }

        fun bind(item: Favourite, loadReal: Boolean) = with(itemView) {
            mItem = item

            favourite_description_view.text = item.description
            favourite_number_view.text = item.stopNumber
            itemView.setOnClickListener {
                mItem?.let { it1 -> mListener.onItemClick(it1) }
            }

            if (loadReal) {
                val d = dataReal[item.stopNumber]
                if (d != null) {
                    onNextData(d)
                } else {
                    hideData()
                    hideNoData()
                    showProgress()
                }

                val repository = RealTimeRepository()
                repository.getData(item.stopNumber)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ data ->
                            dataReal[item.stopNumber] = data
                            onNextData(data)
                        }, {
                            onError()
                        })
            }
        }

        private fun onNextData(data: List<StopData>) {
            hideProgress()
            if (data.isNotEmpty() && data.filter { !TextUtils.isEmpty(it.destinationName) }.any()) {
                showData(data)
            } else {
                showNoData()
            }
        }

        private fun showData(data: List<StopData>) {
            for ((i, d) in data.withIndex()) {
                realTimes[i].container.visibility = View.VISIBLE
                realTimes[i].description.text = d.destinationName
                realTimes[i].route.text = d.publishedLineName
                realTimes[i].time.text = d.timeRemainingFormatted()

                realTimes[i].liveBlip.setBackgroundResource(R.drawable.live)
                val progressAnimation = realTimes[i].liveBlip.background as AnimationDrawable?
                progressAnimation?.start()

                if (i + 1 == 3) {
                    return
                }
            }
        }

        private fun hideData() {
            for (realTime in realTimes) {
                realTime.container.visibility = View.GONE
            }
        }

        private fun hideProgress() {
            itemView.favourite_progress_bar_view.visibility = View.GONE
        }

        private fun showProgress() {
            itemView.favourite_progress_bar_view.visibility = View.VISIBLE
        }

        private fun onError() {
            hideProgress()
            showNoData()
        }

        private fun showNoData() {
            itemView.favourite_real_time_message_view.visibility = View.VISIBLE
        }

        private fun hideNoData() {
            itemView.favourite_real_time_message_view.visibility = View.GONE
        }
    }

    fun inflate(parent: ViewGroup, viewType: Int): View {
        return when (viewType) {
            ITEM -> parent.inflate(R.layout.item_list_favourite)
            ITEM_TOP -> parent.inflate(R.layout.item_list_favourite_top)
            ITEM_MIDDLE -> parent.inflate(R.layout.item_list_favourite_middle)
            ITEM_BOTTOM -> parent.inflate(R.layout.item_list_favourite_bottom)
            else -> parent.inflate(R.layout.item_list_favourite_with_real)
        }
    }

    class RealTime(val container: View,
                   val route: TextView,
                   val description: TextView,
                   val time: TextView,
                   val liveBlip: ImageView)
}