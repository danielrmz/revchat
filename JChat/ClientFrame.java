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

public class ClientFrame extends JFrame implements ActionListener {
	/**
	 * Constante de Eclipse
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Conexion del Cliente
	 */
	public Client app;
	
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
	 * Menu item sobre
	 */
	private JMenuItem sobre = null;
	
	/**
	 * Linkedlist de la historia local.
	 */
	private LinkedList localhistory = new LinkedList();
	
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
	 * This Constructor por default
	 */
	public ClientFrame() {
		super("Revolution Chat");
		this.initialize();
	}
	
	private final String ruta = (new File ("").getAbsolutePath());

	/**
	 * Acciones del Frame
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(cerrar)){
			this.close("");
			System.exit(1);
		} else if(e.getSource().equals(send)){
			this.send();
		} else if(e.getSource().equals(conectar)){
			ConfigFrame frame = new ConfigFrame(this);
			frame.setVisible(true);
			if(this.app != null && this.app.getStatus()){
				this.taLog.setText("");
			}
		} else if(e.getSource().equals(desconectar) || e.getSource().equals(logout)){
			this.close("> Has cerrado la sesion  \n");
		} else if(e.getSource().equals(sobre)){
			AboutFrame frame = new AboutFrame();
			frame.setVisible(true);
		} else if(e.getSource().equals(guardar)){
			this.save();
		}
	}
	
	/**
	 * Regresa un arreglo ordenado de objetos
	 * @param usuarios
	 * @return
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
	
	//-- Empiezan bloques de construccion y manejo de la GUI
	
	/**
	 * Inicializador de Componentes
	 * @return void
	 */
	private void initialize() {
		this.setSize(788, 570);
		this.setResizable(false);
		this.setContentPane(getJDesktop());
		this.setJMenuBar(getJMenubar());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		timer = new Timer(100,new ActionListener(){
			/**
			 * Metodo usado dentro del timer para actualizar el screen cada cierto tiempo
			 * comparando el historial local contra el del servidor 
			 */
			public void actionPerformed(ActionEvent arg0) {
				if(app != null && app.getStatus()){
					LinkedList diffs = app.compare(localhistory);
					while(!diffs.isEmpty() && !app.getNickname().equals("")){
						Message mensaje = (Message)diffs.removeFirst();
						localhistory.addLast(mensaje);
						if(mensaje.getTipo() == Message.MENSAJE && !mensaje.getUsuario().equals("SERVER")){
							displayMessage(mensaje.getUsuario()+ ">> " + mensaje.getMensaje()+"\n");
						} else if(mensaje.getTipo() == Message.MENSAJE && mensaje.getUsuario().equals("SERVER")){
							displayMessage(mensaje.getMensaje()+"\n\n");
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
	    }
	}
	
	/**
	 * Parsea el comando recibido de acuerdo a un filtro
	 * @param cmd
	 */
	private void parseCommand(Command cmd){
		switch(cmd.type){
		case Command.NICK_CHANGE:
			String nicks[] = (String[])cmd.msg;
			this.userlist.removeElement(nicks[0]);
			this.userlist.addElement(nicks[1]);
			this.displayMessage("> El usuario [ "+nicks[0]+" ] ha cambiado su nickname a [ "+nicks[1]+" ]\n");
			break;
		case Command.ADD_USER:
			String user = (String)cmd.msg;
			if(!this.userlist.isEmpty()){
				//-- Busca la posicion ordenada donde insertarlo
				int j = 0;
				while(j<userlist.getSize()&&((String)userlist.getElementAt(j)).hashCode()<user.hashCode()){
					j++;
				}
				this.userlist.add(j-1,user);
			} else {
				this.userlist.addElement(user);
			}
			this.lblNumContacts.setText("Usuarios ["+this.userlist.getSize()+"]");
			this.displayMessage("> El usuario "+user+" entro en la sala.\n");
			break;
		case Command.REMOVE_USER:
			String user_ = (String)cmd.msg;
			this.userlist.removeElement(user_);
			if(!user_.equals("")){
				this.displayMessage("> El usuario "+user_+" se salio de la sala.\n");
			}
			this.lblNumContacts.setText("Usuarios ["+this.userlist.getSize()+"]");
			
			break;
		case Command.CLOSE_CONNECTION:
			this.close("\n> El server fue cerrado "+this.getDate()+"\n ");
			
			break;
		
		case Command.FETCH_USERS:
			Object[] usuarios = this.sort((LinkedList)cmd.msg);
			
			for(int i=0;i<usuarios.length; i++){
				if(!this.userlist.contains(usuarios[i])){
					this.userlist.addElement(usuarios[i]);
				}
			}
			
			this.lblNumContacts.setText("Usuarios ["+this.userlist.size()+"]");
			break;
		}
	}
	
	/**
	 * Trae la fecha del sistema
	 * @return
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
		JMenu ayuda = new JMenu("Ayuda");
		
		conectar = new JMenuItem("Conectar");
		conectar.addActionListener(this);
		desconectar = new JMenuItem("Desconectar");
		desconectar.addActionListener(this);
		desconectar.setVisible(false);
		guardar = new JMenuItem("Guardar");
		guardar.addActionListener(this);
		sobre = new JMenuItem("Sobre");
		sobre.addActionListener(this);
		cerrar = new JMenuItem("Cerrar");
		cerrar.addActionListener(this);
		
		archivo.add(conectar);
		archivo.add(desconectar);
		archivo.addSeparator();
		archivo.add(guardar);
		archivo.addSeparator();
		archivo.add(cerrar);
		
		ayuda.add(sobre);
		
		menubar.add(archivo);
		menubar.add(ayuda);
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
	
	private void initFormatBar(){
		
		logout.addActionListener(this);
		logout.setToolTipText("Terminar Sesion");
		logout.setEnabled(false);
		ImageIcon logoutimg = this.getIconImage("logout.png");
		logout.setIcon(logoutimg);
		logout.setSize(new Dimension(20,20));
		logout.setLocation(new Point(12,490));
		jDesktop.add(logout,JLayeredPane.MODAL_LAYER);
		
		JLabel help = new JLabel("Teclee '\\nick nombre' para cambiar su nickname, y \\exit para salir de la aplicación");
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
		msg.setEnabled(false);
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
		send.setEnabled(false);
		
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
		} else if(txt.indexOf("\\nick")==0){
			String nickname = txt.substring(5,txt.length());
			nickname = nickname.trim();
			String success = this.app.setNickname(nickname);
			if(success.equals("")) { ConfigFrame.setNickname(nickname);}
			return;
		}
		Message msg = new Message(txt,ConfigFrame.getNickname());	
		this.app.sendMessage(msg);
		this.msg.setText("");
		this.msg.requestFocus();
	}
	
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
				fileOut.println("* Historial de la Sesin    *");
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
	 * Reestablece los valores para cuando se cierra una conexion
	 */
	private void close(String msg){
		if(this.app == null || !this.app.getStatus()) {
			return;
		}
		this.app.closeConnection();
		this.conectar.setVisible(true);
		this.desconectar.setVisible(false);
		this.send.setEnabled(false);
		this.msg.setEnabled(false);
		this.msg.setText("");
		this.logout.setEnabled(false);
		this.app.localhistory.clear();
		this.localhistory.clear();
		this.displayMessage(msg);
		this.lblNumContacts.setText("Usuarios [0]");
		this.userlist.removeAllElements();
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