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
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import model.Persoon;
import model.Uitlening;

/**
 *
 * @author Method
 */
public class PersoonFrame extends javax.swing.JFrame {
    private ArrayList<Persoon> persoonKlant;
    private ArrayList<Persoon> persoonKunstenaar;
    
    public PersoonFrame() {
        initComponents();
        this.setTitle("Persoon | Gallerie");
        this.setDefaultCloseOperation(PersoonFrame.DISPOSE_ON_CLOSE);
    }
    
    public PersoonFrame(Object klasseNaam){
        initComponents();
        this.setTitle("Persoon | Gallerie");
        this.setDefaultCloseOperation(PersoonFrame.DISPOSE_ON_CLOSE);
        
        persoonKlant = new ArrayList();
        persoonKunstenaar = new ArrayList();
        vulPersoonTabelMetKlanten();
        vulPersoonTabelMetKunstenaars();
    }

    // Overzicht tabel vullen met ALLEEN klanten.
    public void vulPersoonTabelMetKlanten() {
        persoonKlant.clear();
        
        //Definieren van de header kolommen.
        String[] kolommen = {   // Kolom nummer
            "id",               // 0             
            "voornaam",         // 1   
            "tussenvoegsel",    // 2     
            "achternaam",       // 3      
            "woonplaats",       // 4
            "straatnaam",       // 5
            "huisnummer"};      // 6

        DefaultTableModel overzichtModel = new DefaultTableModel(kolommen, 0);
        
        //Maak het tabel leeg
        DefaultTableModel clearModel = (DefaultTableModel) tblOverzichtKlant.getModel();
        clearModel.setRowCount(0);
        
        //Klanten ophalen uit database
        try {
            Connection conn = DataBaseConnector.getConnection();
            String selectTafelIndeling = "select * from persoon\n" +
                                            "inner join klant on id = klant.persoon_id\n" +
                                            "order by voornaam ASC;";
                    
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(selectTafelIndeling);

            //Resultaat doorlopen.
            while (result.next()) {
                String id = result.getString("id");
                String voornaam = result.getString("voornaam");
                String tussenvoegsel = result.getString("tussenvoegsel");
                String achternaam = result.getString("achternaam");
                String woonplaats = result.getString("klant.woonplaats");
                String straatnaam = result.getString("klant.straatnaam");
                String huisnummer = result.getString("klant.huisnummer");
                
                // De gebriker mag geen null zien als er geen tussenvoegsel is.
                if(tussenvoegsel.equals("null")){
                    tussenvoegsel = "";
                }
                
                Persoon klant = new Persoon(id, voornaam, tussenvoegsel, achternaam, woonplaats, straatnaam, huisnummer);
                persoonKlant.add(klant);
            }

        } catch (SQLException exc) {
            JOptionPane.showMessageDialog(this, "Fout bij het ophalen van de "
                    + "personen uit de database"
                    + exc.toString());
        }
        
        // Accepteert geen primitive int, alleen objecten.
        for (Persoon klant : persoonKlant) {
            overzichtModel.addRow(klant.getKlantInfo());;
        }

        //model koppelen aan JTable
        this.tblOverzichtKlant.setModel(overzichtModel);
        
        // Verberg een kolom bij index 0(ID)
        tblOverzichtKlant.getColumnModel().getColumn(0).setMinWidth(0);
        tblOverzichtKlant.getColumnModel().getColumn(0).setMaxWidth(0);
    }
    
