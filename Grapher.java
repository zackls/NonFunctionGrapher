import java.applet.Applet;

import java.security.AccessController;
import java.security.PrivilegedAction;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.TextField;

import java.awt.event.ActionListener;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.MaskFormatter;

public class Grapher extends Applet
{
    //width/height of the image
    static public float width = 450;
    static public float height = 450;
    //The following four numbers are the range you wish to view on
    public float minX;
    public float maxX;
    public float minY;
    public float maxY;
    //Higher accuracy means longer but more accurate picture generating
    static public float accuracy = 1;
    //Higher tolerance means less precise drawing
    static public float tolerance = (float)1.0 / 300;
    public String l;
    public String r;

    public ScriptEngineManager manager = new ScriptEngineManager();
    public ScriptEngine engine = manager.getEngineByName("js");

    public JButton graphButton = new JButton("Graph!");
    public TextField leftText = new TextField("-X * Math.sin(X)", 25);
    public TextField rightText = new TextField("Y * Math.sin(Y)", 25);
    public TextField maxXText = new TextField("10.0", 7);
    public TextField minXText = new TextField("-10.0", 7);
    public TextField maxYText = new TextField("10.0", 7);
    public TextField minYText = new TextField("-10.0", 7);
    public JLabel leftLabel = new JLabel("f(x, y)");
    public JLabel rightLabel = new JLabel("g(x, y)");
    public JLabel bottomEqualsLabel = new JLabel("=");
    public JLabel topEqualsLabel = new JLabel("=");
    public JLabel maxXLabel = new JLabel("Max x:");
    public JLabel minXLabel = new JLabel("Min x:");
    public JLabel maxYLabel = new JLabel("Max y:");
    public JLabel minYLabel = new JLabel("Min y:");
    public JLabel graphingText = new JLabel("Graphing");

    public static Grapher instance = new Grapher();

    public JLabel graphedImage = new JLabel(new ImageIcon(new BufferedImage((int)width, (int)height, BufferedImage.TYPE_INT_ARGB)));

    public String error = "";

    public static JFrame frame = new JFrame();

    public static void main(String[] args)
    {
        frame.getContentPane().add(instance);
        frame.setSize(500, 650);
        run(instance);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.revalidate();
        frame.repaint();
        frame.setVisible(true);
    }

    public static void run(Applet applet)
    {
        applet.init();
        applet.start();
    }

