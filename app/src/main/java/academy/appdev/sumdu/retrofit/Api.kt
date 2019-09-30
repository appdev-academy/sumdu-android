package academy.appdev.sumdu.retrofit

import academy.appdev.sumdu.objects.ContentObject
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Api {

    const val baseUrl = "http://schedule.sumdu.edu.ua/"

    private val service = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder().setLenient().create()
            )
        )
        .build().create(IObjectLoader::class.java)

    fun loadGroupContent(id: String?, dateBeg: String, dateEnd: String, onSuccess: (List<ContentObject>?) -> Unit, onFailure: (throwable: Throwable) -> Unit) {
        service.getGroupContent(id, dateBeg, dateEnd).enqueue(CommonCallbackImplementation(onSuccess, onFailure))
    }

    fun loadTeacherContent(id: String?, dateBeg: String, dateEnd: String, onSuccess: (List<ContentObject>?) -> Unit, onFailure: (throwable: Throwable) -> Unit) {
        service.getTeacherContent(id, dateBeg, dateEnd).enqueue(CommonCallbackImplementation(onSuccess, onFailure))
    }

    fun loadAuditoriumContent(id: String?, dateBeg: String, dateEnd: String, onSuccess: (List<ContentObject>?) -> Unit, onFailure: (throwable: Throwable) -> Unit) {
        service.getAuditoriumContent(id, dateBeg, dateEnd).enqueue(CommonCallbackImplementation(onSuccess, onFailure))
    }

    class CommonCallbackImplementation<T>(
        private val onSuccess: (T?) -> Unit,
        private val onFailure: ((Throwable) -> Unit)? = null
    ) : Callback<T> {

        override fun onFailure(call: Call<T>, t: Throwable) {
            onFailure?.invoke(t)
        }

        override fun onResponse(call: Call<T>, response: Response<T>) {
            onSuccess(response.body())
        }
    }
}