    // Kunstenaars ophalen uit de database
    public void vulPersoonTabelMetKunstenaars(){
        persoonKunstenaar.clear();
        
        //Definieren van de header kolommen.
        String[] kolommen = {   // Kolom nummer
            "id",               // 0             
            "voornaam",         // 1   
            "tussenvoegsel",    // 2     
            "achternaam",       // 3      
            "geboortedatum",    // 4
            "sterfdatum"};      // 5

        DefaultTableModel overzichtModel = new DefaultTableModel(kolommen, 0);

        //Maak het tabel leeg
        DefaultTableModel clearModel = (DefaultTableModel) tblOverzichtKunst.getModel();
        clearModel.setRowCount(0);
        
        //Klanten ophalen uit database
        try {
            Connection conn = DataBaseConnector.getConnection();
            String selectTafelIndeling = "select * from persoon\n" +
                                        "inner join kunstenaar on id = kunstenaar.persoon_id\n" +
                                        "order by voornaam DESC;";
                    
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(selectTafelIndeling);

            //Resultaat doorlopen.
            while (result.next()) {
                String id = result.getString("id");
                String voornaam = result.getString("voornaam");
                String tussenvoegsel = result.getString("tussenvoegsel");
                String achternaam = result.getString("achternaam");
                String geboortedatum = result.getString("geboortedatum");
                String sterfdatum = result.getString("sterfdatum");
                
                // De gebriker mag geen null zien als er geen tussenvoegsel is.
                if(tussenvoegsel.equals("null")){
                    tussenvoegsel = "";
                }

                Persoon kunst = new Persoon(id, voornaam, tussenvoegsel, achternaam, geboortedatum, sterfdatum);
                persoonKunstenaar.add(kunst);
            }

        } catch (SQLException exc) {
            JOptionPane.showMessageDialog(this, "Fout bij het ophalen van de "
                    + "personen uit de database"
                    + exc.toString());
        }
        
        // Accepteert geen primitive int, alleen objecten.
        for (Persoon kunst : persoonKunstenaar) {
            overzichtModel.addRow(kunst.getKunstenaarInfo());;
        }

        //model koppelen aan JTable
        this.tblOverzichtKunst.setModel(overzichtModel);
        
        // Verberg een kolom bij index 6(Speler_ID)
        tblOverzichtKunst.getColumnModel().getColumn(0).setMinWidth(0);
        tblOverzichtKunst.getColumnModel().getColumn(0).setMaxWidth(0);
    }
    
    private boolean checkUitleningenActief(int kunstenaarId) {
        try {
            Connection conn = DataBaseConnector.getConnection();
            //String selectTafelIndeling = "select einddatum from uitlening where einddatum > CURDATE()";
            String selectTafelIndeling = "select einddatum, schilderij.id from uitlening \n" +
                "inner join schilderij on schilderij_id = schilderij.id\n" +
                "where einddatum > CURDATE() and kunstenaar = " + kunstenaarId ;       
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(selectTafelIndeling);
            
            if(!result.next()){
                return true;
            } else {
                controlPersoonFrameOptie.bevestigingLegeVelden("Kunstenaar met geplande of actieve uitleningen kunnen niet worden verwijderd!");
                return false;
            }
        } catch (SQLException exc) {
            // error
            return false;
        }
    }
    
    private boolean verwijderKunstenaar() {
        
        System.out.println(" Called 1#");
        
        //Achterhalen welke kunstenaar geselecteerd is.
        int index = this.tblOverzichtKunst.getSelectedRow();
        Persoon persoon = this.persoonKunstenaar.get(index);
        
        if(!checkUitleningenActief(Integer.parseInt(persoon.getId())))
            return false;
        
        System.out.println(" Called 2#");
        //Vragen of de geselecteerde speler echt verwijderd moet worden. 
        int antwoord = JOptionPane.showConfirmDialog(null, "Weet je zeker dat u deze kunstenaar wilt verwijderen? ", "Verwijder kunstenaar", JOptionPane.YES_NO_OPTION);
        if (antwoord == JOptionPane.YES_OPTION) {

            boolean gelukt = true;
            try {
                Connection conn = DataBaseConnector.getConnection();
                Statement stat = conn.createStatement();

                String prepStatVerwijderKunstenaar = "DELETE FROM persoon WHERE id = ?";

                PreparedStatement prepStat = conn.prepareStatement(prepStatVerwijderKunstenaar);

                prepStat.setString(1, persoon.getId());

                prepStat.executeUpdate();
            } catch (SQLException exc) {
                System.err.println(exc);
                gelukt = false;
            }

            if (gelukt) {
                this.vulPersoonTabelMetKunstenaars();
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Het verwijderen van de kunstenaar is niet gelukt");
            }
        }
        return false;
    }
    
