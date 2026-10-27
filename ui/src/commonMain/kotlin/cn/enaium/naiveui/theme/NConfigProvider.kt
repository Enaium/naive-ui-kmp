package cn.enaium.naiveui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.Modifier

/** Theme provided to every naive-ui KMP component. */
val LocalNTheme = staticCompositionLocalOf { NTheme.Light }

/** Locale provided to every naive-ui KMP component. */
val LocalNLocale = staticCompositionLocalOf { NLocale.English }

/** Component size inherited from a form item or a provider. */
val LocalNComponentSize = staticCompositionLocalOf<NSize?> { null }

/** Right-to-left flag inherited from a provider. */
val LocalNRtl = staticCompositionLocalOf { false }

/** Resolves the effective size for a component. */
@Composable
fun nSize(size: NSize? = null): NSize =
    size ?: LocalNComponentSize.current ?: NSize.Medium

/**
 * Configures theme, locale, component size and text direction for a subtree.
 *
 * Mirrors `<n-config-provider>` from naive-ui. Compose has no DOM wrapper, so
 * `abstract`, `tag` and `preflight-style-disabled` are accepted for API parity
 * only.
 */
@Composable
fun NConfigProvider(
    theme: NTheme = NTheme.Light,
    themeOverrides: NThemeOverrides? = null,
    locale: NLocale = NLocale.English,
    size: NSize? = null,
    rtl: Boolean = false,
    abstract: Boolean = false,
    inlineThemeDisabled: Boolean = false,
    preflightStyleDisabled: Boolean = false,
    tag: String = "div",
    content: @Composable () -> Unit,
) {
    // Accepted for parity with the Vue component; Compose does not render a
    // host element for these options.
    @Suppress("UNUSED_VARIABLE")
    val parity = listOf(abstract, inlineThemeDisabled, preflightStyleDisabled, tag)
    val mergedTheme = remember(theme, themeOverrides) { theme.withOverrides(themeOverrides) }
    CompositionLocalProvider(
        LocalNTheme provides mergedTheme,
        LocalNLocale provides locale,
        LocalNComponentSize provides size,
        LocalNRtl provides rtl,
        LocalLayoutDirection provides if (rtl) LayoutDirection.Rtl else LayoutDirection.Ltr,
    ) {
        content()
    }
}

/** Applies the naive-ui body background to a subtree. */
@Composable
fun NGlobalStyle(content: @Composable () -> Unit) {
    val theme = LocalNTheme.current
    Box(Modifier.fillMaxSize().background(theme.colors.body)) {
        content()
    }
}
