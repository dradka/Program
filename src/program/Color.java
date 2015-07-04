/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package program;

import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import java.util.ArrayList;

/**
 *
 * @author Darek
 */
public class Color {
    public static void resetColors(Graph graph)
    {
       for(Node n:graph.getNodes(false))
       {
            n.setAttribute("color", "");
            if(!n.getId().getId().equals("e_start")&&!n.getId().getId().equals("e_end"))
            {
               n.setAttribute("penwidth", "");
            }
       }
       for(Edge e:graph.getEdges())
       {
           e.setAttribute("color", "");
           e.setAttribute("penwidth", "");
       } 
    }
    public static void color(int idOfDisease, Graph graph, ArrayList<String> therapyList)
    {
        for(String elem:therapyList)
        {
            for(Node n:graph.getNodes(false))
            {
                if(elem.indexOf('?')==-1)
                {
                    if(n.getId().getId().equals(elem) || (elem.indexOf('=')>=0 && !(elem.indexOf('?')>=0)
                            && n.getId().getId().equals(elem.substring(0,elem.indexOf('=')))))
                    {
                        n.setAttribute("color", "blue");
                        if(!n.getId().getId().equals("e_start")&&!n.getId().getId().equals("e_end"))
                        {
                            n.setAttribute("penwidth", "2");
                        }
                        ArrayList<Edge> edges = GraphFunctions.getOutEdges(graph, n);
                        for(Edge e:edges)
                        {
                            e.setAttribute("color", "blue");
                            e.setAttribute("penwidth", "2");
                            if(((edges.size()==1)&&(therapyList.indexOf(elem)==therapyList.size()-1))
                               ||(edges.size()>1))
                            {
                                e.getTarget().getNode().setAttribute("color", "blue");
                                e.getTarget().getNode().setAttribute("penwidth", "2");
                            }
                        }
                    }
                }
                else
                {
                    if(n.getId().getId().equals(elem.substring(0,elem.lastIndexOf('?'))))
                    {
                        n.setAttribute("color", "blue");
                        if(!n.getId().getId().equals("e_start")&&!n.getId().getId().equals("e_end"))
                        {
                            n.setAttribute("penwidth", "2");
                        }
                        ArrayList<Edge> edges = GraphFunctions.getOutEdges(graph, n);
                        for(Edge e:edges)
                        {
                            if(e.getAttribute("label").equals(elem.substring(elem.lastIndexOf('?')+1)))
                            {
                                e.setAttribute("color", "blue");
                                e.setAttribute("penwidth", "2");
                                if(therapyList.indexOf(elem)==therapyList.size()-1)
                                {
                                    e.getTarget().getNode().setAttribute("color", "blue");
                                    e.getTarget().getNode().setAttribute("penwidth", "2");
                                }
                            }
                        }
                    }
                }
            }
            
        }
    }
}
