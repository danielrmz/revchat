import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;

import javax.swing.*;

/** 
 * Clase ConfigFrame.java 
 * 
 * @author Revolution Software Developers
 **/

public class ConfigFrame extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private static String nickname = "";
	
	private static String hostip   = "";
	
	private JButton conectar = new JButton("Conectar");
	
	private JButton cancelar = new JButton("Cancelar");
	
	private JTextField nick = new JTextField(ConfigFrame.getNickname().equals("")?"Anonimo":ConfigFrame.getNickname());
	
	private JTextField server = new JTextField(ConfigFrame.getHostip());
	
	private JLabel error = new JLabel("Nickname esta siendo usado");
	
	public ConfigFrame() {
		this.setSize(new Dimension(217,130));
		this.setLocation(new Point(250,230));
		this.setTitle("Iniciar...");
		this.setResizable(false);
		this.setModal(true);
		
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
			
			if(!nickname.equals("") && !hostip.equals("")){
				boolean registered = true;
				if(ClientFrame.app == null){
					ClientFrame.setClient(hostip);
					registered = ClientFrame.app.setNickname(nickname);
					this.server.setEnabled(false);
				} else { 
					//ClientFrame.app.setServerip(hostip);
					registered = ClientFrame.app.setNickname(nickname);
				}
				
				if(!registered){
					error.setVisible(true);
					return;
				}
				
				Command list = ((Message)ClientFrame.app.getUsers()).getCommand();
				
				LinkedList aux = (LinkedList)list.msg;
				Object[] x = new Object[aux.size()];
				ClientFrame.lstUsers.removeAll();
				int i = 0;
				while(!aux.isEmpty()){
					x[i] = (String)aux.removeFirst();
					System.out.println((String)x[i]);
					//ClientFrame.lstUsers.setListData(aux.toArray());
					i++;
				}
				//TODO: pasar esto a la parte donde checa los mensajes
				ClientFrame.conectar.setText("Desconectar");
			}
			
			ConfigFrame.setHostip(hostip);
			ConfigFrame.setNickname(nickname);
			this.dispose();
		}
	}

}
