package logic;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import exception.GameLoadException;
import model.Animal;
import model.ApexPredator;
import model.Entity;
import model.Food;
import model.Grid;
import model.IRespawnable;
import model.Predator;
import model.Prey;
import util.FileManager;
import util.GameLogger;

/**
 * The Game Engine that connects model with logic.
 * Handles creating/locating/moving entities and game execution.
 */

public class GameEngine {
	private SecureRandom random;
	private Grid grid;
	private GameMode currentMode;
	private List<Animal> animals;
	private boolean isRunning;
	
	private int currentRaund = 1;
	private int totalRaunds = 10;
	private boolean isGameOver = false;
	
	
	/**
	 * Constructs a new GameEngine with the specified game settings.
	 * Calls startGame() method to start game with taken parameters.
	 * @param eraName Name of the are to be played.
	 * @param gridSize Size of the grid (gridSize x gridSize)
	 * @param rounds Number of rounds to be played
	 */
	public GameEngine(String eraName, int gridSize, int rounds) {
		this.grid = new Grid(gridSize, gridSize);
		this.totalRaunds = rounds;
		this.currentRaund = 1;
		
		if (eraName.equalsIgnoreCase("Past")) {
			this.currentMode = GameMode.PAST;
		}
		else if (eraName.equalsIgnoreCase("Future")) {
			this.currentMode = GameMode.FUTURE;
		}
		else {
			this.currentMode = GameMode.PRESENT;	
		}		
		
		this.isRunning = true;
		this.animals = new ArrayList<>();
		this.random = new SecureRandom();
		
		startGame();
		
	}
	
	/**
	 * Places entity at a random empty slot.
	 * @param entity The entity object to be placed randomly.
	 */
	private void placeEntityRandomly(Entity entity) {
		int x = random.nextInt(grid.getCols());
		int y = random.nextInt(grid.getRows());
		
		while (!grid.isEmpty(x, y)) {
			x = random.nextInt(grid.getCols());
			y = random.nextInt(grid.getRows());
		}
		
		grid.putEntity(entity, x, y);
		System.out.println("Placed " + entity.getSymbol() + " at (" + x + ", " + y + ")");		
	}
	
	/**
	 * Places animal randomly using "placeEntityRandomly()" method.
	 * Adds animal to the list animals.
	 * @param animal Animal to be placed
	 */
	private void placeAnimal(Animal animal) {
		placeEntityRandomly(animal);
		animals.add(animal);
	}
	
	
	
	// !!!!!!!!!!!!!!!!!! BU METHOD DEĞİŞECEK UNUTMA (halloldu)!!!!!!!!!!!!!!!!!!
	/**
	 * Creates entity objects to fill game grid.
	 * Calls movePreyBot() method and initializes the game.
	 */
	public void startGame() { 
		System.out.println("Initializing Game in mode: " + this.currentMode);
		System.out.println("Grid Size: " + grid.getRows() + "x" + grid.getCols());
		System.out.println("Total Rounds: " + totalRaunds);
		
		// Test için konsolda yazdırılacak entity'ler oluştur
		// Burası daha sonra dosya okuma yaparak yapılcak (FileManager)
		
		String[] animalNames;
		
		try {
			animalNames = FileManager.loadRandomFoodChain(this.currentMode);
			
			String apexName = animalNames[0];
			String predatorName = animalNames[1];
			String preyName = animalNames[2]; 
			String foodName = animalNames[3];	
			
			placeAnimal(new ApexPredator(0, 0, apexName, this.currentMode));
			placeAnimal(new Predator(0, 0, predatorName, this.currentMode));
			placeAnimal(new Prey(0, 0, preyName, this.currentMode));
			
			placeEntityRandomly(new Food(0, 0, foodName));
			
			System.out.println("Game is being played with: " + apexName + ", " + predatorName + ", " + preyName + ", " + foodName);
			
			GameLogger.log("");
			GameLogger.log("");
			GameLogger.log("=========================================================");
			GameLogger.log("=== NEW GAME STARTED ===");
			GameLogger.log("The Player is playing predator with name: " + predatorName);
			GameLogger.log("Mode: " + this.currentMode + ", Grid: " + grid.getRows() + "x" + grid.getCols() + ", Total Rounds: " + totalRaunds);
			GameLogger.log("Food Chain Loaded: " + apexName + " -> " + predatorName + " -> " + preyName + " -> " + foodName);
			GameLogger.log("");
			
		}
		catch (GameLoadException e) {
			System.out.println("HATA VAR. KARAKTERLERİ YÜKLEYEMEDİ");
			
			GameLogger.log("ERROR: Could not load game data.");
			e.printStackTrace();
			
			
		}
		
		
		// preyi oynat ve oyunu başlat
		movePreyBot();
		
	}
	
	
	
	
	//////////////////////// BOT MOVES CODES ///////////////////////////////	
	
	/**
	 * Method to move Preys.
	 */
	private void movePreyBot() {
		for (Animal a : animals) {
			if (a instanceof Prey) {
				//moveBotRandomly(a);
				GameLogger.log("Raund " + this.currentRaund);
				a.makeMove(grid);
			}
		}
	}
	
