package model;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import logic.GameMode;
import util.GameLogger;
/**
 * For apex predator type animals.
 */
public class ApexPredator extends Animal {
	
	private int abilityCooldown = 0;
	
	/**
	 * Constructor for apex predator class.
	 * @param x Coordinate-X for apex predator
	 * @param y Coordinate-Y for apex predator
	 * @param name Name of the animal
	 * @param era Current era
	 */
	public ApexPredator(int x, int y, String name, GameMode era) {
		super(x, y, name.substring(0, 1), name, era);
	}
	
	/**
	 * Main movement method for apex predator.
	 * Finds the nearest target and moves towards it.
	 * @param grid The game grid
	 */
	@Override
	public void makeMove(Grid grid) {
		List<Entity> targets = findTargets(grid);
		Entity closestTarget = getClosest(targets);
		
		
		boolean abilityUsed = false;
		if (abilityCooldown == 0 && closestTarget != null) {
			abilityUsed = tryUseSpecialAbility(grid, closestTarget);
		}
		
		if (!abilityUsed) {
			if (abilityCooldown > 0) {
				abilityCooldown--;
			}
			makeStandardMove(grid, closestTarget);
		}
	}
	
	
	/**
	 * Tries to perform a special move based on the current era.
	 * @param grid The game grid
	 * @param target The target entity to hunt
	 * @return true if a special ability move was performed
	 */
	private boolean tryUseSpecialAbility(Grid grid, Entity target) {
		List<int[]> candidates = new ArrayList<>();
		int cooldownCost = 0;
		
		switch (this.era) {
			case PAST:
				candidates = getLinearMoves(grid, 2);
				cooldownCost = 2;
				break;
			case PRESENT:
				candidates = getValidMovesInRange(grid, 3);
				cooldownCost = 3;
				break;
			case FUTURE:
				List<int[]> potentialMoves = getValidMovesInRange(grid, 3);
				
				for (int[] move : potentialMoves) {
					if (getDistance(getX(), getY(), move[0], move[1]) <= 3.0) {
						candidates.add(move);
					}
				}	
				cooldownCost = 3;
				break;
		}
		
		int[] bestMove = pickBestMove(candidates, target);
		
		if (bestMove != null) {
			moveTo(grid, bestMove[0], bestMove[1]);
			this.abilityCooldown = cooldownCost;
			return true;
		}
		return false;
	}
	
	
	/**
	 * Performs a standard 1-cell movement towards the target.
	 * @param grid The game grid
	 * @param target The target entity
	 */
	private void makeStandardMove(Grid grid, Entity target) {
		List<int[]> candidates = getValidMovesInRange(grid, 1);
		int[] bestMove = pickBestMove(candidates, target);
		
		if (bestMove != null) {
			moveTo(grid, bestMove[0], bestMove[1]);
		}
	}
	
	/**
	 * Evaluates candidate moves and selects the one closest to the target.
	 * @param candidates List of possible moves
	 * @param target The target entity
	 * @return The best move 
	 */
	private int[] pickBestMove(List<int[]> candidates, Entity target) {
		int[] bestMove = null;
		double minDistance = Double.MAX_VALUE;
		
		for (int[] move : candidates) {
			double dist = 0;
			
			if (target != null) {
				dist = getDistance(move[0], move[1], target.getX(), target.getY());
			} 
			
			if (dist < minDistance) {
				minDistance = dist;
				bestMove = move;
			}
		}
		return bestMove;
	}
	
	
	
