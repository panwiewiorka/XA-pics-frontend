package xapics.app

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import coil.annotation.ExperimentalCoilApi
import dagger.hilt.android.AndroidEntryPoint
import xapics.app.ui.common.NavScreen
import xapics.app.ui.theme.XAPicsTheme

@AndroidEntryPoint
@ExperimentalCoilApi
class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
            ) {
                XAPicsTheme {
                    NavScreen()
                }
            }
        }
    }
}