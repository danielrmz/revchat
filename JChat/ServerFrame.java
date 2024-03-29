import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.Timer;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

/** 
 * Clase ClientFrame.java 
 * Crea el Frame del Chat
 * @author Revolution Software Developers
 **/

public class ServerFrame extends JFrame implements ActionListener {
	/**
	 * Constante de Eclipse
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Conexion del Cliente
	 */
	public Server server;
	
	/**
	 * Menu Item Conectar
	 */
	public JMenuItem conectar = null;
	
	/**
	 * Menu Item desconectar
	 */
	public JMenuItem desconectar = null;
	
	/**
	 * JList de Usuarios conectados
	 */
	public static JList lstUsers = null;
	
	/**
	 * Boton de Enviar mensaje
	 */
	public  JButton send = null;
	
	/**
	 * Textarea del mensaje a mandar
	 */
	public  JTextArea msg = null;
	
	/**
	 * Panel de Contenidos principal en capas
	 */
	private JDesktopPane jDesktop = null;
	
	/**
	 * Textarea del Historial
	 */
	private JTextArea taLog = null;
	
	/**
	 * Menu item cerrar
	 */
	private JMenuItem cerrar = null;
	
	/**
	 * Menu item guardar
	 */
	private JMenuItem guardar = null;
	
	/**
	 * Linkedlist de la historia local.
	 */
	private LinkedList<Message> localhistory = new LinkedList<Message>();
	
	/**
	 * Timer del action listener
	 */
	private Timer timer = null;
	
	/**
	 * Lista de usuarios que se adiere a la JList, para poder manipular los 
	 * items facilmente
	 */
	private DefaultListModel userlist = new DefaultListModel();
	
	/**
	 * Header de la lista de contactos 
	 */
	private JLabel lblNumContacts = new JLabel();
	
	/**
	 * Boton de logout
	 */
	public JButton logout = new JButton();
	
	/**
	 * Ruta de la aplicacino
	 */
	private final String ruta = (new File ("").getAbsolutePath());
	
	/**
	 * Ventanas privadas abiertas
	 */
	public static LinkedList<String> privates = new LinkedList<String>();
	
	/**
	 * This Constructor por default
	 */
	public ServerFrame() {
		super("Revolution Chat [SERVER]");
		this.initialize();
	}
	
