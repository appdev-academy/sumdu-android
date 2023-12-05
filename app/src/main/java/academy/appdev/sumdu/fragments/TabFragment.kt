package academy.appdev.sumdu.fragments

import academy.appdev.sumdu.R
import academy.appdev.sumdu.adapters.HeaderItemDecorator
import academy.appdev.sumdu.adapters.TabListAdapter
import academy.appdev.sumdu.databinding.TabListLayoutBinding
import academy.appdev.sumdu.mainActivity
import academy.appdev.sumdu.networking.getLists
import academy.appdev.sumdu.objects.ListObject
import academy.appdev.sumdu.saveToHistory
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager


open class TabFragment : Fragment(R.layout.tab_list_layout) {

    open var key = ""

    lateinit var binding: TabListLayoutBinding

    var data = emptyList<ListObject>()

    var searchQuery = ""

    val dataIsEmpty get() = data.isNullOrEmpty()

    private var listAdapter: TabListAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = TabListLayoutBinding.bind(view)

        setUpRecycler()
    }

    fun setNewData(newData: ArrayList<ListObject>) {
        if (listAdapter != null) {
            data = newData
            listAdapter?.setNewData(newData)
        }
    }

    private fun setUpRecycler() {
        binding.recyclerView.apply {
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
        binding.swipeRefreshLayout.setOnRefreshListener {
            mainActivity?.getLists {}
        }
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

