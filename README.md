#SIP Version 1 run with java 8

	SIP is implemented in java and includes achoice between command line options or	a graphical user
	 interface (gui) allowing for more general use. This method is intended as an alternative loop 
	 caller especially for difficult to identify loops and works in conjunction with juicebox .hic files.

	Usage:
	
        hic <hicFile> <chrSizeFile> <Output> <juicerToolsPath> [options]
        processed <Directory with processed data> <chrSizeFile> <Output> [options]

	Parameters:
    
        chrSizeFile: path to the chr size file, with the same name of the chr as in the hic file (i.e. chr1 
        does not match Chr1 or 1)
        -res: resolution in bp (default 5000 bp)
        -mat: matrix size to use for each chunk of the chromosome (default 2000 bins)
        -d: diagonal size in bins, remove the maxima found at this size (eg: a size of 2 at 5000 bp resolution 
        removes all maxima detected at a distance inferior or equal to 10kb) (default 6 bins).
        -g: Gaussian filter: smoothing factor to reduce noise during primary maxima detection (default 1.5)
        -cpu: Number of CPU used for SIP processing (default 1)
        -factor: Multiple resolutions can be specified using:          
        		-factor 1: run only for the input res (default)
                -factor 2: res and res*2
                -factor 3: res and res*5
                -factor 4: res, res*2 and res*5
        -max: Maximum filter: increases the region of high intensity (default 2)
        -min: Minimum filter: removes the isolated high value (default 2)
        -sat: % of staturated pixel: enhances the contrast in the image (default 0.01)
        -t Threshold for loops detection (default 2800 for hic)
        -nbZero: number of zeros: number of pixels equal to zero that are allowed in the 24 pixels surrounding
         the detected  maxima (default 6)
        -norm: <NONE/VC/VC_SQRT/KR> (default KR)
        -del: true or false, whether not to delete tif files used for loop detection (default true)
        -fdr: Empirical FDR value for filtering based on random sites (default 0.01)
        -isDroso: default false, if true apply extra filter to help detect loops similar to those found in 
        D. mel cells
        -h, --help print help

		Command line eg:

			java -jar SIP_HiC.jar processed inputDirectory pathToChromosome.size OutputDir .... paramaters
			java -jar SIP_HiC.jar hic inputDirectory pathToChromosome.size OutputDir juicer_tools.jar
			
		Authors:
			Axel Poulet
				Department of Molecular, Cellular  and Developmental Biology Yale University 165 Prospect St
				New Haven, CT 06511, USA
			M. Jordan Rowley
				Department of Genetics, Cell Biology and Anatomy, University of Nebraska Medical Center 
				Omaha,NE 68198-5805
				
		Contact: pouletaxel@gmail.com OR jordan.rowley@unmc.edu