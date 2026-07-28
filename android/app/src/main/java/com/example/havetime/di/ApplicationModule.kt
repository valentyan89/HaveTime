package com.example.havetime.di

import android.app.AlarmManager
import android.content.Context
import android.preference.PreferenceManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.work.WorkManager
import com.example.calendar.domain.repository.ActivityRepository
import com.example.havetime.data.local.ActivityDataBase
import com.example.havetime.data.local.dao.TodoDao
import com.example.havetime.data.local.dao.UserDao
import com.example.havetime.data.repository.GeocodingRepositoryImpl
import com.example.havetime.domain.repository.GeocodingRepository
import com.example.havetime.domain.usecase.GetAndSaveAddressUseCase
import com.example.havetime.domain.usecase.activity.SyncWithServerUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.osmdroid.bonuspack.location.GeocoderNominatim
import org.osmdroid.config.Configuration
import org.osmdroid.config.IConfigurationProvider
import javax.inject.Named
import javax.inject.Singleton

private val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_tokens")

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {
    @Provides
    @Singleton
    fun provideRoom(@ApplicationContext context: Context): ActivityDataBase =
        Room.databaseBuilder(
            context.applicationContext,
            ActivityDataBase::class.java,
            "activity.db"
        )
            .fallbackToDestructiveMigration()
            // .addMigrations(...)
            // .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            // .enableMultiInstanceInvalidation()
            .build()

    @Provides
    fun provideTodoDao(db: ActivityDataBase): TodoDao = db.todoDao()

    @Provides
    fun provideUserDao(db: ActivityDataBase): UserDao = db.userDao()

    @Provides
    @Singleton
    @Named("auth_store")
    fun provideTokenDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.tokenDataStore

    @Provides
    @Singleton
    fun provideAlarmManager(@ApplicationContext context: Context): AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    @Provides
    @Singleton
    fun provideOsmdroidConfiguration(@ApplicationContext context: Context): IConfigurationProvider {
        val osmConfig = Configuration.getInstance()
        val sharedPrefs = context.getSharedPreferences("osmdroid_pref", Context.MODE_PRIVATE)
        osmConfig.load(context, sharedPrefs)

        osmConfig.userAgentValue = "HaveTimeCalendarApp/1.0 (Android; contact: dreminvalentin32@gmail.com)"

        return osmConfig
    }

    @Provides
    @Singleton
    fun provideSyncWithServerUseCase(repository: ActivityRepository): SyncWithServerUseCase =
        SyncWithServerUseCase(repository)

    @Provides
    @Singleton
    fun provideOsmGeocoder(@ApplicationContext context: Context): GeocoderNominatim =
        GeocoderNominatim("HaveTimeAndroidCalendarApp/1.0")

    @Provides
    @Singleton
    fun provideGetAndSaveAddressUseCase(repository: ActivityRepository, geocodingRepository: GeocodingRepository): GetAndSaveAddressUseCase =
        GetAndSaveAddressUseCase(repository, geocodingRepository)

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
        WorkManager.getInstance(context)
}