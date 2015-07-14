/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package program;

import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

/**
 *
 * @author Darek
 */
public class MainClass {
    private ArrayList<ArrayList<String>> dataList;
    private ArrayList<ArrayList<String>> dataIdList;
    private ArrayList<String> selectedDiseases;
    private JTextArea resultsTextArea;
    private JTextArea results2TextArea;
    private boolean startProgram = true;
    private boolean globalStop = false;
    private Node globalN;
    private ArrayList<ArrayList<ArrayList<String>>> therapiesOfDiseases;
    private ArrayList<Graph> graphs;
    private ArrayList<JLabel> currentImages;
    private ArrayList<Node> parallelNodes;
    private ArrayList<Integer> parallelPaths; 
    private ArrayList<Node> lastNodes;
    private ArrayList<ArrayList<Node>> parallelNodesHistory;
    private ArrayList<ArrayList<Integer>> parallelPathsHistory;
    public MainClass()
    {
        dataList = new ArrayList<ArrayList<String>>();
        dataIdList = new ArrayList<ArrayList<String>>();
        selectedDiseases = new ArrayList<String>();
        therapiesOfDiseases = new ArrayList<ArrayList<ArrayList<String>>>();
        graphs = new ArrayList<Graph>();
        currentImages = new ArrayList<JLabel>();
        parallelNodes = new ArrayList<Node>();
        parallelPaths = new ArrayList<Integer>();
        lastNodes = new ArrayList<Node>();
        parallelNodesHistory = new ArrayList<ArrayList<Node>>();
        parallelPathsHistory = new ArrayList<ArrayList<Integer>>();
    }
    public void setResults(ArrayList<String[]> foundConflicts, 
            ArrayList<ArrayList<String[]>> executedInteractions, ArrayList<ArrayList<ArrayList<String>>> therapies, 
            JTabbedPane resultsTabbedPane, JTextArea conflictsTextArea, JScrollPane conflictsScrollPane, 
            JLabel conflictsLabel, JLabel selectedPathsLabel, 
            JLabel resultsLabel, JTabbedPane mainTabbedPane, JTabbedPane graphsResultsTabbedPane) 
    {
            Results.setResults(foundConflicts, executedInteractions, therapies, resultsTabbedPane, 
                    therapiesOfDiseases, graphs, selectedDiseases, conflictsTextArea, 
                    conflictsScrollPane, conflictsLabel, selectedPathsLabel, 
                    resultsLabel, mainTabbedPane, graphsResultsTabbedPane);
    }
    private boolean findNestedParallelPath(int idOfDisease, Node nP, boolean parallelP)
    {
        boolean parallel = parallelP;
        Graph graph = graphs.get(idOfDisease);
        Node n = nP;
        ArrayList<Edge> list = GraphFunctions.getOutEdges(graph, n);
        ArrayList<Edge> inList = GraphFunctions.getInEdges(graph, n);
        if(inList.size()>1 && n.getAttribute("label").equals("") && list.size()>0)
        {
            parallel = false;
        }
        while(list.size()==1)
        {
            n = list.get(0).getTarget().getNode();
            list = GraphFunctions.getOutEdges(graph, n);
            inList = GraphFunctions.getInEdges(graph, n);
            if(inList.size()>1 && n.getAttribute("label").equals("") && list.size()>0)
            {
                parallel = false;
            }
        }
        if(list.size()>1 && n.getAttribute("label").equals(""))
        {
            if(parallel)
            {
                return true;
            }
            else
            {
                parallel = true;
            }
        }
        if(list.size()>1)
        {
            for(Edge e:list)
            {
                n = e.getTarget().getNode();
                if(findNestedParallelPath(idOfDisease, n, parallel))
                {
                    return true;
                }
            }
        }
        return false;
    }
    private void execute(int k, Node n, Graph graph, ArrayList<String> dataId)
    {
        lastNodes.add(null);
        GoForward.goForward(n, k, dataId, parallelNodes, parallelPaths, graphs, lastNodes, 
            parallelNodesHistory, parallelPathsHistory);
        Color.color(k, graph, dataId);
    }
    private void createTextAreas(int k, JTabbedPane selectedPathsTabbedPane)
    {
        
        resultsTextArea = new JTextArea();
        resultsTextArea.setEditable(false);
        resultsTextArea.setLineWrap(true);
        resultsTextArea.setText("Wyniki");
        resultsTextArea.setSize(200, 50);
        JScrollPane scrollResults = new JScrollPane(resultsTextArea);
        
        results2TextArea = new JTextArea();
        results2TextArea.setEditable(false);
        resultsTextArea.setLineWrap(true);
        results2TextArea.setText("Wyniki2");
        results2TextArea.setSize(200, 50);
        JScrollPane scrollResults2 = new JScrollPane(results2TextArea);
        
        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new GridLayout(1,2));
        resultsPanel.add(scrollResults);
        resultsPanel.add(scrollResults2);
        
