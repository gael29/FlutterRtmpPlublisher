package io.github.ponnamkarthik.flutterrtmppublisher

import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.github.faucamp.simplertmp.RtmpHandler
import net.ossrs.yasea.SrsCameraView
import net.ossrs.yasea.SrsEncodeHandler
import net.ossrs.yasea.SrsPublisher
import net.ossrs.yasea.SrsRecordHandler
import java.io.IOException
import java.net.SocketException
import java.util.*

class RTMPActivity:AppCompatActivity(), RtmpHandler.RtmpListener, SrsRecordHandler.SrsRecordListener, SrsEncodeHandler.SrsEncodeListener {
    private lateinit var circleBtn: ToggleButton
    private lateinit var btnSwitchCamera:ImageView
    private lateinit var btnBack:ImageView
    //    private lateinit var btnPause:Button
    private lateinit var sp:SharedPreferences
    private var rtmpUrl = "rtmp://live.mux.com/app/22083600-1066-72a9-adf9-704aaf1c42b8"
    private lateinit var mPublisher:SrsPublisher


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         supportActionBar?.hide()
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
         getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)
        // response screen rotation event
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        // restore data.


        sp = getSharedPreferences("Yasea", MODE_PRIVATE)

        rtmpUrl = intent.getStringExtra("url")

        circleBtn = findViewById<ToggleButton>(R.id.circleBtn)
        btnSwitchCamera = findViewById<ImageView>(R.id.swCam)
        btnBack = findViewById<ImageView>(R.id.backButton)
//        btnPause = findViewById(R.id.pause) as Button
//        btnPause.isEnabled = false
        mPublisher = SrsPublisher(findViewById<SrsCameraView>(R.id.glsurfaceview_camera))
        mPublisher.setEncodeHandler(SrsEncodeHandler(this))
        mPublisher.setRtmpHandler(RtmpHandler(this))
        mPublisher.setRecordHandler(SrsRecordHandler(this))
        mPublisher.setPreviewResolution(1920, 1080)
        mPublisher.setOutputResolution(1080, 1920)
        mPublisher.setVideoHDMode()
        mPublisher.startCamera()
        circleBtn.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                val editor = sp.edit()
                editor.putString("rtmpUrl", rtmpUrl)
                editor.apply()
                mPublisher.startPublish(rtmpUrl)
                mPublisher.startCamera()

            } else {
                mPublisher.stopPublish()
                mPublisher.stopRecord()
                this.onBackPressed()
            }
        }

        btnSwitchCamera.setOnClickListener {
            mPublisher.switchCameraFace((mPublisher.cameraId + 1) % Camera.getNumberOfCameras())
        }

        btnBack.setOnClickListener {
            mPublisher.stopPublish()
             this.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        mPublisher.resumeRecord()
    }
    override  fun onPause() {
        super.onPause()
        mPublisher.pauseRecord()
    }
    override  fun onDestroy() {
        super.onDestroy()
        mPublisher.stopPublish()
        mPublisher.stopRecord()
    }
    override fun onConfigurationChanged(newConfig:Configuration) {
        super.onConfigurationChanged(newConfig)
        mPublisher.stopEncode()
        mPublisher.stopRecord()
        mPublisher.setScreenOrientation(newConfig.orientation)
        mPublisher.startCamera()
    }
    private fun handleException(e:Exception) {
        try
        {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
            mPublisher.stopPublish()
            mPublisher.stopRecord()
        }
        catch (e1:Exception) {
            //
        }
    }
    // Implementation of SrsRtmpListener.
    override fun onRtmpConnecting(msg:String) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show()
    }
    override fun onRtmpConnected(msg:String) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show()
    }
    override fun onRtmpVideoStreaming() {}
    override fun onRtmpAudioStreaming() {}
    override fun onRtmpStopped() {
        Toast.makeText(getApplicationContext(), "Stopped", Toast.LENGTH_SHORT).show()
    }
    override fun onRtmpDisconnected() {
        Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show()
    }
    override fun onRtmpVideoFpsChanged(fps:Double) {
        Log.i(TAG, String.format("Output Fps: %f", fps))
    }
    override fun onRtmpVideoBitrateChanged(bitrate:Double) {
        val rate = bitrate.toInt()
        if (rate / 1000 > 0)
        {
            Log.i(TAG, String.format("Video bitrate: %f kbps", bitrate / 1000))
        }
        else
        {
            Log.i(TAG, String.format("Video bitrate: %d bps", rate))
        }
    }
    override fun onRtmpAudioBitrateChanged(bitrate:Double) {
        val rate = bitrate.toInt()
        if (rate / 1000 > 0)
        {
            Log.i(TAG, String.format("Audio bitrate: %f kbps", bitrate / 1000))
        }
        else
        {
            Log.i(TAG, String.format("Audio bitrate: %d bps", rate))
        }
    }
    override fun onRtmpSocketException(e:SocketException) {
        handleException(e)
    }
    override fun onRtmpIOException(e:IOException) {
        handleException(e)
    }
    override fun onRtmpIllegalArgumentException(e:IllegalArgumentException) {
        handleException(e)
    }
    override fun onRtmpIllegalStateException(e:IllegalStateException) {
        handleException(e)
    }
    // Implementation of SrsRecordHandler.
    override fun onRecordPause() {
        Toast.makeText(getApplicationContext(), "Record paused", Toast.LENGTH_SHORT).show()
    }
    override fun onRecordResume() {
        Toast.makeText(getApplicationContext(), "Record resumed", Toast.LENGTH_SHORT).show()
    }
    override fun onRecordStarted(msg:String) {
        Toast.makeText(getApplicationContext(), "Recording file: " + msg, Toast.LENGTH_SHORT).show()
    }
    override fun onRecordFinished(msg:String) {
        Toast.makeText(getApplicationContext(), "MP4 file saved: " + msg, Toast.LENGTH_SHORT).show()
    }
    override fun onRecordIOException(e:IOException) {
        handleException(e)
    }
    override fun onRecordIllegalArgumentException(e:IllegalArgumentException) {
        handleException(e)
    }
    // Implementation of SrsEncodeHandler.
    override fun onNetworkWeak() {
        Toast.makeText(getApplicationContext(), "Network weak", Toast.LENGTH_SHORT).show()
    }
    override fun onNetworkResume() {
        Toast.makeText(getApplicationContext(), "Network resume", Toast.LENGTH_SHORT).show()
    }
    override fun onEncodeIllegalArgumentException(e:IllegalArgumentException) {
        handleException(e)
    }
    companion object {
        private val TAG = "Yasea"
        private fun getRandomAlphaString(length:Int):String {
            val base = "abcdefghijklmnopqrstuvwxyz"
            val random = Random()
            val sb = StringBuilder()
            for (i in 0 until length)
            {
                val number = random.nextInt(base.length)
                sb.append(base.get(number))
            }
            return sb.toString()
        }
        private fun getRandomAlphaDigitString(length:Int):String {
            val base = "abcdefghijklmnopqrstuvwxyz0123456789"
            val random = Random()
            val sb = StringBuilder()
            for (i in 0 until length)
            {
                val number = random.nextInt(base.length)
                sb.append(base.get(number))
            }
            return sb.toString()
        }
    }
}