package cn.enaium.naiveui.sample.gallery.display

import cn.enaium.naiveui.components.NTableRow
import cn.enaium.naiveui.components.nRow

internal val dtSongs = listOf(
    nRow("key" to "1", "no" to 3, "title" to "Wonderwall", "length" to "4:18"),
    nRow("key" to "2", "no" to 4, "title" to "Don't Look Back in Anger", "length" to "4:48"),
    nRow("key" to "3", "no" to 12, "title" to "Champagne Supernova", "length" to "7:27"),
    nRow("key" to "4", "no" to 7, "title" to "Supersonic", "length" to "4:43"),
    nRow("key" to "5", "no" to 9, "title" to "Live Forever", "length" to "4:36"),
)

internal val dtPeople = listOf(
    nRow("key" to "0", "name" to "John Brown", "age" to 32, "address" to "New York No. 1 Lake Park"),
    nRow("key" to "1", "name" to "Jim Green", "age" to 42, "address" to "London No. 1 Lake Park"),
    nRow("key" to "2", "name" to "Joe Black", "age" to 32, "address" to "Sidney No. 1 Lake Park"),
    nRow("key" to "3", "name" to "Jim Red", "age" to 32, "address" to "London No. 2 Lake Park"),
)

internal val dtPeople46 = List(46) { index ->
    nRow(
        "key" to index.toString(),
        "name" to "Edward King $index",
        "age" to 32,
        "address" to "London, Park Lane no. $index",
    )
}

internal val dtScores = listOf(
    nRow(
        "key" to "0",
        "name" to "John Brown",
        "age" to 32,
        "address" to "New York No. 1 Lake Park",
        "chinese" to 98,
        "math" to 60,
        "english" to 70,
        "score" to 89,
    ),
    nRow(
        "key" to "1",
        "name" to "Jim Green",
        "age" to 42,
        "address" to "London No. 1 Lake Park",
        "chinese" to 98,
        "math" to 66,
        "english" to 89,
        "score" to 92,
    ),
    nRow(
        "key" to "2",
        "name" to "Joe Black",
        "age" to 32,
        "address" to "Sidney No. 1 Lake Park",
        "chinese" to 98,
        "math" to 66,
        "english" to 89,
        "score" to 78,
    ),
    nRow(
        "key" to "3",
        "name" to "Jim Red",
        "age" to 32,
        "address" to "London No. 2 Lake Park",
        "chinese" to 88,
        "math" to 99,
        "english" to 89,
        "score" to 85,
    ),
    nRow(
        "key" to "4",
        "name" to "Anna White",
        "age" to 27,
        "address" to "New York No. 2 Lake Park",
        "chinese" to 76,
        "math" to 88,
        "english" to 92,
        "score" to 90,
    ),
)

internal val dtTagPeople = listOf(
    nRow(
        "key" to "0",
        "name" to "John Brown",
        "age" to 32,
        "address" to "New York No. 1 Lake Park",
        "tags" to listOf("nice", "developer"),
    ),
    nRow(
        "key" to "1",
        "name" to "Jim Green",
        "age" to 42,
        "address" to "London No. 1 Lake Park",
        "tags" to listOf("wow"),
    ),
    nRow(
        "key" to "2",
        "name" to "Joe Black",
        "age" to 32,
        "address" to "Sidney No. 1 Lake Park",
        "tags" to listOf("cool", "teacher"),
    ),
)

internal val dtFixedRows = List(46) { index ->
    nRow(
        "key" to index.toString(),
        "name" to "Edward King $index",
        "age" to 32,
        "row" to "row $index",
        "row1" to "row $index",
        "row2" to "row $index",
        "address" to "London, Park Lane no. $index",
    )
}

internal fun NTableRow.dtWith(key: String, value: Any?): NTableRow =
    NTableRow(values + (key to value))
