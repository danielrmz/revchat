import java.io.*;
import java.net.*;

/** 
 * Clase Connection.java 
 * Provee las conexiones de los usuarios
 * @author Revolution Software Developers
 * @package server
 **/

public class ServerThread implements Runnable {
	
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
	 * Nickname
	 */
	private String nickname = "";
	
	/**
	 * Constructor de la conexion
	 * @param connection
	 */
	public ServerThread(Socket connection) throws IOException {
		this.nickname = "";
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
		if(this.connection != null){
			try {
				
				this.output.close();
				this.input.close();
				this.connection.close();
				Server.clients.remove(this);
				this.connection = null;
				Message m = new Message(new Command(Command.REMOVE_USER,this.nickname),"SERVER");
				Message aux = (Message)Server.history.peek();
				if(aux.getTipo() == Message.COMMAND && aux.getCommand().type == Command.REMOVE_USER && aux.getCommand().msg.equals(this.nickname)){
					//-- la accion de este filtro esta en run... por si el usa el menuitem de cerrar
				} else {
					//-- Si el cliente no le dio cerrar en su ventana y lo cerro de otra forma
					//-- se detecto y se guarda en el historial
					Server.history.add(m);
					ServerThread.sendToAll(m);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
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
			ServerThread client = (ServerThread)Server.clients.get(i);
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
						ServerThread.sendToAll(message); //-- Se lo manda a todos
					} else if(message.getTipo() == Message.COMMAND){
						Command c = message.getCommand();
						if(c.type == Command.NICK_REGISTER ){
							String nick = (String)c.msg;
							Command c2 = new Command(Command.NICK_REGISTER,this.setNickname(nick));
							Message m = new Message(c2,"SERVER");
							this.sendMessage(m);
						} else if(c.type == Command.NICK_CHANGE){
							String nick = (String)c.msg;
							Command c2 = new Command(Command.NICK_REGISTER,this.changeNickname(nick));
							Message m = new Message(c2,"SERVER");
							this.sendMessage(m);
						} else if(c.type == Command.REMOVE_USER){
							this.close();
						} else if(c.type == Command.FETCH_USERS){
							Command fu = new Command(Command.FETCH_USERS,Server.getUsers());
							this.sendMessage(new Message(fu,"SERVER"));
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
	
	/**
	 * Establece el nickname
	 * @return
	 */
	public String getNickname(){
		return this.nickname;
	}
	
	/**
	 * Verifica si un nickname existe
	 * @param nickname
	 * @return
	 */
	public boolean nicknameExists(String nickname){
		//-- No debe tomar el mismo nombre que el servidor
		if(nickname.toUpperCase().equals("SERVER")) {
			return true;
		}
		
		//-- Se busca a ver si no esta registrado
		for(int i = 0; i < Server.clients.size(); i++){
			ServerThread client = (ServerThread)Server.clients.get(i);
			String aux = client.getNickname();
			if(aux.toUpperCase().equals(nickname.toUpperCase())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Cambia el nickname del usuario de esta conexion
	 * @param nickname
	 * @return true si fue satisfactorio el cambio false de lo contrario
	 */
	public boolean changeNickname(String nickname){
		if(!this.nicknameExists(nickname)){
			String changes[] = {this.nickname,nickname}; //Original,cambiado
			Message nickRegistered = new Message(new Command(Command.NICK_CHANGE,changes),"SERVER");
			ServerThread.sendToAll(nickRegistered);
			Server.history.add(nickRegistered);
			this.nickname = nickname;
			return true;
		}
		return false;
	}
	
	/**
	 * Establece el nickname de un usuario nuevo
	 * @param nickname
	 * @return true si lo registro false si ocurrio un error
	 */
	public boolean setNickname(String nickname){
		
		if(!this.nicknameExists(nickname)){
			this.nickname = nickname;
			
			//-- Se le notifica a todos que agreguen el nick a la lista
			if(!Server.initmsg.equals("")){
				this.sendMessage(new Message(Server.initmsg,"SERVER"));
			}
			
			Message nickRegistered = new Message(new Command(Command.ADD_USER,nickname),"SERVER");
			ServerThread.sendToAll(nickRegistered);
			return true;
		}
		return false;
	}
}
