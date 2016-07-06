/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.controlPersoonFrameOptie;
import databaseUtil.DataBaseConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import model.Schilderij;

/**
 *
 * @author Method
 */
public class WijzigSchilderij extends javax.swing.JFrame {
    SchilderijFrame schilderijFrame;
    Schilderij schilderij;
    /**
     * Creates new form WijzigSchilderij
     */
    public WijzigSchilderij(Schilderij schilderij, SchilderijFrame schilderijFrame) {
        initComponents();
        this.setTitle("Persoon wijzigen");
        this.schilderij = schilderij;
        this.schilderijFrame = schilderijFrame;
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        cbbKunstenaarVullen();
        vulInvoerVelden();
    }

    private void vulInvoerVelden() {
        this.txfNaam.setText(schilderij.getNaam());
        this.txfJaar.setText(schilderij.getJaar());
        this.txfWaarde.setText(schilderij.getWaarde());
    }
    
    private boolean wijzigSchilderijGegevens(int id) {
        String Naam = this.txfNaam.getText();
        String Jaar = this.txfJaar.getText();
        int Kunstenaar = id;
        double Waarde = Double.parseDouble(this.txfWaarde.getText());

        if(!checkDatum(Kunstenaar, Jaar))
            return false;
        
        try {
            Connection conn = DataBaseConnector.getConnection();
            Statement stat = conn.createStatement();

            String prepStatWijzigSchilderij = "UPDATE schilderij SET naam=?,"
                    + "jaar=?, kunstenaar=?, waarde=? WHERE id = ?";
            PreparedStatement prepStat = conn.prepareStatement(prepStatWijzigSchilderij);

            prepStat.setString(1, Naam);
            prepStat.setString(2, Jaar);
            prepStat.setInt(3, Kunstenaar);
            prepStat.setDouble(4, Waarde);
            prepStat.setString(5, schilderij.getId());

            prepStat.executeUpdate();

            stat.close();
            
            return true;
        } catch (SQLException exc) {
            JOptionPane.showMessageDialog(this, "Wijzigingen voor persoon niet opgeslagen\n" + exc.toString());
        }
        return false;
    }
    // ComboBox vullen met kunstenaars
    private void cbbKunstenaarVullen() {
        DefaultComboBoxModel model = (DefaultComboBoxModel) cbbKunstenaar.getModel();
        model.removeAllElements();

        try{
            Connection conn = DataBaseConnector.getConnection();
            String getKunstenaar = "select id, voornaam, tussenvoegsel, achternaam from persoon\n" 
                                + "where persoon.type = \"ku\";";

            Statement statement = conn.createStatement();
            ResultSet  result = statement.executeQuery(getKunstenaar);
            
            if(!result.isBeforeFirst()) { } 
            else {
                while(result.next()){
                    String samenvoegen = result.getInt("id") + " " +
                                        result.getString("voornaam") + " " +
                                        result.getString("tussenvoegsel") +" " +  
                                        result.getString("achternaam");
                    
                    cbbKunstenaar.addItem(samenvoegen);
                }
            }
        } catch (SQLException exc) {
            System.err.println("Error Code: " + 
                    ((SQLException)exc).getMessage());
        }
    }
    
