package pk.arkrealestate.app.ui.screens.marketplace

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pk.arkrealestate.app.data.model.AreaUnit
import pk.arkrealestate.app.data.model.ListingPurpose
import pk.arkrealestate.app.data.model.PropertyType
import pk.arkrealestate.app.ui.components.ArkButton
import pk.arkrealestate.app.ui.components.ArkButtonVariant
import pk.arkrealestate.app.ui.components.ArkTextField
import pk.arkrealestate.app.ui.components.ValidationErrorBanner
import pk.arkrealestate.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitListingScreen(
    viewModel: MarketplaceViewModel,
    isUrdu: Boolean,
    onNavigateBack: () -> Unit,
    onSubmitSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val submitState by viewModel.submitState.collectAsState()
    val scrollState = rememberScrollState()

    var showCityDropdown by remember { mutableStateOf(false) }
    var showTypeDropdown by remember { mutableStateOf(false) }
    var showUnitDropdown by remember { mutableStateOf(false) }

    val cities = listOf("Lahore", "Karachi", "Islamabad", "Rawalpindi", "Faisalabad")
    val propertyTypes = listOf(
        PropertyType.HOUSE to if (isUrdu) "رہائشی مکان / ولا" else "Residential House / Villa",
        PropertyType.APARTMENT to if (isUrdu) "اپارٹمنٹ / فلیٹ" else "Apartment / Flat",
        PropertyType.RESIDENTIAL_PLOT to if (isUrdu) "رہائشی پلاٹ" else "Residential Plot",
        PropertyType.COMMERCIAL_PLOT to if (isUrdu) "کمرشل پلاٹ" else "Commercial Plot"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ArkCanvas)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // App Bar
        TopAppBar(
            title = {
                Text(
                    text = if (isUrdu) "پراپرٹی کا اندراج" else "Submit Property Listing",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = ArkNavyPrimary
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = ArkNavyPrimary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = ArkSurfaceWhite)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Error banner if any
            submitState.errorMessage?.let { error ->
                ValidationErrorBanner(message = error)
            }

            // Custodian Vetting Authority Notice Banner (Rule 17 & 19 Compliance)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = ArkNavyPrimary,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = ArkGoldAccent,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = ArkNavyPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isUrdu) "قانونی تصدیق کا نوٹس" else "Custodian Vetting Notice",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = ArkSurfaceWhite
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = ArkSurfaceWhite.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "Rule 17 & 19",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ArkGoldAccent,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (isUrdu)
                                "تمام نئی لسٹنگز عوام کو دکھانے سے پہلے قانونی کسٹوڈین کے زیر جائزہ رہیں گی۔ پلاٹ اور مکان کی جانچ مکمل ہونے کے بعد ہی ویریفائیڈ بیج جاری کیا جائے گا۔"
                            else
                                "ARK inspects allotment letters and title deeds against LDA, CDA, or KDA records before listings appear publicly in marketplace search.",
                            style = MaterialTheme.typography.bodySmall,
                            color = ArkSurfaceWhite.copy(alpha = 0.85f),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Purpose & Category Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = ArkSurfaceWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, ArkBorderSubtle)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (isUrdu) "مقصد اور نوعیت" else "Purpose & Property Category",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ArkNavyPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Purpose Toggle (For Sale / For Rent)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(ArkCanvas)
                            .padding(4.dp)
                    ) {
                        Surface(
                            onClick = { viewModel.onSubmitPurposeChanged(ListingPurpose.FOR_SALE) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            color = if (submitState.purpose == ListingPurpose.FOR_SALE) ArkNavyPrimary else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sell,
                                    contentDescription = null,
                                    tint = if (submitState.purpose == ListingPurpose.FOR_SALE) ArkSurfaceWhite else ArkTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isUrdu) "برائے فروخت" else "For Sale",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (submitState.purpose == ListingPurpose.FOR_SALE) ArkSurfaceWhite else ArkTextSecondary
                                )
                            }
                        }

                        Surface(
                            onClick = { viewModel.onSubmitPurposeChanged(ListingPurpose.FOR_RENT) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            color = if (submitState.purpose == ListingPurpose.FOR_RENT) ArkNavyPrimary else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Key,
                                    contentDescription = null,
                                    tint = if (submitState.purpose == ListingPurpose.FOR_RENT) ArkSurfaceWhite else ArkTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isUrdu) "برائے کرایہ" else "For Rent",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (submitState.purpose == ListingPurpose.FOR_RENT) ArkSurfaceWhite else ArkTextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Property Type Dropdown Box
                    Text(
                        text = if (isUrdu) "پراپرٹی کی قسم" else "Property Type",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = ArkTextSecondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Box {
                        Surface(
                            onClick = { showTypeDropdown = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = ArkCanvas,
                            border = androidx.compose.foundation.BorderStroke(1.dp, ArkBorderSubtle)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = propertyTypes.find { it.first == submitState.propertyType }?.second ?: submitState.propertyType.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = ArkNavyPrimary
                                )
                                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = ArkTextSecondary)
                            }
                        }

                        DropdownMenu(
                            expanded = showTypeDropdown,
                            onDismissRequest = { showTypeDropdown = false }
                        ) {
                            propertyTypes.forEach { (type, label) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        viewModel.onSubmitTypeChanged(type)
                                        showTypeDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Location Specifics Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = ArkSurfaceWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, ArkBorderSubtle)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (isUrdu) "مقام کی تفصیلات" else "Location Specifics",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ArkNavyPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // City Selection Box
                    Text(
                        text = if (isUrdu) "شہر" else "City",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = ArkTextSecondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Box {
                        Surface(
                            onClick = { showCityDropdown = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = ArkCanvas,
                            border = androidx.compose.foundation.BorderStroke(1.dp, ArkBorderSubtle)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = submitState.city, style = MaterialTheme.typography.bodyMedium, color = ArkNavyPrimary)
                                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = ArkTextSecondary)
                            }
                        }

                        DropdownMenu(
                            expanded = showCityDropdown,
                            onDismissRequest = { showCityDropdown = false }
                        ) {
                            cities.forEach { city ->
                                DropdownMenuItem(
                                    text = { Text(city) },
                                    onClick = {
                                        viewModel.onSubmitCityChanged(city)
                                        showCityDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    ArkTextField(
                        value = submitState.areaLocation,
                        onValueChange = { viewModel.onSubmitAreaLocationChanged(it) },
                        label = if (isUrdu) "سوسائٹی / علاقہ" else "Society / Sector (e.g. DHA Phase 6)",
                        placeholder = "e.g. DHA Phase 6, Sector J",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Pricing & Specifications Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = ArkSurfaceWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, ArkBorderSubtle)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (isUrdu) "قیمت اور رقبہ" else "Pricing & Specifications",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ArkNavyPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ArkTextField(
                        value = submitState.pricePkr,
                        onValueChange = { viewModel.onSubmitPriceChanged(it) },
                        label = if (isUrdu) "ڈیمانڈ / قیمت (پاکستانی روپے)" else "Demand / Price (PKR)",
                        placeholder = "e.g. 65000000",
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (submitState.pricePkr.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            color = ArkCanvas,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Formatted: ${MarketplaceViewModel.formatPkrPrice(submitState.pricePkr, submitState.purpose == ListingPurpose.FOR_RENT)}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = ArkGoldAccent,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Area value and unit row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ArkTextField(
                            value = submitState.areaValue,
                            onValueChange = { viewModel.onSubmitAreaValueChanged(it) },
                            label = if (isUrdu) "رقبہ" else "Area Size",
                            placeholder = "1",
                            modifier = Modifier.weight(1f)
                        )

                        Box(modifier = Modifier.weight(1f)) {
                            Column {
                                Text(
                                    text = if (isUrdu) "پیمائش" else "Unit",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ArkTextSecondary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    onClick = { showUnitDropdown = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    color = ArkCanvas,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ArkBorderSubtle)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = submitState.areaUnit.name, style = MaterialTheme.typography.bodyMedium, color = ArkNavyPrimary)
                                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = ArkTextSecondary)
                                    }
                                }

                                DropdownMenu(
                                    expanded = showUnitDropdown,
                                    onDismissRequest = { showUnitDropdown = false }
                                ) {
                                    listOf(AreaUnit.MARLA, AreaUnit.KANAL, AreaUnit.SQ_FT).forEach { unit ->
                                        DropdownMenuItem(
                                            text = { Text(unit.name) },
                                            onClick = {
                                                viewModel.onSubmitAreaUnitChanged(unit)
                                                showUnitDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Title & Description Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = ArkSurfaceWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, ArkBorderSubtle)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (isUrdu) "عنوان اور تفصیل" else "Title & Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ArkNavyPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ArkTextField(
                        value = submitState.title,
                        onValueChange = { viewModel.onSubmitTitleChanged(it) },
                        label = if (isUrdu) "پراپرٹی کا عنوان" else "Listing Title",
                        placeholder = "e.g. 1 Kanal Designer Modern Villa in DHA Phase 6",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    ArkTextField(
                        value = submitState.description,
                        onValueChange = { viewModel.onSubmitDescChanged(it) },
                        label = if (isUrdu) "مکمل تفصیلات" else "Detailed Description",
                        placeholder = "Provide clear details regarding fixtures, facing, and possession status...",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Submit Button
            ArkButton(
                text = if (isUrdu) "تصدیق کے لیے جمع کرائیں" else "Submit for Custodian Verification",
                onClick = {
                    viewModel.submitListing(onSuccess = onSubmitSuccess)
                },
                isLoading = submitState.isSubmitting,
                variant = ArkButtonVariant.PRIMARY,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
