import java.awt.*;
import javax.swing.*;

public class MarkedPlace extends JComponent implements 
	Comparable <MarkedPlace>{

	public Color color;
	private Position pos = new Position(0, 0);
	
	public MarkedPlace(int x, int y) {
		setBounds(x - 30, y - 50, 1, 1);
		pos.setxV�rde(x); 
		pos.setyV�rde(y);
	}
	
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		
		g.setColor(color); 
		setBounds(getX(), getY(), 61, 51);
		int[] x = {0, 30, 60};
		int[] z = {0, 50, 0};
		g.fillPolygon(x, z, 3);
		g.setColor(Color.red);
		g.drawRect(0, 0, 60, 50);
	}
	
	public int compareTo(MarkedPlace mp) {
		int x = pos.getxV�rde() - mp.pos.getxV�rde();
		if (x != 0)
			return x;
		else
			return pos.getyV�rde() - mp.pos.getyV�rde();
	}
}

/* Den grafiska representationen av platsen, om den inte �r omarkerad. N�r klassen hade boolean-
v�rden blev hela programmet tr�gare om det var f�r m�nga av den. 
*/
