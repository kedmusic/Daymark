package com.daymark.app.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daymark.app.data.FakeData
import com.daymark.app.model.Category
import com.daymark.app.model.Person
import com.daymark.app.ui.theme.Canvas
import com.daymark.app.ui.theme.DaymarkTheme
import com.daymark.app.ui.theme.Purple
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
    onWishClick: (Person) -> Unit = {},
    onPrepareWishClick: () -> Unit = {},
    pendingSnackbarMessage: String? = null,
    onPendingSnackbarConsumed: () -> Unit = {}
) {
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val today = viewModel.getToday()
    val uiState = viewModel.getUiState(today)

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }
    LaunchedEffect(pendingSnackbarMessage) {
        pendingSnackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            onPendingSnackbarConsumed()
        }
    }

    HomeScreenContent(
        uiState = uiState,
        today = today,
        onCategorySelected = { viewModel.selectCategory(it) },
        onEventClick = { viewModel.onEventClicked(it) },
        onQuickActionClick = { action, person -> viewModel.onQuickActionClicked(action, person) },
        onWishClick = onWishClick,
        onPrepareWishClick = onPrepareWishClick,
        onAddClick = {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Add person clicked")
            }
        },
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    today: LocalDate,
    onCategorySelected: (Category) -> Unit,
    onEventClick: (Person) -> Unit,
    onQuickActionClick: (String, Person) -> Unit,
    onAddClick: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    modifier: Modifier = Modifier,
    onWishClick: (Person) -> Unit = {},
    onPrepareWishClick: () -> Unit = {}
) {
    val listState = rememberLazyListState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Canvas,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = Purple,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add someone"
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                GreetingHeader()
            }
            item {
                HeroCard(
                    upcomingCount = uiState.totalUpcomingMoments,
                    onPrepareWish = onPrepareWishClick
                )
            }
            item {
                CategoryFilterRow(
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelected = onCategorySelected
                )
            }
            if (uiState.events.isEmpty()) {
                item {
                    EmptyStateComponent()
                }
            } else {
                items(
                    items = uiState.events,
                    key = { it.id }
                ) { person ->
                    EventCard(
                        person = person,
                        today = today,
                        onCardClick = { onEventClick(person) },
                        onQuickAction = { action -> onQuickActionClick(action, person) },
                        onWishClick = { onWishClick(person) }
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// Previews
@Preview(name = "Normal Data - Pixel 6", widthDp = 412, heightDp = 915)
@Preview(name = "Normal Data - Small Phone", widthDp = 320, heightDp = 640)
@Composable
fun HomeScreenNormalPreview() {
    DaymarkTheme {
        HomeScreenContent(
            uiState = HomeUiState(
                selectedCategory = Category.ALL,
                events = FakeData.samplePeople,
                totalUpcomingMoments = FakeData.samplePeople.size
            ),
            today = LocalDate.of(2026, 5, 15),
            onCategorySelected = {},
            onEventClick = {},
            onQuickActionClick = { _, _ -> },
            onAddClick = {}
        )
    }
}

@Preview(name = "Empty State - Pixel 6", widthDp = 412, heightDp = 915)
@Preview(name = "Empty State - Small Phone", widthDp = 320, heightDp = 640)
@Composable
fun HomeScreenEmptyPreview() {
    DaymarkTheme {
        HomeScreenContent(
            uiState = HomeUiState(
                selectedCategory = Category.ALL,
                events = emptyList(),
                totalUpcomingMoments = 0
            ),
            today = LocalDate.of(2026, 5, 15),
            onCategorySelected = {},
            onEventClick = {},
            onQuickActionClick = { _, _ -> },
            onAddClick = {}
        )
    }
}

@Preview(name = "Long Name - Pixel 6", widthDp = 412, heightDp = 915)
@Preview(name = "Long Name - Small Phone", widthDp = 320, heightDp = 640)
@Composable
fun HomeScreenLongNamePreview() {
    val longNamePerson = Person(
        id = "99",
        name = "Alexander Bartholomew Christopher Montgomery III",
        category = Category.FAMILY,
        eventTitle = "50th Jubilee Celebration",
        eventMonth = 5,
        eventDay = 15,
        phoneNumber = "+1 555-9999",
        isPrivate = true
    )
    DaymarkTheme {
        HomeScreenContent(
            uiState = HomeUiState(
                selectedCategory = Category.ALL,
                events = listOf(longNamePerson),
                totalUpcomingMoments = 1
            ),
            today = LocalDate.of(2026, 5, 15),
            onCategorySelected = {},
            onEventClick = {},
            onQuickActionClick = { _, _ -> },
            onAddClick = {}
        )
    }
}
