package com.plcoding.androidstorage

import kotlin.experimental.and
import kotlin.experimental.or

class TEA(key: ByteArray?) {

    private val SUGAR = -0x61c88647
    private val CUPS = 32
    private val UNSUGAR = -0x3910c8e0

    val S = IntArray(4)

    init {
        if (key == null) throw RuntimeException("Invalid key: Key was null")
        if (key.size < 16) throw RuntimeException("Invalid key: Length was less than 16 bytes")
        var off = 0
        var i = 0
        while (i < 4) {
            S[i] = (key[off++].toInt() and 0xff or
                    (((key[off++].toInt() and 0xff) shl 8)) or
                    (((key[off++].toInt() and 0xff) shl 16)) or
                    (((key[off++].toInt() and 0xff) shl 24)))
            i++
        }
    }

    fun encrypt(clear: ByteArray): ByteArray {
        val paddedSize = (clear.size / 8 + if (clear.size % 8 == 0) 0 else 1) * 2
        val buffer = IntArray(paddedSize + 1)
        buffer[0] = clear.size
        pack(clear, buffer, 1)
        brew(buffer)
        return unpack(buffer, 0, buffer.size * 4)
    }

    fun decrypt(crypt: ByteArray): ByteArray {
        assert(crypt.size % 4 == 0)
        assert(crypt.size / 4 % 2 == 1)
        val buffer = IntArray(crypt.size / 4)
        pack(crypt, buffer, 0)
        unbrew(buffer)
        return unpack(buffer, 1, buffer[0])
    }

    fun brew(buf: IntArray) {
        assert(buf.size % 2 == 1)
        var v0: Int
        var v1: Int
        var sum: Int
        var n: Int
        var i = 1
        while (i < buf.size) {
            n = CUPS
            v0 = buf[i]
            v1 = buf[i + 1]
            sum = 0
            while (n-- > 0) {
                sum += SUGAR
                v0 += ((v1 shl 4) + S[0] xor v1) + (sum xor (v1 ushr 5)) + S[1]
                v1 += ((v0 shl 4) + S[2] xor v0) + (sum xor (v0 ushr 5)) + S[3]
            }
            buf[i] = v0
            buf[i + 1] = v1
            i += 2
        }
    }

    fun unbrew(buf: IntArray) {
        assert(buf.size % 2 == 1)
        var v0: Int
        var v1: Int
        var sum: Int
        var n: Int
        var i = 1
        while (i < buf.size) {
            n = CUPS
            v0 = buf[i]
            v1 = buf[i + 1]
            sum = UNSUGAR
            while (n-- > 0) {
                v1 -= ((v0 shl 4) + S[2] xor v0) + (sum xor (v0 ushr 5)) + S[3]
                v0 -= ((v1 shl 4) + S[0] xor v1) + (sum xor (v1 ushr 5)) + S[1]
                sum -= SUGAR
            }
            buf[i] = v0
            buf[i + 1] = v1
            i += 2
        }
    }

    fun pack(src: ByteArray, dest: IntArray, destOffset: Int) {
        assert(destOffset + src.size / 4 <= dest.size)
        var i = 0
        var shift = 24
        var j = destOffset
        dest[j] = 0
        while (i < src.size) {
            dest[j] = dest[j] or ((src[i].toInt() and 0xff) shl shift)
            if (shift == 0) {
                shift = 24
                j++
                if (j < dest.size) dest[j] = 0
            } else {
                shift -= 8
            }
            i++
        }
    }

    fun unpack(src: IntArray, srcOffset: Int, destLength: Int): ByteArray {
        assert(destLength <= (src.size - srcOffset) * 4)
        val dest = ByteArray(destLength)
        var i = srcOffset
        var count = 0
        for (j in 0 until destLength) {
            dest[j] = (src[i] shr 24 - 8 * count and 0xff).toByte()
            count++
            if (count == 4) {
                count = 0
                i++
            }
        }
        return dest
    }
}