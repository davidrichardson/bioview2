package uk.ac.sanger.cgp.bioview.demo;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import uk.ac.sanger.cgp.bioview.beans.FeatureBean;
import uk.ac.sanger.cgp.bioview.enums.FeatureType;
import uk.ac.sanger.cgp.bioview.exceptions.ImageRenderException;
import uk.ac.sanger.cgp.bioview.legend.LegendRenderer;
import uk.ac.sanger.cgp.bioview.util.ImageUtils;
import uk.ac.sanger.cgp.bioview.util.PropertyUtils;

/*
 * Copyright (c) 2006 Genome Research Ltd.
 * Author: Cancer Genome Project, cgpit@sanger.ac.uk
 *
 * THIS SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the BSD License.
 *
 * Any redistribution or derivation in whole or in part including any
 * substantial portion of this code must include this copyright and permission
 * notice. 
 */

/**
 * @author Original: keiranmraine.code@gmail.com
 * @author  $Author:$
 * @version $Revision:$
 */
public class FeatureLegendDemo {
  
  private static ImageUtils imageUtil = null;
  
  /** Creates a new instance of ChromatogramDemo */
  public FeatureLegendDemo() {
  }
  
  public static void main(String [] args) {
    imageUtil = new ImageUtils();
    
    String featurePropLoc = "resources/featureDemo.properties";
    File output = new File("featureLegend.png");
    try {
      List features = genFeatures(featurePropLoc);
      imageUtil.writeImageToFile(output, createLegend(features));
    }
    catch(Exception e) {
      e.printStackTrace();
      System.err.println(e.toString());
    }
  }
  
  private static List genFeatures(String featurePropLoc) {
    List features = new ArrayList();
    Properties featProps = PropertyUtils.getProperties(featurePropLoc);
    for(Enumeration elements = featProps.propertyNames(); elements.hasMoreElements(); ) {
      String propertyName = (String)elements.nextElement();
      if(propertyName.startsWith("start-")) {
        String featName = propertyName.substring(6);
        int start = Integer.parseInt(featProps.getProperty("start-"+featName));
        int stop = Integer.parseInt(featProps.getProperty("stop-"+featName));
        //Color col = Color.getColor(featProps.getProperty("color-"+featName).toUpperCase());
        Color col = getColor(featProps.getProperty("color-"+featName).toUpperCase());
        System.out.println("name: "+ featName +" start: "+ start +" stop: "+ stop +" color: "+ col);
        FeatureBean feat = null;
        try {
          feat = new FeatureBean(start, stop, FeatureType.DOMAIN, col, featName);
        }
        catch(ImageRenderException e) {
          System.err.println("Domain "+ featName +" failed to construct, should not happen if code used correctly");
        }
        if(feat != null) {
          features.add(feat);
        }
      }
    }
    return features;
  }
  
  private static BufferedImage createLegend(List features) {
    BufferedImage bi = null;
    LegendRenderer legendRend = new LegendRenderer();
    bi = legendRend.generateLegend(features, FeatureType.DOMAIN);
    return bi;
  }
  
  // Returns a Color based on 'colorName' which must be one of the predefined colors in
  // java.awt.Color. Returns null if colorName is not valid.
  private static Color getColor(String colorName) {
    try {
      // Find the field and value of colorName
      Field field = Class.forName("java.awt.Color").getField(colorName);
      return (Color)field.get(null);
    } catch (Exception e) {
      return null;
    }
  }
}
