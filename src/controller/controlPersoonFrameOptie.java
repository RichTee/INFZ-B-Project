/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import javax.swing.JOptionPane;

/**
 *
 * @author Method
 */
public class controlPersoonFrameOptie {

    public static void bevestigingLegeVelden(String feedback){
         JOptionPane.showConfirmDialog(null, 
                            feedback, "Informatie bevestiging ",
                            JOptionPane.WARNING_MESSAGE);
    }
}
