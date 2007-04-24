/*
 * IntensityChooser.java
 *
 * Created on April 5, 2007, 11:43 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package uk.ac.sanger.cgp.bioview.chromatogram;

import org.biojava.bio.chromatogram.Chromatogram;
import org.biojava.bio.symbol.IllegalSymbolException;

/**
 * Select an intensity to scale to, based on the region of the chromatogram 
 * that you wish to render.
 *
 * @author Dave Richardson
 */
public interface IntensityChooser {
  
  int selectIntensity(Chromatogram c, int startScan, int stopScan) throws IllegalSymbolException; 
  
}
