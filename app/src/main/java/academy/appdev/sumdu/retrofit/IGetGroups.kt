package academy.appdev.sumdu.retrofit

import academy.appdev.sumdu.objects.NetworkingObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface GerritAPI {

    @GET("changes/")
    fun loadChanges(@Query("q") status: String): Call<List<NetworkingObject>>
}