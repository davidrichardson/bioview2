package uk.ac.sanger.cgp.bioview.chromatogram;

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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.biojava.bio.chromatogram.Chromatogram;
import uk.ac.sanger.cgp.bioview.beans.ChromError;
import uk.ac.sanger.cgp.bioview.beans.ChromRenderBean;
import uk.ac.sanger.cgp.bioview.beans.FeatureBean;
import uk.ac.sanger.cgp.bioview.enums.FeatureType;
import uk.ac.sanger.cgp.bioview.exceptions.ChromatogramRenderException;
import uk.ac.sanger.cgp.bioview.util.ImageUtils;


/**
 * This class handles rendering of chromatogram graphics.
 * @author Original: kr2
 * @author  $Author: kr2 $
 * @version $Revision: 1.29 $
 */
public class ChromatogramRenderer {
	private static final IntensityChooser DEFAULT_INTENSITY_CHOOSER = new MaxIntensityChooser();  
  protected Log log = LogFactory.getLog(this.getClass());
  private static String dirSep = System.getProperty("file.separator");
  
  private static final int CHROMAT_HEIGHT = 100;
  private static final int HEIGHT_BUFFER = 10;
  private static final int TEXT_BUFFER = 10;
  private static final int DEFAULT_VAR_VIEW_BASES = 41; // must be odd number @todo add a check
  private static final float HORIZONTAL_SCALE = 2.0F;
  private static final int APPROX_PEAK_SPACE = 10;
  private static final boolean RENDER_SCALE = true;
  
  private static ImageUtils imageUtil = new ImageUtils();
  
  private IntensityChooser intensityChooser = null;
  
  public int[] getVariantViewStartAndStop(FeatureBean feat) {
    int varLength = (feat.getFeatureStop() - feat.getFeatureStart()) + 1;
    int basesRemaning = (DEFAULT_VAR_VIEW_BASES - varLength);
    int endBases = (int)(basesRemaning/2);
    int startAtBase = feat.getFeatureStart() - (basesRemaning - endBases);
    int stopAtBase = startAtBase + (DEFAULT_VAR_VIEW_BASES-1);
    return new int[] {startAtBase, stopAtBase};
  }
  
  public BufferedImage generateComplexVariantView(ChromRenderBean varCrb, ChromRenderBean wtCrb, boolean reverseChromObject) {
    BufferedImage buffImg = null;
    
    try {
      BufferedImage biVar = renderChromatogram(varCrb, reverseChromObject);
      if(biVar != null) {
        BufferedImage biWt = renderChromatogram(wtCrb, reverseChromObject);
        if(biWt != null) {

          FeatureBean varFeat = getVarFeat(varCrb);

          int largestPos;
          int smallestPos;
          int [] scanBoundryVar;
          int [] scanBoundryWt;

          boolean shiftWt = false;
          if(reverseChromObject) {
            scanBoundryVar = getScanBoundries(varCrb.getPerBaseScans(), varFeat.getFeatureStop() - 1);
            scanBoundryWt = getScanBoundries(wtCrb.getPerBaseScans(), varFeat.getFeatureStop() - 1);
            largestPos = scanBoundryVar[1];
            smallestPos = scanBoundryWt[1];
            if(scanBoundryWt[1] > largestPos) {
              largestPos = scanBoundryWt[1];
              smallestPos = scanBoundryVar[1];
              shiftWt = true;
            }
          }
          else {
            scanBoundryVar = getScanBoundries(varCrb.getPerBaseScans(), varFeat.getFeatureStart() - 1);
            scanBoundryWt = getScanBoundries(wtCrb.getPerBaseScans(), varFeat.getFeatureStart() - 1);
            largestPos = scanBoundryVar[0];
            smallestPos = scanBoundryWt[0];
            if(scanBoundryWt[0] > largestPos) {
              largestPos = scanBoundryWt[0];
              smallestPos = scanBoundryVar[0];
              shiftWt = true;
            }
          }
          int xOffset = (int) ((largestPos - smallestPos) * HORIZONTAL_SCALE);
      
          int widest = biVar.getWidth();
          if(biWt.getWidth() > widest) {
            widest = biWt.getWidth();
          }
          buffImg = buildComplexImage(widest, xOffset, shiftWt, biWt, biVar);
        }
      }
    }
    catch(ChromatogramRenderException e) {
      log.error("An error occured when rendering a chromatogram", e);
    }
    
    return buffImg;
  }