    @Override
    public void init()
    {
        AccessController.doPrivileged(new PrivilegedAction<Object>()
        {
            public Object run()
            {
                frame.getContentPane().add(instance);
                frame.setSize(500, 650);

                //min and max X fields
                JPanel horizontalX = new JPanel();
                horizontalX.setLayout(new BoxLayout(horizontalX, BoxLayout.LINE_AXIS));
                horizontalX.add(minXLabel);
                horizontalX.add(Box.createRigidArea(new Dimension(10, 0)));
                horizontalX.add(minXText);
                horizontalX.add(Box.createHorizontalGlue());
                horizontalX.add(maxXLabel);
                horizontalX.add(Box.createRigidArea(new Dimension(10, 0)));
                horizontalX.add(maxXText);

                //min and max Y fields
                JPanel horizontalY = new JPanel();
                horizontalY.setLayout(new BoxLayout(horizontalY, BoxLayout.LINE_AXIS));
                horizontalY.add(minYLabel);
                horizontalY.add(Box.createRigidArea(new Dimension(10, 0)));
                horizontalY.add(minYText);
                horizontalY.add(Box.createHorizontalGlue());
                horizontalY.add(maxYLabel);
                horizontalY.add(Box.createRigidArea(new Dimension(10, 0)));
                horizontalY.add(maxYText);

                //fucntion labels
                JPanel horizontalLabels = new JPanel();
                horizontalLabels.setLayout(new BoxLayout(horizontalLabels, BoxLayout.LINE_AXIS));
                horizontalLabels.add(leftLabel);
                horizontalLabels.add(Box.createHorizontalGlue());
                horizontalLabels.add(topEqualsLabel);
                horizontalLabels.add(Box.createHorizontalGlue());
                horizontalLabels.add(rightLabel);

                //left and right function fields
                JPanel horizontalFuncs = new JPanel();
                horizontalFuncs.setLayout(new BoxLayout(horizontalFuncs, BoxLayout.LINE_AXIS));
                horizontalFuncs.add(Box.createHorizontalGlue());
                horizontalFuncs.add(leftText);
                horizontalFuncs.add(Box.createRigidArea(new Dimension(10, 0)));
                horizontalFuncs.add(bottomEqualsLabel);
                horizontalFuncs.add(Box.createRigidArea(new Dimension(10, 0)));
                horizontalFuncs.add(rightText);
                horizontalFuncs.add(Box.createHorizontalGlue());

                //graph container
                JPanel graph = new JPanel();
                graph.setLayout(new BoxLayout(graph, BoxLayout.LINE_AXIS));
                graph.add(graphedImage);

                //vertical column displays everything
                JPanel verticalColumn = new JPanel();
                verticalColumn.setLayout(new BoxLayout(verticalColumn, BoxLayout.PAGE_AXIS));
                verticalColumn.add(horizontalX);
                verticalColumn.add(Box.createRigidArea(new Dimension(0, 5)));
                verticalColumn.add(horizontalY);
                verticalColumn.add(Box.createRigidArea(new Dimension(0, 10)));
                verticalColumn.add(horizontalLabels);
                verticalColumn.add(Box.createRigidArea(new Dimension(0, 5)));
                verticalColumn.add(horizontalFuncs);
                verticalColumn.add(Box.createRigidArea(new Dimension(0, 10)));
                verticalColumn.add(graphButton);
                verticalColumn.add(Box.createVerticalGlue());
                verticalColumn.add(graph);

                add(verticalColumn);

                graphButton.addActionListener(e -> 
                {
                    graph.remove(graphedImage);
                    graph.add(graphingText);

                    //grab variables from text fields
                    instance.l = leftText.getText();
                    instance.r = rightText.getText();
                    ActionListener thisAction = graphButton.getActionListeners()[0];
                    if (thisAction != null)
                    {
                        //disable button while graphing
                        graphButton.removeActionListener(thisAction);
                    }
                    try
                    {
                        //parse bounds
                        instance.minX = ((Double)engine.eval(minXText.getText())).floatValue();
                        instance.maxX = ((Double)engine.eval(maxXText.getText())).floatValue();
                        instance.minY = ((Double)engine.eval(minYText.getText())).floatValue();
                        instance.maxY = ((Double)engine.eval(maxYText.getText())).floatValue();

                        //graph user input
                        if (!graph())
                        {
                            //invalid input, read error. possible errors: l, r, x, y
                        }
                    }
                    catch(ScriptException err)
                    {
                        //invalid input in the max/min fields
                    }
                    error = "";

                    //re-enable button
                    graphButton.addActionListener(thisAction);

                    graph.remove(graphingText);
                    graph.add(graphedImage);
                });

                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.revalidate();
                frame.repaint();
                frame.setVisible(true);

                return null;
            }
        });
    }

