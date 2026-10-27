package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NTheme

/** Colors used by the built-in code highlighter. */
@Immutable
internal data class NCodePalette(
    val keyword: Color,
    val string: Color,
    val number: Color,
    val comment: Color,
    val function: Color,
)

internal fun nCodePalette(theme: NTheme): NCodePalette = if (theme.isDark) {
    NCodePalette(
        keyword = Color(0xFFC678DD),
        string = Color(0xFF98C379),
        number = Color(0xFFD19A66),
        comment = Color(0xFF5C6370),
        function = Color(0xFF61AEEE),
    )
} else {
    NCodePalette(
        keyword = Color(0xFFA626A4),
        string = Color(0xFF50A14F),
        number = Color(0xFF986801),
        comment = Color(0xFFA0A1A7),
        function = Color(0xFF4078F2),
    )
}

private data class NCodeSpec(
    val keywords: Set<String> = emptySet(),
    val constants: Set<String> = emptySet(),
    val lineComments: List<String> = listOf("//"),
    val blockComments: Boolean = true,
    val quotes: Set<Char> = setOf('"', '\''),
    val backticks: Boolean = false,
    val annotations: Boolean = false,
    val tripleQuotes: Boolean = false,
)

private val nJsKeywords = setOf(
    "function", "const", "let", "var", "return", "if", "else", "for", "while", "do",
    "switch", "case", "default", "break", "continue", "new", "class", "extends", "super",
    "this", "typeof", "instanceof", "in", "of", "try", "catch", "finally", "throw",
    "async", "await", "yield", "import", "export", "from", "delete", "void", "static",
    "get", "set", "interface", "type", "enum", "implements", "abstract", "declare",
    "namespace", "module", "readonly", "public", "private", "protected", "as", "is",
    "keyof", "never", "unknown", "any", "string", "number", "boolean", "symbol", "bigint",
    "object", "satisfies",
)

