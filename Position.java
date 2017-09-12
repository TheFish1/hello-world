
public class Position implements Comparable<Position>{
	private int xVärde;
	private int yVärde;
	
	public Position(int xVärde, int yVärde){
		this.xVärde = xVärde;
		this.yVärde = yVärde;
	}

	public int getxVärde() {
		return xVärde;
	}

	public void setxVärde(int xVärde) {
		this.xVärde = xVärde;
	}

	public int getyVärde() {
		return yVärde;
	}

	public void setyVärde(int yVärde) {
		this.yVärde = yVärde;
	}
	
	public boolean equals(Object other) {
		if (other instanceof Position) {
			Position p = (Position) other;
				return xVärde == p.xVärde && yVärde == p.yVärde;
		}
		else
			return false;
	}
	
	public int hashCode() {
		return xVärde*10000 + yVärde;
	}
	
	public int compareTo(Position other) {
		int x = xVärde - other.xVärde;
		if (x != 0)
			return x;
		else 
			return yVärde - other.yVärde;
	}

}
