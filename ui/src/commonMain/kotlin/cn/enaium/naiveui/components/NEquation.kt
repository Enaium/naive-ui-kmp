package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.internal.nTopBorder
import cn.enaium.naiveui.theme.LocalNTheme

private sealed interface NMathNode

private data class NMathText(val text: String) : NMathNode

private data class NMathRow(val children: List<NMathNode>) : NMathNode

private data class NMathFraction(val numerator: NMathNode, val denominator: NMathNode) : NMathNode

private data class NMathScript(
    val base: NMathNode,
    val superscript: NMathNode?,
    val subscript: NMathNode?,
) : NMathNode

private data class NMathSqrt(val index: NMathNode?, val radicand: NMathNode) : NMathNode

private data object NMathNewline : NMathNode

private val nMathSymbols: Map<String, String> = mapOf(
    "alpha" to "α", "beta" to "β", "gamma" to "γ", "delta" to "δ", "epsilon" to "ε",
    "varepsilon" to "ε", "zeta" to "ζ", "eta" to "η", "theta" to "θ", "vartheta" to "ϑ",
    "iota" to "ι", "kappa" to "κ", "lambda" to "λ", "mu" to "μ", "nu" to "ν", "xi" to "ξ",
    "pi" to "π", "varpi" to "ϖ", "rho" to "ρ", "varrho" to "ϱ", "sigma" to "σ",
    "varsigma" to "ς", "tau" to "τ", "upsilon" to "υ", "phi" to "φ", "varphi" to "φ",
    "chi" to "χ", "psi" to "ψ", "omega" to "ω",
    "Gamma" to "Γ", "Delta" to "Δ", "Theta" to "Θ", "Lambda" to "Λ", "Xi" to "Ξ",
    "Pi" to "Π", "Sigma" to "Σ", "Upsilon" to "Υ", "Phi" to "Φ", "Psi" to "Ψ", "Omega" to "Ω",
    "infty" to "∞", "partial" to "∂", "nabla" to "∇", "pm" to "±", "mp" to "∓",
    "times" to "×", "div" to "÷", "cdot" to "·", "ast" to "∗", "star" to "⋆",
    "le" to "≤", "leq" to "≤", "ge" to "≥", "geq" to "≥", "ne" to "≠", "neq" to "≠",
    "approx" to "≈", "equiv" to "≡", "sim" to "∼", "propto" to "∝", "ll" to "≪", "gg" to "≫",
    "to" to "→", "rightarrow" to "→", "leftarrow" to "←", "Rightarrow" to "⇒",
    "Leftarrow" to "⇐", "leftrightarrow" to "↔", "Leftrightarrow" to "⇔", "mapsto" to "↦",
    "in" to "∈", "notin" to "∉", "ni" to "∋", "subset" to "⊂", "supset" to "⊃",
    "subseteq" to "⊆", "supseteq" to "⊇", "cup" to "∪", "cap" to "∩", "setminus" to "∖",
    "emptyset" to "∅", "varnothing" to "∅", "forall" to "∀", "exists" to "∃",
    "nexists" to "∄", "neg" to "¬", "land" to "∧", "lor" to "∨", "angle" to "∠",
    "degree" to "°", "prime" to "′", "ldots" to "…", "dots" to "…", "cdots" to "⋯",
    "langle" to "⟨", "rangle" to "⟩", "lfloor" to "⌊", "rfloor" to "⌋", "lceil" to "⌈",
    "rceil" to "⌉", "int" to "∫", "iint" to "∬", "iiint" to "∭", "oint" to "∮",
    "sum" to "∑", "prod" to "∏", "coprod" to "∐", "therefore" to "∴", "because" to "∵",
    "perp" to "⊥", "parallel" to "∥", "hbar" to "ℏ", "ell" to "ℓ", "aleph" to "ℵ",
)

/**
 * A small recursive-descent parser for the LaTeX subset used by
 * `<n-equation>`: fractions, roots, scripts, groups, greek letters, operators
 * and text runs. Unknown commands render as their literal name, and the
 * component never throws.
 */
