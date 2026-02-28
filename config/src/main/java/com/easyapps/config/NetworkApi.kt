package com.easyapps.config

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.easyapps.config.dialog.LoadingDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import androidx.core.net.toUri
import com.easyapps.config.dialog.InformativeDialog
import kotlinx.coroutines.Job
import org.json.JSONArray

class NetworkApi(private val activity: AppCompatActivity) {

    private val client = OkHttpClient()
    private var prefs = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
    private var loadingDialog = LoadingDialog(activity)

    private suspend fun startRequest(url: String): JSONObject = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .build()

        return@withContext try {
            val response = client.newCall(request).execute()
            val bodyStr = response.body.string()
            val result = bodyStr.asObject()
            result.put("isSuccess", response.isSuccessful)
            result
        } catch (_: IOException) {
            JSONObject().put("isSuccess", false)
        }
    }

    private fun String.asObject() = runCatching {
        if (this.isEmpty()) JSONObject()
        else JSONObject(this)
    }.getOrDefault(JSONObject())

    private fun getFromPref(key: String, defValue: String = ""): String {
        return prefs.getString(key, defValue) ?: ""
    }

    private  fun setFromPref(key: String, value: String = "") {
        prefs.edit { putString(key, value) }
    }

    fun setConfig(url: String,version: Int,onFirstComplete: () -> Unit = {}) {
        val json = getFromPref( "config")
        if (json.isEmpty()) {
            loadingDialog.show()
            setConfiguration(version,url,onFirstComplete)
        } else {
            Config.jsonConfig = JSONObject(json)
            setAdsParams(version)
            setConfiguration(version,url,onFirstComplete)
        }
    }

    private fun setConfiguration(appVersion: Int,url: String,onFinish: () -> Unit= {}): Job = activity.lifecycleScope.launch {
        val results = startRequest(url)
        val globalJson = getFromPref("config")
        if (results.optBoolean("isSuccess")) {
            if (globalJson.isEmpty()) {
                Config.jsonConfig = results
                setAdsParams(appVersion)
                onFinish.invoke()
            }
            setFromPref("config", "$results")
        } else if (globalJson.isEmpty()) showError {
            setConfiguration(appVersion, url)
        }
    }
    private fun showError(onClick: () -> Unit = {}) = with(InformativeDialog.Builder(activity)) {
        loadingDialog.dismiss()
        setMessage("Нет подключения к интернету")
        setAnimation("error.json")
        setPositiveButton("Попробовать снова") {
            onClick.invoke()
        }
        show()
    }

    private fun setAdsParams(appVersion: Int) {
        loadingDialog.dismiss()
        val jsonConfig = Config.jsonConfig.optJSONObject("config") ?: JSONObject()
        Config.adsBanner = jsonConfig.jsonArray("adsBanner")
        Config.adsInterstitial = jsonConfig.jsonArray("adsInterstitial")
        Config.adsReward = jsonConfig.jsonArray("adsReward")
        val version = jsonConfig.optInt("version",1)
        val message = jsonConfig.optString("message")
        val url = jsonConfig.optString("urlMessage")


        val infoId = jsonConfig.optString("infoId")
        val infoIsCancelable = jsonConfig.optBoolean("infoIsCancelable")
        val infoMessage = jsonConfig.optString("infoMessage")
        val infoLottie = jsonConfig.optString("infoLottie")
        val infoAction = jsonConfig.optString("infoAction")


        if (checkVersion(appVersion,version,message,url)) {
            showInformationDialog(infoId,infoMessage, infoAction,infoLottie,infoIsCancelable)
        }
    }

    private fun checkVersion(appVersion: Int, version: Int, message: String, url: String): Boolean {
        return if (appVersion < version) {
            InformativeDialog.Builder(activity).apply {
                setMessage(message)
                setAnimation("update.json")
                setPositiveButton("Перейти") {
                    goWithUrl(url)
                    activity.finish()
                }
                show()
            }
            true
        } else {
            var rateTime = getFromPref("rateTime", "0").toInt()
            rateTime += 1
            setFromPref("rateTime", "$rateTime")
            if (rateTime == 30) checkRate()
            false
        }
    }

    private fun checkRate() = with(InformativeDialog.Builder(activity)) {
        setTitle("Оцените приложение")
        setMessage("Понравилось приложение? Оставьте отзыв!")
        setAnimation("rate.json")
        setCancelable(true)
        setPositiveButton("Понравилось") { goWithUrl( "https://play.google.com/store/apps/details?id=${activity.packageName}") }
        setNegativeButton("Не понравилось") { sendEmail(activity) }
        show()
    }

    private fun goWithUrl(url:String) {
        val intent = Intent(Intent.ACTION_VIEW,  url.toUri())
        activity.startActivity(intent)
    }

    private fun sendEmail(context: Context) {
        val email = Intent(Intent.ACTION_SEND)
        email.putExtra(Intent.EXTRA_EMAIL, arrayOf("khurshed.apps@gmail.com"))
        email.putExtra(Intent.EXTRA_SUBJECT, "Написать письмо")
        email.type = "message/rfc822"
        context.startActivity(Intent.createChooser(email, "Выбор:"))
    }

    private fun JSONObject.jsonArray(key: String) = optJSONArray(key) ?: JSONArray()

    private fun showInformationDialog(id:String, information:String, infoAction: String, lottieJSON: String?= null, cancelable: Boolean = true)= activity.lifecycleScope.launch(Dispatchers.Main){
        if (id.isEmpty()) return@launch
        if (getPrefBool("showInformation_$id",true)){
            val informDialog = InformativeDialog.Builder(activity)
            informDialog.setCancelable(cancelable)
            informDialog.setMessage(information)
            informDialog.setAnimation(lottieJSON)
            if (cancelable) informDialog.setPositiveButton("OK"){
                if (infoAction.startsWith("https://")) goWithUrl(infoAction)
                setPrefBool("showInformation_$id",false)
            }
            informDialog.show()
        }
    }


    private fun getPrefBool(key: String, defValue: Boolean =false): Boolean {
        return prefs.getBoolean(key, defValue)
    }

    private fun setPrefBool( key: String, value: Boolean =false) {
        prefs.edit { putBoolean(key, value) }
    }


}
