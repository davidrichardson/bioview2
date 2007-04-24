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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.biojava.bio.BioError;
import org.biojava.bio.chromatogram.Chromatogram;
import org.biojava.bio.seq.DNATools;
import org.biojava.bio.symbol.IllegalSymbolException;
import uk.ac.sanger.cgp.bioview.exceptions.ChromatogramRenderException;

/**
 * @author Original: kr2
 * @author  $Author: kr2 $
 * @version $Revision: 1.8 $
 */
public class SimpleChromGraphic {
	protected Log log = LogFactory.getLog(this.getClass());
	private static final IntensityChooser DEFAULT_INTENSITY_CHOOSER = new MaxIntensityChooser();
  
	private int startScan = -1;
	private int stopScan = -1;
	int[][] samples = new int[5][];
	private int maxIntensity = -1;
	private int height = -1;
	private float horizScale = -1.0F;
	private float vertScaleFac = -1.0F;
	private int renderedWidth = -1;
  private int topHeightBuff = 0;
  private int scaledHeightBuff = 0;
  private int renderedHeight = -1;
  private int maxScan = -1;
  private boolean renderScale = false;
  private int xOffset = 0;
  private int scaledXoffset = 0;
  
  
  
  public static final Color A_BASE_COLOR = Color.GREEN;
  public static final Color C_BASE_COLOR = Color.BLUE;
  public static final Color G_BASE_COLOR = Color.BLACK;
  public static final Color T_BASE_COLOR = Color.RED;
  
  public static final Color A_HIGHLIGHT_COLOR = generateFillForColor(A_BASE_COLOR);
  public static final Color C_HIGHLIGHT_COLOR = generateFillForColor(C_BASE_COLOR);
  public static final Color G_HIGHLIGHT_COLOR = generateFillForColor(G_BASE_COLOR);
  public static final Color T_HIGHLIGHT_COLOR = generateFillForColor(T_BASE_COLOR);
  
  private static final int SCALE_WIDTH = 55;
	
	private static final int A = 0, C = 1, G = 2, T = 3, X = 4;
	
	protected SimpleChromGraphic() {
	}
	
  /**
   * The supplied height is used to calculate the scaling factor for the image.
   * To add a buffer above peaks setHeightBuffer in px...this will be divided by the scaling factor and applied across the coords
   * @param chrom 
   * @param startScanIn 
   * @param stopScanIn 
   * @param heightIn 
   * @param horizScaleIn 
   * @throws uk.ac.sanger.cgp.biodraw.exceptions.ChromatogramRenderException 
   */
	public SimpleChromGraphic(Chromatogram chrom, int startScanIn, int stopScanIn, int heightIn, float horizScaleIn) throws ChromatogramRenderException {
    setUp(chrom, startScanIn, stopScanIn, heightIn, horizScaleIn, topHeightBuff, renderScale);
	}
  
    public SimpleChromGraphic(Chromatogram chrom, int startScanIn, int stopScanIn, int heightIn, float horizScaleIn, IntensityChooser intensityChooser) throws ChromatogramRenderException {
    setUp(chrom, startScanIn, stopScanIn, heightIn, horizScaleIn, topHeightBuff, renderScale, intensityChooser);
	}
  
  public SimpleChromGraphic(Chromatogram chrom, int startScanIn, int stopScanIn, int heightIn, float horizScaleIn, int topHeightBuffIn, boolean renderScaleIn) throws ChromatogramRenderException {
    setUp(chrom, startScanIn, stopScanIn, heightIn, horizScaleIn, topHeightBuffIn, renderScaleIn);
  }
  
  public SimpleChromGraphic(Chromatogram chrom, int startScanIn, int stopScanIn, int heightIn, float horizScaleIn, int topHeightBuffIn, boolean renderScaleIn, IntensityChooser intensityChooser) throws ChromatogramRenderException {
    setUp(chrom, startScanIn, stopScanIn, heightIn, horizScaleIn, topHeightBuffIn, renderScaleIn, intensityChooser);
  }
  
  private void setUp(Chromatogram chrom, int startScanIn, int stopScanIn, int heightIn, float horizScaleIn, int topHeightBuffIn, boolean renderScaleIn) throws ChromatogramRenderException {
    setUp(chrom, startScanIn, stopScanIn, heightIn, horizScaleIn, topHeightBuffIn, renderScaleIn, DEFAULT_INTENSITY_CHOOSER);
  }
  