	/**
	 * Executes the move and handles eating logic.
	 * Updates the position of apex.
	 * Also calls respawnVictimOnGrid() method to respawn eaten animal
	 * @param grid The game grid
	 * @param newX New X coordinate
	 * @param newY New Y coordinate
	 */
	private void moveTo(Grid grid, int newX, int newY) {
		Entity target = grid.getEntity(newX, newY);
		
		if (target instanceof Prey || target instanceof Predator) {
			this.changeScoreBy(+1);
			System.out.println(this.getSymbol() + " ATE " + target.getSymbol() + "!");
			GameLogger.log(this.name + " (Apex) ATE " + target.getName() + " at (" + newX + "," + newY + ")");
			
			if (target instanceof Prey) {
				((Prey) target).changeScoreBy(-1);
			} 
			
			else if (target instanceof Predator) {
				((Predator) target).changeScoreBy(-1);
				GameLogger.log("Player loses -1 point.");  
				
			}
			
			respawnVictimOnGrid(grid, target);
		}
		
		grid.putEntity(null, this.getX(), this.getY());
		this.setX(newX);
		this.setY(newY);
		grid.putEntity(this, newX, newY);
		
		System.out.println(this.getSymbol() + " (Apex AI) moved to (" + newX + ", " + newY + ")");
		GameLogger.log( this.name + " (Apex) moved to (" + newX + ", " + newY + ")");
		
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
	 * Searches for prey and predator and returns in a list.	
	 * @param grid The game grid
	 * @return List of potential targets
	 */
	private List<Entity> findTargets(Grid grid) {
		List<Entity> targets = new ArrayList<>();  
		
		for (int y = 0; y < grid.getRows(); y++) {
			for (int x = 0; x < grid.getCols(); x++) {
				
				Entity e = grid.getEntity(x, y);
				if (e instanceof Prey || e instanceof Predator) {
					targets.add(e);
				}
			}
		}
		
		return targets;
	}
	
	
	/**
	 * Finds the closest entity from a list.
	 * @param entities List of entities
	 * @return The closest entity
	 */
	private Entity getClosest(List<Entity> entities) {
		Entity closest = null;
		double minDistance = Double.MAX_VALUE;
		for (Entity e : entities) {
			double distance = getDistance(this.getX(), this.getY(), e.getX(), e.getY());
			if (distance < minDistance) {
				minDistance = distance;
				closest = e;
			}
		}
		return closest;
	}
	
	
	/**
	 * Finds all available moves in a given range.
	 * Used for standard move and present/future abilities.
	 * @param grid The game grid
	 * @param range The range to search
	 * @return List of valid move coordinates
	 */
	private List<int[]> getValidMovesInRange(Grid grid, int range) {
		List<int[]> moves = new ArrayList<>();
		int currentX = getX();
		int currentY = getY(); 
		
		for (int y = currentY - range; y <= currentY + range; y++) {
			for (int x = currentX - range; x <= currentX + range; x++) {
				if (x == currentX && y == currentY) continue;
				
				if (grid.isValidPosition(x, y)) {
					Entity e = grid.getEntity(x, y);
					if (e == null || e instanceof Prey || e instanceof Predator) {
						moves.add(new int[]{x, y});
					}
				}
			}
		}
		return moves;	
		
	}
	 
	/**
	 * Gives valid straight line moves.
	 * Used for past era ability.
	 * @param grid The game grid
	 * @param distance Reachable distance
	 * @return List of valid move coordinates
	 */
	private List<int[]> getLinearMoves(Grid grid, int distance) {
		List<int[]> moves = new ArrayList<>();
		int[][] directions = {{0, distance}, {0, -distance}, {distance, 0}, {-distance, 0}
							, {distance, distance}, {-distance, distance}, {distance, -distance}, {-distance, -distance}};
		
		for (int[] dir : directions) {
			int newX = getX() + dir[0];
			int newY = getY() + dir[1];
			
			if (grid.isValidPosition(newX, newY)) {
				Entity e = grid.getEntity(newX, newY);
				if (e == null || e instanceof Prey || e instanceof Predator) {
					moves.add(new int[]{newX, newY});
				}
			}
		}
		return moves;
	}
	
		
	
	/**
	 * Calculates distance between two points.
	 * @param x1 First X
	 * @param y1 First Y
	 * @param x2 Second X
	 * @param y2 Second Y
	 * @return Distance value
	 */
	private double getDistance(int x1, int y1, int x2, int y2) {
		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}
	
	
	// getter-setters
	public int getAbilityCooldown() { 
		return abilityCooldown;
	}
	
	public void setCooldown(int rounds) {
		this.abilityCooldown = rounds;
	}
	
}