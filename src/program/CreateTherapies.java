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
public class CreateTherapies {
   public static ArrayList<ArrayList<String>> parallelCreateTherapies(int k, ArrayList<ArrayList<String>> listOfTherapies, Node globalNP, 
           ArrayList<Node> parallelNodes, ArrayList<Integer> parallelPaths, ArrayList<Graph> graphs)
    {
         Node globalN = globalNP;
         ArrayList<Edge> parallelEdges = GraphFunctions.getOutEdges(graphs.get(k), parallelNodes.get(k));
         ArrayList<Edge> inList = null;
         ArrayList<Edge> list = null;
         while(parallelPaths.get(k)<parallelEdges.size())
         {

            globalN = parallelEdges.get(parallelPaths.get(k)).getTarget().getNode();
            list = GraphFunctions.getOutEdges(graphs.get(k), globalN);
            inList = GraphFunctions.getInEdges(graphs.get(k), globalN);
            if(((list.size()<2)||((list.size()>1)&&(globalN.getAttribute("label").equals(""))))
               &&!(inList.size()>1 
                            && globalN.getAttribute("label").equals("") 
                            &&list.size()>0 && parallelNodes.get(k)!=null))
            {

                for(ArrayList<String> t:listOfTherapies)
                {
                    AddToTherapy.addToTherapy(t, globalN);
                }

            }
            while((list.size()==1) && ((inList.size()==1)||(!globalN.getAttribute("label").equals(""))))
            {
                globalN = list.get(0).getTarget().getNode();
                list = GraphFunctions.getOutEdges(graphs.get(k), globalN);
                inList = GraphFunctions.getInEdges(graphs.get(k), globalN);
                if(((list.size()<2)||((list.size()>1)&&(globalN.getAttribute("label").equals(""))))
                  &&!(inList.size()>1 
                            && globalN.getAttribute("label").equals("")
                            &&list.size()>0 && parallelNodes.get(k)!=null))
                {

                    for(ArrayList<String> t:listOfTherapies)
                    {
                        AddToTherapy.addToTherapy(t, globalN);
                    }

                }
            }
            if(list.size()>1 && !((inList.size()>1)&& globalN.getAttribute("label").equals("")))
            {
                ArrayList<ArrayList<String>> parallelNewTherapies = new ArrayList<ArrayList<String>>();
                Node tempN = globalN;
                for(Edge e:list)
                {
                    for(ArrayList<String> t:listOfTherapies)
                    {
                        ArrayList<String> newTherapy = new ArrayList<String>();

                        for(String id:t)
                        {
                            newTherapy.add(id);
                        }
                        newTherapy.add(tempN.getId().getId()+"?"+e.getAttribute("label"));
                        globalN = e.getTarget().getNode();
                        ArrayList<ArrayList<String>> newTherapies = createTherapies(k, newTherapy, globalN, 
                                graphs, parallelNodes, parallelPaths);
                        for(ArrayList<String> t2:newTherapies)
                        {
                            parallelNewTherapies.add(t2);
                        } 
                    }
                }

                listOfTherapies = parallelNewTherapies;
            }
            parallelPaths.set(k, parallelPaths.get(k)+1);

        }
        parallelNodes.set(k, null);
        ArrayList<ArrayList<String>> newListOfTherapies = new ArrayList<ArrayList<String>>();
        Node tempN = globalN;
        for(ArrayList<String> t:listOfTherapies)
        {
            globalN = tempN;
            ArrayList<String> newTherapy = new ArrayList<String>();
            for(String id:t)
            {
                newTherapy.add(id);
            }
            ArrayList<ArrayList<String>> newTherapies = createTherapies(k, newTherapy, globalN, 
                    graphs, parallelNodes, parallelPaths);
            for(ArrayList<String> t2:newTherapies)
            {
                newListOfTherapies.add(t2);
            }
        }
        return newListOfTherapies;
    }
    public static ArrayList<ArrayList<String>> createTherapies(int k, ArrayList<String> therapy, Node globalNP, 
            ArrayList<Graph> graphs, ArrayList<Node> parallelNodes, ArrayList<Integer> parallelPaths)
    {
        Node globalN = globalNP;
        ArrayList<ArrayList<String>> returnedListOfTherapies = new ArrayList<ArrayList<String>>();
        Graph graph = graphs.get(k);
        ArrayList<Edge> list = GraphFunctions.getOutEdges(graph, globalN);
        ArrayList<Edge> inList = GraphFunctions.getInEdges(graph, globalN);
        if(((list.size()<2)||((list.size()>1)&&(globalN.getAttribute("label").equals(""))))
                &&!(inList.size()>1 
                            && globalN.getAttribute("label").equals("") 
                            &&list.size()>0 && parallelNodes.get(k)!=null))
        {
            AddToTherapy.addToTherapy(therapy, globalN);
        }
        while((list.size()==1)&&((inList.size()<=1)
                ||!globalN.getAttribute("label").equals("")||(parallelNodes.get(k)==null)))
        {
            globalN = list.get(0).getTarget().getNode();
            list = GraphFunctions.getOutEdges(graph, globalN);
            inList = GraphFunctions.getInEdges(graph, globalN);
            if(((list.size()<2)||((list.size()>1)&&(globalN.getAttribute("label").equals(""))))
                &&!(inList.size()>1 
                            && globalN.getAttribute("label").equals("") 
                            &&list.size()>0 && parallelNodes.get(k)!=null ))
            {
                AddToTherapy.addToTherapy(therapy, globalN);
            }
        }
        if(list.isEmpty()||(inList.size()>1 && globalN.getAttribute("label").equals("") && list.size()>0
                                    &&(parallelNodes.get(k)!=null)))
        {
            returnedListOfTherapies.add(therapy);
        }
        else if(list.size()>1)
        {
            if(globalN.getAttribute("label").equals(""))
            {
                ArrayList<ArrayList<String>> listOfTherapies = new ArrayList<ArrayList<String>>();
                listOfTherapies.add(therapy);
                parallelNodes.set(k, globalN);
                parallelPaths.set(k, 0);
                listOfTherapies = parallelCreateTherapies(k, listOfTherapies, globalN, 
                        parallelNodes, parallelPaths, graphs);
                returnedListOfTherapies = listOfTherapies;
            }
            else
            {
                Node tempN = globalN;
                for(Edge e:GraphFunctions.getOutEdges(graph, globalN))
                {
                    globalN = e.getTarget().getNode();
                    ArrayList<String> newTherapy = new ArrayList<String>();
                    for(String id:therapy)
                    {
                        newTherapy.add(id);
                    }
                    if(!tempN.getAttribute("label").equals(""))
                    {
                      newTherapy.add(tempN.getId().getId()+"?"+e.getAttribute("label"));
                    }
                    ArrayList<ArrayList<String>> newTherapies = createTherapies(k, newTherapy, 
                            globalN, graphs, parallelNodes, parallelPaths);
                    for(ArrayList<String> t:newTherapies)
                    {
                        returnedListOfTherapies.add(t);
                    }
                }
            }
        }
        return returnedListOfTherapies;
    } 
}
