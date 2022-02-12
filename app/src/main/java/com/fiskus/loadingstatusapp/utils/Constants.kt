package com.fiskus.loadingstatusapp.utils

object Constants {
    //downloads urls
    const val GLIDE_URL =
        "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
    const val LOAD_APP_URL =
        "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
    const val RETROFIT_URL =
        "https://github.com/square/retrofit/archive/refs/heads/master.zip"

    //download notification channel id
    const val DOWNLOAD_NOTIFICATION_CHANNEL_ID = "download"
    //send complete download notification request code
    const val SEND_DOWNLOAD_COMPLETED_NOTIFICATION_ID = 100
    //intent extra download status
    const val EXTRA_DOWNLOAD_RESULT_STATUS = "download_result"
    //intent extra download message
    const val EXTRA_DOWNLOAD_MESSAGE = "download_message"

}