/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package program;

import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

/**
 *
 * @author Darek
 */
public class RadioButtonList {
    public static void createRadioButtonList(int k, Window window, MainClass mainClass)
    {
        ArrayList<String> therapy = mainClass.getDataIdList().get(k);
        Graph currentGraph = mainClass.getGraphs().get(k);
        JPanel rowsPanel = new JPanel();
        rowsPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridy = 0;
        ArrayList<JPanel> panels = new ArrayList<JPanel>();
        int counter=0;
        for(String elem:therapy)
        {
            if(elem.indexOf('?')>=0)
            {
                for(Node node:currentGraph.getNodes(false))
                {
                   if(node.getId().getId().equals(elem.substring(0,elem.lastIndexOf('?'))))
                   {
                       JPanel panel = new JPanel();
                       JLabel label = new JLabel();
                       label.setText(node.getAttribute("label"));
                       panel.add(label);

                       ButtonGroup buttonGroup = new ButtonGroup();
                       ArrayList<Edge> edges = GraphFunctions.getOutEdges(currentGraph, node);
                       for(Edge edge:edges)
                       {
                           JRadioButton radioButton = new JRadioButton();
                           radioButton.setText(edge.getAttribute("label"));
                           if(edge.getAttribute("label").equals(elem.substring(elem.lastIndexOf('?')+1)))
                           {
                               radioButton.setSelected(true);
                           }
                           final int idOfDisease = k;
                           final String question = node.getId().getId();
                           final String answer = edge.getAttribute("label");
                           final int idOfQuestion = counter;
                           final Window windowP = window;
                           final MainClass mainClassP = mainClass;
                           radioButton.addActionListener(new ActionListener() {
                               @Override
                               public void actionPerformed(ActionEvent e) {
                                    updateRadioButtonList(idOfDisease, question, answer, idOfQuestion, 
                                        windowP, mainClassP);
                               }
                           });
                           buttonGroup.add(radioButton);
                           panel.add(radioButton);
                       }
                       JRadioButton nullRadioButton = new JRadioButton();
                       nullRadioButton.setText("brak wartości");
                       final int idOfDisease = k;
                       final String question = node.getId().getId();
                       final String answer = "brak wartości";
                       final int idOfQuestion = counter;
                       final Window windowP = window;
                       final MainClass mainClassP = mainClass;
                       nullRadioButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                updateRadioButtonList(idOfDisease, question, answer, idOfQuestion, windowP, mainClassP);
                            }
                       });
                       buttonGroup.add(nullRadioButton);
                       panel.add(nullRadioButton);
                       panels.add(panel);

                       counter++;
                   }
                }

            }
        }
        if(mainClass.getLastNodes().get(k)!=null)
        {
                JPanel panel = new JPanel();
                Node additionalNode = mainClass.getLastNodes().get(k);
                JLabel label = new JLabel();
                label.setText(additionalNode.getAttribute("label"));
                panel.add(label);

                ButtonGroup buttonGroup = new ButtonGroup();
                ArrayList<Edge> edges = GraphFunctions.getOutEdges(currentGraph, additionalNode);
                for(Edge edge:edges)
                {
                    JRadioButton radioButton = new JRadioButton();
                    radioButton.setText(edge.getAttribute("label"));
                    buttonGroup.add(radioButton);
                    panel.add(radioButton);
                    final int idOfDisease = k;
                    final String question = additionalNode.getId().getId();
                    final String answer = edge.getAttribute("label");
                    final int idOfQuestion = counter;
                    final Window windowP = window;
                    final MainClass mainClassP = mainClass;
                    radioButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            updateRadioButtonList(idOfDisease, question, answer, idOfQuestion, windowP, mainClassP);
                        }
                    });
                }
                JRadioButton nullRadioButton = new JRadioButton();
                nullRadioButton.setText("brak wartości");
                nullRadioButton.setSelected(true);
                final int idOfDisease = k;
                final String question = additionalNode.getId().getId();
                final String answer = "brak wartości";
                final int idOfQuestion = counter;
                final Window windowP = window;
                final MainClass mainClassP = mainClass;
                nullRadioButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        updateRadioButtonList(idOfDisease, question, answer, idOfQuestion, windowP, mainClassP);
                    }
                });
                buttonGroup.add(nullRadioButton);
                panel.add(nullRadioButton);
                panels.add(panel);
        }
        for(int i=0;i<panels.size();i++)
        {
            if(i==panels.size()-1)
            {
                c.weightx = 1;
                c.weighty = 1;
            }
            rowsPanel.add(panels.get(i), c);
            c.gridy++;
        }
        JScrollPane scrollPane = new JScrollPane(rowsPanel);
        window.getQuestionsTabbedPane().insertTab(mainClass.getSelectedDiseases().get(k), null, scrollPane, null, k);
    }
    
    public static void updateRadioButtonList(int idOfDisease, String question, String answer, int idOfQuestion, 
            Window window, MainClass mainClass)
    {
        ArrayList<String> therapyList = mainClass.getDataIdList().get(idOfDisease);
        int index = -1;
        for(String elem:therapyList)
        {
            if(elem.indexOf('?')>=0 && elem.substring(0, elem.lastIndexOf('?')).equals(question))
            {
                index = therapyList.indexOf(elem);
            }
        }
        if(answer.equals("brak wartości"))
        {
            if(index!=-1)
            {
                int size = therapyList.size();
                for(int i=index;i<size;i++)
                {
                    therapyList.remove(index);
                }
                for(Node node:mainClass.getGraphs().get(idOfDisease).getNodes(false))
                {
                    if(node.getId().getId().equals(question))
                    {
                        mainClass.getLastNodes().set(idOfDisease, node);
                    }
                }
            }
        }
        else
        {
            if(index==-1)
            {
                therapyList.add(question+"?"+answer);
            }
            else
            {
                therapyList.set(index, question+"?"+answer);
                int size = therapyList.size();
                for(int i=index+1;i<size;i++)
                {
                    therapyList.remove(index+1);
                }
            }
            int size = mainClass.getParallelNodesHistory().get(idOfDisease).size();
            for(int i=idOfQuestion+1;i<size;i++)
            {
                mainClass.getParallelNodesHistory().get(idOfDisease).remove(idOfQuestion+1);
                mainClass.getParallelPathsHistory().get(idOfDisease).remove(idOfQuestion+1);
            }
            mainClass.getParallelNodes().set(idOfDisease, 
                    mainClass.getParallelNodesHistory().get(idOfDisease).get(idOfQuestion));
            mainClass.getParallelPaths().set(idOfDisease, 
                    mainClass.getParallelPathsHistory().get(idOfDisease).get(idOfQuestion));
            Node next = null;
            for(Node node:mainClass.getGraphs().get(idOfDisease).getNodes(false))
            {
                if(node.getId().getId().equals(question))
                {
                    next = node;
                }
            }
            ArrayList<Edge> list = GraphFunctions.getOutEdges(mainClass.getGraphs().get(idOfDisease), next);
            for(Edge e:list)
            {
                if(e.getAttribute("label").equals(answer))
                {
                    next = e.getTarget().getNode();
                }
            }
            GoForward.goForward(next, idOfDisease, therapyList, mainClass.getParallelNodes(), 
                    mainClass.getParallelPaths(), mainClass.getGraphs(), mainClass.getLastNodes(), 
                mainClass.getParallelNodesHistory(), mainClass.getParallelPathsHistory());
            
        }
        Color.resetColors(mainClass.getGraphs().get(idOfDisease));
        Color.color(idOfDisease, mainClass.getGraphs().get(idOfDisease), therapyList);
        ImageGraph.newImageGraph(idOfDisease, mainClass.getSelectedDiseases().get(idOfDisease), 
                mainClass.getGraphs().get(idOfDisease), mainClass.getCurrentImages().get(idOfDisease), 
                mainClass.getSelectedDiseases());
        window.getImagesTabbedPane().setSelectedIndex(idOfDisease);
        window.getQuestionsTabbedPane().removeTabAt(idOfDisease);
        createRadioButtonList(idOfDisease, window, mainClass);
        window.getQuestionsTabbedPane().setSelectedIndex(idOfDisease);
    }
}