  private BufferedImage buildComplexImage(final int widest, final int xOffset, final boolean shiftWt, final BufferedImage biWt, final BufferedImage biVar) {
    BufferedImage buffImg;

    buffImg = new BufferedImage(widest + xOffset, biVar.getHeight() + biWt.getHeight() + 5, BufferedImage.TYPE_INT_RGB);
    Graphics2D g2 = buffImg.createGraphics();
    imageUtil.basicDrawArea(g2, buffImg);
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    AffineTransform af = new AffineTransform();

    BufferedImageOp bufOps = new AffineTransformOp(af, g2.getRenderingHints());

    int xOfVar = 0;
    int xOfWt = 0;
    if(shiftWt) {
      xOfWt = xOffset;
    }
    else {
      xOfVar = xOffset;
    }
    int yOfVar = 0; // wildtype trace on top
    int yOfWt = biWt.getHeight() + 4; // variant trace on bottom

    g2.drawImage(biWt, bufOps, xOfVar, yOfVar); // wildtype trace on top
    g2.drawImage(biVar, bufOps, xOfWt, yOfWt); // variant trace on bottom

    g2.dispose();
    buffImg.flush();
    return buffImg;
  }
	
  public ChromError processChromRenderException(final ChromatogramRenderException e) {
    ChromError error;
    if(e.getMessage().equals(ChromError.UNKNOWN_SCAN_START.toString())) {
      error = ChromError.UNKNOWN_SCAN_START;
    }
    else if(e.getMessage().equals(ChromError.UNKNOWN_SCAN_STOP.toString())) {
      error = ChromError.UNKNOWN_SCAN_STOP;
    }
    else if(e.getMessage().equals(ChromError.OUTSIDE_OF_COVERAGE.toString())) {
      error = ChromError.OUTSIDE_OF_COVERAGE;
    }
    else {
      error = ChromError.UNKNOWN_REASON;
    }
    return error;
  }
  
  public BufferedImage renderChromatogram(ChromRenderBean crb, boolean revCompChromatogram) throws ChromatogramRenderException{
    BufferedImage bi = null;
    
    int [] scanRange = convertBaseToScan(crb.getChromatogram(), crb.getPerBaseScans(), crb.getStartRenderAtBase(), crb.getStopRenderAtBase());
    int startAtScan = scanRange[0];
    int stopAtScan = scanRange[1];

    if(startAtScan == 0 && crb.getStartRenderAtBase() != 0) {
      if(log.isWarnEnabled()) log.warn(ChromError.UNKNOWN_SCAN_START.toString());
      throw new ChromatogramRenderException(ChromError.UNKNOWN_SCAN_START.toString());
    }
    else if(stopAtScan == 0) {
      if(log.isWarnEnabled()) log.warn(ChromError.UNKNOWN_SCAN_STOP.toString());
      throw new ChromatogramRenderException(ChromError.UNKNOWN_SCAN_STOP.toString());
    }
    
    List features = crb.getFeatures();
    
    int localTopHeightBuffer = HEIGHT_BUFFER * 2;
    
    SimpleChromGraphic chromGraph = null;
    try {
      Chromatogram chrom = crb.getChromatogram();
      if(revCompChromatogram) {
        chrom = chrom.reverseComplement();
      }
      
      chromGraph = new SimpleChromGraphic(chrom, startAtScan, stopAtScan, CHROMAT_HEIGHT, HORIZONTAL_SCALE, localTopHeightBuffer, RENDER_SCALE, intensityChooser);
    }
    catch(ChromatogramRenderException e) {
      throw new ChromatogramRenderException(ChromError.OUTSIDE_OF_COVERAGE.toString(), e);
    }
    
    bi = createBuffer(chromGraph);
    Graphics2D g2 = bi.createGraphics();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    imageUtil.basicDrawArea(g2, bi);
    renderNonDomainFeatures(features, g2, chromGraph, crb);
    
    try {
      drawChromatogram(g2, chromGraph);
    }
    catch(Exception e) {
      throw new ChromatogramRenderException(e.getMessage(), e);
    }

    g2.dispose();
    bi.flush();
    
    // list of lists to create non-overlapping domain tracks
    List domainList = buildDomainList(crb);
    bi = renderDomains(domainList, bi, chromGraph, crb);
    
    return bi;
  }

