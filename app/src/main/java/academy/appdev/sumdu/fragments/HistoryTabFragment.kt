package academy.appdev.sumdu.fragments

import academy.appdev.sumdu.R
import academy.appdev.sumdu.mainActivity
import academy.appdev.sumdu.networking.HISTORY_KEY
import academy.appdev.sumdu.networking.parseStringToArrayList
import academy.appdev.sumdu.objects.ListObject
import android.app.AlertDialog
import androidx.core.view.isVisible
import com.google.gson.Gson
import kotlinx.android.synthetic.main.tab_list_layout.*

class HistoryTabFragment : TabFragment() {

    override fun onResume() {
        super.onResume()
        refreshData(HISTORY_KEY)
        setUpHistoryPlaceholder()
    }

    override fun setUpSwipeRefresh() {
        swipeRefreshLayout.isEnabled = false
    }

    override fun onLongClick(listObject: ListObject) {
        AlertDialog.Builder(context).apply {
            setTitle(R.string.history_delete)
//            setMessage("Are you want to set the app background color to RED?")
            setPositiveButton(R.string.delete_item) { dialog, which ->
                deleteItemFromHistory(listObject)
            }

            setNeutralButton(R.string.clear_history) { dialog, which ->
                mainActivity?.sharedPreferences?.edit()?.apply {
                    putString(HISTORY_KEY, "")
                    apply()
                }
                refreshData(HISTORY_KEY)
                setUpHistoryPlaceholder()
            }

            create()
            show()
        }
    }

    private fun setUpHistoryPlaceholder() {
        if (dataIsEmpty) {
            historyPlaceholderLayout.isVisible = true
            swipeRefreshLayout.isVisible = false
        } else {
            historyPlaceholderLayout.isVisible = false
            swipeRefreshLayout.isVisible = true
        }
    }

    private fun deleteItemFromHistory(listObject: ListObject) {
        val historyList = (parseStringToArrayList(
            mainActivity?.sharedPreferences?.getString(
                HISTORY_KEY,
                ""
            ) ?: ""
        )
            ?: arrayListOf())

        // Iterator throws ConcurrentModificationException if an item is deleted in iterator
        var objectToRemove = ListObject()

        historyList.forEach {
            if (it.id == listObject.id) {
                objectToRemove = it
            }
        }
        historyList.remove(objectToRemove)

        mainActivity?.sharedPreferences?.edit()?.apply {
            putString(HISTORY_KEY, Gson().toJson(historyList))
            apply()
        }

        refreshData(HISTORY_KEY)
        setUpHistoryPlaceholder()
    }
}
