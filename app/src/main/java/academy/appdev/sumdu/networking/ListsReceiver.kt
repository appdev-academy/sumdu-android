package academy.appdev.sumdu.networking

import academy.appdev.sumdu.*
import academy.appdev.sumdu.networking.retrofit.Api
import android.app.Activity
import android.content.Context


fun Activity.getLists(handler: () -> Unit) {
    val dispatchGroup = DispatchGroup()

    getAuditorium(baseContext, dispatchGroup)
    getGroup(baseContext, dispatchGroup)
    getTeacher(baseContext, dispatchGroup)

    dispatchGroup.notify {
        handler()
    }
}

private fun getTeacher(baseContext: Context, dispatchGroup: DispatchGroup) {
    dispatchGroup.enter()
    Api.getTeachersRequest(
        baseContext,
        onSuccess = {
            if (it != null) {
                val serializedTeachers = parseListObjects(it.toMap(), "id_fio")
                baseContext.sharedPreferences.edit()?.apply {
                    putString(TEACHERS_KEY, serializedTeachers)
                    apply()
                }
            }
            dispatchGroup.leave()
        },
        onFailure = { dispatchGroup.leave() }
    )
}

private fun getAuditorium(baseContext: Context, dispatchGroup: DispatchGroup) {
    dispatchGroup.enter()
    Api.getAuditoriumsRequest(
        baseContext,
        onSuccess = {
            if (it != null) {
                val serializedGroups = parseListObjects(it.toMap(), "id_aud")
                baseContext.sharedPreferences.edit()?.apply {
                    putString(AUDITORIUMS_KEY, serializedGroups)
                    apply()
                }
            }
            dispatchGroup.leave()
        },
        onFailure = { dispatchGroup.leave() }
    )
}

private fun getGroup(baseContext: Context, dispatchGroup: DispatchGroup) {
    dispatchGroup.enter()
    Api.getGroupsRequest(
        baseContext,
        onSuccess = {
            if (it != null) {
                val serializedGroups = parseListObjects(it.toMap(), "id_grp")
                baseContext.sharedPreferences.edit()?.apply {
                    putString(GROUPS_KEY, serializedGroups)
                    apply()
                }
            }
            dispatchGroup.leave()
        },
        onFailure = { dispatchGroup.leave() }
    )
}
