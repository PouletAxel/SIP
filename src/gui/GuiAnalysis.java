package gui;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JTextPane;



/**
 * Class to construct graphical interface for the chromocenter analysis pipeline in batch
 * 
 * @author poulet axel
 *
 */

public class GuiAnalysis extends JFrame{
	/** */
	private static final long serialVersionUID = 1L;
	/** */
	private Container _container;
	/** */
	private JLabel _jlWorkDir;
	/** */
	private JButton _jbWorkDir = new JButton("Output directory");
	/** */
	private JButton _jbRawData = new JButton("Raw data");
	/** */
	private JButton _jbChrSize = new JButton("Chr size file");
	/** */
	private JTextField _jtfChrSize  =  new JTextField();
	/** */
	private JTextField _jtfWorkDir  =  new JTextField();
	/** */
	private JTextField _jtfRawData = new JTextField();
	/***/
	private JButton _jbStart = new JButton("Start");
	/** */
	private JButton _jbQuit = new JButton("Quit");
	/** */
	private JLabel _jlInputFileType;
	/** */
	private ButtonGroup _bGroupInputType = new ButtonGroup();
	/** */
	private JRadioButton _jrbHic = new JRadioButton("hic");
	/** */
	private JRadioButton _jrbProcessed = new JRadioButton("processed");
	/** */
	private ButtonGroup _bGroupInputData = new ButtonGroup();
	/** */
	private JRadioButton _jrbHiCData = new JRadioButton("hic");
	/** */
	private JRadioButton _jrbHichipData = new JRadioButton("HiChIP");
	/** */
    private String _juiceBoxTools;
    /** */
    private JTextField _jtfBoxTools = new JTextField();
    /** */
    private JButton _jbBoxTools = new JButton("JuiceBox Tools");
    /** */
    private JLabel _jlNorm;
    /** */
    private ButtonGroup _bNorm = new ButtonGroup();
    /** */
    private JRadioButton _jrbNone = new JRadioButton("NONE");
    /** */
    private JRadioButton _jrbKR = new JRadioButton("KR");
    /** */
    private JRadioButton _jrbVC = new JRadioButton("VC");
    /** */
    private JRadioButton _jrbVC_sqrt = new JRadioButton("VC SQRT");
    /** */    
    private JLabel _imgParam;
    /** */
	private JFormattedTextField _matrixSize = new JFormattedTextField(Number.class);
	/** */
	private JFormattedTextField _resolution = new JFormattedTextField(Number.class);
	/** */
	private JFormattedTextField _diagSize =  new JFormattedTextField(Number.class);
	/** */
    private JLabel _jlMatrixSize;
    /** */
    private JLabel _jlResolution;
    /** */
    private JLabel _jldiagSize;
    /** */
    private JFormattedTextField _gaussian = new JFormattedTextField(Number.class);
    /** */
    private JFormattedTextField _min = new JFormattedTextField(Number.class);
    /** */
    private JFormattedTextField _max =  new JFormattedTextField(Number.class);
    /** */
    private JFormattedTextField _enhanceContrast =  new JFormattedTextField(Number.class);
    /** */
    private JFormattedTextField _nbZero =  new JFormattedTextField(Number.class);
    /** */
    private JFormattedTextField _noiseTolerance =  new JFormattedTextField(Number.class);
    /** */
    private JLabel _jlGaussian;
    /** */
    private JLabel _jlMin;
    /** */
    private JLabel _jlMax;
    /** */
    private JLabel _jlEnhance;
    /** */
    private JLabel _jlNbZero;
    /** */
    private JLabel _jlNT;
    /** */
    private boolean _start = false;
 
   
    
    /** */
    private JCheckBox _checkbox2 = new JCheckBox("resolution*2",true);
    /** */
    private JCheckBox _checkbox5 = new JCheckBox("resolution*5",false);
   
