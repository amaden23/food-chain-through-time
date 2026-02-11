package model;

import java.util.ArrayList;
import java.util.List;
import logic.GameMode;
import util.GameLogger;
import java.security.SecureRandom;

/**
 * For predator type animals.
 */
public class Predator extends Animal {
	
	private int abilityCooldown = 0;
	
	/**
	 * Constructor for Predator class.
	 * @param x Coordinate-X for Predator object
	 * @param y Coordinate-Y for Predator object
	 * @param name Name for Predator object
	 * @param era Current Game Era
	 */
	public Predator(int x, int y, String name, GameMode era) {
		super(x, y, name.substring(0, 1), name, era);
	}
	
	
	/**
	 * Since predator is player-controlled
	 * this only handles cooldown reduction.
	 * @param grid The game grid
	 */
	@Override
	public void makeMove(Grid grid) {
		if (abilityCooldown > 0) {
			abilityCooldown--;
		}
	}
	
	 
	/**
	 * Checks if the given given move is valid.
	 * @param grid The game grid
	 * @param targetX Target X coordinate
	 * @param targetY Target Y coordinate
	 * @return true if the move is valid, false otherwise.
	 */
	public boolean isValidMove(Grid grid, int targetX, int targetY) {
		List<int[]> validMoves = getAvailableMoves(grid);
		for (int[] move : validMoves) {
			if (move[0] == targetX && move[1] == targetY) {
				return true;
			}
		}
		return false;	
	}
	
		
	
	/**
	 * Calculates all possible moves.
	 * @param grid The game grid
	 * @return List of possible moves.
	 */
	public List<int[]> getAvailableMoves(Grid grid) {
		List<int[]> moves = new ArrayList<>();
		
		moves.addAll(getValidMovesInRange(grid, 1));	
		
		if (abilityCooldown == 0) {
			switch (this.era) {
				case PAST:
					addLinearMoves(grid, moves, 2); 
					break;
					
				case PRESENT:
					if (isAdjacentToApex(grid)) {
						moves.addAll(getValidMovesInRange(grid, 2));
					}
					break;
					
				case FUTURE:
					moves.addAll(getValidMovesInRange(grid, 2));
					break;
			}
		}
		return moves;
	}
	
	
	/**
	 * Executes the move to the target coordinates and handles interactions.
	 * @param grid The game grid
	 * @param targetX Target X coordinate
	 * @param targetY Target Y coordinate
	 * @return true if the move was successfully executed
	 */
	public boolean performMove(Grid grid, int targetX, int targetY) {
		if (!isValidMove(grid, targetX, targetY)) {
			System.out.println("Invalid Move!");
			return false;
		}
		
		double distance = Math.sqrt(Math.pow(targetX - getX(), 2) + Math.pow(targetY - getY(), 2));
		boolean isAbilityUsed = distance > 1.5;
		
		Entity targetEntity = grid.getEntity(targetX, targetY);
		
		if (targetEntity instanceof ApexPredator) {
			System.out.println(this.name + " ran into an Apex Predator and DIED!");
			GameLogger.log(this.getName() + " (Player) ran into Apex and DIED!");
			this.changeScoreBy(-1);
			GameLogger.log("Player loses -1 point.");
			((ApexPredator) targetEntity).changeScoreBy(+1);
			GameLogger.log(targetEntity.getName() +" (Apex Predator) gains +3 points." );
			
			
			grid.putEntity(null, this.getX(), this.getY());
			respawnRandomly(grid); 
			return true; 
		}
		
		if (targetEntity instanceof Prey) {
			System.out.println(this.getSymbol() + " ATE " + targetEntity.getSymbol() + "!");
			GameLogger.log(this.name + " (Player) ATE " + targetEntity.getName() + " at (" + targetX + "," + targetY + ")");
			this.changeScoreBy(3);
			GameLogger.log("Player gains +3 points.");
			((Prey) targetEntity).changeScoreBy(-1);
			
			respawnVictimOnGrid(grid, targetEntity);
			
		}
		
		grid.putEntity(null, this.getX(), this.getY());
		this.setX(targetX);
		this.setY(targetY);
		grid.putEntity(this, targetX, targetY);
		 
		if (isAbilityUsed) {
			triggerCooldown();
		}
		
		System.out.println("Predator (Player) moved to (" + targetX + ", " + targetY + ")");
		GameLogger.log(this.name + " (Player/Predator) moved to (" + targetX + ", " + targetY + ")");
		return true;
	}
	
	
	/**
	 * Respawns eaten entity on the grid.
	 * @param grid The game grid.
	 * @param victim The victim to respawn.
	 */
	private void respawnVictimOnGrid(Grid grid, Entity victim) {
		int maxAttempts = 50;
		SecureRandom random = new SecureRandom();
		
		for (int i = 0; i < maxAttempts; i++) {
			int randomX = random.nextInt(grid.getCols());
			int randomY = random.nextInt(grid.getRows());
			
			if (grid.getEntity(randomX, randomY) == null) {
				victim.setX(randomX);
				victim.setY(randomY);
				
				grid.putEntity(victim, randomX, randomY);
				return;
			}
		}
		
	}
	
