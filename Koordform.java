import javax.swing.*;

public class Koordform extends JPanel{
	
	private JTextField xF�lt, yF�lt;
	
	Koordform() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		JPanel textrad = new JPanel();
		add(textrad);
		textrad.add(new JLabel("X:"));
		xF�lt = new JTextField(5);
		textrad.add(xF�lt);
		textrad.add(new JLabel("Y:"));
		yF�lt = new JTextField(5);
		textrad.add(yF�lt);
	}
	
	public int getXV�rde() {
		return Integer.parseInt(xF�lt.getText());
	}
	
	public int getYV�rde() {
		return Integer.parseInt(yF�lt.getText());
	}

}
