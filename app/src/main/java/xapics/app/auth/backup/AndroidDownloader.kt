package xapics.app.auth.backup

import android.app.DownloadManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import androidx.core.net.toUri

class AndroidDownloader(
    private val prefs: SharedPreferences
): Downloader {

    override fun downloadFile(context: Context, url: String): Long {
        val downloadManager = context.getSystemService(DownloadManager::class.java)

        val token = prefs.getString("jwt", null)// ?: return AuthResult.Unauthorized()

        val request = DownloadManager.Request(url.toUri())
            .setMimeType("application/zip")
//            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle("volume-backup.zip")
            .addRequestHeader("Authorization", "Bearer $token")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "volume-backup.zip")

        return downloadManager.enqueue(request)
    }
}