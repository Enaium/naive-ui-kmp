package cn.enaium.naiveui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Component size shared by naive-ui components. */
enum class NSize(val label: String) {
    Tiny("tiny"),
    Small("small"),
    Medium("medium"),
    Large("large"),
    ;

    companion object {
        fun fromLabel(value: String?): NSize? = entries.firstOrNull { it.label == value }
    }
}

/** Semantic status used by buttons, alerts, tags, etc. */
enum class NStatus { Default, Primary, Info, Success, Warning, Error }

/** The naive-ui base font size tokens. */
@Immutable
data class NFontSize(
    val mini: TextUnit = 12.sp,
    val tiny: TextUnit = 12.sp,
    val small: TextUnit = 14.sp,
    val medium: TextUnit = 14.sp,
    val large: TextUnit = 15.sp,
    val huge: TextUnit = 16.sp,
) {
    fun of(size: NSize): TextUnit = when (size) {
        NSize.Tiny -> tiny
        NSize.Small -> small
        NSize.Medium -> medium
        NSize.Large -> large
    }
}

/** The naive-ui control height tokens. */
@Immutable
data class NHeight(
    val tiny: Dp = 22.dp,
    val small: Dp = 28.dp,
    val medium: Dp = 34.dp,
    val large: Dp = 40.dp,
    val huge: Dp = 46.dp,
) {
    fun of(size: NSize): Dp = when (size) {
        NSize.Tiny -> tiny
        NSize.Small -> small
        NSize.Medium -> medium
        NSize.Large -> large
    }
}

@Immutable
data class NStatusColors(
    val color: Color,
    val hover: Color,
    val pressed: Color,
    val suppl: Color,
)

/**
 * Naive UI global color tokens. Light values come from
 * `naive-ui/src/_styles/common/light.ts` and dark values from `dark.ts`.
 */