	/**
	 * Method to move Apex Predators.
	 */
	private void moveApexBot() {
		for (Animal a : animals) {
			if (a instanceof ApexPredator) {
				//moveBotRandomly(a);
				a.makeMove(grid);
			}
		}
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Processes movement of predator when player clicks.
	 * And calls processEndOfRound() method to proceed round.
	 * @param targetX X coordinate to move.
	 * @param targetY Y coordinate to move.
	 * @return Returns true if raund is successful.
	 */
	public boolean processTurnWhenCliked(int targetX, int targetY) {
		if (isGameOver) {
			return false;
		}
		
		Predator player = this.getPredatorPlayer();
		
		if (player != null) {
			
			int currentX = player.getX();
			int currentY = player.getY();
			
			if (targetX == currentX && targetY == currentY) {
				System.out.println("Player skipped turn.");
				GameLogger.log("Player skipped turn at (" + currentX + "," + currentY + ")");
				
				player.makeMove(grid);
				processEndOfRound();
				return true;
			}
			
			if (player.isValidMove(grid, targetX, targetY)) {
				player.makeMove(grid);
				
				boolean moved = player.performMove(grid, targetX, targetY);
				
				if (moved) {
					processEndOfRound();
					return true;
				}
			}
			
		}
		
		System.out.println("Invalid move ignored.");
		return false;
	}
	
	
	/**
	 * Calls apex's move method and applies required end of raund procedures.
	 * Finishes the game if total raunds played.
	 * If not, calls movePreyBot() and next raund begins.
	 */
	private void processEndOfRound() {
		moveApexBot();
		
		GameLogger.log("Round " + currentRaund + " completed.");
		GameLogger.log("");
		GameLogger.log("------------------------------------------------------");
		GameLogger.log("");
		
		currentRaund++;
		if (currentRaund > totalRaunds) {
			isGameOver = true;
			
			System.out.println("GAME OVER! " + totalRaunds + " played."); 
			GameLogger.log("=== GAME OVER ===");
			GameLogger.log("");
			
			System.out.println(getGameResult());
			GameLogger.log(getGameResult());
			GameLogger.log("");
			return;
		}
		movePreyBot();
	}
	
	/**
	 * Gives the game results with winner and points of each character.
	 * @return Returns the result string.
	 */
	public String getGameResult() {
		Animal apex = getAnimalByType(ApexPredator.class);
		Animal predator = getAnimalByType(Predator.class);
		Animal prey = getAnimalByType(Prey.class);
		
		int apexScore = apex.getScore();
		int predatorScore = predator.getScore();
		int preyScore = prey.getScore();
		
		int maxScore = Math.max(Math.max(apexScore, preyScore), predatorScore);
		
		StringBuilder winMessage = new StringBuilder("Game Over! \nScores: \n");
		winMessage.append("Predator: " + predatorScore + "\n");
		winMessage.append("Prey: " + preyScore + "\n");
		winMessage.append("Apex: "+ apexScore + "\n");
		winMessage.append("WINNER(S): \n");
		
		if (predatorScore == maxScore) {
			winMessage.append("-> Predator (You)\n");
		}
		if (preyScore == maxScore) {
			winMessage.append("-> Prey\n");
		}
		if (apexScore == maxScore) {
			winMessage.append("-> Apex Predator\n");
		}
		
		return winMessage.toString();
	}
	
	
	
	////////////////// SAVE/LOAD METOTLARI ///////////////////////////
	
	/**
	 * Clears the grid and animals list.
	 */
	public void resetGame() {
		this.animals.clear();
		for (int y = 0; y < grid.getRows(); y++) {
			for (int x = 0; x < grid.getCols(); x++) {
				grid.putEntity(null, x, y);
			}
		}	
	}
	
	/**
	 * Adds animal to the list and the grid.
	 * @param animal animal to add.
	 */
	public void addLoadedAnimal(Animal animal) {
		animals.add(animal);
		grid.putEntity(animal, animal.getX(), animal.getY());
	}
	
	/**
	 * Adds food characther to the grid.
	 * @param food
	 */
	public void addLoadedFood(Food food) {
		grid.putEntity(food, food.getX(), food.getY());
	}
	
	
	/**
	 * Resets the game grid for load purposes.
	 * @param cols new cols
	 * @param rows new rows
	 */
	public void resetGrid(int cols, int rows) {
		this.grid = new Grid(cols, rows);
	}
	
	//////////////////////////////////////////////////////////////////
	
	
	
	// getter-setters		
	public Animal getAnimalByType(Class<?> c) {
		for (Animal a : animals) {
			if (c.isInstance(a)) {
				return a;
			}
		}
		return null;
	}
	
	public Predator getPredatorPlayer() {
		for (Animal a : animals) {
			if (a instanceof Predator) {
				return (Predator) a;
			}
		}
		return null;
	}
	
	public boolean isGameOver() {
		return this.isGameOver;
	}
	
	public Grid getGrid() {
		return this.grid;
	}
	
	
	public int getCurrentRaund() {
		return this.currentRaund;
	}
	
	public int getTotalRaunds() {
		return this.totalRaunds;
	}
	
	public GameMode getCurrentMode() {
		return this.currentMode;
	}
	
	public List<Animal> getAnimals(){
		return this.animals;
	}
	
	public void setCurrentMode(GameMode mode) {
		this.currentMode = mode;
	}
	
	public void setCurrentRound(int round) {
		this.currentRaund = round;
	}
	
	public void setTotalRounds(int total) {
		this.totalRaunds = total;
	}
	
}
