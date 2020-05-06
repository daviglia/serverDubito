/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverdubito;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Daviglia
 */
public interface ServerInterface extends Remote{ // passo sia il clientIntercface e l'utente per non intoppare la linea e rallentare il gioco 
                                                 // passo solo interfaccia e riciedo i campi invocando il metodo remoto 
                                                 // passo un utente che contiene anche l'interfaccia... però in realtà io gia lo faccio 
    
    
    public boolean uniscitiLobby(ClienteInterface utente) throws RemoteException; //login
    
    
    public boolean creaPartita(String nomePartita, ClienteInterface creatorePartita) throws RemoteException;
    public boolean uniscitiPartita(int indicePartita, ClienteInterface utente) throws RemoteException; 
    public boolean lasciaCodaAttesaPartita(Game game ); //game?? 
    
    
    public boolean lasciaLobby(ClienteInterface utente) throws RemoteException; // per chiudere il gioco 
    
    
}
