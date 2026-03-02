package com.easyapps.config.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable.INFINITE
import com.google.android.material.button.MaterialButton
import androidx.core.graphics.toColorInt

class InformativeDialog(context: Context) : Dialog(context) {

    private var title: String? = null
    private var message: String? = null
    private var positiveButtonText: String? = null
    private var negativeButtonText: String? = null
    private var positiveButtonClickListener: (() -> Unit)? = null
    private var negativeButtonClickListener: (() -> Unit)? = null
    private var lottie: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Create CardView
        val cardView = CardView(context).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                setMargins(20.dpToPx())
            }
            radius = 15f.dpToPx()
        }

        // Create LinearLayout
        val linearLayout = LinearLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            orientation = LinearLayout.VERTICAL
            setPadding(20.dpToPx())
            gravity = Gravity.CENTER
        }
        val animationView = LottieAnimationView(context)
        // Create LottieAnimationView
        runCatching {
            animationView.layoutParams = LinearLayout.LayoutParams(220.dpToPx(), 220.dpToPx()).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                setMargins(30)
            }
            lottie?.let { animationView.setAnimationFromJson(it, message) }
            animationView.repeatCount = INFINITE
            animationView.playAnimation()
        }.onFailure {
            animationView.isVisible = false
        }

        if (lottie.isNullOrEmpty()) animationView.isVisible = false

        // Create Title TextView
        val titleTextView = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                topMargin = 10.dpToPx()
            }
            text = title
            textSize = 16f
            gravity = Gravity.CENTER
            setTypeface(typeface, Typeface.BOLD)
            isGone = title == null
        }

        // Create Message TextView
        val messageTextView = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                topMargin = 5.dpToPx()
            }
            text = message
            textSize = 12f
            gravity = Gravity.CENTER
            isGone = message == null
        }

        // Create Positive Button
        val positiveButton = MaterialButton(context).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                topMargin = 20.dpToPx()
            }
            text = positiveButtonText ?: ""
            setOnClickListener {
                positiveButtonClickListener?.invoke()
                dismiss()
            }
        }

        // Create Negative Button
        val negativeButton = MaterialButton(context).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                topMargin = 5.dpToPx()
            }
            setBackgroundColor("#0F000000".toColorInt())
            setTextColor(Color.RED)
            text = negativeButtonText ?: ""
            setOnClickListener {
                negativeButtonClickListener?.invoke()
                dismiss()
            }
        }
        if (positiveButton.text.isEmpty()) positiveButton.isVisible = false
        if (negativeButton.text.isEmpty()) negativeButton.isVisible = false



        // Add views to LinearLayout
        linearLayout.apply {
            addView(animationView)
            addView(titleTextView)
            addView(messageTextView)
            addView(positiveButton)
            addView(negativeButton)
        }

        cardView.addView(linearLayout)

        setContentView(cardView)
    }

    private fun Int.dpToPx(): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    private fun Float.dpToPx(): Float {
        return (this * context.resources.displayMetrics.density)
    }

    class Builder(context: Context) {
        private val dialog = InformativeDialog(context)

        fun setTitle(title: String): Builder {
            dialog.title = title
            return this
        }

        fun setMessage(message: String): Builder {
            dialog.message = message
            return this
        }

        fun setPositiveButton(text: String, listener: () -> Unit={}): Builder {
            dialog.positiveButtonText = text
            dialog.positiveButtonClickListener = listener
            return this
        }

        fun setCancelable(isCancelable: Boolean): Builder {
            dialog.setCancelable(isCancelable)
            return this
        }

        fun setNegativeButton(text: String, listener: () -> Unit={}): Builder {
            dialog.negativeButtonText = text
            dialog.negativeButtonClickListener = listener
            return this
        }

        fun setAnimation(anim: String?): Builder {
            if (anim.isNullOrEmpty()) return this
            dialog.lottie = anim
            return this
        }

        fun show(): InformativeDialog {
            dialog.show()
            return dialog
        }
    }
}