private class NMathParser(
    private val source: String,
    private val macros: Map<String, String> = emptyMap(),
    private val macroStack: Set<String> = emptySet(),
) {
    private var position = 0

    fun parse(): NMathNode = NMathRow(parseExpression(stopAtBrace = false).trimSpaces())

    private fun List<NMathNode>.trimSpaces(): List<NMathNode> {
        var start = 0
        var end = size
        while (start < end && (this[start] as? NMathText)?.text == " ") start++
        while (end > start && (this[end - 1] as? NMathText)?.text == " ") end--
        return subList(start, end).toList()
    }

    private fun parseExpression(stopAtBrace: Boolean): List<NMathNode> {
        val nodes = mutableListOf<NMathNode>()
        while (position < source.length) {
            val c = source[position]
            if (c == '}') {
                if (stopAtBrace) break
                position++
                nodes += NMathText("}")
                continue
            }
            if (c.isWhitespace()) {
                while (position < source.length && source[position].isWhitespace()) position++
                appendSpace(nodes)
                continue
            }
            if (c == '&') {
                position++
                continue
            }
            if (c == '~') {
                position++
                nodes += NMathText("\u00A0")
                continue
            }
            val atom = parseAtomWithScripts() ?: continue
            nodes += atom
        }
        return nodes
    }

    private fun appendSpace(nodes: MutableList<NMathNode>) {
        val last = nodes.lastOrNull()
        if (last is NMathText && last.text.endsWith(" ")) return
        if (last == null) return
        nodes += NMathText(" ")
    }

    private fun parseAtomWithScripts(): NMathNode? {
        val base = parseAtom() ?: return null
        var superscript: NMathNode? = null
        var subscript: NMathNode? = null
        while (position < source.length) {
            when (source[position]) {
                '^' -> {
                    position++
                    superscript = parseScriptArgument()
                }
                '_' -> {
                    position++
                    subscript = parseScriptArgument()
                }
                else -> return if (superscript == null && subscript == null) {
                    base
                } else {
                    NMathScript(base, superscript, subscript)
                }
            }
        }
        return if (superscript == null && subscript == null) base else NMathScript(base, superscript, subscript)
    }

    private fun parseAtom(): NMathNode? {
        if (position >= source.length) return null
        val c = source[position]
        return when {
            c == '{' -> parseBraceGroup()
            c == '\\' -> parseCommand()
            c == '^' || c == '_' -> {
                position++
                null
            }
            c.isDigit() -> {
                val start = position
                while (position < source.length && (source[position].isDigit() || source[position] == '.')) position++
                NMathText(source.substring(start, position))
            }
            c.isLetter() -> {
                val start = position
                while (position < source.length && source[position].isLetterOrDigit()) position++
                NMathText(source.substring(start, position))
            }
            else -> {
                position++
                NMathText(c.toString())
            }
        }
    }

    private fun parseBraceGroup(): NMathNode {
        position++
        val children = parseExpression(stopAtBrace = true)
        if (position < source.length && source[position] == '}') position++
        return NMathRow(children)
    }

    private fun parseScriptArgument(): NMathNode {
        skipSpaces()
        if (position >= source.length) return NMathText("")
        return parseAtom() ?: NMathText("")
    }

    private fun parseGroup(): NMathNode {
        skipSpaces()
        if (position >= source.length) return NMathRow(emptyList())
        return if (source[position] == '{') parseBraceGroup() else parseScriptArgument()
    }

    private fun parseBraceExpression(): List<NMathNode> {
        skipSpaces()
        if (position >= source.length) return emptyList()
        return if (source[position] == '{') {
            val group = parseBraceGroup()
            (group as? NMathRow)?.children ?: listOf(group)
        } else {
            parseAtom()?.let { listOf(it) } ?: emptyList()
        }
    }

    private fun parseRawGroup(): String {
        skipSpaces()
        if (position >= source.length) return ""
        if (source[position] != '{') {
            val single = source[position]
            position++
            return single.toString()
        }
        position++
        var depth = 1
        val builder = StringBuilder()
        while (position < source.length) {
            val c = source[position]
            if (c == '{') depth++
            if (c == '}') {
                depth--
                if (depth == 0) {
                    position++
                    break
                }
            }
            builder.append(c)
            position++
        }
        return builder.toString()
    }

    private fun skipSpaces() {
        while (position < source.length && source[position].isWhitespace()) position++
    }

    private fun parseCommand(): NMathNode? {
        position++
        if (position >= source.length) return NMathText("\\")
        val c = source[position]
        if (!c.isLetter()) {
            position++
            return when (c) {
                '{' -> NMathText("{")
                '}' -> NMathText("}")
                '%' -> NMathText("%")
                '$' -> NMathText("$")
                '#' -> NMathText("#")
                '_' -> NMathText("_")
                '&' -> NMathText("&")
                '\\' -> NMathNewline
                ' ' -> NMathText(" ")
                ',' -> NMathText("\u2009")
                ';' -> NMathText("\u2005")
                '!' -> NMathText("")
                else -> NMathText(c.toString())
            }
        }
        val start = position
        while (position < source.length && source[position].isLetter()) position++
        val name = source.substring(start, position)
        return when (name) {
            "frac", "dfrac", "tfrac", "cfrac" -> NMathFraction(parseGroup(), parseGroup())
            "sqrt" -> {
                var index: NMathNode? = null
                skipSpaces()
                if (position < source.length && source[position] == '[') {
                    position++
                    val startIndex = position
                    while (position < source.length && source[position] != ']') position++
                    val indexEnd = position
                    if (position < source.length) position++
                    index = NMathParser(source.substring(startIndex, indexEnd)).parse()
                }
                NMathSqrt(index, parseGroup())
            }
            "text", "textrm", "textnormal", "mbox", "operatorname" -> NMathText(parseRawGroup())
            "mathrm", "mathbf", "mathit", "mathsf", "mathtt", "mathcal", "mathbb" ->
                NMathRow(parseBraceExpression())
            "begin", "end" -> {
                parseRawGroup()
                null
            }
            "left", "right", "middle", "big", "Big", "bigg", "Bigg", "bigl", "bigr",
            "Bigl", "Bigr", "displaystyle", "textstyle", "scriptstyle", "scriptscriptstyle",
            "limits", "nolimits",
            -> null
            "quad" -> NMathText("\u2003")
            "qquad" -> NMathText("\u2003\u2003")
            "enspace" -> NMathText("\u2002")
            else -> {
                val symbol = nMathSymbols[name]
                val expansion = macros[name]
                when {
                    symbol != null -> NMathText(symbol)
                    expansion != null && name !in macroStack ->
                        NMathParser(expansion, macros, macroStack + name).parse()
                    else -> NMathText(name)
                }
            }
        }
    }
}

