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



/**
 * GUI for SIP program
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
	private JButton _jbWorkDir = new JButton("Output directory");
	/** */
	private JButton _jbRawData = new JButton("Data (.hic or SIP)");
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
	private ButtonGroup _bGroupInputType = new ButtonGroup();
	/** */
	private JRadioButton _jrbHic = new JRadioButton("hic");
	/** */
	private JCheckBox  _jCbIsDroso = new JCheckBox("Is Droso");
	/** */
	private JRadioButton _jrbProcessed = new JRadioButton("processed");
	/** */
	//private ButtonGroup _bGroupInputData = new ButtonGroup();
	/** */
	private JRadioButton _jrbHiCData = new JRadioButton("hic");
	/** */
	private JRadioButton _jrbHichipData = new JRadioButton("HiChIP");
    /** */
    private JTextField _jtfBoxTools = new JTextField();
    /** */
    private JButton _jbBoxTools = new JButton("Juicer Tools");
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
	private JFormattedTextField _jtfMatrixSize = new JFormattedTextField(Number.class);
	/** */
	private JFormattedTextField _jtfResolution = new JFormattedTextField(Number.class);
	/** */
	private JFormattedTextField _jtfDiagSize =  new JFormattedTextField(Number.class);
    /** */
    private JFormattedTextField _jtfGaussian = new JFormattedTextField(Number.class);
    /** */
    private JFormattedTextField _jtfMin = new JFormattedTextField(Number.class);
    /** */
    private JFormattedTextField _jtfMax =  new JFormattedTextField(Number.class);
    /** */
    private JFormattedTextField _jtfEnhanceContrast =  new JFormattedTextField(Number.class);
    /** */
    private JFormattedTextField _jtfNbZero =  new JFormattedTextField(Number.class);
    /** */
    private JFormattedTextField _jtfNoiseTolerance =  new JFormattedTextField(Number.class);
    /** */
    private boolean _start = false;
    /** */
    private JFormattedTextField _jtfCpu = new JFormattedTextField(Number.class);
    /** */
    private JCheckBox _checkbox2 = new JCheckBox("resolution*2",false);
    /** */
    private JCheckBox _checkbox5 = new JCheckBox("resolution*5",false);
   
    private JCheckBox _checkboxDeleteTif = new JCheckBox("Delete tif files",true);
    /**    */
    private JCheckBox _checkboxIsAccurate = new JCheckBox("Sacrifice speed for increased accuracy ",true);
    /** */
    private JFormattedTextField _jtfFdr =  new JFormattedTextField(Number.class);
	
    
    /**
	 * gui main
	 * 
	 * @param args
	 */
	public static void main(String[] args){
		GuiAnalysis gui = new GuiAnalysis();
		gui.setLocationRelativeTo(null);
    } 
	
    
    /**
     * GUI Architecture
     *
     */
    
	public GuiAnalysis(){
		///////////////////////////////////////////// Global parameter of the JFram and def of the gridBaglayout
		this.setTitle("SIP_HiC ");
		this.setSize(550, 750);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setLocationByPlatform(true);
		this.setBackground(Color.LIGHT_GRAY);
		
		
		this._container = getContentPane();
		GridBagLayout gridBagLayout = new GridBagLayout();
	   	gridBagLayout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.1};
	   	gridBagLayout.rowHeights = new int[] {17, 270, 124, 7};
	   	gridBagLayout.columnWeights = new double[] {0.0, 0.0, 0.0, 0.1};
	   	gridBagLayout.columnWidths = new int[] {270, 120, 72, 20};
	   	this._container.setLayout (gridBagLayout);
	   	
	   	//////////////////////////////////////// First case of the grid bag layout
	   	
	    /////////////////////// group of radio button to choose the input type file 
	   	JLabel label= new JLabel();
	   	label.setFont(new java.awt.Font("arial",1,12));
	   	label.setText("Program choice:");
	   	this._container.add(label, new GridBagConstraints(
	   		0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   		GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0
	   	));
	   	
	   	this._bGroupInputType.add(this._jrbProcessed);
	   	this._bGroupInputType.add(this._jrbHic);
	 	
	   	this._jrbHic.setFont(new java.awt.Font("arial",2,11));
	   	this._jrbProcessed.setFont(new java.awt.Font("arial",2,11));
	   	this._container.add(this._jrbHic,new GridBagConstraints(
			0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
			GridBagConstraints.NONE, new Insets(20, 20, 0, 0), 0, 0
		));
	   	this._container.add(this._jrbProcessed,new GridBagConstraints(
			0, 1, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,
			GridBagConstraints.NONE,new Insets(20, 100, 0, 0), 0, 0
		));
		
	   	this._jrbHic.setSelected(true);
		
