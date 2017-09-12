/*Till skillnad fr�n f�rra g�ngen: Kartbilden kan inte f�rstoras, s� det blir ingen 
 * f�rvirring med positioner. Nya platser kan bara l�ggas till om det finns en kartbild. 
 * Bara en typ av plats �r markerad �t g�ngen. Vid st�ngning anm�rker programmet om det finns
 * osparade �ndringar. Annan filv�ljare vid sparande av platser. Man kan inte trycka p� "New" mer
 * �n en g�ng vid skapande av platser. Programmet blir inte l�ngre tr�gt vid laddning av m�nga 
 * platser. Feedback vid namns�kning om det inte blir n�gon tr�ff. F�r att kolla om det finns 
 * en plats p� en viss position, anv�nd postion-jlabeln nere i h�gra h�rnet. 
 */


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import javax.swing.event.*; 

public class Kartan extends JFrame {
	
	BildPanel bp = null;
	JScrollPane sp;
	JButton nyPlats;
	JRadioButton r1 = new JRadioButton("Named");
	JRadioButton r2 = new JRadioButton("Described");
	JTextField s�kf�lt;
	
	DefaultListModel<String> listmodell = new DefaultListModel<>();
	JList<String> kategorinamn = new JList<String>(listmodell);
	Map<String,Color> kategorier = new HashMap<>();
	
	JFileChooser kartVal = new JFileChooser(".");
	JFileChooser platsVal = new JFileChooser(".");
	
	NyPlatsKlick npc = new NyPlatsKlick();
	MarkedListener ml = new MarkedListener();
	UnmarkedListener uml = new UnmarkedListener();
	
	Map<Position, Plats> PosOchPlats = new HashMap<>();
	Map<UnmarkedPlace, Plats> unmarked = new TreeMap<>();
	Map<Plats, UnmarkedPlace> getunmarked = new TreeMap<>();
	Map<MarkedPlace, Plats> marked = new TreeMap<>();
	Map<Plats, MarkedPlace> getmarked = new TreeMap<>();
	Map<UnmarkedPlace, MarkedPlace> shift1 = new TreeMap<>();
	Map<MarkedPlace, UnmarkedPlace> shift2 = new TreeMap<>();
	Map<String, ArrayList<Plats>>namne = new HashMap<>();
	Map<String, ArrayList<Plats>> sammaKat = new HashMap<>();
	
	ArrayList<MarkedPlace> targeted = new ArrayList<>();
	
	JLabel position = new JLabel("0,0");
	boolean sparad = true; 
	
/* �ndringar i programmet markeras som osparade om nya platser l�ggs till eller gamla tas bort.
 * Detta f�r att platser sparas �ven om de �r osynliga eller markerade. 
 */
	
