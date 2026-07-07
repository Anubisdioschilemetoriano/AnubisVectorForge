package com.anubis.vectorforge.di

import android.content.Context
import com.anubis.vectorforge.data.local.TodoLocalDataSource
import com.anubis.vectorforge.data.repository.TodoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TodoModule {

    @Singleton
    @Provides
    fun provideTodoLocalDataSource(
        @ApplicationContext context: Context
    ): TodoLocalDataSource {
        return TodoLocalDataSource(context)
    }

    @Singleton
    @Provides
    fun provideTodoRepository(
        localDataSource: TodoLocalDataSource
    ): TodoRepository {
        return TodoRepository(localDataSource)
    }
}
