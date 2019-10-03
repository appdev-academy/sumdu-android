package academy.appdev.sumdu

import android.content.Context
import android.os.Build
import java.util.*

val Context.defaultLocale: Locale
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        resources.configuration.locales.get(0)
    } else {
        resources.configuration.locale
    }

val Context.appLocale get() = when (defaultLocale.language) {
    "ru" -> Locale("ru")
    "uk" -> Locale("uk")
    else -> Locale("en")
}