    public boolean graph()
    {
        try
        {
            if (maxX <= minX)
            {
                error = "x";
                return false;
            }
            if (maxY <= minY)
            {
                error = "y";
                return false;
            }
            float X, Y;
            System.out.println("GRAPHING: " + l + " = " + r);
            BufferedImage image = new BufferedImage((int)width, (int)height, BufferedImage.TYPE_INT_ARGB);
            for (X = 0; X < width; X += 1.0 / accuracy)
            {
                if (((int)Math.floor(X * accuracy)) % 5 == 4)
                {
                    System.out.print(".");
                    if ((((int)Math.floor(X * accuracy)) / 5) % 4 == 0)
                    {
                        graphingText.setText("Graphing");
                    }
                    else
                    {
                        graphingText.setText(graphingText.getText() + ".");
                    }
                }
                for (Y = 0; Y < height; Y += 1.0 / accuracy)
                {
                    if (areEqual((X / width) * (maxX - minX) + minX,(Y / height) * (maxY - minY) + minY))
                    {
                        image.setRGB((int)Math.floor(X), (int)Math.floor(Y), 0xFF000000);
                    }
                }
            }
            graphedImage = new JLabel(new ImageIcon(image));
            return true;
        }
        catch (ScriptException e)
        {
            return false;
        }
    }
    public Boolean areEqual(float X, float Y) throws ScriptException
    {
        float leftValue = -L(X, Y);
        float rightValue = R(X, Y);
        float adjustedTolerance = (maxY - minY + maxX - minX) * tolerance / 2;
        float upperLimit = leftValue - adjustedTolerance;
        float lowerLimit = leftValue + adjustedTolerance;
        if (((lowerLimit <= rightValue) && (rightValue <= upperLimit)) || ((upperLimit <= rightValue) && (rightValue <= lowerLimit)))
        {
            return true;
        }
        return false;
    }
    /*The following two functions should be edited to accomodate the pre-programmed relationship you wish to view. Order doesn't matter. For
    example, to view the relationship X * Y = X * sin(Y), enter X * Y into the return for the first function and X * Math.sin(Y)
    into the return statement of the second function*/
    public float L(float X, float Y) throws ScriptException
    {
        Bindings binds = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        binds.put("X", X);
        binds.put("Y", Y);
        binds.put("x", X);
        binds.put("y", Y);
        try
        {
            return (float)engine.eval(l, binds);
        }
        catch (ScriptException e)
        {
            error = "l";
            throw e;
        }
        catch (ClassCastException e)
        {
            return ((Double)engine.eval(l, binds)).floatValue();
        }
    }
    public float R(float X, float Y) throws ScriptException
    {
        Bindings binds = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        binds.put("X", X);
        binds.put("Y", Y);
        binds.put("x", X);
        binds.put("y", Y);
        try
        {
            return (float)engine.eval(r, binds);
        }
        catch (ScriptException e)
        {
            error = "r";
            throw e;
        }
        catch (ClassCastException e)
        {
            return ((Double)engine.eval(r, binds)).floatValue();
        }
    }

    private void oldMain(String[] args) throws Exception
    {
        double X,Y;
        System.out.println("Working...");
        BufferedImage image = new BufferedImage(640,640,BufferedImage.TYPE_INT_ARGB);
        for (X = 0; X < 640.0; X += 1.0 / accuracy)
        {
            for (Y = 0; Y < 640.0; Y += 1.0 / accuracy)
            {
                if (oldf((X / 640.0) * (maxX - minX) + minX,(Y / 640.0) * (maxY - minY) + minY))
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
    private Boolean oldf(double X,double Y)
    {
        double leftValue = oldL(X,Y);
        double upperLimit1 = oldR(X + ((maxX - minX) * tolerance), Y - ((maxY - minY) * tolerance));
        double lowerLimit1 = oldR(X - ((maxX - minX) * tolerance), Y + ((maxY - minY) * tolerance));
        if (((lowerLimit1 <= leftValue) && (leftValue <= upperLimit1)) || ((upperLimit1 <= leftValue) && (leftValue <= lowerLimit1)))
        {
            return true;
        }
        return false;
    }
    /*The following two functions should be edited to accomodate the relationship you wish to view. Order doesn't matter. For
    example, to view the relationship X * Y = X * sin(Y), enter X * Y into the return for the first function and X * Math.sin(Y)
    into the return statement of the second function*/
    private double oldR(double X,double Y)
    {
        return -X * Math.sin(X);
    }
    private double oldL(double X,double Y)
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