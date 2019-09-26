package academy.appdev.sumdu.fragments

import academy.appdev.sumdu.R
import academy.appdev.sumdu.objects.ListObject
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class AuditoriumTabFragment : TabFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val historyArray = listOf(
            ListObject(1, "AUDITORIUM1"),
            ListObject(11, "ABC2"),
            ListObject(12, "ATB3"),
            ListObject(2, "BBB4"),
            ListObject(3, "CCC1"),
            ListObject(4, "DDD5"),
            ListObject(5, "EEE66"),
            ListObject(6, "FFF5"),
            ListObject(7, "GGG88"),
            ListObject(8, "HHH0")
        )
        setNewData(historyArray)
        super.onViewCreated(view, savedInstanceState)
    }
}