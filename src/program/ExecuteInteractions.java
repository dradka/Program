/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package program;

import java.util.ArrayList;

/**
 *
 * @author Darek
 */
public class ExecuteInteractions {
    public static void executeInteractions(ArrayList<ArrayList<String[]>> interactionsList, ArrayList<String[]> conflictsList, String[] conflict, 
            ArrayList<ArrayList<ArrayList<String>>> therapiesOfDiseases)
    {
        ArrayList<String[]> interactions = interactionsList.get(conflictsList.indexOf(conflict));
        for(String[] interaction:interactions)
        {
            if(interaction[0].equals("replace"))
            {
                for(ArrayList<ArrayList<String>> therapiesOfDisease:therapiesOfDiseases)
                {
                    for(ArrayList<String> therapy:therapiesOfDisease)
                    {
                        String medicineVar = null;
                        for(String medicine:therapy)
                        {
                            if(medicine.equals(interaction[1])
                               ||(medicine.indexOf('=')>=0 && !(medicine.indexOf('?')>=0)
                                    && medicine.substring(0,medicine.indexOf('=')).equals(interaction[1])))
                            {
                                medicineVar = medicine;
                            }
                        }
                        if(medicineVar!=null)
                        {
                            therapy.set(therapy.indexOf(medicineVar), interaction[3]);
                        }
                    }
                }
            }
            else if(interaction[0].equals("add"))
            {
                for(ArrayList<ArrayList<String>> therapiesOfDisease:therapiesOfDiseases)
                {
                    for(ArrayList<String> therapy:therapiesOfDisease)
                    {
                        String medicineVar = null;
                        for(String medicine:therapy)
                        {
                            if(medicine.equals(interaction[3])
                               ||(medicine.indexOf('=')>=0 && !(medicine.indexOf('?')>=0)
                                    && medicine.substring(0,medicine.indexOf('=')).equals(interaction[3])))
                            {
                                medicineVar = medicine;
                            }
                        }
                        if(medicineVar!=null)
                        {
                          if(interaction[2].equals("after"))
                          {
                              therapy.add(therapy.indexOf(medicineVar)+1, interaction[1]);
                          }
                          else if(interaction[2].equals("before"))
                          {
                              therapy.add(therapy.indexOf(medicineVar), interaction[1]);
                          }
                        }
                    }
                } 
            }
            else if(interaction[0].equals("remove"))
            {
                for(ArrayList<ArrayList<String>> therapiesOfDisease:therapiesOfDiseases)
                {
                    for(ArrayList<String> therapy:therapiesOfDisease)
                    {
                        String medicineVar = null;
                        for(String medicine:therapy)
                        {
                            if(medicine.equals(interaction[1])
                               ||(medicine.indexOf('=')>=0 && !(medicine.indexOf('?')>=0)
                                    && medicine.substring(0,medicine.indexOf('=')).equals(interaction[1])))
                            {
                                medicineVar = medicine;
                            }
                        }
                        if(medicineVar!=null)
                        {
                            therapy.remove(medicineVar);
                        }
                    }
                } 
            }
            else if(interaction[0].equals("increase_dosage")||interaction[0].equals("decrease_dosage")
                    ||interaction[0].equals("change_dosage"))
            {
                for(ArrayList<ArrayList<String>> therapiesOfDisease:therapiesOfDiseases)
                {
                    for(ArrayList<String> therapy:therapiesOfDisease)
                    {
                        String medicineVar = null;
                        for(String medicine:therapy)
                        {
                            if(medicine.indexOf('=')>=0 && !(medicine.indexOf('?')>=0)
                                    && medicine.substring(0,medicine.indexOf('=')).equals(interaction[1]))
                            {
                                medicineVar = medicine;
                            }
                        }
                        if(medicineVar!=null)
                        {
                            int dosage = Integer.parseInt(medicineVar.substring(medicineVar.indexOf('=')+1));
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
                             therapy.set(therapy.indexOf(medicineVar), 
                                     medicineVar.substring(0, medicineVar.indexOf('=')+1)+String.valueOf(dosage));
                            
                        }
                    }
                } 
            }
        }
    }
}
