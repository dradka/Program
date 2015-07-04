/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package program;

import com.alexmerz.graphviz.objects.Node;
import java.util.ArrayList;

/**
 *
 * @author Darek
 */
public class AddToTherapy {
    public static void addToTherapy(ArrayList<String> therapy, Node n)
    {
        if(n.getAttribute("label").matches(".*\\[[0-9]+\\]$"))
        {
            String label = n.getAttribute("label");
            int value = Integer.parseInt(label.substring(label.lastIndexOf('[')+1, 
                    label.lastIndexOf(']')));
            therapy.add(n.getId().getId()+"="+String.valueOf(value));
        }
        else
        {
            therapy.add(n.getId().getId());
        }
    }
}
