package academy.appdev.sumdu.adapters

import academy.appdev.sumdu.*
import academy.appdev.sumdu.databinding.ContentListItemLayoutBinding
import academy.appdev.sumdu.databinding.ListHeaderLayoutBinding
import academy.appdev.sumdu.objects.ContentObject
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat


class ContentAdapter(private var data: List<ContentObject>, private val forGroup: Boolean?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var generalData: ArrayList<Any> = ArrayList()

    init {
        mixGeneralArray()
    }

    private val ITEM = 1
    private val HEADER = 0

    fun setNewData(newData: List<ContentObject>) {
        data = newData
        mixGeneralArray()
        notifyDataSetChanged()
    }

    private fun mixGeneralArray() {
        generalData = ArrayList()
        data.sortedBy { it.date }.forEach { contentObject ->
            val date = contentObject.date?.formatDateString()
            if (date != null && !generalData.contains(date)) {
                generalData.add(date)
                generalData.add(contentObject)
            } else {
                generalData.add(contentObject)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM) {
            val binding = ContentListItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ItemViewHolder(binding)
        } else {
            val binding = ListHeaderLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            HeaderViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                val dataObject = (generalData[position] as? String) ?: return
                holder.bind(dataObject)
            }

            is ItemViewHolder -> {
                val dataObject = (generalData[position] as? ContentObject) ?: return
                holder.bind(dataObject)
            }
        }
    }

    override fun getItemCount() = generalData.size

    override fun getItemViewType(position: Int) = if (isPositionHeader(position)) ITEM else HEADER

    private fun isPositionHeader(position: Int) = generalData[position] is ContentObject

    inner class HeaderViewHolder(private val binding: ListHeaderLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(date: String) {
            binding.titleText.text = date.formatDayMonth(binding.root.context)
            binding.dayOfWeekText.text = SimpleDateFormat("EEEE", binding.root.context?.appLocale).format(date.toDate())
        }
    }

    inner class ItemViewHolder(private val binding: ContentListItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(listObject: ContentObject) {
            with(listObject) {
                binding.titleText.text = if (!pairType.isNullOrBlank()) "$title ($pairType)" else title
                binding.pairTimeText.text = time
                binding.auditoriumAndGroupText.text = if (forGroup == true) auditorium else group
                binding.teacherText.text = teacher
            }
        }
    }
}
