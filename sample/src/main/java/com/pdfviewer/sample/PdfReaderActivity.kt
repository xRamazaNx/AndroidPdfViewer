package com.pdfviewer.sample

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.app.ShareCompat
import androidx.core.content.edit
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.pdfviewer.sample.databinding.ActivityPdfBinding
import java.io.FileNotFoundException


class PdfReaderActivity : CommonActivity<ActivityPdfBinding>() {

    companion object {
        private const val ASSETS_FILE_NAME = "kotlin-reference.pdf"
    }

    private val scrollHandle by lazy {
        DefaultScrollHandle(this, false)
    }

    private val prefProgress by lazy {
        getSharedPreferences("pdf_progress", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        title = ASSETS_FILE_NAME

        binding.pdfView.setSelectionPaintView(binding.selectionView)

        loadPDF()

        onBackPressedDispatcher.addCallback {

            if (selectedText().isNotBlank()) {
                binding.pdfView.clearSelection()
                return@addCallback
            }

            finish()
        }
    }

    private fun loadPDF() {

        try {

            val pdfView = binding.pdfView

            val progressPage = prefProgress.getInt(ASSETS_FILE_NAME, 0)

            pdfView.fromAsset(ASSETS_FILE_NAME)
                .defaultPage(progressPage)
                .spacing(8)
                .pageSnap(true)
                .pageFling(true)
                .autoSpacing(true)
                .fitEachPage(true)
                .swipeHorizontal(false)
                .scrollHandle(scrollHandle)
                .onPageChange { page, _ ->
                    prefProgress.edit { putInt(ASSETS_FILE_NAME, page) }
                }
                .load()

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

    }

    private fun selectedText() = binding.pdfView.selection

    private fun shareSelectedText() {

        val selection = selectedText().ifBlank {
            toastNoFound()
            return
        }

        ShareCompat.IntentBuilder(this)
            .setType("text/plain")
            .setText(selection)
            .setChooserTitle("Send selected text")
            .startChooser()
    }

    private fun copySelectedText() {

        val selection = selectedText().ifBlank {
            toastNoFound()
            return
        }

        getSystemService(ClipboardManager::class.java)?.setPrimaryClip(
            ClipData.newPlainText(
                "Selected text from $title",
                selection
            )
        )
    }

    private fun toastNoFound() {
        Toast.makeText(
            this,
            "Selected text not found",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_pdf_viewer, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.copySelectedText -> copySelectedText()
            R.id.shareSelectedText -> shareSelectedText()
            android.R.id.home -> onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

}