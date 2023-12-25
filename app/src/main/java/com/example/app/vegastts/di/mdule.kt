package com.example.app.testtask.di

import android.content.Context
import com.rhonda.app.vegasrhonda.permissions.PermissionsApi
import com.rhonda.app.vegasrhonda.permissions.PermissionsImpl
import com.rhonda.data.repository.api.GlobalSettingsRepositoryApi
import com.rhonda.data.repository.impl.GlobalSettingsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object PermissionsModule {
    @Provides
    fun providePermissionsApi(@ApplicationContext appContext: Context): PermissionsApi {

        return PermissionsImpl(appContext)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object GlobalSettingsRepositoryModule {
    @Provides
    fun provideGlobalSettingsRepositoryApi(@ApplicationContext appContext: Context): GlobalSettingsRepositoryApi {

        return GlobalSettingsRepositoryImpl(appContext)
    }
}
