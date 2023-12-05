package academy.appdev.sumdu.fragments

import academy.appdev.sumdu.GROUPS_KEY
import academy.appdev.sumdu.mainActivity
import academy.appdev.sumdu.networking.getLists
import academy.appdev.sumdu.refreshData
import android.os.Bundle
import android.view.View
class GroupTabFragment : TabFragment() {

    override var key: String = GROUPS_KEY

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshData()
    }

    override fun setUpSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            mainActivity?.getLists {
                refreshData()
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }
}
