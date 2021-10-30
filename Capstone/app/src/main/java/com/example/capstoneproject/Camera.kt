package com.example.capstoneproject

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.net.Uri
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.util.SparseIntArray
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.exifinterface.media.ExifInterface
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import splitties.toast.toast
import splitties.views.InputType.Companion.uri
import java.io.*
import java.net.URL


class Camera : AppCompatActivity() {
    private lateinit var mSurfaceViewHolder: SurfaceHolder
    private lateinit var mImageReader: ImageReader
    private lateinit var mCameraDevice: CameraDevice
    private lateinit var mPreviewBuilder: CaptureRequest.Builder
    private lateinit var mSession: CameraCaptureSession

    private var mHandler: Handler? = null

    private lateinit var mAccelerometer: Sensor
    private lateinit var mMagnetometer: Sensor
    private lateinit var mSensorManager: SensorManager
    private lateinit var photoFile: File

    private val deviceOrientation = ORIENTATIONS
    private var mHeight: Int = 0
    private var mWidth: Int = 0

    var mCameraId = CAMERA_BACK

    companion object {
        var cameracheck: Int = 0
        const val CAMERA_BACK = "0"
        const val CAMERA_FRONT = "1"

        private val ORIENTATIONS = SparseIntArray(4)

        init {
            ORIENTATIONS.append(Surface.ROTATION_0, 90)
            ORIENTATIONS.append(Surface.ROTATION_90, 0)
            ORIENTATIONS.append(Surface.ROTATION_180, 270)
            ORIENTATIONS.append(Surface.ROTATION_270, 180)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 상태바 숨기기
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // 화면 켜짐 유지
        window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        setContentView(R.layout.main)
        initSensor()
        initView()
    }

    private fun initSensor() {
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    private fun initView() {
        with(DisplayMetrics()) {
            windowManager.defaultDisplay.getMetrics(this)
            mHeight = heightPixels
            mWidth = widthPixels
        }

        mSurfaceViewHolder = surfaceView.holder
        mSurfaceViewHolder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                initCameraAndPreview()
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                mCameraDevice.close()
            }

            override fun surfaceChanged(
                holder: SurfaceHolder, format: Int,
                width: Int, height: Int
            ) {

            }
        })

