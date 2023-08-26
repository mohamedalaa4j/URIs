package com.example.uris

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.google.android.material.button.MaterialButton
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    private lateinit var iv: ImageView
    private lateinit var tv: TextView
    private lateinit var btnRes: MaterialButton
    private lateinit var btnContent: MaterialButton
    private lateinit var btnSaveFile: MaterialButton

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            iv.setImageURI(it)
            println(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        iv = findViewById(R.id.iv)
        tv = findViewById(R.id.tv)
        btnRes = findViewById(R.id.btn_use_resource_uri)
        btnContent = findViewById(R.id.btn_use_content_uri)
        btnSaveFile = findViewById(R.id.btn_save_file_uri)

        btnRes.setOnClickListener { useResourceURI() }
        btnContent.setOnClickListener { useContentURI() }
        btnSaveFile.setOnClickListener { saveFileURI() }

    }

    // _________________________________________________________________________//

    private fun useResourceURI() {
        val uri = Uri.parse("android.resource://$packageName/drawable/crunch")

        readAsByteArray(uri)?.let {
            iv.setImageBitmap(convertByteArrayToBitmap(it))
        }
    }

    private fun readAsByteArray(uri: Uri): ByteArray? {
        return contentResolver.openInputStream(uri)?.use { it.readBytes() }
    }

    private fun convertByteArrayToBitmap(image: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(image, 0, image.size)
    }

    // _________________________________________________________________________//

    private fun useContentURI() {
        pickImageLauncher.launch("image/*")
    }

    // _________________________________________________________________________//

    private fun saveFileURI() {

        // Convert imageView to byteArray
        val bitmap = iv.drawable.toBitmap()
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val imageByteArray = byteArrayOutputStream.toByteArray()

        // Save file in app's private storage
        val file = File(filesDir, "my_image.png")
        FileOutputStream(file).use { it.write(imageByteArray) }

        tv.text = "Saved to " + file.toURI()
        println(file.toURI())
    }

    // _________________________________________________________________________//
}

/*


Resource URIs
It's URIs for files exist in the project like res and raw folders
EX -> "android.resource://$packageName/drawable/crunch"

Content URIs
It's URIs for files can be get by content provider when exchange files between applications for EX pick image from gallery
EX -> "content://com.google.android.apps.photos.contentprovider/-1/1/content%3A%2F%2Fmedia%2Fexternal%2Fimages%2Fmedia%2F61/ORIGINAL/NONE/image%2Fjpeg/1749523937"


File URIs
It's URIs for files you make sure of it's location and not managed by content provider (contentResolver) or you want to deliver it to other apps
EX -> "file:/data/user/0/com.example.uris/files/my_image.png"


Data URIs
is a scheme that allows data to be encoded into a string


 */

// URI -> byteArray -> bitmap -> imageView
// imageView -> bitmap -> compress to byteArrayOutputStream -> toByteArray