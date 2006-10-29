import java.io.*;
import java.net.*;
import java.util.LinkedList;

/** 
 * Clase Client.java 
 * Engloba toda la información de un cliente del sistema.
 * 
 * @author Revolution Software Developers
 * @package client
 **/

public class Client {
	/**
	 * Nickname 
	 */
	private String nickname = "";
	
	/**
	 * IP del servidor
	 */
	private String serverip = "";
	
	/**
	 * Stream de output al servidor
	 */
	private ObjectOutputStream output; 
	
	/**
	 * Stream de input al servidor
	 */
	private ObjectInputStream input; 
	
	/**
	 * Log local
	 */
	public LinkedList<Message> localhistory = new LinkedList<Message>();

	/**
	 * Socket de conexion
	 */
	public Socket client; 
	
	
	/**
	 * Constructor
	 * @param ip IP del servidor
	 * @param nickname Nickname del usuario
	 */
	public Client(String ip) {
		this.serverip = ip;
		this.runClient();
		
	}
	
	/**
	 * Empieza la conexion con el servidor
	 */
	public void runClient() {
		try {
			System.out.println( "Attempting connection\n" );
			client = new Socket(InetAddress.getByName(this.serverip) , 1211 );
			System.out.println( "Connected to: " + client.getInetAddress().getHostName() );
			
			output = new ObjectOutputStream( client.getOutputStream() );
			output.flush();
			Receiver r = new Receiver();
			r.start();
			
		} catch (ConnectException e){
			System.out.println("Fallo la conexion con el servidor.");
		} catch ( EOFException eofException ) {
			System.out.println("\nClient terminated connection");
		} catch ( IOException ioException ) {
			ioException.printStackTrace();
		} 
	} 
	
	/**
	 * Cierra las conexiones
	 */
	public void closeConnection() {
		try {
	         output.close(); 
	         input.close(); 
	         client.close(); 
	         System.out.println("Conexion del cliente terminada");
	      
	    } catch ( IOException ioException ) {
	         ioException.printStackTrace();
	    } 
	}

	/**
	 * Manda el mensaje
	 * @param message
	 */
	public void sendMessage( Object message ) {
		 try {
			 output.writeObject(message);
			 output.flush();
		 } catch ( IOException ioException ) {
			 ioException.printStackTrace();
	         System.out.println( "\nError writing object" );
		 } 
	 }

	/**
	 * @param serverip The serverip to set.
	 */
	public void setServerip(String serverip) {
		this.serverip = serverip;
		this.closeConnection();
		this.runClient();
	}

	/**
	 * Compara las ultimas entradas
	 */
	public LinkedList<Message> compare(LinkedList remote){
		LinkedList<Message> neu = new LinkedList<Message>();
		int j = -1;
		
		if(remote != null && !remote.isEmpty()) {
			Message aux = (Message)remote.getLast(); //-- Del Localhistory
			for(int i = localhistory.size()-1; i>=0; i--){
				Message aux2 = (Message)localhistory.get(i); //-- Trae los mas recientes
				if(aux2.equals(aux)){
					j = i+1;
					break;
				}
			}
		} else { 
			j = 0;
		}
		for(int k = j; k>=0 && k<localhistory.size(); k++){
			neu.addLast(localhistory.get(k));
		}
		return neu;
	}
	
	/**
	 * @return Returns the localhistory.
	 */
	public LinkedList<Message> getLocalhistory() {
		return localhistory;
	}

	/**
	 * @param localhistory The localhistory to set.
	 */
	public void setLocalhistory(LinkedList<Message> localhistory) {
		this.localhistory = localhistory;
	}

	/**
	 * @return Returns the nickname.
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * @param nickname The nickname to set.
	 */
	public boolean setNickname(String nickname) {
		Message regnick = new Message(new Command(Command.NICK_REGISTER,this.nickname),this.client.getLocalAddress().toString());
		this.sendMessage(regnick);
		try {
			Thread.sleep(15);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Message reply = this.localhistory.getLast();
		boolean ok = true;
		if(reply.getTipo() == Message.COMMAND){
			Command c = reply.getCommand();
			if(c.type == Command.NICK_REGISTER){
				ok = ((Boolean)c.msg).booleanValue();
			}
		}
		
		this.nickname = (ok)?nickname:"Usuario";
		return ok;
	}
	
	public Message getUsers(){
		Message regnick = new Message(new Command(Command.FETCH_USERS,this.nickname),this.client.getLocalAddress().toString());
		this.sendMessage(regnick);
		try {
			Thread.sleep(15);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Message reply = this.localhistory.getLast();
		if(reply.getTipo() == Message.COMMAND){
			Command c = reply.getCommand();
			if(c.type == Command.FETCH_USERS){
				return reply;
			}
		}
		return null;
	}

	private class Receiver extends Thread {
		private Message message = null;
		
		public Receiver(){
			try {
				input = new ObjectInputStream( client.getInputStream() );
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		public void run() {
			while(ClientFrame.app.client != null && ClientFrame.app.client.isConnected()){
				try {
					message = (Message)input.readObject();
					localhistory.addLast(message);
						
					if(message != null && message.getTipo() == Message.COMMAND){
						Command c = message.getCommand();
						if(c.type == Command.CLOSE_CONNECTION && message.getUsuario().equals("SERVER")){
							closeConnection();
						}
					}
				} catch (SocketException se) {
					closeConnection();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	
}
