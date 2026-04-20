package com.example.jsync.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jsync.core.helpers.ui.BinaryDialog
import com.example.jsync.ui.theme.blue20
import com.example.jsync.ui.theme.blue40
import com.example.jsync.ui.theme.blue80

//@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel, systemTheme : MutableState<Int>, onLogOut : () -> Unit, onBack : () -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxSize() , topBar = {
            TopAppBar(title = {
               Text(
                   text="JSync"
               )
            } , navigationIcon = {
                IconButton(
                    onClick = onBack
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft , contentDescription = "back"
                    )
                }
            } ,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = blue80 , titleContentColor = if (isSystemInDarkTheme()) Color.White else Color.Black  ,
                    actionIconContentColor = if (isSystemInDarkTheme()) Color.White else Color.Black
                )
                )

        }
    ) { ip ->
        val uiMode by viewModel.uiMode.collectAsStateWithLifecycle()
        val uiModes = listOf(
            UIModeOption(icon = Icons.Default.LightMode , text = "Light") ,
            UIModeOption(icon = Icons.Default.DarkMode , text = "Dark") ,
            UIModeOption(icon = Icons.Default.AutoMode , text = "System Default"))
        var showLogoutDialog by remember {
            mutableStateOf(false)
        }
        if (showLogoutDialog){
            BinaryDialog(
                mainText = "Are you want to logout?",
                firstButtonText = "Cancel" , secondButtonText = "LogOut",
                onFirstClick = {
                    showLogoutDialog = false
                } , onSecondClick = {
                     viewModel.onLogOut()
                     onLogOut()
                }
            )
        }
        Column(
            modifier = Modifier.fillMaxSize().padding(ip)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(
                    horizontal = 6.dp , vertical = 8.dp
                )
            ) {
                Text(
                    text = "Modes"  , fontSize = 24.sp , fontWeight = FontWeight.SemiBold , color = blue80
                )

            }
            LazyColumn(
                modifier = Modifier , verticalArrangement = Arrangement.spacedBy(
                    4.dp
                )
            ) {
                itemsIndexed(uiModes){ i , mode ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(
                            horizontal = 6.dp
                        ) , verticalAlignment = Alignment.CenterVertically , horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = mode.icon, contentDescription = "uiMode"
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = mode.text , fontSize = 20.sp
                            )
                        }
                        RadioButton(
                            selected = i == uiMode , onClick = {
                                viewModel.changeUiMode(i)
                                systemTheme.value = i
                            } , colors = RadioButtonDefaults.colors(
                                selectedColor = blue40 , unselectedColor = blue20
                            )

                        )
                    }
                }
            }
            Spacer(
                modifier = Modifier.height(8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(
                    horizontal = 12.dp , vertical = 8.dp
                )
            ) {
                Text(
                    text = "LogOut" , fontSize = 20.sp , fontWeight = FontWeight.SemiBold , color = blue80 , modifier = Modifier.clickable{
                     showLogoutDialog = true
                    }
                )
            }
        }

    }
}
private data class UIModeOption(
    val icon : ImageVector , val text : String

)