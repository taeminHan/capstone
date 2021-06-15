//package com.example.capstoneproject
//
//import android.graphics.Camera
//import android.view.SurfaceHolder
//import android.view.SurfaceView
//
///** A basic Camera preview class */
//class Detection(
//    context: Context,
//    private val mCamera: Camera
//) : SurfaceView(context), SurfaceHolder.Callback {
//
//    private val mHolder: SurfaceHolder = holder.apply {
//        // Install a SurfaceHolder.Callback so we get notified when the
//        // underlying surface is created and destroyed.
//        addCallback(this@Detection)
//        // deprecated setting, but required on Android versions prior to 3.0
//        setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
//    }
//
//    override fun surfaceCreated(holder: SurfaceHolder) {
//        // The Surface has been created, now tell the camera where to draw the preview.
//        mCamera.apply {
//            try {
//                setPreviewDisplay(holder)
//                startPreview()
//            } catch (e: IOException) {
//                Log.d(TAG, "Error setting camera preview: ${e.message}")
//            }
//        }
//    }
//
//    override fun surfaceDestroyed(holder: SurfaceHolder) {
//        // empty. Take care of releasing the Camera preview in your activity.
//    }
//
//    override fun surfaceChanged(holder: SurfaceHolder, format: Int, w: Int, h: Int) {
//        // If your prseview can change or rotate, take care of those events here.
//        // Make sure to stop the preview before resizing or reformatting it.
//        if (mHolder.surface == null) {
//            // preview surface does not exist
//            return
//        }
//
//        // stop preview before making changes
//        try {
//            mCamera.stopPreview()
//        } catch (e: Exception) {
//            // ignore: tried to stop a non-existent preview
//        }
//
//        // set preview size and make any resize, rotate or
//        // reformatting changes here
//
//        // start preview with new settings
//        mCamera.apply {
//            try {
//                setPreviewDisplay(mHolder)
//                startPreview()
//            } catch (e: Exception) {
//                Log.d(TAG, "Error starting camera preview: ${e.message}")
//            }
//        }
//    }
//}