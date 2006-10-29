import java.io.Serializable;

/** 
 * Clase Command.java 
 *
 * @author Revolution Software Developers
 * @package 
 **/
public class Command implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Estatica que cierra la conexion 
		 */
		public static final int CLOSE_CONNECTION = 0;
		
		/**
		 * Estatica que pide la lista de usuarios
		 */
		public static final int FETCH_USERS = 1;
		
		/**
		 * Nick Register
		 */
		public static final int NICK_REGISTER = 2;
		
		
		
		/**
		 * Tipo local de comando
		 */
		public int type = -1;
		
		/**
		 * Anexos adicionals del comando
		 */
		public Object msg = null;
		
		/**
		 * Constructor de un comando solamente
		 * @param type
		 */
		public Command(int type){
			this.type = type;
		}
		
		/**
		 * Constructor
		 * @param msg
		 * @param type
		 */
		public Command(int type, Object msg){
			this.type = type;
			this.msg = msg;
		}
	}