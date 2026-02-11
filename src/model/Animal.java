package model;

import logic.GameMode;


/**
 * Abstract class representing an animal in the game.
 * Handles common attributes like score, life status, and ability cooldowns.
 */

public abstract class Animal extends Entity implements IRespawnable {
	protected int score;
	protected boolean isAlive;
	protected int abilityCooldown;
	protected GameMode era;
	
	/**
	 * Constructer for Animal class.
	 * @param x coordinate-X of animal
	 * @param y coordinate-Y of animal
	 * @param symbol String type symbol of animal (tentative)
	 * @param name Name of the animal species
	 */    
	public Animal(int x, int y, String symbol, String name, GameMode era) {
		super(x, y, symbol, name);
		this.score = 0;
		this.isAlive = true;	
		this.abilityCooldown = 0;
		this.era = era;
	}
	
	
	/**
	 * Abstract method for defining movement logic
	 * prey-predator-apex predator implements this method
	 * @param grid The game grid
	 */
	public abstract void makeMove(Grid grid);
	
	
	
	
	
	/**
	 * Handles required operations when an animal dies.
	 */
	public void die() {
		this.score -= 1;
		this.isAlive = false;
		this.abilityCooldown = 0;
	}
	
	/**
	 * Changes total points of an animal by "points".
	 * @param points Desired increases amount.
	 */
	public void changeScoreBy(int points) {
		this.score += points;
	}
	
	/**
	 * Decrases cooldown by -1.
	 */
	public void decreaseCooldown() {
		if (abilityCooldown > 0) {
			abilityCooldown -= 1;
		}
	}
	
	/**
	 * Checks if the special ability is ready to use.
	 * @return true if cooldown is 0.
	 */
	public boolean isAbilityAvailable() {
		return abilityCooldown == 0;
	}
	
	
	/**
	 * Respawns the entitiy at a random empty location on the grid.
	 * Sets animal alive and cooldown to zero.
	 * @param x New x coordinate to respawn
	 * @param y New y coordinate to respawn 
	 */
	@Override
	public void respawn(int x, int y) {
		this.isAlive = true; 
		this.setX(x);
		this.setY(y);
		this.abilityCooldown = 0;
	}
	
	
	// getter-setters
	public void setScore(int score) {
		this.score = score;
	}
	
	public void setCooldown(int rounds) {
		this.abilityCooldown = rounds;
	}
	
	public boolean isAlive() {
		return isAlive;
	}
	 	
	
	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}
	
	public int getScore() {
		return score;
	}
	
	public String getName() {
		return name;
	}
	
	
	
	public int getAbilityCooldown() {
		return this.abilityCooldown;
	}
	
	
	
}




