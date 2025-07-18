package com.mohit.kgfindia.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weblite.kgf.R
import com.weblite.kgf.data.RecentItem
import com.weblite.kgf.utils.AssetImage

// recent contents
@Composable
fun RecentSection(recentItems: List<RecentItem>) {
    Column(modifier = Modifier.padding(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.history),
                contentDescription = "recent",
                modifier = Modifier.size(22.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )
            Text(
                text = "Recent",
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )
        }

        val grouped = recentItems.chunked(2)

        LazyRow(
            contentPadding = PaddingValues(horizontal = 1.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(grouped) { group ->
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    group.forEach { item ->
                        RecentItemCard(item)
                    }
                }
            }
        }
    }
}

@Composable
fun RecentItemCard(item: RecentItem) {
    // Calculate dynamic width based on screen size
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val cardWidth = (screenWidth - 32.dp - 12.dp) / 2

    Column(
        modifier = Modifier
            .width(cardWidth)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .width(cardWidth - 8.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(10.dp)),
            color = Color.Gray
        ) {
            AssetImage(
                assetPath = item.imageResId,
                contentDescription = "Banner",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
        }

        Text(
            text = item.name,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Default,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
// hot contents
@Composable
fun HotSection(recentItems: List<RecentItem>) {
    Column(modifier = Modifier.padding(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.ic_fire),
                contentDescription = "Hot",
                modifier = Modifier.size(22.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )
            Text(
                text = "Hot",
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
            )
        }

        val grouped = recentItems.chunked(2)

        LazyRow(
            contentPadding = PaddingValues(horizontal = 1.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(grouped) { group ->
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    group.forEach { item ->
                        HotItemCard(item)
                    }
                }
            }
        }
    }
}

@Composable
fun HotItemCard(item: RecentItem) {
    // Get the screen width minus padding and spacing
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val cardWidth = (screenWidth - 32.dp - 12.dp) / 2

    Column(
        modifier = Modifier
            .width(cardWidth)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .width(cardWidth - 8.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(10.dp)),
            color = Color.Gray
        ) {
            AssetImage(
                assetPath = item.imageResId,
                contentDescription = "Banner",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
        }

        Text(
            text = item.name,
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
// lottery contents
@Composable
fun LotterySection(recentItems: List<RecentItem>) {
    Column(modifier = Modifier.padding(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.ic_lottery_balls),
                contentDescription = "Lottery",
                modifier = Modifier.size(22.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )
            Text(
                text = "Lottery",
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
            )
        }

        val grouped = recentItems.chunked(2)

        LazyRow(
            contentPadding = PaddingValues(horizontal = 1.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(grouped) { group ->
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    group.forEach { item ->
                        LotteryItemCard(item)
                    }
                }
            }
        }
    }
}

@Composable
fun LotteryItemCard(item: RecentItem) {
    // Calculate dynamic width based on screen size
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val cardWidth = (screenWidth - 32.dp - 12.dp) / 2

    Column(
        modifier = Modifier
            .width(cardWidth)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .width(cardWidth - 8.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(10.dp)),
            color = Color.Gray
        ) {
            AssetImage(
                assetPath = item.imageResId,
                contentDescription = "Banner",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
        }

        Text(
            text = item.name,
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
// favorite contents
@Composable
fun FavoriteSection(recentItems: List<RecentItem>) {
    Column(modifier = Modifier.padding(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Favorite",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = "Favorite",
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
            )
        }

        val grouped = recentItems.chunked(2)

        LazyRow(
            contentPadding = PaddingValues(horizontal = 1.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(grouped) { group ->
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    group.forEach { item ->
                        FavoriteItemCard(item)
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteItemCard(item: RecentItem) {
    // Calculate dynamic width based on screen size
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val cardWidth = (screenWidth - 32.dp - 12.dp) / 2

    Column(
        modifier = Modifier
            .width(cardWidth)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .width(cardWidth - 8.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(10.dp)),
            color = Color.Gray
        ) {
            AssetImage(
                assetPath = item.imageResId,
                contentDescription = "Banner",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
        }

        Text(
            text = item.name,
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}



