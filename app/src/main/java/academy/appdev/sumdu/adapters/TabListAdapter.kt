package academy.appdev.sumdu.adapters

import academy.appdev.sumdu.R
import academy.appdev.sumdu.fragments.TabFragment
import academy.appdev.sumdu.mainActivity
import academy.appdev.sumdu.objects.ListObject
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_layout.view.*
import java.text.Collator


class TabListAdapter(private var data: List<ListObject>, private val owner: TabFragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var generalData: ArrayList<Any> = ArrayList()

    init {
        mixGeneralArray()
    }

    private val ITEM = 1
    private val HEADER = 0

    fun setNewData(newData: List<ListObject>) {
        data = newData
        mixGeneralArray()
        owner.mainActivity?.runOnUiThread { notifyDataSetChanged() }
    }

    private fun mixGeneralArray() {
        generalData = ArrayList()

        data.sortedWith(compareBy(Collator.getInstance()) { it.title }).forEach {
            val letter = it.title?.get(0)?.toString()
            if (letter != null && !generalData.contains(letter ?: "")) {
                generalData.add(letter.toUpperCase())
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
                    R.layout.list_item_layout,
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
        // Throwing IndexOutOfBoundsException sometimes when tabs are swiped rapidly
        when (holder) {
            is HeaderViewHolder -> {
                holder.itemView.titleText.text = generalData[position] as String
            }
            is ItemViewHolder -> {
                holder.itemView.apply {
                    val listObject = generalData[position] as ListObject
                    titleText.text = listObject.title

                    setOnClickListener {
                        owner.onItemClicked(listObject)
                    }

                    setOnLongClickListener {
                        owner.onLongClick(listObject)
                        true
                    }
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
        return generalData[position] is String || generalData[position] is String?
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
