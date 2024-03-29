import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/** 
 * Clase Server.java 
 * Administra las conexiones entre los clientes
 * @author Revolution Software Developers
 **/
public class Server {
	/**
	 * Servidor actual
	 */
	public static ServerSocket server = null;
	
	/**
	 * Clientes del servidor
	 */
	public static LinkedList<ServerThread> clients = new LinkedList<ServerThread>(); 
	
	/**
	 * Historial del Chat 
	 * Es un arreglo de Mensajes
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
	 * Pool de threads
	 */
	public static ExecutorService pool; 

	/**
	 * Mensaje personalizado inicial
	 */
	public static String initmsg = "";
	
	/**
	 * Constructor vacio
	 */
	public Server(){}
	
	/**
	 * Constructor con mensaje de inicio
	 * @param initmsg
	 */
	public Server(String initmsg){
		Server.initmsg = initmsg;
	}
	
	/**
	 * RunServer Crea los espacios de los sockets
	 */
	public void runServer(){
		Server.pool = Executors.newFixedThreadPool(Server.user_limit );
		
		try {
	         server = new ServerSocket(Server.port, Server.user_limit); 
	         System.out.println("Esperando conexiones");
	        
	         while (Server.active) {
	        	try {
	            	Socket serversocket = server.accept();
	            	ServerThread connection = new ServerThread(serversocket);
	            	pool.execute(connection);
	            	Server.clients.addLast(connection);
	          //  	Server.viewConnections(); 
	        
	        	} catch ( EOFException eofException ) {
	            	System.out.println( "\nError del server" );
	            } catch(SocketException e){
	           
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
		this.closeConnections();
		if(Server.server!= null){
			Server.server.close();
		}
	}
	
	/**
	 * Cierra todas las conexiones existentes de forma remota, 
	 * por lo que no es necesario cerrarlas aqui, si se cerrara el programa como quiera.
	 */
	private void closeConnections() throws IOException {
		while(Server.clients != null && !Server.clients.isEmpty()){
			ServerThread client = (ServerThread)(Server.clients.removeFirst());
			client.sendMessage(new Message(new Command(Command.CLOSE_CONNECTION),"SERVER"));
			//client.close();
		}
	}
	
	/**
	 * Compara las ultimas entradas
	 */
	public LinkedList compare(LinkedList remote){
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
	
	/**
	 * Muestra las conexiones en consola
	 */
	public static void viewConnections(){
		System.out.println("=========================");
		System.out.println("#  Conexiones Activas   #");
		System.out.println("=========================");
		for(int i=0; i<Server.clients.size();i++){
			ServerThread cliente = (ServerThread)Server.clients.get(i);
			System.out.println((1+i)+": "+cliente.getConnection().getInetAddress().getHostName());
		}
		System.out.println("\n-------------------------\n");
	}
	
	/**
	 * Trae los usuarios disponibles
	 * @return LinkedList<String> usuarios existentes
	 */
	public static LinkedList<String> getUsers(){
		LinkedList<String> usuarios = new LinkedList<String>();
		for(int i=0; i<Server.clients.size();i++){
			ServerThread cliente = (ServerThread)Server.clients.get(i);
			usuarios.addLast(cliente.getNickname());
		}
		return usuarios;
	}
	
	public void kick(String usuario){
		for(int i=0; i<Server.clients.size();i++){
			ServerThread cliente = (ServerThread)Server.clients.get(i);
			if(cliente.getNickname().equals(usuario)){
				cliente.sendMessage(new Message(new Command(Command.CLOSE_CONNECTION),"SERVER"));
				return;
			}
		}
	}
	
}