        btn_convert.setOnClickListener { takepicture() }
    }


    fun initCameraAndPreview() {
        val handlerThread = HandlerThread("CAMERA2")
        handlerThread.start()
        mHandler = Handler(handlerThread.looper)

        openCamera()
    }

    private fun openCamera() {
        try {
            val mCameraManager = this.getSystemService(CAMERA_SERVICE) as CameraManager
            val characteristics = mCameraManager.getCameraCharacteristics(mCameraId)
            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

            val largestPreviewSize = map!!.getOutputSizes(ImageFormat.JPEG)[0]
            setAspectRatioTextureView(largestPreviewSize.height, largestPreviewSize.width)

            mImageReader = ImageReader.newInstance(
                largestPreviewSize.width,
                largestPreviewSize.height,
                ImageFormat.JPEG,
                7
            )
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) return

            mCameraManager.openCamera(mCameraId, deviceStateCallback, mHandler)
        } catch (e: CameraAccessException) {
            toast("카메라를 열지 못했습니다.")
        }
    }


    private val deviceStateCallback = object : CameraDevice.StateCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        override fun onOpened(camera: CameraDevice) {
            mCameraDevice = camera
            try {
                takePreview()
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }

        override fun onDisconnected(camera: CameraDevice) {
            mCameraDevice.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            toast("카메라를 열지 못했습니다.")
        }
    }

    @Throws(CameraAccessException::class)
    fun takePreview() {
        mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        mPreviewBuilder.addTarget(mSurfaceViewHolder.surface)
        mCameraDevice.createCaptureSession(
            listOf(mSurfaceViewHolder.surface, mImageReader.surface),
            mSessionPreviewStateCallback,
            mHandler
        )
    }

    private val mSessionPreviewStateCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigured(session: CameraCaptureSession) {
            mSession = session
            try {
                // Key-Value 구조로 설정
                // 오토포커싱이 계속 동작
                mPreviewBuilder.set(
                    CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                )
                //필요할 경우 플래시가 자동으로 켜짐
                mPreviewBuilder.set(
                    CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
                )
                mSession.setRepeatingRequest(mPreviewBuilder.build(), null, mHandler)
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }

        }

        override fun onConfigureFailed(session: CameraCaptureSession) {
            Toast.makeText(this@Camera, "카메라 구성 실패", Toast.LENGTH_SHORT).show()
        }
    }



    private fun setAspectRatioTextureView(ResolutionWidth: Int, ResolutionHeight: Int) {
        if (ResolutionWidth > ResolutionHeight) {
            val newWidth = mWidth
            val newHeight = mWidth * ResolutionWidth / ResolutionHeight
            updateTextureViewSize(newWidth, newHeight)

        } else {
            val newWidth = mWidth
            val newHeight = mWidth * ResolutionHeight / ResolutionWidth
            updateTextureViewSize(newWidth, newHeight)
        }

    }

    private fun updateTextureViewSize(viewWidth: Int, viewHeight: Int) {
        Log.d("ViewSize", "TextureView Width : $viewWidth TextureView Height : $viewHeight")
        surfaceView.layoutParams = FrameLayout.LayoutParams(viewWidth, viewHeight)
    }

    private fun takepicture() {
        try {
            val manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val characteristics = manager.getCameraCharacteristics(mCameraDevice!!.id)
            var jpegSizes: Array<Size>? = null
            jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
                .getOutputSizes(ImageFormat.JPEG)

            var width = jpegSizes[0].width
            var height = jpegSizes[0].height

            var imageReader = mImageReader

            val outputSurface = ArrayList<Surface>(2)
            outputSurface.add(imageReader!!.surface)


            val captureBuilder =
                mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            captureBuilder.addTarget(imageReader!!.surface)

            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)

            // 사진의 rotation 을 설정해준다
            val rotation = windowManager.defaultDisplay.rotation
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation))


            val dir = externalCacheDir
            var file = File.createTempFile("photo_", ".jpg", dir)
            val readerListener = object : ImageReader.OnImageAvailableListener {
                override fun onImageAvailable(reader: ImageReader?) {
                    var image: Image? = null

                    try {
                        image = imageReader!!.acquireLatestImage()

                        val buffer = image!!.planes[0].buffer
                        val bytes = ByteArray(buffer.capacity())
                        buffer.get(bytes)

                        var output: OutputStream? = null
                        try {
                            output = FileOutputStream(file)
                            output.write(bytes)
                        } finally {
                            output?.close()
                        }
                        var uri = Uri.fromFile(file)
                        var bitmap: Bitmap = BitmapFactory.decodeFile(file.path)


                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        image?.close()
                    }
                }

            }

            // imageReader 객체에 위에서 만든 readerListener 를 달아서, 이미지가 사용가능하면 사진을 저장한다
            imageReader!!.setOnImageAvailableListener(readerListener, null)
            photoFile = file
            val captureListener = object : CameraCaptureSession.CaptureCallback() {
                override fun onCaptureCompleted(
                    session: CameraCaptureSession,
                    request: CaptureRequest,
                    result: TotalCaptureResult
                ) {
                    super.onCaptureCompleted(session, request, result)
                    Toast.makeText(this@Camera, "사진이 촬영되었습니다", Toast.LENGTH_SHORT).show()
                    when(cameracheck){
                        1 -> img_networking("http:/121.162.15.236:80/imgInformation", photoFile)
                        2 -> img_search_networking("http:/121.162.15.236:80/imgSearch", photoFile)
                    }
                    initCameraAndPreview()
                }
            }

            // outputSurface 에 위에서 만든 captureListener 를 달아, 캡쳐(사진 찍기) 해주고 나서 카메라 미리보기 세션을 재시작한다
            mCameraDevice!!.createCaptureSession(
                outputSurface,
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigureFailed(session: CameraCaptureSession) {}

                    override fun onConfigured(session: CameraCaptureSession) {
                        try {
                            session.capture(captureBuilder.build(), captureListener, null)
                        } catch (e: CameraAccessException) {
                            e.printStackTrace()
                        }
                    }

                },
                null
            )


        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun img_search_networking(urlString: String, file: File) {
        // URL을 만들어 주고
        val url = URL(urlString)
        //데이터를 담아 보낼 바디를 만든다
//        val requestBody : RequestBody = FormBody.Builder()
//            .add("id","아이디")
//            .build()
        // OkHttp Request 를 만들어준다.
        val requestBody: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                "photo",
                "photo.png",
                RequestBody.create("image/jpg".toMediaTypeOrNull(), file)
            )
            .build()
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
        // 클라이언트 생성
        val client = OkHttpClient()

        Log.d(TAG, "전송 주소 $urlString")
        // 요청 전송
        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {
                Log.d(TAG, "요청 완료")
                Log.d(TAG, "파일 이름: ${photoFile.name}")

//                val result: String = Gson().toJson(response.body()!!.string())
//                Log.d("JSON", result)
                val resStr = response.body!!.string()
                val json = JSONObject(resStr)

                val obj = json.getString("name")
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(applicationContext, obj, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG, "서버 요청 실패 ")
            }
        })
    }
    private fun img_networking(urlString: String, file: File) {
        // URL을 만들어 주고
        val url = URL(urlString)

        //데이터를 담아 보낼 바디를 만든다
//        val requestBody : RequestBody = FormBody.Builder()
//            .add("id","아이디")
//            .build()
        // OkHttp Request 를 만들어준다.
        val requestBody: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                "photo",
                "photo.png",
                RequestBody.create("image/jpg".toMediaTypeOrNull(), file)
            )
            .build()
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
        // 클라이언트 생성
        val client = OkHttpClient()

        Log.d(TAG, "전송 주소 $urlString")
        // 요청 전송
        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {
                Log.d(TAG, "요청 완료")
                Log.d(TAG, "파일 이름: ${photoFile.name}")

//                val result: String = Gson().toJson(response.body()!!.string())
//                Log.d("JSON", result)
                val resStr = response.body!!.string()
                val json = JSONObject(resStr)

                val obj = json.getString("object")
                val price = json.getString("price")
                val facts = json.getString("nutrition_facts")
                val event = json.getString("event")
                Handler(Looper.getMainLooper()).post{
                    Toast.makeText(applicationContext, obj,Toast.LENGTH_SHORT).show()
                    Toast.makeText(applicationContext, price,Toast.LENGTH_SHORT).show()
                    Toast.makeText(applicationContext, facts,Toast.LENGTH_SHORT).show()
                    Toast.makeText(applicationContext, event,Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG,"서버 요청 실패 ")
            }
        })

    }
}



