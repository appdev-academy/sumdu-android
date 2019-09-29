package academy.appdev.sumdu.fragments

import academy.appdev.sumdu.mainActivity
import academy.appdev.sumdu.networking.GROUPS_KEY
import academy.appdev.sumdu.networking.getLists
import kotlinx.android.synthetic.main.tab_list_layout.*

class GroupTabFragment : TabFragment() {
    override fun onResume() {
        super.onResume()
        mainActivity?.getLists { refreshData(GROUPS_KEY) }
    }

    override fun setUpSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener {
            mainActivity?.getLists { refreshData(GROUPS_KEY) }
        }
    }
}