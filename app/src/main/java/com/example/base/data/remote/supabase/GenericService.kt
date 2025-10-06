@file:Suppress("UNCHECKED_CAST")

package com.example.base.data.remote.supabase

import com.example.base.util.interfaces.Identifiable
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.result.PostgrestResult
import io.github.jan.supabase.postgrest.rpc
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.buildJsonArray
import kotlin.reflect.KClass
import javax.inject.Inject

class GenericService<T : Identifiable<ID>,I : Any, ID> @Inject constructor(
    private val client: SupabaseClient,
    private val tableName: String,
) {
    private val table = client.from(tableName)

    // 👉 extensiones para decode sin reified
    private fun <T : Any> PostgrestResult.decodeList(clazz: KClass<T>): List<T> {
        val deserializer = serializer(clazz.java)
        return Json.decodeFromString(ListSerializer(deserializer), data) as List<T>
    }

    private fun <T : Any> PostgrestResult.decodeSingle(clazz: KClass<T>): T {
        return decodeList(clazz).first()
    }

    suspend fun getAll(clazz: KClass<T>): List<T> {
        val result = table.select()
        return result.decodeList(clazz)
    }

    suspend fun insert(
        model: I,
        insertClass: KClass<I>,
        clazz: KClass<T>
    ): T {
        val kSerializer = serializer(insertClass.java)
        val jsonElement = Json.encodeToJsonElement(kSerializer, model)
        val jsonArray = buildJsonArray { add(jsonElement) }

        val result = table.insert(jsonArray) { select() }
        return result.decodeSingle(clazz)
    }


    suspend fun update(
        model: T,
        clazz: KClass<T>
    ): T {
        val kSerializer = serializer(clazz.java)
        val jsonElement = Json.encodeToJsonElement(kSerializer, model)
        val jsonArray = buildJsonArray { add(jsonElement) }

        val result = table.update(jsonArray) {
            filter { eq("id", model.id as Any) }
            select()
        }
        return result.decodeSingle(clazz)
    }

    suspend fun delete(filters: Map<String, Any>) {
        table.delete {
            filter {
                filters.forEach { (col, value) ->
                    eq(col, value)
                }
            }
        }
    }

    suspend fun getAllRelationByView(
        viewName: String,
        clazz: KClass<T>
    ): List<T> {
        val result = client.from(viewName).select()
        return result.decodeList(clazz)
    }

    suspend fun getEnumValues(enumName: String): List<String> {
        val result = client.postgrest.rpc(
            "get_enum_labels",
            mapOf("enum_name" to enumName)
        )
        return Json.decodeFromString(result.data)
    }

    suspend fun getMultipleEnums(enumNames: List<String>): Map<String, List<String>> {
        if(enumNames.isEmpty()) return emptyMap()
        if(enumNames.size == 1) return mapOf(enumNames.first() to getEnumValues(enumNames.first()))
        val result = client.postgrest.rpc(
            "get_enums",
            mapOf("enum_names" to enumNames)
        )
        return Json.decodeFromString(result.data)
    }
}
