package com.zmy.deferred_permission.demo

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.zmy.permission_request.PermissionRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.lang.StringBuilder

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private lateinit var permission_tips:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        permission_tips=findViewById(R.id.permission_tips)
        launch {
            val ret = PermissionRequest(this@MainActivity)
                .requestAsync(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
                .await()

            if (ret.isEmpty()) {
                permission_tips.text="NO PERMISSIONS WERE GRANTED"
            } else {
                val builder=StringBuilder()
                builder.append("granted permission:\n")
                for (s in ret) {
                    builder.append(s)
                    builder.append("\n")
                }
                permission_tips.text=builder.toString()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}