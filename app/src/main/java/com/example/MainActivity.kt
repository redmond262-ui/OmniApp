package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.example.util.LocationProvider
import kotlinx.coroutines.launch
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.screens.ComparisonScreen
import com.example.ui.screens.ContactsScreen
import com.example.ui.screens.DailyHubScreen
import com.example.ui.screens.InteractiveRouteScreen
import com.example.ui.screens.MerchantHubScreen
import com.example.ui.screens.SmartListScreen
import com.example.ui.screens.StoreDetailScreen
import com.example.ui.screens.StoresListScreen
import com.example.ui.screens.TripPlannerScreen
import com.example.ui.screens.UserProfileScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SlateNavy

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                OmniHubApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OmniHubApp(viewModel: MainViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val dataStatus by viewModel.dataStatus.collectAsState()

    // Ubicación real: se pide el permiso al abrir la app y se cargan clima y comercios de la zona.
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        scope.launch { viewModel.onLocationResolved(LocationProvider.get(context)) }
    }
    LaunchedEffect(Unit) {
        if (LocationProvider.hasPermission(context)) {
            viewModel.onLocationResolved(LocationProvider.get(context))
        } else {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    // Manejo de navegación hacia atrás en subpantallas
    BackHandler(
        enabled = currentScreen is Screen.StoreDetail ||
                currentScreen is Screen.InteractiveRoute ||
                currentScreen is Screen.Comparison ||
                currentScreen is Screen.UserProfile ||
                currentScreen is Screen.MerchantHub ||
                currentScreen is Screen.TripPlanner
    ) {
        when (currentScreen) {
            is Screen.InteractiveRoute -> viewModel.navigateTo(Screen.StoreDetail)
            is Screen.StoreDetail -> viewModel.navigateTo(Screen.StoresList)
            is Screen.Comparison -> viewModel.navigateTo(Screen.SmartList)
            is Screen.MerchantHub -> viewModel.navigateTo(Screen.UserProfile)
            is Screen.TripPlanner -> viewModel.navigateTo(Screen.DailyHub)
            is Screen.UserProfile -> viewModel.navigateTo(Screen.DailyHub)
            else -> viewModel.navigateTo(Screen.DailyHub)
        }
    }

    val isFullScreenSubpage = currentScreen is Screen.StoreDetail ||
            currentScreen is Screen.InteractiveRoute ||
            currentScreen is Screen.MerchantHub ||
            currentScreen is Screen.TripPlanner

    Scaffold(
        topBar = {
            if (!isFullScreenSubpage) {
                TopAppBar(
                    title = {
                        Text(
                            text = when (currentScreen) {
                                Screen.DailyHub -> "OmniHub • Tu Día"
                                Screen.SmartList -> "Mi Lista Inteligente"
                                Screen.StoresList -> "Comercios del Barrio"
                                Screen.Comparison -> "Comparador de Canasta"
                                Screen.Wallet -> "OmniPay • Billetera"
                                Screen.Contacts -> "Remises & Servicios"
                                Screen.UserProfile -> "Mi Perfil & Ajustes"
                                else -> "OmniHub"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = SlateNavy
                        )
                    },
                    actions = {
                        // Acceso rápido a Viajes & Rutas
                        IconButton(onClick = { viewModel.navigateTo(Screen.TripPlanner) }) {
                            Icon(
                                imageVector = Icons.Default.Directions,
                                contentDescription = "Planificar Viaje",
                                tint = EmeraldPrimary
                            )
                        }

                        // Comparador
                        if (currentScreen is Screen.SmartList || currentScreen is Screen.DailyHub) {
                            IconButton(onClick = { viewModel.navigateTo(Screen.Comparison) }) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Comparador de Canasta",
                                    tint = EmeraldPrimary
                                )
                            }
                        }

                        // Perfil de Usuario
                        IconButton(onClick = { viewModel.navigateTo(Screen.UserProfile) }) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Mi Perfil",
                                tint = if (currentScreen is Screen.UserProfile) EmeraldPrimary else SlateNavy
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White
                    )
                )
            }
        },
        bottomBar = {
            if (!isFullScreenSubpage && currentScreen !is Screen.UserProfile) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 6.dp
                ) {
                    NavigationBarItem(
                        selected = currentScreen is Screen.DailyHub,
                        onClick = { viewModel.navigateTo(Screen.DailyHub) },
                        icon = { Icon(Icons.Default.WbSunny, contentDescription = "Tu Día") },
                        label = { Text("Tu Día", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldPrimary,
                            selectedTextColor = EmeraldDark,
                            indicatorColor = EmeraldContainer
                        ),
                        modifier = Modifier.testTag("nav_daily_hub")
                    )

                    NavigationBarItem(
                        selected = currentScreen is Screen.SmartList || currentScreen is Screen.Comparison,
                        onClick = { viewModel.navigateTo(Screen.SmartList) },
                        icon = { Icon(Icons.Default.FormatListBulleted, contentDescription = "Mi Lista") },
                        label = { Text("Mi Lista", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldPrimary,
                            selectedTextColor = EmeraldDark,
                            indicatorColor = EmeraldContainer
                        ),
                        modifier = Modifier.testTag("nav_smart_list")
                    )

                    NavigationBarItem(
                        selected = currentScreen is Screen.StoresList,
                        onClick = { viewModel.navigateTo(Screen.StoresList) },
                        icon = { Icon(Icons.Default.Storefront, contentDescription = "Comercios") },
                        label = { Text("Comercios", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldPrimary,
                            selectedTextColor = EmeraldDark,
                            indicatorColor = EmeraldContainer
                        ),
                        modifier = Modifier.testTag("nav_stores")
                    )

                    NavigationBarItem(
                        selected = currentScreen is Screen.Wallet,
                        onClick = { viewModel.navigateTo(Screen.Wallet) },
                        icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Billetera") },
                        label = { Text("Billetera", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldPrimary,
                            selectedTextColor = EmeraldDark,
                            indicatorColor = EmeraldContainer
                        ),
                        modifier = Modifier.testTag("nav_wallet")
                    )

                    NavigationBarItem(
                        selected = currentScreen is Screen.Contacts,
                        onClick = { viewModel.navigateTo(Screen.Contacts) },
                        icon = { Icon(Icons.Default.LocalTaxi, contentDescription = "Remises") },
                        label = { Text("Remises", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldPrimary,
                            selectedTextColor = EmeraldDark,
                            indicatorColor = EmeraldContainer
                        ),
                        modifier = Modifier.testTag("nav_contacts")
                    )
                }
            }
        },
        containerColor = Color(0xFFF8FAFC),
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
        if (dataStatus != null && !isFullScreenSubpage) {
            Text(
                text = dataStatus ?: "",
                fontSize = 11.sp,
                color = SlateNavy,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFEF3C7))
                    .clickable {
                        scope.launch { viewModel.onLocationResolved(LocationProvider.get(context)) }
                    }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Crossfade(targetState = currentScreen, label = "screen_crossfade") { screen ->
                when (screen) {
                    Screen.DailyHub -> DailyHubScreen(viewModel = viewModel)
                    Screen.SmartList -> SmartListScreen(viewModel = viewModel)
                    Screen.StoresList -> StoresListScreen(viewModel = viewModel)
                    Screen.Comparison -> ComparisonScreen(viewModel = viewModel)
                    Screen.StoreDetail -> StoreDetailScreen(viewModel = viewModel)
                    Screen.InteractiveRoute -> InteractiveRouteScreen(viewModel = viewModel)
                    Screen.Wallet -> WalletScreen(viewModel = viewModel)
                    Screen.Contacts -> ContactsScreen(viewModel = viewModel)
                    Screen.UserProfile -> UserProfileScreen(viewModel = viewModel)
                    Screen.MerchantHub -> MerchantHubScreen(viewModel = viewModel)
                    Screen.TripPlanner -> TripPlannerScreen(viewModel = viewModel)
                }
            }
        }
        }
    }
}
