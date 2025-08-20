package com.softklass.lazuli.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.softklass.lazuli.data.models.Item
import com.softklass.lazuli.data.models.Parent

@Database(
    entities = [
        Item::class,
        Parent::class,
    ],
    version = 3,
    exportSchema = false,
)
abstract class ListDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao

    abstract fun parentDao(): ParentDao

    companion object {
        val MIGRATION_1_2 =
            object : androidx.room.migration.Migration(1, 2) {
                override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                    db.execSQL("ALTER TABLE list_item ADD COLUMN notes TEXT")
                    db.execSQL("ALTER TABLE list_item ADD COLUMN notesIsMarkdown INTEGER NOT NULL DEFAULT 0")
                }
            }

        val MIGRATION_2_3 =
            object : androidx.room.migration.Migration(2, 3) {
                override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                    db.execSQL("ALTER TABLE list_item ADD COLUMN drawing TEXT")
                }
            }
    }
}
