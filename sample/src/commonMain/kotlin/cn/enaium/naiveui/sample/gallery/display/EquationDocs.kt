package cn.enaium.naiveui.sample.gallery.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NEquation
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage

private const val BasicEquation = """\displaystyle = \frac{k(k+1)}{2}+k+1"""

private const val FractionEquation = """\frac{1}{1+x} + \sqrt{x^2+y^2} + \sqrt[3]{8}"""

private const val ScriptEquation = """a^2 + b_1^2 = c^2"""

private const val IntegralEquation = """\int_{-\infty}^{\infty} e^{-x^2} dx = \sqrt{\pi}"""

private const val GreekEquation = """\alpha + \beta = \gamma, \quad \sum_{i=1}^{n} i = \frac{n(n+1)}{2}"""

private const val TextEquation = """\text{area} = \pi r^2"""

@Composable
fun EquationDocs() {
    DocsPage(
        title = "Equation",
        description = "Render LaTeX formulas with a lightweight built-in layout engine.",
    ) {
        DemoSection(
            title = "Basic usage",
            description = "Fractions, operators and groups are laid out directly in Compose.",
            code = "NEquation(\"\\\\displaystyle = \\\\frac{k(k+1)}{2}+k+1\")",
        ) {
            NEquation(BasicEquation)
        }
        DemoSection(
            title = "Fractions and roots",
            description = "\\frac produces a stacked fraction and \\sqrt a radical with an optional index.",
        ) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                NEquation(FractionEquation, fontSize = 20.sp)
                NEquation(IntegralEquation, fontSize = 20.sp)
            }
        }
        DemoSection(
            title = "Scripts",
            description = "Use ^ for superscripts and _ for subscripts, with braces for groups.",
        ) {
            NEquation(ScriptEquation, fontSize = 22.sp)
        }
        DemoSection(
            title = "Greek letters and operators",
            description = "Greek letters, sums and common relations are mapped to Unicode.",
        ) {
            NEquation(GreekEquation, fontSize = 20.sp)
        }
        DemoSection(
            title = "Text runs",
            description = "\\text{...} renders upright text inside a formula.",
        ) {
            NEquation(TextEquation, fontSize = 22.sp)
        }
    }
}
