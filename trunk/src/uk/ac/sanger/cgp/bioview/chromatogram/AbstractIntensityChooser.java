/*
 * AbstractIntensityChooser.java
 *
 * Created on April 25, 2007, 2:45 PM
 *
 */

package uk.ac.sanger.cgp.bioview.chromatogram;

import org.biojava.bio.chromatogram.Chromatogram;
import org.biojava.bio.seq.DNATools;
import org.biojava.bio.symbol.IllegalSymbolException;

/**
 * Reads the 4 channels from the chromatogram and mixes them down to one - 
 * always using the maximum intensity per scan, across the source channels.
 *
 * Only samples from the region to render.
 *
 * Delegates decision about what to do with that information to subclasses
 *
 *
 * @author dr4
 * @author $Author$
 * @version $Revision$
 */
public abstract class AbstractIntensityChooser implements IntensityChooser {
  
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
    
    return chooseIntensityFromMaxPerScan(maxIntensityPerScan);
  }
  
  abstract int chooseIntensityFromMaxPerScan(double[] maxIntensityPerScan);
  

  
}
