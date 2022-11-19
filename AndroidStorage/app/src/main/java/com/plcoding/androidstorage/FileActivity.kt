package com.plcoding.androidstorage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.IOException

class FileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file)

//        Log.d("TaiPT", this.getExternalFilesDir(null)!!.absolutePath)
//        Log.d("TaiPT", this.getExternalFilesDirs(null)!![0].absolutePath)
//        Log.d("TaiPT", this.externalCacheDir!!.absolutePath)
//        Log.d("TaiPT", this.externalMediaDirs!![0].absolutePath)

        val quote =
            "Now rise, and show your strength. Be eloquent, and deep, and tender; see, with a clear eye, into Nature, and into life:  spread your white wings of quivering thought, and soar, a god-like spirit, over the whirling world beneath you, up through long lanes of flaming stars to the gates of eternity!"
        /* Create a cipher using the first 16 bytes of the passphrase */
        /* Create a cipher using the first 16 bytes of the passphrase */
        val tea = TEA("And is there honey still for tea?".toByteArray())

        val original: ByteArray = quote.toByteArray()
        val crypt = tea.encrypt(original)
        val result = tea.decrypt(crypt)

        val test = String(result)
        Log.d("TaiPT", test)
        Log.d("TaiPT", String(crypt))
        Log.d("TaiPT", tea.S[0].toString())
        Log.d("TaiPT", tea.S[1].toString())
        Log.d("TaiPT", tea.S[2].toString())
        Log.d("TaiPT", tea.S[3].toString())
    }

    fun getLogCatFile(): File {
        var fileLogCat = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "logcat_eLB.txt"
        )
        if (fileLogCat.exists()) {
            val url_LogCat = fileLogCat.absolutePath
            fileLogCat.delete()
            fileLogCat = File(url_LogCat)
        }
        try {
            Runtime.getRuntime().exec("logcat -f " + fileLogCat.absolutePath)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return fileLogCat
    }
}