	/**
	 * Acciones del Frame
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == cerrar || e.getSource() == logout){
			this.close();
		} else if(e.getSource() == send){
			this.send();
		} else if(e.getSource() == guardar){
			this.save();
		}
	}
	
	/**
	 * Cierra la aplicacion
	 */
	private void close(){
		try {
			this.server.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		if(timer != null && timer.isRunning()){
			timer.stop();
		}
		
		Server.active = false;
		System.exit(1);
	}
	
	//-- Empiezan bloques de construccion y manejo de la GUI
	/**
	 * Inicializador de Componentes
	 */
	private void initialize() {
		this.setSize(788, 570);
		this.setResizable(false);
		this.setContentPane(getJDesktop());
		this.setJMenuBar(getJMenubar());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addWindowListener(new WindowListener(){

			public void windowOpened(WindowEvent arg0) {}
			public void windowClosing(WindowEvent arg0) {close();} //-- Cerrar los clientes si se presiona la cruz
			public void windowClosed(WindowEvent arg0) {}
			public void windowIconified(WindowEvent arg0) {}
			public void windowDeiconified(WindowEvent arg0) {}
			public void windowActivated(WindowEvent arg0) {}
			public void windowDeactivated(WindowEvent arg0) {}});
		
		server = new Server("== Bienvenido ==");
		
		timer = new Timer(100,new ActionListener(){
			/**
			 * Metodo usado dentro del timer para actualizar el screen cada cierto tiempo
			 * comparando el historial local contra el del servidor 
			 */
			public void actionPerformed(ActionEvent arg0) {
				if(server!=null){
					LinkedList diffs = server.compare(localhistory);
					
					while(!diffs.isEmpty()){
						Message mensaje = (Message)diffs.removeFirst();
						localhistory.addLast(mensaje);
						if(mensaje.getTipo() == Message.MENSAJE && !mensaje.getUsuario().equals("SERVER") && mensaje.getDestinatario().equals("")){
							displayMessage(mensaje.getUsuario()+ ">> " + mensaje.getMensaje()+"\n");
						} else if(mensaje.getTipo() == Message.MENSAJE && mensaje.getUsuario().equals("SERVER") &&  mensaje.getDestinatario().equals("")){
							displayMessage(mensaje.getMensaje()+"\n\n");
						} else if(mensaje.getTipo() == Message.COMMAND &&  mensaje.getDestinatario().equals("")){
							parseCommand(mensaje.getCommand()); // se parsea el comando
						} else if(mensaje.getTipo() == Message.MENSAJE && !mensaje.getDestinatario().equals("")){
							if(!ClientFrame.privates.contains(mensaje.getUsuario())&& !mensaje.getUsuario().equals("SERVER")){
								PrivateFrame frame = new PrivateFrame(server,localhistory,mensaje.getUsuario());
								ClientFrame.privates.addLast(mensaje.getUsuario());
								frame.setVisible(true);
						    } 
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
	    }
	}
	
	/**
	 * Parsea el comando recibido de acuerdo a un filtro
	 * @param cmd
	 */
	private void parseCommand(Command cmd){
		switch(cmd.type){
		case Command.NICK_CHANGE:
			if(cmd.msg!=null){
				try {
					String nicks[] = (String[])cmd.msg;
					this.userlist.removeElement(nicks[0]);
					this.userlist.addElement(nicks[1]);
					this.displayMessage("> El usuario [ "+nicks[0]+" ] ha cambiado su nickname a [ "+nicks[1]+" ]\n");
				} catch (ClassCastException ee){} 
			}
			break;
		
		case Command.REMOVE_USER:
			String user_ = (String)cmd.msg;
			this.userlist.removeElement(user_);
			if(!user_.equals("")){
				this.displayMessage("> El usuario "+user_+" se salio de la sala.\n");
			}
			this.lblNumContacts.setText("Usuarios ["+this.userlist.getSize()+"]");
			
			break;
			
		case Command.ADD_USER:
			String user = (String)cmd.msg;
			this.displayMessage("> El usuario "+user+" entro en la sala.\n");
			//-- Dropdown para ordenarlos
		case Command.FETCH_USERS:
			Object[] usuarios = this.sort(Server.getUsers());

			this.userlist.removeAllElements();
			for(int i = 0; i < usuarios.length; i++){
				this.userlist.add(i,(String)usuarios[i]);
			}
			
			this.lblNumContacts.setText("Usuarios ["+this.userlist.size()+"]");
			break;
		}
	}
	
	/**
	 * Ordena la lista mandada
	 * @param usuarios
	 * @return Object[] lista ordenada
	 */
	private Object[] sort(LinkedList usuarios){
		Object[] u = usuarios.toArray();
		
		for(int i=0;i<usuarios.size();i++){
			for(int j=i+1;j<usuarios.size();j++){
				if(((String)u[i]).hashCode()>((String)u[j]).hashCode()){
					String aux = (String)u[j];
					u[j] = u[i];
					u[i] = aux;
				}
			}
		}
		return u;
	}
	
	/**
	 * Regresa la fecha actual
	 * @return String fecha
	 */
	private String getDate(){
		java.text.DateFormat format = java.text.DateFormat.getDateTimeInstance();
		format.setTimeZone(TimeZone.getTimeZone("UTC"));
		String date = format.format(new Date());
		return date;
	}
	/**
	 * Inicializa la barra de menu	
	 * @return JMenuBar	
	 */
	private JMenuBar getJMenubar() {
		JMenuBar menubar = new JMenuBar();
		JMenu archivo = new JMenu("Archivo");
		
		guardar = new JMenuItem("Guardar");
		guardar.addActionListener(this);
		cerrar = new JMenuItem("Cerrar");
		cerrar.addActionListener(this);
		
		archivo.add(guardar);
		archivo.addSeparator();
		archivo.add(cerrar);
		
		
		menubar.add(archivo);
		return menubar;
	}
	
	/**
	 * Inicializa el Desktoppane y todos sus componentes	
	 * @return JDesktopPane	
	 */
	private JDesktopPane getJDesktop() {
		jDesktop = new JDesktopPane();
		jDesktop.add(getTaLog(), JLayeredPane.MODAL_LAYER);		
		jDesktop.add(getMsg(), JLayeredPane.MODAL_LAYER);
		jDesktop.add(getSend(), JLayeredPane.MODAL_LAYER);
		jDesktop.add(getLstUsers(), JLayeredPane.MODAL_LAYER);
		this.initBoxes();
		this.initFormatBar();
		return jDesktop;
	}
	
	/**
	 * Crea la barra de formato que esta ubicada abajo del textarea del mensaje del usuario
	 */
	private void initFormatBar(){
		logout.addActionListener(this);
		logout.setToolTipText("Cerrar Servidor");
		ImageIcon logoutimg = this.getIconImage("logout.png");
		logout.setIcon(logoutimg);
		logout.setSize(new Dimension(20,20));
		logout.setLocation(new Point(12,490));
		jDesktop.add(logout,JLayeredPane.MODAL_LAYER);
		
		JLabel help = new JLabel("Teclee '\\init mensaje' para cambiar el mensaje de inicio de los clientes");
		help.setSize(new Dimension(400,20));
		help.setLocation(new Point(40,490));
		jDesktop.add(help,JLayeredPane.MODAL_LAYER);
	}
	
	/**
	 * Inicializa los bordes de los inputareas, es decir el layout
	 */
	private void initBoxes(){
		JLabel bg = new JLabel();
		ImageIcon bgimg = this.getIconImage("bg.png");
		bg.setIcon(bgimg);
		bg.setLocation(new Point(0,0));
		bg.setSize(new Dimension(800,547));
		jDesktop.add(bg,JLayeredPane.DEFAULT_LAYER);
		
		//-- Message Box Area
		JLabel msgbox = new JLabel();
		ImageIcon mbimg = this.getIconImage("messagebox.png");
		msgbox.setIcon(mbimg);
		msgbox.setLocation(4,440);
		msgbox.setSize(new Dimension(775,75));
		jDesktop.add(msgbox,JLayeredPane.PALETTE_LAYER);
		
		//-- History Log
		JLabel logbg = new JLabel();
		ImageIcon logimg = this.getIconImage("logbox.png");
		logbg.setIcon(logimg);
		logbg.setLocation(4,4);
		logbg.setSize(new Dimension(600,435));
		jDesktop.add(logbg,JLayeredPane.PALETTE_LAYER);
		
		//-- Usuarios
		JLabel users = new JLabel();
		ImageIcon uimg = this.getIconImage("usersbox.png");
		users.setIcon(uimg);
		users.setSize(new Dimension(174,434));
		users.setLocation(new Point(606,4));
		jDesktop.add(users,JLayeredPane.PALETTE_LAYER);
		
		JLabel contacts = new JLabel();
		ImageIcon cimg = this.getIconImage("contacts.png");
		contacts.setIcon(cimg);
		contacts.setSize(new Dimension(22,22));
		contacts.setLocation(new Point(613,5));
		jDesktop.add(contacts,JLayeredPane.MODAL_LAYER);
		lblNumContacts = new JLabel("Usuarios [0]");
		lblNumContacts.setFont(new Font("Arial",Font.BOLD,12));
		lblNumContacts.setSize(new Dimension(100,20));
		lblNumContacts.setLocation(new Point(640,9));
		jDesktop.add(lblNumContacts,JLayeredPane.MODAL_LAYER);
	}
	
	/**
	 * Regresa la lista de usuarios	
	 * @return JScrollPane Contenedor de los usuarios	
	 */
	private JScrollPane getLstUsers() {
		
		lstUsers = new JList(this.userlist);
		lstUsers.setSize(new Dimension(165,400));
		lstUsers.setFixedCellHeight(20);
		lstUsers.setCellRenderer(new CellRenderer());
		
		JScrollPane usp = new JScrollPane(lstUsers,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		usp.setSize(new Dimension(160,400));
		usp.setLocation(new Point(612,31));
		usp.setOpaque(false);
		usp.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,1));
		
		lstUsers.addMouseListener(new MouseListener(){

			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2){
					int index = lstUsers.locationToIndex(e.getPoint());
				    ListModel dlm = lstUsers.getModel();
				    Object item = dlm.getElementAt(index);
				    lstUsers.ensureIndexIsVisible(index);
				    PrivateFrame frame = new PrivateFrame(server,localhistory,(String)item);
				    if(!ClientFrame.privates.contains((String)item)&&!((String)item).equals("SERVER")){
				    	ClientFrame.privates.addLast((String)item);
				    	frame.setVisible(true);
				    	return;
				    } 
				   
				   
				}
			}

			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent arg0) {}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
        	
        });
		return usp;
	}

	/**
	 * Regresa el textarea con el log de los mensajes, y su estilo predefinido	
	 * @return JScrollPane
	 */
	private JScrollPane getTaLog() {
		taLog = new JTextArea();
		taLog.setEditable(false);
		taLog.setSize(new Dimension(586,420));
		taLog.setLineWrap(true);
		taLog.setFont(new Font("Arial",Font.PLAIN,12));
		
		JScrollPane log = new JScrollPane(taLog,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		log.setSize(new Dimension(586,420));
		log.setLocation(new Point(11,11));
		log.setOpaque(false);
		log.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,1));
		
		
		return log;
	}

	/**
	 * Crea el Scrollpane del area del mensaje
	 * @return JScrollPane	
	 */
	private JScrollPane getMsg() {
		msg = new JTextArea();
		JScrollPane msgsp = new JScrollPane(msg,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		msgsp.setSize(new Dimension(683,40));
		msgsp.setLocation(new Point(11,446));
		msgsp.setOpaque(false);
		msgsp.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,1));
		
		msg.setSize(new Dimension(683,40));
		msg.setLineWrap(true);
		msg.setToolTipText("Escriba su mensaje...");
		msg.setFont(new Font("Arial",Font.PLAIN,12));
		msg.addKeyListener(new KeyListener(){
							//-- Si presiono enter manda el mensaje
							public void keyPressed(KeyEvent arg0) {if(arg0.getKeyCode() == 10){send();}}	
							public void keyReleased(KeyEvent arg0) {if(arg0.getKeyCode() == 10){msg.setText("");}}
							public void keyTyped(KeyEvent arg0) {}
							});
		return msgsp;
	}

