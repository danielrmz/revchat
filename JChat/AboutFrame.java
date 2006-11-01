import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;

/**
 * Sobre el Juego
 * @author Revolution Software Developers
 */
public class AboutFrame extends JDialog implements ActionListener {

	/**
	 * Constante de Eclipse
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public AboutFrame() {
		//-- Preferencias de la pantalla
		this.setTitle("Sobre el Revolution Chat...");
		this.getContentPane().setLayout(new BorderLayout());
		this.setResizable(false);
		this.setSize(420,250);
		this.setLocation(new Point(200,200));
		this.setModal(true);
		
		ImageIcon img = this.getIconImage("banner.png");
		JLabel top = new JLabel(img);
		top.setSize(415,70);
		this.add(top,BorderLayout.NORTH);
		
		JPanel centro = new JPanel(new BorderLayout());
		ImageIcon java = this.getIconImage("java.gif");
		
		JLabel j = new JLabel(java);
		JPanel left = new JPanel(new BorderLayout());
		JPanel auxleft = new JPanel(new BorderLayout());
		
		j.setSize(new Dimension(34,63));
		auxleft.add(j,BorderLayout.SOUTH);
		left.add(new JLabel("    "),BorderLayout.WEST);
		left.add(new JLabel("  "),BorderLayout.EAST);
		
		left.add(auxleft,BorderLayout.CENTER);
		
		centro.add(left,BorderLayout.WEST);
		centro.add(new JLabel(" "),BorderLayout.NORTH);
		JPanel contenido = new JPanel(new GridLayout(6,1));
		contenido.add(new JLabel("Revsoft ® Revolution Chat "));
		contenido.add(new JLabel("Versión 1.0"));
		contenido.add(new JLabel("Derechos Reservados 2006  "));
		contenido.add(new JLabel("Revolution Software Developers"));
		JPanel auxcontenido = new JPanel(new BorderLayout());
		auxcontenido.add(contenido,BorderLayout.CENTER);
		auxcontenido.add(new JLabel("   "),BorderLayout.WEST);
		centro.add(auxcontenido,BorderLayout.CENTER);
		JPanel auxcerrar = new JPanel(new FlowLayout());
		JButton cerrar = new JButton("Cerrar");
		cerrar.addActionListener(this);
		contenido.add(auxcerrar);
		auxcerrar.add(cerrar);
		centro.add(auxcerrar,BorderLayout.SOUTH);
		this.add(centro,BorderLayout.CENTER);
	}
	

	
	/**
	 * Para cerrar la pantalla
	 */
	public void actionPerformed(ActionEvent arg0) {
		this.dispose();
	}
	
	/**
	 * Regresa el ImageIcon de una imagen especificada
	 * @param filename
	 * @return image
	 */
	public ImageIcon getIconImage(String filename){	
		ImageIcon image = new ImageIcon((new File ("").getAbsolutePath())+"/img/"+filename);
		if(image.getImageLoadStatus()==4) return null;
		return image;
	}

}
