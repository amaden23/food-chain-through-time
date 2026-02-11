package model;

/**
 * Abstract class entity represents all characters on grid (Prey, Predator, ApexPredator, Food).
 * Holds position (x, y) and symbol for characters.
 */
public abstract class Entity {
	protected int x; // x coordinate of entity
	protected int y; // y cordinate of entity
	protected String symbol; // visual of entity (tentative)
	protected String name;
	
	/**
	 * Constructer for entity class
	 * @param x coordinate-X of entity
	 * @param y coordinate-Y of entity
	 * @param symbol String type symbol of entity (tentative) 
	 */
	public Entity(int x, int y, String symbol, String name) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.symbol = symbol;
	}
	
	
	// getter-setters
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public String getSymbol() {
		return symbol;
	}
	
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public String getName() {
		return this.name;
	}
	
	
	
	
}