	/**
	 * Sets the cooldown based on the era.	
	 */
	private void triggerCooldown() { 
		switch (this.era) {
			case PAST:
				this.abilityCooldown = 2;
				break;
			case PRESENT:
				this.abilityCooldown = 0;
				break;
			case FUTURE:
				this.abilityCooldown = 2;
				break;
		}
	} 
	
	
	/////////////////////////// HELPER METHODS FOR MOVEMENT /////////////////////////////
	///
	
	/**
	 * Adds valid linear moves to the list.
	 * Needed for past era ability.
	 * @param grid The game grid
	 * @param moves The list to populate
	 * @param distance The distance to check
	 */
	private void addLinearMoves(Grid grid, List<int[]> moves, int distance) {
		int[][] directions = {{0, distance}, {0, -distance}, {distance, 0}, {-distance, 0}};
		
		for (int[] dir : directions) {
			int newX = getX() + dir[0];
			int newY = getY() + dir[1];
			if (grid.isValidPosition(newX, newY)) {
				
				Entity targetEntity = grid.getEntity(newX, newY);
				if (!(targetEntity instanceof Food)) {
					moves.add(new int[]{newX, newY}); 
				}
				 	
			}
		}
	}
	
	
	/**
	 * Checks if the preadator is currently adjacent to an apex.
	 * Required for Present Era ability.
	 * @param grid The game grid
	 * @return true if an Apex is adjacent.
	 */
	private boolean isAdjacentToApex(Grid grid) {
		int currentX = getX();
		int currentY = getY();
		
		for (int y = currentY - 1; y <= currentY + 1; y++) {
			for (int x = currentX - 1; x <= currentX + 1; x++) {
				
				if (grid.isValidPosition(x, y) && !(x == currentX && y == currentY)) {
					Entity e = grid.getEntity(x, y);
					if (e instanceof ApexPredator) {
						return true;
					}
				}
				
			}
		}
		
		return false;
	}
	
	
	
	/**
	 * Finds all available moves in a given range.
	 * @param grid The game grid
	 * @param range range to search
	 * @return list of valid moves   
	 */
	private List<int[]> getValidMovesInRange(Grid grid, int range) {
		List<int[]> moves = new ArrayList<>();
		int currentX= getX();
		int currentY = getY();
		
		for (int y = currentY - range; y <= currentY + range; y++) {
			for (int x = currentX - range; x <= currentX + range; x++) {
				if (grid.isValidPosition(x, y) && !(x == currentX && y == currentY)) {
					
					Entity targetEntity = grid.getEntity(x, y);	
					if (targetEntity instanceof Food) {
						continue;
					}
					else {
						moves.add(new int[]{x, y});
					} 
					
				}
			}
			 
		}
		return moves;
	}
	
	
	/**
	 * Respawns the preadator at a random empty location.
	 * @param grid The game grid
	 */
	private void respawnRandomly(Grid grid) {
		int maxAttempts = 50;
		SecureRandom random = new SecureRandom();
		
		for (int i = 0; i < maxAttempts; i++) {
			int randomX = random.nextInt(grid.getCols());
			int randomY = random.nextInt(grid.getRows());
			
			if (grid.getEntity(randomX, randomY) == null) {
				this.setX(randomX);
				this.setY(randomY);
				grid.putEntity(this, randomX, randomY);
				System.out.println("Predator RESPAWNED at (" + randomX + ", " + randomY + ")");
				return;
			}
			  
		}
	}
	
	
	
	
	// getter-setters
	
	public int getAbilityCooldown() {
		return abilityCooldown;
	}
	
	public void setCooldown(int rounds) {
		this.abilityCooldown = rounds;
	}
	
	
	
	
}