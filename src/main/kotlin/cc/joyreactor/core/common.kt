package cc.joyreactor.core

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

fun IntArray.binarySearchOption(x: Int): Int? =
    Arrays.binarySearch(this, x).let { if (it < 0) null else it }

fun <T : Any, R : Any> T?.mapOption(f: (T) -> R): R? = this?.let(f)

fun ByteArray.toIntArray(): IntArray =
    this.let(ByteBuffer::wrap)
        .order(ByteOrder.LITTLE_ENDIAN)
        .asIntBuffer()
        .run { IntArray(remaining()).also { get(it) } }