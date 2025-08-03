package com.aiwazian.messenger.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.aiwazian.messenger.R
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwazian.messenger.viewModels.UserSearchViewModel
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.utils.LottieAnimation
import com.aiwazian.messenger.viewModels.NavigationViewModel
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(viewModel: UserSearchViewModel = viewModel()) {
    val navViewModel: NavigationViewModel = viewModel()
    val users = viewModel.searchResults

    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopBar(viewModel, keyboardController)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (users.isEmpty()) {
                if (viewModel.query.isEmpty()) {
                    return@Column
                }

                Column(
                    modifier = Modifier
                        .imePadding()
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val composition by rememberLottieComposition(
                        spec = LottieCompositionSpec.Asset(LottieAnimation.SEARCH_OUT)
                    )

                    LottieAnimation(
                        composition = composition,
                        modifier = Modifier
                            .size(100.dp)
                            .padding(bottom = 10.dp),
                        iterations = LottieConstants.IterateForever,
                        isPlaying = true
                    )

                    Text(
                        text = "Нет результатов",
                        textAlign = TextAlign.Center,
                    )
                }

                return@Column
            }

            LazyColumn {
                items(users) { user ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RectangleShape,
                        onClick = {
                            keyboardController?.hide()
                            navViewModel.addScreenInStack {
                                ChatScreen(user.id)
                            }
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent
                        ),
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = user.firstName,
                                modifier = Modifier.padding(bottom = 4.dp),
                            )

                            val username = user.username
                            if (username == null) {
                                return@Column
                            }
                            val query = viewModel.query

                            val startIndex = username.indexOf(query)
                            val endIndex = startIndex + query.length

                            Text(
                                text = buildAnnotatedString {
                                    if (startIndex in username.indices) {
                                        append("@" + username.substring(0, startIndex))

                                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                            append(username.substring(startIndex, endIndex))
                                        }

                                        append(username.substring(endIndex))
                                    } else {
                                        append("@$username")
                                    }
                                },
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    userSearch: UserSearchViewModel,
    keyboardController: SoftwareKeyboardController?,
) {
    val navViewModel: NavigationViewModel = viewModel()

    PageTopBar(
        title = {
            SearchTextField(userSearch, keyboardController)
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    navViewModel.removeLastScreenInStack()
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = null,
                )
            }
        }
    )
}

@Composable
private fun SearchTextField(
    userSearch: UserSearchViewModel,
    keyboardController: SoftwareKeyboardController?,
) {
    val focusRequester = remember { FocusRequester() }

    var query by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
        userSearch.searchUsersByPrefix("")
    }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        singleLine = true,
        value = query,
        onValueChange = {
            query = it
            scope.launch {
                userSearch.searchUsersByPrefix(query)
            }
        },
        placeholder = {
            Text(
                text = stringResource(R.string.search),
            )
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        ),
        trailingIcon = {
            if (query.isNotEmpty() && query != "") {
                IconButton(
                    onClick = {
                        query = ""
                        scope.launch {
                            userSearch.searchUsersByPrefix(query)
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = null,
                    )
                }
            }
        }
    )
}