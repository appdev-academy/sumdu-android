package academy.appdev.sumdu.networking.retrofit

import academy.appdev.sumdu.objects.ContentObject
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface IObjectLoader {

    @GET("index/json")
    fun getGroupContent(
        @Query("method") method: String = "getSchedules",
        @Query("id_grp") id: String?,
        @Query("date_beg") dateBeginning: String,
        @Query("date_end") dateEnd: String
    ): Call<List<ContentObject>>

    @GET("index/json")
    fun getTeacherContent(
        @Query("method") method: String = "getSchedules",
        @Query("id_fio") id: String?,
        @Query("date_beg") dateBeginning: String,
        @Query("date_end") dateEnd: String
    ): Call<List<ContentObject>>

    @GET("index/json")
    fun getAuditoriumContent(
        @Query("method") method: String = "getSchedules",
        @Query("id_aud") id: String?,
        @Query("date_beg") dateBeginning: String,
        @Query("date_end") dateEnd: String
    ): Call<List<ContentObject>>

    @GET("index/json")
    fun getGroups(
        @Query("method") method: String = "getGroups"
    ): Call<JsonObject>

    @GET("index/json")
    fun getTeachers(
        @Query("method") method: String = "getTeachers"
    ): Call<JsonObject>

    @GET("index/json")
    fun getAuditoriums(
        @Query("method") method: String = "getAuditoriums"
    ): Call<JsonObject>
}
