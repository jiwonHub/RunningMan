package com.cjwjsw.runningman.data.data_source.db.userInfo

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UserInformationEntity::class], version = 1)
abstract class UserInfoAppDatabase : RoomDatabase() {
    abstract fun userInfoDao(): UserInformationDao
}
