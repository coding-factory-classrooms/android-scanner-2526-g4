package com.example.scanner

import android.graphics.ColorMatrix
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import com.example.scanner.BuildConfig
import io.ktor.http.content.MultiPartData
import io.paperdb.Paper


object ApiService {

    private val API_KEY = BuildConfig.CLASH_ROYALE_API_KEY;
    private val BASE_URL = "https://api.clashroyale.com/v1"


    // Permet de communiquer avec l'api
    private val client = HttpClient(Android) {
        // Recup le Json et le transforme en object Kotlin
        install(ContentNegotiation) {
            json(Json {
                // Evite que ça plante si dans ce qu'on recup ya des trucs en plus comparé au model Card
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun fetchAllCards(): CardListResponse {
        val url = "$BASE_URL/cards"

        val response = client.get(url) {
            header("Authorization", API_KEY)
        }

        if (response.status.value != 200) {
            throw Exception("Échec API: Statut ${response.status.value}. Clé Bearer invalide ou expirée.")
        }

//        var tests = response.body<CardListResponse>();
//        for (test in tests.items){
//            print("TEST ${test.name}")
//            print("TEST ${test.isUnlock}")
//            var db = DbService()
//            var card = db.getCardId(test.id);
//            if (test.isUnlock != card?.isUnlock){
//            }
//        }
//        tests.items.filter { it -> print("HABITOX ${it}"); it.isUnlock == false  }.forEach { print("HABITOX ${it.name}") }
//        print("HABITOX ${tests.items[0].name}");
//        print("HABITOX ${tests.items[0].isUnlock}");
//        print("HABITOX ${tests.items[1].name}");
//        print("HABITOX ${tests.items[1].isUnlock}");

        return response.body<CardListResponse>()
    }

    fun isUnlockCard(cardApi: Card, cardDB: Card?): Boolean{
        var card = cardApi.isUnlock
        if (cardDB == null){
            return false
        }
        var cardDB = cardDB.isUnlock;

        if (card != cardDB) return true;
        return false
    }


    // L'api ne permet pas de recup qu'une seule carte avec un id, donc on recup tout puis on fait la recherche nous même
    suspend fun getCardById(id: Int): Card {
        val cardListResponse = fetchAllCards()

        return cardListResponse.items.firstOrNull {
            it.id == id
        }
            ?: throw IllegalStateException("Carte non trouvée.")
    }
}