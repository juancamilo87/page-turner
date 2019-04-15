package co.wlue.pageturner.utils

import android.content.Context
import android.util.Log
import uk.co.dolphin_com.seescoreandroid.LicenceKeyInstance
import uk.co.dolphin_com.sscore.LoadOptions
import uk.co.dolphin_com.sscore.SScore
import uk.co.dolphin_com.sscore.ex.ScoreException
import uk.co.dolphin_com.sscore.ex.XMLValidationException
import java.io.*
import java.lang.Exception
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

fun loadMXLFile(file: File): SScore? {
    if (!file.name.endsWith(".mxl"))
        throw IllegalArgumentException("It's not an MXL file")

    file.inputStream()
            .use { inputStream ->
                ZipInputStream(inputStream.buffered())
                        .use { return readSScoreFromZipInputStream(it) }
            }
}

fun loadMXLFromData(data: ByteArray): SScore {
    ZipInputStream(ByteArrayInputStream(data)).use {
        return readSScoreFromZipInputStream(it)
    }
}

fun loadMXLFromRes(context: Context, fileRes: Int): SScore {
    val byteArray = readRawByteArray(context.resources.openRawResource(fileRes))
    return loadMXLFromData(byteArray)
}

/**
 * Reads a file from /raw/res/ and returns it as a byte array
 * @return byte[] if successful, null otherwise
 */
fun readRawByteArray(inputStream: InputStream): ByteArray {
    inputStream.use {
        val raw = ByteArray(inputStream.available())
        inputStream.read(raw)
        return raw
    }
}

private fun readSScoreFromZipInputStream(zipInputStream: ZipInputStream): SScore {
    var entry: ZipEntry
    do {
        entry = zipInputStream.nextEntry
        if (entry == null) break

        if (!entry.name.startsWith("META-INF") // ignore META-INF/ and container.xml
                && entry.name != "container.xml") {
            val outputStream = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var count: Int
            do {
                count = zipInputStream.read(buffer)

                if (count == -1) break

                outputStream.write(buffer, 0, count)

            } while(true)

            try {
                val loadOptions = LoadOptions(LicenceKeyInstance.SeeScoreLibKey, true)
                return SScore.loadXMLData(outputStream.toByteArray(), loadOptions)
            } catch (exception: XMLValidationException) {
                Log.w("sscore", "xml validation error: ${exception.message}")
                exception.printStackTrace()
                throw exception
            } catch (exception: ScoreException) {
                Log.w("sscore", "error:$exception")
                exception.printStackTrace()
                throw exception
            }
        }

    }while(true)
    throw Exception("Error parsing data")
}