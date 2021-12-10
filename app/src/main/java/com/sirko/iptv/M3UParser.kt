package com.sirko.iptv

import java.io.InputStream
import java.util.*

class M3UParser {
    companion object {
        private const val EXT_M3U = "#EXTM3U"
        private const val EXT_INF = "#EXTINF:"
        private const val EXT_GROUP = "group-title=\""
        private const val EXT_GROUP2 = "#EXTGRP:"
        private const val EXT_LOGO = "tvg-logo=\""
        private const val EXT_URL = "htt(p|ps)://"
    }

    private fun convertStreamToString(stream: InputStream): String {
        return try {
            Scanner(stream).useDelimiter("\\A").next()
        } catch (e: NoSuchElementException) {
            ""
        }
    }

    fun parseFile(stream: InputStream): ArrayList<M3UItem> {
        val m3UPlaylist = ArrayList<M3UItem>()
        val linesArray = convertStreamToString(stream).split(EXT_INF)
        for (currLine in linesArray) {
            if (!currLine.contains(EXT_M3U)) {
                val dataArray = currLine.split(",")
                if (dataArray.size >= 2) {
                    val m3UItem = M3UItem()
                    Regex("${EXT_GROUP}.*?\"").find(dataArray[0])?.value?.let {
                        m3UItem.group = it.substring(EXT_GROUP.length, it.length - 1)
                    }
                    Regex("${EXT_GROUP2}.*?[\n\r]").find(dataArray[1])?.value?.let {
                        m3UItem.group = it.substring(EXT_GROUP2.length, it.length - 1).trim()
                    }
                    Regex("${EXT_LOGO}.*?\"").find(dataArray[0])?.value?.let {
                        m3UItem.icon = it.substring(EXT_LOGO.length, it.length - 1)
                    }
                    Regex("${EXT_URL}.*").find(dataArray[1])?.value?.let {
                        m3UItem.url = it
                    }
                    Regex(".*?[\n\r]").find(dataArray[1])?.value?.let {
                        m3UItem.name = it.trim()
                    }
                    m3UPlaylist.add(m3UItem)
                }
            }
        }
        return m3UPlaylist
    }
}