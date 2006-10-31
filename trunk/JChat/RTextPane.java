import javax.swing.*;
import java.awt.*;
import javax.swing.text.*;
/** 
 * Clase Prueba.java 
 *
 * @author Revolution Software Developers
 * @package 
 **/

public class RTextPane extends JTextPane {
	public static final int NORMAL = 0;
	public static final int BOLD = 1;
	public static final int ITALIC = 2;
	public static final int UNDERLINE = 4;
	
	//-- 1+2 = bold + italic = 3
	//-- 1+4 = bold + underline = 5
	//-- 2+4 = italic + underline = 6 
	
	private int style = 0;
	private static final long serialVersionUID = 1L;
	
	public RTextPane() {
		super();
	//	bold.setAction(new StyledEditorKit.UnderlineAction());

	}

	public void append(String text){
		this.style = 0;
		this.add(text,this.getStyle());
	}
	
	public void append(String text, int style){
		this.style = style;
		this.add(text,this.getStyle());
	}
	
	public void append(String text, int style, Color forecolor){
		this.style = style;
		SimpleAttributeSet s = this.getStyle();
		this.addColor(s,forecolor);
		this.add(text,s);
	}
	
	public void append(String text, int style, Color forecolor, Color background){
		this.style = style;
		SimpleAttributeSet s = this.getStyle();
		this.addColor(s,forecolor);
		this.addBgColor(s,background);
		this.add(text,s);
	}
	
	public void append(String text, Color forecolor){
		this.style = 0;
		SimpleAttributeSet s = this.getStyle();
		this.addColor(s,forecolor);
		this.add(text,s);
	}
	
	public void append(String text, Color forecolor, Color background){
		this.style = 0;
		SimpleAttributeSet s = this.getStyle();
		this.addColor(s,forecolor);
		this.addBgColor(s,background);
		this.add(text,s);
	}
	
	private void add(String text, SimpleAttributeSet style){
		try {
			this.getDocument().insertString(this.getDocument().getLength(),text,style);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	public void addBgColor(SimpleAttributeSet style, Color bgcolor){
		StyleConstants.setBackground(style,bgcolor);
	}
	
	public void addColor(SimpleAttributeSet style, Color fcolor){
		StyleConstants.setForeground(style,fcolor);
	}
	
	public SimpleAttributeSet getStyle(){
		SimpleAttributeSet attribute = new SimpleAttributeSet();
		switch(style){
		case 1:  //-- Bold
			StyleConstants.setBold(attribute, true);
			break; 
		case 2:  //-- Italic 
			StyleConstants.setItalic(attribute, true);
			break;
		case 3:  //-- Bold + Italic 
			StyleConstants.setBold(attribute, true);
			StyleConstants.setItalic(attribute, true);
			break;
		case 4:  //-- Underline
			StyleConstants.setUnderline(attribute,true);
			
			break;
		case 5:  //-- Bold + Underline 
			StyleConstants.setBold(attribute, true);
			StyleConstants.setUnderline(attribute,true);
			
			break;
		case 6:  //-- Italic + underline
			StyleConstants.setItalic(attribute, true);
			StyleConstants.setUnderline(attribute,true);
			
			break;
		case 7:  //-- Bold + Italic + Underline
			StyleConstants.setBold(attribute, true);
			StyleConstants.setItalic(attribute, true);
			StyleConstants.setUnderline(attribute,true);
			break;
		default:
			StyleConstants.setBold(attribute, false);
			StyleConstants.setItalic(attribute, false);
			StyleConstants.setUnderline(attribute,false);
			break;
		}
		return attribute;
	}
}
