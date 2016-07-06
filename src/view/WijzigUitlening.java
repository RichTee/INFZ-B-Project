/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.controlPersoonFrameOptie;
import databaseUtil.DataBaseConnector;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import model.Uitlening;
import static view.ToevoegenUitlening.getCurrentDate;

/**
 *
 * @author Method
 */
public class WijzigUitlening extends javax.swing.JFrame {
    Uitlening uitlening;
    UitleningFrame uitleningFrame;
    /**
     * Creates new form WijzigUitlening
     */
    public WijzigUitlening(Uitlening uitlening, UitleningFrame uitleningFrame) {
        initComponents();
        this.setVisible(true);
        this.setTitle("Uitlening Wijzigen");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        this.uitlening = uitlening;
        this.uitleningFrame = uitleningFrame;
        
        vulInvoerVelden();
        cbbKlantVullen();
        cbbSchilderijVullen();
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
               int a = JOptionPane.showConfirmDialog(null, 
                            "Weet u zeker dat u deze scherm wilt sluiten?", "Scherm sluiten ",
                            JOptionPane.YES_NO_OPTION);
               if(a==JOptionPane.OK_OPTION){
                   dispose(); // Dispose de window
               }
           }
        });
    }

    private void vulInvoerVelden() {
        this.txfStartdatum.setText(uitlening.getStartdatum());
        this.txfEinddatum.setText(uitlening.getEinddatum());
        this.txfWaardering.setText(uitlening.getWaardering());
    }
    
    private boolean wijzigUitleningGegevens(int Klant_id, int Schilderij_id) {
        
        String Startdatum = this.txfStartdatum.getText();
        String Einddatum = this.txfEinddatum.getText();
        double Waarde = Double.parseDouble(this.txfWaardering.getText());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
        
        if(!checkDatum())
            return false;
        
        if(!checkEinddatumleen(Schilderij_id, Startdatum))
            return false;
        
        try {
            Connection conn = DataBaseConnector.getConnection();
            Statement stat = conn.createStatement();

            String prepStatWijzigSchilderij = "UPDATE uitlening SET klant_id=?,"
                    + "schilderij_id=?, startdatum=?, einddatum=?, waardering=? WHERE id = ?";
            PreparedStatement prepStat = conn.prepareStatement(prepStatWijzigSchilderij);
            try {
            Date dateStartdatum = formatter.parse(Startdatum);
            Date dateEinddatum = formatter.parse(Einddatum);

            java.sql.Date sqlDateStartdatum = convertUtilToSql(dateStartdatum);
            java.sql.Date sqlDateEinddatum = convertUtilToSql(dateEinddatum);
            
            System.out.println(sqlDateStartdatum);
            System.out.println(sqlDateEinddatum);
            
            prepStat.setInt(1, Klant_id);
            prepStat.setInt(2, Schilderij_id);
            prepStat.setDate(3, sqlDateStartdatum);
            prepStat.setDate(4, sqlDateEinddatum);
            prepStat.setDouble(5, Waarde);
            prepStat.setString(6, uitlening.getId());

            prepStat.executeUpdate();

            stat.close();
            return true;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (SQLException exc) {
            JOptionPane.showMessageDialog(this, "Wijzigingen voor persoon niet opgeslagen\n" + exc.toString());
            return false;
        }
        return false;
    }
    
    // ComboBox vullen met kunstenaars
    private void cbbKlantVullen() {
        DefaultComboBoxModel model = (DefaultComboBoxModel) cbbKlant.getModel();
        model.removeAllElements();

        try{
            Connection conn = DataBaseConnector.getConnection();
            String getKunstenaar = "select id, voornaam, tussenvoegsel, achternaam from persoon\n" 
                                + "where persoon.type = \"kl\";";

            Statement statement = conn.createStatement();
            ResultSet  result = statement.executeQuery(getKunstenaar);
            
            if(!result.isBeforeFirst()) { } 
            else {
                while(result.next()){
                    String samenvoegen = result.getInt("id") + " " +
                                        result.getString("voornaam") + " " +
                                        result.getString("tussenvoegsel") +" " +  
                                        result.getString("achternaam");
                    
                    cbbKlant.addItem(samenvoegen);
                }
            }
        } catch (SQLException exc) {
            System.err.println("Error Code: " + 
                    ((SQLException)exc).getMessage());
        }
    }
    
    private void cbbSchilderijVullen() {
        DefaultComboBoxModel model = (DefaultComboBoxModel) cbbSchilderij.getModel();
        model.removeAllElements();

        try{
            Connection conn = DataBaseConnector.getConnection();
            String getKunstenaar = "select id, naam, kunstenaar from schilderij\n";

            Statement statement = conn.createStatement();
            ResultSet  result = statement.executeQuery(getKunstenaar);
            
            if(!result.isBeforeFirst()) { } 
            else {
                while(result.next()){
                    int id = result.getInt("id");
                    String naam = result.getString("naam");
                    int kunstenaar_id = result.getInt("kunstenaar");
                    
                    String samenvoegen = id + " " + naam + " " + kunstenaarIdNaarNaam(kunstenaar_id);
                    cbbSchilderij.addItem(samenvoegen);
                }
            }
        } catch (SQLException exc) {
            System.err.println("Error Code: " + 
                    ((SQLException)exc).getMessage());
        }
    }
    // Get huidige datum
    public static String getCurrentDate() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }
    
    private boolean checkDatum() {
        String Startdatum = txfStartdatum.getText();
        String Einddatum = txfEinddatum.getText();
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
        Date dateStartdatum = formatter.parse(Startdatum);
        Date dateEinddatum = formatter.parse(Einddatum);
        Date dateHuidigedatum = formatter.parse(getCurrentDate());
        
        if(dateStartdatum.before(dateEinddatum)) {
            return true;
        } else {
            controlPersoonFrameOptie.bevestigingLegeVelden("Het einddatum moet na het startdatum en startDatum 1 dag na het huidige datum!");
            return false;
        }
        
        } catch (ParseException e){
            e.printStackTrace();
        }
        return false;
    }
    
    // Schilderijen die uitgeleent zijn mogen alleen na de einddatum opnieuw uitgeleent worden.
    private boolean checkEinddatumleen(int Schilderij_id, String Startdatum) {
        try {
            Connection conn = DataBaseConnector.getConnection();
            String selectNaam = "select einddatum from uitlening where einddatum >= \""
                    + Startdatum
                    + "\" AND schilderij_id = " + Schilderij_id;
            
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(selectNaam);
            
            while(result.next()) {
                System.out.println("#call");
                controlPersoonFrameOptie.bevestigingLegeVelden("Startdatum van een nieuwe lening moet na de oude lening zijn");
                return false;
            }
        } catch (SQLException exc){
            JOptionPane.showMessageDialog(this, "Fout bij het ophalen van de "
                    + "uitleningen uit de database"
                    + exc.toString());
        }
        return true;
    }
    
    private String kunstenaarIdNaarNaam(int kunstenaar_id) {
        try {
            Connection conn = DataBaseConnector.getConnection();
            String getKunstenaar = "select voornaam, tussenvoegsel, achternaam from persoon"
                                   + " WHERE id = " + kunstenaar_id;

            Statement statement = conn.createStatement();
            ResultSet  result = statement.executeQuery(getKunstenaar);
            
            while(result.next()){
                String voornaam = result.getString("voornaam");
                String tussenvoegsel = result.getString("tussenvoegsel");
                String achternaam =  result.getString("achternaam");
                
                String samenvoegen = voornaam + " " + tussenvoegsel + " " + achternaam;
                
                return samenvoegen;
            }
            
        } catch (SQLException exc) {
            System.err.println("Error Code: " + 
                    ((SQLException)exc).getMessage());
        }
        return "";
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
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        cbbKlant = new javax.swing.JComboBox();
        cbbSchilderij = new javax.swing.JComboBox();
        txfStartdatum = new javax.swing.JTextField();
        txfEinddatum = new javax.swing.JTextField();
        txfWaardering = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        lblStartdatum = new javax.swing.JLabel();
        lblEinddatum = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Uitlening Wijzigen");

        jLabel2.setText("Klant:");

        jLabel3.setText("Schilderij:");

        jLabel4.setText("Startdatum:");

        jLabel5.setText("Einddatum:");

        jLabel6.setText("Waardering:");

        cbbKlant.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cbbSchilderij.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        txfStartdatum.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txfStartdatumKeyReleased(evt);
            }
        });

        txfEinddatum.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txfEinddatumKeyReleased(evt);
            }
        });

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
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addGap(80, 80, 80)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1)
                            .addComponent(txfStartdatum)
                            .addComponent(cbbSchilderij, 0, 125, Short.MAX_VALUE)
                            .addComponent(cbbKlant, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txfEinddatum)
                            .addComponent(txfWaardering))
                        .addGap(27, 27, 27)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblEinddatum, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                            .addComponent(lblStartdatum, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cbbKlant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(cbbSchilderij, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txfStartdatum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblStartdatum, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txfEinddatum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblEinddatum, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(txfWaardering, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Object getSelectedKlant = cbbKlant.getSelectedItem();
        Object getSelectedSchilderij = cbbSchilderij.getSelectedItem();
        
        String klant_id = getSelectedKlant.toString();
        String schilderij_id = getSelectedSchilderij.toString();
        
        int selectedKlantId = Integer.parseInt(klant_id.substring(0, klant_id.indexOf(' ')));
        int selectedSchilderijId = Integer.parseInt(schilderij_id.substring(0, schilderij_id.indexOf(' ')));
        
        wijzigUitleningGegevens(selectedKlantId, selectedSchilderijId);
        this.setVisible(false);
        this.uitleningFrame.vulTabelMetUitleningen();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txfEinddatumKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txfEinddatumKeyReleased
        lblEinddatum.setText("Voorbeeld: 1995-10-10. YYYY-MM-DD");
    }//GEN-LAST:event_txfEinddatumKeyReleased

    private void txfStartdatumKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txfStartdatumKeyReleased
        lblStartdatum.setText("Voorbeeld: 1995-10-10. YYYY-MM-DD");
    }//GEN-LAST:event_txfStartdatumKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cbbKlant;
    private javax.swing.JComboBox cbbSchilderij;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel lblEinddatum;
    private javax.swing.JLabel lblStartdatum;
    private javax.swing.JTextField txfEinddatum;
    private javax.swing.JTextField txfStartdatum;
    private javax.swing.JTextField txfWaardering;
    // End of variables declaration//GEN-END:variables
}
