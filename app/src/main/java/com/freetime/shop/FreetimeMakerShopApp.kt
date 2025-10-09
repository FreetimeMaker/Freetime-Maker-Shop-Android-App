package com.freetime.shop

import android.app.Application
import com.freetime.domain.di.DomainModule
import com.freetime.shop.di.AppModule
import com.freetime.data.di.DataModule
import org.koin.core.context.GlobalContext.startKoin
import org.koin.android.ext.koin.androidContext

class FreetimeMakerShopApp : Application() {


    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@FreetimeMakerShopApp)
            modules(listOf(
                AppModule,
                DomainModule,
                DataModule
            ))
        }
    }
}