package cn.enaium.naiveui.sample.gallery.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NCode
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage

private const val JavaScriptCode = """
function sleep (ms = 1000) {
  return new Promise(resolve => setTimeout(resolve, ms))
}
"""

private const val PythonCode = """
def say_hello():
    print('Hello Naive UI')
"""

private const val CppCode = """
int main () {
  std::cout << "Hello Naive UI";
  return 0;
}
"""

private const val NumberedCode = """#include <bits/stdc++.h>
using namespace std;

int main() {
  cout << "Hello Naive UI" << endl;
  return 0;
}"""

private const val WrappedCode = """function padEnd(string, length, chars) {
  const strLength = length ? stringSize(string) : 0
  return (length && strLength < length) ? (string + createPadding(length - strLength, chars)) : (string || '')
}"""

@Composable
fun CodeDocs() {
    DocsPage(
        title = "Code",
        description = "Display code with theme colors and a built-in syntax highlighter.",
    ) {
        DemoSection(
            title = "Basic",
            description = "JavaScript, Python and C++ are highlighted by the compact tokenizer.",
            code = "NCode(code, language = \"javascript\")",
        ) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                NCode(JavaScriptCode, language = "javascript")
                NCode(PythonCode, language = "python")
                NCode(CppCode, language = "cpp")
            }
        }
        DemoSection(
            title = "Inline",
            description = "Use inline for code inside a paragraph.",
            code = "NCode(code, language = \"javascript\", inline = true)",
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                NText("JavaScript ")
                NCode("console.log(\"Hello World\")", language = "javascript", inline = true)
            }
        }
        DemoSection(
            title = "Line numbers",
            description = "Show the line number gutter with showLineNumbers.",
            code = "NCode(code, language = \"cpp\", showLineNumbers = true)",
        ) {
            NCode(NumberedCode, language = "cpp", showLineNumbers = true)
        }
        DemoSection(
            title = "Soft wrap",
            description = "Long lines wrap instead of scrolling horizontally.",
            code = "NCode(code, language = \"javascript\", wordWrap = true)",
        ) {
            NCode(WrappedCode, language = "javascript", wordWrap = true)
        }
    }
}
