package academy.appdev.sumdu.objects

import com.google.gson.annotations.SerializedName

class ContentObject(

    @SerializedName("ABBR_DISC")
    var title: String? = null,
    @SerializedName("NAME_STUD")
    var pairType: String? = null,
    @SerializedName("TIME_PAIR")
    var time: String? = null,
    @SerializedName("NAME_GROUP")
    var group: String? = null,
    @SerializedName("NAME_AUD")
    var auditorium: String? = null,
    @SerializedName("NAME_FIO")
    var teacher: String? = null,
    @SerializedName("DATE_REG")
    var date: String? = null
)