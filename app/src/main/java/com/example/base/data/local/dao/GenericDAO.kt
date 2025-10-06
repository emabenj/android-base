package com.example.base.data.local.dao


interface GenericDAO<Entity, ID> {
    suspend fun getAll(): List<@JvmSuppressWildcards Entity>
    suspend fun insert(entity: Entity)
    suspend fun insertAll(entities: List<@JvmSuppressWildcards Entity>)
    suspend fun update(entity: Entity)
    suspend fun delete(entity: Entity)
    suspend fun deleteAll()
}