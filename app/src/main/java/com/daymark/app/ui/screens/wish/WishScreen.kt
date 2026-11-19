package com.daymark.app.ui.screens.wish

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daymark.app.data.FakeData
import com.daymark.app.model.Person
import com.daymark.app.ui.theme.Canvas

@Composable
fun WishScreen(
    person: Person,
    onCancel: () -> Unit,
    onShareSheetClosed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WishViewModel = viewModel(
        key = "wish-${person.id}",
        factory = remember(person.id) { WishViewModel.Factory(person) }
    )
) {
    val state by viewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val shareLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        onShareSheetClosed()
    }

    LaunchedEffect(state.isEditing) {
        if (state.isEditing) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    BackHandler {
        if (viewModel.requestCancel()) onCancel()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Canvas
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                WishHeader(person = state.person, eventDate = state.eventDate)
                WishToneChips(
                    selectedTone = state.selectedTone,
                    onToneSelected = viewModel::selectTone
                )
                WishMessageCard(
                    message = state.message,
                    isEditing = state.isEditing,
                    onMessageChanged = viewModel::updateMessage,
                    focusRequester = if (state.isEditing) focusRequester else null
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            WishFooter(
                isEditing = state.isEditing,
                onSend = {
                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, state.message)
                    }
                    shareLauncher.launch(Intent.createChooser(sendIntent, "Share your wish"))
                },
                onEdit = {
                    if (state.isEditing) {
                        viewModel.toggleEditing()
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    } else {
                        viewModel.toggleEditing()
                    }
                },
                onCancel = { if (viewModel.requestCancel()) onCancel() }
            )
        }
    }

    if (state.showReplaceEditsConfirmation) {
        AlertDialog(
            onDismissRequest = viewModel::dismissToneReplacement,
            title = { Text("Replace your edits?") },
            text = { Text("Switching tone will replace the message you've changed.") },
            confirmButton = {
                TextButton(onClick = viewModel::confirmToneReplacement) {
                    Text("Replace")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissToneReplacement) {
                    Text("Keep editing")
                }
            }
        )
    }

    if (state.showDiscardConfirmation) {
        AlertDialog(
            onDismissRequest = viewModel::dismissDiscardConfirmation,
            title = { Text("Discard your changes?") },
            text = { Text("Your edited wish will be discarded.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.dismissDiscardConfirmation()
                    onCancel()
                }) {
                    Text("Discard")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissDiscardConfirmation) {
                    Text("Keep editing")
                }
            }
        )
    }
}

@Composable
fun WishPersonPickerScreen(
    people: List<Person> = FakeData.samplePeople,
    onPersonSelected: (Person) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Canvas
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Who would you like to wish?", style = MaterialTheme.typography.headlineSmall)
            people.forEach { person ->
                Button(
                    onClick = { onPersonSelected(person) },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(person.name)
                        Text(
                            "${person.eventTitle} · ${person.avatarInitials}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = onCancel, modifier = Modifier.size(width = 96.dp, height = 48.dp)) {
                    Text("Cancel")
                }
            }
        }
    }
}
