/*
 * ImageUtils.java
 *
 * Created on 17 March 2007, 09:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package uk.ac.sanger.cgp.bioview.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.sanger.cgp.bioview.exceptions.ImageRenderException;

/**
 *
 * @author Home
 */
public class ImageUtils {
  
  protected Log log = LogFactory.getLog(this.getClass());
  private static ImageWriter iw = null;
  
  public static final float ALPHA_BLEND = 0.2f;
  
  /** Creates a new instance of ImageUtils */
  public ImageUtils() {
  }
  
  public void basicDrawArea(Graphics2D g2, BufferedImage bi) {
		g2.setBackground(Color.white);
    g2.clearRect(0, 0, bi.getWidth(), bi.getHeight());
    if (g2.getClip() == null) {
        g2.setClip(new Rectangle(0, 0, bi.getWidth(), bi.getHeight()));
    }
	}
  
  public void writeImageToFile(File image, BufferedImage bi) throws ImageRenderException {
    if(iw == null) {
      Iterator writers = ImageIO.getImageWritersBySuffix("png");
      iw = (ImageWriter) writers.next();
      if(log.isDebugEnabled()) log.debug(iw.getClass().getName());
    }
    FileImageOutputStream out = null;
		
		try {
      out = new FileImageOutputStream(image);
      iw.setOutput(out);
      iw.write(bi);
    } 
    catch (FileNotFoundException e) {
			throw new ImageRenderException("Can't write to " + image, e);
    }
    catch(IOException e) {
      throw new ImageRenderException("Can't write to " + image, e);
    }
    finally {
      iw.reset();
      if(out != null) {
        try {
          out.flush();
          out.close();
        }
        catch(IOException e) {
          if(log.isErrorEnabled()) log.error("fudge, still could not close in finally");
        }
      }
    }
  }
  
}
