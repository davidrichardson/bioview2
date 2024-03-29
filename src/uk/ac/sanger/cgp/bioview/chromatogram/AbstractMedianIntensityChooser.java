/*
 * AbstractMedianIntensityChooser.java
 *
 * Created on April 5, 2007, 1:27 PM
 *
 */

package uk.ac.sanger.cgp.bioview.chromatogram;

import org.apache.commons.math.stat.StatUtils;
import org.biojava.bio.chromatogram.Chromatogram;
import org.biojava.bio.seq.DNATools;
import org.biojava.bio.symbol.IllegalSymbolException;

/**
 * Extracts the median and maximum intensity from the maximum intensity per scan.
 * 
 * Delegates decisions about what to do with that information to the implementing
 * class.
 *
 * @author dr4
 * @author $Author$
 * @version $Revision$
 */
public abstract class AbstractMedianIntensityChooser extends AbstractIntensityChooser {
  
  
  public int chooseIntensityFromMaxPerScan(double[] maxIntensityPerScan){
    double maxScanFound = StatUtils.max(maxIntensityPerScan);
    double median = StatUtils.percentile(maxIntensityPerScan, 50);
    
    return chooseIntensityFromMaxAndMedian(maxScanFound, median);    
  }
  
  abstract int chooseIntensityFromMaxAndMedian(double maxIntensity, double medianIntensity);
}
