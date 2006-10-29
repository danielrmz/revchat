import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;

/** 
 * Clase ClientFrame.java 
 *
 * @author Revolution Software Developers
 * @package 
 **/

public class ClientFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	public static Client app;
	
	private JMenuBar menubar = null;
	private JMenu archivo = null;
	public static JMenuItem conectar = null;
	private JDesktopPane jDesktop = null;
	private JPanel jPanel = null;
	private JPanel jPanel1 = null;
	private JPanel jPanel2 = null;
	public static JList lstUsers = null;
	private JTextArea taLog = null;
	private JMenuItem cerrar = null;
	private JTextArea msg = null;
	private JButton send = null;
	private JMenuItem guardar = null;
	private JMenu ayuda = null;
	private JMenuItem tutorial = null;
	private JMenuItem sobre = null;
	private LinkedList<Message> localhistory = new LinkedList<Message>();
	private Timer timer = null;
	/**
	 * This is the default constructor
	 */
	public ClientFrame() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(800, 595);
		this.setResizable(false);
		this.setContentPane(getJDesktop());
		this.setJMenuBar(getMenubar());
		this.setTitle("Revolution Chat");
		this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		timer = new Timer(100,new ActionListener(){
			/**
			 * Metodo usado dentro del timer para actualizar el screen cada cierto tiempo
			 * comparando el historial local contra el del servidor 
			 */
			public void actionPerformed(ActionEvent arg0) {
				if(ClientFrame.app != null && ClientFrame.app.client.isConnected()){
					LinkedList<Message> diffs = ClientFrame.app.compare(localhistory);
					
					while(!diffs.isEmpty()){
						Message mensaje = (Message)diffs.removeFirst();
						localhistory.addLast(mensaje);
						if(mensaje.getTipo() == Message.MENSAJE){
							displayMessage("\n"+mensaje.getUsuario()+ " >> " + mensaje.getMensaje());
						}
					}
				}
			} 
			
		});
		timer.start();
	}

	/**
	 * This method initializes menubar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuBar getMenubar() {
		if (menubar == null) {
			try {
				menubar = new JMenuBar();
				menubar.add(getArchivo());
				menubar.add(getAyuda());
			} catch (java.lang.Throwable e) {
				
			}
		}
		return menubar;
	}

	/**
	 * This method initializes archivo	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getArchivo() {
		if (archivo == null) {
			try {
				archivo = new JMenu("Archivo");
				archivo.add(getConectar());
				archivo.addSeparator();
				archivo.add(getGuardar());
				archivo.addSeparator();
				archivo.add(getCerrar());
			} catch (java.lang.Throwable e) {
				
			}
		}
		return archivo;
	}

	/**
	 * This method initializes conectar	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getConectar() {
		if (conectar == null) {
			try {
				conectar = new JMenuItem("Conectar");
				conectar.addActionListener(this);
			} catch (java.lang.Throwable e) {
			
			}
		}
		return conectar;
	}

	/**
	 * This method initializes jDesktopPane	
	 * 	
	 * @return javax.swing.JDesktopPane	
	 */
	private JDesktopPane getJDesktop() {
		if (jDesktop == null) {
			try {
				
				jDesktop = new JDesktopPane();
				jDesktop.setBackground(Color.GRAY);
				jDesktop.add(getJPanel(), null);
				jDesktop.add(getJPanel1(), null);
				jDesktop.add(getJPanel2(), null);
			} catch (java.lang.Throwable e) {
				
			}
		}
		return jDesktop;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			try {
				jPanel = new JPanel(new GridLayout(1,1));
				jPanel.setBounds(new java.awt.Rectangle(8,6,594,430));
				jPanel.add(getTaLog(), null);
			} catch (java.lang.Throwable e) {
				
			}
		}
		return jPanel;
	}

	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			try {
				jPanel1 = new JPanel(new GridLayout(1,1));
				jPanel1.setBounds(new java.awt.Rectangle(612,4,174,430));
				jPanel1.add(getLstUsers(), null);
			} catch (java.lang.Throwable e) {
				
			}
		}
		return jPanel1;
	}

	/**
	 * This method initializes jPanel2	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			try {
				jPanel2 = new JPanel(new BorderLayout());
				jPanel2.setBounds(new java.awt.Rectangle(8,445,775,90));
				jPanel2.add(getMsg(), BorderLayout.CENTER);
				jPanel2.add(getSend(), BorderLayout.EAST);
			} catch (java.lang.Throwable e) {
				
			}
		}
		return jPanel2;
	}

	/**
	 * This method initializes jList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getLstUsers() {
		if (lstUsers == null) {
			try {
				lstUsers = new JList();
			} catch (java.lang.Throwable e) {
				
			}
		}
		return lstUsers;
	}

	/**
	 * This method initializes jTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getTaLog() {
		if (taLog == null) {
			try {
				taLog = new JTextArea();
				taLog.setEditable(false);
			} catch (java.lang.Throwable e) {
				
			}
		}
		return taLog;
	}

	/**
	 * This method initializes cerrar	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getCerrar() {
		if (cerrar == null) {
			try {
				cerrar = new JMenuItem("Cerrar");
				cerrar.addActionListener(this);
			} catch (java.lang.Throwable e) {
				
			}
		}
		return cerrar;
	}



	/**
	 * This method initializes msg	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getMsg() {
		if (msg == null) {
			try {
				msg = new JTextArea();
			} catch (java.lang.Throwable e) {
				
			}
		}
		return msg;
	}

	/**
	 * This method initializes send	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSend() {
		if (send == null) {
			try {
				send = new JButton("Enviar");
				send.addActionListener(this);
				send.setDefaultCapable(true);
			} catch (java.lang.Throwable e) {
				
			}
		}
		return send;
	}

	/**
	 * This method initializes guardar	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getGuardar() {
		if (guardar == null) {
			try {
				guardar = new JMenuItem("Guardar");
				guardar.addActionListener(this);
			} catch (java.lang.Throwable e) {
				
			}
		}
		return guardar;
	}

	/**
	 * This method initializes ayuda	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getAyuda() {
		if (ayuda == null) {
			try {
				ayuda = new JMenu("Ayuda");
				ayuda.add(getTutorial());
				ayuda.add(getSobre());
			} catch (java.lang.Throwable e) {
				
			}
		}
		return ayuda;
	}

	/**
	 * This method initializes tutorial	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getTutorial() {
		if (tutorial == null) {
			try {
				tutorial = new JMenuItem("Tutorial");
			} catch (java.lang.Throwable e) {
				
			}
		}
		return tutorial;
	}

	/**
	 * This method initializes sobre	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getSobre() {
		if (sobre == null) {
			try {
				sobre = new JMenuItem("Sobre");
			} catch (java.lang.Throwable e) {
				
			}
		}
		return sobre;
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(cerrar)){
			if(timer!=null && timer.isRunning()){
				timer.stop();
			}
			System.exit(1);
			this.dispose();
		} else if(e.getSource().equals(send)){
			String txt = this.msg.getText();
			if(txt.equals("")) {
				return;
			}
			Message msg = new Message(txt,ConfigFrame.getNickname());	
			ClientFrame.app.sendMessage(msg);
			this.msg.setText("");
		} else if(e.getSource().equals(conectar)){
			
			ConfigFrame frame = new ConfigFrame();
			frame.setVisible(true);
			
		}
	}
	
	/**
	 * Estaticas
	 */
	public static void setClient(String ip){
		ClientFrame.app = new Client(ip);
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
	               taLog.append( messageToDisplay ); 
	            } 
	         } 
	      ); 
	}
	
}  //  @jve:decl-index=0:visual-constraint="139,5"
