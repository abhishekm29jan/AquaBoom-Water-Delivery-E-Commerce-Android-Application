package com.example.aquaboom.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aquaboom.ui.components.BottomNavBar
import com.example.aquaboom.R
import com.example.aquaboom.data.DarkModeManager
import com.example.aquaboom.model.Product
import com.example.aquaboom.viewmodel.CartViewModel
import com.example.aquaboom.viewmodel.ProductViewModel
import kotlinx.coroutines.delay
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.aquaboom.ui.components.Background

@Composable
fun HomeScreen(
    navController: NavHostController,
    cartViewModel: CartViewModel
) {
    val productViewModel: ProductViewModel = viewModel()
    val products by productViewModel.products.collectAsState()

    Scaffold(bottomBar = { BottomNavBar(navController) })
    { padding ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background Image
            Background()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                HomeTopBar()

                Spacer(modifier = Modifier.height(12.dp))

                SearchBar()

                Spacer(modifier = Modifier.height(16.dp))

                PromoBanner()

                Spacer(modifier = Modifier.height(16.dp))

                OurProducts(
                    products = products,
                    cartViewModel = cartViewModel
                )

                Spacer(modifier = Modifier.height(16.dp))

                AboutAndWhyUsSection()
            }
        }
    }
}

@Composable
fun HomeTopBar() {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 0..11 -> "Good Morning"
        in 12..17 -> "Good Afternoon"
        else -> "Good Evening"
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
    ) {
        // Greeting section
        Column(modifier = Modifier.align(Alignment.CenterStart))
        {
            Text(
                text = greeting,
                color = Color.White
            )
            Text(
                text = "Stay Hydrated 💧",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        // Notification icon (top right corner)
        IconButton(
            onClick = { },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = Color.White
            )
        }
    }
}

@Composable
fun SearchBar() {
    var searchText by remember { mutableStateOf("") }

    OutlinedTextField(
        value = searchText,
        onValueChange = {searchText = it},
        placeholder = {
            Text(
                "Search water bottles...",
                color = Color.White
            )
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = Color.White
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(30.dp),
        colors = OutlinedTextFieldDefaults.colors(

            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,

            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White.copy(alpha = 0.6f),

            focusedContainerColor = Color.White.copy(alpha = 0.7f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.5f),

            cursorColor = Color.Black
        )
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PromoBanner() {

    var banners by remember { mutableStateOf<List<String>>(emptyList()) }

    // 🔥 FETCH FROM FIREBASE
    LaunchedEffect(Unit) {
        com.google.firebase.firestore.FirebaseFirestore.getInstance()
            .collection("banners")
            .addSnapshotListener { snapshot, _ ->

                if (snapshot != null) {
                    banners = snapshot.documents.mapNotNull {
                        it.getString("imageUrl")
                    }
                }
            }
    }

    // 🚫 Prevent crash if empty
    if (banners.isEmpty()) {
        Text("Loading banners...", color = Color.White)
        return
    }

    val pagerState = rememberPagerState(pageCount = { banners.size })

    // 🔥 AUTO SLIDE
    LaunchedEffect(pagerState.currentPage) {
        kotlinx.coroutines.delay(3000)
        val nextPage = (pagerState.currentPage + 1) % banners.size
        pagerState.animateScrollToPage(nextPage)
    }

    Column {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2.22f)
        ) { page ->

            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {

                // 🔥 LOAD IMAGE FROM URL
                coil.compose.AsyncImage(
                    model = banners[page],
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(banners.size) { index ->
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(if (pagerState.currentPage == index) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index)
                                Color.White
                            else
                                Color.White.copy(alpha = 0.4f)
                        )
                )
            }
        }
    }
}

@Composable
fun OurProducts(
    products: List<Product>,
    cartViewModel: CartViewModel
) {
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = Int.MAX_VALUE / 2
    )
    val itemCount = Int.MAX_VALUE

    LaunchedEffect(listState) {
        while (true) {
            if (listState.isScrollInProgress) {
                // Wait until user stops
                snapshotFlow { listState.isScrollInProgress }
                    .collect { isScrolling ->
                        if (!isScrolling) return@collect
                    }

                delay(1200)
            }
            listState.scrollBy(1f)
            delay(16L)
        }
    }

    Column {
        Text(
            text = "Our Products",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 60.dp)
        ) {

            items(itemCount) { index ->

                val product = products[index % products.size] // 🔥 LOOP MAGIC

                BottleCard(product, cartViewModel)
            }
        }
    }
}

@Composable
fun BottleCard(
    product: Product,
    cartViewModel: CartViewModel
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val item = cartItems.firstOrNull { it.product.id == product.id }
    val darkMode = DarkModeManager.isDarkMode.value

    Card(
        modifier = Modifier
            .width(160.dp)
            .height(220.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        ),
        border = BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.25f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = product.image),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = product.name,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                maxLines = 2
            )

            Text(
                text = "₹${product.price}",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            if (item == null) {
                OutlinedButton(
                    onClick = { cartViewModel.addToCart(product) },
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(
                        1.dp,
                        if (darkMode) Color(0xFF272C2E) else Color(0xFF26658C)
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (darkMode) Color(0xFF272C2E) else Color(0xFF26658C),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                ) {
                    Text("ADD", fontSize = 12.sp)
                }

            } else {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (darkMode) Color(0xFF272C2E)
                            else Color(0xFF26658C)
                        )
                        .padding(vertical = 6.dp)
                ) {

                    Text(
                        "-",
                        color = Color.White,
                        modifier = Modifier
                            .clickable {
                                cartViewModel.decreaseQuantity(product)
                            }
                            .padding(6.dp)
                    )

                    Text(
                        text = item.quantity.toString(),
                        color = Color.White
                    )

                    Text(
                        "+",
                        color = Color.White,
                        modifier = Modifier
                            .clickable {
                                cartViewModel.addToCart(product)
                            }
                            .padding(6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AboutAndWhyUsSection() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // 🔷 ABOUT CARD
        GlassCard {
            Column {

                Text(
                    text = "About Aqua Boom",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "We deliver premium purified water right to your doorstep. Aqua Boom ensures safe, hygienic and reliable hydration with fast delivery and affordable pricing.",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }

        // 🔷 WHY US CARD
        GlassCard {
            Column {

                Text(
                    text = "Why Choose Us",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                PremiumWhyItem(Icons.Default.WaterDrop, "100% Purified & Safe Water")
                PremiumWhyItem(Icons.Default.LocalShipping, "Fast Doorstep Delivery")
                PremiumWhyItem(Icons.Default.Inventory2, "Multiple Bottle Sizes")
                PremiumWhyItem(Icons.Default.AttachMoney, "Affordable Pricing")
                PremiumWhyItem(Icons.Default.Star, "Trusted by Customers")

            }
        }
    }
}

@Composable
fun GlassCard(content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.08f)
        ),
        border = BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.15f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.padding(16.dp)
        ) {
            content()
        }
    }
}

@Composable
fun PremiumWhyItem(icon: ImageVector, text: String) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = text,
            color = Color.White,
            fontSize = 14.sp
        )
    }
}