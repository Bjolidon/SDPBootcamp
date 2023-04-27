package com.github.sdp.tarjetakuna.database.local

import androidx.room.*
import com.github.sdp.tarjetakuna.database.DBMagicCard

/**
 * Represents a Magic card data access object.
 */

@Dao
interface MagicCardDao {

    @Query("SELECT * FROM magic_card WHERE 'set' = :set AND 'number' = :number")
    suspend fun getCard(set: String, number: String): DBMagicCard?

    @Query("SELECT * FROM magic_card")
    suspend fun getAllCards(): List<DBMagicCard>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: DBMagicCard)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<DBMagicCard>)

    @Delete
    suspend fun deleteCard(card: DBMagicCard)

    @Query("DELETE FROM magic_card")
    suspend fun deleteAllCards()
}