/////////////////////////////////////
		/*label = new JLabel();
		label.setFont(new java.awt.Font("arial",1,12));
		label.setText("Data type:");
		this._container.add(label, new GridBagConstraints(
	   		0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   		GridBagConstraints.NONE, new Insets(0, 250, 0, 0), 0, 0
	   	));
	   	
	   	this._bGroupInputData.add(this._jrbHiCData);
	   	this._bGroupInputData.add(this._jrbHichipData);
	   	this._jrbHiCData.setFont(new java.awt.Font("arial",2,11));
	   	this._jrbHichipData.setFont(new java.awt.Font("arial",2,11));
	   	this._container.add(this._jrbHiCData,new GridBagConstraints(
			0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
			GridBagConstraints.NONE, new Insets(20, 280, 0, 0), 0, 0
		));
	   	this._container.add(this._jrbHichipData,new GridBagConstraints(
			0, 1, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,
			GridBagConstraints.NONE,new Insets(20, 380, 0, 0), 0, 0
		));
	   	this._jrbHiCData.setSelected(true);*/
//////////////////////////////////////////////
			
	   	this._jbBoxTools.setPreferredSize(new java.awt.Dimension(120, 21));
	   	this._jbBoxTools.setFont(new java.awt.Font("Albertus",2,10));
	   	this._container.add(this._jbBoxTools, new GridBagConstraints(
	   		0, 1, 0, 0, 0.0, 0.0,GridBagConstraints.NORTHWEST,
	   		GridBagConstraints.NONE, new Insets(50, 20, 0, 0), 0, 0
	   	));
	  
	   	this._jtfBoxTools.setPreferredSize(new java.awt.Dimension(280, 21));
	   	this._jtfBoxTools.setFont(new java.awt.Font("Albertus",2,10));	
	   	this._container.add( this._jtfBoxTools, new GridBagConstraints(
			0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
			GridBagConstraints.NONE, new Insets(50, 160, 0, 0), 0, 0
		));
/////////////////////////////////////////////////////		
		label = new JLabel();
		label.setText("Normalization scheme (prefers KR):");
		label.setFont(new java.awt.Font("arial",2,11));
		this._container.add(label, new GridBagConstraints(
	   		0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   		GridBagConstraints.NONE, new Insets(83, 20, 0, 0), 0, 0
	   	));
	   	
		this._bNorm.add(this._jrbNone);
		this._bNorm.add(this._jrbKR);
		this._bNorm.add(this._jrbVC);
		this._bNorm.add(this._jrbVC_sqrt);
		this._jrbNone.setFont(new java.awt.Font("arial",2,11));
		this._jrbKR.setFont(new java.awt.Font("arial",2,11));
		this._jrbVC.setFont(new java.awt.Font("arial",2,11));
		this._jrbVC_sqrt.setFont(new java.awt.Font("arial",2,11));
		this._container.add(this._jrbNone,new GridBagConstraints(
	   		0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
			GridBagConstraints.NONE, new Insets(80, 220, 0, 0), 0, 0
		));
		this._container.add(this._jrbKR,new GridBagConstraints(
	   		0, 1, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,
			GridBagConstraints.NONE,new Insets(80, 290, 0, 0), 0, 0
		));
		
		this._container.add(this._jrbVC, new GridBagConstraints(
			0,1, 0, 0, 0.0, 0.0,GridBagConstraints.NORTHWEST,
			GridBagConstraints.NONE, new Insets(80, 340, 0, 0), 0, 0
		));
	   	
		this._container.add(this._jrbVC_sqrt, new GridBagConstraints(
			0,1, 0, 0, 0.0, 0.0,GridBagConstraints.NORTHWEST,
			GridBagConstraints.NONE, new Insets(80, 390, 0, 0), 0, 0
		));
		this._jrbKR.setSelected(true);
		
