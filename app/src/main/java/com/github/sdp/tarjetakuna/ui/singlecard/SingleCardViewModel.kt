package com.github.sdp.tarjetakuna.ui.singlecard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.sdp.tarjetakuna.database.*
import com.github.sdp.tarjetakuna.database.local.AppDatabase
import com.github.sdp.tarjetakuna.model.MagicCard
import com.github.sdp.tarjetakuna.ui.authentication.SignIn
import kotlinx.coroutines.launch

/**
 * ViewModel for the SingleCardFragment
 */
class SingleCardViewModel : ViewModel() {

    var localDatabase: AppDatabase? = null
    var card: MagicCard? = null

    private val _isConnected = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean> = _isConnected

    private val _buttonAddText = MutableLiveData<Boolean>()
    val buttonAddText: LiveData<Boolean> = _buttonAddText

    private val _buttonWantedText = MutableLiveData<Boolean>()
    val buttonWantedText: LiveData<Boolean> = _buttonWantedText

    private var userDB = UserRTDB(
        FirebaseDB()
    )

    /**
     * Check if the user is connected to the app with a google account
     */
    fun checkUserConnected() {
        _isConnected.value = SignIn.getSignIn().isUserLoggedIn()
    }


    /**
     * Check if the card is in the collection of the user, either wanted or owned
     */
    fun checkCardInCollection() {
        if (!SignIn.getSignIn().isUserLoggedIn()) {
            return
        }
        DatabaseSync.sync()
        viewModelScope.launch {
            val lCard =
                card?.set?.let {
                    localDatabase?.magicCardDao()?.getCard(it.code, card!!.number.toString())
                }
            if (lCard != null) {
                _buttonAddText.value = lCard.possession != CardPossession.OWNED
                _buttonWantedText.value = lCard.possession != CardPossession.WANTED
                Log.i("SingleCardViewModel", "checkCardInCollection: card found in local database")
            }
        }
    }

    /**
     * Manage the collection of cards wanted by the user,
     * add the card if it's not in the collection,
     * remove it only if it's not owned
     */
    fun manageWantedCollection() {

        viewModelScope.launch {
            val lCard =
                card?.set?.let {
                    localDatabase?.magicCardDao()?.getCard(it.code, card!!.number.toString())
                }
            if (lCard != null) {
                // card not wanted -> make it unwanted from the collection
                if (lCard.possession == CardPossession.WANTED) {
                    card?.let { manageCardsInDatabase(it, CardPossession.NONE) }
                    updateButtons(CardPossession.NONE)
                } else {
                    // card wanted -> make it wanted in the collection
                    card?.let { manageCardsInDatabase(it, CardPossession.WANTED) }
                    updateButtons(CardPossession.WANTED)
                }
            } else {
                // card not in the database -> add it as wanted in the local database
                card?.let { manageCardsInDatabase(it, CardPossession.WANTED) }
                updateButtons(CardPossession.WANTED)
            }
            // sync the local database with the firebase if possible
            DatabaseSync.sync()
        }
    }

    /**
     * Manage the collection of cards owned by the user,
     * add the card if it's not in the collection,
     * remove it if it's in the collection
     */
    fun manageOwnedCollection() {
        viewModelScope.launch {
            val lCard =
                card?.set?.let {
                    localDatabase?.magicCardDao()?.getCard(it.code, card!!.number.toString())
                }
            if (lCard != null) {
                // card owned -> make it not owned from the collection
                if (lCard.possession == CardPossession.OWNED) {
                    card?.let { manageCardsInDatabase(it, CardPossession.NONE) }
                    updateButtons(CardPossession.NONE)
                } else {
                    // card not owned -> make it owned in the collection
                    card?.let { manageCardsInDatabase(it, CardPossession.OWNED) }
                    updateButtons(CardPossession.OWNED)
                }
            } else {
                // card not in the database -> add it as owned in the local database
                card?.let { manageCardsInDatabase(it, CardPossession.OWNED) }
                updateButtons(CardPossession.OWNED)
            }
            // sync the local database with the firebase if possible
            DatabaseSync.sync()
        }
    }

    /**
     * Manage the cards in the local database
     * @param card the card to manage
     * @param p the possession of the card (owned or wanted)
     */
    private suspend fun manageCardsInDatabase(card: MagicCard, p: CardPossession) {
        localDatabase?.magicCardDao()?.insertCard(DBMagicCard(card, p))
    }

    /**
     * Updates the wanted button text
     */
    private fun updateButtons(p: CardPossession) {
        when (p) {
            CardPossession.WANTED -> {
                _buttonAddText.value = true
                _buttonWantedText.value = false
            }
            CardPossession.OWNED -> {
                _buttonWantedText.value = true
                _buttonAddText.value = false
            }
            else -> {
                _buttonWantedText.value = true
                _buttonAddText.value = true
            }
        }
    }
}
