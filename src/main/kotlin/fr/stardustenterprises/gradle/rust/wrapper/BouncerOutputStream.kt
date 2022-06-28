package fr.stardustenterprises.gradle.rust.wrapper

import java.io.ByteArrayOutputStream
import java.io.OutputStream

class BouncerOutputStream(
    private val primaryStream: OutputStream,
    private val proxiedStream: OutputStream,
) : OutputStream() {
    private var skipNextFlush = false
    private var buffer = ByteArrayOutputStream()

    override fun write(b: Int) {
        val char = Char(b)
        if (buffer.size() == 0 && char == '{') {
            skipNextFlush = true
        }

        buffer.write(b)

        if (char == '\n') {
            buffer.writeTo(proxiedStream)
            proxiedStream.flush()
            if (!skipNextFlush) {
                buffer.writeTo(primaryStream)
                primaryStream.flush()
            }

            buffer.flush()
            buffer.close()
            buffer = ByteArrayOutputStream()
            skipNextFlush = false
        }
    }
}
