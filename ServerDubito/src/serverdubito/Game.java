/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverdubito;

import com.sun.xml.internal.ws.api.client.ServiceInterceptor;
import java.lang.reflect.Array;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Game extends UnicastRemoteObject implements GameInterface,Runnable{
    
    public static final int AVVIO_PARTITA=1; 
    public static final int DUBITA_E_PRENDE_TUTTO=2; 
    public static final int DUBITA_E_AZZECCA=3; 
    public static final int GIOCA_UNA_CARTA=4;
    public static final int GIOCA_DUE_CARTE=5; 
    public static final int GIOCA_TRE_CARTE=6; 
    public static final int TERMINA_PARTITA=-1; 
    
    private String nomePartita;
    private ServerDubito server; 
    
    private int numeroGiocatori; //NECESSARIO PER SAPERE QUANDO STARTARE LA PARTITA 
    private boolean abbandono=false;
    
    private ArrayList<Carta> piatto; 
    private Hashtable<String, ArrayList<Carta>> tabellaCarte; 
    private ArrayList<Carta> mazzo; // CONTIENE TUTTE LE CARTE IN ORDINE DA DISTRIBUIRE 
    private ArrayList<Carta> giocataScorsa; 
    
    private int[] numeroCarteGiocatori; 
    
    private ArrayList<ClienteInterface> giocatoriInterfacce; 
    private String[] nomiPlayer; 
    
    private int [][] contoNumeroCartaPerPlayer; 
    
    private int turnoDiPlayer; 
    private int numeroDichiarato; 
    
    public Game(String name, ClienteInterface creatore,ServerDubito server){
        this.server=server; 
        inizializzaMazzo(); 
        piatto=new ArrayList<Carta>(); 
        numeroGiocatori=1; 
        giocatoriInterfacce= new ArrayList<ClienteInterface>(); 
        tabellaCarte= new Hashtable<String, ArrayList<Carta>>(); 
        giocatoriInterfacce.add(creatore); 
        this.nomePartita=name; 
        numeroCarteGiocatori=new int[4]; 
        
        contoNumeroCartaPerPlayer= new int[4][10]; 
    }
     
    
    @Override
    public void run() {
        nomiPlayer= new String[4]; 
        int k=0; 
        for(ClienteInterface c: giocatoriInterfacce){
            try { 
                nomiPlayer[k]= c.getNome();
            } catch (RemoteException ex) {
                terminaPerAbbandono();
            }
            k++; 
        }
        tabellaCarte.put("uno", new ArrayList<Carta>()); 
        tabellaCarte.put("due", new ArrayList<Carta>()); 
        tabellaCarte.put("tre", new ArrayList<Carta>()); 
        tabellaCarte.put("quattro", new ArrayList<Carta>()); 
        
        numeroCarteGiocatori[0]=10; 
        numeroCarteGiocatori[1]=10; 
        numeroCarteGiocatori[2]=10; 
        numeroCarteGiocatori[3]=10; 
        
        Random r=new Random(); 
        turnoDiPlayer= r.nextInt(4); // selezione player che parte 
        
        
        
        
        int playToAdd=0; 
        while(mazzo.isEmpty()){ //distribuisci carte
            int cartaDaDare= r.nextInt(mazzo.size()); 
            if(playToAdd==0){
                contoNumeroCartaPerPlayer[0][mazzo.get(cartaDaDare).getNumero()]++;
                tabellaCarte.get("uno").add(mazzo.remove(cartaDaDare)); 
            }
            else if(playToAdd==1){
                   contoNumeroCartaPerPlayer[1][mazzo.get(cartaDaDare).getNumero()]++;
                 tabellaCarte.get("due").add(mazzo.remove(cartaDaDare)); 
            }
            else if(playToAdd==2){
                contoNumeroCartaPerPlayer[2][mazzo.get(cartaDaDare).getNumero()]++;
                 tabellaCarte.get("tre").add(mazzo.remove(cartaDaDare)); 
            }
            else if(playToAdd==3){
                contoNumeroCartaPerPlayer[3][mazzo.get(cartaDaDare).getNumero()]++;
                 tabellaCarte.get("quattro").add(mazzo.remove(cartaDaDare)); 
            }
            
            playToAdd=(playToAdd+1)%4; 
        }
//        try { //QUESTI SONO POTENZIALMENTE DI TROPPO PERCHE LI INVIO CON LO START PARTITA
//            giocatoriInterfacce.get(0).riceviCarte(tabellaCarte.get("uno")); 
//            giocatoriInterfacce.get(0).riceviCarte(tabellaCarte.get("due"));
//            giocatoriInterfacce.get(0).riceviCarte(tabellaCarte.get("tre"));
//            giocatoriInterfacce.get(0).riceviCarte(tabellaCarte.get("quattro"));
//        } catch (RemoteException ex) {
//            terminaPerAbbandono();
//        }
        
        
//            for(ClienteInterface u: giocatoriInterfacce)
//                try{
//                //u.aggiornamentoGraficaPartita(numeroCarteGiocatori, piatto.size(),  turnoDiPlayer, AVVIO_PARTITA);
//                }
//            catch(RemoteException e){
//                 terminaPerAbbandono(); 
//            }
       try { //QUESTI SONO POTENZIALMENTE DI TROPPO PERCHE LI INVIO CON LO START PARTITA
           giocatoriInterfacce.get(0).gameStart(tabellaCarte.get("uno"),1,nomiPlayer);
            giocatoriInterfacce.get(0).gameStart(tabellaCarte.get("due"),2, nomiPlayer);
            giocatoriInterfacce.get(0).gameStart(tabellaCarte.get("tre"),3, nomiPlayer );
            giocatoriInterfacce.get(0).gameStart(tabellaCarte.get("quattro"),4, nomiPlayer);
        } catch (RemoteException ex) {
            terminaPerAbbandono();
        }
        while(numeroCarteGiocatori[0]!=0&&numeroCarteGiocatori[1]!=0&&numeroCarteGiocatori[2]!=0&&numeroCarteGiocatori[3]!=0){
            int mossa=-2; 
            for(ClienteInterface u: giocatoriInterfacce){
                try {
                    u.aggiornamentoGraficaPartita(numeroCarteGiocatori,piatto.size(), turnoDiPlayer, mossa);
                } catch (RemoteException ex) {
                  terminaPerAbbandono();
                }
            }
        }
    }
    
    public synchronized boolean addPlayer(ClienteInterface utente){
        if(numeroGiocatori<4){
            numeroGiocatori=numeroGiocatori+1; 
            giocatoriInterfacce.add(utente); 
            return true; 
        }
        else {
            new Thread(this).start();
            return false;
            
        }
    }

    
    @Override
    public void lasciaCoda(ClienteInterface utente) throws RemoteException {
        numeroGiocatori=numeroGiocatori-1; 
        giocatoriInterfacce.remove(utente); 
        if(numeroGiocatori<=0)
            endGame();
    }
    private String vincitore; 
    private void endGame(){
        for( ClienteInterface u: giocatoriInterfacce){
            try {
                u.gameEnd(vincitore);
            } catch (RemoteException ex) {
                
            }
        }
    }

    @Override
    public String getNome() throws RemoteException {
        return this.nomePartita; 
    }

    @Override
    public void startTime() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean dubita(int giocatore) throws RemoteException {
        boolean ris=true; //true ha fatto bene a dubitare, false ervano vere e si cucca tutto 
        for(Carta ca: giocataScorsa){
            if(numeroDichiarato!=ca.getNumero()){
                ris=false; 
                break;
            }
        }
        if(ris){
            ArrayList<Carta> tmp;
            switch(giocatore){
                case 1: 
                    
                    for(Carta c: piatto){
                       contoNumeroCartaPerPlayer[3][c.getNumero()]++;
                       numeroCarteGiocatori[3]++;
                    }
                    tmp= tabellaCarte.get("quattro");
                    tmp.addAll(piatto); 
                    tabellaCarte.put("quattro", tmp); 
                    piatto=new ArrayList<Carta>(); 
                    
                    giocatoriInterfacce.get(3).riceviCarte(tabellaCarte.get("quattro"));
                    for(ClienteInterface c: giocatoriInterfacce)
                        c.aggiornamentoGraficaPartita(numeroCarteGiocatori, 0, 1, DUBITA_E_AZZECCA);
                    break;
                case 2: 
                    for(Carta c: piatto){
                       contoNumeroCartaPerPlayer[0][c.getNumero()]++;
                       numeroCarteGiocatori[0]++;
                    }
                    tmp= tabellaCarte.get("uno");
                    tmp.addAll(piatto); 
                    tabellaCarte.put("uno", tmp); 
                    piatto=new ArrayList<Carta>(); 
                    
                    giocatoriInterfacce.get(0).riceviCarte(tabellaCarte.get("uno"));
                    for(ClienteInterface c: giocatoriInterfacce)
                        c.aggiornamentoGraficaPartita(numeroCarteGiocatori, 0, 2, DUBITA_E_AZZECCA);
                    break;
                case 3: 
                    for(Carta c: piatto){
                       contoNumeroCartaPerPlayer[1][c.getNumero()]++;
                        numeroCarteGiocatori[1]++;
                    }
                    tmp= tabellaCarte.get("due");
                    tmp.addAll(piatto); 
                    tabellaCarte.put("due", tmp); 
                    piatto=new ArrayList<Carta>(); 
                    giocatoriInterfacce.get(1).riceviCarte(tabellaCarte.get("due"));
                    for(ClienteInterface c: giocatoriInterfacce)
                        c.aggiornamentoGraficaPartita(numeroCarteGiocatori, 0, 3, DUBITA_E_AZZECCA);
                    break;
                case 4: 
                    for(Carta c: piatto){
                       contoNumeroCartaPerPlayer[2][c.getNumero()]++;
                       numeroCarteGiocatori[2]++;
                    }
                    tmp= tabellaCarte.get("tre");
                    tmp.addAll(piatto); 
                    tabellaCarte.put("tre", tmp); 
                    piatto=new ArrayList<Carta>(); 
                    giocatoriInterfacce.get(2).riceviCarte(tabellaCarte.get("tre"));
                    for(ClienteInterface c: giocatoriInterfacce)
                        c.aggiornamentoGraficaPartita(numeroCarteGiocatori, 0, 4, DUBITA_E_AZZECCA);
                    break;
                
            }
            
        }
        else{
           ArrayList<Carta> tmp= new ArrayList<Carta>(); 
           switch(giocatore){
               
                case 1:
                    for(Carta c: piatto){
                       contoNumeroCartaPerPlayer[0][c.getNumero()]++;
                       numeroCarteGiocatori[0]++;
                    }
                    tmp= tabellaCarte.get("uno");
                    tmp.addAll(piatto); 
                    tabellaCarte.put("uno", tmp); 
                    piatto=new ArrayList<Carta>(); 
                    
                    giocatoriInterfacce.get(0).riceviCarte(tabellaCarte.get("uno"));
                    for(ClienteInterface c: giocatoriInterfacce)
                        c.aggiornamentoGraficaPartita(numeroCarteGiocatori, 0, 1, DUBITA_E_PRENDE_TUTTO);
                    break;
                case 2:
                    for(Carta c: piatto){
                       contoNumeroCartaPerPlayer[1][c.getNumero()]++;
                       numeroCarteGiocatori[1]++;
                    }
                    tmp= tabellaCarte.get("due");
                    tmp.addAll(piatto); 
                    tabellaCarte.put("due", tmp); 
                    piatto=new ArrayList<Carta>(); 
                    
                    giocatoriInterfacce.get(1).riceviCarte(tabellaCarte.get("due"));
                    for(ClienteInterface c: giocatoriInterfacce)
                        c.aggiornamentoGraficaPartita(numeroCarteGiocatori, 0, 2, DUBITA_E_PRENDE_TUTTO);
                    break;
                case 3:
                    for(Carta c: piatto){
                       contoNumeroCartaPerPlayer[2][c.getNumero()]++;
                       numeroCarteGiocatori[2]++;
                    }
                    tmp= tabellaCarte.get("tre");
                    tmp.addAll(piatto); 
                    tabellaCarte.put("tre", tmp); 
                    piatto=new ArrayList<Carta>(); 
                    
                    giocatoriInterfacce.get(2).riceviCarte(tabellaCarte.get("tre"));
                    for(ClienteInterface c: giocatoriInterfacce)
                        c.aggiornamentoGraficaPartita(numeroCarteGiocatori, 0, 3, DUBITA_E_PRENDE_TUTTO);
                    break;
                case 4:
                    for(Carta c: piatto){
                       contoNumeroCartaPerPlayer[3][c.getNumero()]++;
                       numeroCarteGiocatori[3]++;
                    }
                    tmp= tabellaCarte.get("quattro");
                    tmp.addAll(piatto); 
                    tabellaCarte.put("quattro", tmp); 
                    piatto=new ArrayList<Carta>(); 
                    
                    giocatoriInterfacce.get(3).riceviCarte(tabellaCarte.get("quattro"));
                    for(ClienteInterface c: giocatoriInterfacce)
                        c.aggiornamentoGraficaPartita(numeroCarteGiocatori, 0, 4, DUBITA_E_PRENDE_TUTTO);
                    break;
                
            }
        }
        
        return ris; 
        
    }

    @Override
    public void giocaCarte(ArrayList<Carta> carte,int giocatore) throws RemoteException {
        giocataScorsa=carte; 
        for(Carta c:carte){
            switch(giocatore){
                case 1:
                    numeroCarteGiocatori[giocatore-1]--; 
                    contoNumeroCartaPerPlayer[giocatore-1][c.getNumero()]--; 
                    tabellaCarte.get("uno").remove(c); 
                    break; 
                case 2:
                    numeroCarteGiocatori[giocatore-1]--; 
                    contoNumeroCartaPerPlayer[giocatore-1][c.getNumero()]--; 
                    tabellaCarte.get("due").remove(c); 
                    break; 
                case 3:
                    numeroCarteGiocatori[giocatore-1]--; 
                    contoNumeroCartaPerPlayer[giocatore-1][c.getNumero()]--; 
                    tabellaCarte.get("tre").remove(c); 
                    break; 
                case 4:
                    numeroCarteGiocatori[giocatore-1]--; 
                    contoNumeroCartaPerPlayer[giocatore-1][c.getNumero()]--; 
                    tabellaCarte.get("quattro").remove(c); 
                    break; 
            }
        }
        
        
    }

    @Override
    public void dichiaraGiocata(int numero) throws RemoteException {
        numeroDichiarato=numero; 
    }

    private void terminaPerAbbandono() {
        abbandono=true; 
        for(ClienteInterface u: giocatoriInterfacce){
            try {
                u.gameEndPerAbbandono();
            } catch (RemoteException ex) {
                System.out.println("metodo terminaPerAbbandono invocato sul game che ha abbandonato ");
            }
        }
        server.rimuoviGame(this); 
        Thread.currentThread().interrupt(); //INSIEME AL BOOLEANO è FACILE CAPIRE CHE è TERMINATA LA PARTITA 
    }
    private void inizializzaMazzo() {
        mazzo.add(new Carta(1,Carta.PICCHE));
        mazzo.add(new Carta(2,Carta.PICCHE));
        mazzo.add(new Carta(3,Carta.PICCHE));
        mazzo.add(new Carta(4,Carta.PICCHE));
        mazzo.add(new Carta(5,Carta.PICCHE));
        mazzo.add(new Carta(6,Carta.PICCHE));
        mazzo.add(new Carta(7,Carta.PICCHE));
        mazzo.add(new Carta(8,Carta.PICCHE));
        mazzo.add(new Carta(9,Carta.PICCHE));
        mazzo.add(new Carta(10,Carta.PICCHE));
        
        mazzo.add(new Carta(1,Carta.FIORI));
        mazzo.add(new Carta(2,Carta.FIORI));
        mazzo.add(new Carta(3,Carta.FIORI));
        mazzo.add(new Carta(4,Carta.FIORI));
        mazzo.add(new Carta(5,Carta.FIORI));
        mazzo.add(new Carta(6,Carta.FIORI));
        mazzo.add(new Carta(7,Carta.FIORI));
        mazzo.add(new Carta(8,Carta.FIORI));
        mazzo.add(new Carta(9,Carta.FIORI));
        mazzo.add(new Carta(10,Carta.FIORI));
        
        mazzo.add(new Carta(1,Carta.CUORI));
        mazzo.add(new Carta(2,Carta.CUORI));
        mazzo.add(new Carta(3,Carta.CUORI));
        mazzo.add(new Carta(4,Carta.CUORI));
        mazzo.add(new Carta(5,Carta.CUORI));
        mazzo.add(new Carta(6,Carta.CUORI));
        mazzo.add(new Carta(7,Carta.CUORI));
        mazzo.add(new Carta(8,Carta.CUORI));
        mazzo.add(new Carta(9,Carta.CUORI));
        mazzo.add(new Carta(10,Carta.CUORI));
        
        mazzo.add(new Carta(1,Carta.QUADRI));
        mazzo.add(new Carta(2,Carta.QUADRI));
        mazzo.add(new Carta(3,Carta.QUADRI));
        mazzo.add(new Carta(4,Carta.QUADRI));
        mazzo.add(new Carta(5,Carta.QUADRI));
        mazzo.add(new Carta(6,Carta.QUADRI));
        mazzo.add(new Carta(7,Carta.QUADRI));
        mazzo.add(new Carta(8,Carta.QUADRI));
        mazzo.add(new Carta(9,Carta.QUADRI));
        mazzo.add(new Carta(10,Carta.QUADRI));
    }
    public boolean[] controllaScarto(int giocatore){
        boolean[] pieno=new boolean[10]; 
        for( int j=0; j<10;j++){
            if(contoNumeroCartaPerPlayer[giocatore][j]==4)
                pieno[j]=true;
            else 
                pieno[j]= false;           
        }
        return pieno; 
    }
}
