package uk.ac.sanger.cgp.bioview.enums;

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
 * Used to encapsulate different types of FeatureType
 * @author Original: kr2
 * @author $Author: kr2 $
 * @version $Revision: 1.1 $
 */
public class FeatureType {
	
  /**
   * The name of the path.
   */
	private final String featureType;
	
  /**
   * Construct a new FeatureType with the specified path string.
   * @param displayPath name to be applied to this display path
   */
	private FeatureType(String featureType) {
		this.featureType = featureType;
	}
	
  /**
   * String representation of this object.
   * @return string representation of this object
   */
	public String toString() {
		return featureType;
	}
  
  /**
   * FeatureType to indicate domain features
   */
  public static final FeatureType DOMAIN = new FeatureType("domain");
  
  /**
   * FeatureType to indicate variant call features
   */
	public static final FeatureType VARIANT_CALL = new FeatureType("variant call");
  
  /**
   * FeatureType to indicate amplimer sequence drawing
   */
	public static final FeatureType RENDER_AMPLIMER = new FeatureType("amplimer seq");
  
  /**
   * FeatureType to indicate a feature where the range between the provided positions should be shaded
   */
	public static final FeatureType INCLUSIVE_BOUNDRY = new FeatureType("inclusive boundry");
  
  /**
   * FeatureType to indicate a feature where the range outside the provided positions should be shaded
   */
	public static final FeatureType EXCLUSIVE_BOUNDRY = new FeatureType("exclusive boundry");
  
  public boolean equals(Object o) {
    FeatureType that = (FeatureType)o;
    return this.toString().equals(that.toString());
  }
}
