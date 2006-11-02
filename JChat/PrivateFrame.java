import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import javax.swing.*;

/** 
 * Clase PrivateFrame.java 
 *
 * @author Revolution Software Developers
 * @package 
 **/

public class PrivateFrame extends JFrame implements WindowListener,ActionListener {

	/**
	 * Estatica de eclipse
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Conexion del cliente
	 */
	private Client connection;
	
	/**
	 * Historial remoto
	 */
	private LinkedList<Message> history = null;
	
	/**
	 * Usuario destino de la ventana
	 */
	private String destinatario = "";
	
	/**
	 * Timer de chequeo de ultimos emnsajes
	 */
	private Timer timer = null;
	
	/**
	 * Temporal de ultimo mensaje
	 */
	private Message lastmessage = null;
	
	/**
	 * Area de historial
	 */
	private JTextArea area = new JTextArea();
	
	/**
	 * Area de mensaje nuevo
	 */
	private JTextArea msg = new JTextArea();
	
	/**
	 * Boton de enviar
	 */
	private JButton send = new JButton("Enviar");
	
	/**
	 * Constructor
	 * @param app Conexion del cliente
	 * @param mainhistory Historial principal
	 * @param destino Usuario destino
	 */
	public PrivateFrame(Client app, LinkedList<Message>mainhistory, String destino) {
		this.connection   = app;
		this.destinatario = destino;
		this.history = mainhistory;
		timer = new Timer(10,new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				if(history!=null && !history.isEmpty()){
					Message aux = (Message)history.getLast();
					if(aux.getTipo() == Message.MENSAJE && aux.getDestinatario().equals(connection.getNickname())){
						if(lastmessage == null){
							lastmessage = aux;
							displayMessage(lastmessage.getUsuario()+ ">> " + lastmessage.getMensaje()+"\n");
						} else if(!lastmessage.equals(aux)){
							lastmessage = aux;
							displayMessage(lastmessage.getUsuario()+ ">> " + lastmessage.getMensaje()+"\n");
						} 
					} else if(aux.getTipo() == Message.COMMAND){
						if(aux.getCommand().type == Command.REMOVE_USER){
							msg.setEnabled(false);
							send.setEnabled(false);
						} else if(aux.getCommand().type == Command.ADD_USER){
							msg.setEnabled(true);
							send.setEnabled(true);
						} else if(aux.getCommand().type == Command.NICK_CHANGE){
							String[] nicks = (String[])aux.getCommand().msg;
							setTitle(nicks[1]);
						}
					}
				}
			}});
		timer.start();
		this.setSize(new Dimension(337,297));
		this.setLocation(new Point(250,230));
		this.setTitle(destino);
		this.setResizable(false);
		this.addWindowListener(this);
		this.initframe();
		this.msg.requestFocus();
	}
	
	public void initframe(){
		this.area.setSize(new Dimension(310,200));
		this.send.setSize(new Dimension(70,45));
		this.send.setLocation(new Point(250,212));
		this.msg.setSize(new Dimension(230,35));
		this.send.addActionListener(this);
		this.msg.setLineWrap(true);
		this.msg.setToolTipText("Escriba su mensaje...");
		this.msg.setFont(new Font("Arial",Font.PLAIN,12));
		this.area.setLineWrap(true);
		this.area.setFont(new Font("Arial",Font.PLAIN,12));
		this.area.setEditable(false);
		this.area.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,1));
		this.msg.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,1));
		this.msg.addKeyListener(new KeyListener(){
			//-- Si presiono enter manda el mensaje
			public void keyPressed(KeyEvent arg0) {if(arg0.getKeyCode() == 10){send();}}	
			public void keyReleased(KeyEvent arg0) {if(arg0.getKeyCode() == 10){msg.setText("");}}
			public void keyTyped(KeyEvent arg0) {}
			});
		JLayeredPane pane = this.getLayeredPane();
		pane.setOpaque(false);
		JScrollPane parea = new JScrollPane(area);
		JScrollPane pmsg  = new JScrollPane(msg);
		parea.setLocation(new Point(0,0));
		parea.setSize(new Dimension(330,220));
		parea.setOpaque(false);
		
		pmsg.setLocation(new Point(0,200));
		pmsg.setSize(new Dimension(250,70));
		pmsg.setOpaque(false);
		
		pane.add(parea);
		pane.add(pmsg);
		pane.add(send);
		
	}
	
	/**
	 * Manda el mensaje
	 */
	public void send(){
		this.displayMessage(this.connection.getNickname()+">> "+msg.getText()+"\n");
		String txt = msg.getText().trim();
		Message m = new Message(txt,this.connection.getNickname(),this.destinatario);	
		this.connection.sendMessage(m);
		msg.setText("");
		this.msg.requestFocus();
	}

	/**
	 * Action performed
	 */
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource().equals(send)){
			this.send();
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
	               area.append(messageToDisplay); 
	            } 
	         } 
	      ); 
	}
	
	/**
	 * Quita el destinatario de la lista de ventanas abiertas
	 */
	public void windowClosing(WindowEvent arg0) {
		ClientFrame.privates.remove(this.destinatario);
	}
	
	
	public void windowOpened(WindowEvent arg0) {}
	public void windowClosed(WindowEvent arg0) {}
	public void windowIconified(WindowEvent arg0) {}
	public void windowDeiconified(WindowEvent arg0) {}
	public void windowActivated(WindowEvent arg0) {}
	public void windowDeactivated(WindowEvent arg0) {}

	
}
