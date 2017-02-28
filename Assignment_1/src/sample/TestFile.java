package sample;

/**
 * Created by clyve on 09/02/17.
 */
import java.text.DecimalFormat;
public class TestFile {
    private String filename;
    private double spamProbability;
    private String actualClass;
    private String actualClassCounted;

    public TestFile(String filename, double spamProbability, String actualClass, String actualClassCounted) {
        this.filename = filename;
        this.spamProbability = spamProbability;
        this.actualClass = actualClass;
        this.actualClassCounted = actualClassCounted;
    }

    public String getFilename() { return this.filename; }
    public double getSpamProbability() { return this.spamProbability; }
    public String getSpamProbRounded() {
        DecimalFormat df = new DecimalFormat("0.00000");
        return df.format(this.spamProbability);
    }
    public String getActualClass() { return this.actualClass; }
    public String getActualClassCounted() { return this.actualClassCounted; }

    public void setFilename(String value) { this.filename = value; }
    public void setSpamProbability(double val) { this.spamProbability = val; }
    public void setActualClass(String value) { this.actualClass = value; }
    public void setActualClassCounted(String value) { this.actualClassCounted = value; }
}