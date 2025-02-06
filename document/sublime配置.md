# sublime
## 插件

### 1. Pretty JSON
配合命令对js进行格式化
{"keys": ["super+shift+f"], "command": "pretty_json"}

### 2. HTML/CSS/JS Prettify
HTML/CSS/JS 语法高亮显示
注：sublime默认也是支持html/css/js高亮显示的，但前题是打开xx.html/xx.css/xx.js文件，
如果单纯是段文本，是不会语法高亮显示的，但安装了该插件，就可以 syntax highlight

## 常用命令配置
Sublime Text -- Setting-Preferences -- Key Bindings 
```jvascript
[
    {"keys": ["super+d"], "command": "run_macro_file", "args": {"file": "Packages/Default/Delete Line.sublime-macro"} },
    {"keys": ["super+k"], "command": "find_next" },
    {"keys": ["super+shift+k"], "command": "find_prev" },
    {"keys": ["super+f"], "command": "show_panel", "args": {"panel": "replace"} },
    {"keys": ["super+s"], "command": "save_all" },
    {"keys": ["super+l"], "command": "show_overlay", "args": {"overlay": "goto", "text": ":"} },
    {"keys": ["shift+enter"], "command": "run_macro_file", "args": {"file": "Packages/Default/Add Line.sublime-macro"} },
    {"keys": ["alt+up"], "command": "swap_line_up" },
    {"keys": ["alt+down"], "command": "swap_line_down" },
    {"keys": ["super+alt+j"], "command": "join_lines" },
    {"keys": ["super+alt+down"], "command": "duplicate_line" },
    {"keys": ["super+shift+r"], "command": "show_overlay", "args": {"overlay": "goto", "show_files": true} },
    {"keys": ["super+shift+y"], "command": "lower_case" },  
    {"keys": ["super+shift+x"], "command": "upper_case" },
    {"keys": ["super+shift+f"], "command": "pretty_json"}
]
```

## 其他配置
Sublime Text -- Setting-Preferences -- Settings
```javascript
{
    "ignored_packages":
    [
        "Vintage",
    ],
    "open_files_in_new_window": false,
    "find_selected_text": true
}
```