/*
 * HarshMeanIntensityChooser.java
 *
 * Created on April 25, 2007, 8:57 AM
 *
 */

package uk.ac.sanger.cgp.bioview.chromatogram;

/**
 * Compare the max scan with the mean + a number of std deviations.
 * Use whichever is lower. 
 *
 * @author dr4
 * @author $Author$
 * @version $Revision$
 */
public class HarshMeanIntensityChooser extends AbstractMeanIntensityChooser {
  
  private double stdDevsFromMean = 3;
      
  /** Creates a new instance of HarshMeanIntensityChooser */
  public HarshMeanIntensityChooser() {
  }

  public double getStdDevsFromMean() {
    return stdDevsFromMean;
  }

  public void setStdDevsFromMean(double stdDevsFromMean) {
    this.stdDevsFromMean = stdDevsFromMean;
  }

  public int chooseIntensityFromMaxMeanAndDev(double maxIntensity, double meanIntensity, double stdDev) {
    return (int)Math.round(
        Math.min(
          maxIntensity,
          meanIntensity + (stdDev * stdDevsFromMean)
          ));
  }
  
}
