package com.fiskus.loadingstatusapp.ui

import android.app.DownloadManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import com.fiskus.loadingstatusapp.R
import com.fiskus.loadingstatusapp.databinding.ActivityMainBinding
import com.fiskus.loadingstatusapp.utils.Constants
import com.fiskus.loadingstatusapp.utils.createChannel
import com.fiskus.loadingstatusapp.utils.sendDownloadCompletedNotification

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var binding: ActivityMainBinding

    //radio group HM
    private lateinit var radioGroupHM: HashMap<Int,()->Unit>

    //radio group lambdas
    private val downloadGlideLambda = {
        download(Constants.GLIDE_URL)
    }
    private val downloadLoadAppLambda = {
        download(Constants.LOAD_APP_URL)
    }
    private val downloadRetrofitLambda = {
        download(Constants.RETROFIT_URL)
    }

    //radio actions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //init view binding
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        //init radio group HM
        initRadioGroupHM()

        //create notification channel
        createChannel(this, Constants.DOWNLOAD_NOTIFICATION_CHANNEL_ID, getString(R.string.notification_download_channel))

        //on button clicked checked change
        binding.mainBody.downloadButton.setOnClickListener{
            //disable radio group clicks while downloading
            binding.mainBody.downloadRadioGroup.setRadioButtonsClick(false)

            //get current check id
            val checkedRadioId = binding.mainBody.downloadRadioGroup.checkedRadioButtonId
            //get download lambda from HM by check id
            val downloadLambda = radioGroupHM[checkedRadioId]
            //check if download lambda is not null
            if (downloadLambda != null) {
                //execute download lambda to download the correct file
                downloadLambda()
            } else {
                //toast that no file has been selected
                Toast.makeText(applicationContext, applicationContext.getString(R.string.download_error_toast), Toast.LENGTH_SHORT).show()
                //stop loading process
                stopLoading()
            }
        }
    }

    private fun initRadioGroupHM() {
        radioGroupHM = hashMapOf(
            R.id.radio_glide_selection to downloadGlideLambda,
            R.id.radio_load_app_selection to downloadLoadAppLambda,
            R.id.radio_retrofit_selection to downloadRetrofitLambda
        )
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            //show download completed notification
            val notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager
            notificationManager.sendDownloadCompletedNotification(applicationContext,binding.mainBody.downloadRadioGroup.getCheckedRadioButtonText(), isDownloadSucceeded())
            //stop loading process
            stopLoading()
        }
    }

    /**
     * Download the zip file using the download manager
     * @param url- the url to download from
     * */
    private fun download(url:String) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        downloadID = getDownloadManager().enqueue(request)// enqueue puts the download request in the queue.
    }

    /**
     * @return download status using the download id
     * */
    private fun isDownloadSucceeded(): Boolean {
        //get the download status using a query on the download manager with the download id
        val query = DownloadManager.Query()
        query.setFilterById(downloadID)
        val cursor = getDownloadManager().query(query)
        if (cursor.moveToFirst()) {
            val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            val status = cursor.getInt(statusIndex)
            if (DownloadManager.STATUS_SUCCESSFUL == status) {
                return true
            }
        }
        return false
    }

    /**
     * @return download manager
     * */
    private fun getDownloadManager()= getSystemService(DOWNLOAD_SERVICE) as DownloadManager

    //stop animation and enable radio group buttons
    fun stopLoading() {
        binding.mainBody.downloadButton.stopLoadingAnimation()
        binding.mainBody.downloadRadioGroup.setRadioButtonsClick(true)
    }

    //get radio button checked text
    fun RadioGroup.getCheckedRadioButtonText() = findViewById<RadioButton>(this.checkedRadioButtonId)?.text.toString()

    //set radio buttons click option
    private fun RadioGroup.setRadioButtonsClick(enableFlag: Boolean) {
        this.forEach {
            it.isClickable = enableFlag
        }
    }
}
