package com.example.scanner

import androidx.annotation.NonNull
import io.paperdb.Paper

class DbService {


    suspend fun saveCardId(id: Int){
        val getCurrentcard = ApiService.getCardById(id);
        val ownedCard = OwnedCard(id, getCurrentcard.count, getCurrentcard.acquisitionDate, getCurrentcard.isFavorite);
        Paper.book().write(id.toString(), ownedCard);
    }

    suspend fun deleteOwnedCard(id: Int){
        Paper.book().delete(id.toString())
    }

    suspend fun setFavorite(id: Int, isFavorite: Boolean) {
        val ownedCard = getOwnedCardId(id)
        val updatedCard = ownedCard!!.copy(isFavorite = isFavorite)
        Paper.book().write(id.toString(), updatedCard)
    }

    fun getOwnedCardId(id: Int): OwnedCard? {
        return Paper.book().read<OwnedCard>(id.toString())
    }

    fun getAllCard(): Map<Int, OwnedCard> {
        val allKeys = Paper.book().allKeys
        val ownedCards = mutableMapOf<Int, OwnedCard>()

        allKeys.forEach { key ->
            val card = Paper.book().read<OwnedCard>(key)
            if (card != null) {
                val cardId = key.toIntOrNull()
                if (cardId != null) {
                    ownedCards[cardId] = card
                }
            }
        }
        return ownedCards
    }

}