  private void setUp(Chromatogram chrom, int startScanIn, int stopScanIn, int heightIn, float horizScaleIn, int topHeightBuffIn, boolean renderScaleIn, IntensityChooser intensityChooser) throws ChromatogramRenderException {
    if(startScanIn < 0 && stopScanIn < 0) {
      throw new ChromatogramRenderException("Only the start scan indicies can be negative for padding purposes");
    }
    
		// allow startScan to be (-)ve to pad image
		// allow stopScan to be greater than channel to pad image
		startScan = startScanIn;
		stopScan = stopScanIn;
		height = heightIn - 1; // height excludes any buffers
		horizScale = horizScaleIn;
    topHeightBuff = topHeightBuffIn;
    if(renderScaleIn) {
      renderScale = true;
      xOffset = SCALE_WIDTH;
    }
    
    maxScan = stopScan - startScan;
		
		try {
			samples[A] = setupSamples(chrom.getTrace(DNATools.a()));
			samples[C] = setupSamples(chrom.getTrace(DNATools.c()));
			samples[G] = setupSamples(chrom.getTrace(DNATools.g()));
			samples[T] = setupSamples(chrom.getTrace(DNATools.t()));
      maxIntensity = intensityChooser.selectIntensity(chrom, startScan, stopScan);
		}
		catch (IllegalSymbolException ise) {
			throw new BioError("Can't happen");
		}

		invertSampleIntensities();
		calcVertScalingFactor();
    calcXcoords();
    
    if(topHeightBuff != 0){
      scaledHeightBuff = (int)(topHeightBuff / vertScaleFac);
    }
    if(xOffset != 0){
      scaledXoffset = (int)(xOffset / horizScale);
    }
		renderedWidth = (int) Math.ceil((double)((samples[A].length + scaledXoffset) * horizScale));
    renderedHeight = (int) (Math.ceil((double) ((maxIntensity + scaledHeightBuff) * vertScaleFac))) + 1;
  }
  
  public static Color generateFillForColor(Color baseCol) {
    float[] hsb = Color.RGBtoHSB(baseCol.getRed(), baseCol.getGreen(), baseCol.getBlue(), null);
    // hue, saturation, brightness
    //return Color.getHSBColor(hsb[0], hsb[1] * 0.09f, Math.max(hsb[2], 0.8f));
    //return Color.getHSBColor(hsb[0], hsb[1] * 0.09f, Math.max(hsb[2], 0.9f));
    return Color.getHSBColor(hsb[0], hsb[1] * 0.18f, Math.max(hsb[2], 0.8f));
    //return Color.getHSBColor(hsb[0], hsb[1] * 0.18f, Math.max(hsb[2], 0.85f));
  }
	
	private void calcVertScalingFactor() {
		vertScaleFac = ( (float) height ) / maxIntensity;
	}
	
	private void invertSampleIntensities() {
		samples[A] = invertSampleIntensity(samples[A]);
		samples[C] = invertSampleIntensity(samples[C]);
		samples[G] = invertSampleIntensity(samples[G]);
		samples[T] = invertSampleIntensity(samples[T]);
	}
	
	private int [] invertSampleIntensity(int [] sample) {
		int [] newSample = new int[sample.length];
		for(int i=0; i<sample.length; i++) {
      // flip the intensity values (as draw from top of image)
      // add on any height buffers
			newSample[i] = (maxIntensity - sample[i]);
		}
		return newSample;
	}
	
	private int[] setupSamples(int[] sample) {
		// allow startScan to be (-)ve to pad image
		// allow stopScan to be greater than channel to pad image
		int arraySize = stopScan - startScan;
    
		int [] newSample = new int[arraySize];
		
		int arrPos = 0;
		for(int i=startScan; i<stopScan; i++) {
			if(i < 0) {
				newSample[arrPos] = 0;
			}
			else if(i >= sample.length) {
				newSample[arrPos] = 0;
			}
			else {
				newSample[arrPos] = sample[i];
			}
			arrPos++;
		}
		return newSample;
	}
  
  private void calcXcoords() {
		samples[X] = new int[maxScan];
		for(int i=0; i<maxScan; i++) {
			samples[X][i] = i;
		}
	}
  
  private void scaleCoords() {
    scaleVertCoords(samples[A]);
    scaleVertCoords(samples[C]);
    scaleVertCoords(samples[G]);
    scaleVertCoords(samples[T]);
    scaleHorizCoords(samples[X]);
  }
  
  private void scaleVertCoords(int[] sample) {
    for(int i=0; i<sample.length; i++) {
      sample[i] = (int) Math.ceil((double) sample[i] * vertScaleFac);
    }
  }
  
