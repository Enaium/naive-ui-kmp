package cn.enaium.naiveui.sample.gallery

import cn.enaium.naiveui.theme.NLocale

private val chineseComponentTitles = mapOf(
    "avatar" to "头像",
    "button" to "按钮",
    "card" to "卡片",
    "carousel" to "轮播图",
    "collapse" to "折叠面板",
    "divider" to "分割线",
    "dropdown" to "下拉菜单",
    "ellipsis" to "文本省略",
    "gradient-text" to "渐变文字",
    "icon" to "图标",
    "page-header" to "页头",
    "tag" to "标签",
    "typography" to "排印",
    "watermark" to "水印",
    "float-button" to "浮动按钮",
    "auto-complete" to "自动填充",
    "cascader" to "级联选择",
    "color-picker" to "颜色选择器",
    "checkbox" to "复选框",
    "date-picker" to "日期选择器",
    "dynamic-input" to "动态录入",
    "dynamic-tags" to "动态标签",
    "form" to "表单",
    "input" to "文本输入",
    "input-number" to "数字输入",
    "input-otp" to "验证码",
    "mention" to "提及",
    "radio" to "单选",
    "rate" to "评分",
    "select" to "选择器",
    "slider" to "滑动选择",
    "switch" to "开关",
    "time-picker" to "时间选择器",
    "transfer" to "穿梭框",
    "tree-select" to "树型选择",
    "upload" to "上传",
    "calendar" to "日历",
    "countdown" to "倒计时",
    "code" to "代码",
    "data-table" to "数据表格",
    "descriptions" to "描述",
    "empty" to "无内容",
    "equation" to "公式",
    "image" to "图像",
    "list" to "列表",
    "log" to "日志",
    "number-animation" to "数值动画",
    "qr-code" to "二维码",
    "statistic" to "统计数据",
    "table" to "表格",
    "thing" to "东西",
    "time" to "时间",
    "timeline" to "时间线",
    "tree" to "树",
    "infinite-scroll" to "无限滚动",
    "highlight" to "高亮文本",
    "heatmap" to "热力图",
    "affix" to "固钉",
    "anchor" to "侧边导航",
    "back-top" to "回到顶部",
    "breadcrumb" to "面包屑",
    "loading-bar" to "加载条",
    "menu" to "菜单",
    "pagination" to "分页",
    "steps" to "步骤",
    "tabs" to "标签页",
    "alert" to "警告信息",
    "badge" to "标记",
    "dialog" to "对话框",
    "drawer" to "抽屉",
    "marquee" to "跑马灯",
    "message" to "信息",
    "modal" to "模态框",
    "notification" to "通知",
    "popconfirm" to "弹出确认",
    "popover" to "弹出信息",
    "popselect" to "弹出选择",
    "progress" to "进度",
    "result" to "结果页",
    "skeleton" to "骨架屏",
    "spin" to "加载",
    "tooltip" to "弹出提示",
    "flex" to "弹性布局",
    "layout" to "布局",
    "grid" to "栅格",
    "space" to "间距",
    "split" to "面板分割",
    "collapse-transition" to "折叠渐变",
    "discrete-api" to "独立 API",
    "scrollbar" to "滚动条",
    "virtual-list" to "虚拟列表",
    "config-provider" to "全局化配置",
    "element" to "元素",
    "global-style" to "全局样式",
)

private val chineseSectionTitles = mapOf(
    SampleSection.General to "通用组件",
    SampleSection.DataEntry to "数据录入",
    SampleSection.Display to "数据展示",
    SampleSection.Navigation to "导航",
    SampleSection.Feedback to "反馈",
    SampleSection.Layout to "布局",
    SampleSection.Utility to "工具",
    SampleSection.Config to "配置",
)

/** "按钮 Button" in Chinese; the English title elsewhere. */
fun SampleComponent.displayTitle(locale: NLocale): String {
    if (!locale.name.startsWith("zh")) return title
    val translation = chineseComponentTitles[id] ?: return title
    return "$translation $title"
}

fun SampleSection.displayTitle(locale: NLocale): String =
    if (locale.name.startsWith("zh")) chineseSectionTitles[this] ?: title else title

/** Docs page headings use the same "English 中文" treatment as the sidebar. */
fun localizedDocsTitle(title: String, locale: NLocale): String {
    if (!locale.name.startsWith("zh")) return title
    val component = sampleComponents().firstOrNull { it.title == title } ?: return title
    val translation = chineseComponentTitles[component.id] ?: return title
    return "$translation $title"
}
