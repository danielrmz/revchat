import java.io.*;


/** 
 * Clase Message.java 
 * Clase del mensaje que se transportara, en ella se determina 
 * el tipo de mensaje y quien lo manda. Opcionalmente 
 * se le puede agregar un campo de destinatario para usar mensajes
 * privados.
 * 
 * @author Revolution Software Developers
 * @package client
 **/

public class Message implements Serializable {
	/**
	 * Variable de Eclipse
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Estatica que indica el tipo de mensaje
	 * Si es mensaje simple entonces se espera un String de regreso
	 */
	public  static final int MENSAJE = 0;
	
	/**
	 * Estatica que indica que el contenido es un comando
	 * Significa que puede mandar un contenido adicinoal
	 * Por ejemplo:
	 * new Message(new Command(Command.FETCH_USERS,user_list));
	 */
	public static final int COMMAND = 1;
	
	/**
	 * Mensaje
	 * Es de tipo object para poder pasar mensajes genericos 
	 * como la lista de usuarios sin tener q mandar una por una
	 */
	private final Object mensaje;
	
	/**
	 * Usuario creador del mensaje
	 */
	private final String usuario;
	
	/**
	 * Usuario del destinatario, para futuros mensajes privados
	 */
	private final String destinatario;
	
	/**
	 * Tipo de mensaje
	 */
	private final int tipo;
	
	/**
	 * Constructor de un mensaje simple
	 * @param mensaje
	 * @param usuario
	 */
	public Message(String mensaje, String usuario) {
		this.mensaje = mensaje;
		this.usuario = usuario;
		this.tipo = MENSAJE;
		this.destinatario = "";
	}
	
	/**
	 * Constructor para un destinatario especifico
	 * @param mensaje
	 * @param usuario
	 * @param destinatario
	 */
	public Message(String mensaje, String usuario, String destinatario) {
		this.mensaje = mensaje;
		this.usuario = usuario;
		this.tipo = Message.MENSAJE;
		this.destinatario = destinatario;
	}
	
	/**
	 * Constructor de un mensaje de un tipo determinado
	 * @param mensaje
	 * @param usuario
	 * @param type
	 */
	public Message(Command mensaje, String usuario) {
		this.mensaje = mensaje;
		this.usuario = usuario;
		this.tipo = Message.COMMAND;
		this.destinatario = "";
	}
	/**
	 * Mensaje de un tipo determinado para un usuario especifico
	 * @param mensaje
	 * @param usuario
	 * @param type
	 */
	public Message(Command mensaje, String usuario, String destinatario) {
		this.mensaje = mensaje;
		this.usuario = usuario;
		this.tipo = Message.COMMAND;
		this.destinatario = destinatario;
	}
	
	
	/**
	 * @return Returns the mensaje, si es string
	 */
	public String getMensaje() {
		return (String)this.mensaje;
	}
	
	/**
	 * @return Command
	 */
	
	public Command getCommand(){
		return (Command)this.mensaje;
	}

	/**
	 * @return Returns the usuario.
	 */
	public String getUsuario() {
		return this.usuario;
	}
	
	/**
	 * @return  tipo
	 */
	public int getTipo(){
		return this.tipo;
	}
	
	/**
	 * @return destinatario
	 */
	public String getDestinatario(){
		return this.destinatario;
	}
	
}
