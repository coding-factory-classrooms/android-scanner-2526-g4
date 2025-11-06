package com.example.scanner

import io.paperdb.Paper

class DbService {


    suspend fun saveCardId(id: Int){
        val card = ApiService.getCardById(id);
        Paper.book().write(id.toString(), card);
    }

    suspend fun deleteCard(id: Int){
        Paper.book().delete(id.toString())
    }

    suspend fun getCardId(id: Int): Card?{
        if (Paper.book().read<Card>(id.toString()) == null){
            return null
        }
        return Paper.book().read<Card>(id.toString());
    }

}