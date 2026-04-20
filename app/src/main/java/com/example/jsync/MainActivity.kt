package com.example.jsync

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.jsync.core.helpers.manageToken
import com.example.jsync.core.helpers.prefDatastore
import com.example.jsync.presentation.AboutScreen
import com.example.jsync.presentation.AuthScreen
import com.example.jsync.presentation.HomeScreen
import com.example.jsync.presentation.SettingsScreen
import com.example.jsync.presentation.auth.AuthScreenViewModel
import com.example.jsync.presentation.home.AskAiViewModel
import com.example.jsync.presentation.home.MainViewModel
import com.example.jsync.presentation.home.TaskCompletionsViewModel
import com.example.jsync.presentation.home.TasksViewModel
import com.example.jsync.presentation.settings.SettingsScreen
import com.example.jsync.presentation.settings.SettingsViewModel
import com.example.jsync.ui.theme.JSyncTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject


class MainActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        val theme = runBlocking {
            prefDatastore(this@MainActivity).uiMode.first() ?: 2
        }
//        Log.d("first" , "Theme value is $theme")
//        when (theme) {
//            0 -> AppCompatDelegate.setDefaultNightMode(
//                AppCompatDelegate.MODE_NIGHT_NO
//            )
//            1-> AppCompatDelegate.setDefaultNightMode(
//                AppCompatDelegate.MODE_NIGHT_YES
//            )
//            else -> AppCompatDelegate.setDefaultNightMode(
//                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
//            )
//        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var systemTheme = remember {
                mutableStateOf(theme)
            }
            JSyncTheme(darkTheme = if (systemTheme.value == 0) false else if (systemTheme.value == 1) true else isSystemInDarkTheme()) {
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
                            val viewModel = koinViewModel<MainViewModel>()
                            val askAiViewModel = koinViewModel<AskAiViewModel>()
                            val tasksViewModel = koinViewModel<TasksViewModel>()
                            val taskCompletionsViewModel = koinViewModel<TaskCompletionsViewModel>()
                            com.example.jsync.presentation.home.HomeScreen(
                                viewModel = viewModel , askAiViewModel = askAiViewModel ,
                                tasksViewModel = tasksViewModel , taskCompletionsViewModel = taskCompletionsViewModel , onSettingsClicked = {
                                    mainBackStack.add(SettingsScreen)
                                } , onAboutClicked = {
                                    mainBackStack.add(AboutScreen)
                                }
                            )
                        }
                        entry<SettingsScreen> {
                            val viewModel = koinViewModel<SettingsViewModel>()
                            SettingsScreen(viewModel , systemTheme , onLogOut = {
                                mainBackStack.clear()
                                mainBackStack.add(AuthScreen)
                            }){
                               mainBackStack.removeLastOrNull()
                            }
                        }
                        entry<AboutScreen>{
                            com.example.jsync.presentation.about.AboutScreen {
                                mainBackStack.removeLastOrNull()
                            }
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
