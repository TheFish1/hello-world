/* Denna klass inneh�ller all text-data i klassen plats. Anledningen till att den �r separat fr�n
 * dess grafiaka representation p� kartan �r f�r att jag uppt�ckte att en sub-klass till
 * JComponent (som den grafiska platsen �r) med text-data eller boolean v�rden g�r att programmet
 * g�r tr�gare om det �r f�r m�nga av den. 
 */


abstract public class Plats implements Comparable<Plats>{
	
	private String kategori;
	private Position position = new Position(0,0);
	private String namn = "Name";
	
	public Plats(int x, int y) {
		position.setxV�rde(x); 
		position.setyV�rde(y); 
	}
	
	public String getNamn(){
		return namn;
	}
	
	public void setNamn(String s){
		namn = s;
	}
	
	public int getXv�rde() {
		return position.getxV�rde();
	}
	
	public int getYv�rde() {
		return position.getyV�rde();
	}
	
	public String getKategori() {
		return kategori;
	}
	
	public void setKategori(String s) {
		kategori = s;
	}
	
	public int compareTo(Plats p) {
		int x = position.getxV�rde() - p.position.getxV�rde();
		if (x != 0)
			return x;
		else
			return position.getyV�rde() - p.position.getyV�rde();
	}

}

class Described extends Plats {
	
	private String beskrivning = "Description";
	
	public Described(int x, int y) {
		super(x, y);
	}
	
	public String getBesk() {
		return beskrivning;
	}
	
	public void setBesk(String s) {
		beskrivning = s;
	}
}

class Named extends Plats {
	
	public Named(int x, int y) {
		super(x,y);
	}
}

