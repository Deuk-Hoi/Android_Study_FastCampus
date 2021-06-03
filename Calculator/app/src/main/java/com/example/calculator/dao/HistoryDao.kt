package com.example.calculator.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.calculator.model.History

@Dao
interface HistoryDao{

    @Query("SELECT * FROM History")
    fun getAll():List<History>

    @Insert
    fun insertHistory(history: History)

    @Query("DELETE FROM History")
    fun deleteAll()

    @Delete
    fun delete(history: History)

    @Query("Select * from History where result like :result limit 1")
    fun findByResult(result : String):History
}