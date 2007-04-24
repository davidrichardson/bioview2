/*
 * MaxIntensityChooser.java
 *
 * Created on April 5, 2007, 11:41 AM
 *
 */

package uk.ac.sanger.cgp.bioview.chromatogram;

import org.biojava.bio.chromatogram.Chromatogram;
import org.biojava.bio.seq.DNATools;
import org.biojava.bio.symbol.IllegalSymbolException;

/**
 *
 * @author dr4
 * @author $Author$
 * @version $Revision$
 */
public class MaxIntensityChooser implements IntensityChooser {
  
  /**
   * Creates a new instance of MaxIntensityChooser
   */
  public  MaxIntensityChooser(){
  }

  
  public int selectIntensity(Chromatogram c, int startScan, int stopScan) throws IllegalSymbolException {
    int[] aScans = c.getTrace(DNATools.a());
    int[] tScans = c.getTrace(DNATools.t());
    int[] gScans = c.getTrace(DNATools.g());
    int[] cScans = c.getTrace(DNATools.c());
    
    int minScan = Math.max(0, startScan);
    int maxScan = Math.min(c.getTraceLength() - 1, stopScan);
    
    int maxIntensity = 0;   
    
    for (int i = minScan; i <= stopScan; i++) {
      maxIntensity = Math.max(maxIntensity, aScans[i]);
      maxIntensity = Math.max(maxIntensity, tScans[i]);
      maxIntensity = Math.max(maxIntensity, gScans[i]);
      maxIntensity = Math.max(maxIntensity, cScans[i]);
    }   
    
    return maxIntensity;
  }
  
  
}