  private void renderNonDomainFeatures(final List features, final Graphics2D g2, final SimpleChromGraphic chromGraph, final ChromRenderBean crb) {
    
    for(Iterator iter = features.iterator(); iter.hasNext(); ) {
      FeatureBean feat = (FeatureBean)iter.next();
      if(feat.getFeatureType() == FeatureType.RENDER_AMPLIMER) {
        renderPeakRange(g2, chromGraph, crb, feat, true, false);
      }
      else if(feat.getFeatureType() == FeatureType.VARIANT_CALL) {
        renderPeakRange(g2, chromGraph, crb, feat, true, true);
      }
      else if(feat.getFeatureType() == FeatureType.EXCLUSIVE_BOUNDRY) {
        // these may need to be drawn after the chromatogram as foreground elements
        drawBoundry(g2, chromGraph, crb, feat, true);
        
      }
      else if(feat.getFeatureType() == FeatureType.INCLUSIVE_BOUNDRY) {
        // these may need to be drawn after the chromatogram as foreground elements
        drawBoundry(g2, chromGraph, crb, feat, false);
      }
    }
  }

  private BufferedImage renderDomains(final List domainList, BufferedImage bi, final SimpleChromGraphic chromGraph, final ChromRenderBean crb) {
    
    if(domainList != null) {
      int localBottomHeightBuffer = (domainList.size() * 6) + 5;
      BufferedImage biDom = new BufferedImage(bi.getWidth(), bi.getHeight() + localBottomHeightBuffer, BufferedImage.TYPE_INT_RGB);
      Graphics2D g2Dom = biDom.createGraphics();
      g2Dom.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      imageUtil.basicDrawArea(g2Dom, biDom);
      
      AffineTransform af = new AffineTransform();
      BufferedImageOp bufOps = new AffineTransformOp(af, g2Dom.getRenderingHints());
      g2Dom.drawImage(bi, bufOps, 0, 0);
      
      for(int listNo = 0; listNo < domainList.size(); listNo++) {
        List subList = (List)domainList.get(listNo);
        for(int featNo = 0; featNo < subList.size(); featNo++) {
          FeatureBean feat = (FeatureBean)subList.get(featNo);
          drawDomain(g2Dom, chromGraph, crb, feat, listNo, biDom.getHeight());
        }
      }
      g2Dom.dispose();
      biDom.flush();
      bi = biDom;
    }
    return bi;
  }
  
  private FeatureBean getVarFeat(ChromRenderBean crb) {
    List features = crb.getFeatures();
    FeatureBean feat = null;
    for(Iterator iter = features.iterator(); iter.hasNext(); ) {
      feat = (FeatureBean)iter.next();
      if(feat.getFeatureType() == FeatureType.VARIANT_CALL) {
        break;
      }
      else {
        feat = null;
      }
    }
    return feat;
  }

  private List buildDomainList(ChromRenderBean crb) {
    List domainList = null;
    List features = crb.getFeatures();
    if(features != null) {
      for(Iterator iter = features.iterator(); iter.hasNext(); ) {
        FeatureBean feat = (FeatureBean)iter.next();
        if(feat.getFeatureType() == FeatureType.DOMAIN) {
          boolean addedFeat = false;
          if(domainList == null) {
            domainList = new ArrayList();
            List subList = new ArrayList();
            subList.add(feat);
            domainList.add(subList);
            continue;
          }
          for(int listNo = 0; listNo < domainList.size(); listNo++) {
            List subList = (List)domainList.get(listNo);
            boolean overlap = false;
            for(int featNo = 0; featNo < subList.size(); featNo++) {
              FeatureBean listFeat = (FeatureBean)subList.get(featNo);
              if(feat.getFeatureStart() > listFeat.getFeatureStart()-5 && feat.getFeatureStart() < listFeat.getFeatureStop()+5) {
                overlap = true;
              }
              else if(feat.getFeatureStop() > listFeat.getFeatureStart()-5 && feat.getFeatureStop() < listFeat.getFeatureStop()+5) {
                overlap = true;
              }
            }
            if(!overlap) {
              subList.add(feat);
              addedFeat = true;
              break;
            }
          }
          if(!addedFeat) {
            List subList = new ArrayList();
            subList.add(feat);
            domainList.add(subList);
          }
        }
      }
      if(domainList != null) {
        Collections.reverse(domainList);
      }
    }
    
    return domainList;
  }
	
