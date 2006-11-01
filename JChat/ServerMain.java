import java.io.File;
import javax.swing.ImageIcon;

/** 
 * Clase ServerMain.java 
 * Clase que inicializa el main
 * @author Revolution Software Developers
 * @package 
 **/

public class ServerMain {
	/**
	 * Ruta de la Aplicación
	 */
	public static final String RUTA = (new File ("")).getAbsolutePath()+"/";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ServerFrame2 frame = new ServerFrame2();
		int user_limit = 100;
		if ( args.length != 0 ) {
			String arg = args[0].toLowerCase();
			if(arg.equals("-limit") && args.length > 1){
				user_limit = Integer.parseInt(args[1]);
			}
			
		}
		Server.user_limit = user_limit;
		frame.setVisible(true);
		frame.server.runServer(); //TODO: Poner esto en el frame para especificar antes un mensaje de bienvenida
	}
	
	/**
	 * Regresa el ImageIcon de una imagen especificada
	 * @param filename
	 * @return image
	 */
	public static ImageIcon getIconImage(String filename){	
		ImageIcon image = new ImageIcon(ServerMain.RUTA+"img/"+filename);
		if(image.getImageLoadStatus()==4) return null;
		return image;
	}
}