@Immutable
data class NColors(
    val base: Color,
    val primary: Color,
    val primaryHover: Color,
    val primaryPressed: Color,
    val primarySuppl: Color,
    val info: Color,
    val infoHover: Color,
    val infoPressed: Color,
    val infoSuppl: Color,
    val success: Color,
    val successHover: Color,
    val successPressed: Color,
    val successSuppl: Color,
    val warning: Color,
    val warningHover: Color,
    val warningPressed: Color,
    val warningSuppl: Color,
    val error: Color,
    val errorHover: Color,
    val errorPressed: Color,
    val errorSuppl: Color,
    val textBase: Color,
    val text1: Color,
    val text2: Color,
    val text3: Color,
    val textDisabled: Color,
    val placeholder: Color,
    val placeholderDisabled: Color,
    val icon: Color,
    val iconHover: Color,
    val iconPressed: Color,
    val iconDisabled: Color,
    val divider: Color,
    val border: Color,
    val closeIcon: Color,
    val closeIconHover: Color,
    val closeIconPressed: Color,
    val closeHover: Color,
    val closePressed: Color,
    val clear: Color,
    val clearHover: Color,
    val clearPressed: Color,
    val scrollbar: Color,
    val scrollbarHover: Color,
    val progressRail: Color,
    val rail: Color,
    val popover: Color,
    val table: Color,
    val card: Color,
    val modal: Color,
    val body: Color,
    val tag: Color,
    val avatar: Color,
    val inverted: Color,
    val input: Color,
    val code: Color,
    val tab: Color,
    val action: Color,
    val tableHeader: Color,
    val hover: Color,
    val tableHover: Color,
    val tableStriped: Color,
    val pressed: Color,
    val inputDisabled: Color,
    val buttonColor2: Color,
    val buttonColor2Hover: Color,
    val buttonColor2Pressed: Color,
    val mask: Color,
    val isDark: Boolean,
) {
    val primaryStatus: NStatusColors get() = NStatusColors(primary, primaryHover, primaryPressed, primarySuppl)
    val infoStatus: NStatusColors get() = NStatusColors(info, infoHover, infoPressed, infoSuppl)
    val successStatus: NStatusColors get() = NStatusColors(success, successHover, successPressed, successSuppl)
    val warningStatus: NStatusColors get() = NStatusColors(warning, warningHover, warningPressed, warningSuppl)
    val errorStatus: NStatusColors get() = NStatusColors(error, errorHover, errorPressed, errorSuppl)

    fun status(type: NStatus): NStatusColors = when (type) {
        NStatus.Default -> NStatusColors(text2, text1, textBase, text2)
        NStatus.Primary -> primaryStatus
        NStatus.Info -> infoStatus
        NStatus.Success -> successStatus
        NStatus.Warning -> warningStatus
        NStatus.Error -> errorStatus
    }

    fun statusColor(type: NStatus): Color = when (type) {
        NStatus.Default -> text2
        NStatus.Primary -> primary
        NStatus.Info -> info
        NStatus.Success -> success
        NStatus.Warning -> warning
        NStatus.Error -> error
    }

    companion object {
        val Light = NColors(
            base = Color(0xFFFFFFFF),
            primary = Color(0xFF18A058),
            primaryHover = Color(0xFF36AD6A),
            primaryPressed = Color(0xFF0C7A43),
            primarySuppl = Color(0xFF36AD6A),
            info = Color(0xFF2080F0),
            infoHover = Color(0xFF4098FC),
            infoPressed = Color(0xFF1060C9),
            infoSuppl = Color(0xFF4098FC),
            success = Color(0xFF18A058),
            successHover = Color(0xFF36AD6A),
            successPressed = Color(0xFF0C7A43),
            successSuppl = Color(0xFF36AD6A),
            warning = Color(0xFFF0A020),
            warningHover = Color(0xFFFCB040),
            warningPressed = Color(0xFFC97C10),
            warningSuppl = Color(0xFFFCB040),
            error = Color(0xFFD03050),
            errorHover = Color(0xFFDE576D),
            errorPressed = Color(0xFFAB1F3F),
            errorSuppl = Color(0xFFDE576D),
            textBase = Color(0xFF000000),
            text1 = Color(0xFF1F2225),
            text2 = Color(0xFF333639),
            text3 = Color(0xFF767C82),
            textDisabled = Color(0xFFC2C2C2),
            placeholder = Color(0xFFC2C2C2),
            placeholderDisabled = Color(0xFFD1D1D1),
            icon = Color(0xFFC2C2C2),
            iconHover = Color(0xFFA6A6A6),
            iconPressed = Color(0xFF8A8A8A),
            iconDisabled = Color(0xFFD1D1D1),
            divider = Color(0xFFEFEFF5),
            border = Color(0xFFE0E0E6),
            closeIcon = Color(0xFF666666),
            closeIconHover = Color(0xFF666666),
            closeIconPressed = Color(0xFF666666),
            closeHover = Color(0xFFE8E8E8),
            closePressed = Color(0xFFDEDEDE),
            clear = Color(0xFFC2C2C2),
            clearHover = Color(0xFFA6A6A6),
            clearPressed = Color(0xFF8A8A8A),
            scrollbar = Color.Black.copy(alpha = 0.25f),
            scrollbarHover = Color.Black.copy(alpha = 0.4f),
            progressRail = Color(0xFFEBEBEB),
            rail = Color(0xFFDBDBDF),
            popover = Color(0xFFFFFFFF),
            table = Color(0xFFFFFFFF),
            card = Color(0xFFFFFFFF),
            modal = Color(0xFFFFFFFF),
            body = Color(0xFFFFFFFF),
            tag = Color(0xFFEEEEEE),
            avatar = Color(0xFFCCCCCC),
            inverted = Color(0xFF001428),
            input = Color.Transparent,
            code = Color(0xFFF4F4F8),
            tab = Color(0xFFF7F7FA),
            action = Color(0xFFFAFAFC),
            tableHeader = Color(0xFFFAFAFC),
            hover = Color(0xFFF3F3F5),
            tableHover = Color(0x08000064),
            tableStriped = Color(0x05000064),
            pressed = Color(0xFFEDEDEF),
            inputDisabled = Color(0xFFFAFAFC),
            buttonColor2 = Color(0x0D2E3338),
            buttonColor2Hover = Color(0x172E3338),
            buttonColor2Pressed = Color(0x212E3338),
            mask = Color.Black.copy(alpha = 0.4f),
            isDark = false,
        )

        val Dark = NColors(
            base = Color(0xFF000000),
            primary = Color(0xFF63E2B7),
            primaryHover = Color(0xFF7FE7C4),
            primaryPressed = Color(0xFF5ACEA7),
            primarySuppl = Color(0xFF2A947D),
            info = Color(0xFF70C0E8),
            infoHover = Color(0xFF8ACBEC),
            infoPressed = Color(0xFF66AFD3),
            infoSuppl = Color(0xFF3889C5),
            success = Color(0xFF63E2B7),
            successHover = Color(0xFF7FE7C4),
            successPressed = Color(0xFF5ACEA7),
            successSuppl = Color(0xFF2A947D),
            warning = Color(0xFFF2C97D),
            warningHover = Color(0xFFF5D599),
            warningPressed = Color(0xFFE6C260),
            warningSuppl = Color(0xFFF08A00),
            error = Color(0xFFE88080),
            errorHover = Color(0xFFE98B8B),
            errorPressed = Color(0xFFE57272),
            errorSuppl = Color(0xFFD03A52),
            textBase = Color(0xFFFFFFFF),
            text1 = Color(0xFFE5E5E5),
            text2 = Color(0xFFD1D1D1),
            text3 = Color(0xFF858585),
            textDisabled = Color(0xFF616161),
            placeholder = Color(0xFF616161),
            placeholderDisabled = Color(0xFF474747),
            icon = Color(0xFF616161),
            iconHover = Color(0xFF797979),
            iconPressed = Color(0xFF4E4E4E),
            iconDisabled = Color(0xFF474747),
            divider = Color(0xFF171717),
            border = Color(0xFF3D3D3D),
            closeIcon = Color(0xFF858585),
            closeIconHover = Color(0xFF858585),
            closeIconPressed = Color(0xFF858585),
            closeHover = Color(0xFF1F1F1F),
            closePressed = Color(0xFF141414),
            clear = Color(0xFF616161),
            clearHover = Color(0xFF797979),
            clearPressed = Color(0xFF4E4E4E),
            scrollbar = Color.White.copy(alpha = 0.2f),
            scrollbarHover = Color.White.copy(alpha = 0.3f),
            progressRail = Color(0xFF1F1F1F),
            rail = Color(0xFF333333),
            popover = Color(0xFF48484E),
            table = Color(0xFF18181C),
            card = Color(0xFF18181C),
            modal = Color(0xFF2C2C32),
            body = Color(0xFF101014),
            tag = Color(0xFF333333),
            avatar = Color(0xFF2E2E2E),
            inverted = Color(0xFF000000),
            input = Color(0xFF1A1A1A),
            code = Color(0xFF1F1F1F),
            tab = Color(0xFF0A0A0A),
            action = Color(0xFF0F0F0F),
            tableHeader = Color(0xFF0F0F0F),
            hover = Color(0xFF171717),
            tableHover = Color(0xFF0F0F0F),
            tableStriped = Color(0xFF0D0D0D),
            pressed = Color(0xFF0D0D0D),
            inputDisabled = Color(0xFF0F0F0F),
            buttonColor2 = Color(0x14FFFFFF),
            buttonColor2Hover = Color(0x1FFFFFFF),
            buttonColor2Pressed = Color(0x14FFFFFF),
            mask = Color.Black.copy(alpha = 0.4f),
            isDark = true,
        )
    }
}

