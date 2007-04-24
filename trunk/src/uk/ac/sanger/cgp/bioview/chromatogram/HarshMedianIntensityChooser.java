/*
 * HarshMedianIntensityChooser.java
 *
 * Created on April 5, 2007, 2:10 PM
 *
 */

package uk.ac.sanger.cgp.bioview.chromatogram;

/**
 * Use the medianIntensity x sf, or the max intensity - whichever is smaller. 
 *
 *
 * @author dr4
 * @author $Author$
 * @version $Revision$
 */
public class HarshMedianIntensityChooser extends AbstractMedianIntensityChooser {
  
  private double scaleFactor = 3;
  
  /** Creates a new instance of HarshMedianIntensityChooser */
  public HarshMedianIntensityChooser() {
  }

  int chooseIntensityFromMaxAndMedian(double maxIntensity, double medianIntensity) {
    return (int)Math.round(Math.min( maxIntensity, medianIntensity * scaleFactor));
  }

  public double getScaleFactor() {
    return scaleFactor;
  }

  public void setScaleFactor(double scaleFactor) {
    this.scaleFactor = scaleFactor;
  }
  
}
