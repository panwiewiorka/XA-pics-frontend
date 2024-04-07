package xapics.app.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xapics.app.ui.MainViewModel

@Composable
fun AdminScreen(
    viewModel: MainViewModel,
    goToEditFilmsScreen: () -> Unit,
    goToEditRollsScreen: () -> Unit,
    goToUploadPicsScreen: () -> Unit,
){
    val context = LocalContext.current

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1f))

            TextButton(
                onClick = {
                    viewModel.getFilmsList()
                    goToEditFilmsScreen()
                },
                modifier = Modifier.offset((-32).dp, 0.dp)
            ) {
                Text("Edit films", fontSize = 18.sp,)
            }

            TextButton(
                onClick = {
                    viewModel.getRollsList()
                    goToEditRollsScreen()
                },
            ) {
                Text("Edit rolls", fontSize = 18.sp)
            }

            TextButton(
                onClick = {
                    viewModel.getRollsList()
                    goToUploadPicsScreen()
                },
                modifier = Modifier.offset(32.dp, 0.dp)
            ) {
                Text("Upload pics", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            TextButton(
                onClick = {
                    viewModel.downloadBackup(context)
                },
                modifier = Modifier
            ) {
                Text("Download backup", fontSize = 18.sp)
            }
        }
    }
}