package cn.enaium.naiveui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalTestApi::class)
class NStatisticDisplayTest {
    @Test
    fun rendersLabelAndNumber() = runComposeUiTest {
        setNContent {
            NStatistic(label = "Users", value = 42)
        }
        onNodeWithText("Users").assertExists()
        onNodeWithText("42").assertExists()
    }

    @Test
    fun precisionPadsDecimalPlaces() = runComposeUiTest {
        setNContent {
            NStatistic(label = "Rating", value = 12.5, precision = 2)
        }
        onNodeWithText("12.50").assertExists()
    }

    @Test
    fun stringValueIsRenderedVerbatim() = runComposeUiTest {
        setNContent {
            NStatistic(label = "Status", value = "N/A")
        }
        onNodeWithText("N/A").assertExists()
    }

    @Test
    fun prefixSuffixAndCustomLabelRender() = runComposeUiTest {
        setNContent {
            NStatistic(
                value = 7,
                labelContent = { NText("Custom label") },
                prefix = { NText("\$") },
                suffix = { NText(" USD") },
            )
        }
        onNodeWithText("Custom label").assertExists()
        onNodeWithText("\$").assertExists()
        onNodeWithText("7").assertExists()
        onNodeWithText(" USD").assertExists()
    }

    @Test
    fun contentSlotRendersWhenValueMissing() = runComposeUiTest {
        setNContent {
            NStatistic(label = "Slot", content = { NText("custom value") })
        }
        onNodeWithText("Slot").assertExists()
        onNodeWithText("custom value").assertExists()
    }

    @Test
    fun tabularNumbersStillShowValue() = runComposeUiTest {
        setNContent {
            NStatistic(label = "Count", value = 1234, tabularNumbers = true)
        }
        onNodeWithText("1234").assertExists()
    }
}

@OptIn(ExperimentalTestApi::class)
class NCountdownDisplayTest {
    private val baseDuration = 3_723_000L // 1h 2m 3s

    @Test
    fun rendersDefaultClockText() = runComposeUiTest {
        setNContent {
            NCountdown(duration = baseDuration, active = false)
        }
        onNodeWithText("01:02:03").assertExists()
    }

    @Test
    fun activeCountdownShowsInitialValue() = runComposeUiTest {
        setNContent {
            NCountdown(duration = baseDuration)
        }
        onNodeWithText("01:02:03").assertExists()
    }

    @Test
    fun precisionAddsFractionDigits() = runComposeUiTest {
        setNContent {
            NCountdown(duration = baseDuration, precision = 2, active = false)
        }
        onNodeWithText("01:02:03.00").assertExists()
    }

    @Test
    fun formatCallbackControlsText() = runComposeUiTest {
        setNContent {
            NCountdown(
                duration = baseDuration,
                active = false,
                format = { info -> "${info.hours}h ${info.minutes}m ${info.seconds}s" },
            )
        }
        onNodeWithText("1h 2m 3s").assertExists()
    }

    @Test
    fun renderSlotReceivesTimeInfo() = runComposeUiTest {
        setNContent {
            NCountdown(
                duration = baseDuration,
                active = false,
                render = { info -> NText("d${info.days} h${info.hours} m${info.minutes} s${info.seconds}") },
            )
        }
        onNodeWithText("d0 h1 m2 s3").assertExists()
    }

    @Test
    fun zeroDurationFinishesImmediately() = runComposeUiTest {
        var finished = 0
        setNContent {
            NCountdown(duration = 0L, onFinish = { finished++ })
        }
        waitUntil { finished == 1 }
        runOnIdle { assertEquals(1, finished) }
    }
}

@OptIn(ExperimentalTestApi::class)
class NNumberAnimationDisplayTest {
    @Test
    fun equalFromAndToShowsFinalValue() = runComposeUiTest {
        setNContent {
            NNumberAnimation(to = 250.0, from = 250.0)
        }
        onNodeWithText("250").assertExists()
    }

    @Test
    fun inactiveAnimationShowsFromValue() = runComposeUiTest {
        setNContent {
            NNumberAnimation(to = 999.0, from = 42.0, active = false)
        }
        onNodeWithText("42").assertExists()
    }

