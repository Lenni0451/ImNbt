package net.lenni0451.imnbt;

import net.lenni0451.imnbt.ui.types.Popup;
import net.lenni0451.imnbt.ui.types.Window;
import net.lenni0451.imnbt.ui.windows.AboutWindow;
import net.lenni0451.imnbt.ui.windows.DiffWindow;
import net.lenni0451.imnbt.ui.windows.MainWindow;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ImNbtDrawer {

    int getLinesPerPage();

    int getIconsTexture();

    void openPopup(final Popup<?> popup);

    void closePopup();

    MainWindow getMainWindow();

    AboutWindow getAboutWindow();

    DiffWindow getDiffWindow();

    void showWindow(@Nonnull final Window window);

    @Nullable
    String showOpenFileDialog(final String title);

    @Nullable
    String showSaveFileDialog(final String title);

}
