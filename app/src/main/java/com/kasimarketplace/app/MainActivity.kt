package com.kasimarketplace.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.kasimarketplace.app.navigation.KasiNavGraph
import com.kasimarketplace.app.ui.theme.KasiMarketplaceTheme
import com.kasimarketplace.app.util.AppContainer

/**
 * Single-activity entry point. Builds the shared AppContainer once and hosts the
 * Compose navigation graph, which handles every screen from here on.
 */
class MainActivity : ComponentActivity() {

    private lateinit var appContainer: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContainer = AppContainer(applicationContext)

        setContent {
            KasiMarketplaceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    KasiNavGraph(container = appContainer)
                }
            }
        }
    }
}
