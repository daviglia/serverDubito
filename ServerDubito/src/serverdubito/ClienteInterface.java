/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverdubito;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 *
 * @author Daviglia
 */
public interface ClienteInterface extends Remote{
    public boolean inviaLobby(ArrayList<Game> lobby) throws RemoteException; 
    
    public void gameStart(ArrayList<Carta> carte, int posizione, String[] nomiPlayer) throws RemoteException; 
    public void riceviCarte(ArrayList<Carta> carte)throws RemoteException; 
    
    public void aggiornamentoGraficaPartita(int[] numeroCarteGiocatori , int numeroCartePiatto, int turno, int mossa) throws RemoteException; 
    public void ruotaCarte(Carta[] carte ) throws RemoteException; 
    
    public String getNome() throws RemoteException; 
    public void gameEndPerAbbandono() throws RemoteException;
    public void gameEnd(String vincitore) throws RemoteException; 
    }
    
