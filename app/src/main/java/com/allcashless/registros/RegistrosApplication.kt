package com.allcashless.registros

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.allcashless.recargasallcashless.utils.SessionManager
import com.allcashless.registros.viewmodel.GlobalDataViewModel

class RegistrosApplication: Application(), ViewModelStoreOwner {
    private val appViewModelStore: ViewModelStore by lazy { ViewModelStore() }

    val globalDataViewModel: GlobalDataViewModel by lazy {
        ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this))
            .get(GlobalDataViewModel::class.java)
    }

    override fun onCreate() {
        super.onCreate()
        SessionManager.init(this)
    }

    override val viewModelStore: ViewModelStore
        get() = appViewModelStore
}

