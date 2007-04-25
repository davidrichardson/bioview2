/*
 * AbstractMeanIntensityChooser.java
 *
 * Created on April 25, 2007, 8:50 AM
 *
 */

package uk.ac.sanger.cgp.bioview.chromatogram;

import org.apache.commons.math.stat.StatUtils;
import org.biojava.bio.chromatogram.Chromatogram;
import org.biojava.bio.seq.DNATools;
import org.biojava.bio.symbol.IllegalSymbolException;

/**
 * Extracts the mean, max and std dev from the maximum intensity per scan.
 * 
 * Delegates decisions about what to do with that information to the implementing
 * class.
 *
 * @author dr4
 * @author $Author$
 * @version $Revision$
 */
public abstract class AbstractMeanIntensityChooser extends AbstractIntensityChooser{
  
  public int chooseIntensityFromMaxPerScan(double[] maxIntensityPerScan){

    double maxScanFound = StatUtils.max(maxIntensityPerScan);
    double mean = StatUtils.mean(maxIntensityPerScan);
    double stdDev = Math.sqrt(StatUtils.variance(maxIntensityPerScan));    
    
    return chooseIntensityFromMaxMeanAndDev(maxScanFound, mean, stdDev);    
  }
  
  abstract int chooseIntensityFromMaxMeanAndDev(double maxScanFound, double mean, double stdDev);
  
}
