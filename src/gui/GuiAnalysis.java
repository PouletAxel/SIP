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

public class GuiAnalysis extends JFrame
{
	/** */
	private static final long serialVersionUID = 1L;
	/** */
	private Container m_container;
	/** */
	private JLabel m_jlWorkDir;
	/** */
	private JButton m_jbWorkDir = new JButton("Output directory");
	/** */
	private JButton m_jbRawData = new JButton("Raw data");
	/** */
	private JButton m_jbChrSize = new JButton("Chr size file");
	/** */
	private JTextField m_jtfChrSize  =  new JTextField();
	/** */
	private JTextField m_jtfWorkDir  =  new JTextField();
	/** */
	private JTextField m_jtfRawData = new JTextField();
	/***/
	private JButton m_jbStart = new JButton("Start");
	/** */
	private JButton m_jbQuit = new JButton("Quit");
	/** */
	private JLabel m_jlInputFileType;
	/** */
	private ButtonGroup m_bGroupInputType = new ButtonGroup();
	/** */
	private JRadioButton m_jrbHic = new JRadioButton("hic");
	/** */
	private JRadioButton m_jrbProcessed = new JRadioButton("processed");
	/** */
    private String m_juiceBoxTools;
    /** */
    private JTextField m_jtfBoxTools = new JTextField();
    /** */
    private JButton m_jbBoxTools = new JButton("JuiceBox Tools");
    /** */
    private JLabel m_jlNorm;
    /** */
    private ButtonGroup m_bNorm = new ButtonGroup();
    /** */
    private JRadioButton m_jrbNone = new JRadioButton("NONE");
    /** */
    private JRadioButton m_jrbKR = new JRadioButton("KR");
    /** */
    private JRadioButton m_jrbVC = new JRadioButton("VC");
    /** */
    private JRadioButton m_jrbVC_sqrt = new JRadioButton("VC SQRT");
    /** */    
    private JLabel m_imgParam;
    /** */
	private JFormattedTextField m_matrixSize = new JFormattedTextField(Number.class);
	/** */
	private JFormattedTextField m_resolution = new JFormattedTextField(Number.class);
	/** */
	private JFormattedTextField m_diagSize =  new JFormattedTextField(Number.class);
	/** */
    private JLabel m_jlMatrixSize;
    /** */
    private JLabel m_jlResolution;
    /** */
    private JLabel m_jldiagSize;
    /** */
    private JFormattedTextField m_gaussian = new JFormattedTextField(Number.class);
    /** */
    private JFormattedTextField m_min = new JFormattedTextField(Number.class);
    /** */
    private JFormattedTextField m_max =  new JFormattedTextField(Number.class);
    /** */
    private JFormattedTextField m_enhanceContrast =  new JFormattedTextField(Number.class);
    /** */
    private JFormattedTextField m_nbZero =  new JFormattedTextField(Number.class);
    /** */
    private JFormattedTextField m_noiseTolerance =  new JFormattedTextField(Number.class);
    /** */
    private JLabel m_jlGaussian;
    /** */
    private JLabel m_jlMin;
    /** */
    private JLabel m_jlMax;
    /** */
    private JLabel m_jlEnhance;
    /** */
    private JLabel m_jlNbZero;
    /** */
    private JLabel m_jlNT;
    /** */
    private boolean _start = false;
 
   
    
    /** */
    private JCheckBox m_checkbox2 = new JCheckBox("resolution*2",true);
    /** */
    private JCheckBox m_checkbox5 = new JCheckBox("resolution*5",false);
   
