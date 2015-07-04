/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package program;

import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 *
 * @author Darek
 */
public class GraphFunctions {
    public static Node getStartNode(Graph graph)
    {
        for(Node n:graph.getNodes(false))
        {
            boolean hasInEdge = false;
            for(Edge e:graph.getEdges())
            {
                if(e.getTarget().getNode()==n)
                {
                    hasInEdge = true;
                }
            }
            if(!hasInEdge)
            {
                return n;
            }
        }
        return null;
    }
    public static ArrayList<Edge> getInEdges(Graph graph, Node n)
    {
        ArrayList<Edge> list = new ArrayList<Edge>();
        for(Edge e:graph.getEdges())
        {
           if(e.getTarget().getNode()==n)
           {
               list.add(e);
           }
        }
        return list;
    }
    public static ArrayList<Edge> getOutEdges(Graph graph, Node n)
    {
        ArrayList<Edge> list = new ArrayList<Edge>();
        for(Edge e:graph.getEdges())
        {
           if(e.getSource().getNode()==n)
           {
               list.add(e);
           }
        }
        return list;
    }
    public static Graph getGraph(String path)
    {
         FileReader in=null;        
        
        try {
            File f = new File( path );
            in = new FileReader(f);
            Parser p = new Parser();
            Boolean b = p.parse(in);
            Graph graph =p.getGraphs().get(0);
            for(Edge e:graph.getEdges())
            {
                e.setType(Graph.DIRECTED);
            }
            return graph;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }     
        return null;
    }
}
