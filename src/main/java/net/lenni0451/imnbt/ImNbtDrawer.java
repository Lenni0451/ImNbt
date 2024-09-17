package net.lenni0451.imnbt;

import net.lenni0451.imnbt.config.ImNbtConfig;
import net.lenni0451.imnbt.config.SessionConfig;
import net.lenni0451.imnbt.ui.types.Popup;
import net.lenni0451.imnbt.ui.types.Window;
import net.lenni0451.imnbt.ui.windows.AboutWindow;
import net.lenni0451.imnbt.ui.windows.DiffWindow;
import net.lenni0451.imnbt.ui.windows.MainWindow;
import net.lenni0451.imnbt.utils.NotificationLevel;
import net.lenni0451.mcstructs.nbt.io.NamedTag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ImNbtDrawer {

    /**
     * @return The configuration for ImNbt
     */
    default ImNbtConfig getConfig() {
        return new SessionConfig();
    }

    /**
     * Get the amount of tags to display per page.<br>
     * This is required for large array/list/compound tags to prevent the UI from freezing.<br>
     * The default value is <b>500</b>
     *
     * @return The lines per page
     */
    int getLinesPerPage();

    /**
     * Get the texture id for the tag icons.
     *
     * @return The texture id
     */
    int getIconsTexture();

    /**
     * Open a popup window on the screen.<br>
     * This will close the current popup if there is one.
     *
     * @param popup The popup to open
     */
    void openPopup(@Nonnull final Popup<?> popup);

    /**
     * Show a notification to the user.<br>
     * It is to be expected that the current popup will stay open when this method is called.
     *
     * @param level   The level of the notification
     * @param title   The title of the notification
     * @param message The message of the notification
     */
    default void showNotification(final NotificationLevel level, final String title, final String message) {
        this.showNotification(level, title, message, () -> {});
    }

    /**
     * Show a notification to the user.<br>
     * The callback should be called when the user has received (and optionally closed) the notification.<br>
     * It is to be expected that the current popup will stay open when this method is called.
     *
     * @param level    The level of the notification
     * @param title    The title of the notification
     * @param message  The message of the notification
     * @param callback The callback to call when the notification is received
     */
    void showNotification(final NotificationLevel level, final String title, final String message, final Runnable callback);

    /**
     * Close the current popup window.
     */
    void closePopup();

    /**
     * Get the instance of the main window.
     *
     * @return The main window
     */
    MainWindow getMainWindow();

    /**
     * Get the instance of the about window.
     *
     * @return The about window
     */
    AboutWindow getAboutWindow();

    /**
     * Get the instance of the diff window.
     *
     * @return The diff window
     */
    DiffWindow getDiffWindow();

    /**
     * Open a window to draw it on the screen.<br>
     * This will close the current window if there is one.
     *
     * @param window The window to open
     */
    void showWindow(@Nonnull final Window window);

    /**
     * Show an open file dialog.<br>
     * Only one file should be selectable.<br>
     * Return {@code null} if the dialog was canceled.
     *
     * @param title The title of the dialog
     * @return The path to the selected file
     */
    @Nullable
    String showOpenFileDialog(final String title);

    /**
     * Show a save file dialog.<br>
     * Return {@code null} if the dialog was canceled.
     *
     * @param title The title of the dialog
     * @return The path to the selected file
     */
    @Nullable
    String showSaveFileDialog(final String title);

    /**
     * @return If the clipboard contains a tag
     */
    boolean hasClipboard();

    /**
     * Set the clipboard to the given tag.
     *
     * @param tag The tag to copy
     */
    void setClipboard(@Nonnull final NamedTag tag);

    /**
     * @return The clipboard tag
     */
    @Nullable
    NamedTag getClipboard();

    /**
     * Gets called when the File -> Exit menu item is clicked.
     */
    void exit();

}
