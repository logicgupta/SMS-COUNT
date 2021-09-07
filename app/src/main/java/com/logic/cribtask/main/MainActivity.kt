package com.logic.cribtask.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.logic.cribtask.R
import com.logic.cribtask.databinding.ActivityMainBinding
import com.logic.cribtask.utils.setupUI
import com.logic.cribtask.utils.showLongToast
import com.logic.cribtask.utils.showToast
import java.util.*


class MainActivity : AppCompatActivity(), MainPresenter.View {

    private lateinit var binding: ActivityMainBinding
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var mainPresenterImpl: MainPresenterImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialize the Presenter
        mainPresenterImpl = MainPresenterImpl(this, this)

        // Hide  Keyboard on click outside of editText
        setupUI(binding.root,this)

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if (granted) {
                    mainPresenterImpl.fetchSMS(
                        binding.etMobile.text.toString(),
                        binding.etDay.text.toString()
                    )
                } else {
                    showToast(getString(R.string.permission_error))
                    return@registerForActivityResult
                }
            }
        binding.etMobile.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.tvCount.isVisible)
                    binding.tvCount.visibility = View.GONE
            }
        })

        binding.etDay.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.tvCount.isVisible)
                    binding.tvCount.visibility = View.GONE
            }
        })

        binding.btnSubmit.setOnClickListener {
            checkPermissionAndGetSMS()
        }

    }

    private fun checkPermissionAndGetSMS() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_SMS
            ) == PackageManager.PERMISSION_GRANTED -> {
                mainPresenterImpl.fetchSMS(
                    binding.etMobile.text.toString(),
                    binding.etDay.text.toString()
                )
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS) -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_SMS)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_SMS)
            }
        }
    }

    override fun emptyMobileNumber(message: String) {
        showLongToast(message)
    }

    override fun emptyNumberOfDays(message: String) {
        showToast(message)
    }

    override fun invalidMobileNumber(message: String) {
        showToast(message)
    }

    override fun onSuccessSMS(message: String) {
        binding.tvCount.visibility = View.VISIBLE
        binding.tvCount.text = message
        binding.tvCount.setTextColor(getColor(R.color.success))
    }

    override fun onFailureSMS(message: String) {
        binding.tvCount.visibility = View.VISIBLE
        binding.tvCount.text = message
        binding.tvCount.setTextColor(getColor(R.color.error))
    }


}