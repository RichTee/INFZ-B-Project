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
public class Uitlening {
    private String id;
    private String klant_id;
    private String schilderij_id;
    private String start_datum;
    private String eind_datum;
    private String waardering;
    
    public Uitlening(String id, String klant_id, String schilderij_id, String start_datum,
                String eind_datum, String waardering) {
        this.id = id;
        this.klant_id = klant_id;
        this.schilderij_id = schilderij_id;
        this.start_datum = start_datum;
        this.eind_datum = eind_datum;
        this.waardering = waardering;
    }
    
    /**
     * Getters 
     */
    public String getId(){
        return id;
    }
    
    public String getKlantId() {
        return klant_id;
    }
    
    public String getSchilderijId() {
        return schilderij_id;
    }
    
    public String getStartdatum() {
        return start_datum;
    }
    
    public String getEinddatum(){
        return eind_datum;
    }
    
    public String getWaardering() {
        return waardering;
    }

    /**
     * Setters
     */
    
    public void setId(String id) {
        this.id = id;
    }
    
    public void setKlantId (String klant_id){
        this.klant_id = klant_id;
    }
    
    public void setSchilderijId(String schilderij_id) {
        this.schilderij_id = schilderij_id;
    }
    
    public void setStartdatum(String start_datum) {
        this.start_datum = start_datum;
    }
    
    public void setEinddatum(String eind_datum) {
        this.eind_datum = eind_datum;
    }
    
    public void setWaardering(String waardering) {
        this.waardering = waardering;
    }
    
    //Methode retourneert de eigenschappen van een klant verpakt in een String[]
    public String[] getUitleningInfo() {
        return new String[]{this.id, this.klant_id,
                            this.schilderij_id, this.start_datum, this.eind_datum, 
                            this.waardering};
    }
}
