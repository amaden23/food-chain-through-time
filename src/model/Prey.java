package model;

import java.util.ArrayList;
import java.util.List;

import logic.GameMode;
import util.GameLogger;
import java.security.SecureRandom;
/**
 * For prey type animals.
 */
public class Prey extends Animal {
	
	private int abilityCooldown = 0; 
	
	/**
	 * Constructer for Prey class.
	 * @param x Coordinate-X for Prey object
	 * @param y Coordinate-Y for Prey object
	 * @param name Name for Prey object
	 */
	public Prey(int x, int y, String name, GameMode era) {
		super(x, y, name.substring(0, 1), name, era);
		// sembol olarak şimdilik baş harfini kullanıyoruz. name.substring(0, 1) yaptık o yüzden.
	}
	
	/**
	 * Main move method for prey.
	 * Prey uses special ability if available.
	 * If not does standard move.
	 */
	@Override
	public void makeMove(Grid grid) {
		
		List<Entity> threats = findEntities(grid, Predator.class, ApexPredator.class);
		List<Entity> foods = findEntities(grid, Food.class);
		
		Entity closestThreat = getClosest(threats);
		Entity closestFood = getClosest(foods);
		
		if (this.abilityCooldown == 0) {
			boolean usedAbility = tryUseSpecialAbility(grid, closestThreat, closestFood);
			if (usedAbility) {
				setCooldownBasedOnEra();
				return;
			}
		}
		
		makeStandardMove(grid, closestThreat, closestFood);
				
	}
	
	/**
	 * Tries to perform a special move based on the current era.
	 * @param grid Grid that prey moves on
	 * @param threat Predator and Apex Predators
	 * @param food Foods
	 * @return true if uses specieal ability.
	 */
	private boolean tryUseSpecialAbility(Grid grid, Entity threat, Entity food) {
		List<int[]> candidates = new ArrayList<>();
		boolean canEatFood = true;
		int whichEra = 0;
		
		switch (this.era) {
			case PAST:
				candidates = getDiagonalMoves(grid);
				whichEra = 1; 
				break;
			case PRESENT:
				candidates = getValidMoves(grid, 2);
				whichEra = 2;
				break;
			case FUTURE:
				// 3 kare zıpla ama yemek yiyemez
				whichEra = 3;
				List<int[]> moves = getValidMoves(grid, 3);
				for (int[] move : moves) {
					int dx = Math.abs(move[0] - getX());
					int dy = Math.abs(move[1] - getY());
					
					if (dx == 3 || dy == 3) {
						candidates.add(move);
					}
				}
				canEatFood = false;
				break;
		}
		
		if (candidates.isEmpty()) {
			return false;
		}
		
		int[] bestMove = pickBestMove(candidates, threat, food, canEatFood);
		
		if (bestMove != null) {
			moveTo(grid, bestMove[0], bestMove[1]);
			
			if (whichEra == 1) {
				System.out.println("Using past specieal move");
			}
			else if (whichEra == 2) {
				System.out.println("Using present specieal move");
			}
			else if (whichEra == 3) {
				System.out.println("Using future specieal move");
			}
			return true;
		}
		return false;	
		
	}
	
	/**
	 * Applies standart move for prey.
	 * @param grid Grid that prey moves on
	 * @param threat Threat to run from
	 * @param food Food
	 */
	private void makeStandardMove(Grid grid, Entity threat, Entity food) {
		List<int[]> candidates = getValidMoves(grid, 1);
		candidates.add(new int[]{getX(), getY()});
		
		int[] bestMove = pickBestMove(candidates, threat, food, true);
		
		if (bestMove != null) {
			if (bestMove[0] == getX() && bestMove[1] == getY()) {
				if (this.abilityCooldown > 0) {
					this.abilityCooldown--;
				}
				GameLogger.log(this.getName() + " (Prey) STAYED STILL at (" + this.getX() + "," + this.getY() + ")");
			}
			else {
				moveTo(grid, bestMove[0], bestMove[1]);
			}		
		}	
		
	}
	
	/**
	 * Compares all candidate cells and picks the best.
	 * AI logic for prey.
	 * Uses a scoring system to choose the best move.
	 * @param candidates candidate cells. 
	 * @param threat Threat to run from
	 * @param food Food
	 * @param canEat Tells if prey can eat such entity
	 * @return best move
	 */
	private int[] pickBestMove(List<int[]> candidates, Entity threat, Entity food, boolean canEat) {
		int[] bestMove = null;
		double bestScore = -Double.MAX_VALUE;
		
		// sürekli kaçmasını engellemek icin 
		// böylece food'lara da yönelebilir.
		double panicDistance = 3.0;
		
		for (int[] move : candidates) {
			double score = 0;
			int targetX = move[0];
			int targetY = move[1];
			
			if (threat != null) {
				double distToThreat = getDistance(targetX, targetY, threat.getX(), threat.getY());
				
				if (distToThreat < panicDistance) {
					score += distToThreat * 20.0;
				}
				else {
					score += distToThreat * 0.1;
				}
				
			}
					
			
			if (food != null && canEat) {
				double distToFood = getDistance(targetX, targetY, food.getX(), food.getY());
				score -= distToFood * 6.0;
			} 
			
			
			if (score > bestScore) {
				bestScore = score;
				bestMove = move;
			}
		}
		
		return bestMove;		
		
	}
	
	
	 
