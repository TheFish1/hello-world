/* Denna klass innehåller all text-data i klassen plats. Anledningen till att den är separat från
 * dess grafiaka representation på kartan är för att jag upptäckte att en sub-klass till
 * JComponent (som den grafiska platsen är) med text-data eller boolean värden gör att programmet
 * går trögare om det är för många av den. 
 */


abstract public class Plats implements Comparable<Plats>{
	
	private String kategori;
	private Position position = new Position(0,0);
	private String namn = "Name";
	
	public Plats(int x, int y) {
		position.setxVärde(x); 
		position.setyVärde(y); 
	}
	
	public String getNamn(){
		return namn;
	}
	
	public void setNamn(String s){
		namn = s;
	}
	
	public int getXvärde() {
		return position.getxVärde();
	}
	
	public int getYvärde() {
		return position.getyVärde();
	}
	
	public String getKategori() {
		return kategori;
	}
	
	public void setKategori(String s) {
		kategori = s;
	}
	
	public int compareTo(Plats p) {
		int x = position.getxVärde() - p.position.getxVärde();
		if (x != 0)
			return x;
		else
			return position.getyVärde() - p.position.getyVärde();
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

