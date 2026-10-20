package com.example.havetime.di

import androidx.work.WorkManager
import com.example.calendar.domain.repository.ActivityRepository
import com.example.havetime.domain.repository.DateRepository
import com.example.havetime.domain.repository.RemindManager
import com.example.havetime.domain.repository.SettingsRepository
import com.example.havetime.domain.repository.UserRepository
import com.example.havetime.domain.repository.WidgetRepository
import com.example.havetime.domain.usecase.activity.AddActivityUseCase
import com.example.havetime.domain.usecase.activity.DeleteActivityUseCase
import com.example.havetime.domain.usecase.activity.GetActivitiesForMonthUseCase
import com.example.havetime.domain.usecase.activity.GetActivitiesUseCase
import com.example.havetime.domain.usecase.activity.GetIntervalsForDateUseCase
import com.example.havetime.domain.usecase.activity.GetUpcomingActivitiesUseCase
import com.example.havetime.domain.usecase.activity.SearchUseCase
import com.example.havetime.domain.usecase.activity.UpdateActivityUseCase
import com.example.havetime.domain.usecase.auth.GetUserUseCase
import com.example.havetime.domain.usecase.auth.LoginUseCase
import com.example.havetime.domain.usecase.auth.LogoutUseCase
import com.example.havetime.domain.usecase.auth.RegisterUseCase
import com.example.havetime.domain.usecase.date.GetCurrentDateUseCase
import com.example.havetime.domain.usecase.date.GetCurrentTimeUseCase
import com.example.havetime.domain.usecase.date.GetCurrentYearUseCase
import com.example.havetime.domain.usecase.date.GetInitialDateTimeUseCase
import com.example.havetime.domain.usecase.date.GetInitialDateUseCase
import com.example.havetime.domain.usecase.date.GetNextDayUseCase
import com.example.havetime.domain.usecase.date.GetNextMonthUseCase
import com.example.havetime.domain.usecase.date.GetNextWeekUseCase
import com.example.havetime.domain.usecase.date.GetNextYearUseCase
import com.example.havetime.domain.usecase.date.GetPreviousDayUseCase
import com.example.havetime.domain.usecase.date.GetPreviousMonthUseCase
import com.example.havetime.domain.usecase.date.GetPreviousWeekUseCase
import com.example.havetime.domain.usecase.date.GetPreviousYearUseCase
import com.example.havetime.domain.usecase.settings.GetSettingsUseCase
import com.example.havetime.domain.usecase.settings.SetDynamicColorUseCase
import com.example.havetime.domain.usecase.settings.SetLanguageUseCase
import com.example.havetime.domain.usecase.settings.SetLocationUseCase
import com.example.havetime.domain.usecase.settings.SetNotificationsEnabledUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {
    // Auth Use Cases
    @Provides
    fun provideLoginUseCase(repository: UserRepository): LoginUseCase = LoginUseCase(repository)

    @Provides
    fun provideRegisterUseCase(repository: UserRepository): RegisterUseCase =
        RegisterUseCase(repository)

    @Provides
    fun provideLogoutUseCase(repository: UserRepository): LogoutUseCase = LogoutUseCase(repository)

    @Provides
    fun provideGetUserUseCase(repository: UserRepository): GetUserUseCase =
        GetUserUseCase(repository)

    // Activity Use Cases
    @Provides
    fun provideAddActivityUseCase(
        repository: ActivityRepository,
        remindManager: RemindManager,
        worker: WorkManager,
        widgetRepository: WidgetRepository
    ): AddActivityUseCase = AddActivityUseCase(repository, remindManager, worker, widgetRepository)

    @Provides
    fun provideDeleteActivityUseCase(
        repository: ActivityRepository,
        remindManager: RemindManager,
        widgetRepository: WidgetRepository
    ): DeleteActivityUseCase = DeleteActivityUseCase(repository, remindManager, widgetRepository)

    @Provides
    fun provideUpdateActivityUseCase(
        repository: ActivityRepository,
        remindManager: RemindManager,
        worker: WorkManager,
        widgetRepository: WidgetRepository
    ): UpdateActivityUseCase =
        UpdateActivityUseCase(repository, remindManager, worker, widgetRepository)

    @Provides
    fun provideGetActivitiesUseCase(repository: ActivityRepository): GetActivitiesUseCase =
        GetActivitiesUseCase(repository)

    @Provides
    fun provideGetIntervalsForDateUseCase(
        repository: ActivityRepository
    ): GetIntervalsForDateUseCase = GetIntervalsForDateUseCase(repository)

    @Provides
    fun provideSearchUseCase(repository: ActivityRepository): SearchUseCase =
        SearchUseCase(repository)

    @Provides
    fun provideGetActivitiesForMonthUseCase(
        repository: ActivityRepository
    ): GetActivitiesForMonthUseCase = GetActivitiesForMonthUseCase(repository)

    // Date Use Cases
    @Provides
    fun provideGetCurrentDateUseCase(repository: DateRepository): GetCurrentDateUseCase =
        GetCurrentDateUseCase(repository)

    @Provides
    fun provideGetCurrentTimeUseCase(repository: DateRepository): GetCurrentTimeUseCase =
        GetCurrentTimeUseCase(repository)

    @Provides
    fun provideGetCurrentYearUseCase(repository: DateRepository): GetCurrentYearUseCase =
        GetCurrentYearUseCase(repository)

    @Provides
    fun provideGetNextDayUseCase(repository: DateRepository): GetNextDayUseCase =
        GetNextDayUseCase(repository)

    @Provides
    fun provideGetNextWeekUseCase(repository: DateRepository): GetNextWeekUseCase =
        GetNextWeekUseCase(repository)

    @Provides
    fun provideGetNextMonthUseCase(repository: DateRepository): GetNextMonthUseCase =
        GetNextMonthUseCase(repository)

    @Provides
    fun provideGetNextYearUseCase(repository: DateRepository): GetNextYearUseCase =
        GetNextYearUseCase(repository)

    @Provides
    fun provideGetPreviousDayUseCase(repository: DateRepository): GetPreviousDayUseCase =
        GetPreviousDayUseCase(repository)

    @Provides
    fun provideGetPreviousWeekUseCase(repository: DateRepository): GetPreviousWeekUseCase =
        GetPreviousWeekUseCase(repository)

    @Provides
    fun provideGetPreviousMonthUseCase(repository: DateRepository): GetPreviousMonthUseCase =
        GetPreviousMonthUseCase(repository)

    @Provides
    fun provideGetPreviousYearUseCase(repository: DateRepository): GetPreviousYearUseCase =
        GetPreviousYearUseCase(repository)

    @Provides
    fun getInitialDateUseCase(repository: DateRepository): GetInitialDateUseCase =
        GetInitialDateUseCase(repository)

    @Provides
    fun getInitialDateTimeUseCase(repository: DateRepository): GetInitialDateTimeUseCase =
        GetInitialDateTimeUseCase(repository)

    @Provides
    fun getUpcomingActivitiesUseCase(repository: ActivityRepository): GetUpcomingActivitiesUseCase =
        GetUpcomingActivitiesUseCase(repository)

    @Provides
    fun setLanguageUseCase(repository: SettingsRepository): SetLanguageUseCase =
        SetLanguageUseCase(repository)

    @Provides
    fun setLocationUseCase(repository: SettingsRepository): SetLocationUseCase =
        SetLocationUseCase(repository)

    @Provides
    fun getSettingsUseCase(repository: SettingsRepository): GetSettingsUseCase =
        GetSettingsUseCase(repository)

    @Provides
    fun setNotificationsEnabledUseCase(
        repository: SettingsRepository
    ): SetNotificationsEnabledUseCase = SetNotificationsEnabledUseCase(repository)

    @Provides
    fun setDynaUseCase(repository: SettingsRepository): SetDynamicColorUseCase =
        SetDynamicColorUseCase(repository)
}
