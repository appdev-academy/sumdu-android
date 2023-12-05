package academy.appdev.sumdu.fragments

import academy.appdev.sumdu.*
import academy.appdev.sumdu.adapters.ContentAdapter
import academy.appdev.sumdu.adapters.HeaderItemDecorator
import academy.appdev.sumdu.databinding.TabListLayoutBinding
import academy.appdev.sumdu.networking.parseListToJson
import academy.appdev.sumdu.networking.parseStringToList
import academy.appdev.sumdu.objects.ContentObject
import academy.appdev.sumdu.objects.ListObject
import academy.appdev.sumdu.networking.retrofit.Api
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import java.util.*


class ContentFragment : Fragment(R.layout.tab_list_layout) {

    companion object {
        /**
         * Should be set before fragment entrance.
         */
        var contentObject: ListObject? = null
    }

    private lateinit var binding: TabListLayoutBinding

    private var data = emptyList<ContentObject>()

    private lateinit var listAdapter: ContentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        binding = TabListLayoutBinding.bind(view)

        mainActivity?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = contentObject?.title
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            getResponse()
        }

        setUpRecycler()
        getResponse()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.content_screen_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                activity?.onBackPressed()
                true
            }
            R.id.save -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(icsURL(contentObject?.objectType, contentObject?.id))
                startActivity(intent)

                true
            }
            else -> false
        }
    }

    private fun getResponse() {
        binding.progressSpinner.isVisible = true

        contentObject?.apply {
            val startDate = Date().stringValue

            when (objectType) {
                "id_grp" -> Api.loadGroupContent(context, id, startDate, endDate(), ::onLoaded, ::onFailure)
                "id_fio" -> Api.loadTeacherContent(context, id, startDate, endDate(), ::onLoaded, ::onFailure)
                "id_aud" -> Api.loadAuditoriumContent(context, id, startDate, endDate(), ::onLoaded, ::onFailure)
            }
        }
    }

    private fun onLoaded(data: List<ContentObject>?) {
        this.data = data ?: emptyList()
        listAdapter.setNewData(this.data)
        mainActivity?.sharedPreferences?.edit()?.apply {
            putString(CONTENT_KEY(contentObject?.id), parseListToJson(this@ContentFragment.data))
            apply()
        }

        binding.progressSpinner.isVisible = false
    }

    private fun onFailure(throwable: Throwable) {
        binding.progressSpinner.isVisible = false
        context?.makeToast(if (data.isNullOrEmpty()) R.string.can_not_load_content else R.string.can_not_load_content_offline)
    }

    private fun endDate(): String {
        return Calendar.getInstance().apply {
            time = Date()
            add(Calendar.DATE, 29)
        }.time.stringValue
    }

    // Building up URL for getting .ics file
    private fun icsURL(chosenID: String?, contentID: String?): String {
        return Uri.Builder().scheme("https")
            .authority("sh.cabinet.sumdu.edu.ua")
            .appendPath("index")
            .appendPath("ical")
            .appendQueryParameter(chosenID, contentID)
            .appendQueryParameter("date_beg", Date().stringValue)
            .appendQueryParameter("date_end", endDate())
            .build().toString()
    }

    private fun setUpRecycler() {
        getStoredContent()
        binding.recyclerView.apply {
            setHasFixedSize(true)
            listAdapter = ContentAdapter(data, contentObject?.objectType?.equals("id_grp"))
            adapter = listAdapter
            layoutManager = LinearLayoutManager(context)

            addItemDecoration(HeaderItemDecorator(this) {
                adapter?.getItemViewType(it) == 0
            })
        }
    }

    private fun getStoredContent() {
        data = parseStringToList(
            mainActivity?.sharedPreferences?.getString(
                CONTENT_KEY(contentObject?.id),
                ""
            )
        ) ?: emptyList()
    }
}
