package plop.straw;

public class QuickStats {
    int numPosPixels;
    float avg;
    float std;
    float sum;
    public QuickStats(int numPosPixels, float avg, float std, float sum) {
        this.numPosPixels = numPosPixels;
        this.avg = avg;
        this.std = std;
        this.sum = sum;
    }
}