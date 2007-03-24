package uk.ac.sanger.cgp.bioview.legend;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import uk.ac.sanger.cgp.bioview.beans.FeatureBean;
import uk.ac.sanger.cgp.bioview.enums.FeatureType;
import uk.ac.sanger.cgp.bioview.util.ImageUtils;

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
public class LegendRenderer {
  
  private static final int IND_LEGEND_HEIGHT = 14;
  private static final int LEGEND_BAR_WIDTH = 25;
  private static final int CHAR_WIDTH = 7;
  
  private static ImageUtils imageUtil = new ImageUtils();
  
  /** Creates a new instance of LegendRenderer */
  public LegendRenderer() {
  }
  
  public BufferedImage generateLegend(List features, FeatureType featureTypeToRend) {
    List featureTypes = new ArrayList(1);
    featureTypes.add(featureTypeToRend);
    return generateLegend(features, featureTypes);
  }
  
  public BufferedImage generateLegend(List allFeatures, List featureTypesToRend) {
    List features = getFeatures(allFeatures, featureTypesToRend);
    
    BufferedImage bi = createBuffer(features);
    Graphics2D g2 = bi.createGraphics();
    imageUtil.basicDrawArea(g2, bi);
    for(int i=0; i<features.size(); i++) {
      FeatureBean feat = (FeatureBean)features.get(i);
      /*g2.setColor(Color.BLACK);
      g2.drawString(feat.getFeatureName(), 5, (12 + (IND_LEGEND_HEIGHT*i)));
      g2.setColor(feat.getFeatureColor());
      int xStart = (bi.getWidth() - LEGEND_BAR_WIDTH) - 5;
      int xStop = bi.getWidth() - 5;
      g2.drawRect(xStart, 2 + (IND_LEGEND_HEIGHT*i), LEGEND_BAR_WIDTH, 10);
      g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ImageUtils.ALPHA_BLEND));
      g2.fillRect(xStart, 2 + (IND_LEGEND_HEIGHT*i), LEGEND_BAR_WIDTH, 10);*/
      
      g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0F));
      g2.setColor(feat.getFeatureColor());
      g2.drawRect(10, 2 + (IND_LEGEND_HEIGHT*i), LEGEND_BAR_WIDTH, 10);
      g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ImageUtils.ALPHA_BLEND));
      g2.fillRect(10, 2 + (IND_LEGEND_HEIGHT*i), LEGEND_BAR_WIDTH, 10);
      g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0F));
      g2.setColor(Color.BLACK);
      int yPos = 12 + (IND_LEGEND_HEIGHT*i);
      g2.drawString(feat.getFeatureName(), LEGEND_BAR_WIDTH + 15, yPos);
    }
    
    return bi;
  }

  private BufferedImage createBuffer(final List features) {
    BufferedImage bi = null;
    if(features.size() > 0) {
      int width = (CHAR_WIDTH * longestName(features)) + LEGEND_BAR_WIDTH + 4;
      int height = (IND_LEGEND_HEIGHT * features.size()) + 4;
      bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }
    return bi;
  }
  
  private List getFeatures(List features, List featureTypesToRend) {
    List selectedFeatures = new ArrayList();
    for(int i=0; i<features.size(); i++) {
      FeatureBean feat = (FeatureBean)features.get(i);
      if(featureTypesToRend.contains(feat.getFeatureType())) {
        selectedFeatures.add(feat);
      }
    }
    return selectedFeatures;
  }
  
  private int longestName(List features) {
    int nameLength = 0;
    for(int i=0; i<features.size(); i++) {
      FeatureBean feat = (FeatureBean)features.get(i);
      if(feat.getFeatureName().length() > nameLength) {
        nameLength = feat.getFeatureName().length();
      }
    }
    return nameLength;
  }
  
}
