/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package program;

import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Id;
import com.alexmerz.graphviz.objects.Node;
import com.alexmerz.graphviz.objects.PortNode;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

/**
 *
 * @author Darek
 */
public class Results {
    public static String getNodeLabel(String id)
    {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("konflikty/nazwy.txt"))) 
        {
            String textLine = bufferedReader.readLine();
            while(textLine != null)
            {
                String idStr = textLine.substring(0,textLine.indexOf(':'));
                String labelStr = textLine.substring(textLine.indexOf(':')+1);
                if(idStr.equals(id))
                {
                    return labelStr;
                }
                textLine = bufferedReader.readLine();
            }
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public static void setGraphs(ArrayList<ArrayList<String[]>> executedInteractions, ArrayList<ArrayList<ArrayList<String>>> therapies, 
            JTabbedPane graphsResultsTabbedPane, ArrayList<String> selectedDiseases)
    {
        graphsResultsTabbedPane.removeAll();
        int counter = 1;
        for(ArrayList<ArrayList<String>> therapiesSolution:therapies)
        {
            JTabbedPane solutionTabbedPane = new JTabbedPane();
            for(ArrayList<String> therapy:therapiesSolution)
            {
                int i = therapiesSolution.indexOf(therapy);
                String current = selectedDiseases.get(i);
                Graph graph = GraphFunctions.getGraph("algorytmy/"+current+".dot");
                for(ArrayList<String[]> interactions:executedInteractions)
                {
                    for(String[] interaction:interactions)
                    {
                        if(interaction[0].equals("replace"))
                        {
                            boolean onPath = false;
                            for(String s:therapy)
                            {
                                if(s.equals(interaction[1])
                                   ||(s.indexOf('=')>=0 && !(s.indexOf('?')>=0)
                                        && s.substring(0,s.indexOf('=')).equals(interaction[1])))
                                {
                                    onPath = true;
                                }
                            }
                            if(onPath)
                            {
                                Node replaceNode = null;
                                for(Node n:graph.getNodes(false))
                                {
                                    if(n.getId().getId().equals(interaction[1]))
                                    {
                                        String label = getNodeLabel(interaction[3]);
                                        if(label!=null)
                                        {
                                            n.setAttribute("label", label);
                                        }
                                        else
                                        {
                                            n.setAttribute("label", interaction[3]);
                                        }
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
                                        }
                                    }
                                }
                            }
                        }
                        else if(interaction[0].equals("add"))
                        {
                            boolean onPath = false;
                            for(String s:therapy)
                            {
                                if(s.equals(interaction[3])
                                   ||(s.indexOf('=')>=0 && !(s.indexOf('?')>=0)
                                        && s.substring(0,s.indexOf('=')).equals(interaction[3])))
                                {
                                    onPath = true;
                                }
                            }
                            if(onPath)
                            {
                                Node n = null;
                                for(Node node:graph.getNodes(false))
                                {
                                    if((interaction[3].indexOf('?')>=0
                                            && node.getId().getId().equals(
                                                    interaction[3].substring(0, interaction[3].lastIndexOf('?')))
                                            &&interaction[2].equals("after"))
                                       ||node.getId().getId().equals(interaction[3]))
                                    {
                                       n = node;
                                    }
                                }
                                if(n!=null)
                                {
                                    Node newNode = new Node();
                                    Id id = new Id();
                                    id.setId(interaction[1]);
                                    newNode.setId(id);
                                    String label = getNodeLabel(interaction[1]);
                                    if(label!=null)
                                    {
                                        newNode.setAttribute("label", label);
                                    }
                                    else
                                    {
                                        newNode.setAttribute("label", interaction[1]);
                                    }
                                    graph.addNode(newNode);
                                    if(interaction[3].indexOf('?')>=0)
                                    {   
                                       ArrayList<Edge> edges = GraphFunctions.getOutEdges(graph, n); 
                                       for(Edge e:edges)
                                       {
                                           if(e.getAttribute("label").equals(
                                                   interaction[3].substring(interaction[3].lastIndexOf('?')+1)))
                                           {
                                               Node targetNode = e.getTarget().getNode();
                                               e.setTarget(new PortNode(newNode));
                                               Edge newEdge = new Edge(new PortNode(newNode), new PortNode(targetNode), 
                                                       Graph.DIRECTED);
                                               graph.addEdge(newEdge);
                                               newNode.setAttribute("color", "blue");
                                               if(!newNode.getId().getId().equals("e_start")
                                                       &&!newNode.getId().getId().equals("e_end"))
                                               {
                                                  newNode.setAttribute("penwidth", "2");
                                               }
                                               newEdge.setAttribute("color", "blue");
                                               newEdge.setAttribute("penwidth", "2");
                                           }
                                       }
                                    }
                                    else
                                    {
                                        if(interaction[2].equals("after"))
                                        {
                                           ArrayList<Edge> edges = GraphFunctions.getOutEdges(graph, n);
                                           if(edges.size()>0)
                                           {
                                                Edge e = edges.get(0);
                                                e.setSource(new PortNode(newNode));
                                                Edge newEdge = new Edge(new PortNode(n), new PortNode(newNode), Graph.DIRECTED);
                                                graph.addEdge(newEdge);
                                                newNode.setAttribute("color", "blue");
                                                if(!newNode.getId().getId().equals("e_start")
                                                        &&!newNode.getId().getId().equals("e_end"))
                                                {
                                                   newNode.setAttribute("penwidth", "2");
                                                }
                                                e.setAttribute("color", "blue");
                                                e.setAttribute("penwidth", "2");
                                           }
                                        }
                                        else if(interaction[2].equals("before"))
                                        {
                                           ArrayList<Edge> edges = GraphFunctions.getInEdges(graph, n);
                                           for(Edge e:edges)
                                           {
                                               e.setTarget(new PortNode(newNode));
                                           }
                                           Edge newEdge = new Edge(new PortNode(newNode), new PortNode(n), Graph.DIRECTED);
                                           graph.addEdge(newEdge);
                                           newNode.setAttribute("color", "blue");
                                           if(!newNode.getId().getId().equals("e_start")
                                                   &&!newNode.getId().getId().equals("e_end"))
                                           {
                                              newNode.setAttribute("penwidth", "2");
                                           }
                                           newEdge.setAttribute("color", "blue");
                                           newEdge.setAttribute("penwidth", "2");
                                        }                         
                                    }
                                }
                            }
                        }
                        else if(interaction[0].equals("remove"))
                        {
                            boolean onPath = false;
                            for(String s:therapy)
                            {
                                if(s.equals(interaction[1])
                                   ||(s.indexOf('=')>=0 && !(s.indexOf('?')>=0)
                                        && s.substring(0,s.indexOf('=')).equals(interaction[1])))
                                {
                                    onPath = true;
                                }
                            }
                            if(onPath)
                            {
                                for(Node n:graph.getNodes(false))
                                {
                                    if(n.getId().getId().equals(interaction[1]))
                                    {
                                        n.setAttribute("style", "invis");
                                        n.setAttribute("fixedsize", "true");
                                        n.setAttribute("width","0");
                                        n.setAttribute("height","0");

                                    }
                                }
                            }
                        }
                        else if(interaction[0].equals("increase_dosage") || interaction[0].equals("decrease_dosage")
                                ||interaction[0].equals("change_dosage"))
                        {
                            boolean onPath = false;
                            for(String s:therapy)
                            {
                                if(s.indexOf('=')>=0 && !(s.indexOf('?')>=0)
                                        && s.substring(0, s.indexOf('=')).equals(interaction[1]))
                                {
                                    onPath = true;
                                }
                            }
                            if(onPath)
                            {
                                for(Node n:graph.getNodes(false))
                                {
                                    if(n.getId().getId().equals(interaction[1]))
                                    {
                                        String label = n.getAttribute("label");
                                        if(label.indexOf('[')>=0)
                                        {
                                            int dosage = Integer.parseInt(label.substring(label.lastIndexOf('[')+1, 
                                                    label.lastIndexOf(']')));
                                            if(interaction[0].equals("increase_dosage"))
                                            {
                                                dosage+=Integer.parseInt(interaction[2]);
                                            }
                                            else if(interaction[0].equals("decrease_dosage"))
                                            {
                                                dosage-=Integer.parseInt(interaction[2]);
                                            }
                                            else if(interaction[0].equals("change_dosage"))
                                            {
                                                dosage=Integer.parseInt(interaction[2]);
                                            }
                                            n.setAttribute("label", label.substring(0, label.indexOf('[')+1)+dosage+"]");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Color.color(i, graph, therapy);
                JLabel jLabel1 = new JLabel();
                jLabel1.setSize(480, 480);
                
                boolean scroll = ImageGraph.newImageGraph(i, "rozwiazanie"+String.valueOf(counter)+selectedDiseases.get(i), 
                        graph, jLabel1, selectedDiseases);
                if(scroll)
                {
                    JScrollPane scrollPane = new JScrollPane(jLabel1);
                    solutionTabbedPane.addTab(selectedDiseases.get(i), scrollPane);
                }
                else
                {
                    solutionTabbedPane.addTab(selectedDiseases.get(i), jLabel1);
                }
            }
            graphsResultsTabbedPane.addTab("Rozwiązanie "+String.valueOf(counter), solutionTabbedPane);
            counter++;
        }
    }
    public static void setResults(ArrayList<String[]> foundConflicts, 
            ArrayList<ArrayList<String[]>> executedInteractions, ArrayList<ArrayList<ArrayList<String>>> therapies, 
            JTabbedPane resultsTabbedPane, ArrayList<ArrayList<ArrayList<String>>> therapiesOfDiseases, 
            ArrayList<Graph> graphs, ArrayList<String> selectedDiseases, 
            JTextArea conflictsTextArea, JScrollPane conflictsScrollPane, 
            JLabel conflictsLabel, JLabel selectedPathsLabel, JLabel resultsLabel, 
            JTabbedPane mainTabbedPane, JTabbedPane graphsResultsTabbedPane)
    {
        setGraphs(executedInteractions, therapies, graphsResultsTabbedPane, selectedDiseases);
        for(String[] conflict:foundConflicts)
        {
           ExecuteInteractions.executeInteractions(executedInteractions, foundConflicts, conflict, therapiesOfDiseases);
        }
        resultsTabbedPane.removeAll();
        int counter=1;
        for(ArrayList<ArrayList<String>> therapiesSolution:therapies)
        {
            JTabbedPane solutionTabbedPane = new JTabbedPane();
            for(ArrayList<String> therapy:therapiesSolution)
            {
                JTextArea results3TextArea = new JTextArea();
                results3TextArea.setEditable(false);
                results3TextArea.setLineWrap(true);
                String text = "";
                for(String s:therapy)
                {
                    boolean found = false;
                    for(Node n:graphs.get(therapiesSolution.indexOf(therapy)).getNodes(false))
                    {
                        if(n.getId().getId().equals(s)||
                                (s.lastIndexOf('?')>=0&&n.getId().getId().equals(s.substring(0,s.lastIndexOf('?')))))
                        {
                            found = true;
                            if(n.getAttribute("label")!=null && !n.getAttribute("label").equals(""))
                            {
                                text=text+n.getAttribute("label");
                                if(s.lastIndexOf('?')>=0)
                                {
                                    text=text+": "+s.substring(s.lastIndexOf('?')+1);
                                }
                                text+="\n";
                            }
                        }
                        else if(s.lastIndexOf('=')>=0 && !(s.indexOf('?')>=0)
                                &&n.getId().getId().equals(s.substring(0,s.lastIndexOf('='))))
                        {
                            found = true;
                            if(n.getAttribute("label")!=null && !n.getAttribute("label").equals(""))
                            {
                                String label = n.getAttribute("label");
                                text=text+label.substring(0, label.lastIndexOf('[')+1)
                                        +s.substring(s.indexOf('=')+1)+"]";
                            }
                        }
                    }
                    if(!found)
                    {
                       String label = getNodeLabel(s);
                       if(label!=null)
                       {
                           text=text+label+"\n";
                       }
                       else
                       {
                           text=text+s+"\n";
                       }
                    }
                }
                results3TextArea.setText(text);
                JScrollPane scrollResults3 = new JScrollPane(results3TextArea);

                JTextArea results4TextArea = new JTextArea();
                results4TextArea.setEditable(false);
                results4TextArea.setLineWrap(true);
                String text2 = "";
                for(String s:therapy)
                {
                    text2=text2+s+"\n";
                }
                results4TextArea.setText(text2);
                JScrollPane scrollResults4 = new JScrollPane(results4TextArea);

                JPanel resultsPanel = new JPanel();
                resultsPanel.setLayout(new GridLayout(1,2));
                resultsPanel.add(scrollResults3);
                resultsPanel.add(scrollResults4);

                solutionTabbedPane.addTab(selectedDiseases.get(therapiesSolution.indexOf(therapy)), resultsPanel);
            }
            resultsTabbedPane.addTab("Rozwiązanie "+String.valueOf(counter), solutionTabbedPane);
            counter++;
        }
        String conflictsText = "";
        for(String[] conflict:foundConflicts)
        {
            for(int i=0;i<conflict.length;i++)
            {
                String text = conflict[i];
                for(Graph g:graphs)
                {
                    for(Node n:g.getNodes(false))
                    {
                        if(n.getId().getId().equals(conflict[i]))
                        {
                            if(n.getAttribute("label")!=null && !n.getAttribute("label").equals(""))
                            {
                                text=n.getAttribute("label")+" ("+selectedDiseases.get(graphs.indexOf(g))+")";
                            }
                        }
                    }
                }
                if(i<conflict.length-1)
                {
                    conflictsText = conflictsText + text + ", ";
                }
                else
                {
                    conflictsText = conflictsText + text + ": ";
                }
                
            }
            ArrayList<String[]> interactions = executedInteractions.get(foundConflicts.indexOf(conflict));
            for(String[] interaction:interactions)
            {
                for(int i=0;i<interaction.length;i++)
                {
                    String text = interaction[i];
                    if(i!=0 && i!=2)
                    {
                        for(Graph g:graphs)
                        {
                            for(Node n:g.getNodes(false))
                            {
                                if(n.getId().getId().equals(interaction[i]))
                                {
                                    if(n.getAttribute("label")!=null && !n.getAttribute("label").equals(""))
                                    {
                                        text=n.getAttribute("label")+" ("+selectedDiseases.get(graphs.indexOf(g))+")";;
                                    }
                                }
                            }
                        }
                        if(text.equals(interaction[i]))
                        {
                            String label = getNodeLabel(interaction[i]);
                            if(label!=null)
                            {
                                text = label;
                            }
                        }
                    }
                    if(i<interaction.length-1)
                    {
                        conflictsText = conflictsText + text + " ";
                    }
                    else
                    {
                        conflictsText = conflictsText + text;
                    }
                }
                if(interactions.indexOf(interaction)<interactions.size()-1)
                {
                    conflictsText+=", ";
                }
                else
                {
                    conflictsText+="\n";
                }
            }
        }
        conflictsTextArea.setText(conflictsText);
        conflictsScrollPane.setVisible(true);
        conflictsLabel.setVisible(true);
        selectedPathsLabel.setVisible(true);
        resultsLabel.setVisible(true);
        mainTabbedPane.setSelectedIndex(2);
        for(ArrayList<ArrayList<String>> therapiesOfDisease:therapiesOfDiseases)
        {
            for(ArrayList<String> therapy:therapiesOfDisease)
            {
                ArrayList<String> toRemove = new ArrayList<String>();
                for(String s:therapy)
                {
                    if(s.startsWith("&"))
                    {
                        toRemove.add(s);
                    }
                }
                for(String s:toRemove)
                {
                    therapy.remove(s);
                }
            }
        }
    } 
}
