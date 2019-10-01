package academy.appdev.sumdu.adapters

import academy.appdev.sumdu.R
import academy.appdev.sumdu.formatDateString
import academy.appdev.sumdu.formatDayMonth
import academy.appdev.sumdu.objects.ContentObject
import academy.appdev.sumdu.toDate
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.content_list_item_layout.view.*
import kotlinx.android.synthetic.main.list_header_layout.view.*
import kotlinx.android.synthetic.main.list_item_layout.view.titleText
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ContentAdapter(private var data: List<ContentObject>, private val forGroup: Boolean?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
                holder.itemView.apply {
                    val date = generalData[position] as String
                    titleText.text = date.formatDayMonth()
                    dayOfWeekText.text = SimpleDateFormat("EEEE", Locale("ru","RU")).format(date.toDate())
                }
            }
            is ItemViewHolder -> {
                holder.itemView.apply {
                    val listObject = generalData[position] as ContentObject
                    listObject.apply {
                        titleText.text =
                            if (!pairType.isNullOrBlank()) "$title ($pairType)" else title
                        pairTimeText.text = time
                        auditoriumAndGroupText.text = if (forGroup == true) auditorium else group
                        teacherText.text = teacher
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return generalData.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (isPositionHeader(position)) ITEM else HEADER
    }

    private fun isPositionHeader(position: Int): Boolean {
        return generalData[position] is ContentObject
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
