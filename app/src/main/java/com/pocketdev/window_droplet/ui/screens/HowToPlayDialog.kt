package com.pocketdev.window_droplet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HowToPlayDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0x99000000)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.90f)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📖 গেমের নিয়মাবলী ও গাইড",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE0F2FE)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0F172A))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GuideItem(
                        icon = "👆",
                        title = "সহজ ও স্বাচ্ছন্দ্যময় কন্ট্রোল",
                        desc = "স্ক্রিনে আঙ্গুল রেখে ডানে বা বামে সরালে পানির ফোঁটাটি মসৃণভাবে সাথে সাথে অনুসরণ করবে।"
                    )
                    GuideItem(
                        icon = "💧",
                        title = "ছোট ফোঁটা সংগ্রহ (Merging)",
                        desc = "কাচে পড়ে থাকা পানির বিন্দুগুলো সংগ্রহ করলে ফোঁটা বড় হবে এবং গতি ও পয়েন্ট বৃদ্ধি পাবে।"
                    )
                    GuideItem(
                        icon = "💨",
                        title = "ধুলোবালির দাগ (Dust Patches)",
                        desc = "ধুলোবালির দাগে পানি শোষিত হয়ে ফোঁটা শুকিয়ে ছোট হতে থাকে। সাবধানে এড়িয়ে চলুন।"
                    )
                    GuideItem(
                        icon = "⚡",
                        title = "কাচের ফাটল (Glass Cracks)",
                        desc = "কাচের গভীর ফাটলে সরাসরি আঘাত লাগলে ফোঁটাটি ভেঙে বিলীন হয়ে যাবে।"
                    )
                    GuideItem(
                        icon = "🌀",
                        title = "তৈলাক্ত দাগ (Grease Spots)",
                        desc = "পিচ্ছিল বাষ্পে ফোঁটার গতি ভারসাম্য হারায়।"
                    )
                    GuideItem(
                        icon = "🏞️",
                        title = "এন্ডলেস গতিশীল পরিবেশ",
                        desc = "নির্দিষ্ট দূরত্ব পরপর ব্যাকগ্রাউন্ড ও পরিবেশ স্বয়ংক্রিয়ভাবে পরিবর্তিত হয়ে নতুন রূপ নেবে।"
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
                ) {
                    Text(
                        text = "বুঝেছি (Got It)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun GuideItem(icon: String, title: String, desc: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(text = icon, fontSize = 20.sp, modifier = Modifier.padding(end = 10.dp, top = 2.dp))
        Column {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF81D4FA)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                fontSize = 11.sp,
                color = Color(0xFFCBD5E1),
                lineHeight = 15.sp
            )
        }
    }
}
