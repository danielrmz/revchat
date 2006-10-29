import java.io.*;
import java.net.*;

/** 
 * Clase Connection.java 
 * Provee las conexiones de los usuarios
 * @author Revolution Software Developers
 * @package server
 **/

public class ClientThread implements Runnable {
	
	/**
	 * Objeto de Salida para el cliente
	 */
	private ObjectOutputStream output; 
	
	/**
	 * Objeto de Entrada para el cliente
	 */
	private ObjectInputStream input; // input stream from client
		
	/**
	 * Socket de conexion del cliente
	 */
	private Socket connection; 
	
	/**
	 * Contador de Conexiones
	 */
	public static int counter = 1; // counter of number of connections
	
	/**
	 * Nickname
	 */
	private String nickname = "";
	
	/**
	 * Constructor de la conexion
	 * @param connection
	 */
	public ClientThread(Socket connection) throws IOException {
		ClientThread.counter++;
		this.nickname = "User_"+ClientThread.counter;
		this.connection = connection;
		this.output = new ObjectOutputStream(connection.getOutputStream());
		this.output.flush();
	    this.input = new ObjectInputStream(connection.getInputStream());
	}
	
	/**
	 * Cierra las conexiones
	 * @throws IOException
	 */
	public void close() {
		try {
			ClientThread.counter--;
			this.output.close();
			this.input.close();
			this.connection.close();
			Server.clients.remove(this);
			this.connection = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Manda un mensaje al host de la conexion
	 * @param msg
	 */
	public void sendMessage(Object msg) {
		
		try {
			output.writeObject(msg);
			output.flush();
		} catch ( IOException ioException ) {
			ioException.printStackTrace();
			System.out.println( "\nError writing object" );
		}
		
	}
	
	/**
	 * Trae el mensaje proporcionado por el cliente
	 * @return
	 * @throws IOException
	 */
	public Message getMessage() throws SocketException, IOException {
		 Message message = null;
		 
		 if(this.connection != null){
			 try {
				message = (Message) input.readObject();
	         } catch(SocketException e) {
	        	Server.clients.remove(this);
	 			this.close();
	 		 } catch ( ClassNotFoundException classNotFoundException ) {
	        	 System.out.println( "\nUnknown object type received" );
	         } catch (EOFException eof){
	        	Server.clients.remove(this);
	  			this.close();
	         }
		 }
         return message;
	}
	
	/**
	 * Trae la conexion
	 * @return connection
	 */
	public Socket getConnection(){
		return this.connection;
	}
	
	/**
	 * Manda el mensaje a todas las demas conexiones
	 * @param message
	 */
	public static void sendToAll(Object message){
		for(int i = 0; i < Server.clients.size(); i++){
			ClientThread client = (ClientThread)Server.clients.get(i);
			client.sendMessage(message);
		}
	}
	
	/**
	 * Trae el hostname dependiendo de un ip
	 * @return
	 */
	public String getHostName(){
		return this.connection.getInetAddress().getHostName();
	}

	/**
	 * Run, checa por nuevos mensajes
	 */
	public void run() {
		
		while(this.connection != null){
			
			try {
				
				Message message = getMessage();
				
				if(message != null){
					Server.history.addLast(message); //-- Copia a Version local
					
					if(message.getTipo() == Message.MENSAJE){
						ClientThread.sendToAll(message); //-- Se lo manda a todos
					} else if(message.getTipo() == Message.COMMAND){
						Command c = message.getCommand();
						if(c.type == Command.NICK_REGISTER){
							String nick = (String)c.msg;
							Command c2 = new Command(Command.NICK_REGISTER,this.setNickname(nick));
							Message m = new Message(c2,"SERVER");
							this.sendMessage(m);
						} else if(c.type == Command.FETCH_USERS){
							Command c2 = new Command(Command.FETCH_USERS,Server.getUsers());
							Message m = new Message(c2,"SERVER");
							ClientThread.sendToAll(m); //-- Se actualiza la lista de todos
						}
					}
				}	
				Thread.sleep(10);
			
			} catch (SocketException ee){ 
				this.close();
			} catch (InterruptedException e) {
					e.printStackTrace();
			} catch (Exception e){
				e.printStackTrace();
				this.close();
			} 
		}
	}
	
	public String getNickname(){
		return this.nickname;
	}
	
	public boolean setNickname(String nickname){
		for(int i = 0; i < Server.clients.size(); i++){
			ClientThread client = (ClientThread)Server.clients.get(i);
			String aux = client.getNickname();
			if(aux.equals(nickname)) {
				return false;
			}
		}
		this.nickname = nickname;
		return true;
	}
}
