package academy.appdev.sumdu.fragments

import academy.appdev.sumdu.*
import academy.appdev.sumdu.adapters.HeaderItemDecorator
import academy.appdev.sumdu.adapters.TabListAdapter
import academy.appdev.sumdu.networking.getLists
import academy.appdev.sumdu.networking.parseStringToArrayList
import academy.appdev.sumdu.objects.ListObject
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.tab_list_layout.*


open class TabFragment : Fragment() {

    open var key = ""

    var data = emptyList<ListObject>()

    var searchQuery = ""

    val dataIsEmpty get() = data.isNullOrEmpty()

    private var listAdapter: TabListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setHasOptionsMenu(true)
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
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        searchSetUp(menu)
//    }

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

//    private fun searchSetUp(menu: Menu?) {
//        val search = menu?.findItem(R.id.search)
//        val searchView = search?.actionView as SearchView?
//        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String): Boolean {
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String): Boolean {
//                listAdapter?.setNewData(filterArrayListWithQuery(newText))
//                return false
//            }
//        })
//    }

    fun filterWithQuery() {
        val filteredArray = ArrayList<ListObject>()
        val generalArrayList = parseStringToArrayList(mainActivity?.sharedPreferences?.getString(key, ""))
            ?: arrayListOf()
        generalArrayList.forEach {
            if (it.title?.toLowerCase()?.contains(searchQuery.toLowerCase()) == true) {
                filteredArray.add(it)
            }
        }
        data = filteredArray
        setNewData(filteredArray)

        swipeRefreshLayout.isRefreshing = false
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

