package com.practicum.pexelsapp.data.util

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri

fun downloadPhoto(
    context: Context,
    url: String,
    fileName: String
    ) {

    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val uri = url.toUri()

    val request = DownloadManager.Request(uri).apply {

        setTitle("Downloading $fileName")
        setDescription("Downloading image from Pexels")
        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "$fileName.jpg")
        setAllowedOverMetered(true)
        setAllowedOverRoaming(true)
    }

    downloadManager.enqueue(request)
}