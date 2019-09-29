package academy.appdev.sumdu.fragments

import academy.appdev.sumdu.R
import academy.appdev.sumdu.adapters.TabListAdapter
import academy.appdev.sumdu.adapters.TabsAdapter
import academy.appdev.sumdu.mainActivity
import academy.appdev.sumdu.networking.getLists
import academy.appdev.sumdu.objects.ListObject
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.container_fragment_layout.*
import kotlinx.android.synthetic.main.tab_list_layout.*

class ContainerFragment : Fragment() {

    private lateinit var tabsAdapter: TabsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.container_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpTabs()
    }

//    override fun onResume() {
//        super.onResume()
//        tabsAdapter.notifyDataSetChanged()
//    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_screen_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setUpTabs() {
        if (mainActivity?.supportFragmentManager != null) {
            tabsAdapter = TabsAdapter(mainActivity!!.supportFragmentManager)
            tabsAdapter.apply {
                addFragment(HistoryTabFragment(), getString(R.string.history))
                addFragment(GroupTabFragment(), getString(R.string.group))
                addFragment(TeacherTabFragment(), getString(R.string.teacher))
                addFragment(AuditoriumTabFragment(), getString(R.string.auditorium))
            }

            viewPager.adapter = tabsAdapter
            tabLayout.setupWithViewPager(viewPager)

            tabLayout.getTabAt(0)?.setIcon(R.drawable.ic_history)
        }
    }
}