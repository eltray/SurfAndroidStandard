package ru.surfstudio.android.imageloader_sample

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import org.jetbrains.anko.find
import ru.surfstudio.android.imageloader.ImageLoader

class MainActivity : AppCompatActivity() {

    private val IMAGE_URL = "https://cdn-images-1.medium.com/max/2000/1*dT8VX9g8ig6lxmobTRmCiA.jpeg"

    private lateinit var imageView: ImageView
    private lateinit var transformButton: Button

    private lateinit var svgIv: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = find(R.id.imageloader_sample_iv)
        transformButton = find(R.id.image_loader_sample_btn)

        var isLoadOriginal = false

        transformButton.setOnClickListener {
            if (isLoadOriginal) loadOriginalImage() else loadTransformedImage()
            isLoadOriginal = !isLoadOriginal
        }

        loadOriginalImage()

        svgIv = find(R.id.imageloader_sample_svg_iv)
        val svgImageUrl = "https://card2card.zenit.ru/assets/images/banks/yandex.svg"
        loadSvgImage(svgImageUrl)
    }

    private fun loadOriginalImage() {
        ImageLoader
                .with(this)
                .crossFade(500)
                .centerCrop()
                .url(IMAGE_URL)
                .preview(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(imageView)
    }

    private fun loadTransformedImage() {
        ImageLoader
                .with(this)
                .crossFade(500)
                .centerCrop()
                .blur(blurDownSampling = 4)
                .url(IMAGE_URL)
                .signature(Math.random()) // картинка будет грузиться при каждом тапе
                .error(R.drawable.ic_launcher_background)
                .into(imageView)
    }

    private fun loadSvgImage(svgImageUrl: String) {
        ImageLoader
                .with(this)
                .skipCache(true) //для svg недоступен кэш
                .centerCrop()
                .url(svgImageUrl)
                .error(R.drawable.ic_launcher_background)
                .into(svgIv)
    }
}