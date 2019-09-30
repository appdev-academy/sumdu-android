package academy.appdev.sumdu.fragments

import academy.appdev.sumdu.mainActivity
import academy.appdev.sumdu.networking.TEACHERS_KEY
import academy.appdev.sumdu.networking.getLists
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.tab_list_layout.*

class TeacherTabFragment : TabFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshData(TEACHERS_KEY)
    }

    override fun setUpSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener {
            mainActivity?.getLists { refreshData(TEACHERS_KEY) }
        }
    }
}