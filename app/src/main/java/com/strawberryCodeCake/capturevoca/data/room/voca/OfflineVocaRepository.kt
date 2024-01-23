package com.strawberryCodeCake.capturevoca.data.room.voca

import android.os.Bundle
import com.strawberryCodeCake.capturevoca.data.room.voca.model.Voca
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow

class OfflineVocaRepository(private val vocaDao: VocaDao) : VocaRepository {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    init {
        firebaseAnalytics = Firebase.analytics
    }
    override fun getAllVocaStream(): Flow<List<Voca>> {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SUCCESS, "Got voca items successfully");
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST, bundle)
        return vocaDao.getAllItems()
    }


    override fun getVocaStream(id: Int): Flow<Voca?> {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SUCCESS, "Got single voca item successfully");
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle)
        return vocaDao.getItem(id)
    }


    override suspend fun insertVoca(voca: Voca) = vocaDao.insert(voca)

    override suspend fun deleteVoca(voca: Voca) = vocaDao.delete(voca)

    override suspend fun deleteVocaById(id: Int) = vocaDao.deleteById(id)

    override suspend fun updateVoca(voca: Voca) = vocaDao.update(voca)
}