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
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import model.Persoon;

/**
 *
 * @author Method
 */
public class WijzigKunstenaar extends javax.swing.JFrame {
    PersoonFrame persoonFrame;
    Persoon persoon;
    /**
     * Creates new form WijzigKunstenaar
     */
    public WijzigKunstenaar(Persoon persoon, PersoonFrame persoonFrame) {
        initComponents();
        this.setTitle("Kunstenaar Wijzigen");
        this.persoonFrame = persoonFrame;
        this.persoon = persoon;
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        vulInvoerVelden();
    }

    private void vulInvoerVelden() {
        this.txfVoornaam.setText(persoon.getVoornaam());
        this.txfTussenvoegsel.setText(persoon.getTussenvoegsel());
        this.txfAchternaam.setText(persoon.getAchternaam());
        this.txfGeboortedatum.setText(persoon.getGeboortedatum());
        this.txfSterfdatum.setText(persoon.getSterfdatum());
    }
    // Wijzig persoon Gegevens
    private void wijzigPersoonGegevens() {
        String Voornaam = this.txfVoornaam.getText();
        String Tussenvoegsel = this.txfTussenvoegsel.getText();
        String Achternaam = this.txfAchternaam.getText();

        try {
            Connection conn = DataBaseConnector.getConnection();
            Statement stat = conn.createStatement();

            String prepStatWijzigPersoon = "UPDATE Persoon SET voornaam=?,"
                    + "tussenvoegsel=?, achternaam=? WHERE persoon.id = ?";
            PreparedStatement prepStat = conn.prepareStatement(prepStatWijzigPersoon);

            prepStat.setString(1, Voornaam);
            prepStat.setString(2, Tussenvoegsel);
            prepStat.setString(3, Achternaam);
            prepStat.setString(4, persoon.getId());

            prepStat.executeUpdate();

            stat.close();

        } catch (SQLException exc) {
            JOptionPane.showMessageDialog(this, "Wijzigingen voor persoon niet opgeslagen\n" + exc.toString());
        }
    }
    // Wijzig Klant gegevens
    private void wijzigKlantGegevens() {
        String Geboortedatum = this.txfGeboortedatum.getText();
        String Sterfdatum = this.txfSterfdatum.getText();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
        
        try {
            Connection conn = DataBaseConnector.getConnection();
            Statement stat = conn.createStatement();

            String prepStatWijzigPersoon = "UPDATE Kunstenaar SET geboortedatum=?,"
                    + "sterfdatum=? WHERE persoon_id = ?";
            PreparedStatement prepStat = conn.prepareStatement(prepStatWijzigPersoon);
            
            try {
            Date dateGeboortedatum = formatter.parse(Geboortedatum);
            Date dateSterfdatum = formatter.parse(Sterfdatum);

            java.sql.Date sqlDateGeboortedatum = convertUtilToSql(dateGeboortedatum);
            java.sql.Date sqlDateSterfdatum = convertUtilToSql(dateSterfdatum);
            
            
            prepStat.setDate(1, sqlDateGeboortedatum);
            prepStat.setDate(2, sqlDateSterfdatum);
            prepStat.setString(3, persoon.getId());

            prepStat.executeUpdate();

            stat.close();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            
        } catch (SQLException exc) {
            JOptionPane.showMessageDialog(this, "Wijzigingen voor Kunstenaar niet opgeslagen\n" + exc.toString());
        }
    }
    
    // TODO: common file
    public boolean checkDatum() {
        String Geboortedatum = txfGeboortedatum.getText();
        String Sterfdatum = txfSterfdatum.getText();
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
        Date dateGeboortedatum = formatter.parse(Geboortedatum);
        Date dateSterfdatum = formatter.parse(Sterfdatum);
        
        if(dateGeboortedatum.before(dateSterfdatum)) {
            return true;
        } else {
            controlPersoonFrameOptie.bevestigingLegeVelden("Het sterfdatum moet NA het geboortedatum!");
            return false;
        }
        
        } catch (ParseException e){
            controlPersoonFrameOptie.bevestigingLegeVelden("Datum moet het formaat 'yyyy-MM-DD' hebben");
            // e.printStackTrace(); No need for parse if user is forced to enter
        }
        return false;
    }
    
    private boolean wijzigenController() {
        if(!checkDatum())
            return false;
        
        wijzigPersoonGegevens();
        wijzigKlantGegevens();
        this.setVisible(false);
        this.persoonFrame.vulPersoonTabelMetKunstenaars();
        
        return true;
    }
    
    private static java.sql.Date convertUtilToSql(java.util.Date uDate) {

        java.sql.Date sDate = new java.sql.Date(uDate.getTime());

        return sDate;

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
        Geboortedatum = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        txfVoornaam = new javax.swing.JTextField();
        txfTussenvoegsel = new javax.swing.JTextField();
        txfAchternaam = new javax.swing.JTextField();
        txfGeboortedatum = new javax.swing.JTextField();
        txfSterfdatum = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Kunstenaar Wijzigen");

        jLabel2.setText("Voornaam:");

        jLabel3.setText("Tussenvoegsel:");

        jLabel4.setText("Achternaam:");

        Geboortedatum.setText("Geboortejaar:");

        jLabel6.setText("Sterfdatum:");

        jButton1.setText("Wijzigen");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(Geboortedatum)
                    .addComponent(jLabel6))
                .addGap(42, 42, 42)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton1)
                    .addComponent(jLabel1)
                    .addComponent(txfVoornaam)
                    .addComponent(txfTussenvoegsel)
                    .addComponent(txfAchternaam)
                    .addComponent(txfGeboortedatum)
                    .addComponent(txfSterfdatum, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE))
                .addContainerGap(148, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txfVoornaam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txfTussenvoegsel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txfAchternaam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Geboortedatum)
                    .addComponent(txfGeboortedatum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txfSterfdatum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(65, 65, 65))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        wijzigenController();
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Geboortedatum;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JTextField txfAchternaam;
    private javax.swing.JTextField txfGeboortedatum;
    private javax.swing.JTextField txfSterfdatum;
    private javax.swing.JTextField txfTussenvoegsel;
    private javax.swing.JTextField txfVoornaam;
    // End of variables declaration//GEN-END:variables
}
