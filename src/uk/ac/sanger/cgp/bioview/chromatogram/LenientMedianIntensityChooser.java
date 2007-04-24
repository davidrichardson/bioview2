/*
 * LenientMedianIntensityChooser.java
 *
 * Created on April 5, 2007, 2:13 PM
 *
 */

package uk.ac.sanger.cgp.bioview.chromatogram;

/**
 * if the max peak height is less than (median intensity * sf), scale to max peak height.
 *
 *  If the difference between (median intensity * sf) and the max peak height is less than difference limit,
 *  and the max peak height is less than 15000, use the max peak height.
 *
 *   Otherwise, use median intensity x sf
 * @author dr4
 * @author $Author$
 * @version $Revision$
 */
public class LenientMedianIntensityChooser extends AbstractMedianIntensityChooser {
  private double differenceLimit = 5000;
  private double peakHeightLimit = 15000;
  private double scaleFactor = 2.5;
  
  /** Creates a new instance of LenientMedianIntensityChooser */
  public LenientMedianIntensityChooser() {
  }
  
  int chooseIntensityFromMaxAndMedian(double maxIntensity, double medianIntensity) {
    /*
    
    */
    double diff = maxIntensity - medianIntensity;
    double scaleIntensity;
    
    
    if (maxIntensity < medianIntensity) {
      scaleIntensity = maxIntensity;
    }
    else if (diff < getDifferenceLimit()
        && maxIntensity < getPeakHeightLimit()) {
      scaleIntensity = maxIntensity;
    }
    else {
      scaleIntensity = medianIntensity * getScaleFactor();
    }
    
    return (int)Math.round(scaleIntensity);
  }

  public double getDifferenceLimit() {
    return differenceLimit;
  }

  public void setDifferenceLimit(double differenceLimit) {
    this.differenceLimit = differenceLimit;
  }

  public double getPeakHeightLimit() {
    return peakHeightLimit;
  }

  public void setPeakHeightLimit(double peakHeightLimit) {
    this.peakHeightLimit = peakHeightLimit;
  }

  public double getScaleFactor() {
    return scaleFactor;
  }

  public void setScaleFactor(double scaleFactor) {
    this.scaleFactor = scaleFactor;
  }
  
}
