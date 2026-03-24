package com.flow.pharos

import android.app.Application
import android.util.Log
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PharosApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            PDFBoxResourceLoader.init(this)
        } catch (e: Exception) {
            Log.w("PharosApp", "PDFBox init failed - PDF support may be unavailable", e)
        }
    }
}
