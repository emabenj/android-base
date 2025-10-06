package com.example.base.util.interfaces

interface ServiceModel<ID>: Identifiable<ID> {
    val status: Boolean
    val isSynced: Boolean
    val createdAt: String?
    fun copyId(id: ID): ServiceModel<ID>
    fun copyIdAndSynced(id: ID, isSynced: Boolean): ServiceModel<ID>
    fun copyWithEstadoAndSynced(status: Boolean, isSynced: Boolean): ServiceModel<ID>
    fun asDeleteFilters(): Map<String, Any>
//    companion object {
//        fun <T : ServiceModel> collectionFor(clazz: KClass<T>): String {
//            return when (clazz) {
//                Customer::class -> Constants.CUSTOMERS
//                Expense::class -> Constants.EXPENSES
//                Product::class -> Constants.PRODUCTS
//                Order::class -> Constants.ORDERS
//                // Agrega otras clases si es necesario
//                else -> throw IllegalArgumentException("No se definió colección para ${clazz.simpleName}")
//            }
//        }
//
//        fun <T : ServiceModel> prefixFor(clazz: KClass<T>): String {
//            return when (clazz) {
//                Customer::class -> Constants.IDCUSTOMERS
//                Expense::class -> Constants.IDEXPENSES
//                Product::class -> Constants.IDPRODUCTS
//                Order::class -> Constants.IDORDERS
//                else -> throw IllegalArgumentException("No se definió prefijo para ${clazz.simpleName}")
//            }
//        }
//
//        fun <T : ServiceModel> lengthPrefixFor(clazz: KClass<T>): Int {
//            val prefix = prefixFor(clazz)
//            val length = Constants.LENGTHPREFIX - prefix.length
//            return length
//        }
//            when (clazz) {
//                Producto::class -> Constants.LENGTHPREFIX - prefix.length
//                Reporte::class -> Constants.LENGTHPREFIX - prefix.length
//                User::class -> Constants.LENGTHPREFIX - prefix.length
//                Supervisor::class -> Constants.LENGTHPREFIX - prefix.length
//                Calificacion::class -> Constants.LENGTHPREFIX - prefix.length
//                Devolucion::class -> Constants.LENGTHPREFIX - prefix.length
//                Incidencia::class -> Constants.LENGTHPREFIX - prefix.length
//                Pedido::class -> Constants.LENGTHPREFIX - prefix.length
//                else -> throw IllegalArgumentException("No se definió longitud de prefijo para ${clazz.simpleName}")
//            }
//    }
}