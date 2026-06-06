package com.example.havetime.data.repository

import com.example.havetime.domain.repository.DateRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime

class DateRepositoryImpl: DateRepository {
    override fun getCurrentTime(): Flow<LocalDateTime> = flow {
        while(true){
            emit(LocalDateTime.now().plusHours(3))
            delay(60*1000)
        }
    }

    override fun getCurrentDate(): Flow<LocalDate> {
        return getCurrentTime().map { it.toLocalDate() }.distinctUntilChanged()
    }

    override fun getCurrentYear(): Flow<Int> {
        return getCurrentTime().map { it.toLocalDate().year }.distinctUntilChanged()
    }
}