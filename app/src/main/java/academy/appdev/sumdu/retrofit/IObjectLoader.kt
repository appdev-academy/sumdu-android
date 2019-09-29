package academy.appdev.sumdu.retrofit

import academy.appdev.sumdu.objects.ContentObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface IObjectLoader {

    // 3 separate methods are temporary
    // todo: make it single with "contentType" dynamic parameter
    @GET("index/json")
    fun getGroupContentJson(@Query("id_grp") string: String, @Query("date_beg") dateBeginning: String, @Query("date_end") dateEnd: String): Call<List<ContentObject>>

    @GET("index/json")
    fun getTeacherContentJson( @Query("id_fio") string: String, @Query("date_beg") dateBeginning: String, @Query("date_end") dateEnd: String): Call<List<ContentObject>>

    @GET("index/json")
    fun getAuditoriumContentJson(@Query("id_aud") string: String, @Query("date_beg") dateBeginning: String, @Query("date_end") dateEnd: String): Call<List<ContentObject>>

}