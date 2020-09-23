package io.redvox.apis

import net.jpountz.lz4.LZ4FrameInputStream
import java.io.File
import java.io.InputStream
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Deserializes an input stream into a RedvoxPacketM.
 * @param inputStream Stream to deserialize.
 * @return A RedvoxPacketM
 */
fun deserialize(inputStream: InputStream): RedvoxApiM.RedvoxPacketM {
    val lz4FrameStream = LZ4FrameInputStream(inputStream.buffered())
    val decompressed = lz4FrameStream.readBytes()
    lz4FrameStream.close()
    return RedvoxApiM.RedvoxPacketM.parseFrom(decompressed)
}

/**
 * Deserializes an input stream into a RedvoxPacketM.
 * @param path Path to .rdvxm file to deserialize.
 * @return A RedvoxPacketM
 */
fun deserialize(path: String): RedvoxApiM.RedvoxPacketM {
    return deserialize(File(path).inputStream())
}

/**
 * Deserializes an input stream into a RedvoxPacketM.
 * @param bytes Buffer to deserialize.
 * @return A RedvoxPacketM
 */
fun deserialize(bytes: ByteArray): RedvoxApiM.RedvoxPacketM {
    return deserialize(bytes.inputStream())
}

/**
 * A type-safe object that represents the components of an API M filename path.
 * @param stationId Represents the path's station ID.
 * @param start Represents the start time of the path's packet.
 * @param extension Represents the file extension of the path.
 */
data class ParsePathResult(val stationId: String, val start: ZonedDateTime, val extension: String) {
    companion object {
        /**
         * Parses the path components from the given file.
         * @param file File to parse path components from.
         * @return A ParsePathResult if the path was successfully parsed, Null otherwise.
         */
        fun parsePath(file: File): ParsePathResult? {
            val fileName = file.name
            val split = fileName.split("_", ".")
            if (split.size != 3) {
                return null
            }

            val dt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(split[1].toLong() / 1000), ZoneId.of("UTC"))
            return ParsePathResult(split[0], dt, split[2])
        }
    }
}

/**
 * A filter for filtering file paths when reading multiple API M files from disk.
 * @param start When provided, all paths must come after this start time.
 * @param end When provided, all paths must come before this end time.
 * @param stationIds When provided, packets must match provided station IDs.
 * @param extension When provided, packets must match this extension.
 */
data class ReadFilter(val start: ZonedDateTime?,
                      val end: ZonedDateTime?,
                      val stationIds: Set<String>?,
                      val extension: String? = "rdvxm") {

    /**
     * Checks if a given file is accepted by this filter.
     * @param file The file to check.
     * @return true if the file is accepted, false otherwise.
     */
    fun acceptsFile(file: File): Boolean {
        val fileToFilter = ParsePathResult.parsePath(file) ?: return false

        start?.let { filterStart ->
            if (fileToFilter.start < filterStart) {
                return false
            }
        }

        end?.let { filterEnd ->
            if (fileToFilter.start > filterEnd) {
                return false
            }
        }

        stationIds?.let { filterStationIds ->
            if (!filterStationIds.contains(fileToFilter.stationId)) {
                return false
            }
        }

        extension?.let { filterExtension ->
            if (fileToFilter.extension != filterExtension) {
                return false
            }
        }

        return true
    }

    companion object {
        /**
         * Returns a default ReadFilter where the only filter is extension == "rdvxm".
         * @return A default ReadFilter where the only filter is extension == "rdvxm".
         */
        fun default(): ReadFilter {
            return ReadFilter(null, null, null)
        }
    }
}


/**
 * Reads API M files from the provided base directory.
 * @param baseDir The directory containing API M files.
 * @param readFilter The (optional) filter used to filter files read from the directory.
 * @return A list of RedvoxPacketM objects.
 */
fun readUnstructured(baseDir: String, readFilter: ReadFilter = ReadFilter.default()): List<RedvoxApiM.RedvoxPacketM> {
    return File(baseDir).walk()
            .filter { file -> readFilter.acceptsFile(file) }
            .map { file -> deserialize(file.inputStream()) }
            .sortedBy { it.timingInformation.packetStartMachTimestamp }
            .toList()
}
