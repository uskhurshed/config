package com.easyapps.config.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible


class LoadingDialog(context: Context,isCloseable:Boolean = false, onFinish: () -> Unit = {}) : Dialog(context) {


    init {
        val cardView = CardView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            radius = 12 * context.resources.displayMetrics.density
            cardElevation = 0f
        }

        val frameLayout = FrameLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        val progressBar = ProgressBar(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                (30 * context.resources.displayMetrics.density).toInt(),
                (30 * context.resources.displayMetrics.density).toInt()
            ).apply {
                setMargins(
                    (25 * context.resources.displayMetrics.density).toInt(),
                    (25 * context.resources.displayMetrics.density).toInt(),
                    (25 * context.resources.displayMetrics.density).toInt(),
                    (25 * context.resources.displayMetrics.density).toInt()
                )
                gravity = Gravity.CENTER
            }
        }

        val closeView = object : View(context) {
            private val paint = Paint().apply {
                color = Color.BLACK
                strokeWidth = 5f
                style = Paint.Style.STROKE
                isAntiAlias = true
            }

            override fun onDraw(canvas: Canvas) {
                super.onDraw(canvas)
                val padding = 10 * context.resources.displayMetrics.density
                val size = (24 * context.resources.displayMetrics.density).toInt()
                val right = size - padding
                val bottom = size - padding
                canvas.drawLine(padding, padding, right, bottom, paint)
                canvas.drawLine(padding, bottom, right, padding, paint)
            }
        }.apply {
            layoutParams = FrameLayout.LayoutParams(
                (24 * context.resources.displayMetrics.density).toInt(),
                (24 * context.resources.displayMetrics.density).toInt()
            ).apply {
                gravity = Gravity.CENTER
            }
            setOnClickListener {
                dismiss()
                onFinish.invoke()
            }
        }

        frameLayout.addView(progressBar)
        frameLayout.addView(closeView)

        cardView.addView(frameLayout)

        setContentView(cardView)
        setCancelable(false)
        closeView.isVisible = isCloseable

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        window?.setGravity(Gravity.CENTER)
    }
}