private val nCodeLanguages: Map<String, NCodeSpec> = mapOf(
    "javascript" to NCodeSpec(keywords = nJsKeywords, constants = setOf("null", "undefined", "true", "false", "NaN", "Infinity")),
    "typescript" to NCodeSpec(keywords = nJsKeywords, constants = setOf("null", "undefined", "true", "false", "NaN", "Infinity"), annotations = true),
    "python" to NCodeSpec(
        keywords = setOf(
            "def", "return", "if", "elif", "else", "for", "while", "in", "not", "and", "or",
            "is", "class", "import", "from", "as", "try", "except", "finally", "raise",
            "with", "yield", "lambda", "global", "nonlocal", "pass", "break", "continue",
            "assert", "del", "async", "await", "match", "case",
        ),
        constants = setOf("None", "True", "False", "self", "cls"),
        lineComments = listOf("#"),
        blockComments = false,
        tripleQuotes = true,
    ),
    "kotlin" to NCodeSpec(
        keywords = setOf(
            "fun", "val", "var", "class", "object", "interface", "data", "enum", "sealed",
            "when", "if", "else", "for", "while", "do", "return", "break", "continue", "is",
            "as", "in", "out", "by", "where", "companion", "init", "constructor", "this",
            "super", "private", "public", "protected", "internal", "open", "override",
            "abstract", "final", "lateinit", "suspend", "inline", "vararg", "typealias",
            "import", "package", "try", "catch", "finally", "throw", "assert", "annotation",
            "crossinline", "noinline", "reified", "operator", "infix", "tailrec", "external",
        ),
        constants = setOf("null", "true", "false"),
        annotations = true,
    ),
    "java" to NCodeSpec(
        keywords = setOf(
            "public", "private", "protected", "class", "interface", "enum", "record", "extends",
            "implements", "static", "final", "void", "int", "long", "double", "float", "boolean",
            "char", "byte", "short", "new", "return", "if", "else", "for", "while", "do",
            "switch", "case", "default", "break", "continue", "try", "catch", "finally",
            "throw", "throws", "import", "package", "this", "super", "abstract", "synchronized",
            "volatile", "transient", "native", "instanceof", "var",
        ),
        constants = setOf("null", "true", "false"),
        annotations = true,
    ),
    "cpp" to NCodeSpec(
        keywords = setOf(
            "int", "float", "double", "char", "void", "bool", "long", "short", "unsigned",
            "signed", "class", "struct", "enum", "namespace", "using", "template", "typename",
            "const", "constexpr", "static", "inline", "virtual", "override", "public", "private",
            "protected", "new", "delete", "return", "if", "else", "for", "while", "do",
            "switch", "case", "default", "break", "continue", "try", "catch", "throw",
            "include", "define", "std", "cout", "cin", "endl", "auto",
        ),
        constants = setOf("true", "false", "nullptr", "NULL"),
    ),
    "c" to NCodeSpec(
        keywords = setOf(
            "int", "float", "double", "char", "void", "long", "short", "unsigned", "signed",
            "struct", "union", "enum", "typedef", "const", "static", "inline", "extern",
            "return", "if", "else", "for", "while", "do", "switch", "case", "default",
            "break", "continue", "goto", "sizeof", "include", "define",
        ),
        constants = setOf("NULL", "true", "false"),
    ),
    "csharp" to NCodeSpec(
        keywords = setOf(
            "using", "namespace", "class", "struct", "interface", "enum", "record", "public",
            "private", "protected", "internal", "static", "readonly", "const", "void", "int",
            "string", "bool", "double", "float", "decimal", "var", "new", "return", "if",
            "else", "for", "foreach", "while", "do", "switch", "case", "break", "continue",
            "try", "catch", "finally", "throw", "this", "base", "async", "await", "in", "is",
            "as", "out", "ref", "params", "get", "set", "partial",
        ),
        constants = setOf("null", "true", "false"),
        annotations = true,
    ),
    "go" to NCodeSpec(
        keywords = setOf(
            "func", "var", "const", "type", "struct", "interface", "map", "chan", "package",
            "import", "return", "if", "else", "for", "range", "switch", "case", "default",
            "break", "continue", "go", "defer", "select", "make", "new", "append", "len", "cap",
            "type", "fallthrough", "goto",
        ),
        constants = setOf("nil", "true", "false"),
        backticks = true,
    ),
    "rust" to NCodeSpec(
        keywords = setOf(
            "fn", "let", "mut", "const", "struct", "enum", "trait", "impl", "for", "in", "if",
            "else", "match", "while", "loop", "break", "continue", "return", "use", "mod",
            "pub", "crate", "self", "Self", "super", "where", "as", "move", "ref", "static",
            "unsafe", "async", "await", "dyn", "type",
        ),
        constants = setOf("None", "Some", "Ok", "Err", "true", "false"),
    ),
    "swift" to NCodeSpec(
        keywords = setOf(
            "func", "var", "let", "class", "struct", "enum", "protocol", "extension", "import",
            "return", "if", "else", "guard", "for", "in", "while", "repeat", "switch", "case",
            "default", "break", "continue", "self", "super", "init", "deinit", "public",
            "private", "internal", "fileprivate", "open", "static", "mutating", "override",
            "async", "await", "throws", "try", "where", "typealias", "defer",
        ),
        constants = setOf("nil", "true", "false"),
    ),
    "bash" to NCodeSpec(
        keywords = setOf(
            "if", "then", "else", "elif", "fi", "for", "do", "done", "while", "case", "esac",
            "function", "echo", "export", "local", "return", "in", "select", "until", "time",
        ),
        lineComments = listOf("#"),
        blockComments = false,
        quotes = setOf('"', '\''),
        backticks = true,
    ),
    "json" to NCodeSpec(
        constants = setOf("true", "false", "null"),
        lineComments = emptyList(),
        blockComments = false,
        quotes = setOf('"'),
    ),
    "css" to NCodeSpec(
        lineComments = emptyList(),
        blockComments = true,
    ),
    "html" to NCodeSpec(
        lineComments = emptyList(),
        blockComments = true,
    ),
    "sql" to NCodeSpec(
        keywords = setOf(
            "select", "from", "where", "insert", "into", "update", "delete", "create", "table",
            "drop", "alter", "join", "inner", "left", "right", "outer", "on", "group", "by",
            "order", "limit", "offset", "values", "set", "and", "or", "not", "as", "distinct",
            "having", "union", "all", "case", "when", "then", "else", "end",
        ),
        constants = setOf("null", "true", "false"),
        lineComments = listOf("--"),
        blockComments = true,
        quotes = setOf('\'', '"'),
    ),
)

