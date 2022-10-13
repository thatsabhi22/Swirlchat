package com.theleafapps.pro.swirlchat.util

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.ViewGroup
import android.widget.ProgressBar
import com.theleafapps.pro.swirlchat.R
import java.io.Serializable


class MyProgressDialog(context: Context?) : Dialog(context!!, R.style.NewDialog),
    Serializable {
    companion object {
        fun show(
            context: Context?, progressDialog: MyProgressDialog, title: CharSequence?,
            message: CharSequence?
        ): MyProgressDialog {
            return show(context, progressDialog, title, message, false)
        }

        private fun show(
            context: Context?, progressDialog: MyProgressDialog, title: CharSequence?,
            message: CharSequence?, indeterminate: Boolean
        ): MyProgressDialog {
            return show(context, progressDialog, title, message, indeterminate, false, null)
        }

        fun show(
            context: Context?, progressDialog: MyProgressDialog, title: CharSequence?,
            message: CharSequence?, indeterminate: Boolean, cancelable: Boolean, o: Any?
        ): MyProgressDialog {
            return show(context, progressDialog, title, message, indeterminate, cancelable, null)
        }

        private fun show(
            context: Context?, dialog: MyProgressDialog, title: CharSequence?,
            message: CharSequence?, indeterminate: Boolean,
            cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener?
        ): MyProgressDialog {
            dialog.setTitle(title)
            dialog.setCancelable(cancelable)
            dialog.setOnCancelListener(cancelListener)
            /* The next line will add the ProgressBar to the dialog. */dialog.addContentView(
                ProgressBar(context), ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
            dialog.show()
            return dialog
        }

        fun dismissDialog() {}
    }
}