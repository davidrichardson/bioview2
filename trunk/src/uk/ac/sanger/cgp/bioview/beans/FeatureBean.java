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

import java.awt.Color;
import uk.ac.sanger.cgp.bioview.enums.FeatureType;
import uk.ac.sanger.cgp.bioview.exceptions.ImageRenderException;

/**
 * This class is used to generate features.  Features are not specific to any
 * component of the BioDraw package, and for this reason are not tied to any
 * form of coordinate mapping.  The type of mapping is determined by the context
 * that the feature is used in, e.g. in the chromatogram the mapping context is the
 * amplimer positions.
 * 
 * Names of feature were introduced initially to alow the creation of a legend table
 * for domain features.  It is anticipated that this will be used in other views such
 * and exon and primer visualisation to label components.
 * @author Original: kr2
 * @author $Author: kr2 $
 * @version $Revision: 1.1 $
 */
public class FeatureBean implements Comparable {
  
  private int featureStart = -1;
  private int featureStop = -1;
  
  private FeatureType featureType = null;
  
  /**
   * The colour that a feature should be rendered in
   * This does not apply to FeatureType.VARIANT_CALL
   */
  private Color featureColor = null;
  
  private String featureName = null;
  
  /**
   * Use this constructor to create features that do not require a Colour.
   * 
   * The start/stop coordinates can relate to the context that the feature is being
   * used in, e.g. in the chromatogram the mapping context is the amplimer positions.
   * @param start Start coordinate of feature.  This can relate to different things dependent on the context.
   * @param stop Stop coordinate of feature.  This can relate to different things dependent on the context.
   * @param type The type of the feature.
   * @throws uk.ac.sanger.cgp.biodraw.exceptions.ChromatogramRenderException Thrown when this constructor is used for features that expect a colour.
   */
  public FeatureBean(int start, int stop, FeatureType type)  throws ImageRenderException {
    this(start, stop, type, null);
  }
  
/**
   * Use this constructor to create features that require a Colour.
   * 
   * The start/stop coordinates can relate to the context that the feature is being
   * used in, e.g. in the chromatogram the mapping context is the amplimer positions.
   * @param start Start coordinate of feature.  This can relate to different things dependent on the context.
   * @param stop Stop coordinate of feature.  This can relate to different things dependent on the context.
   * @param type The type of the feature.
   * @param color The colour to be used when rendering the feature.
   * @throws uk.ac.sanger.cgp.biodraw.exceptions.ChromatogramRenderException Thrown when null is passed as a colour for features that expect a colour.
   */
  public FeatureBean(int start, int stop, FeatureType type, Color color) throws ImageRenderException {
    featureStart = start;
    featureStop = stop;
    featureType = type;
    if(featureType != FeatureType.VARIANT_CALL && color == null) {
      throw new ImageRenderException("All features except "+ FeatureType.VARIANT_CALL +" must be generated with a colour");
    }
    featureColor = color;
  }
  
  /**
   * Use this constructor to create features that require a Colour and a Name.
   * 
   * The start/stop coordinates can relate to the context that the feature is being
   * used in, e.g. in the chromatogram the mapping context is the amplimer positions.
   * 
   * Names of feature were introduced initially to alow the creation of a legend table
   * for domain features.  It is anticipated that this will be used in other views such
   * and exon and primer visualisation to label components.
   * @param start Start coordinate of feature.  This can relate to different things dependent on the context.
   * @param stop Stop coordinate of feature.  This can relate to different things dependent on the context.
   * @param type The type of the feature.
   * @param color The colour to be used when rendering the feature.
   * @param name The name to be applied to the feature.
   * @throws uk.ac.sanger.cgp.biodraw.exceptions.ChromatogramRenderException Thrown when null is passed as a colour for features that expect a colour.
   */
  public FeatureBean(int start, int stop, FeatureType type, Color color, String name) throws ImageRenderException {
    this(start, stop, type, color);
    featureName = name;
  }

  /**
   * Get the feature start.
   * @return start position
   */
  public int getFeatureStart() {
    return featureStart;
  }

  /**
   * Get the feature stop
   * @return stop position
   */
  public int getFeatureStop() {
    return featureStop;
  }

  /**
   * Get the feature type.
   * @return the feature type
   */
  public FeatureType getFeatureType() {
    return featureType;
  }

  /**
   * Get the feature colour.
   * @return the feature colour
   */
  public Color getFeatureColor() {
    return featureColor;
  }
  
  public String getFeatureName() {
    return featureName;
  }
  
  /**
   * String representation of this object.
   * @return a string representing this object.
   */
  public String toString() {
      String nl = System.getProperty("line.separator");
      StringBuffer sb = new StringBuffer();

      String dec = "========";

      sb.append(dec).append(" [S] uk.ac.sanger.cgp.biodraw.beans.FeatureBean ").append(dec).append(nl);
      sb.append("featureStart=").append(featureStart).append(nl);
      sb.append("featureStop=").append(featureStop).append(nl);
      sb.append("featureType=").append(featureType).append(nl);
      sb.append("featureColor=").append(featureColor).append(nl);
      sb.append("featureName=").append(featureName).append(nl);

      sb.append(dec).append(" [E] uk.ac.sanger.cgp.biodraw.beans.FeatureBean ").append(dec);

      return sb.toString();
  }
  
  public int compareTo(Object o) {
    FeatureBean that = (FeatureBean) o;
    return this.getFeatureStart() - that.getFeatureStart();
  }
  
}
