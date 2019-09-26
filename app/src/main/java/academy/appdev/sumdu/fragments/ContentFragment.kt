package academy.appdev.sumdu.fragments

import academy.appdev.sumdu.MainActivity
import academy.appdev.sumdu.R
import academy.appdev.sumdu.adapters.ContentAdapter
import academy.appdev.sumdu.adapters.HeaderItemDecorator
import academy.appdev.sumdu.mainActivity
import academy.appdev.sumdu.objects.ListObject
import academy.appdev.sumdu.objects.NetworkingObject
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.tab_list_layout.*
import java.text.ParseException
import java.text.SimpleDateFormat


class ContentFragment : Fragment() {

    init {
        setHasOptionsMenu(true)
    }

    companion object {
        /**
         * Should be set before fragment entrance.
         */
        var contentObject: ListObject? = null
    }

    private var data = emptyList<NetworkingObject>()

    private lateinit var listAdapter: ContentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tab_list_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        data = listOf(
            NetworkingObject(
                1,
                "Философия 1",
                "практическая работа",
                "08:15-9:30",
                "Г1309",
                "Чибиряк Олег Павловна",
                "01-30-2019",
                "Понедельник"
            ),
            NetworkingObject(
                2,
                "Философия 2",
                "практическая работа",
                "08:15-9:30",
                "Г1309",
                "Чибиряк Олег Павловна",
                "01-30-2019",
                "Понедельник"
            ),
            NetworkingObject(
                3,
                "Философия 3",
                "практическая работа",
                "08:15-9:30",
                "Г1309",
                "Чибиряк Олег Павловна",
                "09-21-2019",
                "Среда"
            ),
            NetworkingObject(
                4,
                "Философия 4",
                "практическая работа",
                "08:15-9:30",
                "Г1309",
                "Чибиряк Олег Павловна",
                "09-21-2019",
                "Среда"
            ),
            NetworkingObject(
                5,
                "Философия 5",
                "практическая работа",
                "08:15-9:30",
                "Г1309",
                "Чибиряк Олег Павловна",
                "09-21-2019",
                "Среда"
            ),
            NetworkingObject(
                6,
                "Философия 6",
                "практическая работа",
                "08:15-9:30",
                "Г1309",
                "Чибиряк Олег Павловна",
                "03-05-2019",
                "Четверг"
            ),
            NetworkingObject(
                7,
                "Философия 7",
                "практическая работа",
                "08:15-9:30",
                "Г1309",
                "Чибиряк Олег Павловна",
                "03-05-2019",
                "Четверг"
            ),
            NetworkingObject(
                8,
                "Философия 8",
                "практическая работа",
                "08:15-9:30",
                "Г1309",
                "Чибиряк Олег Павловна",
                "03-05-2019",
                "Четверг"
            )
        )

//        setUpToolbar()

        mainActivity?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = contentObject?.title
        }

        setUpRecycler()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.content_screen_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                activity?.onBackPressed()
                true
            }
            R.id.share -> {
                Log.d("TAG", "SHARE!")
                true
            }
            else -> false
        }
    }

//    private fun setUpToolbar() {
//        (activity as AppCompatActivity?)?.apply {
//            setSupportActionBar(toolbar)
//
//            supportActionBar?.apply {
//                setDisplayHomeAsUpEnabled(true)
//                setDisplayShowHomeEnabled(true)
//            }
//
//            toolbar?.apply {
//                setHasOptionsMenu(true)
//                title = contentObject?.title ?: "GUS"
//
//                inflateMenu(R.menu.content_screen_menu)
//
//                setNavigationOnClickListener {
//                    activity?.onBackPressed()
//                }
//
//                setOnMenuItemClickListener {
//                    when (it.itemId) {
//                        android.R.id.home -> {
//                            activity?.onBackPressed()
//                            true
//                        }
//                        R.id.share -> {
//                            Log.d("TAG", "SHARE!")
//                            true
//                        }
//                        else -> {
//                            false
//                        }
//                    }
//                }
//            }
//        }
//    }

    private fun setUpRecycler() {
        recyclerView.apply {
            setHasFixedSize(true)
            listAdapter = ContentAdapter(data)
            adapter = listAdapter
            layoutManager = LinearLayoutManager(context)

            addItemDecoration(HeaderItemDecorator(this) {
                adapter?.getItemViewType(it) == 0
            })

            swipeRefreshLayout.apply {
                setOnRefreshListener {
                    // todo: add request
                    Log.d("TAG", "REFRESH!")
                    Handler().postDelayed({ isRefreshing = false }, 2000)
                }
            }
        }
    }
}

fun String.formatDate(): String? {
    val dateFormat = SimpleDateFormat("dd-mm-yyyy")
    return try {
        val date = dateFormat.parse(this)
        dateFormat.format(date)
    } catch (e: ParseException) {
        null
    }
}
