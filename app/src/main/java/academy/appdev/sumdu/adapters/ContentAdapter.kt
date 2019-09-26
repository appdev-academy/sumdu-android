package academy.appdev.sumdu.adapters

import academy.appdev.sumdu.R
import academy.appdev.sumdu.fragments.ContentFragment
import academy.appdev.sumdu.fragments.TabFragment
import academy.appdev.sumdu.fragments.formatDate
import academy.appdev.sumdu.objects.ContentHeaderObject
import academy.appdev.sumdu.objects.ListObject
import academy.appdev.sumdu.objects.NetworkingObject
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_header_layout.view.*
import kotlinx.android.synthetic.main.list_item_layout.view.*
import kotlinx.android.synthetic.main.list_item_layout.view.titleText


class ContentAdapter(private var data: List<NetworkingObject>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var generalData: ArrayList<Any> = ArrayList()

    init {
        mixGeneralArray()
    }

    private val ITEM = 1
    private val HEADER = 0

    fun setNewData(newData: List<NetworkingObject>) {
        data = newData
        mixGeneralArray()
        notifyDataSetChanged()
    }

    private fun mixGeneralArray() {
        generalData = ArrayList()
        data.sortedBy { it.date }.forEach {
            val date = it.date?.formatDate()
            if (date != null && !generalData.contains(date ?: "")) {
                generalData.add(ContentHeaderObject(date, it.dayOfTheWeek))
                generalData.add(it)
            } else {
                generalData.add(it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM) {
            ItemViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.content_list_item_layout,
                    parent,
                    false
                )
            )
        } else {
            HeaderViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.list_header_layout,
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                holder.itemView.titleText.text = (generalData[position] as ContentHeaderObject).date
                holder.itemView.dayOfWeekText.text = (generalData[position] as ContentHeaderObject).dayOfWeek
            }
            is ItemViewHolder -> {
                holder.itemView.apply {
                    val listObject = generalData[position] as NetworkingObject
                    titleText.text = listObject.title
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return generalData.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (isPositionHeader(position)) HEADER else ITEM
    }

    private fun isPositionHeader(position: Int): Boolean {
        return generalData[position] is ContentHeaderObject
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
