package academy.appdev.sumdu.fragments

import academy.appdev.sumdu.*
import academy.appdev.sumdu.networking.parseStringToArrayList
import academy.appdev.sumdu.objects.ListObject
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.google.gson.Gson

class HistoryTabFragment : TabFragment() {

    override var key: String = HISTORY_KEY

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshData()
        setUpHistoryPlaceholder()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
        setUpHistoryPlaceholder()
    }

    override fun setUpSwipeRefresh() {
        binding.swipeRefreshLayout.isEnabled = false
    }

    override fun onLongClick(listObject: ListObject) {
        AlertDialog.Builder(context).apply {
            setTitle(R.string.history_delete)
            setPositiveButton(R.string.delete_item) { dialog, which ->
                mainActivity?.sharedPreferences?.edit()?.remove(CONTENT_KEY(listObject.id))?.apply()
                deleteItemFromHistory(listObject)
            }

            setNeutralButton(R.string.clear_history) { dialog, which ->
                mainActivity?.sharedPreferences?.edit()?.apply {
                    parseStringToArrayList(
                        mainActivity?.sharedPreferences?.getString(
                            HISTORY_KEY,
                            ""
                        )
                    )?.forEach {
                        remove(CONTENT_KEY(it.id))
                    }
                    remove(HISTORY_KEY)
                }?.apply()

                refreshData()
                setUpHistoryPlaceholder()
            }

            create()
            show()
        }
    }

    private fun setUpHistoryPlaceholder() {
        if (dataIsEmpty) {
            binding.historyPlaceholderLayout.isVisible = true
            binding.swipeRefreshLayout.isVisible = false
        } else {
            binding.historyPlaceholderLayout.isVisible = false
            binding.swipeRefreshLayout.isVisible = true
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

        refreshData()
        setUpHistoryPlaceholder()
    }
}
