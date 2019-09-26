package academy.appdev.sumdu.fragments

import academy.appdev.sumdu.MainActivity
import academy.appdev.sumdu.R
import academy.appdev.sumdu.adapters.HeaderItemDecorator
import academy.appdev.sumdu.adapters.TabListAdapter
import academy.appdev.sumdu.objects.ListObject
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.tab_list_layout.*
import java.util.ArrayList

open class TabFragment : Fragment() {

    private var data = emptyList<ListObject>()

    private lateinit var listAdapter: TabListAdapter

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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        searchSetUp(menu)
    }

    open fun setNewData(newData: List<ListObject>) {
        data = newData
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

            swipeRefreshLayout.apply {
                setOnRefreshListener {
                    // todo: add request for specific endpoints for each tab
                    Log.d("TAG", "REFRESH!")
                    Handler().postDelayed({ isRefreshing = false }, 2000)
                }
            }
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
                listAdapter.setNewData(filterArrayListWithQuery(newText))
                return false
            }
        })
    }

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

        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )?.replace(R.id.containerLayout, ContentFragment())
            ?.addToBackStack(null)
            ?.commit()
    }
}