package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SuggestionsScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var useDarkTheme by remember { mutableStateOf(true) } // default true for low-light comfort

            MyApplicationTheme(darkTheme = useDarkTheme) {
                var currentTab by remember { mutableStateOf("tasks") }

                val toastEvent by viewModel.toastEvent.collectAsState()
                LaunchedEffect(toastEvent) {
                    toastEvent?.let {
                        Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
                        viewModel.clearToast()
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(
                            modifier = Modifier
                                .navigationBarsPadding()
                                .testTag("bottom_nav_bar"),
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = NavigationBarDefaults.Elevation
                        ) {
                            NavigationBarItem(
                                selected = currentTab == "tasks",
                                onClick = { currentTab = "tasks" },
                                icon = { Icon(Icons.Default.List, contentDescription = "Agenda") },
                                label = { Text("Agenda") },
                                modifier = Modifier.testTag("nav_item_tasks")
                            )
                            NavigationBarItem(
                                selected = currentTab == "suggestions",
                                onClick = { currentTab = "suggestions" },
                                icon = { Icon(Icons.Default.VolumeUp, contentDescription = "Briefings") },
                                label = { Text("Briefings") },
                                modifier = Modifier.testTag("nav_item_suggestions")
                            )
                            NavigationBarItem(
                                selected = currentTab == "analytics",
                                onClick = { currentTab = "analytics" },
                                icon = { Icon(Icons.Default.BarChart, contentDescription = "Insights") },
                                label = { Text("Insights") },
                                modifier = Modifier.testTag("nav_item_analytics")
                            )
                            NavigationBarItem(
                                selected = currentTab == "settings",
                                onClick = { currentTab = "settings" },
                                icon = { Icon(Icons.Default.Settings, contentDescription = "Config") },
                                label = { Text("Config") },
                                modifier = Modifier.testTag("nav_item_settings")
                            )
                        }
                    }
                ) { innerPadding ->
                    val screenModifier = Modifier
                        .padding(innerPadding)
                        .statusBarsPadding()

                    when (currentTab) {
                        "tasks" -> TasksScreen(viewModel = viewModel, modifier = screenModifier)
                        "suggestions" -> SuggestionsScreen(viewModel = viewModel, modifier = screenModifier)
                        "analytics" -> AnalyticsScreen(viewModel = viewModel, modifier = screenModifier)
                        "settings" -> SettingsScreen(
                            viewModel = viewModel,
                            useDarkTheme = useDarkTheme,
                            onDarkThemeToggle = { useDarkTheme = it },
                            modifier = screenModifier
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

