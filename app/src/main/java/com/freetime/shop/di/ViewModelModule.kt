package com.freetime.shop.di

import com.freetime.shop.ui.feature.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val ViewModelModule = module {
    viewModel {
        HomeViewModel(get())
    }
}