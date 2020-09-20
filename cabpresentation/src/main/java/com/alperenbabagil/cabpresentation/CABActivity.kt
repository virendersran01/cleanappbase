package com.alperenbabagil.cabpresentation

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.alperenbabagil.cabdomain.model.DataHolder

interface CABActivity : DialogHost{

}

fun <T : Any>CABActivity.handleDataHolderResult(dataHolder: DataHolder<T>,
                                                errorBody : (errorStr:String,errorResId:Int) -> Unit,
                                                errorButtonClick : () -> Unit,
                                                bypassErrorHandling:Boolean,
                                                bypassDisableCurrentPopupOnSuccess:Boolean,
                                                successBody : (data:T) -> Unit

){
    if(dataHolder is DataHolder.Success){
        if(!bypassDisableCurrentPopupOnSuccess) dismissCurrentDialog()
        successBody.invoke(dataHolder.data)
    }

    if(dataHolder is DataHolder.Fail){
        dismissCurrentDialog()
        if(bypassErrorHandling){
            errorBody.invoke(dataHolder.errStr,dataHolder.errorResourceId ?: -1)
        }
        else{
            showErrorDialog(
                titleRes = R.string.error,
                errorRes = dataHolder.errorResourceId?:-1,
                errorStr = dataHolder.errStr,
                positiveButtonStrRes = R.string.ok,
                positiveButtonClick = errorButtonClick
                )
        }
    }
    if(dataHolder is DataHolder.Loading){
        dismissCurrentDialog()
        showLoadingDialog(dataHolder = dataHolder)
    }
}

fun <T : Any>CABActivity.observeDataHolder(liveData: LiveData<DataHolder<T>>,
                               errorBody : (errorStr:String,errorResId:Int) -> Unit = {_,_ -> },
                               errorButtonClick : () -> Unit = {},
                               bypassErrorHandling:Boolean=false,
                               bypassDisableCurrentPopupOnSuccess:Boolean=false,
                               successBody : (data:T) -> Unit){
    liveData.observe(this as LifecycleOwner, Observer {dataHolder ->
        handleDataHolderResult(dataHolder,
            errorBody,
            errorButtonClick,
            bypassErrorHandling,
            bypassDisableCurrentPopupOnSuccess,
            successBody)
    })
}