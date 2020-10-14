package io.redvox.apis

fun main(args: Array<String>) {
    val res = readUnstructured("/home/opq/data/movement")

    println(res.size)
}
