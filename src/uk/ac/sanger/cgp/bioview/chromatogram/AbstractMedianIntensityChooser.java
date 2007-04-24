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
 *
 * @author dr4
 * @author $Author$
 * @version $Revision$
 */
public abstract class AbstractMedianIntensityChooser implements IntensityChooser {
  
  
  public int selectIntensity(Chromatogram c, int startScan, int stopScan) throws IllegalSymbolException {
    int[] aScans = c.getTrace(DNATools.a());
    int[] tScans = c.getTrace(DNATools.t());
    int[] gScans = c.getTrace(DNATools.g());
    int[] cScans = c.getTrace(DNATools.c());
    
    int minScan = Math.max(0, startScan);
    int maxScan = Math.min(aScans.length - 1, stopScan);
    
    double[] maxIntensityPerScan = new double[maxScan - minScan + 1];
    
    for (int i = minScan; i <= maxScan; i++) {
      
      int maxHere = Math.max(
                          Math.max(aScans[i], tScans[i]),
                          Math.max(gScans[i], cScans[i])
                        );
      
      maxIntensityPerScan[i-minScan] = maxHere;
    }
    
    double maxScanFound = StatUtils.max(maxIntensityPerScan);
    double median = StatUtils.percentile(maxIntensityPerScan, 50);
    
    return chooseIntensityFromMaxAndMedian(maxScanFound, median);
    
  }
  
  abstract int chooseIntensityFromMaxAndMedian(double maxIntensity, double medianIntensity);
  
  
}