    private boolean checkKlantBoundException() {
        if(this.tblOverzichtKlant.getSelectedRow() == -1) {
            return false;
        }
        
        int index = this.tblOverzichtKlant.getSelectedRow();        

        Persoon selectedKlant = (Persoon) this.persoonKlant.get(index);

        WijzigPersoon wijzigPersoon = new WijzigPersoon(selectedKlant, this);
        wijzigPersoon.setVisible(true);
        
        return true;
    }
    private boolean checkKunstenaarBoundException() {
        if(this.tblOverzichtKunst.getSelectedRow() == -1) {
            return false;
        }
        
        int index = this.tblOverzichtKunst.getSelectedRow();        

        Persoon selectedKunstenaar = (Persoon) this.persoonKunstenaar.get(index);

        WijzigKunstenaar wijzigKunstenaar = new WijzigKunstenaar(selectedKunstenaar, this);
        wijzigKunstenaar.setVisible(true);
        
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
        btnToevoegen = new javax.swing.JButton();
        btnWijzigen = new javax.swing.JButton();
        btnVerwijderen = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOverzichtKlant = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        txfZoekKlant = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblOverzichtKunst = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        txfZoekKunstenaar = new javax.swing.JTextField();
        btnWijzigenKunstenaar = new javax.swing.JButton();
        btnVerwijderenKunstenaar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Persoon | Gallerie");

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

        tblOverzichtKlant.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Voornaam", "Tussenvoegsel", "Achternaam", "Woonplaats", "Straatnaam", "Huisnummer"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblOverzichtKlant);

        jLabel2.setText("Klant zoeken:");

        txfZoekKlant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txfZoekKlantActionPerformed(evt);
            }
        });
        txfZoekKlant.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txfZoekKlantKeyReleased(evt);
            }
        });

        tblOverzichtKunst.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Voornaam", "Tussenvoegsel", "Achternaam", "Geboortedatum", "Sterfdatum"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblOverzichtKunst);

        jLabel3.setText("Kunstenaar zoeken:");

        txfZoekKunstenaar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txfZoekKunstenaarActionPerformed(evt);
            }
        });
        txfZoekKunstenaar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txfZoekKunstenaarKeyReleased(evt);
            }
        });

        btnWijzigenKunstenaar.setText("Wijzigen");
        btnWijzigenKunstenaar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnWijzigenKunstenaarActionPerformed(evt);
            }
        });

        btnVerwijderenKunstenaar.setText("Verwijderen");
        btnVerwijderenKunstenaar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerwijderenKunstenaarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(322, 322, 322))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnToevoegen)
                            .addComponent(btnWijzigen, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnVerwijderen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnVerwijderenKunstenaar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnWijzigenKunstenaar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 634, Short.MAX_VALUE)
                            .addComponent(jScrollPane1)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txfZoekKunstenaar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txfZoekKlant, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txfZoekKlant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnToevoegen)
                        .addGap(25, 25, 25)
                        .addComponent(btnWijzigen)
                        .addGap(18, 18, 18)
                        .addComponent(btnVerwijderen))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txfZoekKunstenaar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(btnWijzigenKunstenaar)
                        .addGap(18, 18, 18)
                        .addComponent(btnVerwijderenKunstenaar)))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnToevoegenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToevoegenActionPerformed
        new ToevoegenPersoon(this).setVisible(true);
    }//GEN-LAST:event_btnToevoegenActionPerformed

    private void btnVerwijderenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerwijderenActionPerformed
        //Achterhalen welke klant geselecteerd is.
        int index = this.tblOverzichtKlant.getSelectedRow();
        Persoon persoon = this.persoonKlant.get(index);

        //Vragen of de geselecteerde speler echt verwijderd moet worden. 
        int antwoord = JOptionPane.showConfirmDialog(null, "Weet je zeker dat u deze klant wilt verwijderen? ", "Verwijder klant", JOptionPane.YES_NO_OPTION);
        if (antwoord == JOptionPane.YES_OPTION) {

            boolean gelukt = true;
            try {
                Connection conn = DataBaseConnector.getConnection();
                Statement stat = conn.createStatement();

                String prepStatVerwijderKlant = "DELETE FROM persoon WHERE id = ?";

                PreparedStatement prepStat = conn.prepareStatement(prepStatVerwijderKlant);

                prepStat.setString(1, persoon.getId());

                prepStat.executeUpdate();
            } catch (SQLException exc) {
                System.err.println(exc);
                gelukt = false;
            }

            if (gelukt) {
                this.vulPersoonTabelMetKlanten();
            } else {
                JOptionPane.showMessageDialog(null, "Het verwijderen van de klant is niet gelukt");
            }
        }
    }//GEN-LAST:event_btnVerwijderenActionPerformed

    private void btnWijzigenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnWijzigenActionPerformed
        checkKlantBoundException();
    }//GEN-LAST:event_btnWijzigenActionPerformed

    private void btnWijzigenKunstenaarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnWijzigenKunstenaarActionPerformed
        checkKunstenaarBoundException();
    }//GEN-LAST:event_btnWijzigenKunstenaarActionPerformed

    private void btnVerwijderenKunstenaarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerwijderenKunstenaarActionPerformed
        verwijderKunstenaar();
    }//GEN-LAST:event_btnVerwijderenKunstenaarActionPerformed

    private void txfZoekKlantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txfZoekKlantActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txfZoekKlantActionPerformed

    private void txfZoekKlantKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txfZoekKlantKeyReleased
        if(txfZoekKlant.getText().isEmpty()){
            this.vulPersoonTabelMetKlanten();
        }
        
        String criteria = txfZoekKlant.getText();
        
        persoonKlant.clear();
        
        //Definieren van de header kolommen.
        String[] kolommen = {   // Kolom nummer
            "id",               // 0             
            "voornaam",         // 1   
            "tussenvoegsel",    // 2     
            "achternaam",       // 3      
            "woonplaats",       // 4
            "straatnaam",       // 5
            "huisnummer"};      // 6

        DefaultTableModel overzichtModel = new DefaultTableModel(kolommen, 0);
        
        //Maak het tabel leeg
        DefaultTableModel clearModel = (DefaultTableModel) tblOverzichtKlant.getModel();
        clearModel.setRowCount(0);
        
        try {
            Connection conn = DataBaseConnector.getConnection();
            String selectTafelIndeling = "select * from persoon\n" +
                                        "inner join klant on id = klant.persoon_id\n" +
                                        "where voornaam like '%"+ criteria +"%'";
                    
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(selectTafelIndeling);
            
            while(result.next()){
                String id = result.getString("id");
                String voornaam = result.getString("voornaam");
                String tussenvoegsel = result.getString("tussenvoegsel");
                String achternaam = result.getString("achternaam");
                String woonplaats = result.getString("klant.woonplaats");
                String straatnaam = result.getString("klant.straatnaam");
                String huisnummer = result.getString("klant.huisnummer");
                
                // De gebriker mag geen null zien als er geen tussenvoegsel is.
                if(tussenvoegsel.equals("null")){
                    tussenvoegsel = "";
                }
                
                Persoon klant = new Persoon(id, voornaam, tussenvoegsel, achternaam, woonplaats, straatnaam, huisnummer);
                persoonKlant.add(klant);
            }
            
        } catch (SQLException exc) {
            // error
        }
        
        // Accepteert geen primitive int, alleen objecten.
        for (Persoon kunst : persoonKlant) {
            overzichtModel.addRow(kunst.getKlantInfo());
        }

        //model koppelen aan JTable
        this.tblOverzichtKlant.setModel(overzichtModel);
        
        // Verberg een kolom bij index 6(Speler_ID)
        tblOverzichtKunst.getColumnModel().getColumn(0).setMinWidth(0);
        tblOverzichtKunst.getColumnModel().getColumn(0).setMaxWidth(0);
    }//GEN-LAST:event_txfZoekKlantKeyReleased

    private void txfZoekKunstenaarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txfZoekKunstenaarActionPerformed
 
    }//GEN-LAST:event_txfZoekKunstenaarActionPerformed

    private void txfZoekKunstenaarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txfZoekKunstenaarKeyReleased
        if(txfZoekKunstenaar.getText().isEmpty()){
            this.vulPersoonTabelMetKunstenaars();
        }
        
        String criteria = txfZoekKunstenaar.getText();
        
        persoonKunstenaar.clear();
        
        //Definieren van de header kolommen.
        String[] kolommen = {   // Kolom nummer
            "id",               // 0             
            "voornaam",         // 1   
            "tussenvoegsel",    // 2     
            "achternaam",       // 3      
            "geboortedatum",    // 4
            "sterfdatum"};      // 5

        DefaultTableModel overzichtModel = new DefaultTableModel(kolommen, 0);

        //Maak het tabel leeg
        DefaultTableModel clearModel = (DefaultTableModel) tblOverzichtKunst.getModel();
        clearModel.setRowCount(0);
        
        //Klanten ophalen uit database
        try {
            Connection conn = DataBaseConnector.getConnection();
            String selectTafelIndeling = "select * from persoon\n" +
                                        "inner join kunstenaar on id = kunstenaar.persoon_id\n" +
                                        "where voornaam like '%"+ criteria +"%'";
                    
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(selectTafelIndeling);

            //Resultaat doorlopen.
            while (result.next()) {
                String id = result.getString("id");
                String voornaam = result.getString("voornaam");
                String tussenvoegsel = result.getString("tussenvoegsel");
                String achternaam = result.getString("achternaam");
                String geboortedatum = result.getString("geboortedatum");
                String sterfdatum = result.getString("sterfdatum");
                
                // De gebriker mag geen null zien als er geen tussenvoegsel is.
                if(tussenvoegsel.equals("null")){
                    tussenvoegsel = "";
                }

                Persoon kunst = new Persoon(id, voornaam, tussenvoegsel, achternaam, geboortedatum, sterfdatum);
                persoonKunstenaar.add(kunst);
            }
            
        } catch (SQLException exc) {
            // error
        }
        
        // Accepteert geen primitive int, alleen objecten.
        for (Persoon kunst : persoonKunstenaar) {
            overzichtModel.addRow(kunst.getKunstenaarInfo());;
        }

        //model koppelen aan JTable
        this.tblOverzichtKunst.setModel(overzichtModel);
        
        // Verberg een kolom bij index 6(Speler_ID)
        tblOverzichtKunst.getColumnModel().getColumn(0).setMinWidth(0);
        tblOverzichtKunst.getColumnModel().getColumn(0).setMaxWidth(0);
    }//GEN-LAST:event_txfZoekKunstenaarKeyReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PersoonFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PersoonFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PersoonFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PersoonFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PersoonFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnToevoegen;
    private javax.swing.JButton btnVerwijderen;
    private javax.swing.JButton btnVerwijderenKunstenaar;
    private javax.swing.JButton btnWijzigen;
    private javax.swing.JButton btnWijzigenKunstenaar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblOverzichtKlant;
    private javax.swing.JTable tblOverzichtKunst;
    private javax.swing.JTextField txfZoekKlant;
    private javax.swing.JTextField txfZoekKunstenaar;
    // End of variables declaration//GEN-END:variables
}
