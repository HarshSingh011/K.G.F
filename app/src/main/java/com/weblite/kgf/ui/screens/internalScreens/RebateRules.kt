package com.weblite.kgf.ui.screens.internalScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weblite.kgf.data.CustomFont.Companion.AptosFontNormal
import com.weblite.kgf.ui.components.AttractiveText
import java.nio.file.WatchEvent


@Composable
fun RebateRules(
    onBackClick: () -> Unit = {},
    onShowTopBar: (Boolean) -> Unit = {},
    onShowBottomBar: (Boolean) -> Unit = {}
){

    val scrollState = rememberScrollState()
    val backgroundColor = Brush.verticalGradient(
        colors = listOf(Color(0xFF001B40), Color(0xFF002D60))
    )

    LaunchedEffect(Unit) {
        onShowTopBar(true)
        onShowBottomBar(false)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundColor)
            .padding(16.dp)
            .verticalScroll(scrollState)
    ){

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth().height(50.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(32.dp),
                    ambientColor = Color(0xff00EBEF),
                    spotColor = Color(0xff00EBEF)
                )
                .clip(RoundedCornerShape(32.dp))
                .border(2.dp, Color(0xFF29659A), RoundedCornerShape(32.dp))
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF007BFF))
            }
            Text("Rebate Rules",
                fontWeight = FontWeight.Bold,
                lineHeight = 16.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Rules Title
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(32.dp),
                    ambientColor = Color(0xff00EBEF),
                    spotColor = Color(0xff00EBEF)
                )
                .clip(RoundedCornerShape(32.dp))
                .background(Color.White)
                .border(2.dp, Color(0xFF29659A), RoundedCornerShape(32.dp)),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(2.dp)
                    .border(2.dp, Color.Black, RoundedCornerShape(32.dp))
            ){
                Text(
                    text = "Rebate Ratio",
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = Color(0xFF000000),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 16.dp)
                        .align(alignment = Alignment.CenterHorizontally)
                )
            }


        }
        Spacer(modifier = Modifier.height(16.dp))
        RebateCard(levelNumber = 0)
        Spacer(modifier = Modifier.height(12.dp))
        RebateCard(levelNumber = 2)
        Spacer(modifier = Modifier.height(12.dp))
        RebateCard(levelNumber = 3)
        Spacer(modifier = Modifier.height(12.dp))
        RebateCard(levelNumber = 4)
        Spacer(modifier = Modifier.height(12.dp))
        RebateCard(levelNumber = 5)
        Spacer(modifier = Modifier.height(12.dp))
        RebateCard(levelNumber = 6)


    }
}

@Composable
fun RebateCard(levelNumber: Number =0){
    val Level0 =" Level lower-level commission rebate: 1%"
    val fontsize =16.sp
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier = Modifier.height(10.dp))
        AttractiveText(
            text = "Rebate level L$levelNumber",
            primaryColor = Color.White,
            shadowColor = Color(0xFF606060),
            fontSize = 18.sp,
            strokeWidth = 1.5f
        )
        Spacer(modifier = Modifier.height(10.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(14.dp),
                    ambientColor = Color(0xff00EBEF),
                    spotColor = Color(0xff00EBEF)
                )
                .clip(RoundedCornerShape(14.dp))
                .border(2.dp, Color(0xFF29659A), RoundedCornerShape(16.dp))
                .background(Color.White)

        ){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
                    .padding(2.dp)
                    .border(2.dp, Color.Black, RoundedCornerShape(14.dp))
            ){
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "1$Level0",
                    fontFamily = AptosFontNormal,
                    fontSize = fontsize,
                    color = Color(0xFF000000)
                )
                Text(
                    text = "2$Level0",
                    fontFamily = AptosFontNormal,
                    fontSize = fontsize,
                    color = Color(0xFF000000)
                )
                Text(
                    text = "3$Level0",
                    fontFamily = AptosFontNormal,
                    fontSize = fontsize,
                    color = Color(0xFF000000)
                )
                Text(
                    text = "4$Level0",
                    fontFamily = AptosFontNormal,
                    fontSize = fontsize,
                    color = Color(0xFF000000)
                )
                Text(
                    text = "5$Level0",
                    fontFamily = AptosFontNormal,
                    fontSize = fontsize,
                    color = Color(0xFF000000)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }


        }
    }
}

@Preview
@Composable
fun RebateView(){
    RebateRules( {  })
}