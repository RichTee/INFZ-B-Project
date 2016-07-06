/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.controlPersoonFrameOptie;
import databaseUtil.DataBaseConnector;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 *
 * @author Method
 */
public class ToevoegenUitlening extends javax.swing.JFrame {
    UitleningFrame uitleningFrame;
    /**
     * Creates new form ToevoegenUitlening
     */
    public ToevoegenUitlening(UitleningFrame uitleningFrame) {
        initComponents();
        this.setTitle("Persoon Toevoegen | Gallerie");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.uitleningFrame = uitleningFrame;
        setVisible(true);
        cbbKlantVullen();
        cbbSchilderijVullen();
        System.out.println(getCurrentDate());
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
    // Toevoegen Uitlening
    private boolean toevoegenUitlening(int klant_id, int schilderij_id){
        String Startdatum = this.txfStartdatum.getText();
        String Einddatum = this.txfEinddatum.getText();
        int Klant = klant_id;
        int Schilderij = schilderij_id;
        int Waardering = Integer.parseInt(this.txfWaardering.getText());

        if(!checkDatum())
            return false;
        
        if(!checkUitleningenKlant(klant_id, schilderij_id))
            return false;
        
        if(!checkEinddatumleen(schilderij_id, Startdatum))
            return false;
        
        if(!checkTegelijkertijdLeen(Klant, Startdatum, Einddatum))
            return false;
        
        if(!checkWaarde())
            return false;
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
        
        try {
            Connection conn = DataBaseConnector.getConnection();

            //                                                         1            2       3           4           5
            String prepStatToevoegenKlant = "INSERT INTO uitlening (klant_id, schilderij_id, startdatum, einddatum, waardering) "
                                                    + "VALUES       (?,         ?,          ?,          ?,          ?       )";
            PreparedStatement prepStat = conn.prepareStatement(prepStatToevoegenKlant);

            try {
            Date dateStartdatum = formatter.parse(Startdatum);
            Date dateEinddatum = formatter.parse(Einddatum);

            java.sql.Date sqlDateStartdatum = convertUtilToSql(dateStartdatum);
            java.sql.Date sqlDateEinddatum = convertUtilToSql(dateEinddatum);
            
            prepStat.setInt(1, Klant);
            prepStat.setInt(2, Schilderij);
            prepStat.setDate(3, sqlDateStartdatum);
            prepStat.setDate(4, sqlDateEinddatum);
            prepStat.setDouble(5, Waardering);

            prepStat.executeUpdate();
            
            gemiddeldeSchilderijWaardering(Waardering, Schilderij);
            getSchilderijUitleningCount(Schilderij);
            
            return true;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (SQLException exc) {
            JOptionPane.showMessageDialog(this, "Persoon niet opgeslagen in de database" + exc.toString());
            return false;
        }
        return false;
    }
    private void updateSchilderijUitlening(int count, int Schilderij_id) {
        try {
            Connection conn = DataBaseConnector.getConnection();

            String prepStatToevoegenUitleningen = "UPDATE schilderij SET Uitleningen = ? where id = ?";
            PreparedStatement prepStat = conn.prepareStatement(prepStatToevoegenUitleningen);

            prepStat.setDouble(1, count);
            prepStat.setInt(2, Schilderij_id);
            
            prepStat.executeUpdate();
        } catch (SQLException exc){
            JOptionPane.showMessageDialog(this, "Persoon niet opgeslagen in de database" + exc.toString());
        }
    }
    private void getSchilderijUitleningCount(int Schilderij_Id){
        try {
            Connection conn = DataBaseConnector.getConnection();

            String getGemiddeldeWaarde = "SELECT count(*) as gemiddelde from uitlening where schilderij_id = " + Schilderij_Id;
            Statement statement = conn.createStatement();
            ResultSet  result = statement.executeQuery(getGemiddeldeWaarde);

            while(result.next()){
                int gemiddelde = result.getInt("gemiddelde");
                
                updateSchilderijUitlening(gemiddelde, Schilderij_Id);
            }
        } catch (SQLException exc){
            JOptionPane.showMessageDialog(this, "Persoon niet opgeslagen in de database" + exc.toString());
        }
    }
    
    private void updateSchilderijWaardering(double Waardering, int Schilderij_id) {
        try {
            Connection conn = DataBaseConnector.getConnection();

            //                                                         1 
            String prepStatToevoegenKlant = "UPDATE schilderij SET waardering = ? where id = ?";
            PreparedStatement prepStat = conn.prepareStatement(prepStatToevoegenKlant);

            prepStat.setDouble(1, Waardering);
            prepStat.setInt(2, Schilderij_id);
            

            prepStat.executeUpdate();
        } catch (SQLException exc){
            JOptionPane.showMessageDialog(this, "Persoon niet opgeslagen in de database" + exc.toString());
        }
    }
    
    private void gemiddeldeSchilderijWaardering(double Waardering, int Schilderij_Id) {
        String gemiddelde = "1";
        DecimalFormat form = new DecimalFormat ("0.00");
        try {
            Connection conn = DataBaseConnector.getConnection();

            //                                                         1 
            String getGemiddeldeWaarde = "SELECT avg(waardering) as gemiddeldeSQL from uitlening where schilderij_id = " + Schilderij_Id;
            Statement statement = conn.createStatement();
            ResultSet  result = statement.executeQuery(getGemiddeldeWaarde);

            while(result.next()){
                gemiddelde = result.getString("gemiddeldeSQL");
                double doubleGemiddelde = Double.parseDouble(gemiddelde);
                
                updateSchilderijWaardering(doubleGemiddelde, Schilderij_Id);
            }
        } catch (SQLException exc){
            JOptionPane.showMessageDialog(this, "Persoon niet opgeslagen in de database" + exc.toString());
        }
    }
    private static java.sql.Date convertUtilToSql(java.util.Date uDate) {

        java.sql.Date sDate = new java.sql.Date(uDate.getTime());

        return sDate;

    }
    
    // ComboBox vullen met Schilderijen
    private void cbbSchilderijVullen() {
        DefaultComboBoxModel model = (DefaultComboBoxModel) cbbSchilderij.getModel();
        model.removeAllElements();

        try{
            Connection conn = DataBaseConnector.getConnection();
            String getKunstenaar = "select id, naam, kunstenaar from schilderij";

            Statement statement = conn.createStatement();
            ResultSet  result = statement.executeQuery(getKunstenaar);
            
            if(!result.isBeforeFirst()) { } 
            else {
                while(result.next()){
                    String id = result.getString("id");
                    String naam = result.getString("naam");
                    String kunstenaar_id = result.getString("kunstenaar");
                    
                    String convKunstenaar_id = kunstenaarIdNaarNaam(Integer.parseInt(kunstenaar_id));
                    String samenvoegen = id + " " + naam + " - " + convKunstenaar_id;
                    cbbSchilderij.addItem(samenvoegen);
                }
            }
        } catch (SQLException exc) {
            System.err.println("Error Code: " + 
                    ((SQLException)exc).getMessage());
        }
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
    // Klanten mogen niet meer dan 1 uitleningen tegelijk
    private boolean checkTegelijkertijdLeen(int Klant_id, String Startdatum, String Einddatum) {
        try {
            Connection conn = DataBaseConnector.getConnection();
            String selectNaam = "select count(*) as count from uitlening\n" +
                            "where klant_id = " + Klant_id
                            + " AND startdatum BETWEEN  '" + Startdatum
                            + "' AND '" + Einddatum + "'";
            
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(selectNaam);
            
            while(result.next()) {
                int count = result.getInt("count");
                if(count > 2) {
                    return false;
                } else {
                    return true;
                }
            }
        } catch (SQLException exc){
            JOptionPane.showMessageDialog(this, "Fout bij het ophalen van de "
                    + "uitleningen uit de database"
                    + exc.toString());
            return false;
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
    
    private String kunstenaarIdNaarNaam(int id) {
        try {
            Connection conn = DataBaseConnector.getConnection();
            String selectNaam = "select voornaam, tussenvoegsel, achternaam from persoon\n" +
                                "inner join schilderij on persoon.id = " + id;
            
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
    private boolean checkWaarde() {
        if(Integer.parseInt(txfWaardering.getText()) > 0 && Integer.parseInt(txfWaardering.getText()) < 11){
            return true;
        } else {
            JOptionPane.showMessageDialog(this, "Waarde moet tussen de 1 en 10 zijn ");
            return false;
        }
    }
    private boolean checkVeldenLeeg(){
        if(txfEinddatum.getText().isEmpty()){
            return false;
        }
        
        if(txfStartdatum.getText().isEmpty()){
            return false;
        }
        
        if(txfWaardering.getText().isEmpty()){
            return false;
        }
        
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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txfStartdatum = new javax.swing.JTextField();
        txfEinddatum = new javax.swing.JTextField();
        txfWaardering = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        cbbKlant = new javax.swing.JComboBox();
        cbbSchilderij = new javax.swing.JComboBox();
        lblStartdatum = new javax.swing.JLabel();
        lblEinddatum = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Uitlening Toevoegen");

        jLabel2.setText("Klant:");

        jLabel3.setText("Schilderij:");

        jLabel4.setText("Startdatum:");

        jLabel5.setText("Einddatum:");

        jLabel6.setText("Waardering:");

        txfStartdatum.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txfStartdatumFocusLost(evt);
            }
        });
        txfStartdatum.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txfStartdatumKeyReleased(evt);
            }
        });

        txfEinddatum.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txfEinddatumFocusLost(evt);
            }
        });
        txfEinddatum.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txfEinddatumKeyReleased(evt);
            }
        });

        jButton1.setText("Toevoegen");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        cbbKlant.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cbbSchilderij.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addGap(72, 72, 72)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton1)
                    .addComponent(jLabel1)
                    .addComponent(txfStartdatum)
                    .addComponent(txfEinddatum)
                    .addComponent(txfWaardering)
                    .addComponent(cbbKlant, 0, 120, Short.MAX_VALUE)
                    .addComponent(cbbSchilderij, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblStartdatum, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEinddatum, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(71, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cbbKlant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cbbSchilderij, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txfStartdatum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStartdatum, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblEinddatum, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(txfEinddatum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txfWaardering, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37)
                .addComponent(jButton1)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Object getSelectedKlant = cbbKlant.getSelectedItem();
        Object getSelectedSchilderij = cbbSchilderij.getSelectedItem();
        
        String klant_id = getSelectedKlant.toString();
        String schilderij_id = getSelectedSchilderij.toString();
        
        int selectedKlant_Id = Integer.parseInt(klant_id.substring(0, klant_id.indexOf(' ')));
        int selectedSchilderij_id = Integer.parseInt(schilderij_id.substring(0, schilderij_id.indexOf(' ' )));
        
        System.out.println("Selected comboboxModel Klant: " + selectedKlant_Id);
        System.out.println("Selected comboboxModel Schilderij:" + selectedSchilderij_id);
        
        if(!checkVeldenLeeg()) {
            controlPersoonFrameOptie.bevestigingLegeVelden("U mist informatie dat noodzakelijk is.\n Voeg de informatie toe om het proces te voltooien.");
        } else {
            if(toevoegenUitlening(selectedKlant_Id, selectedSchilderij_id)) {
                this.setVisible(false);
                this.uitleningFrame.vulTabelMetUitleningen();
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private boolean checkUitleningenKlant(int Klant_id, int Schilderij_id){
        try {
            Connection conn = DataBaseConnector.getConnection();
            String selectNaam = "select count(*) as count from uitlening\n" +
                                "where klant_id = " + Klant_id
                                + " AND schilderij_id = " + Schilderij_id;
            
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(selectNaam);
            
            while(result.next()) {
                if(result.getInt("count") == 0){
                    return true;
                } else {
                    controlPersoonFrameOptie.bevestigingLegeVelden("De klant heeft al een keer deze schilderij geleent!");
                    return false;
                }
            }
        } catch (SQLException exc){
            return false;
        }
        return false;
    }
    private boolean checkStartdatum() {
        String Startdatum = txfStartdatum.getText();
        
        try {
            Connection conn = DataBaseConnector.getConnection();
            String selectNaam = "select * from uitlening"
                                + " WHERE '" + Startdatum + 
                                  "' NOT BETWEEN startdatum AND einddatum";
            
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(selectNaam);
            
            return true;
        } catch (SQLException exc){
            return false;
        }
    }
    // Get huidige datum
    public static String getCurrentDate() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }
    
    // Check of eindDatum niet lager is als startDatum
    private boolean checkDatum() {
        String Startdatum = txfStartdatum.getText();
        String Einddatum = txfEinddatum.getText();
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        
        if(Startdatum.isEmpty() || Einddatum.isEmpty())
            return false;
        
        try {
        Date dateStartdatum = formatter.parse(Startdatum);
        Date dateEinddatum = formatter.parse(Einddatum);
        Date dateHuidigedatum = formatter.parse(getCurrentDate());
        
        if(dateStartdatum.after(dateHuidigedatum) && dateStartdatum.before(dateEinddatum)) {
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
    private void txfEinddatumFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txfEinddatumFocusLost
        if(!checkDatum()){
             txfEinddatum.setBackground(Color.red);
        } else { 
            txfEinddatum.setBackground(UIManager.getColor("TextField.background"));
            txfStartdatum.setBackground(UIManager.getColor("TextField.background"));
        }
    }//GEN-LAST:event_txfEinddatumFocusLost

    private void txfStartdatumKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txfStartdatumKeyReleased
        lblStartdatum.setText("Voorbeeld: 1995-10-10. YYYY-MM-DD");
    }//GEN-LAST:event_txfStartdatumKeyReleased

    private void txfEinddatumKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txfEinddatumKeyReleased
         lblEinddatum.setText("Voorbeeld: 1995-10-10. YYYY-MM-DD");
    }//GEN-LAST:event_txfEinddatumKeyReleased

    private void txfStartdatumFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txfStartdatumFocusLost
        if(!checkDatum()) {
             txfStartdatum.setBackground(Color.red);
        } else { 
            txfStartdatum.setBackground(UIManager.getColor("TextField.background"));
            txfEinddatum.setBackground(UIManager.getColor("TextField.background"));
        }
    }//GEN-LAST:event_txfStartdatumFocusLost

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
