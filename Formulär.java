import javax.swing.*;

public class Formul�r extends JPanel {
	
private JTextField namnf�lt, beskrivningsf�lt;
	
	Formul�r() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JPanel rad1 = new JPanel();
		add(rad1);
		rad1.add(new JLabel("Namn:"));
		namnf�lt = new JTextField(10);
		rad1.add(namnf�lt);
		
		JPanel rad2 = new JPanel();
		add(rad2);
		rad2.add(new JLabel("Beskrivning"));
		beskrivningsf�lt = new JTextField(10);
		rad2.add(beskrivningsf�lt);
	}
	
	public String getNamn() {
		return namnf�lt.getText();
	}
	
	public String getBeskrivning() {
		return beskrivningsf�lt.getText(); 
	}

}
