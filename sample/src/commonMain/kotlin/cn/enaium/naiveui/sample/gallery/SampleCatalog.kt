package cn.enaium.naiveui.sample.gallery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

import cn.enaium.naiveui.sample.gallery.general.AvatarDocs
import cn.enaium.naiveui.sample.gallery.general.ButtonDocs
import cn.enaium.naiveui.sample.gallery.general.CardDocs
import cn.enaium.naiveui.sample.gallery.general.CarouselDocs
import cn.enaium.naiveui.sample.gallery.general.CollapseDocs
import cn.enaium.naiveui.sample.gallery.general.DividerDocs
import cn.enaium.naiveui.sample.gallery.general.DropdownDocs
import cn.enaium.naiveui.sample.gallery.general.EllipsisDocs
import cn.enaium.naiveui.sample.gallery.general.GradientTextDocs
import cn.enaium.naiveui.sample.gallery.general.IconDocs
import cn.enaium.naiveui.sample.gallery.general.PageHeaderDocs
import cn.enaium.naiveui.sample.gallery.general.TagDocs
import cn.enaium.naiveui.sample.gallery.general.TypographyDocs
import cn.enaium.naiveui.sample.gallery.general.WatermarkDocs
import cn.enaium.naiveui.sample.gallery.general.FloatButtonDocs
import cn.enaium.naiveui.sample.gallery.dataentry.AutoCompleteDocs
import cn.enaium.naiveui.sample.gallery.dataentry.CascaderDocs
import cn.enaium.naiveui.sample.gallery.dataentry.ColorPickerDocs
import cn.enaium.naiveui.sample.gallery.dataentry.CheckboxDocs
import cn.enaium.naiveui.sample.gallery.dataentry.DatePickerDocs
import cn.enaium.naiveui.sample.gallery.dataentry.DynamicInputDocs
import cn.enaium.naiveui.sample.gallery.dataentry.DynamicTagsDocs
import cn.enaium.naiveui.sample.gallery.dataentry.FormDocs
import cn.enaium.naiveui.sample.gallery.dataentry.InputDocs
import cn.enaium.naiveui.sample.gallery.dataentry.InputNumberDocs
import cn.enaium.naiveui.sample.gallery.dataentry.InputOtpDocs
import cn.enaium.naiveui.sample.gallery.dataentry.MentionDocs
import cn.enaium.naiveui.sample.gallery.dataentry.RadioDocs
import cn.enaium.naiveui.sample.gallery.dataentry.RateDocs
import cn.enaium.naiveui.sample.gallery.dataentry.SelectDocs
import cn.enaium.naiveui.sample.gallery.dataentry.SliderDocs
import cn.enaium.naiveui.sample.gallery.dataentry.SwitchDocs
import cn.enaium.naiveui.sample.gallery.dataentry.TimePickerDocs
import cn.enaium.naiveui.sample.gallery.dataentry.TransferDocs
import cn.enaium.naiveui.sample.gallery.dataentry.TreeSelectDocs
import cn.enaium.naiveui.sample.gallery.dataentry.UploadDocs
import cn.enaium.naiveui.sample.gallery.display.CalendarDocs
import cn.enaium.naiveui.sample.gallery.display.CountdownDocs
import cn.enaium.naiveui.sample.gallery.display.CodeDocs
import cn.enaium.naiveui.sample.gallery.display.DataTableDocs
import cn.enaium.naiveui.sample.gallery.display.DescriptionsDocs
import cn.enaium.naiveui.sample.gallery.display.EmptyDocs
import cn.enaium.naiveui.sample.gallery.display.EquationDocs
import cn.enaium.naiveui.sample.gallery.display.ImageDocs
import cn.enaium.naiveui.sample.gallery.display.ListDocs
import cn.enaium.naiveui.sample.gallery.display.LogDocs
import cn.enaium.naiveui.sample.gallery.display.NumberAnimationDocs
import cn.enaium.naiveui.sample.gallery.display.QrCodeDocs
import cn.enaium.naiveui.sample.gallery.display.StatisticDocs
import cn.enaium.naiveui.sample.gallery.display.TableDocs
import cn.enaium.naiveui.sample.gallery.display.ThingDocs
import cn.enaium.naiveui.sample.gallery.display.TimeDocs
import cn.enaium.naiveui.sample.gallery.display.TimelineDocs
import cn.enaium.naiveui.sample.gallery.display.TreeDocs
import cn.enaium.naiveui.sample.gallery.display.InfiniteScrollDocs
import cn.enaium.naiveui.sample.gallery.display.HighlightDocs
import cn.enaium.naiveui.sample.gallery.display.HeatmapDocs
import cn.enaium.naiveui.sample.gallery.navigation.AffixDocs
import cn.enaium.naiveui.sample.gallery.navigation.AnchorDocs
import cn.enaium.naiveui.sample.gallery.navigation.BackTopDocs
import cn.enaium.naiveui.sample.gallery.navigation.BreadcrumbDocs
import cn.enaium.naiveui.sample.gallery.navigation.LoadingBarDocs
import cn.enaium.naiveui.sample.gallery.navigation.MenuDocs
import cn.enaium.naiveui.sample.gallery.navigation.PaginationDocs
import cn.enaium.naiveui.sample.gallery.navigation.StepsDocs
import cn.enaium.naiveui.sample.gallery.navigation.TabsDocs
import cn.enaium.naiveui.sample.gallery.feedback.AlertDocs
import cn.enaium.naiveui.sample.gallery.feedback.BadgeDocs
import cn.enaium.naiveui.sample.gallery.feedback.DialogDocs
import cn.enaium.naiveui.sample.gallery.feedback.DrawerDocs
import cn.enaium.naiveui.sample.gallery.feedback.MarqueeDocs
import cn.enaium.naiveui.sample.gallery.feedback.MessageDocs
import cn.enaium.naiveui.sample.gallery.feedback.ModalDocs
import cn.enaium.naiveui.sample.gallery.feedback.NotificationDocs
import cn.enaium.naiveui.sample.gallery.feedback.PopconfirmDocs
import cn.enaium.naiveui.sample.gallery.feedback.PopoverDocs
import cn.enaium.naiveui.sample.gallery.feedback.PopselectDocs
import cn.enaium.naiveui.sample.gallery.feedback.ProgressDocs
import cn.enaium.naiveui.sample.gallery.feedback.ResultDocs
import cn.enaium.naiveui.sample.gallery.feedback.SkeletonDocs
import cn.enaium.naiveui.sample.gallery.feedback.SpinDocs
import cn.enaium.naiveui.sample.gallery.feedback.TooltipDocs
import cn.enaium.naiveui.sample.gallery.layout.FlexDocs
import cn.enaium.naiveui.sample.gallery.layout.LayoutDocs
import cn.enaium.naiveui.sample.gallery.layout.GridDocs
import cn.enaium.naiveui.sample.gallery.layout.SpaceDocs
import cn.enaium.naiveui.sample.gallery.layout.SplitDocs
import cn.enaium.naiveui.sample.gallery.utility.CollapseTransitionDocs
import cn.enaium.naiveui.sample.gallery.utility.DiscreteApiDocs
import cn.enaium.naiveui.sample.gallery.utility.ScrollbarDocs
import cn.enaium.naiveui.sample.gallery.utility.VirtualListDocs
import cn.enaium.naiveui.sample.gallery.config.ConfigProviderDocs
import cn.enaium.naiveui.sample.gallery.config.ElementDocs
import cn.enaium.naiveui.sample.gallery.config.GlobalStyleDocs

