import javax.swing.*;

public class Formulär extends JPanel {
	
private JTextField namnfält, beskrivningsfält;
	
	Formulär() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JPanel rad1 = new JPanel();
		add(rad1);
		rad1.add(new JLabel("Namn:"));
		namnfält = new JTextField(10);
		rad1.add(namnfält);
		
		JPanel rad2 = new JPanel();
		add(rad2);
		rad2.add(new JLabel("Beskrivning"));
		beskrivningsfält = new JTextField(10);
		rad2.add(beskrivningsfält);
	}
	
	public String getNamn() {
		return namnfält.getText();
	}
	
	public String getBeskrivning() {
		return beskrivningsfält.getText(); 
	}

}
