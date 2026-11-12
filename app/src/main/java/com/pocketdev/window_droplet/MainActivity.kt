package com.pocketdev.window_droplet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.ViewModelProvider
import com.pocketdev.window_droplet.ui.DropletGameScreen
import com.pocketdev.window_droplet.viewmodel.DropletGameViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: DropletGameViewModel by lazy {
        ViewModelProvider(this)[DropletGameViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                DropletGameScreen(viewModel = viewModel)
            }
        }
    }
}
