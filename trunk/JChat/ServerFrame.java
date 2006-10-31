import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;

/** 
 * Clase ServerFrame.java 
 * Clase grafica para la administracion del servidor
 * @author Revolution Software Developers
 * @package 
 **/

public class ServerFrame extends JFrame implements ActionListener {
	
	/**
	 * Variable identificadora de Eclipse
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Display
	 */
	private JTextArea display = new JTextArea();
	
	/**
	 * Menu Bar
	 */
	private JMenuBar menubar = new JMenuBar();

	/**
	 * Cierra la aplicacion
	 */
	private JMenuItem cerrar   = new JMenuItem("Cerrar");
	
	/**
	 * Historia local, para no hacer dependiente el server del Frame
	 */
	private LinkedList<Message> localhistory = new LinkedList<Message>();
	
	/**
	 * Timer de Revision
	 */
	private Timer timer = null;
	
	/**
	 * Instancia del servidor
	 */
	public Server server = null;
	
	public ServerFrame() {
		this.setSize(new Dimension(800,600));
		this.setTitle("Revolution Chat [Server]");
		this.setResizable(true);
		this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		this.setLayout(new BorderLayout());
		this.add(display,BorderLayout.CENTER);
		cerrar.addActionListener(this);
		JMenu archivo = new JMenu("Archivo");
		archivo.add(cerrar);
		menubar.add(archivo);
		this.setJMenuBar(menubar);
		display.setAutoscrolls(true);
		display.setEditable(false);
		//-- Accion Principal
		server = new Server("Bienvenido a dexochannel");
		
		timer = new Timer(100,new ActionListener(){
			/**
			 * Metodo usado dentro del timer para actualizar el screen cada cierto tiempo
			 * comparando el historial local contra el del servidor 
			 */
			public void actionPerformed(ActionEvent arg0) {
				LinkedList<Message> diffs = server.compare(localhistory);
				
				while(!diffs.isEmpty()){
					Message mensaje = (Message)diffs.removeFirst();
					localhistory.addLast(mensaje);
					if(mensaje.getTipo() == Message.MENSAJE){
						displayMessage("\n"+mensaje.getUsuario()+ " >> " + mensaje.getMensaje());
					}
				}
			} 
			
		});
		timer.start();
	}
	
	/**
	 * Display de mensajes en un textarea
	 */
	public void displayMessage(final String messageToDisplay) {
	      SwingUtilities.invokeLater(
	         new Runnable() 
	         {
	            public void run() 
	            {
	               display.append( messageToDisplay ); 
	            } 
	         } 
	      ); 
	}

	/**
	 * Acciones del Frame
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(cerrar)){
			try {
				this.server.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			if(timer != null && timer.isRunning()){
				timer.stop();
			}
			Server.active = false;
			this.dispose();
			System.exit(1);
		}
	} 

}
