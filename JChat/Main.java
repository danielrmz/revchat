import java.io.File;

import javax.swing.*;

/** 
 * Clase Main.java 
 * Clase inicializadora de ambas aplicaciones, cliente y servidor dependiendo de los parametros
 * @author Revolution Software Developers
 **/

public class Main {
	/**
	 * Ruta de la Aplicación
	 */
	public static final String RUTA = (new File ("")).getAbsolutePath()+"/";

	/**
	 * @param args -server|-client -host 127.0.0.1
	 */
	public static void main(String[] args) {
		JFrame frame = null;
		
		if ( args.length == 0 ) {
			ConfigFrame.setHostip("127.0.0.1");
			frame = new ClientFrame();
		} else {
			String arg = args[0].toLowerCase();
			if (arg.equals("-server")){
				frame = new ServerFrame();
			} else if(arg.equals("-client")){
				ConfigFrame.setHostip("127.0.0.1");
				frame = new ClientFrame();
			} else {
				ConfigFrame.setHostip("127.0.0.1");
				frame = new ClientFrame();
			}
			
		}
		frame.setVisible(true);
		
	}
	
	/**
	 * Regresa el ImageIcon de una imagen especificada
	 * @param filename
	 * @return image
	 */
	public static ImageIcon getIconImage(String filename){	
		ImageIcon image = new ImageIcon(Main.RUTA+"img/"+filename);
		if(image.getImageLoadStatus()==4) return null;
		return image;
	}
}
