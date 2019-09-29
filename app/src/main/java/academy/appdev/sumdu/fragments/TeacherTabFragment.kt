package academy.appdev.sumdu.fragments

import academy.appdev.sumdu.R
import academy.appdev.sumdu.mainActivity
import academy.appdev.sumdu.networking.GROUPS_KEY
import academy.appdev.sumdu.networking.TEACHERS_KEY
import academy.appdev.sumdu.networking.getLists
import academy.appdev.sumdu.objects.ListObject
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.tab_list_layout.*

class TeacherTabFragment : TabFragment() {

    override fun onResume() {
        super.onResume()
        mainActivity?.getLists { refreshData(TEACHERS_KEY) }
    }

    override fun setUpSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener {
            mainActivity?.getLists { refreshData(TEACHERS_KEY) }
        }
    }
}