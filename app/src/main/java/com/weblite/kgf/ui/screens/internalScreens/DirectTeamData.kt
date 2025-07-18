package com.weblite.kgf.ui.screens.internalScreens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenu
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun DirectTeamData(
    onBackClick: () -> Unit = {},
    onShowTopBar: (Boolean) -> Unit,
    onShowBottomBar: (Boolean) -> Unit
) {
    val scrollState = rememberScrollState()
    var searchText by remember { mutableStateOf("") }
    var selectedLevel by remember { mutableStateOf("All") }
    var selectedDate by remember { mutableStateOf("") }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var showLoading by remember { mutableStateOf(false) }

    val allTeamMembers = listOf(
        TeamMember("9093226216", "L0", "1030.00", "20.00", "2025-01-28 11:02:14"),
        TeamMember("0196365812", "L1", "1100210.00", "20000.00", "2025-05-13 14:55:25"),
        TeamMember("9385520029", "L3", "20200.00", "200.00", "2025-05-22 14:06:10"),
        TeamMember("9385520029", "L3", "20200.00", "200.00", "2025-05-22 14:06:10"),
        TeamMember("9385520029", "L3", "20200.00", "200.00", "2025-05-22 14:06:10")
    )

    // Filtering
    val filteredList = allTeamMembers.filter {
        (searchText.isBlank() || it.uid.contains(searchText)) &&
                (selectedLevel == "All" || it.level == selectedLevel) &&
                (selectedDate.isBlank() || it.joinDate.startsWith(selectedDate))
    }

    val backgroundColor = Brush.verticalGradient(
        colors = listOf(Color(0xFF001B40), Color(0xFF002D60))
    )

    val levelOptions = listOf("All", "Level 0", "Level 1", "Level 2", "Level 3", "Level 4", "Level 5", "Level 6")
    var showLevelMenu by remember { mutableStateOf(false) }

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day)
            },
            2025, 0, 1
        )
    }
    LaunchedEffect(Unit) {
        onShowTopBar(true)
        onShowBottomBar(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundColor)
            .padding(16.dp)
    ) {
        // Top Bar
        TopBar(onBackClick)

        Spacer(modifier = Modifier.height(16.dp))

        // Search Field
        SearchBar(searchText) { searchText = it }

        Spacer(modifier = Modifier.height(12.dp))

        // Filters: Level + Date
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                FilterButton(
                    text = selectedLevel,
                    onClick = { showLevelMenu = true }
                )

                DropdownMenu(
                    modifier = Modifier.width(200.dp),
                    expanded = showLevelMenu,
                    onDismissRequest = { showLevelMenu = false }
                ) {
                    levelOptions.forEach { level ->
                        androidx.compose.material.DropdownMenuItem(
                            onClick = {
                                selectedLevel = level
                                showLevelMenu = false
                            }
                        ) {
                            Text(level,color = Color.Black)
                        }
                    }
                }

            }

            Spacer(modifier = Modifier.width(12.dp))

            Box(modifier = Modifier.weight(1f)) {
                FilterButton(
                    text = selectedDate.ifEmpty { "yyyy-mm-dd" },
                    onClick = { datePickerDialog.show() },
                    showIcon = true
                )
            }
        }



        Spacer(modifier = Modifier.height(16.dp))

        // Filtered List
        // Show loading or data
        if (showLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.CircularProgressIndicator(color = Color.White)
            }
        } else {
            if (filteredList.isEmpty()) {
                Text(
                    text = "No team data found.",
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.verticalScroll(scrollState)
                ) {
                    filteredList.forEach { member ->
                        TeamCard(
                            uid = member.uid,
                            level = member.level,
                            deposit = member.deposit,
                            commission = member.commission,
                            joinDate = member.joinDate
                        )
                    }
                }
            }
        }
    }
}

data class TeamMember(
    val uid: String,
    val level: String,
    val deposit: String,
    val commission: String,
    val joinDate: String
)


@Composable
fun TeamCard(uid: String, level: String, deposit: String, commission: String, joinDate: String) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFF007BFF), RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(12.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)) { append("UID: ") }
                    withStyle(SpanStyle(fontWeight = FontWeight.Normal)) { append(uid) }
                },
                color = Color.Black,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.width(2.dp))

            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy UID",
                tint = Color.DarkGray,
                modifier = Modifier
                    .clickable {
                        clipboardManager.setText(AnnotatedString(uid))
                        Toast.makeText(context, "UID copied to clipboard", Toast.LENGTH_SHORT).show()
                    }
                    .padding(4.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Level:", fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Text(level, fontSize = 13.sp, color = Color.Black)
        }

        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Deposit Amount:", fontSize = 14.sp, fontWeight = FontWeight.Bold,color = Color.Black)
            Spacer(modifier = Modifier.weight(1f))
            Text(deposit, fontSize = 13.sp, color = Color(0xFF007BFF), fontWeight = FontWeight.Medium)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Referral Commission:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.weight(1f))
            Text(commission, fontSize = 13.sp, color = Color(0xFF007BFF), fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Joining Date:", fontSize = 14.sp,color = Color.Black, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Text(joinDate, fontSize = 13.sp, color = Color.DarkGray)
        }
    }
}

@Composable
fun TopBar(onBackClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(32.dp))
            .border(2.dp, Color(0xFF29659A), RoundedCornerShape(32.dp))
            .background(Color.White)
            .padding(horizontal = 12.dp)
    ) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF007BFF))
        }
        Text(
            "Direct Team Data",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SearchBar(text: String, onTextChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(32.dp))
            .border(2.dp, Color(0xFF29659A), RoundedCornerShape(32.dp))
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(color = Color.Black, fontSize = 14.sp),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            if (text.isEmpty()) {
                Text("Search Direct Team Id", color = Color.Gray, fontSize = 14.sp)
            }
            it()
        }

        Box(
            modifier = Modifier
                .padding(4.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0xFF007BFF))
                .padding(10.dp)
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
        }
    }
}

@Composable
fun FilterButton(
    text: String,
    onClick: () -> Unit,
    showIcon: Boolean = false
) {
    Box(
        modifier = Modifier
            .height(45.dp)
            .fillMaxWidth() // Fill width within the parent Box
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, Color(0xFF29659A), RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                color = if (text == "yyyy-mm-dd") Color.Black else Color.Black
            )

            if (showIcon) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Calendar Icon",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(18.dp)
                )
            }
            else{
                Icon(Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}



@Preview
@Composable
fun DirectView(){
    //DirectTeamData()
}