/** Typography used by naive-ui's textual components. */
@Immutable
data class NTypography(
    val fontFamily: FontFamily = FontFamily.SansSerif,
    val monoFamily: FontFamily = FontFamily.Monospace,
    val fontWeight: FontWeight = FontWeight.Normal,
    val fontWeightStrong: FontWeight = FontWeight.Medium,
    val lineHeight: Float = 1.6f,
    val fontSize: NFontSize = NFontSize(),
) {
    fun style(size: TextUnit, weight: FontWeight = fontWeight, color: Color = Color.Unspecified): TextStyle =
        TextStyle(
            fontFamily = fontFamily,
            fontSize = size,
            lineHeight = size * lineHeight,
            fontWeight = weight,
            color = color,
        )

    val h1: TextStyle get() = style(30.sp, FontWeight.SemiBold)
    val h2: TextStyle get() = style(24.sp, FontWeight.SemiBold)
    val h3: TextStyle get() = style(20.sp, FontWeight.Medium)
    val h4: TextStyle get() = style(18.sp, FontWeight.Medium)
    val h5: TextStyle get() = style(16.sp, FontWeight.Medium)
    val h6: TextStyle get() = style(15.sp, FontWeight.Medium)
    val body: TextStyle get() = style(fontSize.medium)
    val caption: TextStyle get() = style(fontSize.small, color = Color.Unspecified)
}

