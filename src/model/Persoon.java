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
public class Persoon {
    private String id;
    private String voornaam;
    private String tussenvoegsel;
    private String achternaam;
    private String woonplaats;
    private String straatnaam;
    private String huisnummer;
    private String geboortedatum;
    private String sterfdatum;
    
    public Persoon(String id, String voornaam, String tussenvoegsel, 
                String achternaam, String woonplaats, String straatnaam,
                String huisnummer) {
        this.id = id;
        this.voornaam = voornaam;
        this.tussenvoegsel = tussenvoegsel;
        this.achternaam = achternaam;
        this.achternaam = achternaam;
        this.woonplaats = woonplaats;
        this.straatnaam = straatnaam;
        this.huisnummer = huisnummer;
    }
    
    public Persoon(String id, String voornaam, String tussenvoegsel, 
                String achternaam, String geboortedatum, 
                String sterfdatum) {
        this.id = id;
        this.voornaam = voornaam;
        this.tussenvoegsel = tussenvoegsel;
        this.achternaam = achternaam;
        this.achternaam = achternaam;
        this.geboortedatum = geboortedatum;
        this.sterfdatum = sterfdatum;
    }
    
    /**
     * Getters 
     */
    
    public String getId(){
        return id;
    }
    
    public String getVoornaam() {
        return voornaam;
    }
    
    public String getTussenvoegsel() {
        return tussenvoegsel;
    }

    public String getAchternaam(){
        return achternaam;
    }
    
    public String getWoonplaats() {
        return woonplaats;
    }
    
    public String getStraatnaam() {
        return straatnaam;
    }
    
    public String getHuisnummer() {
        return huisnummer;
    }
    
    public String getGeboortedatum() {
        return geboortedatum;
    }
    
    public String getSterfdatum(){
        return sterfdatum;
    }

    /**
     * Setters
     */
    
    public void setRondeNummer(String id) {
        this.id = id;
    }
    
    public void setVoornaam (String voornaam){
        this.voornaam = voornaam;
    }
    
    public void setTussenvoegsel(String tussenvoegsel) {
        this.tussenvoegsel = tussenvoegsel;
    }
    
    public void setAchternaam(String achternaam) {
        this.achternaam = achternaam;
    }
    
    public void setWoonplaats(String woonplaats) {
        this.woonplaats = woonplaats;
    }
    
    public void setStraatnaam(String straatnaam) {
        this.straatnaam = straatnaam;
    }
    
    public void setHuisnummer(String huisnummer) {
        this.huisnummer = huisnummer;
    }
    
    public void setGeboortedatum(String geboortedatum) {
        this.geboortedatum = geboortedatum;
    }
    
    public void setSterfdatum(String sterfdatum){
        this.sterfdatum = sterfdatum;
    }
    
    //Methode retourneert de eigenschappen van een klant verpakt in een String[]
    public String[] getKlantInfo() {
        return new String[]{this.id, this.voornaam,
                            this.tussenvoegsel, this.achternaam, 
                            this.woonplaats, this.straatnaam,
                            this.huisnummer};
    }
    
    public String[] getKunstenaarInfo() {
        return new String[]{this.id, this.voornaam,
                            this.tussenvoegsel, this.achternaam, 
                            this.geboortedatum,
                            this.sterfdatum};
    }
}
