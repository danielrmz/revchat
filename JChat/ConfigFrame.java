import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/** 
 * Clase ConfigFrame.java 
 * Registra el usuario en el servidor, y crea la conexion.
 * 
 * @author Revolution Software Developers
 **/

public class ConfigFrame extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private static String nickname = "";
	
	private static String hostip   = "";
	
	private JButton conectar = new JButton("Conectar");
	
	private JButton cancelar = new JButton("Cancelar");
	
	private JTextField nick = new JTextField(ConfigFrame.getNickname().equals("")?"":ConfigFrame.getNickname());
	
	private JTextField server = new JTextField(ConfigFrame.getHostip());
	
	private JLabel error = new JLabel("");
	
	private ClientFrame frame = null;
	
	public ConfigFrame(ClientFrame frame) {
		this.setSize(new Dimension(217,130));
		this.setLocation(new Point(250,230));
		this.setTitle("Iniciar...");
		this.setResizable(false);
		this.setModal(true);
		this.frame = frame;
		
		JLayeredPane principal = this.getLayeredPane();
		JLabel lblNick = new JLabel("Nickname ");
		JLabel lblHost = new JLabel("Servidor ");
		error.setForeground(Color.RED);
		
		conectar.addActionListener(this);
		cancelar.addActionListener(this);
		
		conectar.setSize(new Dimension(90,20));
		cancelar.setSize(new Dimension(90,20));
		lblNick.setSize(new Dimension(85,20));
		lblHost.setSize(new Dimension(80,20));
		nick.setSize(new Dimension(130,20));
		server.setSize(new Dimension(130,20));
		error.setSize(new Dimension(200,20));
		
		lblNick.setLocation(new Point(10,10));
		lblHost.setLocation(new Point(10,30));
		nick.setLocation(new Point(70,10));
		server.setLocation(new Point(70,30));
		conectar.setLocation(new Point(10,70));
		cancelar.setLocation(new Point(110,70));
		error.setLocation(new Point(25,48));
		error.setVisible(false);
		
		principal.add(error);
		principal.add(lblNick);
		principal.add(lblHost);
		principal.add(nick);
		principal.add(server);
		principal.add(conectar);
		principal.add(cancelar);
	}
	
	public static String getNickname(){
		return ConfigFrame.nickname;
	}
	
	public static String getHostip(){
		return ConfigFrame.hostip;
	}
	
	public static void setHostip(String hostip){
		ConfigFrame.hostip = hostip;
	}
	
	public static void setNickname(String nick){
		ConfigFrame.nickname = nick;
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(cancelar)){
			this.dispose();
		} else if(e.getSource().equals(conectar)){
			
			String hostip   = server.getText();
			String nickname = nick.getText();
			//-- Si no estan vacios los campos, se conecta
			if(!nickname.equals("") && !hostip.equals("")){
				//-- Inicializa el cliente.
				if(this.frame.app == null){
					this.frame.app = new Client(hostip);
					this.server.setEnabled(false);
				} else {
					this.frame.app.closeConnection();
					this.frame.app = new Client(hostip);
				}
				
				//-- Intenta conectarse con el servidor
				if(!this.frame.app.runClient()){
					error.setForeground(Color.RED);
					error.setText("     Servidor no disponible");
					error.setVisible(true);
					this.server.setEnabled(true);
					return;
				} 
				
				//-- Intenta registrar el nick, si ya esta ocupado
				//-- regresa mensaje de error
				String registered = this.frame.app.setNickname(nickname);
				if(!registered.equals("")){
					error.setForeground(Color.RED);
					error.setText(registered);
					error.setVisible(true);
					if(registered.equals("Servidor no disponible (timeout)")){
						server.setEnabled(true);
					}
					return;
				}
				
				this.frame.app.sendMessage(new Message(new Command(Command.FETCH_USERS),nickname));
				//-- Reconfigura la pantalla principal para habilitar el chat
				this.frame.conectar.setVisible(false);
				this.frame.desconectar.setVisible(true);
				this.frame.send.setEnabled(true);
				this.frame.msg.setEnabled(true);
				this.frame.logout.setEnabled(true);
			}
			
			//-- Guarda las variables para que si se desconecta al conectarse
			//-- Continue con su nick y hostip
			ConfigFrame.setHostip(hostip);
			ConfigFrame.setNickname(nickname);
			this.dispose();
		} else {
			return;
		}
	}

}
