import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

/** 
 * Clase ClientFrame.java 
 * Crea el Frame del Chat
 * @author Revolution Software Developers
 **/

public class ClientFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	public static Client app;
	public static JMenuItem conectar = null;
	public static JMenuItem desconectar = null;
	public static JList lstUsers = null;
	public static JButton send = null;
	public static JTextArea msg = null;
	
	private JMenuBar menubar = null;
	private JMenu archivo = null;
	private JDesktopPane jDesktop = null;
	private JTextArea taLog = null;
	private JMenuItem cerrar = null;
	private JMenuItem guardar = null;
	private JMenu ayuda = null;
	private JMenuItem tutorial = null;
	private JMenuItem sobre = null;
	private LinkedList<Message> localhistory = new LinkedList<Message>();
	private Timer timer = null;
	private DefaultListModel userlist = new DefaultListModel();
	
	/**
	 * This is the default constructor
	 */
	public ClientFrame() {
		super("Revolution Chat");
		this.initialize();
	}

	/**
	 * This method initializes this
	 * @return void
	 */
	private void initialize() {
		this.setSize(788, 570);
		this.setResizable(false);
		this.setContentPane(getJDesktop());
		this.setJMenuBar(getMenubar());
		this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		
		timer = new Timer(100,new ActionListener(){
			/**
			 * Metodo usado dentro del timer para actualizar el screen cada cierto tiempo
			 * comparando el historial local contra el del servidor 
			 */
			public void actionPerformed(ActionEvent arg0) {
				if(ClientFrame.app != null && ClientFrame.app.client != null && !ClientFrame.app.client.isClosed()){
					LinkedList<Message> diffs = ClientFrame.app.compare(localhistory);
					
					while(!diffs.isEmpty()){
						Message mensaje = (Message)diffs.removeFirst();
						localhistory.addLast(mensaje);
						if(mensaje.getTipo() == Message.MENSAJE){
							displayMessage("\n"+mensaje.getUsuario()+ ">> " + mensaje.getMensaje());
						} else if(mensaje.getTipo() == Message.COMMAND){
							parseCommand(mensaje.getCommand()); // se parsea el comando
						}
					}
				}
			} 
			
		});
		timer.start();
		
		//-- Look n' Feel a la Windows Style
	    try { 
	    	UIManager.setLookAndFeel(new WindowsLookAndFeel());
	    	SwingUtilities.updateComponentTreeUI(this);
	    } catch (UnsupportedLookAndFeelException e){
	    	System.out.println("Error: Windows LookAndFeel no esta soportado");
	    	this.dispose();
	    }
	}
	
	/**
	 * Parsea el comando recibido de acuerdo a un filtro
	 * @param cmd
	 */
	private void parseCommand(Command cmd){
		//Command list = ((Message)ClientFrame.app.getUsers()).getCommand();
		switch(cmd.type){
		case Command.ADD_USER:
			System.out.println("Agrego usuario '"+(String)cmd.msg+"'");
			String user = (String)cmd.msg;
			this.userlist.addElement(user);
			break;
		case Command.REMOVE_USER:
			String user_ = (String)cmd.msg;
			this.userlist.removeElement(user_);
			break;
		case Command.CLOSE_CONNECTION:
			this.userlist.removeAllElements();
			ClientFrame.msg.setEnabled(false);
			ClientFrame.send.setEnabled(false);
			ClientFrame.conectar.setVisible(true);
			ClientFrame.desconectar.setVisible(false);
			ClientFrame.app.closeConnection();
			this.displayMessage("<< El server fue cerrado >> ");
			break;
		}
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
				archivo.add(getDesconectar());
				desconectar.setVisible(false);
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
	 * This method initializes desconectar	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getDesconectar() {
		if (desconectar == null) {
			try {
				desconectar = new JMenuItem("Desconectar");
				desconectar.addActionListener(this);
			} catch (java.lang.Throwable e) {
			
			}
		}
		return desconectar;
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
				jDesktop.add(getTaLog(), JLayeredPane.MODAL_LAYER);
				
				jDesktop.add(getMsg(), JLayeredPane.MODAL_LAYER);
				jDesktop.add(getSend(), JLayeredPane.MODAL_LAYER);
				jDesktop.add(getLstUsers(), JLayeredPane.MODAL_LAYER);
				this.initBoxes();
				
			} catch (java.lang.Throwable e) {
				
			}
		}
		return jDesktop;
	}

	/**
	 * Inicializa los bordes de los inputareas
	 */
	private void initBoxes(){
		JLabel bg = new JLabel();
		ImageIcon bgimg = Main.getIconImage("bg.png");
		bg.setIcon(bgimg);
		bg.setLocation(new Point(0,0));
		bg.setSize(new Dimension(800,547));
		jDesktop.add(bg,JLayeredPane.DEFAULT_LAYER);
		
		//-- Message Box Area
		JLabel msgbox = new JLabel();
		ImageIcon mbimg = Main.getIconImage("messagebox.png");
		msgbox.setIcon(mbimg);
		msgbox.setLocation(4,440);
		msgbox.setSize(new Dimension(775,75));
		jDesktop.add(msgbox,JLayeredPane.PALETTE_LAYER);
		
		//-- History Log
		JLabel logbg = new JLabel();
		ImageIcon logimg = Main.getIconImage("log.png");
		logbg.setIcon(logimg);
		logbg.setLocation(4,4);
		logbg.setSize(new Dimension(600,435));
		jDesktop.add(logbg,JLayeredPane.PALETTE_LAYER);
	}
	

	

	/**
	 * This method initializes jList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getLstUsers() {
		if (lstUsers == null) {
			try {
				lstUsers = new JList(this.userlist);
				lstUsers.setLocation(new Point(610,11));
				lstUsers.setSize(new Dimension(165,300));
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
	private JScrollPane getTaLog() {
		taLog = new JTextArea();
		taLog.setEditable(false);
		taLog.setSize(new Dimension(586,420));
		taLog.setLineWrap(true);
		taLog.setFont(new Font("Arial",Font.PLAIN,12));
		//taLog.setLocation(new Point(11,11));
		//taLog.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,1));
		
		JScrollPane log = new JScrollPane(taLog,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		log.setSize(new Dimension(586,420));
		log.setLocation(new Point(11,11));
		log.setOpaque(false);
		log.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,1));
		
		
		return log;
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
	private JScrollPane getMsg() {
		msg = new JTextArea();
		JScrollPane msgsp = new JScrollPane(msg,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		msg.setEnabled(false);
		msgsp.setSize(new Dimension(683,40));
		msgsp.setLocation(new Point(11,446));
		msgsp.setOpaque(false);
		msgsp.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,1));
		
		msg.setSize(new Dimension(683,40));
		msg.setLineWrap(true);
		msg.setToolTipText("Escriba su mensaje...");
		msg.setFont(new Font("Arial",Font.PLAIN,12));
		return msgsp;
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
				send.setLocation(new Point(700,446));
				send.setSize(new Dimension(70,40));
				send.setEnabled(false);
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
			String txt = ClientFrame.msg.getText();
			if(txt.equals("")) {
				return;
			}
			Message msg = new Message(txt,ConfigFrame.getNickname());	
			ClientFrame.app.sendMessage(msg);
			ClientFrame.msg.setText("");
		} else if(e.getSource().equals(conectar)){
			
			ConfigFrame frame = new ConfigFrame();
			frame.setVisible(true);
			
		} else if(e.getSource().equals(desconectar)){
			ClientFrame.app.closeConnection();
			ClientFrame.conectar.setVisible(true);
			ClientFrame.desconectar.setVisible(false);
			ClientFrame.send.setEnabled(false);
			ClientFrame.msg.setEnabled(false);
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
