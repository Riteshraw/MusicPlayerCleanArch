package com.example.musicplayer.presentation.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.musicplayer.MusicPlayerApplication
import com.example.musicplayer.R
import com.example.musicplayer.databinding.ActivityMainBinding
import com.example.musicplayer.domain.repo.MusicRepo
import com.example.musicplayer.presentation.di.FolderViewModelFactory
import com.example.musicplayer.presentation.utils.MusicPreferences
import com.example.musicplayer.presentation.vm.FolderListVM
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: FolderViewModelFactory

    @Inject
    lateinit var repo: MusicRepo
    lateinit var vm: FolderListVM

    private val pref = MusicPreferences(this)
    private var isBottomViewVisible: Boolean = false

    private val mainView: ConstraintLayout by lazy {
        findViewById(R.id.main_view)
    }

    private val playerView: ConstraintLayout by lazy {
        findViewById(R.id.player_view)
    }

    private val bottomView: LinearLayout by lazy {
        findViewById(R.id.bottom_view)
    }
    private val downArrow: ImageView by lazy {
        findViewById(R.id.img_down_arrow)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as MusicPlayerApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)

        vm = ViewModelProvider(this, viewModelFactory)[FolderListVM::class.java]
        Log.v("TAG_VM", "MainActivity : $vm")
        Log.v("TAG_VM", "MainActivity Repo : $repo")
        binding.vm = vm

        getSongsFromProvider()

        bottomView.setOnClickListener {
            //it.slideUp()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                showBottomView()
            }
        }

        downArrow.setOnClickListener {
            //it.slideUp()
            hideBottomView()
        }

        binding.playerView.progressBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                vm.seekTo(seekBar.progress)
            }
        })

        //Observe if playerFragment is current Fragment
        //if true,bottomView visibility GONE else VISIBLE
        vm.isPlayerFragmentVisible.observe(this, {
            if (it)
                bottomView.visibility = View.VISIBLE
            else
                bottomView.visibility = View.GONE
        })

        vm.currentSong.observe(this, {
            it?.let {
                binding.song = it

//                vm.releaseResources()
//                vm.onPlayClick()
            }
        })

        vm.positionLD.observe(this, {
            showBottomView()
            vm.setCurrentSong()
        })


    }

    private fun getSongsFromProvider() {
        //check if app is running for first time, if yes then scan for all songs and store in DB
        lifecycleScope.launch {
            val isFirstRun = isAppFirstRun()
            if (isFirstRun) {
                refreshSongsDB()
            } else {
                vm.getAllFolders()
            }
        }
    }

    private suspend fun isAppFirstRun(): Boolean {
        /*val pref = dataStore.data.first()
        return pref[applicationFirstRun]?:true*/

        return pref.getBooleanKeyValue(
            MusicPreferences.applicationFirstRun
        )
    }

    private fun refreshSongsDB() {
        vm.getSongsByContentProvider()
        // change preferences value of App_first_run to false after songs fetched from db
        makeAppFirstRunFalse()
    }

    private fun makeAppFirstRunFalse() {
        lifecycleScope.launch {
            pref.saveKeyValue(MusicPreferences.applicationFirstRun, false)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showBottomView() {
        val animate =
            TranslateAnimation(0f, 0f, mainView.height.toFloat() - bottomView.height.toFloat(), 0f)
        animate.duration = 500.toLong()
        animate.fillAfter = true
        playerView.visibility = View.VISIBLE
        playerView.startAnimation(animate)
        playerView.isClickable = true
        playerView.isFocusable = true
        playerView.isFocusableInTouchMode = true
        isBottomViewVisible = true
    }

    private fun hideBottomView() {
        val animate = TranslateAnimation(0f, 0f, 0f, mainView.height.toFloat())
        animate.duration = 500.toLong()
        animate.fillAfter = false
        playerView.startAnimation(animate)
        playerView.visibility = View.GONE
        playerView.isClickable = false
        playerView.isFocusable = false
        playerView.isFocusableInTouchMode = false
        isBottomViewVisible = false
    }

    override fun onBackPressed() {
        if (isBottomViewVisible)
            hideBottomView()
        else
            super.onBackPressed()
    }

}