	Kartan() {
		setTitle("Kartan");
		JMenuBar bar = new JMenuBar();
		setJMenuBar(bar); 
		JMenu arkiv = new JMenu("Archive");
		bar.add(arkiv);
		JMenuItem nyKarta = new JMenuItem("New Map");
		arkiv.add(nyKarta); 
		nyKarta.addActionListener(new �ppnaKarta());
		JMenuItem taFramPlats = new JMenuItem("Load Places");
		arkiv.add(taFramPlats);
		taFramPlats.addActionListener(new LoadLyss());
		JMenuItem spara = new JMenuItem("Save");
		arkiv.add(spara);
		spara.addActionListener(new SaveLyss());
		JMenuItem l�mna = new JMenuItem("Exit");
		arkiv.add(l�mna);
		l�mna.addActionListener(new ExitLyss());
		
		JPanel norr = new JPanel(); 
		add(norr, BorderLayout.NORTH);
		nyPlats = new JButton("New");
		norr.add(nyPlats);
		nyPlats.addActionListener(new NyPlats());
		JPanel val = new JPanel();
		val.setLayout(new GridLayout(2,1));
		norr.add(val);
		val.add(r1);
		val.add(r2);
		r1.setSelected(true);
		r1.setSelected(true);
		r1.addActionListener(new TypeListener());
		r2.addActionListener(new TypeListener());
		
		s�kf�lt = new JTextField(10);
		norr.add(s�kf�lt);
		JButton s�kKnapp = new JButton("Search");
		norr.add(s�kKnapp);
		s�kKnapp.addActionListener(new S�kLyss());
		JButton g�mKnapp = new JButton("Hide");
		norr.add(g�mKnapp);
		g�mKnapp.addActionListener(new G�mLyss());
		JButton taBortKnapp = new JButton("Remove");
		norr.add(taBortKnapp);
		taBortKnapp.addActionListener(new TaBort());
		JButton koordKnapp = new JButton("Coordinates");
		norr.add(koordKnapp);
		koordKnapp.addActionListener(new KoordS�k());
		
		JPanel �st = new JPanel();
		add(�st, BorderLayout.EAST);
		�st.setLayout(new BoxLayout(�st, BoxLayout.Y_AXIS));
		�st.add(new JLabel("Categories")); 
		kategorinamn.setFixedCellWidth(80);
		�st.add(new JScrollPane(kategorinamn));
		kategorinamn.setVisibleRowCount(8);
		kategorinamn.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listmodell.addElement("Bus");
		kategorier.put("Bus", Color.RED);
		listmodell.addElement("Underground");
		kategorier.put("Underground", Color.BLUE);
		listmodell.addElement("Train");
		kategorier.put("Train", Color.GREEN);
		JButton g�mKat = new JButton("Hide category");
		�st.add(g�mKat); 
		g�mKat.addActionListener(new G�mKat());
		kategorinamn.addListSelectionListener(new ListLyss());
		�st.add(position);
		
		setSize(1000,800);
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
		
/*Om det finns osparade �ndringar m�ste anv�ndaren v�lja om de ska sparas innan programmet st�ngs.
 */
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (!sparad) {
					int svar = JOptionPane.showConfirmDialog(Kartan.this, "Do you wish to save before closing?");
					if (svar == JOptionPane.YES_OPTION){
					Save();
					setDefaultCloseOperation(EXIT_ON_CLOSE);
					}
					else if (svar == JOptionPane.CANCEL_OPTION) {
					setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
					}
					else 
					setDefaultCloseOperation(EXIT_ON_CLOSE);
					}
				
				else 
					setDefaultCloseOperation(EXIT_ON_CLOSE);
			}
		});
		
	}
	
