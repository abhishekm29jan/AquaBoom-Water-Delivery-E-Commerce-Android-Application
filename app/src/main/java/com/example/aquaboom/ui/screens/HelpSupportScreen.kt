package com.example.aquaboom.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aquaboom.ui.components.Background

@Composable
fun HelpSupportScreen() {
    val context = LocalContext.current

    Scaffold(
        containerColor = Color.Transparent
    ) { paddingValues ->
        Background()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            FaqSection(
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            ContactSection(context)
        }
    }
}

@Composable
fun FaqSection(
    modifier: Modifier = Modifier
) {
    val faqs = listOf(
        "How do I place an order?" to
                "Browse products, select items, add to cart, and proceed to checkout.",

        "How can I track my order?" to
                "Open Orders section and tap your active order for live tracking.",

        "What payment methods are available?" to
                "UPI, cards, net banking, and Cash on Delivery are supported.",

        "Can I cancel my order?" to
                "Yes, before dispatch. After shipping, cancellation may not be possible.",

        "How long does delivery take?" to
                "Usually 30–60 minutes depending on your location.",

        "What if my order is delayed?" to
                "You can track it live or contact support for quick help.",

        "Can I modify my order?" to
                "No, but you can cancel and place a new order.",

        "Is there a minimum order value?" to
                "It depends on location and offers. It’s shown at checkout.",

        "How do I apply promo codes?" to
                "Apply promo codes during checkout to get discounts.",

        "How can I contact support?" to
                "Use email or call options available below."
    )

    var expandedIndex by remember { mutableStateOf(-1) }

    Column(modifier = modifier) {

        Text(
            text = "Frequently Asked Questions",
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {

            itemsIndexed(faqs) { index, faq ->

                val isExpanded = expandedIndex == index

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.07f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {

                    Column(
                        modifier = Modifier
                            .clickable {
                                expandedIndex = if (isExpanded) -1 else index
                            }
                            .padding(16.dp)
                    ) {

                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Text(
                                text = faq.first,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )

                            val rotation by animateFloatAsState(
                                targetValue = if (isExpanded) 180f else 0f,
                                label = ""
                            )

                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.rotate(rotation)
                            )
                        }

                        AnimatedVisibility(
                            visible = isExpanded,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {

                            Text(
                                text = faq.second,
                                color = Color.White,
                                fontSize = 14.sp,
                                lineHeight = 18.sp,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContactSection(context: Context) {

    Column {

        Text(text = "Contact Us", color = Color.White, fontWeight = FontWeight.SemiBold)

        Spacer(modifier = Modifier.height(10.dp))

        ContactItem(
            icon = Icons.Default.Email,
            title = "Email Support",
            subtitle = "support@aquaboom.com"
        ) {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:support@aquaboom.com")
            }
            context.startActivity(intent)
        }

        ContactItem(
            icon = Icons.Default.Call,
            title = "Call Us",
            subtitle = "+91 9876543210"
        ) {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:+919876543210")
            }
            context.startActivity(intent)
        }
    }
}

@Composable
fun ContactItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(0.08f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(icon, contentDescription = null, tint = Color.White)

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(title, color = Color.White)
                Text(subtitle, color = Color.White.copy(0.6f))
            }
        }
    }
}