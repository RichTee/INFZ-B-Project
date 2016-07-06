/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author Method
 */
public class Schilderij {
    private String id;
    private String naam;
    private String jaar;
    private String kunstenaar;
    private String uitleningen;
    private String waardering;
    private String waarde;

    
    public Schilderij(String id, String naam, String jaar, String kunstenaar,
                String uitleningen, String waardering, String waarde) {
        this.id = id;
        this.naam = naam;
        this.jaar = jaar;
        this.kunstenaar = kunstenaar;
        this.uitleningen = uitleningen;
        this.waardering = waardering;
        this.waarde = waarde;
    }
    
    /**
     * Getters 
     */
    public String getId(){
        return id;
    }
    
    public String getNaam() {
        return naam;
    }
    
    public String getJaar() {
        return jaar;
    }
    
    public String getKunstenaar() {
        return kunstenaar;
    }
    
    public String getUitleningen(){
        return uitleningen;
    }
    
    public String getWaardering() {
        return waardering;
    }
    
    public String getWaarde() {
        return waarde;
    }

    /**
     * Setters
     */
    
    public void setId(String id) {
        this.id = id;
    }
    
    public void setNaam (String naam){
        this.naam = naam;
    }
    
    public void setJaar(String jaar) {
        this.jaar = jaar;
    }
    
    public void setKnstenaar(String kunstenaar) {
        this.kunstenaar = kunstenaar;
    }
    
    public void setUitleningen(String uitleningen) {
        this.uitleningen = uitleningen;
    }
    
    public void setWaardering(String waardering) {
        this.waardering = waardering;
    }
    
    public void setWaarde(String waarde) {
        this.waarde = waarde;
    }

    
    //Methode retourneert de eigenschappen van een klant verpakt in een String[]
    public String[] getSchilderijInfo() {
        return new String[]{this.id, this.naam,
                            this.jaar, this.kunstenaar, this.uitleningen, 
                            this.waardering, this.waarde};
    }
}
