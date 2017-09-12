import javax.swing.*;
import java.awt.*;

public class BildPanel extends JPanel {
	
	private ImageIcon bild;
	public BildPanel(String filnamn){
		bild = new ImageIcon(filnamn);
		int w = bild.getIconWidth();
		int h = bild.getIconHeight();
		Dimension d = new Dimension(w, h);
		setPreferredSize(d);
		setMaximumSize(d);
		setMinimumSize(d);
	
	}
	protected void paintComponent(Graphics g){
		super.paintComponent(g); 
		g.drawImage(bild.getImage(), 0, 0, getWidth(), getHeight(), this);
	}

}