        selectedPathsTabbedPane.addTab(selectedDiseases.get(k), resultsPanel);
    }
    public void buttonClick(JLabel selectDiseases, JPanel diseasesPanel, 
            JTabbedPane imagesTabbedPane, JTabbedPane selectedPathsTabbedPane, 
            Window window)
    {
        if(startProgram)
        {
            selectedDiseases.clear();
            
            for(JCheckBox c:window.getCheckBoxGroup())
            {
                if(c.isSelected())
                {
                    selectedDiseases.add(c.getText());
                }
            }
            if(!selectedDiseases.isEmpty())
            {
                startProgram = false;
                selectDiseases.setVisible(false);
                diseasesPanel.setVisible(false);
                dataList.clear();
                dataIdList.clear();
                parallelNodes.clear();
                parallelPaths.clear();
                parallelNodesHistory.clear();
                parallelPathsHistory.clear();
                graphs.clear();
                currentImages.clear();
                lastNodes.clear();
                therapiesOfDiseases.clear();
                boolean stop = false;
                int diseaseNumber = -1;
                ArrayList<Node> startNodes = new ArrayList<Node>();
                for(int i=0;i<selectedDiseases.size();i++)
                {
                    dataList.add(new ArrayList<String>());
                    dataIdList.add(new ArrayList<String>());
                    parallelNodes.add(null);
                    parallelPaths.add(0);
                    parallelNodesHistory.add(new ArrayList<Node>());
                    parallelPathsHistory.add(new ArrayList<Integer>());
                    String current = selectedDiseases.get(i);
                    graphs.add(GraphFunctions.getGraph("algorytmy/"+current+".dot"));
                    Graph graph = graphs.get(i);
                    Node n = GraphFunctions.getStartNode(graph);
                    startNodes.add(n);
                    boolean stopTmp = findNestedParallelPath(i, n, false);
                    if(!stop)
                    {
                        stop = stopTmp;
                        diseaseNumber = i;
                    }
                }
                for(int i=0;i<selectedDiseases.size();i++)
                {
                    if(!stop)
                    {
                        execute(i, startNodes.get(i), graphs.get(i), dataIdList.get(i));    
                    }
                    JLabel jLabel1 = new JLabel();
                    jLabel1.setSize(570, 570);
                    currentImages.add(jLabel1);
                    boolean scroll = ImageGraph.newImageGraph(i, selectedDiseases.get(i), graphs.get(i), jLabel1, 
                            selectedDiseases);
                    if(scroll)
                    {
                        JScrollPane scrollPane = new JScrollPane(jLabel1);
                        imagesTabbedPane.addTab(selectedDiseases.get(i), scrollPane);
                    }
                    else
                    {
                        imagesTabbedPane.addTab(selectedDiseases.get(i), jLabel1);
                    }
                    if(!stop)
                    {
                        RadioButtonList.createRadioButtonList(i, window, this);
                    }
                }
                if(stop && diseaseNumber!=-1)
                {
                    JOptionPane.showMessageDialog(window, "Wykryto zagnieżdżoną równoległą ścieżkę w algorytmie "
                            +selectedDiseases.get(diseaseNumber)+". Funkcje programu zostaną wyłączone.", "Komunikat", 
                            JOptionPane.WARNING_MESSAGE);
                    globalStop = true;
                }
            }
        }
        else if(!globalStop)
        {
            selectedPathsTabbedPane.removeAll();
            therapiesOfDiseases.clear();
            for(int k=0;k<selectedDiseases.size();k++)
            {
                dataList.get(k).clear();
                String last = dataIdList.get(k).get(dataIdList.get(k).size()-1);
                Node next = null;
                boolean end = false;
                for(Node n:graphs.get(k).getNodes(false))
                {
                    if(n.getId().getId().equals(last)
                       ||(last.indexOf('?')>=0 && n.getId().getId().equals(last.substring(0,last.lastIndexOf('?'))))
                       ||(last.indexOf('=')>=0 && !(last.indexOf('?')>=0)
                            && n.getId().getId().equals(last.substring(0,last.lastIndexOf('=')))))
                    {
                        if(last.indexOf('?')==-1)
                        {
                            ArrayList<Edge> edges = GraphFunctions.getOutEdges(graphs.get(k), n);
                            if(edges.size()==0)
                            {
                                end = true;
                            }
                            else
                            {
                                next = edges.get(0).getTarget().getNode();
                            }
                        }
                        else
                        {
                            ArrayList<Edge> edges = GraphFunctions.getOutEdges(graphs.get(k), n);
                            if(edges.size()==0)
                            {
                                end = true;
                            }
                            else
                            {
                                for(Edge e:edges)
                                {
                                    if(e.getAttribute("label").equals(last.substring(last.lastIndexOf('?')+1)))
                                    {
                                        next = e.getTarget().getNode();
                                    }
                                }
                            }
                        }
                    }
                }
                if(end)
                {
                    ArrayList<ArrayList<String>> therapiesOfDisease = new ArrayList<ArrayList<String>>();
                    therapiesOfDisease.add(dataIdList.get(k));
                    therapiesOfDiseases.add(therapiesOfDisease);
                }
                else if(next!=null)
                {
                    globalN = next;
                    ArrayList<Edge> list = GraphFunctions.getOutEdges(graphs.get(k), next);
                    ArrayList<ArrayList<String>> therapies = new ArrayList<ArrayList<String>>();
                    Node tempParallelNode = parallelNodes.get(k);
                    Integer tempParallelPath = parallelPaths.get(k);
                    if(parallelNodes.get(k)!=null)
                    {

                        ArrayList<ArrayList<String>> listOfTherapies = new ArrayList<ArrayList<String>>();
                        Node tempN = globalN;
                        for(Edge e:list)
                        {
                                ArrayList<String> therapy = new ArrayList<String>();
                                for(String id:dataIdList.get(k))
                                {
                                   therapy.add(id);
                                }
                                therapy.add(tempN.getId().getId()+"?"+e.getAttribute("label"));
                                globalN = e.getTarget().getNode();
                                ArrayList<ArrayList<String>> newTherapies = CreateTherapies.createTherapies(k, therapy, 
                                        globalN, graphs, parallelNodes, parallelPaths);
                                for(ArrayList<String> t:newTherapies)
                                {
                                   listOfTherapies.add(t);
                                }
                        }
                        parallelPaths.set(k, parallelPaths.get(k)+1);
                        therapies = CreateTherapies.parallelCreateTherapies(k, listOfTherapies, globalN, 
                                parallelNodes, parallelPaths, graphs);
                    }
                    else
                    {
                        Node tempN = globalN;
                        for(Edge e:list)
                        {
                            ArrayList<String> therapy = new ArrayList<String>();
                            for(String id:dataIdList.get(k))
                            {
                                therapy.add(id);
                            }
                            therapy.add(tempN.getId().getId()+"?"+e.getAttribute("label"));
                            globalN = e.getTarget().getNode();
                            ArrayList<ArrayList<String>> newTherapies = CreateTherapies.createTherapies(k, therapy, globalN, 
                                    graphs, parallelNodes, parallelPaths);
                            for(ArrayList<String> t:newTherapies)
                            {
                                therapies.add(t);
                            }
                        }
                    }
                    therapiesOfDiseases.add(therapies);
                    parallelNodes.set(k, tempParallelNode);
                    parallelPaths.set(k, tempParallelPath);
                }
                for(String elem:dataIdList.get(k))
                {
                    for(Node n:graphs.get(k).getNodes(false))
                    {
                        if(n.getId().getId().equals(elem))
                        {
                            if(n.getAttribute("label")!=null && !n.getAttribute("label").equals(""))
                            {
                                dataList.get(k).add(n.getAttribute("label"));
                            }
                        }
                        else if(elem.indexOf('?')>=0 && n.getId().getId().equals(elem.substring(0,elem.lastIndexOf('?'))))
                        {
                            if(n.getAttribute("label")!=null && !n.getAttribute("label").equals(""))
                            {
                                dataList.get(k).add(n.getAttribute("label")+": "+elem.substring(elem.lastIndexOf('?')+1));
                            }
                        }
                        else if(elem.indexOf('=')>=0 && !(elem.indexOf('?')>=0)
                                && n.getId().getId().equals(elem.substring(0,elem.lastIndexOf('='))))
                        {
                            String dosage = elem.substring(elem.lastIndexOf('=')+1);
                            String label = n.getAttribute("label");
                            dataList.get(k).add(label.substring(0, label.indexOf('[')+1)+dosage+"]");
                        }
                    }
                }
                createTextAreas(k, selectedPathsTabbedPane);
                String text = "";
                for(String s:dataList.get(k))
                {
                    text=text+s+"\n";
                }
                resultsTextArea.setText(text);
                String text2 = "";
                for(String s:dataIdList.get(k))
                {
                    text2=text2+s+"\n";
                }
                results2TextArea.setText(text2);
                
            }
            
            ChocoClass chocoClass = new ChocoClass();
            chocoClass.solve(window, selectedDiseases, therapiesOfDiseases, graphs);
        }
    }
     /**
     * @return the dataIdList
     */
    public ArrayList<ArrayList<String>> getDataIdList() {
        return dataIdList;
    }

    /**
     * @return the selectedDiseases
     */
    public ArrayList<String> getSelectedDiseases() {
        return selectedDiseases;
    }

    /**
     * @return the graphs
     */
    public ArrayList<Graph> getGraphs() {
        return graphs;
    }

    /**
     * @return the currentImages
     */
    public ArrayList<JLabel> getCurrentImages() {
        return currentImages;
    }

    /**
     * @return the parallelNodes
     */
    public ArrayList<Node> getParallelNodes() {
        return parallelNodes;
    }

    /**
     * @return the parallelPaths
     */
    public ArrayList<Integer> getParallelPaths() {
        return parallelPaths;
    }

    /**
     * @return the lastNodes
     */
    public ArrayList<Node> getLastNodes() {
        return lastNodes;
    }

    /**
     * @return the parallelNodesHistory
     */
    public ArrayList<ArrayList<Node>> getParallelNodesHistory() {
        return parallelNodesHistory;
    }

    /**
     * @return the parallelPathsHistory
     */
    public ArrayList<ArrayList<Integer>> getParallelPathsHistory() {
        return parallelPathsHistory;
    }
    public void setStartProgram(boolean startProgram)
    {
        this.startProgram = startProgram;
    }
}
