package com.klochkov.app.imaging;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by georgyklochkov on 16/10/16.
 */
public class Resizer {
    public static BufferedImage process(String filePath, String tmpPath, int w, int h) {


        String data = filePath;
        BufferedImage bsrc, bdest;
        BufferedImage whiteImage;
        ImageIcon theIcon;

//scale the image
        try
        {

            bsrc = ImageIO.read(new File(data));

            bdest = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

            Graphics2D g = bdest.createGraphics();
            g.setPaint(new Color(255,255,255));
            g.fillRect(0,0,w,h);
            double longest = bsrc.getWidth() > bsrc.getHeight() ? bsrc.getWidth() : bsrc.getHeight();
            AffineTransform at = AffineTransform.getScaleInstance((double) w / longest,
                    (double) h / longest);
            g.drawRenderedImage(bsrc, at);
            ImageIO.write(bdest,"JPG",new File(tmpPath));
            return bdest;
        }
        catch (Exception e)
        {
            System.out.println("This image can not be resized. Please check the path and type of file.");
            return null;
        }

    }




}
