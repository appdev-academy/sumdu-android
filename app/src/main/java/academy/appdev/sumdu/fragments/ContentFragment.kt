package academy.appdev.sumdu.fragments

import academy.appdev.sumdu.R
import academy.appdev.sumdu.adapters.ContentAdapter
import academy.appdev.sumdu.adapters.HeaderItemDecorator
import academy.appdev.sumdu.mainActivity
import academy.appdev.sumdu.makeToast
import academy.appdev.sumdu.networking.*
import academy.appdev.sumdu.objects.ListObject
import academy.appdev.sumdu.objects.ContentObject
import academy.appdev.sumdu.retrofit.Api
import academy.appdev.sumdu.retrofit.Api.baseUrl
import academy.appdev.sumdu.retrofit.IObjectLoader
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.tab_list_layout.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*


class ContentFragment : Fragment() {

    init {
        setHasOptionsMenu(true)
    }

    companion object {
        /**
         * Should be set before fragment entrance.
         */
        var contentObject: ListObject? = null
    }

    private var data = emptyList<ContentObject>()

    private lateinit var listAdapter: ContentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tab_list_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mainActivity?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = contentObject?.title
        }

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            getResponse()
        }

        setUpRecycler()
        getResponse()

        super.onViewCreated(view, savedInstanceState)
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
            R.id.share -> {
                Log.d("TAG", "SHARE!")
                true
            }
            else -> false
        }
    }

    private fun getResponse() {
        progressSpinner.isVisible = true

        contentObject?.apply {
            val startDate = Date().stringValue
            fun endDate(): String {
                return Calendar.getInstance().apply {
                    time = Date()
                    add(Calendar.DATE, 30)
                }.time.stringValue
            }

            when (contentObject?.objectType) {
                "id_grp" -> {
                    Api.loadGroupContent(id, startDate, endDate(), ::onLoaded, ::onFailure)
                }
                "id_fio" -> {
                    Api.loadTeacherContent(id, startDate, endDate(), ::onLoaded, ::onFailure)
                }
                "id_aud" -> {
                    Api.loadAuditoriumContent(id, startDate, endDate(), ::onLoaded, ::onFailure)
                }
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

        progressSpinner.isVisible = false
    }

    private fun onFailure(throwable: Throwable) {
        progressSpinner.isVisible = false
        context?.makeToast(if (data.isNullOrEmpty()) R.string.can_not_load_content else R.string.can_not_load_content_offline)
    }

//    private fun setUpToolbar() {
//        (activity as AppCompatActivity?)?.apply {
//            setSupportActionBar(toolbar)
//
//            supportActionBar?.apply {
//                setDisplayHomeAsUpEnabled(true)
//                setDisplayShowHomeEnabled(true)
//            }
//
//            toolbar?.apply {
//                setHasOptionsMenu(true)
//                title = contentObject?.title ?: "GUS"
//
//                inflateMenu(R.menu.content_screen_menu)
//
//                setNavigationOnClickListener {
//                    activity?.onBackPressed()
//                }
//
//                setOnMenuItemClickListener {
//                    when (it.itemId) {
//                        android.R.id.home -> {
//                            activity?.onBackPressed()
//                            true
//                        }
//                        R.id.share -> {
//                            Log.d("TAG", "SHARE!")
//                            true
//                        }
//                        else -> {
//                            false
//                        }
//                    }
//                }
//            }
//        }
//    }

    private fun setUpRecycler() {
        getStoredContent()
        recyclerView.apply {
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

fun String.toDate(): Date? {
    return try {
        SimpleDateFormat("dd.MM.yyyy").parse(this)
    } catch (e: ParseException) {
        null
    }
}

fun String.formatDateString(): String? {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy")
    return try {
        val date = dateFormat.parse(this)
        dateFormat.format(date)
    } catch (e: ParseException) {
        null
    }
}

fun String.formatDayMonth(): String? {
    return try {
        val date = SimpleDateFormat("dd.MM.yyyy").parse(this)
        SimpleDateFormat("d MMMM", Locale("ru", "RU")).format(date)
    } catch (e: ParseException) {
        null
    }
}

val Date.stringValue: String get() = run { SimpleDateFormat("dd.MM.yyyy").format(this) }
