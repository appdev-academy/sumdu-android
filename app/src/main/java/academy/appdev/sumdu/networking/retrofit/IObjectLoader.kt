package academy.appdev.sumdu.networking.retrofit

import academy.appdev.sumdu.objects.ContentObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface IObjectLoader {

    @GET("index/json")
    fun getGroupContent(@Query("id_grp") id: String?, @Query("date_beg") dateBeginning: String, @Query("date_end") dateEnd: String): Call<List<ContentObject>>

    @GET("index/json")
    fun getTeacherContent( @Query("id_fio") id: String?, @Query("date_beg") dateBeginning: String, @Query("date_end") dateEnd: String): Call<List<ContentObject>>

    @GET("index/json")
    fun getAuditoriumContent( @Query("id_aud") id: String?, @Query("date_beg") dateBeginning: String, @Query("date_end") dateEnd: String): Call<List<ContentObject>>
}