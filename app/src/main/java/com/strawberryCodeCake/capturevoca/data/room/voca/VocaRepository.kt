package com.strawberryCodeCake.capturevoca.data.room.voca

import com.strawberryCodeCake.capturevoca.data.room.voca.model.Voca
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Item] from a given data source.
 */
interface VocaRepository {
    /**
     * Retrieve all the items from the given data source.
     */
    fun getAllVocaStream(): Flow<List<Voca>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getVocaStream(id: Int): Flow<Voca?>

    /**
     * Insert item in the data source
     */
    suspend fun insertVoca(item: Voca)

    /**
     * Delete item from the data source
     */
    suspend fun deleteVoca(item: Voca)

    suspend fun deleteVocaById(id: Int)

    /**
     * Update item in the data source
     */
    suspend fun updateVoca(item: Voca)
}