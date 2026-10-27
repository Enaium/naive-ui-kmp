# Naive UI for Kotlin Multiplatform

![](https://img.cdn1.vip/i/6ae02b2731388_1793076007.webp)

A Compose Multiplatform port of [naive-ui](https://github.com/tusen-ai/naive-ui) (Vue 3) for
Android, iOS, Desktop and Web.

The library ships all 94 components in scope, the naive-ui light and dark themes, theme overrides,
79 locales and right-to-left layouts. An interactive gallery mirrors the upstream documentation and
provides 500+ runnable demos.

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

- **General (15):** Avatar, Button, Card, Carousel, Collapse, Divider, Dropdown, Ellipsis,
  Gradient Text, Icon, Page Header, Tag, Typography, Watermark, Float Button.
- **Data entry (21):** Auto Complete, Cascader, Color Picker, Checkbox, Date Picker, Dynamic Input,
  Dynamic Tags, Form, Input, Input Number, Input OTP, Mention, Radio, Rate, Select, Slider, Switch,
  Time Picker, Transfer, Tree Select, Upload.
- **Data display (21):** Calendar, Countdown, Code, Data Table, Descriptions, Empty, Equation, Image,
  List, Log, Number Animation, QR Code, Statistic, Table, Thing, Time, Timeline, Tree, Infinite Scroll,
  Highlight, Heatmap.
- **Navigation (9):** Affix, Anchor, Back Top, Breadcrumb, Loading Bar, Menu, Pagination, Steps, Tabs.
- **Feedback (16):** Alert, Badge, Dialog, Drawer, Marquee, Message, Modal, Notification, Popconfirm,
  Popover, Popselect, Progress, Result, Skeleton, Spin, Tooltip.
- **Layout (5):** Flex, Layout, Grid, Space, Split.
- **Utility (4):** Collapse Transition, Discrete API, Scrollbar, Virtual List.
- **Config (3):** Config Provider, Element, Global Style.

## Documentation gallery

The sample app presents every component in a searchable vertical menu grouped by category. Each
entry opens a page with interactive demos, and the header can switch between all supported languages
and light/dark themes.

## License

MIT. Component APIs, visual design, icons and demo content originate from
[naive-ui](https://github.com/tusen-ai/naive-ui), also MIT licensed.
