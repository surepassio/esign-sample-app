package io.surepass.esignsampleapp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import io.surepass.esign.ui.activity.InitSDK
import io.surepass.esignsampleapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var eSignActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var response: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerActivityForResult()

        binding.btnGetStarted.setOnClickListener {
            val token = "YOUR TOKEN"
            val env = "PREPROD"
            openActivity(env, token)
        }

        // to copy the entire response received from sdk
        binding.btnCopyButton.setOnClickListener {
            if (response.isNotEmpty()) {
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Response", response)
                clipboard.setPrimaryClip(clip)
                showToast("Response Copied...")
            }
        }
    }


    private fun openActivity(env: String, token: String) {
        eSignActivityResultLauncher.launch(InitSDK.newIntent(this, token, env))
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun registerActivityForResult() {
        eSignActivityResultLauncher =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
                ActivityResultCallback { result ->
                    val resultCode = result.resultCode
                    val data = result.data
                    if (resultCode == RESULT_OK && data != null) {
                        val eSignResponse = data.getStringExtra(InitSDK.EXTRA_SIGNED_RESPONSE)
                        Log.e("MainActivity", "eSign Response $eSignResponse")
                        showResponse(eSignResponse)
                    }
                })
    }

    private fun showResponse(eSignResponse: String?) {
        binding.etResponse.visibility = View.VISIBLE
        binding.btnCopyButton.visibility = View.VISIBLE
        binding.etResponse.setText(eSignResponse.toString())
        response = eSignResponse.toString()
    }
}