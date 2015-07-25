import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import java.util.Scanner;

public class Grapher
{
    //The following four numbers are the range you wish to view on
    static private double minX = -20;
    static private double maxX = 20;
    static private double minY = -20;
    static private double maxY = 20;
    //Higher accuracy means longer but more accurate picture generating
    static private double accuracy = 5;
    //Higher tolerance means less precise drawing
    static private double tolerance = 1.0 / 600.0;
    public static void main(String[] args) throws Exception
    {
        double X,Y;
        System.out.println("Working...");
        BufferedImage image = new BufferedImage(640,640,BufferedImage.TYPE_INT_ARGB);
        for (X = 0; X < 640.0; X += 1.0 / accuracy)
        {
            for (Y = 0; Y < 640.0; Y += 1.0 / accuracy)
            {
                if (f((X / 640.0) * (maxX - minX) + minX,(Y / 640.0) * (maxY - minY) + minY))
                {
                    image.setRGB((int)Math.floor(X),(int)Math.floor(Y),0xFF000000);
                }
            }
        }
        JFrame frame = new JFrame("Your Graph!");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new JLabel(new ImageIcon(image)));
        frame.pack();
        frame.setVisible(true);
    }
    static private Boolean f(double X,double Y)
    {
        double upperLimit1 = R(X + ((maxX - minX) * tolerance), Y - ((maxY - minY) * tolerance));
        double lowerLimit1 = R(X - ((maxX - minX) * tolerance), Y + ((maxY - minY) * tolerance));
        if (((lowerLimit1 <= L(X,Y)) && (L(X,Y) <= upperLimit1)) || ((upperLimit1 <= L(X,Y)) && (L(X,Y) <= lowerLimit1)))
        {
            return true;
        }
        return false;
    }
    /*The following two functions should be edited to accomodate the relationship you wish to view. Order doesn't matter. For
    example, to view the relationship X * Y = X * sin(Y), enter X * Y into the return for the first function and X * Math.sin(Y)
    into the return statement of the second function*/
    static private double R(double X,double Y)
    {
        return -X * Math.sin(X);
    }
    static private double L(double X,double Y)
    {
        return Y * Math.sin(Y);
    }
    /*My favorite relationships:
    Math.cos(X * X + Y * Y) = Math.sin(Math.sqrt(Math.abs(X * Y))) [-50,50]
    Math.sin(X * Y) = Math.cos(Math.pow(X,2) / Y) [-20,20]
    Math.cos(X / Y) * Math.sin(X - Y) = Math.sin(X / Y) [-20,20]
    Math.cos(X - Y + Y / X) = Math.sin(X + Y - Y / X) [-20,20]
    X / Math.cos(X - 2 * Y) = Y / Math.sin(2 * X + Y - Y / X) [-20,20]
    X / Math.cos(X - 2 * Y / X) = Y / Math.sin(2 * X + Y * Y / X)
    -X * Math.sin(X) = Y * Math.sin(Y)
    Y * X * Math.sin(Y) = Y * Math.cos(X * Y)*/
}