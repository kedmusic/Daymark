package com.daymark.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.daymark.app.ui.theme.DaymarkTheme
import com.daymark.app.ui.theme.Muted
import com.daymark.app.ui.theme.Purple

import com.daymark.app.ui.screens.home.HomeScreen

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Calendar : Screen("calendar", "Calendar", Icons.Default.CalendarMonth)
    object Groups : Screen("groups", "Groups", Icons.Default.Group)
    object Lists : Screen("lists", "Lists", Icons.Default.List)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
}

val NavigationRoutes = listOf(
    Screen.Home,
    Screen.Calendar,
    Screen.Groups,
    Screen.Lists,
    Screen.Profile
)

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    Scaffold(
        modifier = modifier,
        bottomBar = {
            DaymarkBottomBar(
                currentRoute = currentRoute,
                onTabSelected = { screen ->
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            NavigationRoutes.forEach { screen ->
                composable(screen.route) {
                    if (screen == Screen.Home) {
                        HomeScreen()
                    } else {
                        PlaceholderScreen(title = screen.title)
                    }
                }
            }
        }
    }
}

@Composable
fun DaymarkBottomBar(
    currentRoute: String,
    onTabSelected: (Screen) -> Unit
) {
    NavigationBar(
        modifier = Modifier.heightIn(min = 48.dp)
    ) {
        NavigationRoutes.forEach { screen ->
            val selected = currentRoute == screen.route
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(screen) },
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                alwaysShowLabel = true,
                colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                    selectedIconColor = Purple,
                    selectedTextColor = Purple,
                    unselectedIconColor = Muted,
                    unselectedTextColor = Muted
                )
            )
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "$title Screen")
    }
}

@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Pixel 6", widthDp = 412, heightDp = 915)
@Composable
fun AppNavPreview() {
    DaymarkTheme {
        AppNavGraph()
    }
}

@Preview(name = "Bottom Bar Preview", widthDp = 412, heightDp = 80)
@Composable
fun BottomBarPreview() {
    DaymarkTheme {
        DaymarkBottomBar(
            currentRoute = Screen.Home.route,
            onTabSelected = {}
        )
    }
}
