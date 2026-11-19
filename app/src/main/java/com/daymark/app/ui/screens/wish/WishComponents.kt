package com.daymark.app.ui.screens.wish

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daymark.app.data.WishTone
import com.daymark.app.model.Person
import com.daymark.app.ui.theme.Canvas
import com.daymark.app.ui.theme.Lavender
import com.daymark.app.ui.theme.Line
import com.daymark.app.ui.theme.Muted
import com.daymark.app.ui.theme.Purple

@Composable
fun WishHeader(person: Person, eventDate: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Lavender),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = person.avatarInitials,
                color = Purple,
                fontWeight = FontWeight.Bold,
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = person.name,
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (person.isPrivate) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Private event",
                        tint = Muted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Text(
                text = "${person.eventTitle} · $eventDate",
                color = Muted,
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
            )
            person.phoneNumber?.let {
                Text(it, color = Muted, style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun WishToneChips(
    selectedTone: WishTone,
    onToneSelected: (WishTone) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        WishTone.entries.forEach { tone ->
            FilterChip(
                selected = tone == selectedTone,
                onClick = { onToneSelected(tone) },
                label = { Text(tone.label) },
                modifier = Modifier.defaultMinSize(minHeight = 48.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Purple,
                    selectedLabelColor = Color.White,
                    containerColor = Color.White,
                    labelColor = Purple
                )
            )
        }
    }
}

@Composable
fun WishMessageCard(
    message: String,
    isEditing: Boolean,
    onMessageChanged: (String) -> Unit,
    focusRequester: FocusRequester?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Line)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Your message",
                color = Muted,
                style = androidx.compose.material3.MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = message,
                onValueChange = onMessageChanged,
                readOnly = !isEditing,
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier),
                textStyle = androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                ),
                shape = RoundedCornerShape(14.dp),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Purple,
                    unfocusedBorderColor = Line,
                    unfocusedContainerColor = Canvas,
                    focusedContainerColor = Canvas
                )
            )
        }
    }
}

@Composable
fun WishFooter(
    isEditing: Boolean,
    onSend: () -> Unit,
    onEdit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onSend,
            modifier = Modifier.weight(1.25f).height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Purple)
        ) {
            Text("Send")
        }
        OutlinedButton(
            onClick = onEdit,
            modifier = Modifier.weight(1f).height(48.dp),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Line)
        ) {
            Text(if (isEditing) "Done" else "Edit", color = Purple)
        }
        TextButton(
            onClick = onCancel,
            modifier = Modifier.weight(1f).height(48.dp)
        ) {
            Text("Cancel", color = Muted)
        }
    }
}