    private JCheckBox m_checkboxDeleteTif = new JCheckBox("Delete tif files",true);
   
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args)  {
		
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
		m_container = getContentPane();
		GridBagLayout gridBagLayout = new GridBagLayout();
	   	gridBagLayout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.1};
	   	gridBagLayout.rowHeights = new int[] {17, 300, 124, 7};
	   	gridBagLayout.columnWeights = new double[] {0.0, 0.0, 0.0, 0.1};
	   	gridBagLayout.columnWidths = new int[] {300, 120, 72, 20};
	   	m_container.setLayout (gridBagLayout);
	   	
	   	//////////////////////////////////////// First case of the grid bag layout
	   	
	    /////////////////////// group of radio button to choose the input type file 
	   	m_jlInputFileType = new JLabel();
		m_jlInputFileType.setFont(new java.awt.Font("arial",1,12));
	   	m_jlInputFileType.setText("Program choice:");
	   	m_container.add(m_jlInputFileType, new GridBagConstraints
	   			(
	   					0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   					GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0
	   		    )
	   	);
	   	
	   	m_bGroupInputType.add(m_jrbProcessed);
	 	m_bGroupInputType.add(m_jrbHic);
	 	
		m_jrbHic.setFont(new java.awt.Font("arial",2,11));
		m_jrbProcessed.setFont(new java.awt.Font("arial",2,11));
		m_container.add(m_jrbHic,new GridBagConstraints
				(
						0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE, new Insets(20, 20, 0, 0), 0, 0
				)
		);
		m_container.add(m_jrbProcessed,new GridBagConstraints
				(
						0, 1, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE,new Insets(20, 100, 0, 0), 0, 0
				)
		);
		
		m_jrbHic.setSelected(true);

		
		m_jbBoxTools.setPreferredSize(new java.awt.Dimension(120, 21));
		m_jbBoxTools.setFont(new java.awt.Font("Albertus",2,10));
	   	m_container.add(m_jbBoxTools, new GridBagConstraints
	   			(
	   					0, 1, 0, 0, 0.0, 0.0,GridBagConstraints.NORTHWEST,
	   					GridBagConstraints.NONE, new Insets(50, 10, 0, 0), 0, 0
	   			)
	   	);
	  
	   	m_jtfBoxTools.setPreferredSize(new java.awt.Dimension(280, 21));
	   	m_jtfBoxTools.setFont(new java.awt.Font("Albertus",2,10));	
		m_container.add( m_jtfBoxTools, new GridBagConstraints
				(
						0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE, new Insets(50, 160, 0, 0), 0, 0
				)
		);
		
		m_jlNorm = new JLabel();
		m_jlNorm.setText("Nomalisation to dump data:");
		m_jlNorm.setFont(new java.awt.Font("arial",2,11));
	   	m_container.add(m_jlNorm, new GridBagConstraints
	   			(
	   					0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   					GridBagConstraints.NONE, new Insets(83, 10, 0, 0), 0, 0
	   		    )
	   	);
	   	
	   	m_bNorm.add(m_jrbNone);
	   	m_bNorm.add(m_jrbKR);
	   	m_bNorm.add(m_jrbVC);
	   	m_bNorm.add(m_jrbVC_sqrt);
	   	
	   	m_jrbNone.setFont(new java.awt.Font("arial",2,11));
	   	m_jrbKR.setFont(new java.awt.Font("arial",2,11));
	   	m_jrbVC.setFont(new java.awt.Font("arial",2,11));
	   	m_jrbVC_sqrt.setFont(new java.awt.Font("arial",2,11));
		
	   	m_container.add(m_jrbNone,new GridBagConstraints
				(
						0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE, new Insets(80, 190, 0, 0), 0, 0
				)
		);
		m_container.add(m_jrbKR,new GridBagConstraints
				(
						0, 1, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE,new Insets(80, 250, 0, 0), 0, 0
				)
		);
		m_container.add(m_jrbVC, new GridBagConstraints
				(
						0,1, 0, 0, 0.0, 0.0,GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE, new Insets(80, 295, 0, 0), 0, 0
				)
		);
		m_container.add(m_jrbVC_sqrt, new GridBagConstraints
				(
						0,1, 0, 0, 0.0, 0.0,GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE, new Insets(80, 340, 0, 0), 0, 0
				)
		);
		m_jrbKR.setSelected(true);
		
	   	m_jlWorkDir = new JLabel();
		m_jlWorkDir.setText("Work directory and data directory choice : ");
		m_jlWorkDir.setFont(new java.awt.Font("arial",1,12));
	   	m_container.add( m_jlWorkDir, new GridBagConstraints
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
	   			+ "<br/>data processed by SIP hic" 
	   			+ "<br/>Option <strong>compare</strong> program need processed data</center></font></html>");
	   	jTextPane.setEditable(false);
	   	

	   	
	   	m_container.add( jTextPane,new GridBagConstraints
	   			(
	   					0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   					GridBagConstraints.NONE, new Insets(130, 100, 0, 0), 0, 0
	   			)	
	   	);
	   	
	   	///////////////////// Rawdata and work dir button and text.	   	
	   	
	   	m_jbRawData.setPreferredSize(new java.awt.Dimension(120, 21));
	   	m_jbRawData.setFont(new java.awt.Font("arial",2,10));
	   	m_container.add ( m_jbRawData, new GridBagConstraints
	   			(
	   					0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, 
	   					GridBagConstraints.NONE, new Insets(250, 10, 0, 0), 0, 0
	   			)
	   	);
	   	
	   	m_jtfRawData.setPreferredSize(new java.awt.Dimension(280, 21));
		m_jtfRawData.setFont(new java.awt.Font("arial",2,10));
		m_container.add(m_jtfRawData, new GridBagConstraints
				(
						0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE, new Insets(250, 160, 0, 0),0, 0
				)
		);
		
		m_jbWorkDir.setPreferredSize(new java.awt.Dimension(120, 21));
	   	m_jbWorkDir.setFont(new java.awt.Font("arial",2,10));
	   	m_container.add(m_jbWorkDir, new GridBagConstraints
	   			(
	   					0, 1, 0, 0, 0.0, 0.0,GridBagConstraints.NORTHWEST,
	   					GridBagConstraints.NONE, new Insets(280, 10, 0, 0), 0, 0
	   			)
	   	);
	  
		m_jtfWorkDir.setPreferredSize(new java.awt.Dimension(280, 21));
	   	m_jtfWorkDir.setFont(new java.awt.Font("arial",2,10));	
		m_container.add( m_jtfWorkDir, new GridBagConstraints
				(
						0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE, new Insets(280, 160, 0, 0), 0, 0
				)
		);
		
		////////////////////////// second case
		m_imgParam = new JLabel();
		m_imgParam.setText("Matrix parameters:");
		m_imgParam.setFont(new java.awt.Font("arial",1,12));
	   	m_container.add (m_imgParam, new GridBagConstraints
	   		(
	   			0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   			GridBagConstraints.NONE, new Insets(20, 10, 0, 0), 0, 0
	   	    )
	   	);
	   	  	
	   	m_container.setLayout (gridBagLayout);
		m_jlMatrixSize = new JLabel();
		m_jlMatrixSize.setText("Matrix size (in bins):");
		m_jlMatrixSize.setFont(new java.awt.Font("arial",2,11));
		m_container.add( m_jlMatrixSize, new GridBagConstraints
				(
				0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(50, 20, 0, 0), 0, 0
			)
		);
		
		m_matrixSize.setText("2000");
		m_matrixSize.setPreferredSize(new java.awt.Dimension(60, 21));
		m_matrixSize.setFont(new java.awt.Font("arial",2,11));
		m_container.add( m_matrixSize, new GridBagConstraints
			(
				0, 2, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST, 
				GridBagConstraints.NONE, new Insets(50, 170, 0, 0), 0, 0
			)
		);
		
	 	m_container.setLayout (gridBagLayout);
			m_jldiagSize = new JLabel();
			m_jldiagSize.setText("Diag size (in bins):");
			m_jldiagSize.setFont(new java.awt.Font("arial",2,11));
			m_container.add( m_jldiagSize, new GridBagConstraints
					(
					0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE, new Insets(80, 20, 0, 0), 0, 0
				)
			);
			
			m_diagSize.setText("5");
			m_diagSize.setPreferredSize(new java.awt.Dimension(60, 21));
			m_diagSize.setFont(new java.awt.Font("arial",2,11));
			m_container.add( m_diagSize, new GridBagConstraints
				(
					0, 2, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST, 
					GridBagConstraints.NONE, new Insets(80, 170, 0, 0), 0, 0
				)
			);
		
		
		m_jlResolution = new JLabel();
		m_jlResolution.setText("Resolution (in bases):");
		m_jlResolution.setFont(new java.awt.Font("arial",2,11));
		m_container.add( m_jlResolution, new GridBagConstraints
			(
				0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(110, 20, 0, 0), 0, 0
			)
		);
		
		m_resolution.setText("5000");
		m_resolution.setPreferredSize(new java.awt.Dimension(60, 21));
		m_resolution.setFont(new java.awt.Font("arial",2,11));
		m_container.add( m_resolution, new GridBagConstraints
			(
				0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(110, 170, 0, 0), 0, 0
			)
		);
		
		
		m_imgParam = new JLabel();
		m_imgParam.setText("<html> Multi resolution loop calling:<br><font size=-2>(default: resolution, resolution*2.</html> ");
		m_container.add(m_imgParam,new GridBagConstraints
				(
						0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE, new Insets(20, 250, 0, 0), 0, 0
				)
		);
		m_checkbox2.setFont(new java.awt.Font("arial",2,12));
		m_container.add(m_checkbox2,new GridBagConstraints
				(
						0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE, new Insets(50, 300, 0, 0), 0, 0
				)
		);
		m_checkbox5.setFont(new java.awt.Font("arial",2,12));
		m_container.add(m_checkbox5,new GridBagConstraints
				(
						0, 2, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE,new Insets(80, 300, 0, 0), 0, 0
				)
		);
			
		
		m_jlInputFileType = new JLabel();
	   	m_jlInputFileType.setText("Chromosome size file (same chr names than in .hic file):");
	   	m_jlInputFileType.setFont(new java.awt.Font("arial",1,12));
	   	m_container.add(m_jlInputFileType, new GridBagConstraints
	   			(
	   					0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   					GridBagConstraints.NONE, new Insets(140, 10, 0, 0), 0, 0
	   		    )
	   	);
	   	
		m_jbChrSize.setPreferredSize(new java.awt.Dimension(120, 21));
		m_jbChrSize.setFont(new java.awt.Font("arial",2,11));
	   	m_container.add ( m_jbChrSize, new GridBagConstraints
	   			(
	   					0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, 
	   					GridBagConstraints.NONE, new Insets(170, 10, 0, 0), 0, 0
	   			)
	   	);
	   	
	   	this.m_jtfChrSize.setPreferredSize(new java.awt.Dimension(280, 21));
	   	m_jtfChrSize.setFont(new java.awt.Font("arial",2,10));
		m_container.add(m_jtfChrSize, new GridBagConstraints
				(
						0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE, new Insets(170, 160, 0, 0),0, 0
				)
		);
		
		
		m_jlInputFileType = new JLabel();
	   	m_jlInputFileType.setText("Image processing parameters:");
	   	m_jlInputFileType.setFont(new java.awt.Font("arial",1,12));
	   	m_container.add(m_jlInputFileType, new GridBagConstraints
	   			(
	   					0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   					GridBagConstraints.NONE, new Insets(200, 10, 0, 0), 0, 0
	   		    )
	   	);
		
	   	m_container.setLayout (gridBagLayout);
		this.m_jlGaussian = new JLabel();
		m_jlGaussian.setText("Gaussian filter:");
		m_jlGaussian.setFont(new java.awt.Font("arial",2,11));
		m_container.add( m_jlGaussian, new GridBagConstraints
				(
				0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(230, 20, 0, 0), 0, 0
			)
		);
		
		this.m_gaussian.setText("1.5");
		m_gaussian.setPreferredSize(new java.awt.Dimension(60, 21));
		m_gaussian.setFont(new java.awt.Font("arial",2,11));
		m_container.add( m_gaussian, new GridBagConstraints
			(
				0, 2, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST, 
				GridBagConstraints.NONE, new Insets(230, 150, 0, 0), 0, 0
			)
		);
		
	 	m_container.setLayout (gridBagLayout);
	
	 	this.m_jlNT = new JLabel();
		m_jlNT.setText("Threshold for maxima detection:");
		m_jlNT.setFont(new java.awt.Font("arial",2,11));
		m_container.add( m_jlNT, new GridBagConstraints
					(
					0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE, new Insets(230, 230, 0, 0), 0, 0
				)
			);
			
		this.m_noiseTolerance.setText("2100");
		m_noiseTolerance.setPreferredSize(new java.awt.Dimension(60, 21));
		m_noiseTolerance.setFont(new java.awt.Font("arial",2,11));
		m_container.add( m_noiseTolerance, new GridBagConstraints
				(
					0, 2, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST, 
					GridBagConstraints.NONE, new Insets(230, 430, 0, 0), 0, 0
				)
			);
		
	
		this.m_jlMax = new JLabel();
		m_jlMax.setText("Maximum filter:");
		m_jlMax.setFont(new java.awt.Font("arial",2,11));
		m_container.add( m_jlMax, new GridBagConstraints
					(
					0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE, new Insets(260, 20, 0, 0), 0, 0
				)
			);
			
		this.m_max.setText("2");
		m_max.setPreferredSize(new java.awt.Dimension(60, 21));
		m_max.setFont(new java.awt.Font("arial",2,11));
		m_container.add( m_max, new GridBagConstraints
				(
					0, 2, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST, 
					GridBagConstraints.NONE, new Insets(260, 150, 0, 0), 0, 0
				)
			);
	   	
		this.m_jlMin = new JLabel();
		m_jlMin.setText("Minimum filter:");
		m_jlMin.setFont(new java.awt.Font("arial",2,11));
		m_container.add( m_jlMin, new GridBagConstraints
					(
					0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE, new Insets(260, 230, 0, 0), 0, 0
				)
			);
			
		this.m_min.setText("2");
		m_min.setPreferredSize(new java.awt.Dimension(60, 21));
		m_container.add( m_min, new GridBagConstraints
				(
					0, 2, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST, 
					GridBagConstraints.NONE, new Insets(260, 350, 0, 0), 0, 0
				)
			);
	   	
		
		this.m_jlEnhance = new JLabel();
		m_jlEnhance.setText("% of satured pixel:");
		m_jlEnhance.setFont(new java.awt.Font("arial",2,11));
		m_container.add( m_jlEnhance, new GridBagConstraints
					(
					0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE, new Insets(290, 20, 0, 0), 0, 0
				)
			);
			
		this.m_enhanceContrast.setText("0.005");
		m_enhanceContrast.setPreferredSize(new java.awt.Dimension(60, 21));
		m_enhanceContrast.setFont(new java.awt.Font("arial",2,11));
		m_container.add( m_enhanceContrast, new GridBagConstraints
				(
					0, 2, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST, 
					GridBagConstraints.NONE, new Insets(290, 150, 0, 0), 0, 0
				)
			);
	   	   	
		this.m_jlNbZero = new JLabel();
		m_jlNbZero.setText("Number of zero allowed in the loop 24 neighboorhoods:");
		m_jlNbZero.setFont(new java.awt.Font("arial",2,11));
		m_container.add( m_jlNbZero, new GridBagConstraints
					(
					0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE, new Insets(320, 20, 0, 0), 0, 0
				)
			);
			
		this.m_nbZero.setText("6");
		m_nbZero.setPreferredSize(new java.awt.Dimension(60, 21));
		m_nbZero.setFont(new java.awt.Font("arial",2,11));
		m_container.add( m_nbZero, new GridBagConstraints
				(
					0, 2, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST, 
					GridBagConstraints.NONE, new Insets(320, 350, 0, 0), 0, 0
				)
			);
		
		m_checkboxDeleteTif.setFont(new java.awt.Font("arial",2,12));
		m_container.add(m_checkboxDeleteTif,new GridBagConstraints
				(
						0, 2, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE,new Insets(345, 20, 0, 0), 0, 0
				)
		);
	   	
		m_jbStart.setPreferredSize(new java.awt.Dimension(120, 21));
	   	m_container.add(m_jbStart, new GridBagConstraints
	   		(
	   			0, 2, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,
	   			GridBagConstraints.NONE, new Insets(380, 140, 0,0), 0, 0
	   		)
	   	);
	   	
	   	
	   	m_jbQuit.setPreferredSize(new java.awt.Dimension(120, 21));
		m_container.add(m_jbQuit,new GridBagConstraints
			(
				0, 2, 0, 0,  0.0, 0.0,GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,new Insets(380, 10, 0, 0), 0, 0
			)
		);
		
		RBHicListener hic = new RBHicListener(this);
	  	this.m_jrbHic.addActionListener(hic);
	  	this.m_jrbProcessed.addActionListener(hic);
	  	WorkDirectoryListener wdListener = new WorkDirectoryListener();
	  	m_jbWorkDir.addActionListener(wdListener);
		FileListener chr = new FileListener(m_jtfChrSize);
	  	m_jbChrSize.addActionListener(chr);
	  	JuiceBoxListener juice = new JuiceBoxListener();
	  	m_jbBoxTools.addActionListener(juice);
	  	RawDataDirectoryListener ddListener = new RawDataDirectoryListener(this,m_jtfRawData);
	  	m_jbRawData.addActionListener(ddListener);
	  	QuitListener quitListener = new QuitListener(this);
	   	m_jbQuit.addActionListener(quitListener);
	   	StartListener startListener = new StartListener(this);
	   	m_jbStart.addActionListener(startListener);	  
	   	this.setVisible(true);
	 }
	
	/**
	 * 
	 * @return
	 */
	public int getMatrixSize(){
		String x = m_matrixSize.getText();
		return Integer.parseInt(x.replaceAll(",", "."));
	}
	
	/**
	 * 
	 * @return
	 */
	public int getResolution(){
		String x = m_resolution.getText();
		return Integer.parseInt(x.replaceAll(",", ".")); 
	}
	
	/**
	 * 
	 * @return
	 */
	public double getMax(){
		String x = m_max.getText();
		return Double.parseDouble(x.replaceAll(",", "."));
	}
	
	/**
	 * 
	 * @return
	 */
	public int getNbZero(){
		String x = m_nbZero.getText();
		return Integer.parseInt(x.replaceAll(",", "."));
	}
	/**
	 * 
	 * @return
	 */
	public double getMin(){
		String x = m_min.getText();
		return Double.parseDouble(x.replaceAll(",", "."));
	}
	
	/**
	 * 
	 * @return
	 */
	public double getEnhanceSignal(){
		String x = this.m_enhanceContrast.getText();
		return Double.parseDouble(x.replaceAll(",", "."));
	}
	
	/**
	 * 
	 * @return
	 */
	public int getFactorChoice(){
		if(m_checkbox2.isSelected() && m_checkbox5.isSelected())	return 4;
		else if( m_checkbox5.isSelected()) return 3;
		else if( m_checkbox2.isSelected()) return 2;
		else return 1;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getNoiseTolerance(){
		String x = this.m_noiseTolerance.getText();
		return Integer.parseInt(x.replaceAll(",", "."));
	}
	/**
	 * 
	 * @return
	 */
	public double getGaussian(){
		String x = m_gaussian.getText();
		return Double.parseDouble(x.replaceAll(",", "."));
	}
	
	/**
	 * 
	 * @return
	 */
	public int getDiagSize(){
		String x = m_diagSize.getText();
		return Integer.parseInt(x.replaceAll(",", "."));
	}
	
	/**
	 * 
	 * @return
	 */
	public String getOutputDir(){
		return m_jtfWorkDir.getText();
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String getRawDataDir(){
		return m_jtfRawData.getText();
	}
	
	/**
	 * 
	 * @return
	 */
	public String getChrSizeFile(){
		return m_jtfChrSize.getText();
	}
	
	/**
	 * 
	 * @return
	 */
	public String getJuiceBox(){
		return m_jtfBoxTools.getText();
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
		return m_jrbProcessed.isSelected();
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isHic(){
		return m_jrbHic.isSelected();
	}
	public boolean isDeletTif(){
		return m_checkboxDeleteTif.isSelected();
	}
	/**
	 * 
	 * @return
	 */
	public boolean isVC_SQRT(){
		return m_jrbVC_sqrt.isSelected();
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isVC(){
		return m_jrbVC.isSelected();
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isNONE(){
		return m_jrbNone.isSelected();
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isKR(){
		return m_jrbKR.isSelected();
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
			if (m_gui.isHic()){
	        	m_gui.m_jrbVC_sqrt.setEnabled(true);
	        	m_gui.m_jrbVC.setEnabled(true);
	        	m_gui.m_jrbNone.setEnabled(true);
	        	m_gui.m_jrbKR.setEnabled(true);
	        	m_gui.m_jtfBoxTools.setEnabled(true);
	        	m_jbBoxTools.setEnabled(true);
        		m_gaussian.setText("1.5");
        		m_enhanceContrast.setText("0.005");
        		m_min.setText("2");
        		m_max.setText("2");
        		m_nbZero.setText("6");
        		m_noiseTolerance.setText("2100");
        		m_gui.m_max.setEditable(true);
        		m_gui.m_min.setEditable(true);
        		m_gui.m_enhanceContrast.setEditable(true);
	        }
	        else if(m_gui.isProcessed()){
	        	m_gui.m_jrbVC_sqrt.setEnabled(false);
	        	m_gui.m_jrbVC.setEnabled(false);
	        	m_gui.m_jrbNone.setEnabled(false);
	        	m_gui.m_jrbKR.setEnabled(false);
	        	m_gui.m_jtfBoxTools.setEnabled(false);
	        	m_jbBoxTools.setEnabled(false);
	        	m_gaussian.setText("1.5");
	        	m_enhanceContrast.setText("0.005");
	        	m_min.setText("2");
	        	m_max.setText("2");
	        	m_nbZero.setText("6");
	        	m_noiseTolerance.setText("2100");
	        	m_gui.m_max.setEditable(true);
	        	m_gui.m_jlNbZero.setEnabled(true);
	        	m_gui.m_min.setEditable(true);
	        	m_gui.m_enhanceContrast.setEditable(true);		
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
			if (m_jtfWorkDir.getText().isEmpty() || m_jtfRawData.getText().isEmpty() || m_gui.m_jtfChrSize.getText().isEmpty())
				JOptionPane.showMessageDialog
				(
					null,
					"You did not choose a output directory, the raw data, or chromosom size file",
					"Error",
					JOptionPane.ERROR_MESSAGE
				); 
			else if(m_gui.isHic() && (m_gui.m_jtfBoxTools.getText().isEmpty() ))
				JOptionPane.showMessageDialog
				(
					null,
					"You did not choose a Juicer_box_tools.jar path",
					"Error",
					JOptionPane.ERROR_MESSAGE
				);
			else if(m_gui.m_diagSize.getText().matches("[a-zA-Z]") || m_gui.m_enhanceContrast.getText().matches("[a-zA-Z]") ||
					m_gui.m_gaussian.getText().matches("[a-zA-Z]") || m_gui.m_matrixSize.getText().matches("[a-zA-Z]") ||
					m_gui.m_max.getText().matches("[a-zA-Z]") || m_gui.m_min.getText().matches("[a-zA-Z]") ||
					m_gui.m_resolution.getText().matches("[a-zA-Z]") || m_gui.m_nbZero.getText().matches("[a-zA-Z]") ||
					m_gui.m_noiseTolerance.getText().matches("[a-zA-Z]"))
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
				 m_jtfWorkDir.setText(workDir);
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
			if(m_gui.m_jrbHic.isSelected())
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
				m_juiceBoxTools = jFileChooser.getSelectedFile().getAbsolutePath();
				m_jtfBoxTools.setText(m_juiceBoxTools);
			}
			setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}	
	}
}