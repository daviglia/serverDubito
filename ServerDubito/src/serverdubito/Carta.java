/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverdubito;

/**
 *
 * @author Daviglia
 */
class Carta {
    public static final int PICCHE=1; 
    public static final int FIORI=2; 
    public static final int CUORI=3; 
    public static final int QUADRI=4; 
    
    private int seme; 
    private int numero; 
    
    public Carta(int numero, int seme){
        this.seme=seme; 
        this.numero=numero; 
    }
    public int getNumero(){
        return numero; 
    }
    
}