/** Per-component style overrides, keyed by the naive-ui component name. */
@Immutable
data class NComponentOverrides(
    val color: Color? = null,
    val colorHover: Color? = null,
    val colorPressed: Color? = null,
    val textColor: Color? = null,
    val borderColor: Color? = null,
    val borderRadius: Dp? = null,
    val height: Dp? = null,
    val fontSize: TextUnit? = null,
    val fontWeight: FontWeight? = null,
)

/** A subset of naive-ui's `themeOverrides`, shaped for Compose. */
@Immutable
data class NThemeOverrides(
    val primaryColor: Color? = null,
    val primaryColorHover: Color? = null,
    val primaryColorPressed: Color? = null,
    val infoColor: Color? = null,
    val successColor: Color? = null,
    val warningColor: Color? = null,
    val errorColor: Color? = null,
    val textColor1: Color? = null,
    val textColor2: Color? = null,
    val textColor3: Color? = null,
    val borderColor: Color? = null,
    val dividerColor: Color? = null,
    val cardColor: Color? = null,
    val modalColor: Color? = null,
    val popoverColor: Color? = null,
    val bodyColor: Color? = null,
    val borderRadius: Dp? = null,
    val borderRadiusSmall: Dp? = null,
    val fontSize: TextUnit? = null,
    val height: Dp? = null,
    val components: Map<String, NComponentOverrides> = emptyMap(),
)

@Immutable
data class NTheme(
    val colors: NColors,
    val typography: NTypography = NTypography(),
    val borderRadius: Dp = 3.dp,
    val borderRadiusSmall: Dp = 2.dp,
    val height: NHeight = NHeight(),
    val opacityDisabled: Float = 0.5f,
    val components: Map<String, NComponentOverrides> = emptyMap(),
) {
    val isDark: Boolean get() = colors.isDark

    fun heightFor(size: NSize): Dp = height.of(size)
    fun fontSizeFor(size: NSize): TextUnit = typography.fontSize.of(size)
    fun component(name: String): NComponentOverrides? = components[name]

    fun withOverrides(overrides: NThemeOverrides?): NTheme {
        if (overrides == null) return this
        val c = colors.copy(
            primary = overrides.primaryColor ?: colors.primary,
            primaryHover = overrides.primaryColorHover ?: overrides.primaryColor ?: colors.primaryHover,
            primaryPressed = overrides.primaryColorPressed ?: overrides.primaryColor ?: colors.primaryPressed,
            info = overrides.infoColor ?: colors.info,
            success = overrides.successColor ?: colors.success,
            warning = overrides.warningColor ?: colors.warning,
            error = overrides.errorColor ?: colors.error,
            text1 = overrides.textColor1 ?: colors.text1,
            text2 = overrides.textColor2 ?: colors.text2,
            text3 = overrides.textColor3 ?: colors.text3,
            border = overrides.borderColor ?: colors.border,
            divider = overrides.dividerColor ?: colors.divider,
            card = overrides.cardColor ?: colors.card,
            modal = overrides.modalColor ?: colors.modal,
            popover = overrides.popoverColor ?: colors.popover,
            body = overrides.bodyColor ?: colors.body,
        )
        return copy(
            colors = c,
            borderRadius = overrides.borderRadius ?: borderRadius,
            borderRadiusSmall = overrides.borderRadiusSmall ?: borderRadiusSmall,
            height = if (overrides.height != null) height.copy(medium = overrides.height) else height,
            typography = if (overrides.fontSize != null) {
                typography.copy(fontSize = typography.fontSize.copy(medium = overrides.fontSize))
            } else {
                typography
            },
            components = components + overrides.components,
        )
    }

    companion object {
        val Light = NTheme(colors = NColors.Light)
        val Dark = NTheme(colors = NColors.Dark)
    }
}
