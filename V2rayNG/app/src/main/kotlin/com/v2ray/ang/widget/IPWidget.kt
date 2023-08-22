package com.v2ray.ang.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViews.RemoteView
import com.v2ray.ang.R
import com.v2ray.ang.util.Utils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

val ipUrls = listOf(
    "https://v4.ident.me",
    "https://api.ipify.org",
    "https://api4.ipify.org",
    "https://ipv4.icanhazip.com",
)

/**
 * Implementation of App Widget functionality.
 */
class IPWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        val views = RemoteViews(context.packageName, R.layout.i_p_widget)

        println("on update called")
        views.setViewVisibility(R.id.loading_ip, View.VISIBLE)

        GlobalScope.launch {
            val ip = getUrlContext(ipUrls.random(), 10000)

            println("your ip is $ip")
            for (appWidgetId in appWidgetIds) {
                views.setTextViewText(R.id.ip_widget, ip)
                views.setViewVisibility(R.id.loading_ip, View.GONE)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }


        }

    }



    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
        val views = RemoteViews(context.packageName, R.layout.i_p_widget)

        views.setViewVisibility(R.id.loading_ip, View.VISIBLE)
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val widgetText = context.getString(R.string.appwidget_text)
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.i_p_widget)
    views.setTextViewText(R.id.ip_widget, widgetText)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

suspend fun getUrlContext(url: String, timeout: Int): String {
    var result: String
    var conn: HttpURLConnection? = null

    try {
        conn = URL(url).openConnection() as HttpURLConnection
        conn.connectTimeout = timeout
        conn.readTimeout = timeout
        conn.setRequestProperty("Connection", "close")
        conn.instanceFollowRedirects = false
        conn.useCaches = false
        //val code = conn.responseCode
        result = conn.inputStream.bufferedReader().readText()
    } catch (e: Exception) {
        result = ""
        println(e)
    } finally {
        conn?.disconnect()
    }
    return result
}