package com.fiskus.loadingstatusapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import com.fiskus.loadingstatusapp.R
import com.fiskus.loadingstatusapp.databinding.ActivityDetailBinding
import com.fiskus.loadingstatusapp.utils.Constants
import com.fiskus.loadingstatusapp.utils.getNotificationManager

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //dismiss notifications
        getNotificationManager(this).cancelAll()
        //init binding
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        var downloadResultStatus = false
        var downloadFileName:String? = null

        intent?.let {
            //set notification extras
            downloadResultStatus = it.getBooleanExtra(Constants.EXTRA_DOWNLOAD_RESULT_STATUS,false)
            downloadFileName = it.getStringExtra(Constants.EXTRA_DOWNLOAD_MESSAGE)
        }

        //set default file name if needed
        if (downloadFileName == null) {
            downloadFileName = getString(R.string.default_selection)
        }

        //set details text
        binding.detailsBody.fileNameTitleValueTV.text = downloadFileName
        binding.detailsBody.statusValueTV.text = getString(if (downloadResultStatus) R.string.status_success else R.string.status_fail)
        binding.detailsBody.statusValueTV.setTextColor(getColor(if (downloadResultStatus) R.color.colorPrimary else R.color.colorFail))

        //set on button click listener
        binding.detailsBody.detailsMotionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int) {
                //do nothing
            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float) {
                //do nothing
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                //when transition is completed exit the activity
                onBackPressed()
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float) {
                //do nothing
            }
        })
    }
}