    @Test
    fun separatorsPrefixAndSuffixAreApplied() = runComposeUiTest {
        setNContent {
            NNumberAnimation(
                to = 1234.0,
                from = 1234.0,
                showSeparator = true,
                prefix = "\$",
                suffix = " USD",
            )
        }
        onNodeWithText("\$1,234 USD").assertExists()
    }

    @Test
    fun precisionFormatsDecimals() = runComposeUiTest {
        setNContent {
            NNumberAnimation(to = 12.5, from = 12.5, precision = 2)
        }
        onNodeWithText("12.50").assertExists()
    }

    @Test
    fun localeControlsSeparators() = runComposeUiTest {
        setNContent {
            NNumberAnimation(to = 1234.5, from = 1234.5, precision = 2, showSeparator = true, locale = "de-DE")
        }
        onNodeWithText("1.234,50").assertExists()
    }

    @Test
    fun explicitSeparatorsOverrideLocale() = runComposeUiTest {
        setNContent {
            NNumberAnimation(
                to = 1234.5,
                from = 1234.5,
                precision = 1,
                showSeparator = true,
                separator = '_',
                decimalSeparator = '~',
            )
        }
        onNodeWithText("1_234~5").assertExists()
    }
}

@OptIn(ExperimentalTestApi::class)
class NQrCodeDisplayTest {
    @Test
    fun bitmapHasRequestedSize() = runComposeUiTest {
        val bitmap = assertNotNull(NQrCodeBitmap("https://example.com", pixels = 128, padding = 8))
        assertEquals(128, bitmap.width)
        assertEquals(128, bitmap.height)
    }

    @Test
    fun canvasQrCodeRenders() = runComposeUiTest {
        setNContent {
            NQrCode(value = "hello", modifier = Modifier.testTag("qr"))
        }
        onNodeWithTag("qr").assertExists()
    }

    @Test
    fun svgQrCodeRenders() = runComposeUiTest {
        setNContent {
            NQrCode(
                value = "hello",
                modifier = Modifier.testTag("qr-svg"),
                type = NQrCodeType.Svg,
                errorCorrectionLevel = "H",
                padding = 4.dp,
            )
        }
        onNodeWithTag("qr-svg").assertExists()
    }

    @Test
    fun emptyValueStillRendersQrCode() = runComposeUiTest {
        setNContent {
            NQrCode(value = "", modifier = Modifier.testTag("qr-empty"))
        }
        onNodeWithTag("qr-empty").assertExists()
    }
}

@OptIn(ExperimentalTestApi::class)
class NTimeDisplayTest {
    @Test
    fun dateTypeUsesDefaultPattern() = runComposeUiTest {
        setNContent {
            NTime(
                type = NTimeType.Date,
                isoTime = "2021-05-19T12:34:56Z",
                timeZone = TimeZone.UTC,
            )
        }
        onNodeWithText("2021-05-19").assertExists()
    }

    @Test
    fun dateTypeUsesCustomPattern() = runComposeUiTest {
        setNContent {
            NTime(
                type = NTimeType.Date,
                format = "dd/MM/yyyy",
                isoTime = "2021-05-19T12:34:56Z",
                timeZone = TimeZone.UTC,
            )
        }
        onNodeWithText("19/05/2021").assertExists()
    }

    @Test
    fun datetimeTypeUsesDefaultPattern() = runComposeUiTest {
        setNContent {
            NTime(
                type = NTimeType.DateTime,
                isoTime = "2021-05-19T12:34:56Z",
                timeZone = TimeZone.UTC,
            )
        }
        onNodeWithText("2021-05-19 12:34:56").assertExists()
    }

    @Test
    fun relativeTargetAfterTimeUsesAgoSuffix() = runComposeUiTest {
        setNContent {
            NTime(
                type = NTimeType.Relative,
                isoTime = "2021-05-19T00:00:00Z",
                isoTo = "2021-05-21T00:00:00Z",
                timeZone = TimeZone.UTC,
                liveUpdate = false,
            )
        }
        onNodeWithText("2 days ago").assertExists()
    }

    @Test
    fun relativeTargetBeforeTimeUsesInPrefix() = runComposeUiTest {
        setNContent {
            NTime(
                type = NTimeType.Relative,
                isoTime = "2021-05-19T00:00:00Z",
                isoTo = "2021-05-17T00:00:00Z",
                timeZone = TimeZone.UTC,
                liveUpdate = false,
            )
        }
        onNodeWithText("in 2 days").assertExists()
    }
}
