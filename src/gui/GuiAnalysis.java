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
 * @author pouletaxel
 *
 */

public class GuiAnalysis extends JFrame
{
	private static final long serialVersionUID = 1L;
	private Container m_container;
	
	private JLabel m_jlWorkDir;
	private JButton m_jbWorkDir = new JButton("Output directory");
	private JButton m_jbRawData = new JButton("Raw data");
	private JButton m_jbChrSize = new JButton("Chr size file");
	
	private JTextField m_jtfChrSize  =  new JTextField();
	private JTextField m_jtfWorkDir  =  new JTextField();
	private JTextField m_jtfRawData = new JTextField();
	
	private String m_workDir;
	private String m_rawDataDir;
	private String m_chrSize;
	
	private JButton m_jbStart = new JButton("Start");
	private JButton m_jbQuit = new JButton("Quit");
	
	private JLabel m_jlInputFileType;
	private ButtonGroup m_bGroupInputType = new ButtonGroup();
	private JRadioButton m_jrbHic = new JRadioButton("hic file");
    private JRadioButton m_jrbProcessed = new JRadioButton("file processed");

    private String m_juiceBoxTools;
    private JTextField m_jtfBoxTools = new JTextField();
    private JButton m_jbBoxTools = new JButton("JuiceBox Tools");
    
    private JLabel m_jlNorm;
	private ButtonGroup m_bNorm = new ButtonGroup();
	private JRadioButton m_jrbNone = new JRadioButton("NONE");
    private JRadioButton m_jrbKR = new JRadioButton("KR");
    private JRadioButton m_jrbVC = new JRadioButton("VC");
    private JRadioButton m_jrbVC_sqrt = new JRadioButton("VC SQRT");
    
    
    private JLabel m_imgParam;
	
	private JFormattedTextField m_matrixSize = new JFormattedTextField(Number.class);
	private JFormattedTextField m_resolution = new JFormattedTextField(Number.class);
    private JFormattedTextField m_step =  new JFormattedTextField(Number.class);
    private JFormattedTextField m_diagSize =  new JFormattedTextField(Number.class);
    
    
	
    private JLabel m_jlMatrixSize;
    private JLabel m_jlResolution;
    private JLabel m_jlStep;
    private JLabel m_jldiagSize;
	
    private ButtonGroup m_bgTypOFMatrix = new ButtonGroup();
	private JRadioButton m_jrbObserved = new JRadioButton("Observed");
    private JRadioButton m_jrbObservedMExpected = new JRadioButton("Observed minus Expected");
    

    private JFormattedTextField m_gaussian = new JFormattedTextField(Number.class);
	private JFormattedTextField m_min = new JFormattedTextField(Number.class);
    private JFormattedTextField m_max =  new JFormattedTextField(Number.class);
    private JFormattedTextField m_enhanceContrast =  new JFormattedTextField(Number.class);
    private JFormattedTextField m_noiseTolerance =  new JFormattedTextField(Number.class);
    
