import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/** 
 * Clase Server.java 
 * Administra las conexiones entre los clientes
 * @author Revolution Software Developers
 * @package server
 **/
public class Server {
	/**
	 * Servidor actual
	 */
	public static ServerSocket server = null;
	
	/**
	 * Clientes del servidor
	 */
	public static LinkedList<ClientThread> clients = new LinkedList<ClientThread>(); 
	
	/**
	 * Historial del Chat 
	 * Es un arreglo de [0] Usuario [1] Linea dicha
	 */
	public static LinkedList<Message> history = new LinkedList<Message>(); 
	
	/**
	 * Puerto de Conexion
	 */
	public static int port = 1211;
	
	/**
	 * Limite de Usuarios
	 */
	public static int user_limit = 100;
	
	/**
	 * Estado del Server
	 */
	public static boolean active = true;
	
	/**
	 * 
	 */
	public static ExecutorService pool; 
	   
	
	/**
	 * Constructor vacio
	 */
	public Server() {
		
	}

	/**
	 * Constructor con parametros definidos
	 */
	public Server(int port, int limit){
		Server.user_limit = limit;
		Server.port = port;
		
	}
	
	/**
	 * RunServer Crea los espacios de los sockets
	 */
	public void runServer(){
		Server.pool = Executors.newFixedThreadPool(Server.user_limit );
		
		try {
	         server = new ServerSocket(Server.port, Server.user_limit); 
	         System.out.println("Waiting Connections");
	        
	         while (Server.active) {
	        	try {
	            	Socket serversocket = server.accept();
	            	ClientThread connection = new ClientThread(serversocket);
	            	pool.execute(connection);
	            	Server.clients.addLast(connection);
	            	Server.viewConnections(); 
	            
	            //	this.processConnections();
	        	} catch ( EOFException eofException ) {
	            	System.out.println( "\nServer terminated connection" );
	            } finally {
	            	//-- this.closeConnections(); -- Se comenta porque es un servidor de varios, y tiene que seguir estando activo
	            } 
	         }
	         
	    } catch ( IOException ioException ) {
	    	ioException.printStackTrace();
	    } 
	    
	}
	
	/**
	 * Cierra las conexiones
	 * @throws IOException 
	 */
	public void close() throws IOException {
		ClientThread.sendToAll(new Message("SERVER>>> TERMINATE","SERVER"));
		this.closeConnections();
		Server.server.close();
		System.out.println("-- Conexiones cerradas --");
	}
	
	/**
	 * Cierra todas las conexiones existentes
	 */
	private void closeConnections() throws IOException {
		while(Server.clients != null && !Server.clients.isEmpty()){
			((ClientThread)(Server.clients.removeFirst())).close();
		}
	}
	
	/**
	 * Compara las ultimas entradas
	 */
	public LinkedList<Message> compare(LinkedList remote){
		LinkedList<Message> neu = new LinkedList<Message>();
		int j = -1;
		
		if(remote != null && !remote.isEmpty()) {
			Message aux = (Message)remote.getLast(); //-- Del Localhistory
			for(int i = history.size()-1; i>=0; i--){
				Message aux2 = (Message)history.get(i); //-- Trae los mas recientes
				if(aux2.equals(aux)){
					j = i+1;
					break;
				}
			}
		} else { 
			j = 0;
		}
		for(int k = j; k>=0 && k<history.size(); k++){
			neu.addLast(history.get(k));
		}
		return neu;
	}
	
	public static void viewConnections(){
		System.out.println("=========================");
		System.out.println("#  Conexiones Activas   #");
		System.out.println("=========================");
		for(int i=0; i<Server.clients.size();i++){
			ClientThread cliente = (ClientThread)Server.clients.get(i);
			System.out.println((1+i)+": "+cliente.getConnection().getInetAddress().getHostName());
		}
		System.out.println("\n-------------------------");
	}
	
	public static LinkedList<String> getUsers(){
		LinkedList<String> usuarios = new LinkedList<String>();
		for(int i=0; i<Server.clients.size();i++){
			ClientThread cliente = (ClientThread)Server.clients.get(i);
			usuarios.addLast(cliente.getNickname());
		}
		return usuarios;
	}
	
}