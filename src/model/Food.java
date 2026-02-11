package model;
/**
 * Represents a food object on the grid.
 * Food is stationary and respawns when it is eaten.
 */
public class Food extends Entity implements IRespawnable {
	private boolean isAlive;
	/**
	 * Construter for a Food object
	 * @param x coordinate-X of Foof object
	 * @param y coordinate-Y of Food object
	 */
	public Food(int x, int y, String name) {
		super(x, y, "F", name);
		this.isAlive = true;
	}
	
	
	@Override
	public void respawn(int x, int y) {
		this.isAlive = true;
		this.setX(x);
		this.setY(y);
	}
	
}
