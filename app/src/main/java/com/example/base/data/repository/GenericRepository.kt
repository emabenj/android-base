package com.example.base.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.base.data.NetworkHelper
import com.example.base.data.local.dao.GenericDAO
import com.example.base.data.remote.supabase.GenericService
import com.example.base.util.interfaces.ServiceModel
import com.example.base.util.interfaces.Identifiable
import kotlin.reflect.KClass

@RequiresApi(Build.VERSION_CODES.M)
class GenericRepository<
        ID,
        Entity : ServiceModel<ID>,
        InsertModel : Any,
        RemoteModel : Identifiable<ID>,
        >(
            private val networkHelper: NetworkHelper,
            private val dao: GenericDAO<Entity, ID>,
            private val remoteInsertClass: KClass<InsertModel>,
            private val remote: GenericService<RemoteModel, InsertModel, ID>,
            private val remoteClass: KClass<RemoteModel>,
            private val toEntity: (RemoteModel) -> Entity,
            private val toRemote: (Entity) -> RemoteModel,
            private val toInsertRemote: (Entity) -> InsertModel,
            private val relationViewName: String? = null,
) {

    suspend fun getAll(sync: Boolean = true): List<Entity> {
        if (sync && networkHelper.isOnline()) {
            try {
                val remoteGenerics = relationViewName?.let{
                    remote.getAllRelationByView(it, remoteClass)
                }  ?: remote.getAll(remoteClass)

                dao.deleteAll()
                dao.insertAll(remoteGenerics.map { toEntity(it) })
            } catch (e: Exception) {
                Log.e("GenericRepository", "Error sync: ${e.message}", e)
            }
        }
        return dao.getAll()
    }

    suspend fun insert(entity: Entity): ID? {
        val id = try {
            val inserted = remote.insert(toInsertRemote(entity), remoteInsertClass, remoteClass)
            inserted.id
        } catch (e: Exception) {
            Log.e("GenericRepository", "Error insert: ${e.message}", e)
            null
        }
        // ⚠️ Aquí depende: si tu Entity es data class con copy(id = x), necesitas que Entity tenga `copy`.
        val entityWithId = if (id != null)
            entity.copyIdAndSynced(id, true) else
                entity.copyWithEstadoAndSynced(true, isSynced = false)
        dao.insert(entityWithId as Entity)
        return id
    }

    suspend fun update(entity: Entity): ID? {
        val id = try {
            val updated = remote.update(toRemote(entity), remoteClass)
            updated.id
        } catch (e: Exception) {
            Log.e("GenericRepository", "Error update: ${e.message}", e)
            null
        }
        val entityWithId = if (id != null)
            entity.copyIdAndSynced(id, true) else
                entity.copyWithEstadoAndSynced(true, isSynced = false)
        dao.update(entityWithId as Entity)
        return id
    }

    suspend fun delete(entity: Entity) {
        try {
            remote.delete(entity.asDeleteFilters() )
            dao.delete(entity)
        } catch (e: Exception) {
            dao.delete(entity.copyWithEstadoAndSynced(false, isSynced = false) as Entity)
            Log.e("GenericRepository", "Error delete: ${e.message}", e)
        }
    }
    suspend fun getEnumValues(enumName: String): List<String> {
        return try {
            remote.getEnumValues(enumName)
        } catch (e: Exception) {
            Log.e("GenericRepository", "Error sync: ${e.message}", e)
            emptyList()
        }
    }
    suspend fun getMultipleEnums(enumNames: List<String>): Map<String, List<String>> {
        return try {
            remote.getMultipleEnums(enumNames)
        } catch (e: Exception) {
            Log.e("GenericRepository", "Error sync: ${e.message}", e)
            emptyMap()
        }
    }
}
