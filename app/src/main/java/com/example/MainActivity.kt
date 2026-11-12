package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.CardSurface
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VoidBlack
import com.example.vphone.ui.MainViewModel
import com.example.vphone.ui.components.FloatingHudOverlay
import com.example.vphone.ui.dashboard.DashboardScreen
import com.example.vphone.ui.games.VirtualGamesScreen
import com.example.vphone.ui.performance.PerformanceScreen
import com.example.vphone.ui.phones.VirtualPhonesScreen
import com.example.vphone.ui.profiles.DeviceProfilesScreen
import com.example.vphone.ui.virtualos.VirtualPhoneDesktopScreen

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Apply highest supported refresh rate automatically on launch
        viewModel.lockMaxRefreshRate(this)

        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    val activeDesktopPhone by viewModel.activeDesktopPhone.collectAsStateWithLifecycle()
    val allProfiles by viewModel.allProfiles.collectAsStateWithLifecycle()
    val showFloatingHud by viewModel.showFloatingHud.collectAsStateWithLifecycle()
    val fpsReading by viewModel.fpsReading.collectAsStateWithLifecycle()
    val displayCaps by viewModel.displayCaps.collectAsStateWithLifecycle()
    val systemStats by viewModel.systemStats.collectAsStateWithLifecycle()
    val notificationMessage by viewModel.notificationMessage.collectAsStateWithLifecycle()

    LaunchedEffect(notificationMessage) {
        notificationMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearToast()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(VoidBlack)) {
        if (activeDesktopPhone != null) {
            val profile = allProfiles.firstOrNull { it.id == activeDesktopPhone?.deviceProfileId }
            VirtualPhoneDesktopScreen(
                phone = activeDesktopPhone!!,
                profile = profile,
                viewModel = viewModel,
                onExitDesktop = { viewModel.exitVirtualDesktop() }
            )
        } else {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = VoidBlack,
                snackbarHost = { SnackbarHost(snackbarHostState) },
                bottomBar = {
                    NavigationBar(
                        containerColor = DarkCharcoal,
                        tonalElevation = 8.dp,
                        modifier = Modifier.testTag("main_bottom_nav")
                    ) {
                        val navItems = listOf(
                            Triple("Dashboard", Icons.Default.Dashboard, "dashboard_tab"),
                            Triple("V-Phones", Icons.Default.Smartphone, "vphones_tab"),
                            Triple("Games", Icons.Default.Games, "games_tab"),
                            Triple("Profiles", Icons.Default.Tune, "profiles_tab"),
                            Triple("Performance", Icons.Default.Speed, "performance_tab")
                        )

                        navItems.forEachIndexed { index, (label, icon, tag) ->
                            val isSelected = (selectedTab == index)
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { selectedTab = index },
                                icon = {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = VoidBlack,
                                    selectedTextColor = NeonCyan,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted,
                                    indicatorColor = NeonCyan
                                ),
                                modifier = Modifier.testTag(tag)
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (selectedTab) {
                        0 -> DashboardScreen(
                            viewModel = viewModel,
                            onNavigateToPhones = { selectedTab = 1 },
                            onNavigateToGames = { selectedTab = 2 },
                            onNavigateToProfiles = { selectedTab = 3 },
                            onNavigateToPerformance = { selectedTab = 4 }
                        )
                        1 -> VirtualPhonesScreen(viewModel = viewModel)
                        2 -> VirtualGamesScreen(viewModel = viewModel)
                        3 -> DeviceProfilesScreen(viewModel = viewModel)
                        4 -> PerformanceScreen(viewModel = viewModel)
                    }
                }
            }
        }

        // Draggable in-app performance HUD
        AnimatedVisibility(visible = showFloatingHud) {
            val activeProfile = allProfiles.firstOrNull { it.id == activeDesktopPhone?.deviceProfileId }
            FloatingHudOverlay(
                fps = fpsReading.currentFps,
                displayHz = displayCaps.currentRefreshRateInt,
                ramPercent = systemStats.ramUsagePercent,
                batteryTempC = systemStats.batteryTemperatureC,
                activeProfileName = activeProfile?.model ?: "Infinix X6891",
                onClose = { viewModel.toggleFloatingHud() }
            )
        }
    }
}
