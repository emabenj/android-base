package com.example.base.util.annotation

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class FormField(
    val visible: Boolean = true,
    val editable: Boolean = true,
    val enumName: String = "",
    val isDate: Boolean = false
)
