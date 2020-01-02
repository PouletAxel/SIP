package gui;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

/**
 * 
 * @author axel poulet
 *
 *OppenClasseroom:
 *https://openclassrooms.com/fr/courses/26832-apprenez-a-programmer-en-java/25010-conteneurs-sliders-et-barres-de-progression
 */
public class Progress extends JFrame{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public JProgressBar _bar;
	
	public Progress(){}
	
	public Progress(String title, int nbChromo){      
		this.setSize(400, 80);
		this.setTitle("*** "+title+" ***");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);      
		this._bar  = new JProgressBar();
		this._bar.setMaximum(nbChromo);
		this._bar.setMinimum(0);
		this._bar.setStringPainted(true);
		this.getContentPane().add(this._bar, BorderLayout.CENTER);  
		this.setVisible(true);
  }

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args){ new Progress("test", 22);}   
}