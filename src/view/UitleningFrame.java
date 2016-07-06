/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import databaseUtil.DataBaseConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.Schilderij;
import model.Uitlening;
/**
 *
 * @author Method
 */
public class UitleningFrame extends javax.swing.JFrame {
     private ArrayList<Uitlening> uitlening;
    /**
     * Creates new form UitleningFrame
     */
    public UitleningFrame() {
        initComponents();
        this.setTitle("Uitleningen | Gallerie");
        this.setDefaultCloseOperation(PersoonFrame.DISPOSE_ON_CLOSE);
        
         uitlening = new ArrayList();
        vulTabelMetUitleningen();
    }

    private String klantIdNaarNaam(int id) {
        try {
            Connection conn = DataBaseConnector.getConnection();
            String selectNaam = "select voornaam, tussenvoegsel, achternaam from persoon"
                                + " WHERE id = " + id;
            
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(selectNaam);
            
            while(result.next()) {
                String voornaam = result.getString("voornaam");
                String tussenvoegsel = result.getString("tussenvoegsel");
                String achternaam = result.getString("achternaam");
                
                String samenvoegen = voornaam + " " + tussenvoegsel + " " + achternaam;
                
                return samenvoegen;
            }
        } catch (SQLException exc){
            JOptionPane.showMessageDialog(this, "Fout bij het ophalen van de "
                    + "uitleningen uit de database"
                    + exc.toString());
        }
        return "";
    }
    private String schilderIdNaarNaam(int id) {
        try {
            Connection conn = DataBaseConnector.getConnection();
            String selectNaam = "select naam from schilderij\n" +
                                "where id = " + id;
            
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(selectNaam);
            
            while(result.next()) {
                String naam = result.getString("naam");
                
                return naam;
            }
        } catch (SQLException exc){
            JOptionPane.showMessageDialog(this, "Fout bij het ophalen van de "
                    + "uitleningen uit de database"
                    + exc.toString());
        }
        return "";
    }
    
    public void vulTabelMetUitleningen() {
        uitlening.clear();
        
        //Definieren van de header kolommen.
        String[] kolommen = {   // Kolom nummer
            "id",               // 0             
            "klant_id",         // 1   
            "schilderij_id",    // 2     
            "start_datum",      // 3      
            "eind_datum",       // 4
            "waardering"};      // 5

        DefaultTableModel overzichtModel = new DefaultTableModel(kolommen, 0);
        
        //Maak het tabel leeg
        DefaultTableModel clearModel = (DefaultTableModel) tblOverzichtUitlening.getModel();
        clearModel.setRowCount(0);
        
        //Klanten ophalen uit database
        try {
            Connection conn = DataBaseConnector.getConnection();
            String selectTafelIndeling = "SELECT * FROM uitlening;";
                    
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(selectTafelIndeling);

            //Resultaat doorlopen.
            while (result.next()) {
                String id = result.getString("id");
                String klant_id = result.getString("klant_id");
                String schilderij_id = result.getString("schilderij_id");
                String start_datum = result.getString("startdatum");
                String eind_datum = result.getString("einddatum");
                String waardering = result.getString("waardering");
                
                String convKlant_Id = "";
                String convSchilderij_id = "";
                if(klant_id != null){
                    convKlant_Id = klantIdNaarNaam(Integer.parseInt(klant_id));
                }
                if(schilderij_id != null){
                    convSchilderij_id = schilderIdNaarNaam(Integer.parseInt(schilderij_id));
                }
                
                Uitlening klant = new Uitlening(id, convKlant_Id, convSchilderij_id, start_datum, eind_datum, waardering);
                uitlening.add(klant);
            }

        } catch (SQLException exc) {
            JOptionPane.showMessageDialog(this, "Fout bij het ophalen van de "
                    + "uitleningen uit de database"
                    + exc.toString());
        }
        
        // Accepteert geen primitive int, alleen objecten.
        for (Uitlening uitlening : uitlening) {
            overzichtModel.addRow(uitlening.getUitleningInfo());
        }

        //model koppelen aan JTable
        this.tblOverzichtUitlening.setModel(overzichtModel);
        
        // Verberg een kolom bij index 0(ID)
        tblOverzichtUitlening.getColumnModel().getColumn(0).setMinWidth(0);
        tblOverzichtUitlening.getColumnModel().getColumn(0).setMaxWidth(0);
    }
    
    private boolean checkUitleningBoundException() {
        if(this.tblOverzichtUitlening.getSelectedRow() == -1) {
            return false;
        }
        
        int index = this.tblOverzichtUitlening.getSelectedRow();        

        Uitlening selectedUitlening = (Uitlening) this.uitlening.get(index);

        WijzigUitlening wijzigUitlening = new WijzigUitlening(selectedUitlening, this);
        wijzigUitlening.setVisible(true);
        return true;
    }
    
    public void verwijderUitleen() {
        //Achterhalen welke speler geselecteerd is.
        int index = this.tblOverzichtUitlening.getSelectedRow();
        Uitlening uitlening = this.uitlening.get(index);

        //Vragen of de geselecteerde speler echt verwijderd moet worden. 
        int antwoord = JOptionPane.showConfirmDialog(null, "Weet je zeker dat u deze uitlening wilt verwijderen? ", "Verwijder speler", JOptionPane.YES_NO_OPTION);
        if (antwoord == JOptionPane.YES_OPTION) {

            boolean gelukt = true;
            try {
                Connection conn = DataBaseConnector.getConnection();
                Statement stat = conn.createStatement();

                String prepStatVerwijderSpeler = "DELETE FROM uitlening WHERE id = ?";

                PreparedStatement prepStat = conn.prepareStatement(prepStatVerwijderSpeler);

                prepStat.setString(1, uitlening.getId());

                prepStat.executeUpdate();
            } catch (SQLException exc) {
                System.err.println(exc);
                gelukt = false;
            }

            if (gelukt) {
                this.vulTabelMetUitleningen();
            } else {
                JOptionPane.showMessageDialog(null, "Het verwijderen van de uitlening is niet gelukt");
            }
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOverzichtUitlening = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Uitleningen | Gallerie");

        tblOverzichtUitlening.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "id", "klant", "schilderij", "start_datum", "eind_datum", "waardering"
            }
        ));
        jScrollPane1.setViewportView(tblOverzichtUitlening);

        jButton1.setText("Toevoegen");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Wijzigen");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Verwijderen");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton3)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jButton2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 516, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(268, 268, 268)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2)
                        .addGap(18, 18, 18)
                        .addComponent(jButton3))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        new ToevoegenUitlening(this).setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        checkUitleningBoundException();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        verwijderUitleen();
    }//GEN-LAST:event_jButton3ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblOverzichtUitlening;
    // End of variables declaration//GEN-END:variables
}
