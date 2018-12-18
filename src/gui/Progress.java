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
	public JProgressBar bar;
	
	public Progress(String title, int nbChromo){      
		this.setSize(300, 80);
		this.setTitle("*** "+title+" ***");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);      
      
		bar  = new JProgressBar();
		bar.setMaximum(nbChromo);
		bar.setMinimum(0);
		bar.setStringPainted(true);
      
		this.getContentPane().add(bar, BorderLayout.CENTER);  
		this.setVisible(true);
  }

  public static void main(String[] args){
    new Progress("prout", 22);
  }   
}