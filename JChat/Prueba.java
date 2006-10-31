import javax.swing.*;
import javax.swing.text.StyledEditorKit;

import java.awt.*;
import java.awt.event.*;

/** 
 * Clase Prueba.java 
 *
 * @author Revolution Software Developers
 * @package 
 **/

public class Prueba extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private RTextPane area = new RTextPane();
	private JButton bold = new JButton("bold");
	private JButton send = new JButton("Mandar");
	
	public Prueba() {
		this.setSize(788, 570);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());
		this.add(area, BorderLayout.CENTER);
		this.add(bold, BorderLayout.SOUTH);
		this.add(send, BorderLayout.WEST);
		
		send.addActionListener(this);
		bold.setAction(new StyledEditorKit.BoldAction());
	}

	public static void main(String[] args) {
		Prueba frame = new Prueba();
		frame.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(send)){
			area.append(area.getText(),0);
		}
	}

}
