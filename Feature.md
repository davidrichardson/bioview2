# Introduction #

What is a feature, why would you use it, what can it be applied to?


# Details #

Bioview currently has 5 different types of feature:

  1. Domain
  1. Variant call
  1. Amplimer sequence
  1. Inclusive boundry
  1. Exclusive boundry

The function of these is as follows

### Domain ###
This is a region of an image to be indicated using a boxed shaded bar of colour.
The mapping and orientation of this feature is dependent on the context of the master image is is being applied to.  In the case of a Chromatogram it will appear as a horizontal bar below the chromatogram image.  Multiple domains can be defined and should be used in conjunction with a legend (CookbookFeatureLegendImage)

### Variant call ###
A variant call is an unboxed translucent shaded region which can be overlayed on an interesting portion of an image.  Currently the only expected use is to highlight variant base locations on a chromatogram.  For examples of this in use see [Standalone AutoCSA](http://www.sanger.ac.uk/genetics/CGP/Software/AutoCSA/demo/BRAFexon11/variant-subPath-NCI-H1395-1-f.html).

### Amplimer sequence ###
Define a DNA sequence to be rendered over the master image.  In the case of a chromatogram this is aligned to the peaks of the image (provided the peak locations can be provided).  The intention is for this to be used in the planned Exon/Domain viewer and so may be relaxed to 'sequence'

### Inclusive boundry ###
This is a region of an image that should be indicated with an unboxed shading between two points.  Very similar to a 'Variant call'.

### Exclusive boundry ###
This is a region of an image that should be indicated with two unboxed shaded regions outside of the specified points (with a solid line at each point).  Running from the begining of the image to the first point and from the second point to the end of the image.  For examples of this in use see [Standalone AutoCSA](http://www.sanger.ac.uk/genetics/CGP/Software/AutoCSA/demo/BRAFexon11/full_trace/NCI-H1395-1-f.png).