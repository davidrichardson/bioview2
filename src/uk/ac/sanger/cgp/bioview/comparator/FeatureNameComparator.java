package uk.ac.sanger.cgp.bioview.comparator;

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

import java.util.Comparator;
import uk.ac.sanger.cgp.bioview.beans.FeatureBean;

/**
 * @author Original: keiranmraine.code@gmail.com
 * @author  $Author:$
 * @version $Revision:$
 */
public class FeatureNameComparator implements Comparator{
  
  public int compare(Object o1, Object o2) {
    FeatureBean feat1 = (FeatureBean) o1;
    FeatureBean feat2 = (FeatureBean) o2;
    
    return feat1.getFeatureName().compareTo(feat2.getFeatureName());
  }
  
}
