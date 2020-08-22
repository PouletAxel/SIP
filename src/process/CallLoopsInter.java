package process;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.plugin.filter.GaussianBlur;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

public class CallLoopsInter {


    /**
     * Save the image file
     *
     * @param imagePlusInput image to save
     * @param pathFile path to save the image
     */
    public static ImagePlus imgDiff(ImagePlus imagePlusInput, String pathFile){
        GaussianBlur gb = new GaussianBlur();
        ImageProcessor ip = imagePlusInput.getProcessor();
        gb.blurGaussian(ip, 2);
        FloatProcessor pRaw = new FloatProcessor(ip.getWidth(), ip.getHeight());
        //faire un gaussiane

        for(int i = 3; i < ip.getWidth()-3; ++i){
            for(int j = 3; j < ip.getWidth()-3; ++j){
                float sum = 0;
                for(int ii = i-3; ii < i+3; ++ii) {
                    for (int jj = j-3; jj < j+3; ++jj) {
                        sum = sum+(ip.getf(i,j)- ip.getf(ii,jj));
                    }
                }
                //if(sum < 0 ) sum = 0;
                pRaw.setf(i,j,sum);
            }
        }
        ImagePlus img = new ImagePlus();
        img.setProcessor(pRaw);

        pathFile = pathFile.replace(".tif","_diff.tif");
        saveFile(img, pathFile);
        return img;

    }

    /**
     * Save the image file
     *
     * @param imagePlusInput image to save
     * @param pathFile path to save the image
     */
    public void saveFile ( ImagePlus imagePlusInput, String pathFile){
        FileSaver fileSaver = new FileSaver(imagePlusInput);
        fileSaver.saveAsTiff(pathFile);
    }


}
