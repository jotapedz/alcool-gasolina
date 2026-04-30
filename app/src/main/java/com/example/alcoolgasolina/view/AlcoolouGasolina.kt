package com.example.alcoolgasolina.view

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.example.alcoolgasolina.R
import com.example.alcoolgasolina.data.Coordenadas
import com.example.alcoolgasolina.data.FuelPreferencesRepository
import com.example.alcoolgasolina.data.Posto
import java.text.DateFormat
import java.text.NumberFormat
import java.util.Date
import java.util.Locale

private data class CalculationOutcome(
    val stationName: String?,
    val alcoholValue: Double,
    val gasolineValue: Double,
    val ratio: Double,
    val threshold: Double,
    val recommendAlcohol: Boolean
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FuelStationScreen(
    navController: NavHostController,
    repository: FuelPreferencesRepository
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var stationName by rememberSaveable { mutableStateOf("") }
    var alcoholPrice by rememberSaveable { mutableStateOf("") }
    var gasolinePrice by rememberSaveable { mutableStateOf("") }
    var useSeventyFivePercent by rememberSaveable { mutableStateOf(repository.useSeventyFivePercent()) }
    var calculationOutcome by remember { mutableStateOf<CalculationOutcome?>(null) }
    var feedback by rememberSaveable { mutableStateOf<String?>(null) }
    var postos by remember { mutableStateOf(repository.getPostos()) }
    var pendingSave by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.any { it }
        if (pendingSave) {
            if (granted) {
                savePosto(
                    context = context,
                    repository = repository,
                    stationName = stationName,
                    alcoholPrice = alcoholPrice,
                    gasolinePrice = gasolinePrice,
                    onSuccess = {
                        feedback = context.getString(R.string.station_saved)
                        stationName = ""
                        alcoholPrice = ""
                        gasolinePrice = ""
                        calculationOutcome = null
                        postos = repository.getPostos()
                    },
                    onError = { feedback = it }
                )
            } else {
                feedback = context.getString(R.string.location_permission_required_to_save)
            }
        }
        pendingSave = false
    }

    DisposableEffect(lifecycleOwner, repository) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                postos = repository.getPostos()
                useSeventyFivePercent = repository.useSeventyFivePercent()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = stringResource(R.string.app_name)) })
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.calculator_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Text(
                    text = stringResource(R.string.extended_home_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = alcoholPrice,
                            onValueChange = {
                                alcoholPrice = sanitizePriceInput(it)
                                calculationOutcome = null
                                feedback = null
                            },
                            label = { Text(text = stringResource(R.string.alcohol_price)) },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = gasolinePrice,
                            onValueChange = {
                                gasolinePrice = sanitizePriceInput(it)
                                calculationOutcome = null
                                feedback = null
                            },
                            label = { Text(text = stringResource(R.string.gasoline_price)) },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = stationName,
                            onValueChange = {
                                stationName = it
                                calculationOutcome = null
                                feedback = null
                            },
                            label = { Text(text = stringResource(R.string.station_name_optional)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            }

            item {
                RuleSelectorCard(
                    useSeventyFivePercent = useSeventyFivePercent,
                    onToggle = { enabled ->
                        useSeventyFivePercent = enabled
                        repository.saveUseSeventyFivePercent(enabled)
                        calculationOutcome = buildCalculationOutcome(
                            stationName = stationName,
                            alcoholPrice = alcoholPrice,
                            gasolinePrice = gasolinePrice,
                            threshold = currentThreshold(enabled)
                        )
                        feedback = null
                    }
                )
            }

            item {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            calculationOutcome = buildCalculationOutcome(
                                stationName = stationName,
                                alcoholPrice = alcoholPrice,
                                gasolinePrice = gasolinePrice,
                                threshold = currentThreshold(useSeventyFivePercent)
                            )

                            feedback = if (calculationOutcome == null) {
                                context.getString(R.string.invalid_prices)
                            } else {
                                null
                            }
                        }
                    ) {
                        Text(text = stringResource(R.string.calculate))
                    }

                    Button(
                        onClick = {
                            if (hasLocationPermission(context)) {
                                savePosto(
                                    context = context,
                                    repository = repository,
                                    stationName = stationName,
                                    alcoholPrice = alcoholPrice,
                                    gasolinePrice = gasolinePrice,
                                    onSuccess = {
                                        feedback = context.getString(R.string.station_saved)
                                        stationName = ""
                                        alcoholPrice = ""
                                        gasolinePrice = ""
                                        calculationOutcome = null
                                        postos = repository.getPostos()
                                    },
                                    onError = { feedback = it }
                                )
                            } else {
                                pendingSave = true
                                permissionLauncher.launch(locationPermissions)
                            }
                        }
                    ) {
                        Text(text = stringResource(R.string.save_station))
                    }

                    OutlinedButton(
                        onClick = {
                            stationName = ""
                            alcoholPrice = ""
                            gasolinePrice = ""
                            calculationOutcome = null
                            feedback = null
                        }
                    ) {
                        Text(text = stringResource(R.string.clear_fields))
                    }
                }
            }

            feedback?.let { message ->
                item {
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            calculationOutcome?.let { outcome ->
                item {
                    ResultCard(outcome = outcome)
                }
            }

            item {
                Text(
                    text = stringResource(R.string.saved_stations),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            item {
                Text(
                    text = stringResource(R.string.saved_stations_help),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (postos.isEmpty()) {
                item {
                    Text(text = stringResource(R.string.no_stations_saved))
                }
            } else {
                items(postos, key = { it.id }) { posto ->
                    StationCard(
                        posto = posto,
                        threshold = currentThreshold(useSeventyFivePercent),
                        modifier = Modifier.clickable {
                            navController.navigate("detail/${Uri.encode(posto.id)}")
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostoDetailView(
    navController: NavHostController,
    repository: FuelPreferencesRepository,
    postoId: String
) {
    val context = LocalContext.current
    val posto = remember(postoId) { repository.getPostoById(postoId) }

    if (posto == null) {
        Scaffold(
            topBar = { TopAppBar(title = { Text(text = stringResource(R.string.station_details)) }) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = stringResource(R.string.station_not_found))
                Button(onClick = { navController.popBackStack() }) {
                    Text(text = stringResource(R.string.back))
                }
            }
        }
        return
    }

    var stationName by rememberSaveable(posto.id) { mutableStateOf(posto.nome) }
    var alcoholPrice by rememberSaveable(posto.id) { mutableStateOf(formatInputPrice(posto.alcool)) }
    var gasolinePrice by rememberSaveable(posto.id) { mutableStateOf(formatInputPrice(posto.gasolina)) }
    var latitude by rememberSaveable(posto.id) { mutableStateOf(posto.latitude) }
    var longitude by rememberSaveable(posto.id) { mutableStateOf(posto.longitude) }
    var dataInformacaoMillis by rememberSaveable(posto.id) { mutableStateOf(posto.dataInformacaoMillis) }
    var feedback by rememberSaveable { mutableStateOf<String?>(null) }
    var pendingLocationUpdate by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.any { it }
        if (pendingLocationUpdate) {
            if (granted) {
                val location = resolveCurrentLocation(context)
                latitude = location?.latitude
                longitude = location?.longitude
                feedback = if (location != null) {
                    context.getString(R.string.location_updated)
                } else {
                    context.getString(R.string.location_unavailable)
                }
            } else {
                feedback = context.getString(R.string.location_permission_required)
            }
        }
        pendingLocationUpdate = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.station_details)) },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text(text = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = stationName,
                onValueChange = {
                    stationName = it
                    feedback = null
                },
                label = { Text(text = stringResource(R.string.station_name)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = alcoholPrice,
                onValueChange = {
                    alcoholPrice = sanitizePriceInput(it)
                    feedback = null
                },
                label = { Text(text = stringResource(R.string.alcohol_price)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )

            OutlinedTextField(
                value = gasolinePrice,
                onValueChange = {
                    gasolinePrice = sanitizePriceInput(it)
                    feedback = null
                },
                label = { Text(text = stringResource(R.string.gasoline_price)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )

            Text(text = stringResource(R.string.saved_at, formatDate(dataInformacaoMillis)))
            Text(text = stringResource(R.string.location_label, formatCoordinates(latitude, longitude)))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        val alcoholValue = parsePrice(alcoholPrice)
                        val gasolineValue = parsePrice(gasolinePrice)

                        when {
                            stationName.isBlank() -> feedback = context.getString(R.string.station_name_required)
                            alcoholValue == null || gasolineValue == null -> {
                                feedback = context.getString(R.string.invalid_prices)
                            }
                            else -> {
                                dataInformacaoMillis = System.currentTimeMillis()
                                repository.upsertPosto(
                                    posto.copy(
                                        nome = stationName.trim(),
                                        alcool = alcoholValue,
                                        gasolina = gasolineValue,
                                        latitude = latitude,
                                        longitude = longitude,
                                        dataInformacaoMillis = dataInformacaoMillis
                                    )
                                )
                                feedback = context.getString(R.string.station_updated)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.update_station))
                }

                Button(
                    onClick = {
                        repository.deletePosto(posto.id)
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.delete_station))
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        if (hasLocationPermission(context)) {
                            val location = resolveCurrentLocation(context)
                            latitude = location?.latitude
                            longitude = location?.longitude
                            feedback = if (location != null) {
                                context.getString(R.string.location_updated)
                            } else {
                                context.getString(R.string.location_unavailable)
                            }
                        } else {
                            pendingLocationUpdate = true
                            permissionLauncher.launch(locationPermissions)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.update_location))
                }

                Button(
                    onClick = {
                        openMap(
                            context = context,
                            stationName = stationName,
                            latitude = latitude,
                            longitude = longitude,
                            onUnavailable = {
                                feedback = context.getString(R.string.map_unavailable)
                            }
                        )
                    },
                    enabled = latitude != null && longitude != null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.open_map))
                }
            }

            feedback?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun RuleSelectorCard(
    useSeventyFivePercent: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.comparison_rule_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(R.string.comparison_rule_body),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(
                            if (useSeventyFivePercent) R.string.rule_seventy_five else R.string.rule_seventy
                        ),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.current_rule_description),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Switch(
                    modifier = Modifier.padding(start = 12.dp, end = 28.dp),
                    checked = useSeventyFivePercent,
                    onCheckedChange = onToggle
                )
            }
        }
    }
}

@Composable
private fun ResultCard(outcome: CalculationOutcome) {
    val bestFuel = stringResource(
        if (outcome.recommendAlcohol) R.string.alcohol else R.string.gasoline
    )
    val headline = outcome.stationName?.let {
        stringResource(R.string.best_option_for_station, bestFuel, it)
    } ?: stringResource(R.string.best_option, bestFuel)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.calculation_result_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = headline,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = stringResource(
                    R.string.result_ratio_summary,
                    formatRatio(outcome.ratio),
                    formatRatio(outcome.threshold)
                ),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stringResource(R.string.alcohol_value, formatCurrency(outcome.alcoholValue)),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stringResource(R.string.gasoline_value, formatCurrency(outcome.gasolineValue)),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun StationCard(
    posto: Posto,
    threshold: Double,
    modifier: Modifier = Modifier
) {
    val recommendAlcohol = posto.gasolina != 0.0 && posto.alcool / posto.gasolina <= threshold
    val bestFuel = stringResource(if (recommendAlcohol) R.string.alcohol else R.string.gasoline)

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = posto.nome, style = MaterialTheme.typography.titleMedium)
            Text(text = stringResource(R.string.alcohol_value, formatCurrency(posto.alcool)))
            Text(text = stringResource(R.string.gasoline_value, formatCurrency(posto.gasolina)))
            Text(text = stringResource(R.string.saved_at, formatDate(posto.dataInformacaoMillis)))
            Text(text = stringResource(R.string.station_card_best_option, bestFuel))
        }
    }
}

private fun savePosto(
    context: Context,
    repository: FuelPreferencesRepository,
    stationName: String,
    alcoholPrice: String,
    gasolinePrice: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val alcoholValue = parsePrice(alcoholPrice)
    val gasolineValue = parsePrice(gasolinePrice)

    when {
        stationName.isBlank() -> onError(context.getString(R.string.station_name_required))
        alcoholValue == null || gasolineValue == null || gasolineValue == 0.0 -> {
            onError(context.getString(R.string.invalid_prices))
        }
        else -> {
            val location = resolveCurrentLocation(context)
            repository.upsertPosto(
                Posto(
                    nome = stationName.trim(),
                    alcool = alcoholValue,
                    gasolina = gasolineValue,
                    latitude = location?.latitude,
                    longitude = location?.longitude,
                    dataInformacaoMillis = System.currentTimeMillis()
                )
            )
            onSuccess()
        }
    }
}

private fun buildCalculationOutcome(
    stationName: String,
    alcoholPrice: String,
    gasolinePrice: String,
    threshold: Double
): CalculationOutcome? {
    val alcoholValue = parsePrice(alcoholPrice)
    val gasolineValue = parsePrice(gasolinePrice)

    if (alcoholValue == null || gasolineValue == null || gasolineValue == 0.0) {
        return null
    }

    val ratio = alcoholValue / gasolineValue
    return CalculationOutcome(
        stationName = stationName.trim().takeIf { it.isNotBlank() },
        alcoholValue = alcoholValue,
        gasolineValue = gasolineValue,
        ratio = ratio,
        threshold = threshold,
        recommendAlcohol = ratio <= threshold
    )
}

private fun currentThreshold(useSeventyFivePercent: Boolean): Double {
    return if (useSeventyFivePercent) 0.75 else 0.70
}

private fun sanitizePriceInput(input: String): String {
    return input.filter { it.isDigit() || it == ',' || it == '.' }
}

private fun parsePrice(input: String): Double? = input.replace(',', '.').toDoubleOrNull()

private fun formatCurrency(value: Double): String {
    return NumberFormat.getCurrencyInstance(Locale.getDefault()).format(value)
}

private fun formatRatio(value: Double): String {
    return NumberFormat.getPercentInstance(Locale.getDefault()).apply {
        maximumFractionDigits = 0
    }.format(value)
}

private fun formatInputPrice(value: Double): String = String.format(Locale.US, "%.2f", value)

private fun formatDate(value: Long): String {
    return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(value))
}

