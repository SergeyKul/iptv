package com.sirko.iptv

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.Util

class MainActivity : AppCompatActivity(), FragmentManager.OnBackStackChangedListener {
    companion object {
        const val GROUPS_STATE = 0
        const val CHANNELS_STATE = 1
        const val FULLSCREEN_STATE = 2
    }

    private val model: IPTVViewModel
        get() = ViewModelProvider(this)[IPTVViewModel::class.java]
    private val groupsView = GroupsView { position -> groupsOnClick(position) }
    private val channelsView = ChannelsView { position -> channelsOnClick(position) }
    private var playerView: PlayerView? = null
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Iptv)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.all_сhannels)

        groupsView.init(this, this)
        channelsView.init(this, this)
        playerView = findViewById(R.id.video_view)
        findViewById<View>(R.id.frame_video_view).setOnClickListener {
            setBackStack(FULLSCREEN_STATE)
        }

        supportFragmentManager.addOnBackStackChangedListener(this)
        if (savedInstanceState == null) setBackStack(CHANNELS_STATE)
    }

    private fun setBackStack(state: Int) {
        repeat(state - supportFragmentManager.backStackEntryCount) {
            supportFragmentManager.beginTransaction().addToBackStack(null).commit()
        }
    }

    private fun groupsOnClick(position: Int) {
        if (model.group.value != position) {
            model.group.value = position
            model.current.value = 0
        }
        setBackStack(CHANNELS_STATE)
    }

    private fun channelsOnClick(position: Int) {
        if (model.current.value != position) model.current.value = position
        else setBackStack(FULLSCREEN_STATE)
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(this).build().also { player ->
            playerView?.player = player
            model.sources.observe(this, { sources ->
                for (item in sources) groupsView.addItem(item.second.group)
                model.group.observe(this, { group ->
                    groupsView.setCurrent(group)
                    supportActionBar?.title = groupsView.title
                    player.clearMediaItems()
                    channelsView.clearAdapter()
                    for (item in sources) {
                        if (item.second.group == groupsView.title || groupsView.current == 0) {
                            player.addMediaSource(item.first)
                            channelsView.addItem(item.second)
                        }
                    }
                })
            })
            model.current.observe(this, { current ->
                channelsView.setCurrent(current)
                player.seekToDefaultPosition(current)
                if (!player.isPlaying) {
                    player.prepare()
                    player.play()
                }
            })
        }
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }

    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) initializePlayer()
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (Util.SDK_INT < 24 || player == null) initializePlayer()
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) releasePlayer()
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) releasePlayer()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) supportFragmentManager.popBackStack()
        return super.onOptionsItemSelected(item)
    }

    override fun onBackStackChanged() {
        supportFragmentManager.backStackEntryCount.let {
            groupsView.updateVisibility(it)
            channelsView.updateVisibility(it, player?.currentMediaItemIndex)
            playerView?.isFocusable = it == FULLSCREEN_STATE
            playerView?.useController = it == FULLSCREEN_STATE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val state = supportFragmentManager.backStackEntryCount
        outState.putInt("state", state)
        player?.let { model.current.value = it.currentMediaItemIndex }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val state = savedInstanceState.getInt("state")
        if (state == supportFragmentManager.backStackEntryCount) onBackStackChanged()
        else setBackStack(state)
    }
}