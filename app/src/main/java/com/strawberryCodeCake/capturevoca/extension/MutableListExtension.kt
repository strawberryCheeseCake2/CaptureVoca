package com.strawberryCodeCake.capturevoca.extension

fun <T> List<T>.searchFrom(from: Int, key: T): Int? {

    for (i in from until this.size) {
        if (key == this[i]) {
            return i
        }
    }

    return null
}