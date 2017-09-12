import java.awt.*;
import javax.swing.*;

public class UnmarkedPlace extends JComponent implements 
	Comparable <UnmarkedPlace> {
	
	public Color color;
	private Position pos = new Position(0, 0);
	
	public UnmarkedPlace(int x, int y) {
		setBounds(x - 30, y - 50, 1, 1);
		pos.setxVärde(x); 
		pos.setyVärde(y);
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g); 
		g.setColor(color); 
		setBounds(getX(), getY(), 61, 51);
		int[] x = {0, 30, 60};
		int[] z = {0, 50, 0};
		g.fillPolygon(x, z, 3);
	}
	
	public int compareTo(UnmarkedPlace up) {
		int x = pos.getxVärde() - up.pos.getxVärde();
		if (x != 0)
			return x;
		else
			return pos.getyVärde() - up.pos.getyVärde();
	}

}

/* Den grafiska representationen av platsen, om den inte är markerad. När klassen hade boolean-
värden blev hela programmet trögare om det var för många av den. 
*/