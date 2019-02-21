# SIP Version 1 run with java 8:	

	hic <hicFile> <chrSizeFile> <Output> <juicerToolsPath> [options]
	processed <Directory with processed data> <chrSizeFile> <Output> [options]
	chrSizeFile: path to the chromosome file size, with the same name of the chromosome than in the hic file
	-res: resolution in bases (default 5000 bases).
	-mat: matrix size in bins (default 2000 bins).
	-d: diagonal size in bins, remove the maxima found at this size (eg: a size of 2 at 5000 bases resolution removed all maxima detected at a distance inferior or equal to 10kb) (default 6 bins).
	-g: Gaussian filter: smooth the image to reduce the noise (default 2 for hic and 1 for hichip)
	-hichip: true or false (default false), use true if you are analyzing HiChIP data
	-factor: Multiple resolutions can be specified using (default -factor 2): 
		1: run only for the input res
		2: res and res*2
		3: res and res*5
        4: res, res*2 and res*5 
	-max: Maximum filter: increase the region of high intensity (default 2 for hic and 1 hichip)
	-min: Minimum filter: removed the isolated high value (default 2 for hic and 1 hichip)
	-sat: % of saturated pixel: enhance the contrast in the image (default 0.01 for hic and 0.5 for hichip)
	-t: Threshold for loops detection (default 2800 for hic and 1 for hichip)
	-nbZero: number of zero: number of pixel equal at zero allowed in the 24 neighborhood of the detected maxima (default parameter 6 for hic and 25 for hichip)
	-norm: <NONE/VC/VC_SQRT/KR> only for hic option (default KR)
	-del: true or false, delete tif files used for loops detection (default true)
	-h, --help print help.

	command line eg:
		java -jar SIP_HiC.jar processed inputDirectory pathToChromosome.size OutputDir .... paramaters
		java -jar SIP_HiC.jar hic inputDirectory pathToChromosome.size OutputDir juicer_tools.jar