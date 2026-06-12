package com.example.havetime.di

import com.example.calendar.domain.repository.ActivityRepository
import com.example.havetime.data.repository.ActivityRepositoryImpl
import com.example.havetime.data.repository.DateRepositoryImpl
import com.example.havetime.data.repository.RemindManagerImpl
import com.example.havetime.data.repository.UserRepositoryImpl
import com.example.havetime.domain.repository.DateRepository
import com.example.havetime.domain.repository.RemindManager
import com.example.havetime.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindActivityRepository(impl: ActivityRepositoryImpl): ActivityRepository

    @Binds
    @Singleton
    abstract fun bindDateRepository(impl: DateRepositoryImpl): DateRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindRemindManager(impl: RemindManagerImpl): RemindManager
}