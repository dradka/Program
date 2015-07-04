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
public class GoForward {
    public static Node parallelPath(boolean modeStart, int idOfDisease, ArrayList<Edge> parallelEdges, ArrayList<Edge> inListP, 
            Node nP, ArrayList<Edge> listP, ArrayList<String> therapyList, ArrayList<Node> parallelNodes, 
            ArrayList<Integer> parallelPaths, ArrayList<Graph> graphs)
    {
        ArrayList<Edge> inList = inListP;
        ArrayList<Edge> list = listP;
        Node n = nP;
        boolean start = true;
        if(modeStart==false)
        {
            start = false;
        }
        while(parallelPaths.get(idOfDisease)<parallelEdges.size() 
                      &&(start||(inList.size()>1 && n.getAttribute("label").equals("") && list.size()>0)))
        {
            start = false;
            n = parallelEdges.get(parallelPaths.get(idOfDisease)).getTarget().getNode();
            list = GraphFunctions.getOutEdges(graphs.get(idOfDisease), n);
            inList = GraphFunctions.getInEdges(graphs.get(idOfDisease), n);
            if(((list.size()<2)||((list.size()>1)&&(n.getAttribute("label").equals(""))))
               &&!(inList.size()>1 
                            && n.getAttribute("label").equals("") 
                            &&list.size()>0 && parallelNodes.get(idOfDisease)!=null))
            {
              AddToTherapy.addToTherapy(therapyList, n);
            }
            while((list.size()==1) && ((inList.size()==1)||(!n.getAttribute("label").equals(""))))
            {
                n = list.get(0).getTarget().getNode();
                list = GraphFunctions.getOutEdges(graphs.get(idOfDisease), n);
                inList = GraphFunctions.getInEdges(graphs.get(idOfDisease), n);
                if(((list.size()<2)||((list.size()>1)&&(n.getAttribute("label").equals(""))))
                  &&!(inList.size()>1 
                            && n.getAttribute("label").equals("")
                            &&list.size()>0 && parallelNodes.get(idOfDisease)!=null))
                {
                    AddToTherapy.addToTherapy(therapyList, n);
                }
            }
            if((inList.size()>1)&& n.getAttribute("label").equals("")&&(list.size()>0))
            {
                parallelPaths.set(idOfDisease, parallelPaths.get(idOfDisease)+1);
            }
        }
        return n;
    }
    public static void goForward(Node n, int idOfDisease, ArrayList<String> therapyList, ArrayList<Node> parallelNodes, 
            ArrayList<Integer> parallelPaths, ArrayList<Graph> graphs, ArrayList<Node> lastNodes, 
            ArrayList<ArrayList<Node>> parallelNodesHistory, ArrayList<ArrayList<Integer>> parallelPathsHistory)
    {
        ArrayList<Edge> list = GraphFunctions.getOutEdges(graphs.get(idOfDisease), n);
        ArrayList<Edge> inList = GraphFunctions.getInEdges(graphs.get(idOfDisease), n);
        if(((list.size()<2)||((list.size()>1)&&(n.getAttribute("label").equals(""))))
           &&!(inList.size()>1 
                        && n.getAttribute("label").equals("") 
                        &&list.size()>0 && parallelNodes.get(idOfDisease)!=null))
        {
            AddToTherapy.addToTherapy(therapyList, n);
        }
        while(((list.size()>1)&&(n.getAttribute("label").equals("")))
              ||((inList.size()>1)&& n.getAttribute("label").equals("")
                &&(list.size()>0)&&(parallelNodes.get(idOfDisease)!=null))
              ||((list.size()==1) && 
                (inList.size()<=1 || (inList.size()>1 && !n.getAttribute("label").equals(""))
                ||(inList.size()>1 
                    && n.getAttribute("label").equals("")
                    && parallelNodes.get(idOfDisease)==null))))
        {
            while((list.size()==1) && 
                    (inList.size()<=1 || (inList.size()>1 && !n.getAttribute("label").equals(""))
                    ||(inList.size()>1 
                        && n.getAttribute("label").equals("") && 
                            parallelNodes.get(idOfDisease)==null)))
            {
                n = list.get(0).getTarget().getNode();
                list = GraphFunctions.getOutEdges(graphs.get(idOfDisease), n);
                inList = GraphFunctions.getInEdges(graphs.get(idOfDisease), n);
                if(((list.size()<2)||((list.size()>1)&&(n.getAttribute("label").equals(""))))
                  && !(inList.size()>1 
                        && n.getAttribute("label").equals("") 
                        &&list.size()>0  && parallelNodes.get(idOfDisease)!=null))
                {
                    AddToTherapy.addToTherapy(therapyList, n);
                }
            }
            if((inList.size()>1)&& n.getAttribute("label").equals("")&&(list.size()>0)&&(parallelNodes.get(idOfDisease)!=null))
            {
               parallelPaths.set(idOfDisease, parallelPaths.get(idOfDisease)+1);
               ArrayList<Edge> parallelEdges = GraphFunctions.getOutEdges(graphs.get(idOfDisease), 
                       parallelNodes.get(idOfDisease));
               if(parallelPaths.get(idOfDisease)<parallelEdges.size())
               {
                    n = parallelNodes.get(idOfDisease);
                    n = parallelPath(false, idOfDisease, parallelEdges, inList, n, list, therapyList, 
                            parallelNodes, parallelPaths, graphs);
                    list = GraphFunctions.getOutEdges(graphs.get(idOfDisease), n);
                    inList = GraphFunctions.getInEdges(graphs.get(idOfDisease), n);
               }
               else
               {
                   parallelNodes.set(idOfDisease, null);
                   AddToTherapy.addToTherapy(therapyList, n);
               }
            }
            else if((list.size()>1)&&(n.getAttribute("label").equals("")))
            {
                parallelNodes.set(idOfDisease, n);
                parallelPaths.set(idOfDisease, 0);
                ArrayList<Edge> parallelEdges = GraphFunctions.getOutEdges(graphs.get(idOfDisease), n);
                n = parallelPath(true, idOfDisease, parallelEdges, inList, n, list, therapyList, 
                        parallelNodes, parallelPaths, graphs);
                list = GraphFunctions.getOutEdges(graphs.get(idOfDisease), n);
                inList = GraphFunctions.getInEdges(graphs.get(idOfDisease), n);
                if(parallelPaths.get(idOfDisease)==parallelEdges.size())
                {
                    parallelNodes.set(idOfDisease, null);
                    AddToTherapy.addToTherapy(therapyList, n);
                }


            }
        }
        if(list.size()>1)
        {
           lastNodes.set(idOfDisease, n);
        }
        else
        {
           lastNodes.set(idOfDisease, null);
        }
        parallelNodesHistory.get(idOfDisease).add(parallelNodes.get(idOfDisease));
        parallelPathsHistory.get(idOfDisease).add(parallelPaths.get(idOfDisease));
    }
    
}
