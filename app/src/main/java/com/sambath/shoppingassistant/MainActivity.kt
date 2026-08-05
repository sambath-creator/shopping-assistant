package com.sambath.shoppingassistant

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.sambath.shoppingassistant.data.PriceOption
import com.sambath.shoppingassistant.data.ScheduleSettings
import com.sambath.shoppingassistant.data.ShoppingItem
import com.sambath.shoppingassistant.data.ShoppingState
import com.sambath.shoppingassistant.notification.NotificationHelper
import com.sambath.shoppingassistant.ui.components.AccentGradient
import com.sambath.shoppingassistant.ui.components.GlassCard3D
import com.sambath.shoppingassistant.ui.components.GradientButton
import com.sambath.shoppingassistant.ui.components.rememberSpringPressState
import com.sambath.shoppingassistant.ui.components.springPress
import com.sambath.shoppingassistant.ui.theme.ShoppingAssistantTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationHelper.createChannel(this)
        setContent {
            ShoppingAssistantTheme {
                val viewModel: ShoppingViewModel = viewModel(
                    factory = ShoppingViewModel.factory(applicationContext)
                )
                val state by viewModel.state.collectAsState()
                RequestNotificationPermission()
                ShoppingAssistantApp(
                    state = state,
                    onAddItem = viewModel::addItem,
                    onRemoveItem = viewModel::removeItem,
                    onRenameItem = viewModel::renameItem,
                    onScheduleChange = viewModel::updateSchedule,
                    onRefresh = viewModel::refreshPrices
                )
            }
        }
    }
}

@Composable
private fun RequestNotificationPermission() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }
    LaunchedEffect(Unit) {
        if (!NotificationHelper.hasPermission(context)) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

@Composable
private fun ShoppingAssistantApp(
    state: ShoppingState,
    onAddItem: (String) -> Unit,
    onRemoveItem: (Long) -> Unit,
    onRenameItem: (Long, String) -> Unit,
    onScheduleChange: (ScheduleSettings) -> Unit,
    onRefresh: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFFF7FBFF),
                        Color(0xFFE6F1ED),
                        Color(0xFFD4E8E4),
                        Color(0xFFFFF4D9)
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0x402A9D8F),
                            Color.Transparent
                        ),
                        radius = 900f
                    )
                )
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Header(state = state, onRefresh = onRefresh) }
            item { ItemEntryCard(onAddItem = onAddItem) }
            item {
                ScheduleCard(
                    schedule = state.schedule,
                    onScheduleChange = onScheduleChange
                )
            }
            if (state.isRefreshing) {
                item {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp)),
                        trackColor = Color.White.copy(alpha = 0.4f)
                    )
                }
            }
            items(state.items, key = { it.id }) { item ->
                ItemCard(
                    item = item,
                    onRemoveItem = onRemoveItem,
                    onRenameItem = onRenameItem
                )
            }
        }
    }
}

@Composable
private fun Header(state: ShoppingState, onRefresh: () -> Unit) {
    GlassCard3D(pressable = false) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Baker Basket",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${state.items.size} items tracked",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
                )
                Text(
                    text = state.lastRunSummary,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            GradientButton(
                onClick = onRefresh,
                enabled = !state.isRefreshing
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Run")
            }
        }
    }
}

@Composable
private fun ItemEntryCard(onAddItem: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    val addPress = rememberSpringPressState()

    GlassCard3D(pressable = false) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                label = { Text("Add baking item") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )
            Spacer(Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .springPress(state = addPress) {
                        onAddItem(text)
                        text = ""
                    }
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(AccentGradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add item",
                    tint = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ScheduleCard(
    schedule: ScheduleSettings,
    onScheduleChange: (ScheduleSettings) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    GlassCard3D(pressable = false) {
        val headerPress = rememberSpringPressState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .springPress(state = headerPress) { expanded = !expanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        "Agent schedule",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        schedule.display,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Collapse schedule" else "Expand schedule",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(animationSpec = tween(220)),
            exit = shrinkVertically(animationSpec = tween(180))
        ) {
            Column {
                Spacer(Modifier.height(14.dp))
                Text(
                    "Frequency",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(1, 2, 4).forEach { frequency ->
                        InputChip(
                            selected = schedule.frequencyWeeks == frequency,
                            onClick = { onScheduleChange(schedule.copy(frequencyWeeks = frequency)) },
                            label = { Text("${frequency}w") }
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    "Day",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    days.forEachIndexed { index, day ->
                        FilterChip(
                            selected = schedule.dayOfWeek == index + 1,
                            onClick = { onScheduleChange(schedule.copy(dayOfWeek = index + 1)) },
                            label = { Text(day) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    NumberField(
                        value = schedule.hour,
                        label = "Hour",
                        range = 0..23,
                        modifier = Modifier.weight(1f),
                        onValueChange = { onScheduleChange(schedule.copy(hour = it)) }
                    )
                    NumberField(
                        value = schedule.minute,
                        label = "Minute",
                        range = 0..59,
                        modifier = Modifier.weight(1f),
                        onValueChange = { onScheduleChange(schedule.copy(minute = it)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NumberField(
    value: Int,
    label: String,
    range: IntRange,
    modifier: Modifier = Modifier,
    onValueChange: (Int) -> Unit
) {
    OutlinedTextField(
        value = value.toString().padStart(2, '0'),
        onValueChange = { typed ->
            typed.toIntOrNull()?.coerceIn(range)?.let(onValueChange)
        },
        modifier = modifier,
        label = { Text(label) },
        singleLine = true,
        shape = RoundedCornerShape(14.dp)
    )
}

@Composable
private fun ItemCard(
    item: ShoppingItem,
    onRemoveItem: (Long) -> Unit,
    onRenameItem: (Long, String) -> Unit
) {
    val drafts = remember { mutableStateMapOf<Long, String>() }
    val draft = drafts[item.id] ?: item.name

    GlassCard3D(pressable = true) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(86.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = draft,
                    onValueChange = { drafts[item.id] = it },
                    label = { Text("Item") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = { onRenameItem(item.id, draft) }) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Save",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { onRemoveItem(item.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Remove ${item.name}")
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        if (item.options.isEmpty()) {
            Text(
                "Run the agent to fetch the three cheapest options.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item.options.forEachIndexed { index, option ->
                    PriceRow(rank = index + 1, option = option)
                }
            }
        }
    }
}

@Composable
private fun PriceRow(rank: Int, option: PriceOption) {
    val context = LocalContext.current
    val rowPress = rememberSpringPressState(restElevation = 4.dp, pressedElevation = 1.dp)

    Surface(
        color = Color.White.copy(alpha = 0.55f),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .springPress(state = rowPress) {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(option.url)))
            },
        shadowElevation = rowPress.elevation()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("#$rank ${option.store}", fontWeight = FontWeight.SemiBold)
                Text(
                    option.note,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                option.price,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(onClick = {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(option.url)))
            }) {
                Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = "Open ${option.store}")
            }
        }
    }
}