    private JCheckBox _checkboxDeleteTif = new JCheckBox("Delete tif files",true);
   
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args){
		GuiAnalysis gui = new GuiAnalysis();
		gui.setLocationRelativeTo(null);
    } 
	
    
    /**
     * Architecture of the graphical windows
     *
     */
    
	public GuiAnalysis(){
		///////////////////////////////////////////// Global parameter of the JFram and def of the gridBaglayout
		this.setTitle("SIP_HiC ");
		this.setSize(550, 780);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setBackground(Color.LIGHT_GRAY);
		_container = getContentPane();
		GridBagLayout gridBagLayout = new GridBagLayout();
	   	gridBagLayout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.1};
	   	gridBagLayout.rowHeights = new int[] {17, 300, 124, 7};
	   	gridBagLayout.columnWeights = new double[] {0.0, 0.0, 0.0, 0.1};
	   	gridBagLayout.columnWidths = new int[] {300, 120, 72, 20};
	   	_container.setLayout (gridBagLayout);
	   	
	   	//////////////////////////////////////// First case of the grid bag layout
	   	
	    /////////////////////// group of radio button to choose the input type file 
	   	_jlInputFileType = new JLabel();
		_jlInputFileType.setFont(new java.awt.Font("arial",1,12));
	   	_jlInputFileType.setText("Program choice:");
	   	_container.add(_jlInputFileType, new GridBagConstraints
	   			(
	   					0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   					GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0
	   		    )
	   	);
	   	
	   	_bGroupInputType.add(_jrbProcessed);
	 	_bGroupInputType.add(_jrbHic);
	 	
		_jrbHic.setFont(new java.awt.Font("arial",2,11));
		_jrbProcessed.setFont(new java.awt.Font("arial",2,11));
		_container.add(_jrbHic,new GridBagConstraints
				(
						0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE, new Insets(20, 20, 0, 0), 0, 0
				)
		);
		_container.add(_jrbProcessed,new GridBagConstraints
				(
						0, 1, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE,new Insets(20, 100, 0, 0), 0, 0
				)
		);
		
		_jrbHic.setSelected(true);
		
/////////////////////////////////////
		_jlInputFileType = new JLabel();
		_jlInputFileType.setFont(new java.awt.Font("arial",1,12));
	   	_jlInputFileType.setText("Data type:");
	   	_container.add(_jlInputFileType, new GridBagConstraints
	   			(
	   					0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   					GridBagConstraints.NONE, new Insets(0, 250, 0, 0), 0, 0
	   		    )
	   	);
	   	
	   	this._bGroupInputData.add(this._jrbHiCData);
	   	_bGroupInputData.add(this._jrbHichipData);
	 	
	 	_jrbHiCData.setFont(new java.awt.Font("arial",2,11));
	 	_jrbHichipData.setFont(new java.awt.Font("arial",2,11));
		_container.add(_jrbHiCData,new GridBagConstraints
				(
						0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE, new Insets(20, 280, 0, 0), 0, 0
				)
		);
		_container.add(_jrbHichipData,new GridBagConstraints
				(
						0, 1, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE,new Insets(20, 380, 0, 0), 0, 0
				)
		);
		
		_jrbHiCData.setSelected(true);