	/**
	 * Sets cooldown times based on era.
	 */
	private void setCooldownBasedOnEra() {
		switch (this.era) {
			case PAST:
				setCooldown(2);
				break;
			case PRESENT:
				setCooldown(3);
				break;
			case FUTURE:
				setCooldown(2);
				break;		
		}
	}
	
	
	///////////////////////// HELPER METHODS ////////////////////
	/// 
	
	/**
	 * Finds desired typed entities on the grid.
	 * @param grid The game grid
	 * @param types Types to find
	 * @return List of entities found
	 */
	@SafeVarargs
	private final List<Entity> findEntities(Grid grid, Class<? extends Entity>... types){
		List<Entity> list = new ArrayList<>();
		
		for (int y = 0; y < grid.getRows(); y++) {
			for (int x = 0; x < grid.getCols(); x++) {
				Entity e = grid.getEntity(x, y);
				if (e != null) {
					for (Class<? extends Entity> type : types) {
						if (type.isInstance(e)) {
							list.add(e);
							break;
							
						}
					}
				}
			}
		}
		return list;
	}
	
	/**
	 * Finds the closest entity in he given list.
	 * @param entities List of entities
	 * @return Returns the closest
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
	 * Finds empty cells and foods in a given range.
	 * @param grid The game grid
	 * @param range The range to search
	 * @return Rturns list of empty cells and foods
	 */
	private List<int[]> getValidMoves(Grid grid, int range){
		List<int[]> moves = new ArrayList<>();
		int currentX = getX();
		int currentY = getY();
		
		for (int y = currentY - range; y <= currentY + range; y++) {
			for (int x = currentX - range; x <= currentX + range; x++) {
				
				if (!grid.isValidPosition(x, y) || (x == currentX && y == currentY)) {
					continue;
				}
				 
				Entity target = grid.getEntity(x, y);
				if (target == null || target instanceof Food) {
					moves.add(new int[]{x, y});
				}
				
			}
		}
		return moves;
	}
	
	/**
	 * Helper method for past era ability. 
	 * @param grid The game grid
	 * @return Returns possible diagonal moves.
	 */
	private List<int[]> getDiagonalMoves(Grid grid){
		List<int[]> moves = new ArrayList<>();
		int currentX = getX();
		int currentY = getY();
		
		int[][] directions = { {1, 1}, {1, -1}, {-1, 1}, {-1, -1} };
		
		for (int[] dir : directions) {
			int newX = currentX + dir[0];
			int newY = currentY + dir[1];
			
			if (grid.isValidPosition(newX, newY)) {
				Entity target = grid.getEntity(newX, newY);
				if (target == null || target instanceof Food) {
					moves.add(new int[]{newX, newY});
				}
			}
		}
		return moves;
	}
	
	
	/**
	 * Moves Prey to given position.
	 * @param grid The game grid
	 * @param newX new x position
	 * @param newY new y position
	 */
	private void moveTo(Grid grid, int newX, int newY) {
		Entity target = grid.getEntity(newX, newY);
		
		if (target instanceof Food) {
			this.changeScoreBy(3);
			System.out.println(this.getSymbol() + " ATE FOOD!");
			GameLogger.log(this.getName() + " (Prey) ATE FOOD at (" + newX + "," + newY + ")");   
			
			int[] spawnPoint = getRandomEmptyCell(grid);
			if (spawnPoint != null) {
				((Food) target).respawn(spawnPoint[0], spawnPoint[1]);
				grid.putEntity(target, spawnPoint[0], spawnPoint[1]);
			}
			
			
		}
		
		if (this.abilityCooldown > 0) {
			this.abilityCooldown--;
		} 
		
		grid.putEntity(null, this.getX(), this.getY());
		this.setX(newX);
		this.setY(newY);
		grid.putEntity(this, newX, newY);
		
		System.out.println(this.getSymbol() + " (Prey AI) moved to (" + newX + ", " + newY + ")");
		GameLogger.log(this.getName() + " (Prey) moved to (" + newX + ", " + newY + ")");
		
		
	}
	
	
	/**
	 * Helper to find a random empty cell on the grid.
	 * @param grid The game grid
	 * @return coordinates of empty cell or null
	 */
	private int[] getRandomEmptyCell(Grid grid) {
		int maxAttempts = 50;
		int cols = grid.getCols();
		int rows = grid.getRows();  
		
		SecureRandom random = new SecureRandom();
		
		for (int i = 0; i < maxAttempts; i++) {
			int randomX = random.nextInt(cols);
			int randomY = random.nextInt(rows);
			
			if (grid.getEntity(randomX, randomY) == null) {
				return new int[] {randomX, randomY};
			}
			
		}
		return null;
	}
	
	
	/**
	 * gives distance between given 2 points.
	 * @param x1 x1
	 * @param y1 y1
	 * @param x2 x2
	 * @param y2 y2 
	 * @return
	 */
	private double getDistance(int x1, int y1, int x2, int y2) {	
		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}
	
	
	// getter-setters
	
	public int getAbilityCooldown() {
		return abilityCooldown;
	}
	
	public void setCooldown(int raunds) {
		this.abilityCooldown = raunds;
	}
	
	
	
}
