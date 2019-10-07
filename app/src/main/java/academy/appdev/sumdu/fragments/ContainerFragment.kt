package academy.appdev.sumdu.fragments

import academy.appdev.sumdu.R
import academy.appdev.sumdu.adapters.TabsAdapter
import academy.appdev.sumdu.refreshData
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.container_fragment_layout.*
import androidx.viewpager.widget.ViewPager.OnPageChangeListener


class ContainerFragment : Fragment() {

    private var tabsList = arrayListOf<TabFragment>()

    private var queryString = ""

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_screen_menu, menu)
        searchSetUp(menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setUpTabs() {
        tabsListFilling()

        viewPager.adapter = TabsAdapter(childFragmentManager).apply {
            addFragment(tabsList[0], getString(R.string.history))
            addFragment(tabsList[1], getString(R.string.group))
            addFragment(tabsList[2], getString(R.string.teacher))
            addFragment(tabsList[3], getString(R.string.auditorium))
        }

        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ){}

            override fun onPageSelected(position: Int) {
                tabsList[position].apply {
                    searchQuery = queryString
                    refreshData()
                }
            }
        })

        tabLayout.setupWithViewPager(viewPager)

        tabLayout.getTabAt(0)?.setIcon(R.drawable.ic_history)
    }

    private fun searchSetUp(menu: Menu?) {
        val search = menu?.findItem(R.id.search)
        val searchView = search?.actionView as SearchView?
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                queryString = newText

                if (viewPager?.currentItem != null) {
                    tabsList[viewPager.currentItem].apply {
                        searchQuery = newText
                        refreshData()
                    }
                }
                return false
            }
        })
    }

    private fun tabsListFilling() {
        tabsList.apply {
            add(HistoryTabFragment())
            add(GroupTabFragment())
            add(TeacherTabFragment())
            add(AuditoriumTabFragment())
        }
    }
}