package com.rakuseru.storyapp1.ui

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import com.rakuseru.storyapp1.R
import com.rakuseru.storyapp1.data.ListStory
import com.rakuseru.storyapp1.databinding.ActivityHomeBinding
import com.rakuseru.storyapp1.preference.UserPreference
import com.rakuseru.storyapp1.ui.adapter.ListStoryAdapter
import com.rakuseru.storyapp1.ui.viewmodel.HomeViewModel
import com.rakuseru.storyapp1.ui.viewmodel.UserViewModel
import com.rakuseru.storyapp1.ui.viewmodel.ViewModelFactory


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var token: String
    private lateinit var rvSkeleton: Skeleton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Init functions
        setAction()

        val pref = UserPreference.getInstance(dataStore)
        val userViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[UserViewModel::class.java]

        // RecycleView
        val layoutManager = LinearLayoutManager(this)
        binding.rvStories.layoutManager = layoutManager

        userViewModel.getToken().observe(this) {
            token = it
            homeViewModel.getStories(token)
        }

        homeViewModel.message.observe(this) {
            if (homeViewModel.isError) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                showNoData(true)
            } else {
                showNoData(false)
                setUserData(homeViewModel.listStories)
            }
        }

        homeViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    // CustomView Actions
    private fun setAction() {
        binding.fab.setOnClickListener {
            startActivity(Intent(this, AddActivity::class.java))
        }

        binding.btnRefresh.setOnClickListener {
            homeViewModel.getStories(token)
        }

        // Skeleton Init
        rvSkeleton = binding.rvStories.applySkeleton(R.layout.item_list_story)
    }

    // Menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.action_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.menu_logout) {
            showAlertDialog()
            return true
        } else if (id == R.id.menu_about) {
            Toast.makeText(this, R.string.about_detail, Toast.LENGTH_SHORT).show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this)
        val alert = builder.create()
        builder
            .setTitle(getString(R.string.logout))
            .setMessage(getString(R.string.you_sure))
            .setPositiveButton(getString(R.string.no)) { _, _ ->
                alert.cancel()
            }
            .setNegativeButton(getString(R.string.yes)) { _, _ ->
                logout()
            }
            .show()
    }

    private fun setUserData(liststory: List<ListStory>) {
        if (liststory.isEmpty()) {
            showNoData(true)
        } else {
            showNoData(false)
            val listAdapter = ListStoryAdapter(liststory)
            binding.rvStories.adapter = listAdapter
        }
    }

    private fun logout() {
        val pref = UserPreference.getInstance(dataStore)
        val userViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[UserViewModel::class.java]

        userViewModel.apply {
            saveLoginState(false)
            saveToken("")
            saveName("")
        }
        Toast.makeText(this, "Logout Success", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) rvSkeleton.showSkeleton() else rvSkeleton.showOriginal()
    }
    private fun showNoData(isNoData: Boolean) {
        binding.tvHomeNoData.visibility = if (isNoData) View.VISIBLE else View.GONE
    }
}