import javax.swing.*;

public class Koordform extends JPanel{
	
	private JTextField xFält, yFält;
	
	Koordform() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		JPanel textrad = new JPanel();
		add(textrad);
		textrad.add(new JLabel("X:"));
		xFält = new JTextField(5);
		textrad.add(xFält);
		textrad.add(new JLabel("Y:"));
		yFält = new JTextField(5);
		textrad.add(yFält);
	}
	
	public int getXVärde() {
		return Integer.parseInt(xFält.getText());
	}
	
	public int getYVärde() {
		return Integer.parseInt(yFält.getText());
	}

}
