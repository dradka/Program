/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package program;

import com.alexmerz.graphviz.objects.Graph;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author Darek
 */
public class ImageGraph {
    public static boolean newImageGraph(int id, String name, Graph graph, JLabel jLabel1, ArrayList<String> selectedDiseases)
    {
        try 
        {
            String current = selectedDiseases.get(id);
            try(BufferedWriter output = new BufferedWriter(new FileWriter(new File("grafy/"+name+".dot")))) {
                String graphToString = graph.toString();
                output.write(graphToString.substring(0, graphToString.indexOf(';')));
                output.write("\n");
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader("algorytmy/"+current+".dot"))) 
                {
                    String textLine = bufferedReader.readLine();
                    while(textLine!=null)
                    {
                        textLine = textLine.replaceAll("\\s+","");
                        if(textLine.startsWith("node")||textLine.startsWith("edge"))
                        {
                            output.write(textLine);
                        }
                        textLine = bufferedReader.readLine();
                    }
                }
                output.write(graphToString.substring(graphToString.indexOf(';')+1));
            }
            BufferedImage image = ImageIO.read(new File(getImageGraphPath(name)));
            if(image.getWidth()>900 || image.getHeight()>900)
            {
                Image newImage = image.getScaledInstance(image.getWidth()*2/3, 
                            image.getHeight()*2/3, java.awt.Image.SCALE_SMOOTH);
                ImageIcon imageIcon = new ImageIcon(newImage);
                jLabel1.setIcon(imageIcon);
                return true;
            }
            else
            {
                if(image.getWidth()>image.getHeight())
                {
                    Image newImage = image.getScaledInstance(jLabel1.getWidth(), 
                            jLabel1.getWidth()*image.getHeight()/image.getWidth(), java.awt.Image.SCALE_SMOOTH);
                    ImageIcon imageIcon = new ImageIcon(newImage);
                    jLabel1.setIcon(imageIcon);
                }
                else
                {
                    Image newImage = image.getScaledInstance(jLabel1.getHeight()*image.getWidth()/image.getHeight(), 
                            jLabel1.getHeight(), java.awt.Image.SCALE_SMOOTH);
                    ImageIcon imageIcon = new ImageIcon(newImage);
                    jLabel1.setIcon(imageIcon);
                }
                return false;
            }
        } catch (IOException ex) {
            Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    public static String getImageGraphPath(String name)
    {
        try 
        {
            String directory = "grafy";
            String[] args = {"D:/Pobrane/graphviz-2.38/release/bin/dot.exe", "-Tpng",
                (new File(directory+"/"+name+".dot")).getAbsolutePath(), "-o",
                (new File("grafy/"+name+".png")).getAbsolutePath()};
            Runtime rt = Runtime.getRuntime();
            Process p = rt.exec(args);
            p.waitFor();
            return "grafy/"+name+".png";
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (InterruptedException ex) 
        {
                Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
