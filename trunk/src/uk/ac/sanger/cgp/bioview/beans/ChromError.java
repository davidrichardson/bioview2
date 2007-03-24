package uk.ac.sanger.cgp.bioview.beans;

/*
 * Copyright (c) 2006 Genome Research Ltd.
 * Author: Cancer Genome Project, cgpit@sanger.ac.uk
 *
 * THIS SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the BSD License.
 *
 * Any redistribution or derivation in whole or in part including any
 * substantial portion of this code must include this copyright and permission
 * notice. 
 */

/**
 * This class converts handled exceptions into meaningful messages.  It should not be used for exceptions that will be thrown.
 * @author Original: kr2
 * @author $Author: kr2 $
 * @version $Revision: 1.5 $
 */
public class ChromError {
	
  /**
   * The message held in the enum.
   */
	private final String message;
	
  /**
   * Constructor for the ChromError.
   * 
   * @param message the message to be held
   */
	private ChromError(String message) {
		this.message = message;
	}
	
  /**
   * Get a string representation of the object.
   * @return a string representation of the object
   */
	public String toString() {
		return message;
	}
	
  /**
   * Use when catching and handling file access problems.
   */
	public static final ChromError IO = new ChromError("File access problem"); // use for CSAException also
  /**
   * Use when catching and handling unknown chromatogram objects (via BioJava).
   */
	public static final ChromError CHROMATOGRAM_FORMAT = new ChromError("Invalid chromatogram object");
  /**
   * Use when catching and handling files that do not appear to be ab1 or scf.
   */
	public static final ChromError UNKNOWN_CHROMATOGRAM = new ChromError("Not a valid scf or ab1 file");
  /**
   * This is here to handle a specific problem seen in autoCSA on Linux under a specific version of Java.
   * Basically the evaluation of the condition in the loop was not being executed for some unknown reason.
   * Hopefully this will never be seen as it has not occurred since 2005.
   */
	public static final ChromError CSA_CHECKED_RUNTIME = new ChromError("Be afraid - loop counter has failed and caught using explicit check in loop, we hope you never see this it seems to be an intermittent java bug");
  /**
   * Bad trace is a standard exception thrown by autoCSA, usually due to insufficient peaks.
   */
	public static final ChromError BAD_TRACE = new ChromError("Bad trace detected, normally due to insufficient number of peaks");
  /**
   * This can be thrown by autoCSA when the mobility correction step fails, currently only applies to 'ab1' files.
   */
	public static final ChromError FAILED_MOB_CORR = new ChromError("Failed to mobility correct");
  /**
   * The region of the amplimer or image being worked on is outside of the coverage.
   */
  public static final ChromError OUTSIDE_OF_COVERAGE = new ChromError("Region is outside of coverage");
  /**
   * Use when catching and handling an UnsupportedChromatogramFormatException from BioJava.
   */
  public static final ChromError UNSUPPORTED_CHROM_FORMAT = new ChromError("UnsupportedChromatogramFormatException was detected when parsing this trace");
  /**
   * Use when catching and handling an BadCommentException from autoCSA caused by missing DYEP.
   */
  public static final ChromError BAD_COMMENT_BLOCK = new ChromError("Bad comments block in file, DYEP information was not found");
  /**
   * Use when catching and handling a BioError with root cause of IllegalSymbolException.
   */
  public static final ChromError BAD_AB1_BASE_CALL = new ChromError("Invalid characters in ab1 files base calling");
  
  /**
   * Use when indicating that a start scan position in the trace cannot be identified.
   */
  public static final ChromError UNKNOWN_SCAN_START = new ChromError("Cannot determine the start scan");
  /**
   * Use when indicating that a stop scan position in the trace cannot be identified.
   */
  public static final ChromError UNKNOWN_SCAN_STOP = new ChromError("Cannot determine the stop scan");
  
  /**
   * Use when an unexpected exception is thrown
   */
  public static final ChromError UNKNOWN_REASON = new ChromError("Unexpected problem, see stack in logs");
}
