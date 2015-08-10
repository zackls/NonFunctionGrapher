import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.util.InputMismatchException;
import java.util.Scanner;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Grapher
{
    //The following four numbers are the range you wish to view on
    static private float minX = -20;
    static private float maxX = 20;
    static private float minY = -20;
    static private float maxY = 20;
    //Higher accuracy means longer but more accurate picture generating
    static private float accuracy = (float)1;
    //Higher tolerance means less precise drawing
    static private float tolerance = (float)1.0 / 75;
    static private String l;
    static private String r;
    static private ScriptEngineManager manager = new ScriptEngineManager();
    static private ScriptEngine engine = manager.getEngineByName("js");
    static private boolean userInput = true;
    public static void main(String[] args) throws Exception
    {
        Scanner s = new Scanner(System.in);
        System.out.println("\nWelcome to my non-function grapher, which graphs the relationship between two equations F(X, Y) and G(X, Y). You will be able to select whether you'd like to enter your "
                + "own equations, or allow the program to graph the programmed one. To edit the programmed equation, you must directly edit the code. The program works more slowly with user input "
                + "(it takes about 30 seconds for even a simple relationship)\n");

        while (true)
        {
            boolean cont = false;
            while (!cont)
            {
                System.out.println("Hit Q to quit, S to change graph settings, or H for help.");
                System.out.print("Would you like to input your own equations? (Y/N/Q/S/H) ");
                String mode = s.next();
                if (mode.equals("Q") || mode.equals("q"))
                {
                    System.out.println("Goodbye!");
                    break;
                }
                else if (mode.equals("Y") || mode.equals("y"))
                {
                    userInput = true;
                    cont = true;
                }
                else if (mode.equals("N") || mode.equals("n"))
                {
                    userInput = false;
                    cont = true;
                }
                else if (mode.equals("S") || mode.equals("s"))
                {
                    String[] settings = {"Min X (cur = " + minX + "): ", "Max X (cur = " + maxX + "): ", "Min Y (cur = " + minY + "): ", "Max Y (cur = " + maxY + "): ",
                                            "Accuracy (longer but more accurate drawing, cur = " + accuracy + "): "};
                    float[] newSettings = new float[5];
                    for (int i = 0; i < settings.length; i++)
                    {
                        try 
                        {
                            System.out.print(settings[i]);
                            if (i == 0)
                            {
                                minX = s.nextFloat();
                            }
                            else if (i == 1)
                            {
                                maxX = s.nextFloat();
                            }
                            else if (i == 2)
                            {
                                minY = s.nextFloat();
                            }
                            else if (i == 3)
                            {
                                maxY = s.nextFloat();
                            }
                            else if (i == 4)
                            {
                                accuracy = s.nextFloat();
                            }
                        }
                        catch (InputMismatchException e)
                        {
                            System.out.println("Invalid input");
                            s.next();
                            i--;
                        }
                    }
                    System.out.println();
                }
                else if (mode.equals("H") || mode.equals("h"))
                {
                    System.out.println("\nAll non-elementary expressions like tangent, power(^), and even absolute value must be written using the 'Math' class so that they may be parsed correctly, "
                        + "so tangent(X) would be written as Math.tan(X), |X| would be Math.abs(X), and X^2 would be Math.pow(X,2). So the expression X * sin(Y^X) would be written as "
                        + "X*Math.sin(Math.pow(Y,X)). Avoid using spaces.\n");
                }
                else
                {
                    System.out.println("Please enter a Y, N, Q, or H.");
                }
            }
            if (!cont)
            {
                break;
            }

            long startTime = System.nanoTime();
            if (userInput)
            {
                System.out.print("F(X, Y) = ");
                l = s.next();
                System.out.print("G(X, Y) = ");
                r = s.next();
                System.out.println();
            }
            else
            {
                l = "Math.cos(X*X+Y*Y)";
                r = "Math.sin(Math.sqrt(Math.abs(X*Y)))";
                System.out.println("F(X, Y) = " + l);
                System.out.println("G(X, Y) = " + r);
            }

            try
            {
                float X, Y;
                System.out.println("GRAPHING: " + l + " = " + r);
                BufferedImage image = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
                for (X = 0; X < 500; X += 1.0 / accuracy)
                {
                    if (((int)Math.floor(X * accuracy)) % 5 == 4)
                    {
                        System.out.print(".");
                    }
                    for (Y = 0; Y < 500; Y += 1.0 / accuracy)
                    {
                        if (areEqual((X / (float)500.0) * (maxX - minX) + minX,(Y / (float)500.0) * (maxY - minY) + minY))
                        {
                            image.setRGB((int)Math.floor(X), (int)Math.floor(Y), 0xFF000000);
                        }
                    }
                }
                JFrame frame = new JFrame(l + " = " + r);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.add(new JLabel(new ImageIcon(image)));
                frame.pack();
                frame.setVisible(true);
                System.out.println("\nGraphing took: " + ((System.nanoTime() - startTime) / 1000000000) + " seconds\n");
            }
            catch (ScriptException e)
            {
                System.out.println("Invalid input. If you're having trouble with correct input, press H.");
            }
        }
    }
    static private Boolean areEqual(float X, float Y) throws ScriptException
    {
        float leftValue = L(X, Y);
        float rightValue = R(X, Y);
        float upperLimit = leftValue - ((maxX - minX) * tolerance / 2 + (maxY - minY) * tolerance / 2);
        float lowerLimit = leftValue + ((maxX - minX) * tolerance / 2 + (maxY - minY) * tolerance / 2);
        if (((lowerLimit <= rightValue) && (rightValue <= upperLimit)) || ((upperLimit <= rightValue) && (rightValue <= lowerLimit)))
        {
            return true;
        }
        return false;
    }
    /*The following two functions should be edited to accomodate the pre-programmed relationship you wish to view. Order doesn't matter. For
    example, to view the relationship X * Y = X * sin(Y), enter X * Y into the return for the first function and X * Math.sin(Y)
    into the return statement of the second function*/
    static private float L(float X, float Y) throws ScriptException
    {
        if (!userInput)
        {
            //pre-programmed relationship
            return (float)Math.cos(X * X + Y * Y);
        }
        Bindings binds = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        binds.put("X", X);
        binds.put("Y", Y);
        binds.put("x", X);
        binds.put("y", Y);
        return ((Double)engine.eval(l, binds)).floatValue();
    }
    static private float R(float X, float Y) throws ScriptException
    {
        if (!userInput)
        {
            //pre-programmed relationship
            return (float)Math.sin(Math.sqrt(Math.abs(X * Y)));
        }
        Bindings binds = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        binds.put("X", X);
        binds.put("Y", Y);
        binds.put("x", X);
        binds.put("y", Y);
        return ((Double)engine.eval(r, binds)).floatValue();
    }
    /*My favorite relationships:
    Math.cos(X * X + Y * Y) = Math.sin(Math.sqrt(Math.abs(X * Y))) [-50,50]
    Math.sin(X * Y) = Math.cos(Math.pow(X, 2) / Y) [-20,20]
    Math.cos(X / Y) * Math.sin(X - Y) = Math.sin(X / Y) [-20,20]
    Math.cos(X - Y + Y / X) = Math.sin(X + Y - Y / X) [-20,20]
    X / Math.cos(X - 2 * Y) = Y / Math.sin(2 * X + Y - Y / X) [-20,20]
    X / Math.cos(X - 2 * Y / X) = Y / Math.sin(2 * X + Y * Y / X)
    -X * Math.sin(X) = Y * Math.sin(Y)
    Y * X * Math.sin(Y) = Y * Math.cos(X * Y)*/
}