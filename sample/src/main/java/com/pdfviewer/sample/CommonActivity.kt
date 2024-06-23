package com.pdfviewer.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class CommonActivity<VB : ViewBinding> : AppCompatActivity() {

    val binding: VB by lazy { inflateViewBinding() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}