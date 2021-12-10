package com.sirko.iptv

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class IPTVViewModel(application: Application) : AndroidViewModel(application) {
    val sources: MutableLiveData<MutableList<Pair<MediaSource, M3UItem>>> by lazy {
        MutableLiveData<MutableList<Pair<MediaSource, M3UItem>>>().also {
            loadSources()
        }
    }
    val group = MutableLiveData<Int>()
    val current = MutableLiveData<Int>()
    private val assets = application.assets

    private fun loadSources() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val sourceList: MutableList<Pair<MediaSource, M3UItem>> = ArrayList()
                val stream = assets.open("playlist.m3u")
                val parser = M3UParser().parseFile(stream)
                stream.close()
                for (item in parser) {
                    try {
                        val mediaItem = MediaItem.fromUri(item.url)
                        val httpDataSource = DefaultHttpDataSource.Factory()
                        val mediaSourceFactory = DefaultMediaSourceFactory(httpDataSource)
                        val mediaSource = mediaSourceFactory.createMediaSource(mediaItem)
                        sourceList.add(Pair(mediaSource, item))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                if (sourceList.size > 0) {
                    sources.postValue(sourceList)
                    group.postValue(0)
                    current.postValue(0)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}