  public BufferedImage badImage(String errorMessage) {
    BufferedImage bi = new BufferedImage(600, 100, BufferedImage.TYPE_INT_RGB);
    Graphics2D g2 = bi.createGraphics();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    imageUtil.basicDrawArea(g2, bi);
    g2.setColor(Color.BLACK);
    g2.drawString("Cannot render image: " + errorMessage, 5, 55);
    
    g2.dispose();
    bi.flush();
    
		return bi;
	}
  
  public BufferedImage createBuffer(SimpleChromGraphic chromGraph) {
    return new BufferedImage(chromGraph.getRenderedWidth(), chromGraph.getRenderedHeight(), BufferedImage.TYPE_INT_RGB);
  }
  
  private void renderPeakRange(Graphics2D g2, SimpleChromGraphic chromGraph, ChromRenderBean crb, FeatureBean feat, boolean drawBaseChar, boolean highlightBase) {
    
    if(crb.getTraceCoverageStop() != 0) {
      AffineTransform initTrans = g2.getTransform();
      chromGraph.setupXoffsetTransform(g2);
      
      FeatureBean variantFeat = null;
      
      if(feat.getFeatureType() != FeatureType.VARIANT_CALL) {
        List features = crb.getFeatures();
        for(Iterator iter = features.iterator(); iter.hasNext(); ) {
          FeatureBean chkFeat = (FeatureBean)iter.next();
          if(chkFeat.getFeatureType() == FeatureType.VARIANT_CALL) {
            variantFeat = chkFeat;
          }
        }
      }
      
      for(int i=feat.getFeatureStart(); i<=feat.getFeatureStop(); i++) {
        if(variantFeat == null || (i < variantFeat.getFeatureStart() || i > variantFeat.getFeatureStop())) {
          drawBaseChangeSimp(g2, chromGraph, crb, i, drawBaseChar, highlightBase);
        }
      }
      
      g2.setTransform(initTrans);
    }
    else {
      if(log.isWarnEnabled()) log.warn("Cannot render variant highlighting when trace fails to analyse");
    }
  }
  
  private void drawDomain(Graphics2D g2, SimpleChromGraphic chromGraph, ChromRenderBean crb, FeatureBean feature, int domPosition, int height) {
    if(crb.getTraceCoverageStop() != 0) {
      float scaleFac = chromGraph.getHorizScale();
      int startScanOffset = chromGraph.getStartScan();
      int width = chromGraph.getRenderedWidth();
      
      int domVertPos = height - ((domPosition * 6) + 5);
      
      List scanList = crb.getPerBaseScans();

      int scanOffsetFac = (int) (startScanOffset * scaleFac);
      
      int startPos = feature.getFeatureStart();
      int stopPos = feature.getFeatureStop();
      
      boolean drawStartLine = true;
      boolean drawStopLine = true;
      Integer startScanObj = (Integer)scanList.get(startPos-1);
      Integer stopScanObj = (Integer)scanList.get(stopPos-1);
      
      if(startScanObj == null) {
        for(int i = 0; i<stopPos; i++) {
          startScanObj = (Integer)scanList.get(startPos+i);
          if(startScanObj != null) {
            break;
          }
        }
        drawStartLine = false;
      }

      if(stopScanObj == null) {
        for(int i = 2; (stopPos - i) > startPos; i++) {
          stopScanObj = (Integer)scanList.get(stopPos-i);
          if(stopScanObj != null) {
            break;
          }
        }
        drawStopLine = false;
      }
      
      if(startScanObj != null && stopScanObj != null) {
      
        int startScan = startScanObj.intValue() - 5;
        startScan = ((int) (startScan * scaleFac)) - scanOffsetFac;
        int stopScan = stopScanObj.intValue() + 5;
        stopScan = ((int) (stopScan * scaleFac)) - scanOffsetFac;

        if(startScan > 0 || stopScan < width) {

          AffineTransform initTrans = g2.getTransform();
          chromGraph.setupXoffsetTransform(g2);

          // draw the lines
          g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0F));
          
          g2.setColor(feature.getFeatureColor());

          if(startScan > 0 && stopScan < width) {
            g2.drawRect(startScan, domVertPos, ((stopScan - startScan) + 1), 4);
            //g2.fillRect(startScan, domVertPos, ((stopScan - startScan) + 1), 4);
          }
          else if(startScan > 0) {
            g2.drawRect(startScan, domVertPos, ((width - startScan) + 1), 4);
            //g2.fillRect(startScan, domVertPos, ((width - startScan) + 1), 4);
          }
          else {
            g2.drawRect(0, domVertPos, ((stopScan - 0) + 1), 4);
            //g2.fillRect(0, domVertPos, ((stopScan - 0) + 1), 4);
          }
          
          g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ImageUtils.ALPHA_BLEND));
          if(startScan > 0 && stopScan < width) {
            //g2.drawRect(startScan, domVertPos, ((stopScan - startScan) + 1), 4);
            g2.fillRect(startScan, domVertPos, ((stopScan - startScan) + 1), 4);
          }
          else if(startScan > 0) {
            //g2.drawRect(startScan, domVertPos, ((width - startScan) + 1), 4);
            g2.fillRect(startScan, domVertPos, ((width - startScan) + 1), 4);
          }
          else {
            //g2.drawRect(0, domVertPos, ((stopScan - 0) + 1), 4);
            g2.fillRect(0, domVertPos, ((stopScan - 0) + 1), 4);
          }

