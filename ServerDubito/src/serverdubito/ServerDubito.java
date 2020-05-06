

package serverdubito;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Daviglia
 */
public class ServerDubito extends UnicastRemoteObject implements ServerInterface{
    public static final int PORT= 25565; 
    
    private ArrayList<ClienteInterface> utenti; 
    private ArrayList<Game> partite;
    
    
    public static void main(String[] args) {
        try { 
            ServerDubito server= new ServerDubito(); 
            Registry registro= LocateRegistry.createRegistry(PORT);
            
        } catch (RemoteException ex) {
            Logger.getLogger(ServerDubito.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public boolean uniscitiLobby(ClienteInterface utente) throws RemoteException {
       utenti.add(utente); 
       System.out.println("Nuovo giocatore in ascolto sulla lobby: "+utente.getNome());
       return true; 
    }

    @Override
    public boolean creaPartita(String nomePartita, ClienteInterface creatorePartita) throws RemoteException {
        Game tmpGame= new Game(nomePartita,creatorePartita); 
        partite.add(tmpGame); 
        
        for (ClienteInterface u: utenti){
            try{
            u.inviaLobby(partite); 
            }
            catch(RemoteException e ){
                System.out.println("giocatore rimosso perchè non raggiungibile: "+u);
                utenti.remove(u); 
                
            }
        }
        return true; 
    }

    @Override
    public boolean uniscitiPartita(int partita, ClienteInterface utente) throws RemoteException {
        boolean tmp= partite.get(partita).addPlayer(utente);
            for(ClienteInterface u: utenti){
                try{
                u.inviaLobby(partite); 
                }
                catch(RemoteException e){
                    System.out.println("giocatore rimosso perchè non raggiungibile: "+u);
                    utenti.remove(u); 
                }
            }
            return tmp; 
        
    }

    @Override
    public boolean lasciaLobby(ClienteInterface utente) throws RemoteException {
       utenti.remove(utente);
       return true;
    }
    public void rimuoviGame(Game game){
        partite.remove(game); 
    }
}