enum class SampleSection(val title: String) {
    General("General"),
    DataEntry("Data Entry"),
    Display("Data Display"),
    Navigation("Navigation"),
    Feedback("Feedback"),
    Layout("Layout"),
    Utility("Utility"),
    Config("Config"),
}

@Immutable
data class SampleComponent(val id: String, val title: String, val section: SampleSection, val content: @Composable () -> Unit)

fun sampleComponents(): List<SampleComponent> = listOf(
    SampleComponent("avatar", "Avatar", SampleSection.General) { AvatarDocs() },
    SampleComponent("button", "Button", SampleSection.General) { ButtonDocs() },
    SampleComponent("card", "Card", SampleSection.General) { CardDocs() },
    SampleComponent("carousel", "Carousel", SampleSection.General) { CarouselDocs() },
    SampleComponent("collapse", "Collapse", SampleSection.General) { CollapseDocs() },
    SampleComponent("divider", "Divider", SampleSection.General) { DividerDocs() },
    SampleComponent("dropdown", "Dropdown", SampleSection.General) { DropdownDocs() },
    SampleComponent("ellipsis", "Ellipsis", SampleSection.General) { EllipsisDocs() },
    SampleComponent("gradient-text", "Gradient Text", SampleSection.General) { GradientTextDocs() },
    SampleComponent("icon", "Icon", SampleSection.General) { IconDocs() },
    SampleComponent("page-header", "Page Header", SampleSection.General) { PageHeaderDocs() },
    SampleComponent("tag", "Tag", SampleSection.General) { TagDocs() },
    SampleComponent("typography", "Typography", SampleSection.General) { TypographyDocs() },
    SampleComponent("watermark", "Watermark", SampleSection.General) { WatermarkDocs() },
    SampleComponent("float-button", "Float Button", SampleSection.General) { FloatButtonDocs() },
    SampleComponent("auto-complete", "Auto Complete", SampleSection.DataEntry) { AutoCompleteDocs() },
    SampleComponent("cascader", "Cascader", SampleSection.DataEntry) { CascaderDocs() },
    SampleComponent("color-picker", "Color Picker", SampleSection.DataEntry) { ColorPickerDocs() },
    SampleComponent("checkbox", "Checkbox", SampleSection.DataEntry) { CheckboxDocs() },
    SampleComponent("date-picker", "Date Picker", SampleSection.DataEntry) { DatePickerDocs() },
    SampleComponent("dynamic-input", "Dynamic Input", SampleSection.DataEntry) { DynamicInputDocs() },
    SampleComponent("dynamic-tags", "Dynamic Tags", SampleSection.DataEntry) { DynamicTagsDocs() },
    SampleComponent("form", "Form", SampleSection.DataEntry) { FormDocs() },
    SampleComponent("input", "Input", SampleSection.DataEntry) { InputDocs() },
    SampleComponent("input-number", "Input Number", SampleSection.DataEntry) { InputNumberDocs() },
    SampleComponent("input-otp", "Input OTP", SampleSection.DataEntry) { InputOtpDocs() },
    SampleComponent("mention", "Mention", SampleSection.DataEntry) { MentionDocs() },
    SampleComponent("radio", "Radio", SampleSection.DataEntry) { RadioDocs() },
    SampleComponent("rate", "Rate", SampleSection.DataEntry) { RateDocs() },
    SampleComponent("select", "Select", SampleSection.DataEntry) { SelectDocs() },
    SampleComponent("slider", "Slider", SampleSection.DataEntry) { SliderDocs() },
    SampleComponent("switch", "Switch", SampleSection.DataEntry) { SwitchDocs() },
    SampleComponent("time-picker", "Time Picker", SampleSection.DataEntry) { TimePickerDocs() },
    SampleComponent("transfer", "Transfer", SampleSection.DataEntry) { TransferDocs() },
    SampleComponent("tree-select", "Tree Select", SampleSection.DataEntry) { TreeSelectDocs() },
    SampleComponent("upload", "Upload", SampleSection.DataEntry) { UploadDocs() },
    SampleComponent("calendar", "Calendar", SampleSection.Display) { CalendarDocs() },
    SampleComponent("countdown", "Countdown", SampleSection.Display) { CountdownDocs() },
    SampleComponent("code", "Code", SampleSection.Display) { CodeDocs() },
    SampleComponent("data-table", "Data Table", SampleSection.Display) { DataTableDocs() },
    SampleComponent("descriptions", "Descriptions", SampleSection.Display) { DescriptionsDocs() },
    SampleComponent("empty", "Empty", SampleSection.Display) { EmptyDocs() },
    SampleComponent("equation", "Equation", SampleSection.Display) { EquationDocs() },
    SampleComponent("image", "Image", SampleSection.Display) { ImageDocs() },
    SampleComponent("list", "List", SampleSection.Display) { ListDocs() },
    SampleComponent("log", "Log", SampleSection.Display) { LogDocs() },
    SampleComponent("number-animation", "Number Animation", SampleSection.Display) { NumberAnimationDocs() },
    SampleComponent("qr-code", "QR Code", SampleSection.Display) { QrCodeDocs() },
    SampleComponent("statistic", "Statistic", SampleSection.Display) { StatisticDocs() },
    SampleComponent("table", "Table", SampleSection.Display) { TableDocs() },
    SampleComponent("thing", "Thing", SampleSection.Display) { ThingDocs() },
    SampleComponent("time", "Time", SampleSection.Display) { TimeDocs() },
    SampleComponent("timeline", "Timeline", SampleSection.Display) { TimelineDocs() },
    SampleComponent("tree", "Tree", SampleSection.Display) { TreeDocs() },
    SampleComponent("infinite-scroll", "Infinite Scroll", SampleSection.Display) { InfiniteScrollDocs() },
    SampleComponent("highlight", "Highlight", SampleSection.Display) { HighlightDocs() },
    SampleComponent("heatmap", "Heatmap", SampleSection.Display) { HeatmapDocs() },
    SampleComponent("affix", "Affix", SampleSection.Navigation) { AffixDocs() },
    SampleComponent("anchor", "Anchor", SampleSection.Navigation) { AnchorDocs() },
    SampleComponent("back-top", "Back Top", SampleSection.Navigation) { BackTopDocs() },
    SampleComponent("breadcrumb", "Breadcrumb", SampleSection.Navigation) { BreadcrumbDocs() },
    SampleComponent("loading-bar", "Loading Bar", SampleSection.Navigation) { LoadingBarDocs() },
    SampleComponent("menu", "Menu", SampleSection.Navigation) { MenuDocs() },
    SampleComponent("pagination", "Pagination", SampleSection.Navigation) { PaginationDocs() },
    SampleComponent("steps", "Steps", SampleSection.Navigation) { StepsDocs() },
    SampleComponent("tabs", "Tabs", SampleSection.Navigation) { TabsDocs() },
    SampleComponent("alert", "Alert", SampleSection.Feedback) { AlertDocs() },
    SampleComponent("badge", "Badge", SampleSection.Feedback) { BadgeDocs() },
    SampleComponent("dialog", "Dialog", SampleSection.Feedback) { DialogDocs() },
    SampleComponent("drawer", "Drawer", SampleSection.Feedback) { DrawerDocs() },
    SampleComponent("marquee", "Marquee", SampleSection.Feedback) { MarqueeDocs() },
    SampleComponent("message", "Message", SampleSection.Feedback) { MessageDocs() },
    SampleComponent("modal", "Modal", SampleSection.Feedback) { ModalDocs() },
    SampleComponent("notification", "Notification", SampleSection.Feedback) { NotificationDocs() },
    SampleComponent("popconfirm", "Popconfirm", SampleSection.Feedback) { PopconfirmDocs() },
    SampleComponent("popover", "Popover", SampleSection.Feedback) { PopoverDocs() },
    SampleComponent("popselect", "Popselect", SampleSection.Feedback) { PopselectDocs() },
    SampleComponent("progress", "Progress", SampleSection.Feedback) { ProgressDocs() },
    SampleComponent("result", "Result", SampleSection.Feedback) { ResultDocs() },
    SampleComponent("skeleton", "Skeleton", SampleSection.Feedback) { SkeletonDocs() },
    SampleComponent("spin", "Spin", SampleSection.Feedback) { SpinDocs() },
    SampleComponent("tooltip", "Tooltip", SampleSection.Feedback) { TooltipDocs() },
    SampleComponent("flex", "Flex", SampleSection.Layout) { FlexDocs() },
    SampleComponent("layout", "Layout", SampleSection.Layout) { LayoutDocs() },
    SampleComponent("grid", "Grid", SampleSection.Layout) { GridDocs() },
    SampleComponent("space", "Space", SampleSection.Layout) { SpaceDocs() },
    SampleComponent("split", "Split", SampleSection.Layout) { SplitDocs() },
    SampleComponent("collapse-transition", "Collapse Transition", SampleSection.Utility) { CollapseTransitionDocs() },
    SampleComponent("discrete-api", "Discrete API", SampleSection.Utility) { DiscreteApiDocs() },
    SampleComponent("scrollbar", "Scrollbar", SampleSection.Utility) { ScrollbarDocs() },
    SampleComponent("virtual-list", "Virtual List", SampleSection.Utility) { VirtualListDocs() },
    SampleComponent("config-provider", "Config Provider", SampleSection.Config) { ConfigProviderDocs() },
    SampleComponent("element", "Element", SampleSection.Config) { ElementDocs() },
    SampleComponent("global-style", "Global Style", SampleSection.Config) { GlobalStyleDocs() },
)
