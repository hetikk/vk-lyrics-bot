package hetikk.bot.utils;

import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.*;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Resources {

    public static Font loadFont(Class nearClass, String src) {
        try {
            InputStream is = nearClass.getResourceAsStream(src);
            return Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (Exception e) {
            e.printStackTrace();
            return new JLabel().getFont();
        }
    }

    public static Font loadFont(Class nearClass, String src, int fontStyle) {
        Font font = loadFont(nearClass, src);
        return font.deriveFont(fontStyle);
    }

    public static Font loadFont(Class nearClass, String src, float fontSize) {
        Font font = loadFont(nearClass, src);
        font = font.deriveFont(fontSize); // Font.PLAIN, Font.BOLD, Font.ITALIC
        return font;
    }

    public static Font loadFont(Class nearClass, String src, int fontStyle, float fontSize) {
        Font font = loadFont(nearClass, src);
        font = font.deriveFont(fontStyle); // Font.PLAIN, Font.BOLD, Font.ITALIC
        font = font.deriveFont(fontSize);
        return font;
    }

    public static ImageIcon loadImageIcon(Class nearClass, String src) {
        try {
            return new ImageIcon(nearClass.getResource(src));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Image loadImage(Class nearClass, String src) {
        try {
            return new ImageIcon(nearClass.getResource(src)).getImage();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BufferedImage loadBufferedImage(Class nearClass, String src) {
        try {
            InputStream bg = nearClass.getResourceAsStream(src);
            return ImageIO.read(bg);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Properties loadProperties(Class nearClass, String src) {
        try {
            InputStream stream = nearClass.getResourceAsStream(src);
            Properties props = new Properties();
            props.load(stream);
            return props;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}

/* Хетаг */