private data class NMathRenderStyle(
    val color: Color,
    val fontSize: TextUnit,
)

@Composable
private fun NMathNodeContent(
    node: NMathNode,
    style: NMathRenderStyle,
    modifier: Modifier = Modifier,
) {
    when (node) {
        is NMathText -> {
            if (node.text.isNotEmpty()) {
                BasicText(
                    text = node.text,
                    modifier = modifier,
                    style = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontSize = style.fontSize,
                        lineHeight = style.fontSize * 1.25f,
                        color = style.color,
                    ),
                )
            }
        }
        is NMathRow -> Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            node.children.forEach { NMathNodeContent(it, style) }
        }
        is NMathFraction -> NMathFractionContent(node, style, modifier)
        is NMathScript -> NMathScriptContent(node, style, modifier)
        is NMathSqrt -> NMathSqrtContent(node, style, modifier)
        NMathNewline -> Unit
    }
}

@Composable
private fun NMathFractionContent(
    fraction: NMathFraction,
    style: NMathRenderStyle,
    modifier: Modifier,
) {
    val small = style.copy(fontSize = style.fontSize * 0.92f)
    val density = LocalDensity.current
    val gap = with(density) { (style.fontSize.toDp() * 0.1f).roundToPx() }
    val barHeight = with(density) { 1.dp.roundToPx() }.coerceAtLeast(1)
    Layout(
        modifier = modifier,
        content = {
            Box { NMathNodeContent(fraction.numerator, small) }
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(style.color),
            )
            Box { NMathNodeContent(fraction.denominator, small) }
        },
    ) { measurables, constraints ->
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val numerator = measurables[0].measure(loose)
        val denominator = measurables[2].measure(loose)
        val width = maxOf(numerator.width, denominator.width) + 4.dp.roundToPx()
        val bar = measurables[1].measure(
            constraints.copy(
                minWidth = width,
                maxWidth = width,
                minHeight = barHeight,
                maxHeight = barHeight,
            ),
        )
        val height = numerator.height + denominator.height + barHeight + gap * 2
        layout(width, height) {
            numerator.placeRelative((width - numerator.width) / 2, 0)
            bar.placeRelative(0, numerator.height + gap)
            denominator.placeRelative(
                (width - denominator.width) / 2,
                numerator.height + gap + barHeight + gap,
            )
        }
    }
}

