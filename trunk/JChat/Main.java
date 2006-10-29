import java.io.File;

import javax.swing.*;

/** 
 * Clase Main.java 
 *
 * @author Revolution Software Developers
 **/

public class Main {
	/**
	 * Ruta de la Aplicación
	 */
	public static final String RUTA = (new File ("")).getAbsolutePath()+"/";
	
	/**
	 * Debug
	 */
	public static final boolean DEBUG = true;
	
	/**
	 * @param args -server|-client -host 127.0.0.1
	 */
	public static void main(String[] args) {
		JFrame frame = null;
		int type = -1;
		
		if ( args.length == 0 ) {
			ConfigFrame.setHostip("127.0.0.1");
			frame = new ClientFrame();
			type = 0;
		} else {
			String arg = args[0].toLowerCase();
			if (arg.equals("-server")){
				frame = new ServerFrame();
				type = 1;
			} else if(arg.equals("-client")){
				type = 0;
				String ip = "127.0.0.1";
				if(args.length>2 && args[1].equals("-host")){
					ip = args[2];
				}
				ConfigFrame.setHostip(ip);
				frame = new ClientFrame();
			} else {
				ConfigFrame.setHostip("127.0.0.1");
				frame = new ClientFrame();
			}
			
		}
		
		frame.setVisible(true);
		if(type == 1){
			((ServerFrame)frame).server.runServer();
		} else if(type == 0){
			
		}
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
