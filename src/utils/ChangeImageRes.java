package utils;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

public class ChangeImageRes{
	ImagePlus m_img;
	public ChangeImageRes(ImagePlus img){
		m_img = img;
	}
	
	public ImagePlus run(){
		ShortProcessor p = new ShortProcessor(m_img.getWidth()/2,m_img.getWidth()/2);
		ImageProcessor ip =  m_img.getProcessor();
		for(int i = 0; i <= m_img.getWidth()-1; i+=2){
			for(int j = 0; j <= m_img.getWidth()-1; j+=2){
				float sum = 0;
				for(int ii = i; ii <= i+1; ++ii){
					for(int jj = j ; jj <= j+1; ++jj){
						sum+= ip.getPixel(ii, jj);
					}
				}
				p.setf(i/2,j/2,sum);
			}
		}
		ImagePlus img10kb = new ImagePlus();
		img10kb.setProcessor(p);
		return img10kb;
	}

}
