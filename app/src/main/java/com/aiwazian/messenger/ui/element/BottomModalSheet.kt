package com.aiwazian.messenger.ui.element

import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.aiwazian.messenger.viewModels.DialogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomModalSheet(
    viewModel: DialogViewModel,
    dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
    content: @Composable () -> Unit,
) {
    val isVisible by viewModel.isDialogVisible

    if (isVisible) {
        ModalBottomSheet(
            modifier = Modifier
                .wrapContentHeight(),
            onDismissRequest = {
                viewModel.hideDialog()
            },
            dragHandle = dragHandle,
        ) {
            content()
        }
    }
}
