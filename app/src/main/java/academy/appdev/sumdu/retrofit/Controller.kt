package academy.appdev.sumdu.retrofit

import academy.appdev.sumdu.objects.NetworkingObject
import android.util.Log
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import com.google.gson.GsonBuilder
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Controller : Callback<List<NetworkingObject>> {

    fun start() {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val gerritAPI = retrofit.create(GerritAPI::class.java)

        val call = gerritAPI.loadChanges("status:open")
        call.enqueue(this)

    }

    override fun onResponse(call: Call<List<NetworkingObject>>, response: Response<List<NetworkingObject>>) {
        if (response.isSuccessful) {
            val changesList = response.body()
            changesList?.forEach { change -> println(change.title) }
        } else {
            Log.d("TAG", "ERROR: " + response.errorBody())
        }
    }

    override fun onFailure(call: Call<List<NetworkingObject>>, t: Throwable) {
        t.printStackTrace()
    }

    companion object {

        internal val BASE_URL = "https://git.eclipse.org/r/"
    }
}