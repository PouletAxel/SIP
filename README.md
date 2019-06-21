#SIP Version 1 run with java 8

	SIP is implemented in java and includes achoice between command line options or	a graphical user
	 interface (gui) allowing for more general use. This method is intended as an alternative loop 
	 caller especially for difficult to identify loops and works inconjunction with juicebox .hic files.

	Usage:
	
		hic <hicFile> <chrSizeFile> <Output> <juicerToolsPath> [options]
		processed <Directory with processed data> <chrSizeFile> <Output> [options]
		
	Parameters:
	
		chrSizeFile: path to the chr size file, with the same name of the chr as in the hic file
		-res: resolution in bases (default 5000 bases)
		-mat: matrix size to use for each chunk of the chromosome (default 2000 bins)\n"
		-d: diagonal size in bins, remove the maxima found at this size (eg: a size of 2 at 5000 bases resolution
		removed all maxima detected at a distance inferior or equal to 10kb) (default 6 bins).
		-g: Gaussian filter: smoothing factor reduce noise during primary maxima detection (default 1.5 for hic 
		and 1 for hichip
			+"\t-cpu: Number of CPU used for SIP processing (default 1)\n"
			+"\t-hichip: true fo HiChIP or false for Hi-C (default false)\n"
			+"\t-factor: Multiple resolutions can be specified using: "
			+ "\t\t-factor 1: run only for the input res (default)\n"
			+ "\t\t-factor 2: res and res*2\n"
			+ "\t\t-factor 3: res and res*5\n"
			+ "\t\t-factor 4: res, res*2 and res*5 \n"
			+"\t-max: Maximum filter: increase the region of high intensity (default 2 for hic and 1 hichip)\n"
			+"\t-min: Minimum filter: removed the isolated high value (default 2 for hic and 1 hichip)\n"
			+"\t-sat: % of staturated pixel: enhance the contrast in the image (default 0.01 for hic and 0.5 for hichip)\n"
			+"\t-t Threshold for loops detection (default 2800 for hic and 1 for hichip)\n"
			+"\t-nbZero: number of zeros: number of pixels equal to zero that are allowed in the 24 pixels sourrounding 
			the detected maxima (default parameter 6 for hic and 25 for hichip)\n"
			+"\t-norm: <NONE/VC/VC_SQRT/KR> only for hic option (default KR)\n"
			+"\t-del: true or false, delete tif files used for loop detection (default true)\n"
			+"\t-fdr: Empirical FDR value for filtering based on random sites (default 0.01)\n"
			+"\t-h, --help print help\n"
			+"\ncommand line eg:\n"
			+"\tjava -jar SIP_HiC.jar processed inputDirectory pathToChromosome.size OutputDir .... paramaters\n"
			+"\tjava -jar SIP_HiC.jar hic inputDirectory pathToChromosome.size OutputDir juicer_tools.jar\n"
			+ "\nAuthors:\n"
			+ "\tAxel Poulet\n"
			+ "\t\tDepartment of Biology, Emory University, 1510 Clifton Rd. NE, Atlanta, GA 30322, USA.\n"
			+ "\t\tDepartment of Molecular, Cellular  and Developmental Biology Yale University 165 Prospect\n"
			+ "\t\tSt New Haven, CT 06511, USA\n"
			+ "\n\tM. Jordan Rowley\n"
			+ "\t\tDepartment of Biology, Emory University, 1510 Clifton Rd. NE, Atlanta, GA 30322, USA.\n"
			+ "\nContact: pouletaxel@gmail.com OR michael.j.rowley@emory.edu");