package academy.appdev.sumdu


val GROUPS_KEY = "academy.appdev.sumdu.groups"
val TEACHERS_KEY = "academy.appdev.sumdu.teachers"
val AUDITORIUMS_KEY = "academy.appdev.sumdu.auditoriums"
val HISTORY_KEY = "academy.appdev.sumdu.history"
fun CONTENT_KEY(contentId: String?): String {
    return ("$HISTORY_KEY.$contentId")
}
