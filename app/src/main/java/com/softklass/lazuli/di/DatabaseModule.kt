package com.softklass.lazuli.di

import android.content.Context
import androidx.room.Room
import com.softklass.lazuli.data.database.ItemDao
import com.softklass.lazuli.data.database.ListDatabase
import com.softklass.lazuli.data.database.ParentDao
import com.softklass.lazuli.data.repository.ItemRepository
import com.softklass.lazuli.data.repository.ParentRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun providesDatabaseSource(
        @ApplicationContext context: Context,
    ) = Room.databaseBuilder(
        context,
        ListDatabase::class.java,
        "list_database",
    ).build()

    @Singleton
    @Provides
    fun providesItemDao(database: ListDatabase) = database.itemDao()

    @Singleton
    @Provides
    fun providesParentDao(database: ListDatabase) = database.parentDao()

    @Singleton
    @Provides
    fun providesParentRepository(parentDao: ParentDao) = ParentRepository(parentDao)

    @Singleton
    @Provides
    fun providesItemRepository(itemDao: ItemDao) = ItemRepository(itemDao)
}
