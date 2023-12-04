package academy.appdev.sumdu.networking.retrofit

import academy.appdev.sumdu.appLocale
import academy.appdev.sumdu.objects.ContentObject
import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Api {

    private fun baseUrl(context: Context?): String =
        if (context?.appLocale?.language == "ru" || context?.appLocale?.language == "uk") "http://schedule.sumdu.edu.ua/" else "http://schedule.sumdu.edu.ua/en/"

    private fun service(context: Context?): IObjectLoader = Retrofit.Builder()
        .baseUrl(baseUrl(context))
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder().setLenient().create()
            )
        )
        .client(OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }).build())
        .build().create(IObjectLoader::class.java)

    fun loadGroupContent(
        context: Context?,
        id: String?,
        dateBeg: String,
        dateEnd: String,
        onSuccess: (List<ContentObject>?) -> Unit,
        onFailure: (throwable: Throwable) -> Unit
    ) {
        service(context).getGroupContent(id, dateBeg, dateEnd)
            .enqueue(CommonCallbackImplementation(onSuccess, onFailure))
    }

    fun loadTeacherContent(
        context: Context?,
        id: String?,
        dateBeg: String,
        dateEnd: String,
        onSuccess: (List<ContentObject>?) -> Unit,
        onFailure: (throwable: Throwable) -> Unit
    ) {
        service(context).getTeacherContent(id, dateBeg, dateEnd)
            .enqueue(CommonCallbackImplementation(onSuccess, onFailure))
    }

    fun loadAuditoriumContent(
        context: Context?,
        id: String?,
        dateBeg: String,
        dateEnd: String,
        onSuccess: (List<ContentObject>?) -> Unit,
        onFailure: (throwable: Throwable) -> Unit
    ) {
        service(context).getAuditoriumContent(id, dateBeg, dateEnd)
            .enqueue(CommonCallbackImplementation(onSuccess, onFailure))
    }

    fun getTeachersRequest(
        context: Context,
        onSuccess: (JsonObject?) -> Unit,
        onFailure: (throwable: Throwable) -> Unit
    ) {
        service(context).getTeachers()
            .enqueue(CommonCallbackImplementation(onSuccess, onFailure))
    }

    fun getGroupsRequest(
        context: Context,
        onSuccess: (JsonObject?) -> Unit,
        onFailure: (throwable: Throwable) -> Unit
    ) {
        service(context).getGroups()
            .enqueue(CommonCallbackImplementation(onSuccess, onFailure))
    }

    fun getAuditoriumsRequest(
        context: Context,
        onSuccess: (JsonObject?) -> Unit,
        onFailure: (throwable: Throwable) -> Unit
    ) {
        service(context).getAuditoriums()
            .enqueue(CommonCallbackImplementation(onSuccess, onFailure))
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
