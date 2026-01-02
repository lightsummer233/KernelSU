package me.weishu.kernelsu.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import me.weishu.kernelsu.ui.viewmodel.KsuStatusViewModel

@Composable
fun KsuIsValid(
    content: @Composable () -> Unit
) {
    val ksuStatus = viewModel<KsuStatusViewModel>()
    val ksuVersion by ksuStatus.ksuVersion.collectAsState()

    if (ksuVersion != null) {
        content()
    }
}