private fun normalizeLanguage(language: String?): String = when (val value = language?.trim()?.lowercase()) {
    null, "" -> ""
    "js", "jsx", "mjs", "cjs", "javascript", "node" -> "javascript"
    "ts", "tsx", "typescript" -> "typescript"
    "py", "py3", "python", "python3" -> "python"
    "kt", "kts", "kotlin" -> "kotlin"
    "c++", "cpp", "cxx", "cc", "hpp" -> "cpp"
    "c", "h" -> "c"
    "cs", "c#", "csharp" -> "csharp"
    "go", "golang" -> "go"
    "rs", "rust" -> "rust"
    "swift" -> "swift"
    "sh", "bash", "shell", "zsh", "console", "shellscript" -> "bash"
    "json" -> "json"
    "html", "xml", "vue", "svg" -> "html"
    "css", "scss", "less" -> "css"
    "sql", "mysql", "postgres", "postgresql" -> "sql"
    else -> value
}

private const val nIdentifierExtra = "_$"

private fun isIdentifierStart(c: Char): Boolean = c.isLetter() || c in nIdentifierExtra

private fun isIdentifierPart(c: Char): Boolean = c.isLetterOrDigit() || c in nIdentifierExtra

private fun nReadStringEnd(code: String, start: Int, quote: Char, tripled: Boolean): Int {
    var i = start + 1
    while (i < code.length) {
        val c = code[i]
        if (c == '\\') {
            i += 2
            continue
        }
        if (!tripled && c == '\n') return i
        if (c == quote) {
            if (tripled) {
                if (i + 2 < code.length && code[i + 1] == quote && code[i + 2] == quote) return i + 3
            } else {
                return i + 1
            }
        }
        i++
    }
    return code.length
}

private fun nReadNumberEnd(code: String, start: Int): Int {
    var i = start
    if (code[i] == '0' && i + 1 < code.length && code[i + 1] in "xXbBoO") {
        i += 2
        while (i < code.length && (code[i].isLetterOrDigit() || code[i] == '_')) i++
        return i
    }
    while (i < code.length && (code[i].isDigit() || code[i] == '_')) i++
    if (i < code.length && code[i] == '.' && i + 1 < code.length && code[i + 1].isDigit()) {
        i++
        while (i < code.length && (code[i].isDigit() || code[i] == '_')) i++
    }
    if (i < code.length && (code[i] == 'e' || code[i] == 'E')) {
        val next = code.getOrNull(i + 1)
        val afterSign = code.getOrNull(i + 2)
        if (next?.isDigit() == true || ((next == '+' || next == '-') && afterSign?.isDigit() == true)) {
            i++
            if (code[i] == '+' || code[i] == '-') i++
            while (i < code.length && code[i].isDigit()) i++
        }
    }
    while (i < code.length && code[i] in "fFlLdDuU") i++
    return i
}

private data class NCodeToken(val start: Int, val end: Int, val color: Color)

private fun nTokenize(code: String, spec: NCodeSpec, palette: NCodePalette): List<NCodeToken> {
    val tokens = mutableListOf<NCodeToken>()
    var i = 0
    while (i < code.length) {
        val c = code[i]
        // Comments
        val lineComment = spec.lineComments.firstOrNull { code.startsWith(it, i) }
        if (lineComment != null) {
            var end = code.indexOf('\n', i)
            if (end < 0) end = code.length
            tokens += NCodeToken(i, end, palette.comment)
            i = end
            continue
        }
        if (spec.blockComments && code.startsWith("/*", i)) {
            var end = code.indexOf("*/", i + 2)
            end = if (end < 0) code.length else end + 2
            tokens += NCodeToken(i, end, palette.comment)
            i = end
            continue
        }
        if (spec.blockComments && code.startsWith("<!--", i)) {
            var end = code.indexOf("-->", i + 4)
            end = if (end < 0) code.length else end + 3
            tokens += NCodeToken(i, end, palette.comment)
            i = end
            continue
        }
        // Strings
        val tripleQuote = "\"\"\""
        val tripled = spec.tripleQuotes && (code.startsWith(tripleQuote, i) || code.startsWith("'''", i))
        if (c in spec.quotes) {
            val end = nReadStringEnd(code, i, c, tripled)
            tokens += NCodeToken(i, end, palette.string)
            i = end
            continue
        }
        if (spec.backticks && c == '`') {
            val end = nReadStringEnd(code, i, '`', false)
            tokens += NCodeToken(i, end, palette.string)
            i = end
            continue
        }
        // Numbers
        if (c.isDigit() || (c == '.' && code.getOrNull(i + 1)?.isDigit() == true)) {
            val end = nReadNumberEnd(code, i)
            tokens += NCodeToken(i, end, palette.number)
            i = end
            continue
        }
        // Annotations / decorators
        if (spec.annotations && c == '@' && isIdentifierStart(code.getOrNull(i + 1) ?: ' ')) {
            var end = i + 1
            while (end < code.length && isIdentifierPart(code[end])) end++
            tokens += NCodeToken(i, end, palette.function)
            i = end
            continue
        }
        // Identifiers, keywords and call sites
        if (isIdentifierStart(c)) {
            var end = i
            while (end < code.length && isIdentifierPart(code[end])) end++
            val word = code.substring(i, end)
            val color = when {
                word in spec.keywords -> palette.keyword
                word in spec.constants -> palette.number
                isCallSite(code, end) -> palette.function
                else -> null
            }
            if (color != null) tokens += NCodeToken(i, end, color)
            i = end
            continue
        }
        i++
    }
    return tokens
}

