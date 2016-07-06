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
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.Schilderij;
import model.Uitlening;

/**
 *
 * @author Method
 */
public class SchilderijFrame extends javax.swing.JFrame {
    private ArrayList<Schilderij> schilderij;

    /**
     * Creates new form SchilderijFrame
     */
    public SchilderijFrame() {
        initComponents();
        this.setDefaultCloseOperation(PersoonFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        
        schilderij = new ArrayList();
        vulSchilderijTabel();
    }
    
    // Overzicht tabel vullen met ALLEEN klanten.
    public void vulSchilderijTabel() {
        schilderij.clear();
        
        //Definieren van de header kolommen.
        String[] kolommen = {   // Kolom nummer
            "id",               // 0             
            "naam",             // 1   
            "jaar",             // 2     
            "kunstenaar",       // 3      
            "uitleningen",      // 4
            "waardering",       // 5
            "waarde"};          // 6

        DefaultTableModel overzichtModel = new DefaultTableModel(kolommen, 0);
        
        //Maak het tabel leeg
        DefaultTableModel clearModel = (DefaultTableModel) tblOverzichtSchilderij.getModel();
        clearModel.setRowCount(0);
        
        //Klanten ophalen uit database
        try {
            Connection conn = DataBaseConnector.getConnection();
            String selectTafelIndeling = "select * from schilderij\n" +
                                            "order by naam ASC;";
                    
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(selectTafelIndeling);

            DecimalFormat df = new DecimalFormat("0.00");
            
            //Resultaat doorlopen.
            while (result.next()) {
                String id = result.getString("id");
                String naam = result.getString("naam");
                String jaar = result.getString("jaar");
                String kunstenaar = result.getString("kunstenaar");
                String uitleningen = result.getString("uitleningen");
                String waardering = result.getString("waardering");
                String waarde = result.getString("waarde");
                
                double test = Double.parseDouble(waardering);
                //String convKunstenaar_id = schilderIdNaarNaam(Integer.parseInt(kunstenaar));
                
                Schilderij _schilderij = new Schilderij(id, naam, jaar, kunstenaar, uitleningen, df.format(test), waarde);
                schilderij.add(_schilderij);
            }

        } catch (SQLException exc) {
            JOptionPane.showMessageDialog(this, "Fout bij het ophalen van de "
                    + "personen uit de database"
                    + exc.toString());
        }
        
        // Accepteert geen primitive int, alleen objecten.
        for (Schilderij schilderij : schilderij) {
            overzichtModel.addRow(schilderij.getSchilderijInfo());;
        }

        //model koppelen aan JTable
        this.tblOverzichtSchilderij.setModel(overzichtModel);
        
        // Verberg een kolom bij index 0(ID)
        tblOverzichtSchilderij.getColumnModel().getColumn(0).setMinWidth(0);
        tblOverzichtSchilderij.getColumnModel().getColumn(0).setMaxWidth(0);
    }
    
    private boolean checkUitleningenActief() {
        try {
            Connection conn = DataBaseConnector.getConnection();
            String selectTafelIndeling = "select einddatum from uitlening where einddatum > CURDATE()";
                    
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(selectTafelIndeling);
            
            if(!result.next())
                return true;
            else
                return false;
        } catch (SQLException exc) {
            // error
            return false;
        }
    }
    private boolean verwijderSchilderij() {
        if(!checkUitleningenActief())
            return false;

        //Achterhalen welke speler geselecteerd is.
        int index = this.tblOverzichtSchilderij.getSelectedRow();
        Schilderij schilderij = this.schilderij.get(index);

        //Vragen of de geselecteerde speler echt verwijderd moet worden. 
        int antwoord = JOptionPane.showConfirmDialog(null, "Weet je zeker dat u deze schilderij wilt verwijderen? ", "Verwijder Schilderij", JOptionPane.YES_NO_OPTION);
        if (antwoord == JOptionPane.YES_OPTION) {

            boolean gelukt = true;
            try {
                Connection conn = DataBaseConnector.getConnection();
                Statement stat = conn.createStatement();

                String prepStatVerwijderSchilderij = "DELETE FROM schilderij WHERE id = ?";

                PreparedStatement prepStat = conn.prepareStatement(prepStatVerwijderSchilderij);

                prepStat.setString(1, schilderij.getId());

                prepStat.executeUpdate();
                
            } catch (SQLException exc) {
                System.err.println(exc);
                gelukt = false;
            }

            if (gelukt) {
                this.vulSchilderijTabel();
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "U kan geen schilderij verwijderen als er nog uitleningen actief zijn");
                return false;
            }
        }
        return false;
    }
    
    public boolean verwijderSchilderijForce(int Kunstenaar_id) {
        if(checkUitleningenActief())
            return false;
        try {
                Connection conn = DataBaseConnector.getConnection();
                Statement stat = conn.createStatement();

                String prepStatVerwijderSchilderij = "DELETE FROM schilderij WHERE id = ?";

                PreparedStatement prepStat = conn.prepareStatement(prepStatVerwijderSchilderij);

                prepStat.setInt(1, Kunstenaar_id);

                prepStat.executeUpdate();
                
                return true;
                
            } catch (SQLException exc) {
                System.err.println(exc);
                return false;
            }
    }
    /* Disc
    private void verwijderUitleningForce(int Kunstenaar_id) {
        try {
                Connection conn = DataBaseConnector.getConnection();
                Statement stat = conn.createStatement();

                String prepStatVerwijderSchilderij = "DELETE FROM uitlening WHERE id = ?";

                PreparedStatement prepStat = conn.prepareStatement(prepStatVerwijderSchilderij);

                prepStat.setInt(1, Kunstenaar_id);

                prepStat.executeUpdate();
                
            } catch (SQLException exc) {
                System.err.println(exc);
            }
    }
    */
    
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
    
