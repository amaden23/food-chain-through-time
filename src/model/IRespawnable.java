package model;

/**
 * Interface that respawnable entities implements such as Animals and Food.
 * Repositioning of entities after being eaten.
 */
public interface IRespawnable {
	/**
	 * Respawns the entitiy at a random empty location on the grid.
	 * @param x New x coordinate to respawn
	 * @param y New y coordinate to respawn
	 */
	void respawn(int x, int y);	
	
}
