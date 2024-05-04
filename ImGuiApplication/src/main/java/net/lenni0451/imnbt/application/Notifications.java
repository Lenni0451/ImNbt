package net.lenni0451.imnbt.application;

import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import net.lenni0451.imnbt.utils.NotificationLevel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Notifications {

    private static final int FADE_TIME = 250;
    private static final int SHOW_TIME = 5000;
    private static final int PADDING = 5;
    private static final int BAR_HEIGHT = 3;
    private static final List<Notification> notifications = new ArrayList<>();

    public static void add(final NotificationLevel level, final String title, final String message) {
        for (Notification notification : notifications) {
            if (notification.title.equals(title) && notification.message.equals(message)) {
                notification.showTime = System.currentTimeMillis();
                return;
            }
        }
        notifications.add(new Notification(level, title, message));
    }

    public static void draw() {
        final float windowWidth = ImGui.getMainViewport().getSizeX();
        final ImDrawList drawList = ImGui.getForegroundDrawList();

        float drawY = 5;
        Iterator<Notification> it = notifications.iterator();
        while (it.hasNext()) {
            Notification notification = it.next();
            if (System.currentTimeMillis() - notification.showTime > SHOW_TIME) {
                it.remove();
                continue;
            }

            int alpha = notification.getAlpha();
            int foregroundColor = (alpha << 24) | 0xFF_FF_FF;
            int backgroundColor = (alpha << 24) | notification.getBackgroundColor();
            int barColor = (alpha << 24) | 0xFF_FF_FF;

            ImVec2 size = notification.getSize();
            drawList.addRectFilled(windowWidth - size.x - PADDING, drawY, windowWidth - PADDING, drawY + size.y + BAR_HEIGHT, backgroundColor);
            drawList.addRectFilled(windowWidth - size.x - PADDING, drawY + size.y, windowWidth - size.x - PADDING + (size.x * notification.getProgress()), drawY + size.y + BAR_HEIGHT, barColor);

            List<String> lines = new ArrayList<>();
            lines.add(notification.title);
            Collections.addAll(lines, notification.message.split("\n"));

            float textY = drawY;
            for (String line : lines) {
                drawList.addText(windowWidth - size.x, textY + PADDING, foregroundColor, line);
                textY += ImGui.calcTextSize(line).y;
            }
            drawY += size.y + BAR_HEIGHT + PADDING;
        }
    }

    private static final class Notification {
        private final NotificationLevel level;
        private final String title;
        private final String message;
        private long showTime = System.currentTimeMillis();

        private Notification(final NotificationLevel level, final String title, final String message) {
            this.level = level;
            this.title = title;
            this.message = message;
        }

        private int getAlpha() {
            int timeVisible = (int) (System.currentTimeMillis() - this.showTime);
            if (timeVisible < FADE_TIME) {
                return (int) (timeVisible / (float) FADE_TIME * 255);
            } else if (timeVisible > SHOW_TIME - FADE_TIME) {
                return (int) ((SHOW_TIME - timeVisible) / (float) FADE_TIME * 255);
            } else {
                return 255;
            }
        }

        private float getProgress() {
            int timeVisible = (int) (System.currentTimeMillis() - this.showTime);
            return 1F / SHOW_TIME * timeVisible;
        }

        private int getBackgroundColor() {
            //BGR color
            return switch (this.level) {
                case INFO -> 0x00_90_00;
                case WARNING -> 0x00_90_90;
                case ERROR -> 0x00_00_90;
            };
        }

        private ImVec2 getSize() {
            ImVec2 titleSize = ImGui.calcTextSize(this.title);
            String[] lines = this.message.split("\n");
            ImVec2 messageSize = new ImVec2();
            for (String line : lines) {
                ImVec2 lineSize = ImGui.calcTextSize(line);
                messageSize.x = Math.max(messageSize.x, lineSize.x);
                messageSize.y += lineSize.y;
            }
            return new ImVec2(Math.max(titleSize.x, messageSize.x) + PADDING * 2, titleSize.y + messageSize.y + PADDING * 2);
        }
    }

}
