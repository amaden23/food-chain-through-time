package model;
/**
 * 2D game board.
 * All animals and food are on this grid.
 * Can be 10x10 15x15 20x20
 */
public class Grid {
	private Entity[][] map; // elemanları animal, food veya null olur.	
	private int rows; // y-ekseni
	private int cols; // x-ekseni
	
	/**
	 * Constructs a grid with given size.
	 * @param rows Height of grid
	 * @param cols Width of grid
	 */
	public Grid(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		this.map = new Entity[rows][cols]; 
	}
	
	/**
	 * Checks if given position is valid.
	 * @param x Coordinate-X
	 * @param y Coordinate-Y
	 * @return
	 */
	public boolean isValidPosition(int x, int y) {
		boolean notNegative = (x >= 0 && y >= 0);
		boolean inBoundary = (x < cols && y < rows);
		return notNegative && inBoundary;
	}
	
	
	/**
	 * Puts given entity in given position
	 * Also sets entity's position
	 * @param e The entity object to place
	 * @param x Coordinate-X to place such entity
	 * @param y Coordinate-Y to place such entity
	 */
	public void putEntity(Entity e, int x, int y) {
		if (isValidPosition(x, y)) {
			map[y][x] = e;
			if (e != null) {
				e.setPosition(x, y);
			}
		}
	}
	
	/**
	 * Removes the entity in given position and sets it to null
	 * @param x Coordinate-X of entity to be removed
	 * @param y Coordinate-Y of entity to be removed 
	 */
	public void removeEntity(int x, int y) {
		if (isValidPosition(x, y)) {
			map[y][x] = null;
		}
	}
	
	/**
	 * Getter for entities in the grid.
	 * @param x Coordinate-X of such entity
	 * @param y Coordinate-Y of such entity
	 * @return
	 */
	public Entity getEntity(int x, int y) {
		if (isValidPosition(x, y)) {
			return map[y][x];
		}
		else {
			return null;
		}
	}
	
	/**
	 * Cheks if given position is empty.
	 * @param x Coordinate-X to be checked
	 * @param y  Coordinate-Y to be checked
	 * @return
	 */
	public boolean isEmpty(int x, int y) {
		if (isValidPosition(x, y)) {
			if (map[y][x] == null) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	
	
	// basic getter-setters

	public int getRows() {
		return rows;
	}

	public int getCols() {
		return cols;
	}
	
	public void setRows(int rows) {
		this.rows = rows;
	}
	
	public void setCols(int cols) {
		this.cols = cols;
	}
		
	
}










