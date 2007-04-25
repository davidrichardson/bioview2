/*
 * MaxIntensityChooser.java
 *
 * Created on April 5, 2007, 11:41 AM
 *
 */

package uk.ac.sanger.cgp.bioview.chromatogram;

import org.apache.commons.math.stat.StatUtils;
import org.biojava.bio.chromatogram.Chromatogram;
import org.biojava.bio.seq.DNATools;
import org.biojava.bio.symbol.IllegalSymbolException;

/**
 * Select the maximum intensity from the sampled region
 *
 * @author dr4
 * @author $Author$
 * @version $Revision$
 */
public class MaxIntensityChooser extends AbstractIntensityChooser { 

  int chooseIntensityFromMaxPerScan(double[] maxIntensityPerScan) {
    return (int)Math.round( StatUtils.max(maxIntensityPerScan) );
  }
  
  
}