//////////////////////////////////////////////
		
		
		_jbBoxTools.setPreferredSize(new java.awt.Dimension(120, 21));
		_jbBoxTools.setFont(new java.awt.Font("Albertus",2,10));
	   	_container.add(_jbBoxTools, new GridBagConstraints
	   			(
	   					0, 1, 0, 0, 0.0, 0.0,GridBagConstraints.NORTHWEST,
	   					GridBagConstraints.NONE, new Insets(50, 10, 0, 0), 0, 0
	   			)
	   	);
	  
	   	_jtfBoxTools.setPreferredSize(new java.awt.Dimension(280, 21));
	   	_jtfBoxTools.setFont(new java.awt.Font("Albertus",2,10));	
		_container.add( _jtfBoxTools, new GridBagConstraints
				(
						0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE, new Insets(50, 160, 0, 0), 0, 0
				)
		);
		
		_jlNorm = new JLabel();
		_jlNorm.setText("Normalisation to dump data:");
		_jlNorm.setFont(new java.awt.Font("arial",2,11));
	   	_container.add(_jlNorm, new GridBagConstraints
	   			(
	   					0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   					GridBagConstraints.NONE, new Insets(83, 10, 0, 0), 0, 0
	   		    )
	   	);
	   	
	   	_bNorm.add(_jrbNone);
	   	_bNorm.add(_jrbKR);
	   	_bNorm.add(_jrbVC);
	   	_bNorm.add(_jrbVC_sqrt);
	   	
	   	_jrbNone.setFont(new java.awt.Font("arial",2,11));
	   	_jrbKR.setFont(new java.awt.Font("arial",2,11));
	   	_jrbVC.setFont(new java.awt.Font("arial",2,11));
	   	_jrbVC_sqrt.setFont(new java.awt.Font("arial",2,11));
		
	   	_container.add(_jrbNone,new GridBagConstraints
				(
						0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE, new Insets(80, 190, 0, 0), 0, 0
				)
		);
		_container.add(_jrbKR,new GridBagConstraints
				(
						0, 1, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE,new Insets(80, 250, 0, 0), 0, 0
				)
		);
		_container.add(_jrbVC, new GridBagConstraints
				(
						0,1, 0, 0, 0.0, 0.0,GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE, new Insets(80, 295, 0, 0), 0, 0
				)
		);
		_container.add(_jrbVC_sqrt, new GridBagConstraints
				(
						0,1, 0, 0, 0.0, 0.0,GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE, new Insets(80, 340, 0, 0), 0, 0
				)
		);
		_jrbKR.setSelected(true);
		
	   	_jlWorkDir = new JLabel();
		_jlWorkDir.setText("Work directory and data directory choice : ");
		_jlWorkDir.setFont(new java.awt.Font("arial",1,12));
	   	_container.add( _jlWorkDir, new GridBagConstraints
	   		(
	   			0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   			GridBagConstraints.NONE, new Insets(105, 10, 0, 0), 0, 0
	   		)
	   	);
	   	
	   
	   	JTextPane jTextPane = new JTextPane();
	   	jTextPane.setContentType("text/html");
	   	jTextPane.setBackground(Color.LIGHT_GRAY);
	   	jTextPane.setText(
	   			"<html> <font face=arial><font size=-1><center> The data directory must contain:"
	   			+ "<strong><br/>OR</strong>"
	   			+ "<br/>hic file"
	   			+ "<strong><br/>OR</strong>"
	   			+ "<br/>data processed by SIP hic" );
	   	jTextPane.setEditable(false);
	   	

	   	
	   	_container.add( jTextPane,new GridBagConstraints
	   			(
	   					0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   					GridBagConstraints.NONE, new Insets(130, 100, 0, 0), 0, 0
	   			)	
	   	);
	   	
	   	///////////////////// Rawdata and work dir button and text.	   	
	   	
	   	_jbRawData.setPreferredSize(new java.awt.Dimension(120, 21));
	   	_jbRawData.setFont(new java.awt.Font("arial",2,10));
	   	_container.add ( _jbRawData, new GridBagConstraints
	   			(
	   					0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, 
	   					GridBagConstraints.NONE, new Insets(250, 10, 0, 0), 0, 0
	   			)
	   	);
	   	
	   	_jtfRawData.setPreferredSize(new java.awt.Dimension(280, 21));
		_jtfRawData.setFont(new java.awt.Font("arial",2,10));
		_container.add(_jtfRawData, new GridBagConstraints
				(
						0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE, new Insets(250, 160, 0, 0),0, 0
				)
		);
		
		_jbWorkDir.setPreferredSize(new java.awt.Dimension(120, 21));
	   	_jbWorkDir.setFont(new java.awt.Font("arial",2,10));
	   	_container.add(_jbWorkDir, new GridBagConstraints
	   			(
	   					0, 1, 0, 0, 0.0, 0.0,GridBagConstraints.NORTHWEST,
	   					GridBagConstraints.NONE, new Insets(280, 10, 0, 0), 0, 0
	   			)
	   	);
	  
		_jtfWorkDir.setPreferredSize(new java.awt.Dimension(280, 21));
	   	_jtfWorkDir.setFont(new java.awt.Font("arial",2,10));	
		_container.add( _jtfWorkDir, new GridBagConstraints
				(
						0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE, new Insets(280, 160, 0, 0), 0, 0
				)
		);
		
		////////////////////////// second case
		_imgParam = new JLabel();
		_imgParam.setText("Matrix parameters:");
		_imgParam.setFont(new java.awt.Font("arial",1,12));
	   	_container.add (_imgParam, new GridBagConstraints
	   		(
	   			0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   			GridBagConstraints.NONE, new Insets(20, 10, 0, 0), 0, 0
	   	    )
	   	);
	   	  	
	   	_container.setLayout (gridBagLayout);
		_jlMatrixSize = new JLabel();
		_jlMatrixSize.setText("Matrix size (in bins):");
		_jlMatrixSize.setFont(new java.awt.Font("arial",2,11));
		_container.add( _jlMatrixSize, new GridBagConstraints
				(
				0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(50, 20, 0, 0), 0, 0
			)
		);
		
		_matrixSize.setText("2000");
		_matrixSize.setPreferredSize(new java.awt.Dimension(60, 21));
		_matrixSize.setFont(new java.awt.Font("arial",2,11));
		_container.add( _matrixSize, new GridBagConstraints
			(
				0, 2, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST, 
				GridBagConstraints.NONE, new Insets(50, 170, 0, 0), 0, 0
			)
		);
		
	 	_container.setLayout (gridBagLayout);
			_jldiagSize = new JLabel();
			_jldiagSize.setText("Diag size (in bins):");
			_jldiagSize.setFont(new java.awt.Font("arial",2,11));
			_container.add( _jldiagSize, new GridBagConstraints
					(
					0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE, new Insets(80, 20, 0, 0), 0, 0
				)
			);
			
			_diagSize.setText("5");
			_diagSize.setPreferredSize(new java.awt.Dimension(60, 21));
			_diagSize.setFont(new java.awt.Font("arial",2,11));
			_container.add( _diagSize, new GridBagConstraints
				(
					0, 2, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST, 
					GridBagConstraints.NONE, new Insets(80, 170, 0, 0), 0, 0
				)
			);
		
		
		_jlResolution = new JLabel();
		_jlResolution.setText("Resolution (in bases):");
		_jlResolution.setFont(new java.awt.Font("arial",2,11));
		_container.add( _jlResolution, new GridBagConstraints
			(
				0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(110, 20, 0, 0), 0, 0
			)
		);
		
		_resolution.setText("5000");
		_resolution.setPreferredSize(new java.awt.Dimension(60, 21));
		_resolution.setFont(new java.awt.Font("arial",2,11));
		_container.add( _resolution, new GridBagConstraints
			(
				0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(110, 170, 0, 0), 0, 0
			)
		);
		
		
		_imgParam = new JLabel();
		_imgParam.setText("<html> Multi resolution loop calling:<br><font size=-2>(default: resolution, resolution*2.</html> ");
		_container.add(_imgParam,new GridBagConstraints
				(
						0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE, new Insets(20, 250, 0, 0), 0, 0
				)
		);
		_checkbox2.setFont(new java.awt.Font("arial",2,12));
		_container.add(_checkbox2,new GridBagConstraints
				(
						0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE, new Insets(50, 300, 0, 0), 0, 0
				)
		);
		_checkbox5.setFont(new java.awt.Font("arial",2,12));
		_container.add(_checkbox5,new GridBagConstraints
				(
						0, 2, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE,new Insets(80, 300, 0, 0), 0, 0
				)
		);
			
		
		_jlInputFileType = new JLabel();
	   	_jlInputFileType.setText("Chromosome size file (same chr names than in .hic file):");
	   	_jlInputFileType.setFont(new java.awt.Font("arial",1,12));
	   	_container.add(_jlInputFileType, new GridBagConstraints
	   			(
	   					0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   					GridBagConstraints.NONE, new Insets(140, 10, 0, 0), 0, 0
	   		    )
	   	);
	   	
		_jbChrSize.setPreferredSize(new java.awt.Dimension(120, 21));
		_jbChrSize.setFont(new java.awt.Font("arial",2,11));
	   	_container.add ( _jbChrSize, new GridBagConstraints
	   			(
	   					0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, 
	   					GridBagConstraints.NONE, new Insets(170, 10, 0, 0), 0, 0
	   			)
	   	);
	   	
	   	this._jtfChrSize.setPreferredSize(new java.awt.Dimension(280, 21));
	   	_jtfChrSize.setFont(new java.awt.Font("arial",2,10));
		_container.add(_jtfChrSize, new GridBagConstraints
				(
						0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE, new Insets(170, 160, 0, 0),0, 0
				)
		);
		
		
		_jlInputFileType = new JLabel();
	   	_jlInputFileType.setText("Image processing parameters:");
	   	_jlInputFileType.setFont(new java.awt.Font("arial",1,12));
	   	_container.add(_jlInputFileType, new GridBagConstraints
	   			(
	   					0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   					GridBagConstraints.NONE, new Insets(200, 10, 0, 0), 0, 0
	   		    )
	   	);
		
	   	_container.setLayout (gridBagLayout);
		this._jlGaussian = new JLabel();
		_jlGaussian.setText("Gaussian filter:");
		_jlGaussian.setFont(new java.awt.Font("arial",2,11));
		_container.add( _jlGaussian, new GridBagConstraints
				(
				0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(230, 20, 0, 0), 0, 0
			)
		);
		
		this._gaussian.setText("1.5");
		_gaussian.setPreferredSize(new java.awt.Dimension(60, 21));
		_gaussian.setFont(new java.awt.Font("arial",2,11));
		_container.add( _gaussian, new GridBagConstraints
			(
				0, 2, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST, 
				GridBagConstraints.NONE, new Insets(230, 150, 0, 0), 0, 0
			)
		);
		
	 	_container.setLayout (gridBagLayout);
	
	 	this._jlNT = new JLabel();
		_jlNT.setText("Threshold for maxima detection:");
		_jlNT.setFont(new java.awt.Font("arial",2,11));
		_container.add( _jlNT, new GridBagConstraints
					(
					0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE, new Insets(230, 230, 0, 0), 0, 0
				)
			);
			
		this._noiseTolerance.setText("2800");
		_noiseTolerance.setPreferredSize(new java.awt.Dimension(60, 21));
		_noiseTolerance.setFont(new java.awt.Font("arial",2,11));
		_container.add( _noiseTolerance, new GridBagConstraints
				(
					0, 2, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST, 
					GridBagConstraints.NONE, new Insets(230, 430, 0, 0), 0, 0
				)
			);
		
	
		this._jlMax = new JLabel();
		_jlMax.setText("Maximum filter:");
		_jlMax.setFont(new java.awt.Font("arial",2,11));
		_container.add( _jlMax, new GridBagConstraints
					(
					0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE, new Insets(260, 20, 0, 0), 0, 0
				)
			);
			
		this._max.setText("2");
		_max.setPreferredSize(new java.awt.Dimension(60, 21));
		_max.setFont(new java.awt.Font("arial",2,11));
		_container.add( _max, new GridBagConstraints
				(
					0, 2, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST, 
					GridBagConstraints.NONE, new Insets(260, 150, 0, 0), 0, 0
				)
			);
	   	
		this._jlMin = new JLabel();
		_jlMin.setText("Minimum filter:");
		_jlMin.setFont(new java.awt.Font("arial",2,11));
		_container.add( _jlMin, new GridBagConstraints
					(
					0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE, new Insets(260, 230, 0, 0), 0, 0
				)
			);
			
		this._min.setText("2");
		_min.setPreferredSize(new java.awt.Dimension(60, 21));
		_container.add( _min, new GridBagConstraints
				(
					0, 2, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST, 
					GridBagConstraints.NONE, new Insets(260, 350, 0, 0), 0, 0
				)
			);
	   	
		
		this._jlEnhance = new JLabel();
		_jlEnhance.setText("% of satured pixel:");
		_jlEnhance.setFont(new java.awt.Font("arial",2,11));
		_container.add( _jlEnhance, new GridBagConstraints
					(
					0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE, new Insets(290, 20, 0, 0), 0, 0
				)
			);
			
		this._enhanceContrast.setText("0.01");
		_enhanceContrast.setPreferredSize(new java.awt.Dimension(60, 21));
		_enhanceContrast.setFont(new java.awt.Font("arial",2,11));
		_container.add( _enhanceContrast, new GridBagConstraints
				(
					0, 2, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST, 
					GridBagConstraints.NONE, new Insets(290, 150, 0, 0), 0, 0
				)
			);
	   	   	
		this._jlNbZero = new JLabel();
		_jlNbZero.setText("Number of zero allowed in the loop 24 neighboorhoods:");
		_jlNbZero.setFont(new java.awt.Font("arial",2,11));
		_container.add( _jlNbZero, new GridBagConstraints
					(
					0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE, new Insets(320, 20, 0, 0), 0, 0
				)
			);
			
		this._nbZero.setText("6");
		_nbZero.setPreferredSize(new java.awt.Dimension(60, 21));
		_nbZero.setFont(new java.awt.Font("arial",2,11));
		_container.add( _nbZero, new GridBagConstraints
				(
					0, 2, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST, 
					GridBagConstraints.NONE, new Insets(320, 350, 0, 0), 0, 0
				)
			);
		
		_checkboxDeleteTif.setFont(new java.awt.Font("arial",2,12));
		_container.add(_checkboxDeleteTif,new GridBagConstraints
				(
						0, 2, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE,new Insets(345, 20, 0, 0), 0, 0
				)
		);
	   	
		_jbStart.setPreferredSize(new java.awt.Dimension(120, 21));
	   	_container.add(_jbStart, new GridBagConstraints
	   		(
	   			0, 2, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,
	   			GridBagConstraints.NONE, new Insets(380, 140, 0,0), 0, 0
	   		)
	   	);
	   	
	   	
	   	_jbQuit.setPreferredSize(new java.awt.Dimension(120, 21));
		_container.add(_jbQuit,new GridBagConstraints
			(
				0, 2, 0, 0,  0.0, 0.0,GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,new Insets(380, 10, 0, 0), 0, 0
			)
		);
		
		RBHicListener hic = new RBHicListener(this);
	  	this._jrbHic.addActionListener(hic);
	  	this._jrbHiCData.addActionListener(hic);
	  	this._jrbHichipData.addActionListener(hic);
	  	this._jrbProcessed.addActionListener(hic);
	  	WorkDirectoryListener wdListener = new WorkDirectoryListener();
	  	_jbWorkDir.addActionListener(wdListener);
		FileListener chr = new FileListener(_jtfChrSize);
	  	_jbChrSize.addActionListener(chr);
	  	JuiceBoxListener juice = new JuiceBoxListener();
	  	_jbBoxTools.addActionListener(juice);
	  	RawDataDirectoryListener ddListener = new RawDataDirectoryListener(this,_jtfRawData);
	  	_jbRawData.addActionListener(ddListener);
	  	QuitListener quitListener = new QuitListener(this);
	   	_jbQuit.addActionListener(quitListener);
	   	StartListener startListener = new StartListener(this);
	   	_jbStart.addActionListener(startListener);	  
	   	this.setVisible(true);
	 }
	
	/**
	 * 
	 * @return
	 */
	public int getMatrixSize(){
		String x = _matrixSize.getText();
		return Integer.parseInt(x.replaceAll(",", "."));
	}
	
	/**
	 * 
	 * @return
	 */
	public int getResolution(){
		String x = _resolution.getText();
		return Integer.parseInt(x.replaceAll(",", ".")); 
	}
	
	/**
	 * 
	 * @return
	 */
	public double getMax(){
		String x = _max.getText();
		return Double.parseDouble(x.replaceAll(",", "."));
	}
	
	/**
	 * 
	 * @return
	 */
	public int getNbZero(){
		String x = _nbZero.getText();
		return Integer.parseInt(x.replaceAll(",", "."));
	}
	/**
	 * 
	 * @return
	 */
	public double getMin(){
		String x = _min.getText();
		return Double.parseDouble(x.replaceAll(",", "."));
	}
	
	/**
	 * 
	 * @return
	 */
	public double getEnhanceSignal(){
		String x = this._enhanceContrast.getText();
		return Double.parseDouble(x.replaceAll(",", "."));
	}
	
	/**
	 * 
	 * @return
	 */
	public int getFactorChoice(){
		if(_checkbox2.isSelected() && _checkbox5.isSelected())	return 4;
		else if( _checkbox5.isSelected()) return 3;
		else if( _checkbox2.isSelected()) return 2;
		else return 1;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getNoiseTolerance(){
		String x = this._noiseTolerance.getText();
		return Integer.parseInt(x.replaceAll(",", "."));
	}
	/**
	 * 
	 * @return
	 */
	public double getGaussian(){
		String x = _gaussian.getText();
		return Double.parseDouble(x.replaceAll(",", "."));
	}
	
	/**
	 * 
	 * @return
	 */
	public int getDiagSize(){
		String x = _diagSize.getText();
		return Integer.parseInt(x.replaceAll(",", "."));
	}
	
	/**
	 * 
	 * @return
	 */
	public String getOutputDir(){
		return _jtfWorkDir.getText();
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String getRawDataDir(){
		return _jtfRawData.getText();
	}
	
	/**
	 * 
	 * @return
	 */
	public String getChrSizeFile(){
		return _jtfChrSize.getText();
	}
	
	/**
	 * 
	 * @return
	 */
	public String getJuiceBox(){
		return _jtfBoxTools.getText();
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isStart(){
		return _start;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isProcessed(){
		return _jrbProcessed.isSelected();
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isHiChIP(){
		return this._jrbHichipData.isSelected();
	}
	/**
	 * 
	 * @return
	 */
	public boolean isHiCData(){
		return this._jrbHiCData.isSelected();
	}
	/**
	 * 
	 * @return
	 */
	public boolean isHic(){
		return _jrbHic.isSelected();
	}
	public boolean isDeletTif(){
		return _checkboxDeleteTif.isSelected();
	}
	/**
	 * 
	 * @return
	 */
	public boolean isVC_SQRT(){
		return _jrbVC_sqrt.isSelected();
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isVC(){
		return _jrbVC.isSelected();
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isNONE(){
		return _jrbNone.isSelected();
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isKR(){
		return _jrbKR.isSelected();
	}



	/********************************************************************************************************************************************
	  * 	Classes listener to interact with the several element of the window
	  */
	 /********************************************************************************************************************************************
	 /********************************************************************************************************************************************
	 /********************************************************************************************************************************************
	 /********************************************************************************************************************************************/
	

	/**
	 * 
	 * @author plop
	 *
	 */
	class RBHicListener implements ActionListener{
		/** */
		GuiAnalysis m_gui;
		/**
		 * 
		 * @param gui
		 */
		public  RBHicListener (GuiAnalysis gui){
			m_gui = gui;
		}
		
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent actionEvent){
			if(m_gui.isHiChIP()){
        		_gaussian.setText("1.5");
        		_enhanceContrast.setText("0.5");
        		_min.setText("1");
        		_max.setText("1");
        		_nbZero.setText("25");
        		_noiseTolerance.setText("1");
        		m_gui._jrbNone.setSelected(true);
        	}else if(m_gui.isHiCData()){
        		_gaussian.setText("1.5");
        		_enhanceContrast.setText("0.005");
        		_min.setText("2.0");
        		_max.setText("2.0");
        		_nbZero.setText("6");
        		_noiseTolerance.setText("2500");
        		m_gui._jrbKR.setSelected(true);
        	}
			if (m_gui.isHic()){
				m_gui._jrbHiCData.setEnabled(true);
				m_gui._jrbHichipData.setEnabled(true);
	        	m_gui._jrbVC_sqrt.setEnabled(true);
	        	m_gui._jrbVC.setEnabled(true);
	        	m_gui._jrbNone.setEnabled(true);
	        	m_gui._jrbKR.setEnabled(true);
	        	m_gui._jtfBoxTools.setEnabled(true);
	        	_jbBoxTools.setEnabled(true);
	        	m_gui._max.setEditable(true);
        		m_gui._min.setEditable(true);
        		m_gui._enhanceContrast.setEditable(true);
	        }
	        else if(m_gui.isProcessed()){
	        	m_gui._jrbHiCData.setEnabled(true);
				m_gui._jrbHichipData.setEnabled(true);
	        	m_gui._jrbVC_sqrt.setEnabled(false);
	        	m_gui._jrbVC.setEnabled(false);
	        	m_gui._jrbNone.setEnabled(false);
	        	m_gui._jrbKR.setEnabled(false);
	        	m_gui._jtfBoxTools.setEnabled(false);
	        	_jbBoxTools.setEnabled(false);
	        	m_gui._max.setEditable(true);
	        	m_gui._jlNbZero.setEnabled(true);
	        	m_gui._min.setEditable(true);
	        	m_gui._enhanceContrast.setEditable(true);
	        }
	    }
	}
	
	
	/**
	 * 
	 * @author plop
	 *
	 */
	class StartListener implements ActionListener {
		/** */
		GuiAnalysis m_gui;
		/**
		 * 
		 * @param gui
		 */
		public  StartListener (GuiAnalysis gui){
			m_gui = gui;
		}
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent actionEvent){
			if (_jtfWorkDir.getText().isEmpty() || _jtfRawData.getText().isEmpty() || m_gui._jtfChrSize.getText().isEmpty())
				JOptionPane.showMessageDialog
				(
					null,
					"You did not choose a output directory, the raw data, or chromosom size file",
					"Error",
					JOptionPane.ERROR_MESSAGE
				); 
			else if(m_gui.isHic() && (m_gui._jtfBoxTools.getText().isEmpty() ))
				JOptionPane.showMessageDialog
				(
					null,
					"You did not choose a Juicer_box_tools.jar path",
					"Error",
					JOptionPane.ERROR_MESSAGE
				);
			else if(m_gui._diagSize.getText().matches("[a-zA-Z]") || m_gui._enhanceContrast.getText().matches("[a-zA-Z]") ||
					m_gui._gaussian.getText().matches("[a-zA-Z]") || m_gui._matrixSize.getText().matches("[a-zA-Z]") ||
					m_gui._max.getText().matches("[a-zA-Z]") || m_gui._min.getText().matches("[a-zA-Z]") ||
					m_gui._resolution.getText().matches("[a-zA-Z]") || m_gui._nbZero.getText().matches("[a-zA-Z]") ||
					m_gui._noiseTolerance.getText().matches("[a-zA-Z]"))
					JOptionPane.showMessageDialog
					(
						null,
						"some alphabetic character detected in integer or float paramters",
						"Error",
						JOptionPane.ERROR_MESSAGE
					);	
			else{
				_start=true;
				m_gui.dispose();
			}
		}
	}
	
	/**
	 * 
	 * @author plop
	 *
	 */
	class QuitListener implements ActionListener {
		/** */
		GuiAnalysis m_gui;	
		/**
		 * 
		 * @param gui
		 */
		public  QuitListener (GuiAnalysis gui){
			m_gui = gui;
		}
		
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent actionEvent){
			m_gui.dispose();
			System.exit(0);
		}
	}
	
	
	
	/**
	 * 
	 * @author plop
	 *
	 */
	class WorkDirectoryListener implements ActionListener{
		/**
		 * 
		 */
		 public void actionPerformed(ActionEvent actionEvent)
		 {
			 setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			 JFileChooser jFileChooser = new JFileChooser();
			 jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			 int returnValue = jFileChooser.showOpenDialog(getParent());
			 if(returnValue == JFileChooser.APPROVE_OPTION)
			 {
				@SuppressWarnings("unused")
				String run = jFileChooser.getSelectedFile().getName();
				 String workDir = jFileChooser.getSelectedFile().getAbsolutePath();
				 _jtfWorkDir.setText(workDir);
			 }
			 setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		 }	
	 }
	
	/**
	 * 
	 * @author plop
	 *
	 */
	class RawDataDirectoryListener implements ActionListener{
		/** */
		GuiAnalysis m_gui;
		/** */
		JTextField m_jtf;
		/**
		 * 
		 * @param gui
		 * @param jtf
		 */
		public RawDataDirectoryListener(GuiAnalysis gui,JTextField jtf){
			m_gui = gui;
			m_jtf = jtf;
		}
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent actionEvent ){
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			JFileChooser jFileChooser = new JFileChooser();
			jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if(m_gui._jrbHic.isSelected())
				jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int returnValue = jFileChooser.showOpenDialog(getParent());
			if(returnValue == JFileChooser.APPROVE_OPTION){
				@SuppressWarnings("unused")
				String run = jFileChooser.getSelectedFile().getName();
				String text= jFileChooser.getSelectedFile().getAbsolutePath();
				m_jtf.setText(text);
			 }
			 setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		 }	
	 }
	
	/**
	 * 
	 * @author plop
	 *
	 */
	class FileListener implements ActionListener
	{
		/** */
		JTextField m_jtf;
		/**
		 * 
		 * @param jtf
		 */
		public FileListener(JTextField jtf){
			m_jtf = jtf;
		}
		
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent actionEvent){
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			JFileChooser jFileChooser = new JFileChooser();
			jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int returnValue = jFileChooser.showOpenDialog(getParent());
			if(returnValue == JFileChooser.APPROVE_OPTION){
				@SuppressWarnings("unused")
				String run = jFileChooser.getSelectedFile().getName();
				String chrSize = jFileChooser.getSelectedFile().getAbsolutePath();
				m_jtf.setText(chrSize);
			}
			setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}	
	}
	
	/**
	 * 
	 * @author plop
	 *
	 */
	class JuiceBoxListener implements ActionListener{
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent actionEvent){
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			JFileChooser jFileChooser = new JFileChooser();
			jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int returnValue = jFileChooser.showOpenDialog(getParent());
			if(returnValue == JFileChooser.APPROVE_OPTION){
				@SuppressWarnings("unused")
				String run = jFileChooser.getSelectedFile().getName();
				_juiceBoxTools = jFileChooser.getSelectedFile().getAbsolutePath();
				_jtfBoxTools.setText(_juiceBoxTools);
			}
			setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}	
	}
}