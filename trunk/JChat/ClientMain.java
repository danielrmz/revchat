import java.io.File;

import javax.swing.ImageIcon;

/** 
 * Clase ClientMain.java 
 * Clase que inicializa la aplicacion de cliente solamente
 * @author Revolution Software Developers
 * @package 
 **/

public class ClientMain {
	/**
	 * Ruta de la Aplicación
	 */
	public static final String RUTA = (new File ("")).getAbsolutePath()+"/";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ClientFrame frame = new ClientFrame();
		frame.setVisible(true);
	}

}
