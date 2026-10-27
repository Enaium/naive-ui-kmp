@file:OptIn(kotlin.time.ExperimentalTime::class)

package cn.enaium.naiveui.sample.gallery.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NTime
import cn.enaium.naiveui.components.NTimeType
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import kotlinx.datetime.TimeZone
import kotlin.time.Clock

@Composable
fun TimeDocs() {
    DocsPage(
        title = "Time",
        description = "Time formats epoch milliseconds, Unix timestamps or ISO-8601 values as dates, date-times or live relative descriptions.",
    ) {
        DemoSection(
            title = "Basic",
            description = "time accepts epoch milliseconds. The default type is DateTime and the default format is yyyy-MM-dd HH:mm:ss.",
            code = """
NTime(time = 1714566896000)
NTime(isoTime = "2024-05-01T12:34:56")
""",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NTime(time = 1714566896000L)
                NTime(isoTime = "2024-05-01T12:34:56")
                NTime(type = NTimeType.Date, time = 1714566896000L)
            }
        }

        DemoSection(
            title = "Relative",
            description = "Relative time is rendered from time to to. It is updated automatically when to is omitted.",
            code = """
NTime(time = 0, to = 86_400_000, type = NTimeType.Relative)
NTime(time = 0, to = 864_000_000, type = NTimeType.Relative)
""",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NTime(time = 0, to = 86_400_000L, type = NTimeType.Relative)
                NTime(time = 0, to = 864_000_000L, type = NTimeType.Relative)
                NTime(time = 0, to = 8_640_000_000L, type = NTimeType.Relative)
            }
        }

        DemoSection(
            title = "Live relative value",
            description = "When to is omitted, a relative time compares against the current clock and refreshes every second.",
            code = """
val started = remember {
    Clock.System.now().toEpochMilliseconds() - 65_000L
}
NTime(time = started, type = NTimeType.Relative)
""",
        ) {
            val started = remember { Clock.System.now().toEpochMilliseconds() - 65_000L }
            NTime(time = started, type = NTimeType.Relative)
        }

        DemoSection(
            title = "Format",
            description = "Custom date-fns style patterns control the output. Supports yyyy, yy, MMMM, MMM, MM, dd, HH, hh, mm, ss, SSS, a and EEEE tokens.",
            code = """
NTime(time = 0, format = "yyyy-MM-dd")
NTime(time = 0, format = "yyyy-MM-dd HH:mm")
NTime(time = 0, format = "yyyy-MM-dd HH:mm:ss.SSS")
""",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NTime(time = 0, format = "yyyy-MM-dd")
                NTime(time = 0, format = "yyyy-MM-dd HH:mm")
                NTime(time = 0, format = "yyyy-MM-dd HH:mm:ss.SSS")
                NTime(time = 0, format = "EEEE, MMMM d, yyyy hh:mm a")
            }
        }

        DemoSection(
            title = "Unix timestamp",
            description = "Set unix = true to interpret time and to as seconds instead of milliseconds.",
            code = """
NTime(time = 4_320_000, format = "yyyy-MM-dd HH:mm:ss", unix = true)
NTime(time = 4_320_000_000, format = "yyyy-MM-dd HH:mm:ss")
""",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NTime(time = 4_320_000L, format = "yyyy-MM-dd HH:mm:ss", unix = true)
                NTime(time = 4_320_000_000L, format = "yyyy-MM-dd HH:mm:ss")
            }
        }

        DemoSection(
            title = "Time zone",
            description = "Pass a TimeZone to format the same instant in another zone. Fixed offsets such as TimeZone.of(\"UTC+08:00\") also work.",
            code = """
NTime(time = 0, timeZone = TimeZone.of("Asia/Shanghai"))
NTime(time = 0, timeZone = TimeZone.of("America/New_York"))
NTime(time = 0, timeZone = TimeZone.of("UTC"))
""",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NTime(time = 0, timeZone = TimeZone.of("Asia/Shanghai"))
                NTime(time = 0, timeZone = TimeZone.of("America/New_York"))
                NTime(time = 0, timeZone = TimeZone.of("UTC"))
            }
        }
    }
}
