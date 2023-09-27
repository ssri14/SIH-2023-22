package com.sih.getrect

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.Image
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sih.getrect.ml.GetRectModel
import com.sih.getrect.ml.MobilenetV110224Quant

import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import kotlin.math.roundToInt
import kotlin.math.roundToLong


class imgDet : AppCompatActivity() {
    private lateinit var bitmap: Bitmap
    private lateinit var imgBtn: Button
    private lateinit var camBtn: Button
    private lateinit var imgShow: ImageView
    private lateinit var predBtn: Button
    private lateinit var predTxt: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_img_det)

        if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 12)

        imgBtn = findViewById(R.id.selImg)
        imgShow = findViewById(R.id.imgShow)
        predBtn = findViewById(R.id.predImg)
        predTxt = findViewById(R.id.predictedText)
        camBtn = findViewById(R.id.camImg)

        val labels = application.assets.open("labels.txt").bufferedReader().readLines()
        val GetRectlabels = application.assets.open("GetRectLabels.txt").bufferedReader().readLines()

        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224,224,ResizeOp.ResizeMethod.BILINEAR))
            .build()



        imgBtn.setOnClickListener{
            val imgIntent = Intent(Intent.ACTION_GET_CONTENT)
            imgIntent.type = "image/*"
            imgIntent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(imgIntent, 13)
        }
        camBtn.setOnClickListener {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, 12)

            }

        predBtn.setOnClickListener {

            var tensorImage = TensorImage(DataType.FLOAT32)
            tensorImage.load(bitmap)

            tensorImage = imageProcessor.process(tensorImage)


            // MobileNet
//
//            val model = MobilenetV110224Quant.newInstance(this)
//
//// Creates inputs for reference.
//            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.UINT8)
//            inputFeature0.loadBuffer(tensorImage.buffer)
//
//// Runs model inference and gets result.
//            val outputs = model.process(inputFeature0)
//            val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray
//
//            var maxIdx = 0
//            outputFeature0.forEachIndexed{ index, fl ->
//                if(outputFeature0[maxIdx] < fl){
//                    maxIdx = index
//                }
//
//            }
//            val pred = "The above object is: " + labels[maxIdx]
//            predTxt.setText(pred)
//
//// Releases model resources if no longer used.
//            model.close()

// Getrect
            val model = GetRectModel.newInstance(this)

// Creates inputs for reference.
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
            inputFeature0.loadBuffer(tensorImage.buffer)

// Runs model inference and gets result.
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

            var maxIdx = 0
            var percentage = 0
            outputFeature0.forEachIndexed{ index, fl ->
                if(outputFeature0[maxIdx] < fl){
                    maxIdx = index
//                    percentage = (fl * 100).toInt()

                }

            }
            val pred = "The above object is: " + GetRectlabels[maxIdx]
            predTxt.setText(pred)


// Releases model resources if no longer used.
            model.close()


        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 13){
            var uri = data?.data
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,uri)
            imgShow.setImageBitmap(bitmap)
        }
        if (requestCode == 12) {
            bitmap = data?.extras?.get("data") as Bitmap
            imgShow.setImageBitmap(bitmap)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            }

        }

    }



}