  private void scaleHorizCoords(int[] sample) {
    for(int i=0; i<sample.length; i++) {
      sample[i] = (int) Math.ceil((double) sample[i] * horizScale);
    }
  }
  
  private int calcScaleCoord(int scaleVal) {
    return (int)((maxIntensity - scaleVal) * vertScaleFac) + topHeightBuff;
  }
	
	public void drawTo(Graphics2D g2) {
    
    if(renderScale) {
      int thirdScaleVal = roundedThirdScaleVal();
      // 3 times bigger than the rounded third val
      int roundedMaxScaleVal = thirdScaleVal * 3;
      
      g2.setColor(Color.BLACK);
      
      int bottomAxisPos = renderedHeight;
      
      g2.drawLine(xOffset-1, 0, xOffset-1, bottomAxisPos); // y axis
      g2.drawLine(renderedWidth, 0, renderedWidth, bottomAxisPos); // y axis
      g2.drawLine(xOffset-5, bottomAxisPos, renderedWidth, bottomAxisPos); // y axis
      
      int thirdLine = calcScaleCoord(thirdScaleVal);
      int maxLine = calcScaleCoord(roundedMaxScaleVal);
      
      g2.drawLine(xOffset - 5, maxLine, renderedWidth, maxLine);
      g2.drawString(roundedMaxScaleVal + "", 5, 5 + maxLine);

      g2.drawLine(xOffset - 5, thirdLine, renderedWidth, thirdLine);
      g2.drawString(thirdScaleVal + "", 5, 5 + thirdLine);
    }
    
    scaleCoords();
    
    Stroke initStroke = g2.getStroke();
    
    g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    
    AffineTransform initTrans = g2.getTransform();
    setupOffsetTransform(g2);
    
    
    g2.setColor(Color.GREEN);
		drawSample(g2, samples[A]);
		g2.setColor(Color.BLUE);
		drawSample(g2, samples[C]);
		g2.setColor(Color.BLACK);
		drawSample(g2, samples[G]);
		g2.setColor(Color.RED);
		drawSample(g2, samples[T]);
    
    g2.setTransform(initTrans);
    g2.setStroke(initStroke);
    
    /*log.warn("bounding box is being drawn");
    g2.setColor(Color.BLACK);
    g2.drawRect(0,0,renderedWidth, renderedHeight);*/
	}
	
	private void drawSample(Graphics2D g2, int[] sample) {
		g2.drawPolyline(samples[X], sample, sample.length);
	}
  
  private int roundedThirdScaleVal() {
    return formatScaleNum((int) (maxIntensity / 3));
  }

  public int getRenderedWidth() {
    return renderedWidth + 1;
  }

  public int getRenderedHeight() {
    return renderedHeight + 1;
  }
  
  public float getHorizScale() {
    return horizScale;
  }
  
  public int getStartScan() {
    return startScan;
  }
  
  public int getStopScan() {
    return stopScan;
  }
  
  /**
   * Developer is responsible for saving the original transform and reinstating
   * @param g2 
   */
  private void setupOffsetTransform(Graphics2D g2) {
    if(topHeightBuff != 0 || renderScale) {
      AffineTransform af = new AffineTransform();
      af.translate((double)xOffset, (double)topHeightBuff);
      g2.setTransform(af);
    }
  }
  
  /**
   * Developer is responsible for saving the original transform and reinstating
   * @param g2 
   */
  public void setupXoffsetTransform(Graphics2D g2) {
    if(renderScale) {
      AffineTransform af = new AffineTransform();
      af.translate((double)xOffset, 0.0D);
      g2.setTransform(af);
    }
  }
  
  /**
   * Developer is responsible for saving the original transform and reinstating
   * @param g2 
   */
  /*private void setupYoffsetTransform(Graphics2D g2) {
    if(renderScale) {
      AffineTransform af = new AffineTransform();
      af.translate(0.0D, (double)topHeightBuff);
      g2.setTransform(af);
    }
  }*/
  
  private int formatScaleNum(int scaleVal) {
    //double magnitude = Math.floor(Math.log10((double) scaleVal)); // Java 1.5 Doh!
    double magnitude = Math.floor(log10(scaleVal));
    double factor = Math.floor((double)scaleVal / Math.pow(10.0D, magnitude));
    return (int) (Math.pow(10.0D, magnitude) * factor);
  }
  
  /**
   * Method to generate log 10, this is required for use under Java 1.4
   */
  private double log10(int value) {
    return (Math.log((double)value) / Math.log(10D));
  }

}