/*Bara en typ av plats kan vara markerad �t g�ngen. Det �r alltid minnst en typ vald. Named �r
 * default-platsen. 
 */
	
	class TypeListener implements ActionListener {
		public void actionPerformed(ActionEvent ave){
			
			JRadioButton jr = (JRadioButton) ave.getSource();
			
			if (jr.equals(r1))
				r2.setSelected(false);
			else if (jr.equals(r2))
				r1.setSelected(false); 
			
			if (!(r1.isSelected()) && !(r2.isSelected()))
					r1.setSelected(true); 
		}
	}
	
	/* Klassen �ppnaKarta implementerar metoden NyKarta().
	 * Kartan �r en ImageIcon som n�s via klassen BildPanel.
	 * Finns det osparade �ndringar f�r man alternativet att
	 * spara dessa. Annars t�ms listorna och kartan tas bort. 
	 * Den �ppnade kartan hamnar i en rullista. 
	 */
	
	class �ppnaKarta implements ActionListener {
		public void actionPerformed(ActionEvent ave) {
			if (!sparad) {
				int svar = JOptionPane.showConfirmDialog(Kartan.this, "Do you wish to save before"
						+ " opening a new map?");
				if (svar == JOptionPane.YES_OPTION){
					Save();
					sparad = true;
				}
				
				else if (svar == JOptionPane.CANCEL_OPTION)
					return;
				
				else {
					if (bp != null)
						bp.removeAll(); 
					
					PosOchPlats.clear();
					unmarked.clear();
					getunmarked.clear();
					marked.clear();
					getunmarked.clear(); 
					shift1.clear();
					shift2.clear();
					targeted.clear();  
					namne.clear();
					sammaKat.clear();  
					
					NyKarta();
					sparad = true;
				}
			}
			else {
				if (bp != null)
					bp.removeAll(); 
				
				PosOchPlats.clear();
				unmarked.clear();
				getunmarked.clear();
				marked.clear();
				getunmarked.clear(); 
				shift1.clear();
				shift2.clear();
				targeted.clear();  
				namne.clear();
				sammaKat.clear();
			NyKarta(); 
			sparad = true;
			}
		}
	}
	
	void NyKarta() {
		int svar = kartVal.showOpenDialog(Kartan.this);
		if (svar != JFileChooser.APPROVE_OPTION){
			return;
		}
		File file = kartVal.getSelectedFile();
		String filnamn = file.getAbsolutePath();
		if (bp != null){
			remove(bp);
			remove(sp);
		}
		bp = new BildPanel(filnamn);
		sp = new JScrollPane(bp);
		add(sp, BorderLayout.CENTER);
		validate();
		repaint();
		bp.setLayout(null); 
		bp.addMouseMotionListener(new PosLyss());
	}
	
	/*Poslyss g�r s� att JLabeln visar vilken position 
	 * musen �r p�. Inget krav i uppgiften, men den g�r
	 * det l�ttare att veta exakt var man klickar f�r att
	 * se om en ny plats intar samma position som en gammal.
	 */
	
	class PosLyss extends MouseAdapter {
		public void mouseMoved(MouseEvent mev) {
			int x = mev.getX();
			int y = mev.getY();
			
			position.setText(x+","+y);
			repaint();
		}
	}
	
	/*NyPlats �ndrar mark�ren till ett kors och implementerar
	 * MusLyss, vilket placerar ut en plats p� kartan, 
	 * precis som det beskrivs i instruktionerna.
	 */
	
	class NyPlats implements ActionListener {
		public void actionPerformed(ActionEvent ave) {
			nyPlats.setEnabled(false); 
			if (bp == null) {
				JOptionPane.showMessageDialog(Kartan.this, "Map Needed.");
				nyPlats.setEnabled(true);
				}
			else {
				bp.addMouseListener(npc);
				bp.setCursor(Cursor.getPredefinedCursor
						(Cursor.CROSSHAIR_CURSOR));
			}
			
		}
	}
	
	/*Knappen "New" kan inte bli tryckt medans anv�ndaren skapar en ny plats. Om det redan finns
	 * en plats p� exakt den position som anv�ndaren v�ljer dyker ett meddelande om det upp och
	 * operationen blir avbryten. Efter detta eller att en plats skapats bli kanppen "New" 
	 * tryckbar igen. 
	 */
	
	class NyPlatsKlick extends MouseAdapter {
		public void mouseClicked(MouseEvent mev) {
			int x = mev.getX();
			int y = mev.getY();
			
			Position pos = new Position(x, y);
			Plats gammal = PosOchPlats.get(pos);
			if (gammal != null) {
				JOptionPane.showMessageDialog(null, "Platsen �r upptagen");
				bp.removeMouseListener(npc); 
				bp.setCursor(Cursor.getDefaultCursor());
				nyPlats.setEnabled(true); 
				return;
			}
			
			Plats plats = null;
			
			if (r1.isSelected()) {
				plats = new Named(x,y);
				String namn = JOptionPane.showInputDialog(null,"Namn?",
						"New Named", JOptionPane.PLAIN_MESSAGE);
				plats.setNamn(namn); 
			}
			else if (r2.isSelected()) {
				plats = new Described(x, y);
				Formul�r form = new Formul�r();
				int svar = JOptionPane.showConfirmDialog
						(null, form, "New Described", JOptionPane.PLAIN_MESSAGE);
				if (svar != JOptionPane.OK_OPTION)
					return;
				String namn = form.getNamn();
				String beskrivning = form.getBeskrivning();
				plats.setNamn(namn); 
				((Described) plats).setBesk(beskrivning); 
			}
			String kategorien = kategorinamn.getSelectedValue();
			Color c = kategorier.get(kategorien);
			plats.setKategori(kategorien); 
			
			MarkedPlace mp = new MarkedPlace(x,y);
			UnmarkedPlace up = new UnmarkedPlace(x,y);
			mp.color = c;
			up.color = c;
			bp.add(up);
			repaint();
			
			bp.removeMouseListener(npc); 
			bp.setCursor(Cursor.getDefaultCursor());
			nyPlats.setEnabled(true); 
			
			PosOchPlats.put(pos, plats);
			unmarked.put(up, plats); 
			marked.put(mp, plats);
			shift1.put(up, mp);
			shift2.put(mp, up);
			getunmarked.put(plats, up);
			getmarked.put(plats, mp);
			
			SammaNamn(plats);
			SammaKat(plats);
			
			up.addMouseListener(uml); 
			sparad = false;
		}
	}
	

	/* N�r varje plats skapas, bildas det listor �ver de 
	 * platser som har samma namn eller kategorier, p� 
	 * samma s�tt som i klassen PersonReg fr�n f15.
	 * Listorna g�r det l�ttare att s�ka efter dem. 
	 */
	
	void SammaNamn(Plats p) {
		String namn = p.getNamn();
		ArrayList<Plats> sammaNamn = namne.get(namn);
		if (sammaNamn == null) {
			sammaNamn = new ArrayList<Plats>();
			namne.put(namn, sammaNamn);
		}
		sammaNamn.add(p);
	}
	
	void SammaKat(Plats p) {
		String kategori = p.getKategori(); 
		ArrayList<Plats> sammaTyp = sammaKat.get(kategori);
		if (sammaTyp == null) {
			sammaTyp = new ArrayList<Plats>();
			sammaKat.put(kategori, sammaTyp); 
		}
		sammaTyp.add(p); 
	}
	
	/*De grafiska representationerna f�r platserna har lyssnarklasser, b�de f�r markerade 
	 * och omarkerade. Vilken av lyssnarklasserna som g�ller skiftas. 
	 */
	
	class UnmarkedListener extends MouseAdapter {
		public void mouseClicked(MouseEvent mev) {
			
			UnmarkedPlace un = (UnmarkedPlace) mev.getSource();
			
			if (SwingUtilities.isLeftMouseButton(mev)) {
				if (un != null) {
				MarkedPlace mp = shift1.get(un);
				bp.remove(un); 
				bp.add(mp);
				repaint();
				
				un.removeMouseListener(this);
				mp.addMouseListener(ml); 
				targeted.add(mp); 
				}
			}
			
			else if (SwingUtilities.isRightMouseButton(mev)) {
				Plats plats = unmarked.get(un);
				if (plats.getClass().getName().equals("Described")){
					JOptionPane.showMessageDialog(Kartan.this, plats.getNamn()+ " " + "{"
							+ plats.getXv�rde()+","+plats.getYv�rde()+"}"+ 
							"\n" + ((Described )plats).getBesk(), "Place Info", JOptionPane.INFORMATION_MESSAGE);
				
				}
				else {
					JOptionPane.showMessageDialog(Kartan.this, plats.getNamn() + " " +"{"
							+ plats.getXv�rde()+","+plats.getYv�rde()+"}" , "Place Info", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
	}
	
	class MarkedListener extends MouseAdapter {
		public void mouseClicked(MouseEvent mev) {
			
			MarkedPlace mp = (MarkedPlace) mev.getSource();
			
			if (SwingUtilities.isLeftMouseButton(mev)) {
				UnmarkedPlace up = shift2.get(mp);
				bp.remove(mp); 
				bp.add(up);
				repaint();
				mp.removeMouseListener(this);
				up.addMouseListener(uml);
				targeted.remove(mp);
			}
			else if (SwingUtilities.isRightMouseButton(mev)) {
				Plats plats = marked.get(mp); 
				if (plats.getClass().getName().equals("Described")){
					JOptionPane.showMessageDialog(Kartan.this, plats.getNamn()+ " " + "{"
							+ plats.getXv�rde()+","+plats.getYv�rde()+"}"+ 
							"\n" + ((Described )plats).getBesk(), "Place Info", JOptionPane.INFORMATION_MESSAGE);
				}
				else {
					JOptionPane.showMessageDialog(Kartan.this, plats.getNamn() + " " +"{"
							+ plats.getXv�rde()+","+plats.getYv�rde()+"}" , "Place Info", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
	}
	
	/* Vid s�kning blir alla markerade platser f�rst 
	 * avmarkerade. Sedan blir de som matchar namnet 
	 * �termarkerade och synliga. Om det inte finns 
	 * en plats med det s�kta namnet f�r anv�ndaren 
	 * en anm�rkning om det.
	 */
	
	class S�kLyss implements ActionListener {
		public void actionPerformed(ActionEvent ave) {
			
			for (MarkedPlace mp: targeted) {
				UnmarkedPlace up = shift2.get(mp);
				mp.removeMouseListener(ml);
				bp.remove(mp);
				bp.add(up);
				up.addMouseListener(uml); 
				repaint();
			}
			
			targeted.clear(); 
			
			String namn = s�kf�lt.getText();
			ArrayList<Plats> sammaNamn = namne.get(namn);
			if (sammaNamn != null) 
			{
				for (Plats p: sammaNamn) {
					UnmarkedPlace up = getunmarked.get(p);
					MarkedPlace mp = getmarked.get(p);
					if (!mp.isVisible()) {
						mp.setVisible(true); 
					}
					if (!up.isVisible()) {
						up.setVisible(true); 
					}
					up.removeMouseListener(uml);
					bp.remove(up);
					bp.add(mp);
					mp.addMouseListener(ml); 
					repaint();
					targeted.add(mp);
				}
			}
			else {
				JOptionPane.showMessageDialog(Kartan.this, "No place with that name");
			}
			
		}
	}
	
	//Alla platser i listan f�r markerade blir osynliga 
	
	class G�mLyss implements ActionListener {
		public void actionPerformed(ActionEvent ave) {
			for (MarkedPlace mp: targeted) {
				mp.setVisible(false); 
				UnmarkedPlace up = shift2.get(mp);
				up.setVisible(false);
			}
		}
	}
	
	/*Anv�ndaren s�ker efter en plats genom att mata in dess
	 * position i ett formul�r (klassen Koordform). Samtliga
	 * platser blir avmarkerade. Den plats vars position matchar
	 * formul�rets blir markerad och synlig. Ett meddelande
	 * dyker upp om ingen position i platslistan matchar.
	 * Endast integers godtas, annars felmeddelande. 
	 * Markering och avmarkering sker p� samma s�tt som vid 
	 * s�kning via namn. 
	 */
	
	class KoordS�k implements ActionListener {
		public void actionPerformed(ActionEvent ave) {
			Koordform kofm = new Koordform();
			int svar = JOptionPane.showConfirmDialog
					(Kartan.this, kofm,"Input coordinates:", JOptionPane.PLAIN_MESSAGE);
			if (svar != JOptionPane.OK_OPTION)
				return;
			
			try {
				int x = kofm.getXV�rde();
				int y = kofm.getYV�rde();
				
				Position s�kpos = new Position(x, y);
				Plats s�kplats = PosOchPlats.get(s�kpos);
				
				if (s�kplats == null)
					JOptionPane.showMessageDialog(null, "Nothing at that position");
				else
				{

					for (MarkedPlace mp: targeted) {
						UnmarkedPlace up = shift2.get(mp);
						mp.removeMouseListener(ml);
						bp.remove(mp);
						bp.add(up);
						up.addMouseListener(uml); 
						repaint();
					}
					
					targeted.clear(); 
					
					UnmarkedPlace up = getunmarked.get(s�kplats);
					MarkedPlace mp = getmarked.get(s�kplats);
					if (!mp.isVisible()) {
						mp.setVisible(true); 
					}
					if (!up.isVisible()) {
						up.setVisible(true); 
					}
					up.removeMouseListener(uml);
					bp.remove(up);
					bp.add(mp);
					mp.addMouseListener(ml); 
					repaint();
					targeted.add(mp);
				}
			
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Round numbers only");
			}
		}
	}
	
	/*Varje plats vars kategori matchar den i listan blir
	 * dold. 
	 */
	
	class G�mKat implements ActionListener {
		public void actionPerformed(ActionEvent ave) {
			String kategori = kategorinamn.getSelectedValue();
			ArrayList<Plats> sammaTyp = sammaKat.get(kategori);
			if (sammaTyp != null) {
				for (Plats p: sammaTyp) {
					MarkedPlace mp = getmarked.get(p);
					UnmarkedPlace up = getunmarked.get(p);
					mp.setVisible(false);
					up.setVisible(false); 
					repaint(); 
				}
			}
		}
	}
	
	/*Platser vars kategori som matchar den valda i listan
	 * blir synliga, vare sig de �r markerade eller inte. 
	 * Om kategorin i listan redan �r vald (t.ex. om man nyss 
	 * g�mda alla platser med den kategorin)f�r man f�rst v�lja 
	 * en annan kategori i listan och sen klicka p� den kategori 
	 * som h�r till de platser man vill ska bli synliga. 
	 */
	
	class ListLyss implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent lse) {
			String kategori = kategorinamn.getSelectedValue();
			ArrayList<Plats> sammaTyp = sammaKat.get(kategori);
			if (sammaTyp != null) {
				for (Plats p: sammaTyp) {
					MarkedPlace mp = getmarked.get(p);
					UnmarkedPlace up = getunmarked.get(p);
					mp.setVisible(true);
					up.setVisible(true);
					repaint(); 
				}
			}
			
		}
	}
	
	/*Markerade platser och deras grafiska representationer blir borttagna, f�rst fr�n
	 * alla datasamlingar de finns i och sedan fr�n kartan. D�refter t�ms listan f�r 
	 * markerade. 
	 */
	
	class TaBort implements ActionListener {
		public void actionPerformed(ActionEvent ave) {
			
			for (MarkedPlace mp: targeted) {
				Plats p = marked.get(mp);
				if (p!= null) {
					Position pos = new Position(p.getXv�rde(), p.getYv�rde());
					String namn = p.getNamn(); 
					String pkat = p.getKategori();
					UnmarkedPlace up = shift2.get(mp);
					unmarked.remove(up);
					getunmarked.remove(p);
					marked.remove(mp);
					getmarked.remove(p);
					shift1.remove(up);
					shift2.remove(mp);
					PosOchPlats.remove(pos);
					bp.remove(mp);
					bp.remove(up);
					repaint();
					ArrayList<Plats> sammaNamn = namne.get(namn);
					sammaNamn.remove(p);
					if (sammaNamn.isEmpty())
						namne.remove(namn);
					
					ArrayList<Plats> sammaTyp = sammaKat.get(pkat);
					sammaTyp.remove(p);
					if (sammaTyp.isEmpty())
						sammaKat.remove(pkat); 
				}
			}
			targeted.clear();
			sparad = false;
		}
	}
	
	/*SaveLyss implementerar metoden Save(). Man f�r en 
	 * bekr�ftelse p� att man har sparat. 
	 */
	
	class SaveLyss implements ActionListener {
		public void actionPerformed(ActionEvent ave) {
			if (bp == null) {
				JOptionPane.showMessageDialog(Kartan.this, "Map needed");
				return; 
			}
			
			Save();
			sparad = true; 
		}
	}
	
	 /*Save tar variablerna fr�n alla text-platser och sparar 
	 * dem som rader av text. Antingen kan man spara i en 
	 * befintlig fil eller skapa en ny genom att skriva
	 * ett namn t.ex. "Save.txt". 
	 */
	
	void Save() {
		int svar = platsVal.showSaveDialog(Kartan.this);
		if (svar == JFileChooser.APPROVE_OPTION) {
			File f = platsVal.getSelectedFile();
			String filnamn = f.getAbsolutePath();
			
			try  {
				FileWriter utfil = new FileWriter(filnamn);
				PrintWriter out = new PrintWriter(utfil);
				for (Plats p: PosOchPlats.values()) {
					String kat = p.getKategori();
					if (kat == null)
						kat = "None";
					if (p.getClass().getName().equals("Named")) {
						out.println(p.getClass().getName()+","+
					kat+","+p.getXv�rde()+","+p.getYv�rde()
					+","+p.getNamn()); 
					}
					else if (p.getClass().getName().equals("Described")){
						out.println(p.getClass().getName()+","+
								kat+","+p.getXv�rde()+","+p.getYv�rde()
								+","+p.getNamn()+","+((Described)p).getBesk());  
					}
				}
				
				out.close();
				utfil.close();
				JOptionPane.showMessageDialog(Kartan.this, "Saved");
				
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(Kartan.this, "Unable to open file");
			} catch (IOException e) {
				JOptionPane.showMessageDialog(Kartan.this, e.getMessage());
			}
		}
	}
	
	/*LoadLyss implementerar metoden Load(). Om det finns
	 * osparade �ndringar f�r man alternativet att spara dem,
	 * annars tas de bort fr�n kartan och listorna. 
	 */
	
	class LoadLyss implements ActionListener {
		public void actionPerformed(ActionEvent ave) {
			
			if (bp == null) {
				JOptionPane.showMessageDialog(Kartan.this, "Map needed");
				return; 
			}
			
			if (!sparad) {
				int svar = JOptionPane.showConfirmDialog(Kartan.this, "Do you wish to save before loading?"); 
				if (svar == JOptionPane.YES_OPTION){
					Save();
					sparad = true;
				}
				else if (svar == JOptionPane.CANCEL_OPTION)
					return;
				else {
					if (bp != null)
						bp.removeAll(); 
					
					PosOchPlats.clear();
					unmarked.clear();
					getunmarked.clear();
					marked.clear();
					getunmarked.clear(); 
					shift1.clear();
					shift2.clear();
					targeted.clear();  
					namne.clear();
					sammaKat.clear();   
					
				}
			}
			if (!PosOchPlats.isEmpty()) {
			
			if (bp != null)
				bp.removeAll(); 
			
			PosOchPlats.clear();
			unmarked.clear();
			getunmarked.clear();
			marked.clear();
			getunmarked.clear(); 
			shift1.clear();
			shift2.clear();
			targeted.clear();  
			namne.clear();
			sammaKat.clear();   
			
			}
			Load();  
			sparad = true;
			
		}
	}
	

	/*Load h�mtar rader av str�ngar fr�n en textfil. Dessa
	 * omvandlas till variabler till klassen Plats. Samma 
	 * procedur som vid skapandet av nya platser upprepas. 
	 */
	
	void Load() {
		Map<String, ArrayList<String>> typer = new TreeMap<>();
		ArrayList<String> named = new ArrayList<>();
		ArrayList<String> described = new ArrayList<>();
		typer.put("Named", named);
		typer.put("Described", described); 
		
		int svar = platsVal.showOpenDialog(Kartan.this);
		if (svar == JFileChooser.APPROVE_OPTION) {
			File f = platsVal.getSelectedFile();
			String filnamn = f.getAbsolutePath();
			
			try {
				FileReader infil =new FileReader(filnamn);
				BufferedReader in = new BufferedReader(infil);
				String line;
				while ( (line = in.readLine()) != null){
					String[] tokens = line.split(",");
					String typ = tokens[0];
					ArrayList<String> typsort = typer.get(typ);
					typsort.add(line);
			}
				in.close();
				
				for (String s: named) {
					String[] ntokens = s.split(",");
					int x = Integer.parseInt(ntokens[2]);
					int y = Integer.parseInt(ntokens[3]);
					Plats p = new Named(x,y);
					p.setNamn(ntokens[4]);
					p.setKategori(ntokens[1]);
					UnmarkedPlace up = new UnmarkedPlace(x,y);
					MarkedPlace mp = new MarkedPlace(x,y);
					up.color = kategorier.get(ntokens[1]);
					mp.color = kategorier.get(ntokens[1]);
					bp.add(up);
					bp.repaint();
					PosOchPlats.put(new Position(x,y), p);
					unmarked.put(up, p);
					marked.put(mp, p);
					shift1.put(up, mp);
					shift2.put(mp, up);
					getunmarked.put(p, up);
					getmarked.put(p, mp);
					SammaNamn(p);
					SammaKat(p);
					
					up.addMouseListener(uml); 
				}
				
				for (String s: described) {
					String[] dtokens = s.split(",");
					int x = Integer.parseInt(dtokens[2]);
					int y = Integer.parseInt(dtokens[3]);
					Plats p = new Described(x,y);
					p.setNamn(dtokens[4]); 
					((Described)p).setBesk(dtokens[5]);  
					p.setKategori(dtokens[1]);
					UnmarkedPlace up = new UnmarkedPlace(x,y);
					MarkedPlace mp = new MarkedPlace(x,y);
					up.color = kategorier.get(dtokens[1]);
					mp.color = kategorier.get(dtokens[1]);
					bp.add(up);
					bp.repaint();
					PosOchPlats.put(new Position(x,y), p);
					unmarked.put(up, p);
					marked.put(mp, p);
					shift1.put(up, mp);
					shift2.put(mp, up);
					getunmarked.put(p, up);
					getmarked.put(p, mp);
					SammaNamn(p);
					SammaKat(p);
					
					up.addMouseListener(uml); 
				}
				
				named.clear();
				described.clear(); 
				typer.clear(); 
				
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(Kartan.this, "Unable to open file");
			} catch (IOException e) {
				JOptionPane.showMessageDialog(Kartan.this, e.getMessage());
			}
		}
	}
	
	/*ExitLyss avslutat programmet. Om det finns osparade
	 * �ndringar f�r man alternativet att spara dem. Annars
	 * f�rsvinner allt n�r man sparar. 
	 */

	class ExitLyss implements ActionListener {
		public void actionPerformed(ActionEvent ave) {
			if (!sparad) {
				int svar = JOptionPane.showConfirmDialog(Kartan.this, "Do you wish to save before leaving?");
				if (svar == JOptionPane.CANCEL_OPTION)
					return;
				else if (svar == JOptionPane.YES_OPTION){
					Save();
					System.exit(0);
				}
				else
					System.exit(0);
			}
			System.exit(0);
		}
	}
	
	public static void main(String[] args) {
		new Kartan();
	}

}