private fun formatCoordinates(latitude: Double?, longitude: Double?): String {
    if (latitude == null || longitude == null) return "-"
    return String.format(Locale.US, "%.5f, %.5f", latitude, longitude)
}

private fun hasLocationPermission(context: Context): Boolean {
    return locationPermissions.any { permission ->
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
}

private fun resolveCurrentLocation(context: Context): Coordenadas? {
    if (!hasLocationPermission(context)) return null

    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null
    val providers = locationManager.getProviders(true)
    val bestLocation = providers
        .mapNotNull { provider -> runCatching { locationManager.getLastKnownLocation(provider) }.getOrNull() }
        .maxByOrNull(Location::getTime)

    return bestLocation?.let { Coordenadas(it.latitude, it.longitude) }
}

private fun openMap(
    context: Context,
    stationName: String,
    latitude: Double?,
    longitude: Double?,
    onUnavailable: () -> Unit
) {
    if (latitude == null || longitude == null) {
        onUnavailable()
        return
    }

    val encodedLabel = Uri.encode(stationName.ifBlank { "Posto" })
    val intents = listOf(
        Intent(Intent.ACTION_VIEW, Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude($encodedLabel)")),
        Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=$latitude,$longitude")),
        Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=$latitude,$longitude"))
    )

    intents.forEach { intent ->
        try {
            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            return
        } catch (_: ActivityNotFoundException) {
            // Try the next supported format.
        }
    }

    onUnavailable()
}

private val locationPermissions = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)