    // Controlle of datum tussen kunstenaar geboorte en sterf datum zit.
    private boolean checkDatum(int Kunstenaar_id, String Jaar) {
        if(checkSterfdatumIsNull(Kunstenaar_id)){
            try {
                Connection conn = DataBaseConnector.getConnection();

                String getKunstenaar = "SELECT geboortedatum, sterfdatum FROM kunstenaar\n" +
                                        "WHERE  " + "'"+Jaar+"'"
                                        + " > geboortedatum AND persoon_id = " + Kunstenaar_id ;

                Statement statement = conn.createStatement();
                ResultSet  result = statement.executeQuery(getKunstenaar);
                while(result.next()){
                    String geboortedatum = result.getString("geboortedatum");
                    String sterfdatum = result.getString("sterfdatum");

                    System.out.println(geboortedatum + sterfdatum);
                    return true;
                }
            } catch (SQLException exc){
                JOptionPane.showMessageDialog(this, "Datum niet gevonden in de database" + exc.toString());
                return false;
            }
            controlPersoonFrameOptie.bevestigingLegeVelden("Het jaartal is niet tussen het geboortedatum en / of sterfdatum van de kunstenaar.\n Voeg en / of pas de informatie aan om het proces te voltooien.");
            return false;
        } else {
            try {
                Connection conn = DataBaseConnector.getConnection();

                String getKunstenaar = "SELECT geboortedatum, sterfdatum FROM kunstenaar\n" +
                                        "WHERE  " + "'"+Jaar+"'"
                                        + " BETWEEN geboortedatum AND sterfdatum AND persoon_id = " + Kunstenaar_id ;

                Statement statement = conn.createStatement();
                ResultSet  result = statement.executeQuery(getKunstenaar);
                while(result.next()){
                    String geboortedatum = result.getString("geboortedatum");
                    String sterfdatum = result.getString("sterfdatum");

                    System.out.println(geboortedatum + sterfdatum);
                    return true;
                }
            } catch (SQLException exc){
                JOptionPane.showMessageDialog(this, "Datum niet gevonden in de database" + exc.toString());
                return false;
            }
            controlPersoonFrameOptie.bevestigingLegeVelden("Het jaartal is niet tussen het geboortedatum en / of sterfdatum van de kunstenaar.\n Voeg en / of pas de informatie aan om het proces te voltooien.");
            return false;
        }
    }
    // Controlle met sterfdatum null
    private boolean checkSterfdatumIsNull(int Kunstenaar_id){
        try {
            Connection conn = DataBaseConnector.getConnection();
            
            String getKunstenaar = "SELECT sterfdatum FROM kunstenaar where persoon_id = " + Kunstenaar_id;

            Statement statement = conn.createStatement();
            ResultSet  result = statement.executeQuery(getKunstenaar);
            while(result.next()){
                
                String sterfdatum = result.getString("sterfdatum");
                
                if(sterfdatum == null){
                    return true;
                }
            }
        } catch (SQLException exc){
            JOptionPane.showMessageDialog(this, "Datum niet gevonden in de database" + exc.toString());
            return false;
        }
        return false;
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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txfNaam = new javax.swing.JTextField();
        txfJaar = new javax.swing.JTextField();
        txfWaarde = new javax.swing.JTextField();
        cbbKunstenaar = new javax.swing.JComboBox();
        btnWijzigen = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Schilderij Wijzigen");

        jLabel2.setText("Naam:");

        jLabel3.setText("Jaar:");

        jLabel4.setText("Kunstenaar:");

        jLabel5.setText("Waarde:");

        cbbKunstenaar.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnWijzigen.setText("Wijzigen");
        btnWijzigen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnWijzigenActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(152, 152, 152)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4))
                        .addGap(74, 74, 74)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnWijzigen)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txfNaam)
                                .addComponent(txfJaar)
                                .addComponent(txfWaarde)
                                .addComponent(cbbKunstenaar, 0, 125, Short.MAX_VALUE)))))
                .addContainerGap(132, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txfNaam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(txfJaar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cbbKunstenaar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txfWaarde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnWijzigen)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnWijzigenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnWijzigenActionPerformed
        Object getSelectedKunstenaar = cbbKunstenaar.getSelectedItem();
        String id = getSelectedKunstenaar.toString();
        int selectedId = Integer.parseInt(id.substring(0, id.indexOf(' ')));
        
        wijzigSchilderijGegevens(selectedId);
        this.setVisible(false);
        this.schilderijFrame.vulSchilderijTabel();
    }//GEN-LAST:event_btnWijzigenActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnWijzigen;
    private javax.swing.JComboBox cbbKunstenaar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JTextField txfJaar;
    private javax.swing.JTextField txfNaam;
    private javax.swing.JTextField txfWaarde;
    // End of variables declaration//GEN-END:variables
}
