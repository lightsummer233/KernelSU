package me.weishu.kernelsu.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import me.weishu.kernelsu.KernelVersion
import me.weishu.kernelsu.Natives
import me.weishu.kernelsu.getKernelVersion
import me.weishu.kernelsu.ui.util.rootAvailable

class KsuStatusViewModel : ViewModel() {

    val isManager: StateFlow<Boolean>
        field = MutableStateFlow(Natives.isManager)

    val kernelVersion: StateFlow<KernelVersion>
        field = MutableStateFlow(getKernelVersion())

    val nativeKsuVersion: StateFlow<Int>
        field = MutableStateFlow(Natives.version)

    val ksuVersion: StateFlow<Int?> =
        combine(isManager, nativeKsuVersion) { isManager, nativeKsuVersion ->
            if (isManager) nativeKsuVersion else null
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            if (Natives.isManager) Natives.version else null
        )

    val requestNewKernel: StateFlow<Boolean> =
        nativeKsuVersion.map {
            it != -1 && it < Natives.MINIMAL_SUPPORTED_KERNEL
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            Natives.requireNewKernel()
        )

    val isRootAvailable: StateFlow<Boolean>
        field = MutableStateFlow(rootAvailable())

    val isFullFeatured: StateFlow<Boolean> =
        combine(
            isManager,
            requestNewKernel,
            isRootAvailable
        ) { isManager, requestNewKernel, isRootAvailable ->
            isManager && !requestNewKernel && isRootAvailable
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            Natives.isManager && !Natives.requireNewKernel() && rootAvailable()
        )

    val isSafeMode: StateFlow<Boolean>
        field = MutableStateFlow(Natives.isSafeMode)

    val nativeIsLkmMode: StateFlow<Boolean>
        field = MutableStateFlow(Natives.isLkmMode)

    val isLkmMode: StateFlow<Boolean?> =
        combine(
            ksuVersion,
            kernelVersion,
            nativeIsLkmMode
        ) { ksuVersion, kernelVersion, nativeIsLkmMode ->
            ksuVersion?.let { if (kernelVersion.isGKI()) nativeIsLkmMode else null }
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            if (Natives.isManager) {
                val kernelVersion = getKernelVersion()
                if (kernelVersion.isGKI()) Natives.isLkmMode else null
            } else null
        )
}
