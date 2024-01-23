package com.strawberryCodeCake.capturevoca.data.room.voca

import androidx.room.*
import com.strawberryCodeCake.capturevoca.data.room.voca.model.Voca
import kotlinx.coroutines.flow.Flow

@Dao
interface VocaDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(voca: Voca)

    @Update
    suspend fun update(voca: Voca)

    @Delete
    suspend fun delete(voca: Voca)

    @Query("DELETE FROM Voca WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * from Voca WHERE id = :id")
    fun getItem(id: Int): Flow<Voca>

    @Query("SELECT * from Voca ORDER BY id ASC")
    fun getAllItems(): Flow<List<Voca>>
}