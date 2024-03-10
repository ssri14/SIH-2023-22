package com.sih.getrect

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private lateinit var image: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        image = findViewById(R.id.imageView)
//        image.alpha = 0f
//        image.animate().setDuration(1000).alpha(1f).withEndAction {
            val intent = Intent(this, imgDet::class.java)
            startActivity(intent)
//            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
//        }
        if (getSupportActionBar() != null) {
            getSupportActionBar()?.hide();
        }

    }

}