/////////////////////////////////////////////////////////////////////////		
		label = new JLabel();
		label.setText("Data and Output directories : ");
		label.setFont(new java.awt.Font("arial",1,12));
		this._container.add( label, new GridBagConstraints(
	   		0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   		GridBagConstraints.NONE, new Insets(105, 10, 0, 0), 0, 0
	   	));
	   	
		this._jbRawData.setPreferredSize(new java.awt.Dimension(120, 21));
		this._jbRawData.setFont(new java.awt.Font("arial",2,10));
		this._container.add ( this._jbRawData, new GridBagConstraints(
	   		0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, 
	   		GridBagConstraints.NONE, new Insets(130, 20, 0, 0), 0, 0
	   	));
	   	
		this._jtfRawData.setPreferredSize(new java.awt.Dimension(280, 21));
		this._jtfRawData.setFont(new java.awt.Font("arial",2,10));
		this._container.add(this._jtfRawData, new GridBagConstraints(
			0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
			GridBagConstraints.NONE, new Insets(130, 160, 0, 0),0, 0
		));
		
		this._jbWorkDir.setPreferredSize(new java.awt.Dimension(120, 21));
		this._jbWorkDir.setFont(new java.awt.Font("arial",2,10));
		this._container.add(this._jbWorkDir, new GridBagConstraints(
	   		0, 1, 0, 0, 0.0, 0.0,GridBagConstraints.NORTHWEST,
	   		GridBagConstraints.NONE, new Insets(160, 20, 0, 0), 0, 0
	   	));
	  
		this._jtfWorkDir.setPreferredSize(new java.awt.Dimension(280, 21));
		this._jtfWorkDir.setFont(new java.awt.Font("arial",2,10));	
		this._container.add( this._jtfWorkDir, new GridBagConstraints(
			0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
			GridBagConstraints.NONE, new Insets(160, 160, 0, 0), 0, 0
		));
		
		//////////////////////////////////////
		label = new JLabel();
		label.setText("Matrix parameters:");
		label.setFont(new java.awt.Font("arial",1,12));
		this._container.add (label, new GridBagConstraints(
	   		0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   		GridBagConstraints.NONE, new Insets(190, 10, 0, 0), 0, 0
	   	));
		this._container.setLayout (gridBagLayout);
	   	label = new JLabel();
	   	label.setText("Matrix size (in bins):");
	   	label.setFont(new java.awt.Font("arial",2,11));
	   	this._container.add( label, new GridBagConstraints(
			0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
			GridBagConstraints.NONE, new Insets(220, 20, 0, 0), 0, 0
		));
	   	this._jtfMatrixSize.setText("2000");
	   	this._jtfMatrixSize.setPreferredSize(new java.awt.Dimension(60, 21));
	   	this._jtfMatrixSize.setFont(new java.awt.Font("arial",2,11));
	   	this._container.add( this._jtfMatrixSize, new GridBagConstraints(
			0, 1, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST, 
			GridBagConstraints.NONE, new Insets(217, 170, 0, 0), 0, 0
		));
	   	this._container.setLayout (gridBagLayout);
	   	label = new JLabel();
	 	label.setText("Diag size (in bins):");
	 	label.setFont(new java.awt.Font("arial",2,11));
	 	this._container.add(label, new GridBagConstraints(
			0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
			GridBagConstraints.NONE, new Insets(250, 20, 0, 0), 0, 0
		));
	 	this._jtfDiagSize.setText("5");
	 	this._jtfDiagSize.setPreferredSize(new java.awt.Dimension(60, 21));
	 	this._jtfDiagSize.setFont(new java.awt.Font("arial",2,11));
	 	this._container.add( this._jtfDiagSize, new GridBagConstraints(
			0, 1, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST, 
			GridBagConstraints.NONE, new Insets(247, 170, 0, 0), 0, 0
		));
		
		label = new JLabel();
		label.setText("Resolution (in bases):");
		label.setFont(new java.awt.Font("arial",2,11));
		this._container.add( label, new GridBagConstraints(
			0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
			GridBagConstraints.NONE, new Insets(10, 20, 0, 0), 0, 0
		));
		
		this._jtfResolution.setText("5000");
		this._jtfResolution.setPreferredSize(new java.awt.Dimension(60, 21));
		this._jtfResolution.setFont(new java.awt.Font("arial",2,11));
		this._container.add( this._jtfResolution, new GridBagConstraints(
			0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
			GridBagConstraints.NONE, new Insets(7, 170, 0, 0), 0, 0
		));
		
		label = new JLabel();
		label.setText("<html> Multi resolution loop calling:</html>");
		this._container.add(label,new GridBagConstraints(
			0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
			GridBagConstraints.NONE, new Insets(190, 250, 0, 0), 0, 0
		));
		this._checkbox2.setFont(new java.awt.Font("arial",2,12));
		this._container.add(this._checkbox2,new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(210, 300, 0, 0), 0, 0
		));
		this._checkbox5.setFont(new java.awt.Font("arial",2,12));
		this._container.add(this._checkbox5,new GridBagConstraints(
			0, 1, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,
			GridBagConstraints.NONE,new Insets(240, 300, 0, 0), 0, 0
		));
		
		////////////////////////////////////////////////////////////////////////////////
		
		label = new JLabel();
		label.setText("Chromosome size file (same chr names as in .hic file):");
		label.setFont(new java.awt.Font("arial",1,12));
		this._container.add(label, new GridBagConstraints(
	   		0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   		GridBagConstraints.NONE, new Insets(40, 10, 0, 0), 0, 0
	   	));
	 
		this._jbChrSize.setPreferredSize(new java.awt.Dimension(120, 21));
		this._jbChrSize.setFont(new java.awt.Font("arial",2,11));
		this._container.add ( this._jbChrSize, new GridBagConstraints(
	   		0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, 
	   		GridBagConstraints.NONE, new Insets(70, 20, 0, 0), 0, 0
	   	));
	   	
		this._jtfChrSize.setPreferredSize(new java.awt.Dimension(280, 21));
		this._jtfChrSize.setFont(new java.awt.Font("arial",2,10));
		this._container.add(this._jtfChrSize, new GridBagConstraints(
			0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
			GridBagConstraints.NONE, new Insets(70, 160, 0, 0),0, 0
		));
		/////////////////////////////////////////////////////////////////////////////////////
		label = new JLabel();
		label.setText("Image processing parameters:");
		label.setFont(new java.awt.Font("arial",1,12));
		this._container.add(label, new GridBagConstraints(
	   		0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   		GridBagConstraints.NONE, new Insets(100, 10, 0, 0), 0, 0
	   	));
		
		this._container.setLayout (gridBagLayout);
		label = new JLabel();
		label.setText("Gaussian filter:");
		label.setFont(new java.awt.Font("arial",2,11));
		this._container.add( label, new GridBagConstraints(
			0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
			GridBagConstraints.NONE, new Insets(130, 20, 0, 0), 0, 0
		));
		
		this._jtfGaussian.setText("1.5");
		this._jtfGaussian.setPreferredSize(new java.awt.Dimension(60, 21));
		this._jtfGaussian.setFont(new java.awt.Font("arial",2,11));
		this._container.add( this._jtfGaussian, new GridBagConstraints(
			0, 2, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST, 
			GridBagConstraints.NONE, new Insets(127, 150, 0, 0), 0, 0
		));
		
		this._container.setLayout (gridBagLayout);
	 	label= new JLabel();
	 	label.setText("Threshold for maxima detection:");
	 	label.setFont(new java.awt.Font("arial",2,11));
	 	this._container.add( label, new GridBagConstraints(
			0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
			GridBagConstraints.NONE, new Insets(130, 230, 0, 0), 0, 0
		));
			
	 	this._jtfNoiseTolerance.setText("2800");
	 	this._jtfNoiseTolerance.setPreferredSize(new java.awt.Dimension(60, 21));
	 	this._jtfNoiseTolerance.setFont(new java.awt.Font("arial",2,11));
	 	this._container.add( this._jtfNoiseTolerance, new GridBagConstraints(
			0, 2, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST, 
			GridBagConstraints.NONE, new Insets(127, 430, 0, 0), 0, 0
		));
		
	
		label = new JLabel();
		label.setText("Maximum filter:");
		label.setFont(new java.awt.Font("arial",2,11));
		this._container.add( label, new GridBagConstraints(
			0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
			GridBagConstraints.NONE, new Insets(160, 20, 0, 0), 0, 0
		));
			
		this._jtfMax.setText("2");
		this._jtfMax.setPreferredSize(new java.awt.Dimension(60, 21));
		this._jtfMax.setFont(new java.awt.Font("arial",2,11));
		this._container.add( this._jtfMax, new GridBagConstraints(
			0, 2, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST, 
			GridBagConstraints.NONE, new Insets(157, 150, 0, 0), 0, 0
		));
	   	
		label = new JLabel();
		label.setText("Minimum filter:");
		label.setFont(new java.awt.Font("arial",2,11));
		this._container.add( label, new GridBagConstraints(
			0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
			GridBagConstraints.NONE, new Insets(160, 230, 0, 0), 0, 0
		));
			
		this._jtfMin.setText("2");
		this._jtfMin.setPreferredSize(new java.awt.Dimension(60, 21));
		this._container.add( this._jtfMin, new GridBagConstraints(
			0, 2, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST, 
			GridBagConstraints.NONE, new Insets(157, 350, 0, 0), 0, 0
		));
	   	
		label = new JLabel();
		label.setText("% of satured pixel:");
		label.setFont(new java.awt.Font("arial",2,11));
		this._container.add( label, new GridBagConstraints(
			0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
			GridBagConstraints.NONE, new Insets(190, 20, 0, 0), 0, 0
		));
			
		this._jtfEnhanceContrast.setText("0.01");
		this._jtfEnhanceContrast.setPreferredSize(new java.awt.Dimension(60, 21));
		this._jtfEnhanceContrast.setFont(new java.awt.Font("arial",2,11));
		this._container.add( this._jtfEnhanceContrast, new GridBagConstraints(
			0, 2, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST, 
			GridBagConstraints.NONE, new Insets(187, 150, 0, 0), 0, 0
		));
	   	
		label = new JLabel();
		label.setText("Empirical FDR:");
		label.setFont(new java.awt.Font("arial",2,11));
		this._container.add( label, new GridBagConstraints(
			0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
			GridBagConstraints.NONE, new Insets(190, 230, 0, 0), 0, 0
		));
		
		this._jtfFdr.setText("0.01");
		this._jtfFdr.setPreferredSize(new java.awt.Dimension(60, 21));
		this._jtfFdr.setFont(new java.awt.Font("arial",2,11));
		this._container.add( this._jtfFdr, new GridBagConstraints(
			0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
			GridBagConstraints.NONE, new Insets(187, 350, 0, 0), 0, 0
		));		
		
		label = new JLabel();
		label.setText("Number of zeros allowed in the 24 surrounding pixels:");
		label.setFont(new java.awt.Font("arial",2,11));
		this._container.add( label, new GridBagConstraints(
			0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
			GridBagConstraints.NONE, new Insets(220, 20, 0, 0), 0, 0
		));
			
		this._jtfNbZero.setText("6");
		this._jtfNbZero.setPreferredSize(new java.awt.Dimension(60, 21));
		this._jtfNbZero.setFont(new java.awt.Font("arial",2,11));
		this._container.add( this._jtfNbZero, new GridBagConstraints(
			0, 2, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST, 
			GridBagConstraints.NONE, new Insets(217, 350, 0, 0), 0, 0
		));
		
		label = new JLabel();
		label.setText("If is droso or like droso HiC map:");
		label.setFont(new java.awt.Font("arial",1,12));
		this._container.add(label, new GridBagConstraints(
	   		0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   		GridBagConstraints.NONE, new Insets(250, 10, 0, 0), 0, 0
	   	));
		
		
		this._jCbIsDroso.setFont(new java.awt.Font("arial",2,11));
		this._container.add(this._jCbIsDroso,new GridBagConstraints(
			0, 2, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,
			GridBagConstraints.NONE,new Insets(268, 20, 0, 0), 0, 0
		));
		
		this._checkboxDeleteTif.setFont(new java.awt.Font("arial",1,12));
		this._container.add(this._checkboxDeleteTif,new GridBagConstraints(
			0, 2, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,
			GridBagConstraints.NONE,new Insets(330, 10, 0, 0), 0, 0
		));
		
		this._checkboxIsAccurate.setFont(new java.awt.Font("arial",1,12));
		this._container.add(this._checkboxIsAccurate,new GridBagConstraints(
			0, 2, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,
			GridBagConstraints.NONE,new Insets(298, 230, 0, 0), 0, 0
		));
		
		label = new JLabel();
		label.setText("Number of CPU:");
		label.setFont(new java.awt.Font("arial",1,12));
		this._container.add( label, new GridBagConstraints(
			0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
			GridBagConstraints.NONE, new Insets(300, 10, 0, 0), 0, 0
		));
			
		this._jtfCpu.setText("1");
		this._jtfCpu.setPreferredSize(new java.awt.Dimension(60, 21));
		this._container.add( this._jtfCpu, new GridBagConstraints(
			0, 2, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST, 
			GridBagConstraints.NONE, new Insets(298, 140, 0, 0), 0, 0
		));
	   	
		this._jbStart.setPreferredSize(new java.awt.Dimension(120, 21));
		this._container.add(this._jbStart, new GridBagConstraints(
	   		0, 2, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,
	   		GridBagConstraints.NONE, new Insets(365, 140, 0,0), 0, 0
	   	));
	   	
		this._jbQuit.setPreferredSize(new java.awt.Dimension(120, 21));
		this._container.add(this._jbQuit,new GridBagConstraints(
			0, 2, 0, 0,  0.0, 0.0,GridBagConstraints.NORTHWEST,
			GridBagConstraints.NONE,new Insets(365, 10, 0, 0), 0, 0
		));
		
		RBHicListener hic = new RBHicListener(this);
	  	this._jrbHic.addActionListener(hic);
	  	this._jrbHiCData.addActionListener(hic);
	  	this._jrbHichipData.addActionListener(hic);
	  	this._jrbProcessed.addActionListener(hic);
	  	WorkDirectoryListener wdListener = new WorkDirectoryListener();
	  	this._jbWorkDir.addActionListener(wdListener);
		FileListener chr = new FileListener(this._jtfChrSize);
		this._jbChrSize.addActionListener(chr);
	  	FileListener juice = new FileListener(this._jtfBoxTools);
	  	this._jbBoxTools.addActionListener(juice);
	  	RawDataDirectoryListener ddListener = new RawDataDirectoryListener(this,this._jtfRawData);
	  	this._jbRawData.addActionListener(ddListener);
	  	QuitListener quitListener = new QuitListener(this);
	  	this._jbQuit.addActionListener(quitListener);
	   	StartListener startListener = new StartListener(this);
	   	this._jbStart.addActionListener(startListener);	  
	   	this.setVisible(true);
	 }
	
	/**
	 * getter of the size of the image 
	 * @return int size
	 */
	public int getMatrixSize(){
		String x = this._jtfMatrixSize.getText();
		return Integer.parseInt(x.replaceAll(",", "."));
	}
	
	/**
	 * getter of the bins resolution
	 * @return int bin size
	 */
	public int getResolution(){
		String x = this._jtfResolution.getText();
		return Integer.parseInt(x.replaceAll(",", ".")); 
	}
	
	/**
	 * getter for the core of the filter max
	 * @return double max value
	 */
	public double getMax(){
		String x = this._jtfMax.getText();
		return Double.parseDouble(x.replaceAll(",", "."));
	}
	
	/**
	 * getter of the nb of zero allow around a detected loops
	 * @return int nb of zero
	 */
	public int getNbZero(){
		String x = this._jtfNbZero.getText();
		return Integer.parseInt(x.replaceAll(",", "."));
	}
	
	/**
	 *getter of the nb of CPU 
	 * @return int nb of CPU
	 */
	public int getNbCpu(){
		String x = this._jtfCpu.getText();
		return Integer.parseInt(x.replaceAll(",", "."));
	}
	/**
	 * getter for the core of the filter min
	 * @return double min value
	 */
	public double getMin(){
		String x = this._jtfMin.getText();
		return Double.parseDouble(x.replaceAll(",", "."));
	}
	
	/**
	 * getter of the percentage of pixel saturated in the image
	 *  
	 * @return double 
	 */
	public double getEnhanceSignal(){
		String x = this._jtfEnhanceContrast.getText();
		return Double.parseDouble(x.replaceAll(",", "."));
	}
	
	/**
	 * getter of the different resolution used in the SIP analysis
	 * 
	 * @return int factor
	 */
	public int getFactorChoice(){
		if(this._checkbox2.isSelected() && this._checkbox5.isSelected())	return 4;
		else if( this._checkbox5.isSelected()) return 3;
		else if( this._checkbox2.isSelected()) return 2;
		else return 1;
	}
	
	/**
	 * threshold for the regional maxima detection
	 * @return int 
	 */
	public int getNoiseTolerance(){
		String x = this._jtfNoiseTolerance.getText();
		return Integer.parseInt(x.replaceAll(",", "."));
	}
	/**
	 * getter of the gaussian filter core
	 * @return double gaussian
	 */
	public double getGaussian(){
		String x = this._jtfGaussian.getText();
		return Double.parseDouble(x.replaceAll(",", "."));
	}
	
	/**
	 * size of the diag, all the loops detected inside this size will be removed
	 * @return int diag size
	 */
	public int getDiagSize(){
		String x = this._jtfDiagSize.getText();
		return Integer.parseInt(x.replaceAll(",", "."));
	}
	
	/**
	 * getter of the workdir path
	 * @return String workdir path
	 */
	public String getOutputDir(){ return this._jtfWorkDir.getText(); }
	
	
	/**
	 * getter of the input path (hic file or SIP)
	 * @return String input path
	 */
	public String getRawDataDir(){	return this._jtfRawData.getText();}
	
	/**
	 * getter of the chrSize file path
	 * @return String 
	 */
	public String getChrSizeFile(){	return this._jtfChrSize.getText();}
	
	/**
	 * getter of the juicerToolsBox.jar path
	 * @return String path
	 */
	public String getJuiceBox(){ return this._jtfBoxTools.getText();}
	
	/**
	 * boolean if true start was pushed else keep the gui active
	 * @return
	 */
	public boolean isStart(){ return this._start;}
	
	/**
	 * boolean isProcessed if true SIP data to reprocess
	 * @return boolean
	 */
	public boolean isProcessed(){return this._jrbProcessed.isSelected();}
	
	/**
	 * 
	 * @return
	 */
	public boolean isHiChIP(){	return this._jrbHichipData.isSelected();}
	/**
	 * 
	 * @return
	 */
	public boolean isHiCData(){	return this._jrbHiCData.isSelected();}
	
	/**
	 * 
	 * @return
	 */
	public boolean isHic(){ return this._jrbHic.isSelected();}
	
	/**
	 * 
	 * @return
	 */
	public boolean isDroso(){ return this._jCbIsDroso.isSelected();}
	
	/**
	 * 
	 * @return
	 */
	public boolean isDeletTif(){ return this._checkboxDeleteTif.isSelected();}
	
	/**
	 * 
	 * @return
	 */
	public boolean isAccurate(){ return this._checkboxIsAccurate.isSelected();}
	
	/**
	 * getter of fdr
	 * @return double fdr
	 */
	public double getFDR(){
		String x = _jtfFdr.getText();
		return Double.parseDouble(x.replaceAll(",", "."));
	}
	
	/**
	 * normalization for dumpdata 
	 * @return boolean
	 */
	public boolean isVC_SQRT(){	return _jrbVC_sqrt.isSelected();}
	
	/**
	 * normalization for dumpdata 
	 * @return boolean
	 */
	public boolean isVC(){ return _jrbVC.isSelected(); }
	
	/**
	 * normalization for dumpdata 
	 * @return boolean
	 */
	public boolean isNONE(){ return _jrbNone.isSelected(); }
	
	/**
	 * normalization for dumpdata 
	 * @return boolean
	 */
	public boolean isKR(){	return _jrbKR.isSelected();}



	/********************************************************************************************************************************************
	  * 	Classes listener to interact with the several element of the window
	  */
	 /********************************************************************************************************************************************
	 /********************************************************************************************************************************************
	 /********************************************************************************************************************************************
	 /********************************************************************************************************************************************/
	

	/**
	 * Radio button listener, manage teh access of the different button box etc on function of the parameters choose
	 * 
	 * @author axel poulet
	 *
	 */
	class RBHicListener implements ActionListener{
		/** */
		GuiAnalysis _gui;
		/**
		 * 
		 * @param gui
		 */
		public  RBHicListener (GuiAnalysis gui){ _gui = gui; }
		
		/**
		 * manage the access of the different gui element on function of the paramter choose
		 */
		public void actionPerformed(ActionEvent actionEvent){
			if(_gui.isHiChIP()){
        		_jtfGaussian.setText("1.5");
        		_jtfEnhanceContrast.setText("0.5");
        		_jtfMin.setText("1");
        		_jtfMax.setText("1");
        		_jtfNbZero.setText("25");
        		_jtfNoiseTolerance.setText("1");
        		_gui._jrbNone.setSelected(true);
        	}else if(_gui.isHiCData()){
        		_jtfGaussian.setText("1.5");
        		_jtfEnhanceContrast.setText("0.005");
        		_jtfMin.setText("2.0");
        		_jtfMax.setText("2.0");
        		_jtfNbZero.setText("6");
        		_jtfNoiseTolerance.setText("2800");
        		_gui._jrbKR.setSelected(true);
        	}
			
			if (_gui.isHic()){
				_gui._jrbHiCData.setEnabled(true);
				_gui._jrbHichipData.setEnabled(true);
	        	_gui._jrbVC_sqrt.setEnabled(true);
	        	_gui._jrbVC.setEnabled(true);
	        	_gui._jrbNone.setEnabled(true);
	        	_gui._jrbKR.setEnabled(true);
	        	_gui._jtfBoxTools.setEnabled(true);
	        	_jbBoxTools.setEnabled(true);
	        	_gui._jtfMax.setEditable(true);
        		_gui._jtfMin.setEditable(true);
        		_gui._jtfEnhanceContrast.setEditable(true);
	        }else if(_gui.isProcessed()){
	        	_gui._jrbHiCData.setEnabled(true);
				_gui._jrbHichipData.setEnabled(true);
	        	_gui._jrbVC_sqrt.setEnabled(false);
	        	_gui._jrbVC.setEnabled(false);
	        	_gui._jrbNone.setEnabled(false);
	        	_gui._jrbKR.setEnabled(false);
	        	_gui._jtfBoxTools.setEnabled(false);
	        	_jbBoxTools.setEnabled(false);
	        	_gui._jtfMax.setEditable(true);
	        	_gui._jtfNbZero.setEnabled(true);
	        	_gui._jtfMin.setEditable(true);
	        	_gui._jtfEnhanceContrast.setEditable(true);
	        }
	    }
	}
	
	
	/**
	 * 
	 * @author axel poulet
	 * Listerner for the start button 
	 */
	
	class StartListener implements ActionListener {
		/** */
		GuiAnalysis _gui;
		/**
		 * 
		 * @param gui
		 */
		public  StartListener (GuiAnalysis gui){ _gui = gui; }
		/**
		 * Test all the box, condition etc before to allow the program to run and dispose the gui
		 */
		public void actionPerformed(ActionEvent actionEvent){
			if (_jtfWorkDir.getText().isEmpty() || _jtfRawData.getText().isEmpty() || _gui._jtfChrSize.getText().isEmpty()){
				JOptionPane.showMessageDialog(
					null, "You did not choose a output directory, the raw data, or chromosom size file",
					"Error", JOptionPane.ERROR_MESSAGE
				); 
			}else if(_gui.isHic() && (_gui._jtfBoxTools.getText().isEmpty() )){
				JOptionPane.showMessageDialog(
					null,"You did not choose a Juicer_box_tools.jar path",
					"Error", JOptionPane.ERROR_MESSAGE
				);
			}else if(_gui._jtfDiagSize.getText().matches("[a-zA-Z]") || _gui._jtfEnhanceContrast.getText().matches("[a-zA-Z]") ||
					_gui._jtfGaussian.getText().matches("[a-zA-Z]") || _gui._jtfMatrixSize.getText().matches("[a-zA-Z]") ||
					_gui._jtfMax.getText().matches("[a-zA-Z]") || _gui._jtfMin.getText().matches("[a-zA-Z]") ||
					_gui._jtfResolution.getText().matches("[a-zA-Z]") || _gui._jtfNbZero.getText().matches("[a-zA-Z]") ||
					_gui._jtfNoiseTolerance.getText().matches("[a-zA-Z]")|| _gui._jtfCpu.getText().matches("[a-zA-Z]")){
					JOptionPane.showMessageDialog(
						null, "some alphabetic character detected in integer or float paramters",
						"Error", JOptionPane.ERROR_MESSAGE
					);	
			}else if(_gui.getNbCpu() > Runtime.getRuntime().availableProcessors() || _gui.getNbCpu()<0 ){
				JOptionPane.showMessageDialog(
						null, "The number of CPU chose is superior to the number of computer's CPU",
						"Error", JOptionPane.ERROR_MESSAGE
					);	
			}else{
				_start=true;
				_gui.dispose();
			}
		}
	}
	
	/**
	 * Quit button listener
	 * 
	 * @author axel poulet
	 *
	 */
	class QuitListener implements ActionListener {
		/** */
		GuiAnalysis _gui;	
		/**
		 *	 Constructor
		 * @param gui
		 */
		public  QuitListener (GuiAnalysis gui){ _gui = gui; }
		/**
		 * dipose the gui and quit the program 
		 */
		public void actionPerformed(ActionEvent actionEvent){
			_gui.dispose();
			System.exit(0);
		}
	}
	
	
	
	/** 
	 * @author axel poulet
	 *
	 */
	class WorkDirectoryListener implements ActionListener{
		/**
		 * 
		 */
		 public void actionPerformed(ActionEvent actionEvent){
			 setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			 JFileChooser jFileChooser = new JFileChooser();
			 jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			 int returnValue = jFileChooser.showOpenDialog(getParent());
			 if(returnValue == JFileChooser.APPROVE_OPTION){
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
	 * @author axel poulet
	 *
	 */
	class RawDataDirectoryListener implements ActionListener{
		/** */
		GuiAnalysis _gui;
		/** */
		JTextField _jtf;
		/**
		 * 
		 * @param gui
		 * @param jtf
		 */
		public RawDataDirectoryListener(GuiAnalysis gui,JTextField jtf){
			_gui = gui;
			_jtf = jtf;
		}
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent actionEvent ){
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			JFileChooser jFileChooser = new JFileChooser();
			jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if(_gui._jrbHic.isSelected())
				jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int returnValue = jFileChooser.showOpenDialog(getParent());
			if(returnValue == JFileChooser.APPROVE_OPTION){
				@SuppressWarnings("unused")
				String run = jFileChooser.getSelectedFile().getName();
				String text= jFileChooser.getSelectedFile().getAbsolutePath();
				_jtf.setText(text);
			 }
			 setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		 }	
	 }
	
	/**
	 * 
	 * @author axel poulet
	 *
	 */
	class FileListener implements ActionListener{
		/** */
		JTextField _jtf;
		/**
		 * 
		 * @param jtf
		 */
		public FileListener(JTextField jtf){ _jtf = jtf; }
		
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
				String file = jFileChooser.getSelectedFile().getAbsolutePath();
				_jtf.setText(file);
			}
			setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}	
	}
}