@Composable
private fun NMathScriptContent(
    script: NMathScript,
    style: NMathRenderStyle,
    modifier: Modifier,
) {
    val smallSize = maxOf(style.fontSize.value * 0.7f, 8f).sp
    val small = style.copy(fontSize = smallSize)
    val lift = with(LocalDensity.current) { style.fontSize.toDp() * 0.42f }
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        NMathNodeContent(script.base, style)
        val superscript = script.superscript
        val subscript = script.subscript
        when {
            superscript != null && subscript != null -> Column(Modifier.padding(start = 1.dp)) {
                NMathNodeContent(superscript, small)
                NMathNodeContent(subscript, small)
            }
            superscript != null -> Box(Modifier.padding(start = 1.dp, bottom = lift)) {
                NMathNodeContent(superscript, small)
            }
            subscript != null -> Box(Modifier.padding(start = 1.dp, top = lift)) {
                NMathNodeContent(subscript, small)
            }
        }
    }
}

@Composable
private fun NMathSqrtContent(
    sqrt: NMathSqrt,
    style: NMathRenderStyle,
    modifier: Modifier,
) {
    val lift = with(LocalDensity.current) { style.fontSize.toDp() * 0.5f }
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        val index = sqrt.index
        if (index != null) {
            Box(Modifier.padding(end = 1.dp, bottom = lift)) {
                NMathNodeContent(
                    index,
                    style.copy(fontSize = maxOf(style.fontSize.value * 0.5f, 8f).sp),
                )
            }
        }
        BasicText(
            text = "√",
            style = TextStyle(
                fontFamily = FontFamily.Serif,
                fontSize = style.fontSize * 1.15f,
                color = style.color,
            ),
        )
        Box(
            Modifier
                .nTopBorder(style.color, 1.dp)
                .padding(top = 2.dp, start = 2.dp),
        ) {
            NMathNodeContent(sqrt.radicand, style)
        }
    }
}

/**
 * Renders a LaTeX formula with a lightweight built-in layout engine.
 *
 * Supported syntax: `\frac`, `\dfrac`, `\sqrt` (with optional index), `^` and
 * `_` scripts, `{}` groups, greek letters, common operators and relations,
 * `\text{...}` runs and line breaks (`\\`). Everything else is rendered as
 * plain text, so the component always shows something meaningful.
 */
@Composable
fun NEquation(
    value: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = 18.sp,
    displayMode: Boolean = false,
    macros: Map<String, String> = emptyMap(),
) {
    val theme = LocalNTheme.current
    val resolvedColor = if (color == Color.Unspecified) theme.colors.text1 else color
    val root = remember(value, macros) {
        runCatching { NMathParser(value, macros).parse() }
            .getOrElse { NMathRow(listOf(NMathText(value))) }
    }
    val style = NMathRenderStyle(resolvedColor, fontSize)
    val children = (root as? NMathRow)?.children ?: listOf(root)
    val segments = mutableListOf<MutableList<NMathNode>>()
    var current = mutableListOf<NMathNode>()
    for (child in children) {
        if (child == NMathNewline) {
            segments += current
            current = mutableListOf()
        } else {
            current += child
        }
    }
    segments += current
    val content: @Composable () -> Unit = {
        if (segments.size <= 1) {
            NMathNodeContent(NMathRow(segments.firstOrNull().orEmpty()), style)
        } else {
            Column {
                segments.forEach { NMathNodeContent(NMathRow(it), style) }
            }
        }
    }
    if (displayMode) {
        Box(modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { content() }
    } else {
        Box(modifier) { content() }
    }
}
