package com.alperenbabagil.cabpresentation

import android.app.Activity
import android.app.Dialog
import com.alperenbabagil.cabdomain.model.DataHolder
import com.alperenbabagil.simpleanimationpopuplibrary.SapActivity
import com.alperenbabagil.simpleanimationpopuplibrary.SapDialog

interface DialogHolderActivity : SapActivity {

    override var currentDialog : Dialog?

    var isFullScreenNow : Boolean

    fun loadingDialogDismissed()

    val cabViewModel : CABViewModel?
}

fun DialogHolderActivity.showDHLoadingDialog(forceCancellable:Boolean=false,
                                             animRes: Int?=null,
                                             dataHolder: DataHolder.Loading?=null,
                                             cancelledCallback: () -> Unit ={}
){
    (this as? Activity)?.let {
        currentDialog?.dismiss()
        currentDialog= SapDialog(it).apply {
            isFullScreen=isFullScreenNow
            isOnlyAnimation=true
            animRes?.let { this.animResource = it }
            isCancellable=dataHolder?.cancellable ?:false
            if(forceCancellable) isCancellable=true
            if(isCancellable){
                addCancelEvent {
                    loadingDialogDismissed()
                    dataHolder?.let { dataHolderLoading->
                        cabViewModel?.loadingCancelled(dataHolderLoading.tag)
                    }
                    cancelledCallback.invoke()
                }
            }
        }.build().show()
    }
}


