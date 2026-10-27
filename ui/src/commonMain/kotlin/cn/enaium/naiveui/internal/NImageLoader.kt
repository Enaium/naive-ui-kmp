@file:OptIn(coil3.annotation.DelicateCoilApi::class)

package cn.enaium.naiveui.internal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.compose.LocalPlatformContext
import coil3.network.ktor3.KtorNetworkFetcherFactory

/**
 * Installs a Coil image loader with a Ktor network fetcher. Call once at the
 * root of an application that renders remote images (avatars, images,
 * carousels).
 */
@Composable
fun NImageLoaderSetup() {
    val context = LocalPlatformContext.current
    LaunchedEffect(context) {
        SingletonImageLoader.setUnsafe { platformContext ->
            ImageLoader.Builder(platformContext)
                .components { add(KtorNetworkFetcherFactory()) }
                .build()
        }
    }
}