    private boolean checkSchilderijBoundException() {
        if(this.tblOverzichtSchilderij.getSelectedRow() == -1) {
            return false;
        }
        
        int index = this.tblOverzichtSchilderij.getSelectedRow();        

        Schilderij selectedKlant = (Schilderij) this.schilderij.get(index);

        WijzigSchilderij wijzigSchilderij = new WijzigSchilderij(selectedKlant, this);
        wijzigSchilderij.setVisible(true);
        
        return true;
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
        tblOverzichtSchilderij = new javax.swing.JTable();
        btnToevoegen = new javax.swing.JButton();
        btnWijzigen = new javax.swing.JButton();
        btnVerwijderen = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txfZoekenSchilderij = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Schilderij | Gallerie");

        tblOverzichtSchilderij.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "id", "Naam", "Jaar", "Kunstenaar", "Uitleningen", "Waardering", "Waarde"
            }
        ));
        jScrollPane1.setViewportView(tblOverzichtSchilderij);

        btnToevoegen.setText("Toevoegen");
        btnToevoegen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnToevoegenActionPerformed(evt);
            }
        });

        btnWijzigen.setText("Wijzigen");
        btnWijzigen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnWijzigenActionPerformed(evt);
            }
        });

        btnVerwijderen.setText("Verwijderen");
        btnVerwijderen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerwijderenActionPerformed(evt);
            }
        });

        jLabel2.setText("Zoeken:");

        txfZoekenSchilderij.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txfZoekenSchilderijKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnVerwijderen)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(btnWijzigen, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnToevoegen, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txfZoekenSchilderij, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE)
                        .addGap(12, 12, 12))))
            .addGroup(layout.createSequentialGroup()
                .addGap(272, 272, 272)
                .addComponent(jLabel1)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txfZoekenSchilderij, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnToevoegen)
                        .addGap(18, 18, 18)
                        .addComponent(btnWijzigen)
                        .addGap(18, 18, 18)
                        .addComponent(btnVerwijderen))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnToevoegenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToevoegenActionPerformed
        new ToevoegenSchilderij(this).setVisible(true);
    }//GEN-LAST:event_btnToevoegenActionPerformed

    private void btnWijzigenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnWijzigenActionPerformed
        checkSchilderijBoundException();
    }//GEN-LAST:event_btnWijzigenActionPerformed

    private void btnVerwijderenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerwijderenActionPerformed
        verwijderSchilderij();
    }//GEN-LAST:event_btnVerwijderenActionPerformed

    private void txfZoekenSchilderijKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txfZoekenSchilderijKeyReleased
       if(txfZoekenSchilderij.getText().isEmpty()){
            this.vulSchilderijTabel();
        }
       
       String criteria = txfZoekenSchilderij.getText();
       
       schilderij.clear();
        
        //Definieren van de header kolommen.
        String[] kolommen = {   // Kolom nummer
            "id",               // 0             
            "naam",             // 1   
            "jaar",             // 2     
            "kunstenaar",       // 3      
            "uitleningen",      // 4
            "waardering",       // 5
            "waarde"};          // 6

        DefaultTableModel overzichtModel = new DefaultTableModel(kolommen, 0);
        
        //Maak het tabel leeg
        DefaultTableModel clearModel = (DefaultTableModel) tblOverzichtSchilderij.getModel();
        clearModel.setRowCount(0);
        
        //Klanten ophalen uit database
        try {
            Connection conn = DataBaseConnector.getConnection();
            String selectTafelIndeling = "select * from schilderij WHERE naam like '%"+ criteria+"%';";
                    
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(selectTafelIndeling);

            DecimalFormat df = new DecimalFormat("0.00");
            
            //Resultaat doorlopen.
            while (result.next()) {
                String id = result.getString("id");
                String naam = result.getString("naam");
                String jaar = result.getString("jaar");
                String kunstenaar = result.getString("kunstenaar");
                String uitleningen = result.getString("uitleningen");
                String waardering = result.getString("waardering");
                String waarde = result.getString("waarde");
                
                double test = Double.parseDouble(waardering);
                System.out.println(df.format(test));
                
                Schilderij _schilderij = new Schilderij(id, naam, jaar, kunstenaar, uitleningen, df.format(test), waarde);
                schilderij.add(_schilderij);
            }

        } catch (SQLException exc) {
            JOptionPane.showMessageDialog(this, "Fout bij het ophalen van de "
                    + "personen uit de database"
                    + exc.toString());
        }
        
        // Accepteert geen primitive int, alleen objecten.
        for (Schilderij schilderij : schilderij) {
            overzichtModel.addRow(schilderij.getSchilderijInfo());;
        }

        //model koppelen aan JTable
        this.tblOverzichtSchilderij.setModel(overzichtModel);
        
        // Verberg een kolom bij index 0(ID)
        tblOverzichtSchilderij.getColumnModel().getColumn(0).setMinWidth(0);
        tblOverzichtSchilderij.getColumnModel().getColumn(0).setMaxWidth(0);
    }//GEN-LAST:event_txfZoekenSchilderijKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnToevoegen;
    private javax.swing.JButton btnVerwijderen;
    private javax.swing.JButton btnWijzigen;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblOverzichtSchilderij;
    private javax.swing.JTextField txfZoekenSchilderij;
    // End of variables declaration//GEN-END:variables
}
