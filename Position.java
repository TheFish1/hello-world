
public class Position implements Comparable<Position>{
	private int xV�rde;
	private int yV�rde;
	
	public Position(int xV�rde, int yV�rde){
		this.xV�rde = xV�rde;
		this.yV�rde = yV�rde;
	}

	public int getxV�rde() {
		return xV�rde;
	}

	public void setxV�rde(int xV�rde) {
		this.xV�rde = xV�rde;
	}

	public int getyV�rde() {
		return yV�rde;
	}

	public void setyV�rde(int yV�rde) {
		this.yV�rde = yV�rde;
	}
	
	public boolean equals(Object other) {
		if (other instanceof Position) {
			Position p = (Position) other;
				return xV�rde == p.xV�rde && yV�rde == p.yV�rde;
		}
		else
			return false;
	}
	
	public int hashCode() {
		return xV�rde*10000 + yV�rde;
	}
	
	public int compareTo(Position other) {
		int x = xV�rde - other.xV�rde;
		if (x != 0)
			return x;
		else 
			return yV�rde - other.yV�rde;
	}

}