    private JLabel m_jlGaussian;
    private JLabel m_jlMin;
    private JLabel m_jlMax;
    private JLabel m_jlEnhance;
    private JLabel m_jlNT;
	private boolean _start = false;
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args)  
    {
		GuiAnalysis gui = new GuiAnalysis();
		gui.setLocationRelativeTo(null);
    }
	
    
    /**
     * Architecture of the graphical windows
     *
     */
    
	public GuiAnalysis ()
	{
		///////////////////////////////////////////// Global parameter of the JFram and def of the gridBaglayout
		this.setTitle("NO NAME program.....");
		this.setSize(550, 840);
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
	   	m_jlWorkDir = new JLabel();
		m_jlWorkDir.setText("Work directory and data directory choice : ");
	   	m_container.add( m_jlWorkDir, new GridBagConstraints
	   		(
	   			0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   			GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0
	   		)
	   	);
	   	
	   
	   	JTextPane jTextPane = new JTextPane();
	   	jTextPane.setContentType("text/html");
	   	jTextPane.setBackground(Color.LIGHT_GRAY);
	   	jTextPane.setText(
	   			"<html> The data directory must contain:"
	   			+ "<center>dump files (observed or observed minus expected)"
	   			+ "<strong><br/>OR</strong>"
	   			+ "<br/>hic file"
	   			+ "<strong><br/>OR</strong>"
	   			+ "<br/>data processed by NO NAME program</center></html>");
	   	jTextPane.setEditable(false);
	   	
	   	///////////////////// Rawdata and work dir button and text.
	   	
	   	m_container.add( jTextPane,new GridBagConstraints
	   			(
	   					0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   					GridBagConstraints.NONE, new Insets(30, 100, 0, 0), 0, 0
	   			)	
	   	);
	   	
	   	m_jbRawData.setPreferredSize(new java.awt.Dimension(120, 21));
	   	m_jbRawData.setFont(new java.awt.Font("Albertus",2,10));
	   	m_container.add ( m_jbRawData, new GridBagConstraints
	   			(
	   					0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, 
	   					GridBagConstraints.NONE, new Insets(160, 10, 0, 0), 0, 0
	   			)
	   	);
	   	
	   	m_jtfRawData.setPreferredSize(new java.awt.Dimension(280, 21));
		m_jtfRawData.setFont(new java.awt.Font("Albertus",2,10));
		m_container.add(m_jtfRawData, new GridBagConstraints
				(
						0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE, new Insets(160, 160, 0, 0),0, 0
				)
		);
		
	 	m_jbWorkDir.setPreferredSize(new java.awt.Dimension(120, 21));
	   	m_jbWorkDir.setFont(new java.awt.Font("Albertus",2,10));
	   	m_container.add(m_jbWorkDir, new GridBagConstraints
	   			(
	   					0, 1, 0, 0, 0.0, 0.0,GridBagConstraints.NORTHWEST,
	   					GridBagConstraints.NONE, new Insets(200, 10, 0, 0), 0, 0
	   			)
	   	);
	  
		m_jtfWorkDir.setPreferredSize(new java.awt.Dimension(280, 21));
	   	m_jtfWorkDir.setFont(new java.awt.Font("Albertus",2,10));	
		m_container.add( m_jtfWorkDir, new GridBagConstraints
				(
						0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE, new Insets(200, 160, 0, 0), 0, 0
				)
		);
		
	   /////////////////////// group of radio button to choose the input type file 
	   	m_jlInputFileType = new JLabel();
	   	m_jlInputFileType.setText("Input file type:");
	   	m_container.add(m_jlInputFileType, new GridBagConstraints
	   			(
	   					0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   					GridBagConstraints.NONE, new Insets(240, 10, 0, 0), 0, 0
	   		    )
	   	);
	   	
	   	m_bGroupInputType.add(m_jrbProcessed);
	 	m_bGroupInputType.add(m_jrbHic);
	 	
		m_jrbHic.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		m_jrbProcessed.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		
		m_container.add(m_jrbHic,new GridBagConstraints
				(
						0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE, new Insets(260, 20, 0, 0), 0, 0
				)
		);
		m_container.add(m_jrbProcessed,new GridBagConstraints
				(
						0, 1, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE,new Insets(260, 100, 0, 0), 0, 0
				)
		);
		m_jrbHic.setSelected(true);

		
		m_jbBoxTools.setPreferredSize(new java.awt.Dimension(120, 21));
		m_jbBoxTools.setFont(new java.awt.Font("Albertus",2,10));
	   	m_container.add(m_jbBoxTools, new GridBagConstraints
	   			(
	   					0, 1, 0, 0, 0.0, 0.0,GridBagConstraints.NORTHWEST,
	   					GridBagConstraints.NONE, new Insets(290, 10, 0, 0), 0, 0
	   			)
	   	);
	  
	   	m_jtfBoxTools.setPreferredSize(new java.awt.Dimension(280, 21));
	   	m_jtfBoxTools.setFont(new java.awt.Font("Albertus",2,10));	
		m_container.add( m_jtfBoxTools, new GridBagConstraints
				(
						0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE, new Insets(290, 160, 0, 0), 0, 0
				)
		);
		
		m_jlNorm = new JLabel();
		m_jlNorm.setText("Nomalisation to dump data:");
		m_jlNorm.setFont(new java.awt.Font("Albertus",2,12));
	   	m_container.add(m_jlNorm, new GridBagConstraints
	   			(
	   					0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   					GridBagConstraints.NONE, new Insets(323, 10, 0, 0), 0, 0
	   		    )
	   	);
	   	
	   	m_bNorm.add(m_jrbNone);
	   	m_bNorm.add(m_jrbKR);
	   	m_bNorm.add(m_jrbVC);
	   	m_bNorm.add(m_jrbVC_sqrt);
	   	
	   	m_jrbNone.setFont(new java.awt.Font("Albertus",2,12));
	   	m_jrbKR.setFont(new java.awt.Font("Albertus",2,12));
	   	m_jrbVC.setFont(new java.awt.Font("Albertus",2,12));
	   	m_jrbVC_sqrt.setFont(new java.awt.Font("Albertus",2,12));
		
	   	m_container.add(m_jrbNone,new GridBagConstraints
				(
						0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE, new Insets(320, 190, 0, 0), 0, 0
				)
		);
		m_container.add(m_jrbKR,new GridBagConstraints
				(
						0, 1, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE,new Insets(320, 250, 0, 0), 0, 0
				)
		);
		m_container.add(m_jrbVC, new GridBagConstraints
				(
						0,1, 0, 0, 0.0, 0.0,GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE, new Insets(320, 295, 0, 0), 0, 0
				)
		);
		m_container.add(m_jrbVC_sqrt, new GridBagConstraints
				(
						0,1, 0, 0, 0.0, 0.0,GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE, new Insets(320, 340, 0, 0), 0, 0
				)
		);
		m_jrbKR.setSelected(true);
	   	
	   	
	   	
	   	
		/////////////////////////// second case
		
		m_imgParam = new JLabel();
		m_imgParam.setText("Matrix parameters:");
	   	m_container.add (m_imgParam, new GridBagConstraints
	   		(
	   			0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   			GridBagConstraints.NONE, new Insets(50, 10, 0, 0), 0, 0
	   	    )
	   	);
	   	
	   	
	   	m_container.setLayout (gridBagLayout);
		m_jlMatrixSize = new JLabel();
		m_jlMatrixSize.setText("Matrix size (in bins):");
		m_jlMatrixSize.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		m_container.add( m_jlMatrixSize, new GridBagConstraints
				(
				0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(90, 20, 0, 0), 0, 0
			)
		);
		
		m_matrixSize.setText("2000");
		m_matrixSize.setPreferredSize(new java.awt.Dimension(60, 21));
		m_container.add( m_matrixSize, new GridBagConstraints
			(
				0, 2, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST, 
				GridBagConstraints.NONE, new Insets(90, 170, 0, 0), 0, 0
			)
		);
		
	 	m_container.setLayout (gridBagLayout);
			m_jldiagSize = new JLabel();
			m_jldiagSize.setText("Diag size (in bins):");
			m_jldiagSize.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
			m_container.add( m_jldiagSize, new GridBagConstraints
					(
					0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE, new Insets(90, 250, 0, 0), 0, 0
				)
			);
			
			m_diagSize.setText("2");
			m_diagSize.setPreferredSize(new java.awt.Dimension(60, 21));
			m_container.add( m_diagSize, new GridBagConstraints
				(
					0, 2, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST, 
					GridBagConstraints.NONE, new Insets(90, 380, 0, 0), 0, 0
				)
			);
		
	
		m_jlStep = new JLabel();
		m_jlStep.setText("Step (in bins):");
		m_jlStep.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		m_container.add	( m_jlStep,new GridBagConstraints
			(
				0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(120, 20, 0, 0), 0, 0
		    )
		);
		
		m_step.setText("1000");
		m_step.setPreferredSize(new java.awt.Dimension(60, 21));	
		m_container.add( m_step, new GridBagConstraints
			(
				0, 2,0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(120, 170, 0, 0), 0, 0
		    )
		);
		
		m_jlResolution = new JLabel();
		m_jlResolution.setText("Resolution (in bases):");
		m_jlResolution.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		m_container.add( m_jlResolution, new GridBagConstraints
			(
				0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(150, 20, 0, 0), 0, 0
			)
		);
		
		m_resolution.setText("5000");
		m_resolution.setPreferredSize(new java.awt.Dimension(60, 21));
		m_container.add( m_resolution, new GridBagConstraints
			(
				0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(150, 170, 0, 0), 0, 0
			)
		);
		
		
	   	m_jlInputFileType = new JLabel();
	   	m_jlInputFileType.setText("Type of dump matrix:");
	   	m_container.add(m_jlInputFileType, new GridBagConstraints
	   			(
	   					0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   					GridBagConstraints.NONE, new Insets(180, 10, 0, 0), 0, 0
	   		    )
	   	);
		
		m_bgTypOFMatrix.add(this.m_jrbObserved);
		m_bgTypOFMatrix.add(this.m_jrbObservedMExpected);
	 		 	
		m_jrbObserved.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		m_jrbObservedMExpected.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		
		
		m_container.add(m_jrbObservedMExpected,new GridBagConstraints
				(
						0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE, new Insets(200, 20, 0, 0), 0, 0
				)
		);
		m_container.add(m_jrbObserved,new GridBagConstraints
				(
						0, 2, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE,new Insets(200, 220, 0, 0), 0, 0
				)
		);

		m_jrbObservedMExpected.setSelected(true);
		
		
		m_jlInputFileType = new JLabel();
	   	m_jlInputFileType.setText("Chromosome size file (same chr names than in .hic file):");
	   	m_container.add(m_jlInputFileType, new GridBagConstraints
	   			(
	   					0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   					GridBagConstraints.NONE, new Insets(230, 10, 0, 0), 0, 0
	   		    )
	   	);
	   	
		m_jbChrSize.setPreferredSize(new java.awt.Dimension(120, 21));
		m_jbChrSize.setFont(new java.awt.Font("Albertus",2,10));
	   	m_container.add ( m_jbChrSize, new GridBagConstraints
	   			(
	   					0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, 
	   					GridBagConstraints.NONE, new Insets(260, 10, 0, 0), 0, 0
	   			)
	   	);
	   	
	   	this.m_jtfChrSize.setPreferredSize(new java.awt.Dimension(280, 21));
	   	m_jtfChrSize.setFont(new java.awt.Font("Albertus",2,10));
		m_container.add(m_jtfChrSize, new GridBagConstraints
				(
						0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE, new Insets(260, 160, 0, 0),0, 0
				)
		);
		
		
		m_jlInputFileType = new JLabel();
	   	m_jlInputFileType.setText("Image processing parameters:");
	   	m_container.add(m_jlInputFileType, new GridBagConstraints
	   			(
	   					0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   					GridBagConstraints.NONE, new Insets(290, 10, 0, 0), 0, 0
	   		    )
	   	);
		
	   	m_container.setLayout (gridBagLayout);
		this.m_jlGaussian = new JLabel();
		m_jlGaussian.setText("Gaussian filter:");
		m_jlGaussian.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		m_container.add( m_jlGaussian, new GridBagConstraints
				(
				0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(320, 20, 0, 0), 0, 0
			)
		);
		
		this.m_gaussian.setText("1");
		m_gaussian.setPreferredSize(new java.awt.Dimension(60, 21));
		m_container.add( m_gaussian, new GridBagConstraints
			(
				0, 2, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST, 
				GridBagConstraints.NONE, new Insets(320, 150, 0, 0), 0, 0
			)
		);
		
	 	m_container.setLayout (gridBagLayout);
	
	 	this.m_jlNT = new JLabel();
		m_jlNT.setText("Noise tolerance for maxima detection:");
		m_jlNT.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		m_container.add( m_jlNT, new GridBagConstraints
					(
					0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE, new Insets(320, 230, 0, 0), 0, 0
				)
			);
			
		this.m_noiseTolerance.setText("3000");
		m_noiseTolerance.setPreferredSize(new java.awt.Dimension(60, 21));
		m_container.add( m_noiseTolerance, new GridBagConstraints
				(
					0, 2, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST, 
					GridBagConstraints.NONE, new Insets(320, 480, 0, 0), 0, 0
				)
			);
		
	
		this.m_jlMax = new JLabel();
		m_jlMax.setText("Maximum filter:");
		m_jlMax.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		m_container.add( m_jlMax, new GridBagConstraints
					(
					0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE, new Insets(350, 20, 0, 0), 0, 0
				)
			);
			
		this.m_max.setText("1.5");
		m_max.setPreferredSize(new java.awt.Dimension(60, 21));
		m_container.add( m_max, new GridBagConstraints
				(
					0, 2, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST, 
					GridBagConstraints.NONE, new Insets(350, 150, 0, 0), 0, 0
				)
			);
	   	
		this.m_jlMin = new JLabel();
		m_jlMin.setText("Minimum filter:");
		m_jlMin.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		m_container.add( m_jlMin, new GridBagConstraints
					(
					0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE, new Insets(350, 230, 0, 0), 0, 0
				)
			);
			
		this.m_min.setText("1.5");
		m_min.setPreferredSize(new java.awt.Dimension(60, 21));
		m_container.add( m_min, new GridBagConstraints
				(
					0, 2, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST, 
					GridBagConstraints.NONE, new Insets(350, 330, 0, 0), 0, 0
				)
			);
	   	
		
		this.m_jlEnhance = new JLabel();
		m_jlEnhance.setText("% of satured pixel:");
		m_jlEnhance.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		m_container.add( m_jlEnhance, new GridBagConstraints
					(
					0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE, new Insets(380, 20, 0, 0), 0, 0
				)
			);
			
		this.m_enhanceContrast.setText("0.05");
		m_enhanceContrast.setPreferredSize(new java.awt.Dimension(60, 21));
		m_container.add( m_enhanceContrast, new GridBagConstraints
				(
					0, 2, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST, 
					GridBagConstraints.NONE, new Insets(380, 150, 0, 0), 0, 0
				)
			);
	   	
	   	
	   	
	   	
	   	
	   	
		m_jbStart.setPreferredSize(new java.awt.Dimension(120, 21));
	   	m_container.add(m_jbStart, new GridBagConstraints
	   		(
	   			0, 2, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,
	   			GridBagConstraints.NONE, new Insets(420, 140, 0,0), 0, 0
	   		)
	   	);
	   	
	   	
	   	m_jbQuit.setPreferredSize(new java.awt.Dimension(120, 21));
		m_container.add(m_jbQuit,new GridBagConstraints
			(
				0, 2, 0, 0,  0.0, 0.0,GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,new Insets(420, 10, 0, 0), 0, 0
			)
		);
		
	  
	  	RBObservedListener rbl = new RBObservedListener(this);
	  	this.m_jrbObserved.addActionListener(rbl);
	  	this.m_jrbObservedMExpected.addActionListener(rbl);
	  	RBHicListener hic = new RBHicListener(this);
	  	this.m_jrbHic.addActionListener(hic);
	  	this.m_jrbProcessed.addActionListener(hic);

	  	WorkDirectoryListener wdListener = new WorkDirectoryListener();
	  	m_jbWorkDir.addActionListener(wdListener);
		ChrSizeFileListener chr = new ChrSizeFileListener();
	  	m_jbChrSize.addActionListener(chr);
	  	
	  	JuiceBoxListener juice = new JuiceBoxListener();
	  	m_jbBoxTools.addActionListener(juice);
	  	RawDataDirectoryListener ddListener = new RawDataDirectoryListener(this);
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
	public int getStep(){
		String x = m_step.getText();
		return Integer.parseInt(x.replaceAll(",", "."));
	}

	public double getMax(){
		String x = m_max.getText();
		return Double.parseDouble(x.replaceAll(",", "."));
	}
	
	public double getMin(){
		String x = m_min.getText();
		return Double.parseDouble(x.replaceAll(",", "."));
	}
	
	public double getEnhanceSignal(){
		String x = this.m_enhanceContrast.getText();
		return Double.parseDouble(x.replaceAll(",", "."));
	}
	
	public int getNoiseTolerance(){
		String x = this.m_noiseTolerance.getText();
		return Integer.parseInt(x.replaceAll(",", "."));
	}
	
	public double getGaussian(){
		String x = m_gaussian.getText();
		return Double.parseDouble(x.replaceAll(",", "."));
	}
	
	public int getDiagSize(){
		String x = m_gaussian.getText();
		return Integer.parseInt(x.replaceAll(",", "."));
	}
	
	
	public String getOutputDir(){return m_jtfWorkDir.getText();}
	public String getRawDataDir(){return m_jtfRawData.getText();}
	public String getChrSizeFile(){return m_jtfChrSize.getText();}
	public String getJuiceBox(){return m_jtfBoxTools.getText();}
	
	public boolean isStart() {	return _start; }
	public boolean isObserved() {	return m_jrbObserved.isSelected(); }

	public boolean isProcessed() {	return m_jrbProcessed.isSelected(); }
	public boolean isHic() {	return m_jrbHic.isSelected(); }
	
	public boolean isVC_SQRT() {	return m_jrbVC_sqrt.isSelected(); }
	public boolean isVC() {	return m_jrbVC.isSelected(); }
	public boolean isNONE() {	return m_jrbNone.isSelected(); }
	public boolean isKR() {	return m_jrbKR.isSelected(); }



	/********************************************************************************************************************************************
	  * 	Classes listener to interact with the several element of the window
	  */
	 /********************************************************************************************************************************************
	 /********************************************************************************************************************************************
	 /********************************************************************************************************************************************
	 /********************************************************************************************************************************************/
	
	class RBObservedListener implements ActionListener 
	{
		GuiAnalysis m_gui;
		public  RBObservedListener (GuiAnalysis gui)
		{
			m_gui = gui;
		}
		
		public void actionPerformed(ActionEvent actionEvent) {
	        if (m_gui.isObserved()) {
	        	m_gui.m_max.setEditable(false);
	        	m_gui.m_min.setEditable(false);
	        	m_gui.m_enhanceContrast.setEditable(false);
	        }
	        else{
	        	m_gui.m_max.setEditable(true);
	        	m_gui.m_max.setEditable(true);
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
	class RBHicListener implements ActionListener {
		GuiAnalysis m_gui;
		public  RBHicListener (GuiAnalysis gui){
			m_gui = gui;
		}
		
		public void actionPerformed(ActionEvent actionEvent) {
	        if (m_gui.isHic()) {
	        	m_gui.m_jrbVC_sqrt.setEnabled(true);
	        	m_gui.m_jrbVC.setEnabled(true);
	        	m_gui.m_jrbNone.setEnabled(true);
	        	m_gui.m_jrbKR.setEnabled(true);
	        	m_gui.m_jtfBoxTools.setEnabled(true);
	        	m_jbBoxTools.setEnabled(true);
	        	//m_gui.m_enhanceContrast.setEditable(false);
	        }
	        else{
	        	m_gui.m_jrbVC_sqrt.setEnabled(false);
	        	m_gui.m_jrbVC.setEnabled(false);
	        	m_gui.m_jrbNone.setEnabled(false);
	        	m_gui.m_jrbKR.setEnabled(false);
	        	m_gui.m_jtfBoxTools.setEnabled(false);
	        	m_jbBoxTools.setEnabled(false);
	        }
	    }
	}
	
	
	/**
	 * 
	 * 
	 *
	 */
	class StartListener implements ActionListener 
	{
	
		GuiAnalysis m_gui;
		/**
		 * 
		 * @param chromocentersAnalysisPipelineBatchDialog
		 */
		public  StartListener (GuiAnalysis gui)
		{
			m_gui = gui;
		}
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent actionEvent)
		{
			if (m_jtfWorkDir.getText().isEmpty() || m_jtfRawData.getText().isEmpty())
				JOptionPane.showMessageDialog
				(
					null,
					"You did not choose a work directory or the raw data",
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
	 * 
	 *
	 */
	class QuitListener implements ActionListener 
	{
		GuiAnalysis m_gui;	
		/**
		 * 
		 * @param chromocentersAnalysisPipelineBatchDialog
		 */
		public  QuitListener (GuiAnalysis gui)
		{
			m_gui = gui;
		}
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent actionEvent)
		{
			m_gui.dispose();
			System.exit(0);
		}
	}
	
	
	
	/**
	 * 
	 * 
	 *
	 */
	class WorkDirectoryListener implements ActionListener
	{
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
				 m_workDir = jFileChooser.getSelectedFile().getAbsolutePath();
				 m_jtfWorkDir.setText(m_workDir);
			 }
			 setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		 }	
	 }
	
	/**
	 * 
	 * 
	 *
	 */
	class RawDataDirectoryListener implements ActionListener
	{
		
		GuiAnalysis m_gui;
		/**
		 * 
		 */
		public RawDataDirectoryListener(GuiAnalysis gui){
			m_gui = gui;
		}
		public void actionPerformed(ActionEvent actionEvent)
		 {
			
			 setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			 JFileChooser jFileChooser = new JFileChooser();
			 jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			 if(m_gui.m_jrbHic.isSelected()){
				 jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			 }
			 int returnValue = jFileChooser.showOpenDialog(getParent());
			 if(returnValue == JFileChooser.APPROVE_OPTION)
			 {
				 @SuppressWarnings("unused")
				 String run = jFileChooser.getSelectedFile().getName();
				 m_rawDataDir = jFileChooser.getSelectedFile().getAbsolutePath();
				 m_jtfRawData.setText(m_rawDataDir);
			 }
			 setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		 }	
	 }
	
	
	/**
	 * 
	 * 
	 *
	 */
	class ChrSizeFileListener implements ActionListener
	{
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent actionEvent)
		 {
			 setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			 JFileChooser jFileChooser = new JFileChooser();
			 jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			 int returnValue = jFileChooser.showOpenDialog(getParent());
			 if(returnValue == JFileChooser.APPROVE_OPTION)
			 {
				 @SuppressWarnings("unused")
				 String run = jFileChooser.getSelectedFile().getName();
				 m_chrSize = jFileChooser.getSelectedFile().getAbsolutePath();
				 m_jtfChrSize.setText(m_chrSize);
			 }
			 setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		 }	
	 }
	
	/**
	 * 
	 * 
	 *
	 */
	class JuiceBoxListener implements ActionListener
	{
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent actionEvent)
		 {
			 setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			 JFileChooser jFileChooser = new JFileChooser();
			 jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			 int returnValue = jFileChooser.showOpenDialog(getParent());
			 if(returnValue == JFileChooser.APPROVE_OPTION)
			 {
				 @SuppressWarnings("unused")
				 String run = jFileChooser.getSelectedFile().getName();
				 m_juiceBoxTools = jFileChooser.getSelectedFile().getAbsolutePath();
				 m_jtfBoxTools.setText(m_juiceBoxTools);
			 }
			 setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		 }	
	 }
}