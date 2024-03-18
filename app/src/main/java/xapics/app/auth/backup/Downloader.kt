package xapics.app.auth.backup

import android.content.Context

interface Downloader {
    fun downloadFile(context: Context, url: String): Long
}