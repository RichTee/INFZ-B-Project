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
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Calendar;
import javax.swing.JComboBox;

/**
 *
 * @author Method
 */
public class ToevoegenSchilderij extends javax.swing.JFrame {
    SchilderijFrame schilderijFrame;
    private String getSelectedId;
    private Date geboortedatum;
    private Date sterfdatum;
    
    /**
     * Creates new form ToevoegenSchilderij
     */
    public ToevoegenSchilderij(SchilderijFrame schilderijFrame) {
        initComponents();
        this.setTitle("Schilderij Toevoegen");
        this.setDefaultCloseOperation(PersoonFrame.DISPOSE_ON_CLOSE);
        
        this.schilderijFrame = schilderijFrame;
        cbbKunstenaarVullen();

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
    // Check positieve waarde
    private boolean checkUitleningWaarde(int waarde) {
        if(waarde < 0 ){
            controlPersoonFrameOptie.bevestigingLegeVelden("De waarde moet positief zijn!");
            return false;
        } else {
            return true;
        }
    }
    
    // Controlle of Schilderij velden leeg zijn.
    private boolean checkSchilderiJVelden() {
        if(txfNaam.getText().isEmpty()) {
            return false;
        }
        
        if(txfJaar.getText().isEmpty()) {
            return false;
        }
        
        if(txfWaarde.getText().isEmpty()){
            return false;
        }
        
        return true;
    }
    
    // Schilderijen mogen niet dezelfde naam hebben in de verzameling 
    private boolean checkDuplicaatSchilderijNaam() {
        String Naam = txfNaam.getText();
        
        try {
            Connection conn = DataBaseConnector.getConnection();

                String getDuplicaatNaam = "SELECT naam from schilderij "
                                        + "where schilderij.naam = " + "'" +Naam+"'";

                Statement statement = conn.createStatement();
                ResultSet  result = statement.executeQuery(getDuplicaatNaam);
                
                if(result.next()){
                    controlPersoonFrameOptie.bevestigingLegeVelden("Er is al een Schilderij met dezelfde naam in uw verzameling.");
                    return false;
                } else {
                    return true;
                }
        } catch (SQLException exc){
            JOptionPane.showMessageDialog(this, "Duplicaat schending in de database" + exc.toString());
            return false;
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
    // Util naar SQL
    private static java.sql.Date convertUtilToSql(java.util.Date uDate) {

        java.sql.Date sDate = new java.sql.Date(uDate.getTime());

        return sDate;

    }
    
    private boolean toevoegenSchilderij(int id) {
        String Naam = this.txfNaam.getText();
        String Jaar = this.txfJaar.getText();
        int Kunstenaar = id;
        String Waarde = this.txfWaarde.getText();

        if(!checkDuplicaatSchilderijNaam())
            return false;
        
        if(!checkDatum(Kunstenaar, Jaar))
            return false;
        
        if(!checkUitleningWaarde(Integer.parseInt(Waarde)))
            return false;
        
        System.out.println(checkDatum(Kunstenaar, Jaar));
        try {
            Connection conn = DataBaseConnector.getConnection();

            //                                                         1    2       3           4
            String prepStatToevoegenKlant = "INSERT INTO schilderij (naam, jaar, kunstenaar, waarde) "
                                                    + "VALUES       (?,     ?,      ?,        ?)";
            PreparedStatement prepStat = conn.prepareStatement(prepStatToevoegenKlant);

            prepStat.setString(1, Naam);
            prepStat.setString(2, Jaar);
            prepStat.setInt(3, Kunstenaar);
            prepStat.setString(4, Waarde);

            prepStat.executeUpdate();
            return true;

        } catch (SQLException exc) {
            JOptionPane.showMessageDialog(this, "Schilderij niet opgeslagen in de database" + exc.toString());
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
        jLabel7 = new javax.swing.JLabel();
        txfNaam = new javax.swing.JTextField();
        txfJaar = new javax.swing.JTextField();
        txfWaarde = new javax.swing.JTextField();
        btnToevoegen = new javax.swing.JButton();
        cbbKunstenaar = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        lblKunstenaarNote = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Schilderij Toevoegen");

        jLabel2.setText("Naam:");

        jLabel3.setText("Jaar:");

        jLabel4.setText("Kunstenaar:");

        jLabel7.setText("Waarde:");

        btnToevoegen.setText("Toevoegen");
        btnToevoegen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnToevoegenActionPerformed(evt);
            }
        });

        cbbKunstenaar.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbbKunstenaar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbbKunstenaarActionPerformed(evt);
            }
        });

        jLabel6.setText("Het jaar moet zich tussen de Kunstenaars geboortedatum en/of sterfdatum plaatsvinden");

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
                    .addComponent(jLabel7))
                .addGap(61, 61, 61)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnToevoegen)
                    .addComponent(txfNaam)
                    .addComponent(txfJaar)
                    .addComponent(cbbKunstenaar, 0, 150, Short.MAX_VALUE)
                    .addComponent(txfWaarde))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblKunstenaarNote, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addGap(50, 50, 50))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(103, 103, 103)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel6)))
                .addContainerGap(47, Short.MAX_VALUE))
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txfJaar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblKunstenaarNote, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(cbbKunstenaar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txfWaarde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addGap(9, 9, 9)
                .addComponent(btnToevoegen)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnToevoegenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToevoegenActionPerformed
        Object getSelectedKunstenaar = cbbKunstenaar.getSelectedItem();
        String id = getSelectedKunstenaar.toString();
        int selectedId = Integer.parseInt(id.substring(0, id.indexOf(' ')));
        
        System.out.println("Selected comboboxModel: " + selectedId);
        
        if(!checkSchilderiJVelden()) {
            controlPersoonFrameOptie.bevestigingLegeVelden("U mist informatie dat noodzakelijk is.\n Voeg de informatie toe om het proces te voltooien.");
        } else {
            if(toevoegenSchilderij(selectedId)){
                this.setVisible(false);
                this.schilderijFrame.vulSchilderijTabel();
            }
            
            
        }
        // Toevoegen aan DB
    }//GEN-LAST:event_btnToevoegenActionPerformed

    private void cbbKunstenaarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbbKunstenaarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbbKunstenaarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnToevoegen;
    private javax.swing.JComboBox cbbKunstenaar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel lblKunstenaarNote;
    private javax.swing.JTextField txfJaar;
    private javax.swing.JTextField txfNaam;
    private javax.swing.JTextField txfWaarde;
    // End of variables declaration//GEN-END:variables
}