package com.zmy.permission_request

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.delay

class PermissionRequest(private val activity: FragmentActivity) {

    constructor(fragment: Fragment) : this(fragment.activity!!)

    suspend fun requestAsync(vararg permissions: String): Deferred<List<String>> {
        val deferred = CompletableDeferred<List<String>>()
        for (permission in permissions) {
            val ret = ContextCompat.checkSelfPermission(activity, permission)
            if (ret != PackageManager.PERMISSION_GRANTED) {
                val fragment = TempFragment(permissions.toList(), deferred)
                activity.supportFragmentManager.beginTransaction()
                    .add(fragment, "TempFragment")
                    .commit()
                return deferred
            }
        }
        deferred.complete(permissions.asList())
        return deferred
    }

}

class TempFragment(
    private val permissions: List<String>,
    private val deferred: CompletableDeferred<List<String>>
) : Fragment() {
    private val requestCode=deferred.hashCode().shr(16)
    override fun onAttach(context: Context) {
        super.onAttach(context)
        requestPermissions(permissions.toTypedArray(), requestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCode) {
            deferred.complete(permissions.filterIndexed { index, _ ->
                grantResults[index] == PackageManager.PERMISSION_GRANTED
            })
        }
    }
}
