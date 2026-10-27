# Naive UI for Kotlin Multiplatform

![](https://img.cdn1.vip/i/6ae02b2731388_1793076007.webp)

A Compose Multiplatform port of [naive-ui](https://github.com/tusen-ai/naive-ui) (Vue 3) for
Android, iOS, Desktop and Web.

The library ships all 94 components in scope, the naive-ui light and dark themes, theme overrides,
79 locales and right-to-left layouts. An interactive gallery mirrors the upstream documentation and
provides 750+ runnable demos.

**Live gallery:** [https://enaium.github.io/naive-ui-kmp/](https://enaium.github.io/naive-ui-kmp/)

## Installation

```kotlin
implementation("cn.enaium.naiveui:naive-ui-kmp:1.1.0")
```

## Features

- Complete naive-ui component set with the original visual language and API surface.
- Light and dark themes derived from naive-ui's design tokens, plus global and component overrides.
- All 79 naive-ui locales, including localized dates, weekdays, pagination, pickers and validation
  messages. RTL languages are supported.
- Naive UI icons ported as Compose vectors.
- Anchored popups for tooltips, popovers, dropdowns, selects, cascades, pickers and menus.
- Overlay scrollbars, virtual lists, remote images and QR codes.
- Interactive documentation gallery with a searchable component menu. In Chinese the menu shows
  bilingual names such as `按钮 Button`.

## Component coverage

Every component name links to its sample Kotlin source file.

| Category | Components |
| --- | --- |
| General | [Avatar](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/general/AvatarDocs.kt) · [Button](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/general/ButtonDocs.kt) · [Card](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/general/CardDocs.kt) · [Carousel](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/general/CarouselDocs.kt) · [Collapse](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/general/CollapseDocs.kt) · [Divider](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/general/DividerDocs.kt) · [Dropdown](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/general/DropdownDocs.kt) · [Ellipsis](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/general/EllipsisDocs.kt) · [Gradient Text](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/general/GradientTextDocs.kt) · [Icon](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/general/IconDocs.kt) · [Page Header](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/general/PageHeaderDocs.kt) · [Tag](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/general/TagDocs.kt) · [Typography](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/general/TypographyDocs.kt) · [Watermark](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/general/WatermarkDocs.kt) · [Float Button](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/general/FloatButtonDocs.kt) |
| Data Entry | [Auto Complete](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/dataentry/AutoCompleteDocs.kt) · [Cascader](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/dataentry/CascaderDocs.kt) · [Color Picker](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/dataentry/ColorPickerDocs.kt) · [Checkbox](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/dataentry/CheckboxDocs.kt) · [Date Picker](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/dataentry/DatePickerDocs.kt) · [Dynamic Input](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/dataentry/DynamicInputDocs.kt) · [Dynamic Tags](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/dataentry/DynamicTagsDocs.kt) · [Form](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/dataentry/FormDocs.kt) · [Input](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/dataentry/InputDocs.kt) · [Input Number](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/dataentry/InputNumberDocs.kt) · [Input OTP](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/dataentry/InputOtpDocs.kt) · [Mention](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/dataentry/MentionDocs.kt) · [Radio](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/dataentry/RadioDocs.kt) · [Rate](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/dataentry/RateDocs.kt) · [Select](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/dataentry/SelectDocs.kt) · [Slider](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/dataentry/SliderDocs.kt) · [Switch](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/dataentry/SwitchDocs.kt) · [Time Picker](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/dataentry/TimePickerDocs.kt) · [Transfer](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/dataentry/TransferDocs.kt) · [Tree Select](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/dataentry/TreeSelectDocs.kt) · [Upload](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/dataentry/UploadDocs.kt) |
| Data Display | [Calendar](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/display/CalendarDocs.kt) · [Countdown](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/display/CountdownDocs.kt) · [Code](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/display/CodeDocs.kt) · [Data Table](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/display/DataTableDocs.kt) · [Descriptions](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/display/DescriptionsDocs.kt) · [Empty](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/display/EmptyDocs.kt) · [Equation](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/display/EquationDocs.kt) · [Image](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/display/ImageDocs.kt) · [List](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/display/ListDocs.kt) · [Log](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/display/LogDocs.kt) · [Number Animation](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/display/NumberAnimationDocs.kt) · [QR Code](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/display/QrCodeDocs.kt) · [Statistic](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/display/StatisticDocs.kt) · [Table](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/display/TableDocs.kt) · [Thing](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/display/ThingDocs.kt) · [Time](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/display/TimeDocs.kt) · [Timeline](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/display/TimelineDocs.kt) · [Tree](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/display/TreeDocs.kt) · [Infinite Scroll](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/display/InfiniteScrollDocs.kt) · [Highlight](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/display/HighlightDocs.kt) · [Heatmap](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/display/HeatmapDocs.kt) |
| Navigation | [Affix](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/navigation/AffixDocs.kt) · [Anchor](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/navigation/AnchorDocs.kt) · [Back Top](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/navigation/BackTopDocs.kt) · [Breadcrumb](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/navigation/BreadcrumbDocs.kt) · [Loading Bar](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/navigation/LoadingBarDocs.kt) · [Menu](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/navigation/MenuDocs.kt) · [Pagination](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/navigation/PaginationDocs.kt) · [Steps](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/navigation/StepsDocs.kt) · [Tabs](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/navigation/TabsDocs.kt) |
| Feedback | [Alert](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/feedback/AlertDocs.kt) · [Badge](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/feedback/BadgeDocs.kt) · [Dialog](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/feedback/DialogDocs.kt) · [Drawer](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/feedback/DrawerDocs.kt) · [Marquee](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/feedback/MarqueeDocs.kt) · [Message](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/feedback/MessageDocs.kt) · [Modal](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/feedback/ModalDocs.kt) · [Notification](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/feedback/NotificationDocs.kt) · [Popconfirm](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/feedback/PopconfirmDocs.kt) · [Popover](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/feedback/PopoverDocs.kt) · [Popselect](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/feedback/PopselectDocs.kt) · [Progress](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/feedback/ProgressDocs.kt) · [Result](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/feedback/ResultDocs.kt) · [Skeleton](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/feedback/SkeletonDocs.kt) · [Spin](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/feedback/SpinDocs.kt) · [Tooltip](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/feedback/TooltipDocs.kt) |
| Layout | [Flex](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/layout/FlexDocs.kt) · [Layout](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/layout/LayoutDocs.kt) · [Grid](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/layout/GridDocs.kt) · [Space](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/layout/SpaceDocs.kt) · [Split](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/layout/SplitDocs.kt) |
| Utility | [Collapse Transition](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/utility/CollapseTransitionDocs.kt) · [Discrete API](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/utility/DiscreteApiDocs.kt) · [Scrollbar](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/utility/ScrollbarDocs.kt) · [Virtual List](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/utility/VirtualListDocs.kt) |
| Config | [Config Provider](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/config/ConfigProviderDocs.kt) · [Element](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/config/ElementDocs.kt) · [Global Style](sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/config/GlobalStyleDocs.kt) |

## Documentation gallery

The sample app presents every component in a searchable vertical menu grouped by category. Each
entry opens a page with interactive demos, and the header can switch between all supported languages
and light/dark themes. The live gallery is deployed to GitHub Pages from the `main` branch.

## Contributing

Read [AGENTS.md](AGENTS.md) for contribution rules, verification commands and demo coverage
requirements.

## License

MIT. Component APIs, visual design, icons and demo content originate from
[naive-ui](https://github.com/tusen-ai/naive-ui), also MIT licensed.