/**
 * Highlights [code] with the compact built-in tokenizer. Returns [code]
 * unstyled when the language is unknown or tokenization is unavailable.
 */
internal fun nHighlightedCode(
    code: String,
    language: String?,
    palette: NCodePalette,
): AnnotatedString {
    val spec = nCodeLanguages[normalizeLanguage(language)] ?: return AnnotatedString(code)
    val tokens = nTokenize(code, spec, palette)
    return buildAnnotatedString {
        append(code)
        tokens.forEach { token ->
            addStyle(SpanStyle(color = token.color), token.start, token.end)
        }
    }
}

private fun isCallSite(code: String, index: Int): Boolean {
    var i = index
    while (i < code.length && code[i] == ' ') i++
    return i < code.length && code[i] == '('
}

/**
 * Displays code with naive-ui's theme colors and an optional compact syntax
 * highlighter for the common languages (JavaScript, TypeScript, Python,
 * Kotlin, Java, C/C++, C#, Go, Rust, Swift, Bash, JSON, HTML, CSS and SQL).
 *
 * Pass [hljs] to plug in a custom highlighter; [highlight] disables the
 * built-in one while keeping the theme styling.
 */
@Composable
fun NCode(
    code: String,
    modifier: Modifier = Modifier,
    language: String? = null,
    wordWrap: Boolean = false,
    showLineNumbers: Boolean = false,
    inline: Boolean = false,
    trim: Boolean = true,
    highlight: Boolean = true,
    fontSize: TextUnit? = null,
    hljs: ((code: String, language: String?) -> AnnotatedString)? = null,
) {
    val theme = LocalNTheme.current
    val palette = remember(theme) { nCodePalette(theme) }
    val source = if (trim) code.trim() else code
    val resolvedFontSize = fontSize ?: theme.typography.fontSize.medium
    val textStyle = TextStyle(
        fontFamily = theme.typography.monoFamily,
        fontSize = resolvedFontSize,
        lineHeight = resolvedFontSize * 1.6f,
        color = theme.colors.text2,
    )
    val annotated = remember(source, language, highlight, palette, hljs) {
        when {
            hljs != null -> hljs(source, language)
            !highlight || language.isNullOrBlank() -> AnnotatedString(source)
            else -> nHighlightedCode(source, language, palette)
        }
    }
    if (inline) {
        Box(
            modifier
                .clip(RoundedCornerShape(theme.borderRadiusSmall))
                .background(theme.colors.code)
                .padding(horizontal = 4.dp, vertical = 1.dp),
        ) {
            BasicText(text = annotated, style = textStyle)
        }
        return
    }
    val showNumbers = showLineNumbers && !wordWrap
    val horizontalScroll = rememberScrollState()
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(theme.borderRadius))
            .background(theme.colors.code)
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        if (showNumbers) {
            BasicText(
                text = nCodeLineNumbers(source),
                modifier = Modifier.padding(end = 12.dp),
                style = textStyle.copy(color = theme.colors.text3),
                softWrap = false,
            )
        }
        Box(
            Modifier
                .weight(1f)
                .then(if (wordWrap) Modifier else Modifier.horizontalScroll(horizontalScroll)),
        ) {
            BasicText(
                text = annotated,
                style = textStyle,
                softWrap = wordWrap,
            )
        }
    }
}

private fun nCodeLineNumbers(code: String): String {
    val count = code.count { it == '\n' } + 1
    return (1..count).joinToString("\n")
}
