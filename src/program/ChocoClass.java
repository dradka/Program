/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package program;

import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import org.chocosolver.solver.ResolutionPolicy;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import org.chocosolver.solver.constraints.LogicalConstraintFactory;
import static org.chocosolver.solver.constraints.LogicalConstraintFactory.and;
import static org.chocosolver.solver.constraints.LogicalConstraintFactory.not;
import org.chocosolver.solver.search.strategy.IntStrategyFactory;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;
import org.chocosolver.solver.variables.VariableFactory;
import org.chocosolver.util.ESat;

/**
 *
 * @author Darek
 */
public class ChocoClass {
    private ArrayList<IntVar> addedVarsList;
    private ArrayList<IntVar> therapiesList;
    private ArrayList<IntVar> globalConflictsList;
    private ArrayList<String> notConflictElems;
    private void setAdditionalVariables(ArrayList<JPanel> panels, Solver s)
    {
        addedVarsList = new ArrayList<IntVar>();
        if(panels!=null)
        {
            for(JPanel panel:panels)
            {
                String label = "&"+((JLabel)panel.getComponent(0)).getText();
                if(panel.getComponent(1) instanceof JTextField)
                {
                    int number = Integer.parseInt(((JTextField)panel.getComponent(1)).getText());
                    addedVarsList.add(VariableFactory.bounded(label, number, number, s));
                }
                else
                {
                    if(((JRadioButton)panel.getComponent(1)).isSelected())
                    {
                        addedVarsList.add(VariableFactory.bounded(label, 1, 1, s));
                    }
                    else
                    {
                        addedVarsList.add(VariableFactory.bounded(label, 0, 0, s));
                    }
                }
                
            }
        }
        
    }
    private void setVariables(ArrayList<String> diseases, ArrayList<ArrayList<ArrayList<String>>> therapiesOfDiseases, Solver s, 
            ArrayList<Graph> graphs)
    {
        therapiesList = new ArrayList<IntVar>();
        globalConflictsList = new ArrayList<IntVar>();
        for(int i=0;i<therapiesOfDiseases.size();i++)
        {
            String disease = diseases.get(i);
            ArrayList<ArrayList<String>> therapiesOfDisease = therapiesOfDiseases.get(i);
            IntVar[] therapiesVar = new IntVar[therapiesOfDisease.size()];
            int counter = 0;
            for(ArrayList<String> therapy:therapiesOfDisease)
            {
                String therapyStr = disease+"_terapia"+String.valueOf(counter);
                IntVar therapyVar = VariableFactory.bounded(therapyStr, 0, 1, s);
                therapiesVar[counter] = therapyVar;
                therapiesList.add(therapyVar);
                
                ArrayList<String> notConflictElemsTherapy = new ArrayList<String>();
                for(String c:notConflictElems)
                {
                    boolean exists = false;
                    for(String medicine:therapy)
                    {
                        String medicineName = medicine;
                        if(medicine.indexOf('=')>=0 && !(medicine.indexOf('?')>=0))
                        {
                            medicineName = medicine.substring(0, medicine.indexOf('='));
                        }
                        if(medicineName.equals(c))
                        {
                            exists = true;
                        }
                    }
                    if(!exists)
                    {
                        boolean exists2 = false;
                        for(String c2:notConflictElemsTherapy)
                        {
                            if(c.equals(c2))
                            {
                                exists2 = true;
                            }
                        }
                        if(!exists2)
                        {
                            for(Node n:graphs.get(i).getNodes(false))
                            {
                                if(n.getId().getId().equals(c))
                                {
                                    notConflictElemsTherapy.add(c);
                                }
                            }
                        }
                    }
                }
                IntVar[] vars = new IntVar[therapy.size()+notConflictElemsTherapy.size()+1];
                
                int counter2=0;
                for(String c:notConflictElemsTherapy)
                {
                    IntVar medicineVar = null;
                    boolean addedVar = false;
                    for(IntVar elem:addedVarsList)
                    {
                        if(elem.getName().equals(c))
                        {
                            addedVar = true;
                            globalConflictsList.add(elem);
                            medicineVar = elem;
                        }
                    }
                    if(!addedVar)
                    {
                        medicineVar = VariableFactory.bounded(c, 0, 1, s);
                        addedVarsList.add(medicineVar);
                        globalConflictsList.add(medicineVar);
                    }
                    addedVar = false;
                    IntVar notMedicineVar = null;
                    for(IntVar elem:addedVarsList)
                    {
                        if(elem.getName().equals("not_"+c))
                        {
                            addedVar = true;
                            globalConflictsList.add(elem);
                            notMedicineVar = elem;
                        }
                    }
                    if(!addedVar)
                    {
                        notMedicineVar = VariableFactory.bounded("not_"+c, 0, 1, s);
                        LogicalConstraintFactory.ifThenElse(IntConstraintFactory.arithm(medicineVar,"=", 1), 
                            IntConstraintFactory.arithm(notMedicineVar,"=",0), 
                            IntConstraintFactory.arithm(notMedicineVar,"=",1));
                        addedVarsList.add(notMedicineVar);
                        globalConflictsList.add(notMedicineVar);
                    }
                    vars[counter2] = notMedicineVar;
                    counter2++;
                }
                for(String medicine:therapy)
                {
                    String medicineName = medicine;
                    if(medicine.indexOf('=')>=0 && !(medicine.indexOf('?')>=0))
                    {
                        medicineName = medicine.substring(0, medicine.indexOf('='));
                    }
                    IntVar medicineVar = null;
                    boolean addedVar = false;
                    for(IntVar elem:addedVarsList)
                    {
                        if(elem.getName().equals(medicineName))
                        {
                            addedVar = true;
                            medicineVar = elem;
                        }
                    }
                    if(addedVar)
                    {
                        if(medicine.indexOf('=')>=0 && !(medicine.indexOf('?')>=0))
                        {
                            boolean addedVar2 = false;
                            for(IntVar elem2:addedVarsList)
                            {
                                if(elem2.getName().equals(medicineName+"_dosage"))
                                {
                                    addedVar2 = true;
                                }
                            }
                            if(!addedVar2)
                            {
                                int value = Integer.parseInt(medicine.substring(medicine.indexOf('=')+1));
                                addedVarsList.add(VariableFactory.bounded(medicineName+"_dosage", value, value, s));
                            }
                        }
                    }
                    else
                    {
                        medicineVar = VariableFactory.bounded(medicineName, 0, 1, s);
                        addedVarsList.add(medicineVar);
                        if(medicine.indexOf('=')>=0 && !(medicine.indexOf('?')>=0))
                        {
                            int value = Integer.parseInt(medicine.substring(medicine.indexOf('=')+1));
                            addedVarsList.add(VariableFactory.bounded(medicineName+"_dosage", value, value, s));
                        }
                    }
                    vars[counter2] = medicineVar;
                    counter2++;
                }
                vars[therapy.size()+notConflictElemsTherapy.size()]=
                VariableFactory.bounded("additional"+String.valueOf(i)+"."+String.valueOf(counter), 
                        0, 1, s);
                IntVar sum = VariableFactory.bounded("sum"+String.valueOf(i)+"."+String.valueOf(counter), 
                        0, vars.length, s);
                s.post(IntConstraintFactory.sum(vars, sum));
                LogicalConstraintFactory.ifThenElse(IntConstraintFactory.arithm(sum,"=",
                        vars.length), 
                        IntConstraintFactory.arithm(therapyVar,"=",1), 
                        IntConstraintFactory.arithm(therapyVar,"=",0));
                counter++;
            }
            IntVar sum = VariableFactory.bounded("sum_therapies"+String.valueOf(i), 0, therapiesOfDisease.size(), s);
            s.post(IntConstraintFactory.sum(therapiesVar, sum));
            s.post(IntConstraintFactory.arithm(sum, "=", 1));
        }
    }
    private void conflictWithDosage(ArrayList<Constraint> constraintsList, String name, String sign, int dosage, Solver s)
    {
        if(name.endsWith("_dosage") || name.startsWith("&"))
        {
            boolean found = false;
            for(IntVar elem:addedVarsList)
            {
                if(elem.getName().equals(name))
                {
                    found = true;
                    if(name.startsWith("&"))
                    {
                        constraintsList.add(IntConstraintFactory.arithm(elem, sign, dosage));
                        globalConflictsList.add(elem);
                    }
                    else
                    {
                        for(IntVar elem2:addedVarsList)
                        {
                            if(elem2.getName().equals(name.substring(0,name.indexOf("_dosage"))))
                            {
                                constraintsList.add(IntConstraintFactory.arithm(elem2, "=", 1));
                                constraintsList.add(IntConstraintFactory.arithm(elem, sign, dosage));
                                globalConflictsList.add(elem);
                                globalConflictsList.add(elem2);
                            }
                        }
                    }
                }
            }
            if(!found)
            {
                IntVar var = VariableFactory.bounded(name+"_false", 0, 1, s);
                constraintsList.add(IntConstraintFactory.arithm(var, "=", 0));
                addedVarsList.add(var);
                globalConflictsList.add(var);
            }
        }
    }
    private void setConflictConstraint(String[] conflict, Solver s)
    {
        ArrayList<Constraint> constraintsList = new ArrayList<Constraint>();
        for(int i=0;i<conflict.length;i++)
        {
            if(conflict[i].indexOf('=')>=0 || conflict[i].indexOf('>')>=0
               || conflict[i].indexOf('<')>=0 && !(conflict[i].indexOf('?')>=0))
            {
                String name = null;
                String sign = null;
                int dosage = -1;
                if(conflict[i].indexOf(">=")>=0)
                {
                    name = conflict[i].substring(0, conflict[i].indexOf(">="));
                    sign = ">=";
                    dosage = Integer.parseInt(conflict[i].substring(conflict[i].indexOf(">=")+2));
                }
                else if(conflict[i].indexOf("<=")>=0)
                {
                    name = conflict[i].substring(0, conflict[i].indexOf("<="));
                    sign = "<=";
                    dosage = Integer.parseInt(conflict[i].substring(conflict[i].indexOf("<=")+2));
                }
                else if(conflict[i].indexOf('=')>=0)
                {
                    name = conflict[i].substring(0, conflict[i].indexOf('='));
                    sign = "=";
                    dosage = Integer.parseInt(conflict[i].substring(conflict[i].indexOf('=')+1));
                }
                else if(conflict[i].indexOf('>')>=0)
                {
                    name = conflict[i].substring(0, conflict[i].indexOf('>'));
                    sign = ">";
                    dosage = Integer.parseInt(conflict[i].substring(conflict[i].indexOf('>')+1));
                }
                else if(conflict[i].indexOf('<')>=0)
                {
                    name = conflict[i].substring(0, conflict[i].indexOf('<'));
                    sign = "<";
                    dosage = Integer.parseInt(conflict[i].substring(conflict[i].indexOf('<')+1));
                }
                conflictWithDosage(constraintsList, name, sign, dosage, s);
            }
            else if(conflict[i].startsWith("not"))
            {
                String c = conflict[i].substring(conflict[i].indexOf('(')+1, conflict[i].indexOf(')'));
                boolean addedVar = false;
                for(IntVar elem:addedVarsList)
                {
                    if(elem.getName().equals(c))
                    {
                        constraintsList.add(not(IntConstraintFactory.arithm(elem, "=", 1)));
                        globalConflictsList.add(elem);
                        addedVar = true;
                    }
                }
                if(!addedVar)
                {
                    IntVar var = VariableFactory.bounded(c, 0, 1, s);
                    addedVarsList.add(var);
                    globalConflictsList.add(var);
                    constraintsList.add(not(IntConstraintFactory.arithm(var, "=", 1)));
                }
            }
            else
            {
                boolean addedVar = false;
                for(IntVar elem:addedVarsList)
                {
                    if(elem.getName().equals(conflict[i]))
                    {
                        constraintsList.add(IntConstraintFactory.arithm(elem, "=", 1));
                        globalConflictsList.add(elem);
                        addedVar = true;
                    }
                }
                if(!addedVar)
                {
                    IntVar var = VariableFactory.bounded(conflict[i], 0, 1, s);
                    addedVarsList.add(var);
                    globalConflictsList.add(var);
                    constraintsList.add(IntConstraintFactory.arithm(var, "=", 1));
                }
            }
        }
        Constraint[] constraints = new Constraint[constraintsList.size()];
        for(int i=0;i<constraints.length;i++)
        {
            constraints[i] = constraintsList.get(i);
        }
        s.post(not(and(constraints)));
    }
    private void addConstraintsTrue(Solver s)
    {
        for(IntVar elem:addedVarsList)
        {
            boolean exists = false;
            for(IntVar conflictElem:globalConflictsList)
            {
                if(elem.equals(conflictElem))
                {
                    exists = true;
                }
            }
            if((!exists) && !elem.getName().endsWith("_dosage")
                 &&!elem.getName().startsWith("&"))
            {
                s.post(IntConstraintFactory.arithm(elem, "=", 1));
            }
        }
    }
    public void solve(Window window, ArrayList<String> diseases, ArrayList<ArrayList<ArrayList<String>>> therapiesOfDiseases, 
            ArrayList<Graph> graphs)
    {  
        notConflictElems = new ArrayList<String>();
        ArrayList<String[]> foundConflicts = new ArrayList<String[]>();
        ArrayList<ArrayList<String[]>> executedInteractions = new ArrayList<ArrayList<String[]>>();
        ArrayList<String[]> conflictsList = new ArrayList<String[]>();
        ArrayList<ArrayList<String[]>> interactionsList = new ArrayList<ArrayList<String[]>>();
        ArrayList<Integer> avoidedConflicts = new ArrayList<Integer>();
        ArrayList<String> lines = new ArrayList<String>();
        ArrayList<String> additionalQuestions = new ArrayList<String>();
        File directory = new File("konflikty");
        File[] listOfFiles = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".txt") && !name.equals("nazwy.txt");
            }
        });
        for(int i=0;i<listOfFiles.length;i++)
        {
            String name = listOfFiles[i].getName();
            String[] arrayDiseases = name.substring(0, name.lastIndexOf('.')).split(",");
            boolean useFile = false;
            for(int j=0;j<arrayDiseases.length && !useFile;j++)
            {
                for(String disease:diseases)
                {
                    if(arrayDiseases[j].equals(disease))
                    {
                        useFile = true;
                    }
                }
            }
            if(useFile)
            {
                BufferedReader br;
                try {
                    br = new BufferedReader(new FileReader(listOfFiles[i]));
                    String line = br.readLine();
                    while (line != null) 
                    {
                        String[] array = line.substring(0, line.indexOf(':')).split(" ");
                        for(int k=0;k<array.length;k++)
                        {
                            if(array[k].startsWith("&"))
                            {
                                additionalQuestions.add(array[k]);
                            }
                            else if(array[k].startsWith("not"))
                            {
                                String c = array[k].substring(array[k].indexOf('(')+1, array[k].indexOf(')'));
                                notConflictElems.add(c);
                            }
                        }
                        conflictsList.add(array);
                        String[] array2 = line.substring(line.indexOf(':')+1).split(",");
                        ArrayList<String[]> list = new ArrayList<String[]>();
                        for(int k=0;k<array2.length;k++)
                        {
                            String[] array3 = array2[k].split(" ");
                            list.add(array3);
                        }
                        interactionsList.add(list);
                        line = br.readLine();
                    }
                    br.close();
                }
                catch (FileNotFoundException ex) 
                {
                    Logger.getLogger(ChocoClass.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (IOException ex) 
                {
                    Logger.getLogger(ChocoClass.class.getName()).log(Level.SEVERE, null, ex);
                }  
            }
        }
        if(additionalQuestions.isEmpty())
        {
           solveNextPart(conflictsList, therapiesOfDiseases,
            diseases,  avoidedConflicts, foundConflicts,
            executedInteractions, interactionsList, window, null, graphs);
        }
        else
        {
            JPanel rowsPanel=new JPanel();
            rowsPanel.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.anchor = GridBagConstraints.NORTHWEST;
            c.gridy = 0;
            ArrayList<JPanel> panels = new ArrayList<JPanel>();
            ArrayList<String> names = new ArrayList<String>();
            for(String s:additionalQuestions)
            {
                
                if(s.indexOf('=')>=0 || s.indexOf('>')>=0
                    || s.indexOf('<')>=0)
                {
                    String name = null;
                    if(s.indexOf('>')>=0)
                    {
                        name = s.substring(0, s.indexOf('>'));
                    }
                    else if(s.indexOf('<')>=0)
                    {
                        name = s.substring(0, s.indexOf('<'));
                    }
                    else if(s.indexOf('=')>=0)
                    {
                        name = s.substring(0, s.indexOf('='));
                    }
                    if(name!=null)
                    {
                        boolean exists = false;
                        for(String n:names)
                        {
                            if(n.equals(name))
                            {
                                exists = true;
                            }
                        }
                        if(!exists)
                        {
                           names.add(name);
                           JPanel panel = new JPanel();
                           JLabel label = new JLabel();
                           label.setText(name.substring(1));
                           panel.add(label);
                           
                           JTextField textField = new JTextField();
                           textField.setPreferredSize(new Dimension(50,25));
                           panel.add(textField);
                           panels.add(panel);
                        }
                    }
                }
                else
                {
                    boolean exists = false;
                    for(String n:names)
                    {
                        if(n.equals(s))
                        {
                            exists = true;
                        }
                    }
                    if(!exists)
                    {
                        names.add(s);
                        JPanel panel = new JPanel();
                        JLabel label = new JLabel();
                        label.setText(s.substring(1));
                        panel.add(label);

                        ButtonGroup buttonGroup = new ButtonGroup();
                        JRadioButton yes = new JRadioButton();
                        yes.setText("tak");
                        yes.setSelected(true);
                        buttonGroup.add(yes);
                        panel.add(yes);

                        JRadioButton no = new JRadioButton();
                        no.setText("nie");
                        buttonGroup.add(no);
                        panel.add(no);

                        panels.add(panel);
                    }
                }
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
            int option = JOptionPane.showOptionDialog(window, scrollPane, "Dodatkowe pytania", 
                   JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"OK", "Anuluj"}, null);
            if(option==JOptionPane.OK_OPTION)
            {
                boolean error = false;
                for(int i=0;i<panels.size() && !error;i++)
                {
                    JPanel panel = panels.get(i);
                    if(panel.getComponent(1) instanceof JTextField)
                    {
                        String text = ((JTextField)panel.getComponent(1)).getText();
                        try  
                        {  
                          Integer.parseInt(text);
                        }  
                        catch(NumberFormatException nfe)  
                        {  
                           error = true; 
                           JOptionPane.showMessageDialog(window,
                                "Nie podano wartości liczbowych.",
                                "Błąd",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                if(!error)
                {
                    solveNextPart(conflictsList, therapiesOfDiseases,
                        diseases,  avoidedConflicts, foundConflicts,
                        executedInteractions, interactionsList, window, panels, graphs);
                }
            }
        }
        
    }
    private void solveNextPart(ArrayList<String[]> conflictsList, ArrayList<ArrayList<ArrayList<String>>> therapiesOfDiseases,
            ArrayList<String> diseases,  ArrayList<Integer> avoidedConflicts, ArrayList<String[]> foundConflicts,
            ArrayList<ArrayList<String[]>> executedInteractions, ArrayList<ArrayList<String[]>> interactionsList,
            Window window, ArrayList<JPanel> panels, ArrayList<Graph> graphs)
    {
        for(String[] conflict:conflictsList)
        {
            Solver s = new Solver();
            setAdditionalVariables(panels, s);
            setVariables(diseases, therapiesOfDiseases, s, graphs);
            for(Integer k:avoidedConflicts)
            {
                setConflictConstraint(conflictsList.get(k), s);
            }
            setConflictConstraint(conflict, s);
            addConstraintsTrue(s);
            s.findSolution();
            //System.out.println(s.solutionToString());
            if(s.isFeasible()==ESat.FALSE)
            {
                foundConflicts.add(conflict);
                executedInteractions.add(interactionsList.get(conflictsList.indexOf(conflict)));
                //executeInteractions(interactionsList, conflictsList, conflict, therapiesOfDiseases);
            }
            else
            {
                avoidedConflicts.add(conflictsList.indexOf(conflict));
            }
        }
        Solver s = new Solver();
        setAdditionalVariables(panels, s);
        setVariables(diseases, therapiesOfDiseases, s, graphs);
        for(Integer k:avoidedConflicts)
        {
            setConflictConstraint(conflictsList.get(k), s);
        }
        addConstraintsTrue(s);
        s.findSolution();
        ArrayList<String> solutions = new ArrayList<String>();
        do
        {
            String solution = "";
            for(IntVar var:therapiesList)
            {
                if(var.getName().contains("terapia")&&var.getValue()==1)
                {
                    solution=solution+var.getName()+",";
                }
            }
            boolean found = false;
            for(String elem:solutions)
            {
                if(solution.equals(elem))
                {
                    found = true;
                }
            }
            if(!found)
            {
                solutions.add(solution);
            }
        }while(s.nextSolution());
        ArrayList<ArrayList<ArrayList<String>>> therapies = new ArrayList<ArrayList<ArrayList<String>>>();
        for(String elem:solutions)
        {
            ArrayList<ArrayList<String>> therapiesSolution = new ArrayList<ArrayList<String>>();
            String[] array = elem.split(",");
            for(String disease:diseases)
            {
                for(int i=0;i<array.length;i++)
                {
                    if(array[i].startsWith(disease))
                    {
                        therapiesSolution.add(therapiesOfDiseases.get(diseases.indexOf(disease))
                               .get(Integer.valueOf(array[i].substring(array[i].lastIndexOf("_terapia")+8))));
                        
                    }
                }
            }
            therapies.add(therapiesSolution);
        }
        window.setResults(foundConflicts, executedInteractions, therapies);
    }
}
