package net.store.divineit.application

import android.app.Application
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.HiltAndroidApp
import net.store.divineit.BuildConfig
import net.store.divineit.binding.FragmentDataBindingComponent
import timber.log.Timber

@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        DataBindingUtil.setDefaultComponent(FragmentDataBindingComponent())
    }
}