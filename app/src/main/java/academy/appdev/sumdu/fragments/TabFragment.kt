package academy.appdev.sumdu.fragments

import academy.appdev.sumdu.MainActivity
import academy.appdev.sumdu.R
import academy.appdev.sumdu.adapters.HeaderItemDecorator
import academy.appdev.sumdu.adapters.TabListAdapter
import academy.appdev.sumdu.mainActivity
import academy.appdev.sumdu.networking.*
import academy.appdev.sumdu.objects.ListObject
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.tab_list_layout.*
import java.util.*
import kotlin.collections.ArrayList


open class TabFragment : Fragment() {

    private var data = emptyList<ListObject>()

    val dataIsEmpty get() = data.isNullOrEmpty()

    protected var listAdapter: TabListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tab_list_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecycler()
        sharedPreferencesListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        searchSetUp(menu)
    }

    fun setNewData(newData: ArrayList<ListObject>) {
        if (listAdapter != null) {
            data = newData
            listAdapter?.setNewData(newData)
        }
    }

    private fun setUpRecycler() {
        recyclerView.apply {
            setHasFixedSize(true)
            listAdapter = TabListAdapter(data, this@TabFragment)
            adapter = listAdapter
            layoutManager = LinearLayoutManager(context)

            addItemDecoration(HeaderItemDecorator(this) {
                adapter?.getItemViewType(it) == 0
            })

            setUpSwipeRefresh()
        }
    }

    open fun setUpSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener {
            mainActivity?.getLists {}
        }
    }

    private fun searchSetUp(menu: Menu?) {
        val search = menu?.findItem(R.id.search)
        val searchView = search?.actionView as SearchView?
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                listAdapter?.setNewData(filterArrayListWithQuery(newText))
                return false
            }
        })
    }

    open fun sharedPreferencesListeners() {}

    fun filterArrayListWithQuery(query: String): ArrayList<ListObject> {
        val filteredArray = ArrayList<ListObject>()
        for (record in data) {
            if (record.title?.toLowerCase()?.contains(query.toLowerCase()) == true) {
                filteredArray.add(record)
            }
        }
        return filteredArray
    }

    fun onItemClicked(listObject: ListObject) {
        ContentFragment.contentObject = listObject

        saveToHistory(listObject)

        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )?.replace(R.id.containerLayout, ContentFragment())
            ?.addToBackStack(null)
            ?.commit()
    }

    open fun onLongClick(listObject: ListObject) {}
}

fun TabFragment.refreshData(key: String) {
    setNewData(
        parseStringToArrayList(mainActivity?.sharedPreferences?.getString(key, "") ?: "")
            ?: arrayListOf()
    )
}

fun TabFragment.saveToHistory(
    newHistoryObject: ListObject
): String {
    val history =
        parseStringToArrayList(mainActivity?.sharedPreferences?.getString(HISTORY_KEY, "") ?: "")
            ?: arrayListOf()

    var doesHistoryContainsObject = false
    history.forEach {
        if (it.id == newHistoryObject.id) doesHistoryContainsObject = true
    }

    if (!doesHistoryContainsObject) {
        history.add(history.size, newHistoryObject)
    }

    val jsonHistoryString = Gson().toJson(history)

    mainActivity?.sharedPreferences?.edit()?.apply {
        putString(HISTORY_KEY, jsonHistoryString)
        apply()
    }

    return jsonHistoryString
}