          g2.setTransform(initTrans);
        }
      }
    }
	}
  
  private void drawBoundry(Graphics2D g2, SimpleChromGraphic chromGraph, ChromRenderBean crb, FeatureBean feature, boolean exclusive) {
    if(crb.getTraceCoverageStop() != 0) {
      float scaleFac = chromGraph.getHorizScale();
      int startScanOffset = chromGraph.getStartScan();
      int width = chromGraph.getRenderedWidth();
      int height = chromGraph.getRenderedHeight();
      
      List scanList = crb.getPerBaseScans();

      int scanOffsetFac = (int) (startScanOffset * scaleFac);
      
      int startPos = feature.getFeatureStart();
      int stopPos = feature.getFeatureStop();
      
      boolean drawStartLine = true;
      boolean drawStopLine = true;
      Integer startScanObj = (Integer)scanList.get(startPos-1);
      Integer stopScanObj = (Integer)scanList.get(stopPos-1);
      
      if(!exclusive) {
        if(startScanObj == null) {
          for(int i = 0; i<stopPos; i++) {
            startScanObj = (Integer)scanList.get(startPos+i);
            if(startScanObj != null) {
              break;
            }
          }
          drawStartLine = false;
        }
        
        if(stopScanObj == null) {
          for(int i = 2; (stopPos - i) > startPos; i++) {
            stopScanObj = (Integer)scanList.get(stopPos-i);
            if(stopScanObj != null) {
              break;
            }
          }
          drawStopLine = false;
        }
      }
      
      if(startScanObj != null && stopScanObj != null) {
      
        int startScan = startScanObj.intValue() - 5;
        startScan = ((int) (startScan * scaleFac)) - scanOffsetFac;
        int stopScan = stopScanObj.intValue() + 5;
        stopScan = ((int) (stopScan * scaleFac)) - scanOffsetFac;

        if(startScan > 0 || stopScan < width) {

          AffineTransform initTrans = g2.getTransform();
          chromGraph.setupXoffsetTransform(g2);

          // set up for grey box
          g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ImageUtils.ALPHA_BLEND));
          g2.setColor(feature.getFeatureColor());

          if(exclusive) {
            if(startScan > 0) {
              g2.fillRect(0, 0, startScan, height);
            }
            if(stopScan < width) {
              g2.fillRect(stopScan, 0, width, height);
            }
          }
          else {
            if(startScan > 0 && stopScan < width) {
              g2.fillRect(startScan, 0, ((stopScan-startScan) + 1), height);
            }
            else if(startScan > 0) {
              g2.fillRect(startScan, 0, width, height);
            }
            else {
              g2.fillRect(0, 0, stopScan, height);
            }
          }

          // draw the lines
          g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0F));
          g2.setColor(feature.getFeatureColor());

          if(startScan > 0) {
            g2.drawLine(startScan, 0, startScan, height);
          }
          if(stopScan < width) {
            g2.drawLine(stopScan, 0, stopScan, height);
          }

          g2.setTransform(initTrans);
        }
      }
    }
	}
  
  private char getBaseCharAt(String amplimer, int indexOfBaseToHighlight) {
    return amplimer.charAt(indexOfBaseToHighlight);
  }
  
  private Color getBaseHiglightAt(String amplimer, int indexOfBaseToHighlight) {
    char baseLet = getBaseCharAt(amplimer, indexOfBaseToHighlight);
    Color col = null;
    if(baseLet == 'A') {
      col = SimpleChromGraphic.A_HIGHLIGHT_COLOR;
    }
    else if(baseLet == 'C') {
      col = SimpleChromGraphic.C_HIGHLIGHT_COLOR;
    }
    else if(baseLet == 'G') {
      col = SimpleChromGraphic.G_HIGHLIGHT_COLOR;
    }
    else if(baseLet == 'T') {
      col = SimpleChromGraphic.T_HIGHLIGHT_COLOR;
    }
    else {
      col = SimpleChromGraphic.generateFillForColor(Color.CYAN);
    }
    return col;
  }
  
  private Color getBaseColorAt(String amplimer, int indexOfBaseToHighlight) {
    char baseLet = getBaseCharAt(amplimer, indexOfBaseToHighlight);
    Color col = null;
    if(baseLet == 'A') {
      col = SimpleChromGraphic.A_BASE_COLOR;
    }
    else if(baseLet == 'C') {
      col = SimpleChromGraphic.C_BASE_COLOR;
    }
    else if(baseLet == 'G') {
      col = SimpleChromGraphic.G_BASE_COLOR;
    }
    else if(baseLet == 'T') {
      col = SimpleChromGraphic.T_BASE_COLOR;
    }
    else {
      col = Color.CYAN;
    }
    return col;
  }
  
  private void drawBaseChangeSimp(Graphics2D g2, SimpleChromGraphic chromGraph, ChromRenderBean crb, int baseToHighlight, boolean drawBaseChar, boolean highlight) {
    int indexOfBase = baseToHighlight - 1;
    
    int startScanOffset = chromGraph.getStartScan();
    float scaleFac = chromGraph.getHorizScale();
    int width = chromGraph.getRenderedWidth();
    int height = chromGraph.getRenderedHeight();
    
		int[] boundries = getScanBoundries(crb.getPerBaseScans(), indexOfBase);
    
    if(boundries[0] < chromGraph.getStartScan()) {
      log.warn("Base feature begins before image coverage, skipping");
    }
    else if(boundries[1] > chromGraph.getStopScan()) {
      log.warn("Base feature ends after image coverage, skipping");
    }
    else {
      int start = (int) ((boundries[0] - startScanOffset) * scaleFac);
      int stop = (int) ((boundries[1] - startScanOffset) * scaleFac);
      int drawWidth = stop - start;

      if(drawWidth > width) {
        log.warn("Tried to render end base change outside of image, truncating");
        drawWidth = width;
      }

      if(highlight) {
        Color baseFill = getBaseHiglightAt(crb.getAmplimerSeq(), indexOfBase);
        g2.setColor(baseFill);
        g2.fillRect(start, 0, drawWidth, height);
      }

      if(drawBaseChar) {
        char baseChar = getBaseCharAt(crb.getAmplimerSeq(), indexOfBase);
        if(highlight) {
          g2.setColor(Color.BLACK);
        }
        else {
          g2.setColor(getBaseColorAt(crb.getAmplimerSeq(), indexOfBase));
        }
        int charOffset = 0;
        if(drawWidth > 8) {
          charOffset = (drawWidth - 8) / 2;
          if(charOffset > 0 && charOffset % 2 != 0) {
            charOffset--;
          }
        }
        g2.drawString(""+baseChar, start + charOffset, TEXT_BUFFER);
      }
    }
	}
  
  private int[] getScanBoundries(List scanList, int indexOfBaseToHighlight) {
		int [] boundries = new int[2];
    
    Integer currScanFromList = (Integer)scanList.get(indexOfBaseToHighlight);
    int currScan = 0;
		if(currScanFromList != null) {
			currScan = currScanFromList.intValue();
		}
		else {
			currScan = estimateBaseScan(scanList, indexOfBaseToHighlight);
		}

		boundries[0] = prevScanBoundry(scanList, indexOfBaseToHighlight, currScan);
		boundries[1] = nextScanBoundry(scanList, indexOfBaseToHighlight, currScan);
    
		return boundries;
	}
  
  private int [] convertBaseToScan(Chromatogram chrom, List scanList, int startAtBase, int stopAtBase) {
    int startAtScan = 0;
    int stopAtScan = 0;
    if(startAtBase == 0 && stopAtBase == 0) {
      stopAtScan = chrom.getTraceLength();
    }
    else {
      if(startAtBase > stopAtBase) {
        int tmpStop = startAtBase;
        startAtBase = stopAtBase;
        stopAtBase = tmpStop;
      }
      // convert to index pos
      startAtBase--;
      stopAtBase--;
			
			int middleBase = startAtBase + ((stopAtBase - startAtBase + 1) / 2);
			
			Integer scan = null;
			if(scanList.size() > middleBase && middleBase >= 0) {
				scan = (Integer)scanList.get(middleBase);
			}
			int middleScan = 0;
			if(scan == null) {
				middleScan = estimateBaseScan(scanList, middleBase);
			}
			else {
				middleScan = scan.intValue();
			}
      
      int surroundingBases = (int) (DEFAULT_VAR_VIEW_BASES - 1) / 2;
      
			startAtScan = middleScan - (surroundingBases * APPROX_PEAK_SPACE);
			stopAtScan = middleScan + (surroundingBases * APPROX_PEAK_SPACE);
    }
    int [] scanRange = {startAtScan, stopAtScan};
    return scanRange;
  }
  
  /**
	 * Works either direction from hole and calculates the average base spacing in that region
	 */
	private int estimateBaseScan(List scanList, int indexOfBasePos) {
		int checkedB = 0;
		int basicScanB = 0;
		for(int i=indexOfBasePos-1; i>=0; i--) {
			checkedB++;
			Integer scan = (Integer)scanList.get(i);
			if(scan != null) {
				basicScanB = scan.intValue();
				break;
			}
		}
		
		int checkedF = 0;
		int basicScanF = 0;
		for(int i=indexOfBasePos+1; i<scanList.size(); i++) {
			checkedF++;
			Integer scan = (Integer)scanList.get(i);
			if(scan != null) {
				basicScanF = scan.intValue();
				break;
			}
		}
		
		int aveSpace = (basicScanF - basicScanB) / (checkedB + checkedF);
		
		return basicScanB + (aveSpace * checkedB);
	}
  
  private int prevScanBoundry(List scanList, int indexOfBaseToHighlight, int currScan) {
		int prevScan = -1;
		int basesChecked = 0;
		for(int i = indexOfBaseToHighlight-1; i >= 0; i--) {
			basesChecked++;
			Integer scanToCheck = (Integer)scanList.get(i);
			if(scanToCheck != null) {
				prevScan = scanToCheck.intValue();
				break;
			}
		}
		int scanStart;
    if(prevScan == -1) {
      scanStart = currScan - 5;
    }
    else {
      scanStart = currScan - ((currScan - prevScan + 1) / (2 * basesChecked));
    }
    
		return scanStart;
	}
  
  private int nextScanBoundry(List scanList, int indexOfBaseToHighlight, int currScan) {
		int nextScan = -1;
		int basesChecked = 0;
		for(int i = indexOfBaseToHighlight+1; i < scanList.size(); i++) {
			basesChecked++;
			Integer scanToCheck = (Integer)scanList.get(i);
			if(scanToCheck != null) {
				nextScan = scanToCheck.intValue();
				break;
			}
		}
		int scanStop;
    if(nextScan == -1) {
      scanStop = currScan + 5;
    }
    else {
      scanStop = currScan + ((nextScan - currScan + 1) / (2 * basesChecked));
    }
    
		return scanStop;
	}
  
  public void drawChromatogram(Graphics2D g2, SimpleChromGraphic chromGraph) {
		// this actually draws the chromatogram on it's own
    chromGraph.drawTo(g2);
	}
	
	// DONT WORRY ABOUT BELOW HERE
	
	/**
   * Creates a new instance of ChromatogramRenderer
   */
	public ChromatogramRenderer () {
    this(DEFAULT_INTENSITY_CHOOSER);
  }
  
  public ChromatogramRenderer(IntensityChooser intensityChooser) {
    this.intensityChooser = intensityChooser;
  }
	
}
