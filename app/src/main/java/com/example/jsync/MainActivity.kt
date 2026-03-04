package com.example.jsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.jsync.core.helpers.manageToken
import com.example.jsync.presentation.AuthScreen
import com.example.jsync.presentation.HomeScreen
import com.example.jsync.presentation.auth.AuthScreenViewModel
import com.example.jsync.ui.theme.JSyncTheme
import kotlinx.coroutines.runBlocking
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject


class MainActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JSyncTheme {
                Scaffold { ip ->
                    val token = koinInject<manageToken>().getRefreshToken()
                    val mainBackStack =
                        rememberNavBackStack(if (token == null) AuthScreen else HomeScreen)
                    NavDisplay(backStack = mainBackStack, entryProvider = entryProvider {
                        entry<AuthScreen> {
                            val viewModel = koinViewModel<AuthScreenViewModel>()
                            com.example.jsync.presentation.auth.AuthScreen(viewModel){
                                mainBackStack.add(HomeScreen)
                            }
                        }
                        entry<HomeScreen> {
                            Text(
                                text = "Home Screen"
                            )
                        }
                    })
                }
            }
        }
    }
}
@Preview
@Composable
fun Prev(modifier: Modifier = Modifier) {
    Text(text = "Hello world" , modifier = Modifier.padding(16.dp))
}
