package academy.appdev.sumdu.fragments

import academy.appdev.sumdu.objects.ListObject
import android.os.Bundle
import android.view.View

class HistoryTabFragment : TabFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val historyArray = listOf(
            ListObject(1, "HISTORY1"),
            ListObject(11, "ABC"),
            ListObject(12, "ATB"),
            ListObject(2, "BBB"),
            ListObject(3, "CCC"),
            ListObject(4, "DDD"),
            ListObject(5, "EEE"),
            ListObject(6, "FFF"),
            ListObject(7, "GGG"),
            ListObject(8, "HHH")
        )
        setNewData(historyArray)
        super.onViewCreated(view, savedInstanceState)
    }
}