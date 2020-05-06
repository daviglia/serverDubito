/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverdubito;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *
 * @author Daviglia
 */
interface GameInterface extends Remote{
    
    public void lasciaCoda(ClienteInterface utente) throws RemoteException; 
    public String getNome() throws RemoteException; 
    
    
    public void startTime() throws RemoteException; 
    
    
    public boolean dubita(int giocatore) throws RemoteException; 
    public void giocaCarte(ArrayList<Carta> carte, int giocatore) throws RemoteException; 
    public void dichiaraGiocata(int numero) throws RemoteException;
    
}
