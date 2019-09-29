package academy.appdev.sumdu.adapters

import academy.appdev.sumdu.R
import academy.appdev.sumdu.fragments.formatDate
import academy.appdev.sumdu.objects.ContentHeaderObject
import academy.appdev.sumdu.objects.ContentObject
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.content_list_item_layout.view.*
import kotlinx.android.synthetic.main.list_header_layout.view.*
import kotlinx.android.synthetic.main.list_item_layout.view.titleText


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
            val date = contentObject.date?.formatDate()
            if (contentObject.date != null && generalData.find {
                    if (it is ContentHeaderObject) {
                        it.date == date
                    } else {
                        it == date
                    }
                } == null) {
                generalData.add(ContentHeaderObject(date, contentObject.dayOfTheWeek))
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
                    val headerObject = generalData[position] as ContentHeaderObject
                    titleText.text = headerObject.date
                    dayOfWeekText.text = headerObject.dayOfWeek
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
        return if (isPositionHeader(position)) HEADER else ITEM
    }

    private fun isPositionHeader(position: Int): Boolean {
        return generalData[position] is ContentHeaderObject
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
