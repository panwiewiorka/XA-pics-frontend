package xapics.app.ui.auth

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import xapics.app.ui.AppState
import xapics.app.ui.MainViewModel
import xapics.app.ShowHide
import xapics.app.Tag
import xapics.app.TagState.ENABLED
import xapics.app.TagState.SELECTED
import xapics.app.capitalize
import xapics.app.data.PicsApi.Companion.BASE_URL
import xapics.app.toTagsList
import xapics.app.ui.composables.AsyncPic
import xapics.app.ui.composables.PicTag
import java.io.File
import java.io.InputStream

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun UploadPicsScreen(
    viewModel: MainViewModel,
    appState: AppState,
    goToAuthScreen: () -> Unit,
) {
    var picUrl by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var keywords by rememberSaveable { mutableStateOf("") }
    var year by rememberSaveable { mutableStateOf("") }
    var hashtags by rememberSaveable { mutableStateOf(appState.tags.filter{ it.type == "hashtag" }) }
    var showAddHashtagField by rememberSaveable { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val keywordsFocusRequester = remember { FocusRequester() }
    val yearFocusRequester = remember { FocusRequester() }
    val alertFocusRequester = remember { FocusRequester() }
    val scrollState = rememberScrollState()

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val myResolver = context.contentResolver

    fun File.copyInputStreamToFile(inputStream: InputStream) {
        this.outputStream().use { fileOut ->
            inputStream.copyTo(fileOut)
        }
    }

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            picUrl = uri.toString()
            imageUri = uri
        }
    )

    if (appState.connectionError.isShown) {
        Toast.makeText(
            context,
            "Error",
            Toast.LENGTH_SHORT
        ).show()

        viewModel.showConnectionError(ShowHide.HIDE)
    }

    /* for future "Delete Pic" feature
    LaunchedEffect(appState.picsList?.size) {
        appState.picsList?.let {
            val pic = appState.picsList.last()
            val picTags = pic.tags.toTagsList()
            picUrl = pic.imageUrl
            description = pic.description
            year = picTags.firstOrNull { it.type == "year" }?.value ?: ""

            val picHashtagValues = picTags.filter { it.type == "hashtag" }.map { it.value }
            hashtags.forEach {
                if (picHashtagValues.contains(it.value)) it.state = SELECTED else it.state = ENABLED
            }
        }
    }
     */

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .padding(vertical = 16.dp)
            .verticalScroll(scrollState)
            .blur(if (showAddHashtagField) 10.dp else 0.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }) { focusManager.clearFocus() }
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RollSelector(
                shouldOpenMenu = appState.rollToEdit == null,
                onRollsPage = false,
                selectRollToEdit = viewModel::selectRollToEdit,
                search = viewModel::search,
                clearPicsList = viewModel::clearPicsList,
                rollsList = appState.rollsList
            )

            Text(text = appState.rollToEdit?.title ?: "", fontSize = 18.sp)
        }

        appState.rollToEdit?.let {
            if (!appState.picsList.isNullOrEmpty()) {
                BoxWithConstraints {
                    val width = maxWidth
                    FlowRow(
                        modifier = Modifier.padding(vertical = 16.dp)
                    ) {
                        appState.picsList.forEachIndexed { index, pic ->
                            AsyncPic(
                                url = pic.imageUrl,
                                description = pic.description,
                                modifier = Modifier
                                    .width(width / 4)
                                    .height(width / 6)
                            ) {
                                viewModel.updatePicState(index)

                                val picTags = pic.tags.toTagsList()

                                picUrl = BASE_URL + "files/images/" + pic.imageUrl
                                description = pic.description
                                keywords = pic.keywords
                                year = picTags.firstOrNull { it.type == "year" }?.value ?: ""

                                val picHashtagValues = picTags.filter { it.type == "hashtag" }.map { it.value }
                                hashtags.forEach {
                                    if (picHashtagValues.contains(it.value)) it.state = SELECTED else it.state = ENABLED
                                }
                            }
                        }
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(horizontal = 32.dp)
            ) {
                Text("Choose pic to edit")

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("or", modifier = Modifier.padding(end = 6.dp))

                    Button(
                        onClick = {
                            pickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    ) {
                        Text("Choose pic for upload")
                    }
                }

                if (picUrl.isNotEmpty()) {
                    AsyncImage(model = picUrl, contentDescription = null)

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it.capitalize() },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Description") },
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { keywordsFocusRequester.requestFocus() }
                        )
                    )

                    OutlinedTextField(
                        value = keywords,
                        onValueChange = { keywords = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(keywordsFocusRequester),
                        label = { Text(text = "Search keywords") },
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { yearFocusRequester.requestFocus() }
                        )
                    )

                    OutlinedTextField(
                        value = year,
                        onValueChange = { year = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(yearFocusRequester),
                        label = { Text(text = "Year") },
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                    )

                    FlowRow{
                        hashtags.forEachIndexed { index, tag ->
                            PicTag(tag) {
                                hashtags[index].state = if (tag.state == SELECTED) ENABLED else SELECTED
                                val tempList = hashtags
                                hashtags = emptyList()
                                hashtags = tempList
                            }
                        }

                        PicTag(tag = Tag("hashtag", "    + + +    ")) {
                            showAddHashtagField = true
                        }
                    }

                    val fieldsAreEmpty = description.isEmpty() || year.isEmpty() || appState.rollToEdit.title.isEmpty()

                    Row (
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                        ) {
                            Button(
                                enabled = !appState.isLoading && picUrl.contains(".jpg"),
                                onClick = {
                                    if (fieldsAreEmpty) {
                                        Toast.makeText(context, "Fill in the text fields", Toast.LENGTH_SHORT).show()
                                    } else {
                                        viewModel.editPic(
                                            appState.pic!!,
                                            year,
                                            description,
                                            keywords,
                                            hashtags,
                                            goToAuthScreen
                                        )

//                                        description = ""
//                                        keywords = ""
                                    }
                                }
                            ) {
                                Text("Save pic")
                            }

                            if (appState.isLoading && picUrl.contains(".jpg")) CircularProgressIndicator()
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                        ) {
                            Button(
                                enabled = !appState.isLoading && picUrl.contains("content://"),
                                onClick = {
                                    if (fieldsAreEmpty) {
                                        Toast.makeText(context, "Fill in the text fields", Toast.LENGTH_SHORT).show()
                                    } else {
                                        val myStream = imageUri?.let { myResolver.openInputStream(it) }
                                        val myFile = File.createTempFile("image.jpg", null, context.cacheDir)

                                        if (myStream != null) {
                                            myFile.copyInputStreamToFile(myStream)
                                            myStream.close()
                                        }

                                        viewModel.uploadImage(
                                            rollTitle = appState.rollToEdit.title,
                                            description = description,
                                            keywords = keywords,
                                            year = year,
                                            hashtags = hashtags.filter { it.state == SELECTED }.map { it.value }.toString().drop(1).dropLast(1), // TODO move into repository
                                            file = myFile,
                                            goToAuthScreen = goToAuthScreen
                                        )

                                        description = ""
                                        keywords = ""
                                    }
                                }
                            ) {
                                Text("Upload pic")
                            }

                            if (appState.isLoading && picUrl.contains("content://")) CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }

    if (showAddHashtagField) {
        BasicAlertDialog(onDismissRequest = { showAddHashtagField = false }) {
            var tags by rememberSaveable { mutableStateOf("") }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("#, #, ...") },
                    modifier = Modifier.focusRequester(alertFocusRequester)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    enabled = tags.isNotEmpty(),
                    onClick = {
                        val newHashtags = tags.split(',').map { Tag("hashtag", it.trim(), SELECTED) }
                        hashtags += newHashtags
                        showAddHashtagField = false
                    }
                ) {
                    Text(text = "Add hashtags")
                }
            }

            LaunchedEffect(Unit) {
                alertFocusRequester.requestFocus()
            }
        }
    }
}