	/**
	 *  Regresa el boton de send
	 * @return JButton	
	 */
	private JButton getSend() {
		send = new JButton("Enviar");
		send.addActionListener(this);
		send.setLocation(new Point(700,446));
		send.setSize(new Dimension(70,40));
		
		return send;
	}


	
	/**
	 * Manda el mensaje
	 */
	private void send(){
		String txt = this.msg.getText();
		txt.trim(); //-- Se omiten los ultimos y los primeros espacios blancos que haya dejado
		if(txt.equals("")) {
			return;
		} else if(txt.equals("\\exit")){
			this.dispose();
			System.exit(1);
		} else if(txt.indexOf("\\init ")==0){
			txt = txt.substring(5,txt.length());
			txt = txt.trim();
			Server.initmsg = txt;
			this.displayMessage("Mensaje inicial cambiado \n "+txt);
			
			return;
		} else if(txt.indexOf("\\kick ")==0){
			txt = txt.substring(5,txt.length());
			txt = txt.trim();
			this.server.kick(txt);
			return;
		}
		Message msg = new Message(txt,"SERVER ");	
		Server.history.add(msg);
		ServerThread.sendToAll(msg);
		this.msg.setText("");
		this.msg.requestFocus();
	}
	
	/**
	 * Guarda el historial
	 */
	private void save(){
		JFileChooser fc = new JFileChooser(this.ruta);
		fc.showSaveDialog(this);
		File file = fc.getSelectedFile();
		if(file!=null){
			try {
				String filename = file.getAbsolutePath();
				filename = (filename.indexOf(".txt")!=-1)?filename:filename+".txt";
				PrintWriter fileOut = new PrintWriter(new FileWriter(new File(filename)));
				fileOut.println("*****************************");
				fileOut.println("* Historial de la Sesion   *");
				fileOut.println("* "+this.getDate()+"   *");
				fileOut.println("*****************************");
				fileOut.println();
				String txt[] = taLog.getText().split("\n");
				
				for(int i = 0; i<txt.length; i++){
					fileOut.println(txt[i]);
				}
				fileOut.println();
				fileOut.println("_______________________________________");
				fileOut.println("Revolution Chat : derechos reservados");
				fileOut.close();
			}
			catch (FileNotFoundException fnfe) {
				System.out.println("Archivo no encontrado");
			}
			catch (IOException nsee) {
				System.out.println("Problem with IO");
			}
		}
	}
	
	/**
	 * Despliega el mensaje en el textarea
	 */
	private void displayMessage(final String messageToDisplay) {
	      SwingUtilities.invokeLater(
	         new Runnable() 
	         {
	            public void run() 
	            {
	               taLog.append(messageToDisplay); 
	            } 
	         } 
	      ); 
	}
	
	/**
	 * Clase para darle formato a las celdas de la lista de usuarios
	 * @author Revolution Software Developers
	 */
	private class CellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 1L;

		public CellRenderer(){
			this.setOpaque(true);
		}

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, 
                    value, 
                    index, 
                    isSelected, 
                    cellHasFocus);
			
			super.setIcon(getIconImage("user.png"));
			super.setText((String)value);
	        super.setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(242,242,242)));
			
	        return this;
		}
	}
		
	/**
	 * Regresa el ImageIcon de una imagen especificada
	 * @param filename
	 * @return image
	 */
	public ImageIcon getIconImage(String filename){	
		ImageIcon image = new ImageIcon(this.ruta+"/img/"+filename);
		if(image.getImageLoadStatus()==4) return null;
		return image;
	}
} 