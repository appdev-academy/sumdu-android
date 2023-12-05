package academy.appdev.sumdu.adapters

import academy.appdev.sumdu.databinding.ListHeaderLayoutBinding
import academy.appdev.sumdu.databinding.ListItemLayoutBinding
import academy.appdev.sumdu.fragments.TabFragment
import academy.appdev.sumdu.mainActivity
import academy.appdev.sumdu.objects.ListObject
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.text.Collator


class TabListAdapter(private var data: List<ListObject>, private val owner: TabFragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
            val binding = ListItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ItemViewHolder(binding)
        } else {
            val binding = ListHeaderLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            HeaderViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // Throwing IndexOutOfBoundsException sometimes when tabs are swiped rapidly
        when (holder) {
            is HeaderViewHolder -> {
                val dataObject = (generalData[position] as? String) ?: return
                holder.bind(dataObject)
            }
            is ItemViewHolder -> {
                val dataObject = (generalData[position] as? ListObject) ?: return
                holder.bind(dataObject)
            }
        }
    }

    override fun getItemCount() = generalData.size

    override fun getItemViewType(position: Int) = if (isPositionHeader(position)) HEADER else ITEM

    private fun isPositionHeader(position: Int) = generalData[position] is String || generalData[position] is String

    inner class ItemViewHolder(private val binding: ListItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(date: ListObject) {
            binding.titleText.text = date.title

            binding.root.setOnClickListener {
                owner.onItemClicked(date)
            }

            binding.root.setOnLongClickListener {
                owner.onLongClick(date)
                true
            }
        }
    }

    inner class HeaderViewHolder(private val binding: ListHeaderLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(date: String) {
            binding.titleText.text = date
        }
    }
}
