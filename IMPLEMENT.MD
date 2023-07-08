# Implementing ImNbt

- [Implementing ImNbt](#implementing-imnbt)
  - [Gradle](#gradle)
  - [Creating an `ImNbtDrawer`](#creating-an-imnbtdrawer)
  - [Drawing the entire application](#drawing-the-entire-application)
    - [Main Window](#main-window)
  - [Drawing only the tag tree view](#drawing-only-the-tag-tree-view)

## Gradle
It is recommended to use gradle to download and manage the dependencies.\
You can find the latest version on my [maven server](https://maven.lenni0451.net/#/releases/net/lenni0451/ImNbt).

```groovy
repositories {
    maven {
        name = "Lenni0451 Maven Server"
        url = "https://maven.lenni0451.net/releases"
    }
}

dependencies {
    implementation "net.lenni0451:ImNbt:x.x.x"
}
```

## Creating an `ImNbtDrawer`
You need to implement your own `ImNbtDrawer` drawer to draw the application.\
You can find an example implementation in the [ImGuiImpl](ImGuiApplication/src/main/java/net/lenni0451/imnbt/application/ImGuiImpl.java) class.

Methods to implements:
| Method                            | Description                                                   |
| --------------------------------- | ------------------------------------------------------------- |
| `int getLinesPerPage()`           | Get the amount of lines that fit on one page                  |
| `int getIconsTexture()`           | Get the texture id for the icons                              |
| `void openPopup(Popup)`           | Open a popup. You have to store it in a variable and draw it  |
| `void closePopup()`               | Close the currently open popup                                |
| `MainWindow getMainWindow()`      | Get the main window instance                                  |
| `AboutWindow getAboutWindow()`    | Get the about window instance                                 |
| `DiffWindow getDiffWindow()`      | Get the diff window instance                                  |
| `void showWindow(Window)`         | Show a window. You have to store it in a variable and draw it |
| `void showOpenFileDialog(String)` | Show a file dialog to open a nbt tag                          |
| `void showSaveFileDialog(String)` | Show a file dialog to save a nbt tag                          |
| `boolean hasClipboard()`          | Check if the clipboard contains a nbt tag                     |
| `void setClipboard(NamedTag)`     | Set the clipboard to a nbt tag                                |
| `NamedTag getClipboard()`         | Get the nbt tag from the clipboard or null if an error occurs |

## Drawing the entire application
To draw the entire application you can just start drawing the currently open window.

This snippet is taken from the [ImGuiImpl](ImGuiApplication/src/main/java/net/lenni0451/imnbt/application/ImGuiImpl.java) class:
```java
ImGui.setNextWindowPos(0, 0);
ImGui.setNextWindowSize(ImGui.getIO().getDisplaySize().x, ImGui.getIO().getDisplaySize().y);
ImGui.begin("MainWindow", ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.MenuBar);
this.window.render();
ImGui.end();
```
You need to push the font you want to use beforehand.

To draw the currently open popup you can use this snippet:
```java
if (this.popup != null) {
    this.popup.open();
    this.popup.render(this);
}
```

### Main Window
The main window requires two arguments when creating it:
 - The `ImNbtDrawer` instance
 - An optional `FontHandler`

The `FontHandler` is used to display the font selection menu.\
If `null` is passed the menu will not be displayed.

## Drawing only the tag tree view
If you only need the tag tree view you can call the `NbtTreeRenderer#render` directly.

The following arguments are required:
| Argument           | Type                      | Description                                                                        |
| ------------------ | ------------------------- | ---------------------------------------------------------------------------------- |
| `drawer`           | `ImNbtDrawer`             | The drawer instance                                                                |
| `nameEditConsumer` | `Consumer<String>`        | A handler for root tag name changes                                                |
| `deleteListener`   | `Runnable`                | A handler for root tag deletion                                                    |
| `colorProvider`    | `Function<String, Color>` | A supplier for the text color of a tag. Can return `null` to use the default color |
| `searchProvider`   | `SearchProvider`          | A provider for the search functionality                                            |
| `openContextMenu`  | `boolean`                 | If the context menu should be available                                            |
| `path`             | `String`                  | The current path (Should be empty for the root tag)                                |
| `name`             | `String`                  | The name of the root tag                                                           |
| `tag`              | `INbtTag`                 | The root tag                                                                       |
