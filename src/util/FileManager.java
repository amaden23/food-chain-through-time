package util;

import java.security.SecureRandom;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import exception.GameLoadException;
import logic.GameEngine;
import logic.GameMode;
import model.Animal;
import model.ApexPredator;
import model.Entity;
import model.Food;
import model.Grid;
import model.Predator;
import model.Prey;
/**
 * Handles file related tasks.
 */
public class FileManager {
	
	private static final String PAST_FILE =  "past_animals.txt";
	private static final String PRESENT_FILE = "present_animals.txt";  
	private static final String FUTURE_FILE = "future_animals.txt";
		
	private static final SecureRandom random = new SecureRandom();
	
	
	/**
	 * Reads from the txt files and returns a random food chain.
	 * @param mode Current game mode 
	 * @return A string array with names of characthers.
	 * @throws GameLoadException throws this custom exception if file is missing.
	 */
	public static String[] loadRandomFoodChain(GameMode mode) throws GameLoadException{
		String filename = getFileNameByMode(mode);
		List<String[]> validChains = new ArrayList<>();
		
		File file = new File(filename);
		if (!file.exists()) {
			throw new GameLoadException("Configuration file not found: " + filename);
		}
		
		try (BufferedReader buf = new BufferedReader(new FileReader(file))){
			String line;
			while ((line = buf.readLine()) != null) {
				line = line.trim();
				
				if (line.startsWith("Food Chain")) {
					String[] parts = parseLine(line);
					if (parts != null) {
						validChains.add(parts);	
						
					}
					
				}
				
			}
			
			
		}
		catch (IOException e) {
			throw new GameLoadException("Error reading file: " + filename); 
			
		}
		
		if (validChains.isEmpty()) {
			throw new GameLoadException("No valid food chains found in " + filename);
		}
		
		return validChains.get(random.nextInt(validChains.size()));			  
	}
	
	
	/**
	 * Takes game mode as a parameter and returns file name accordingly.
	 * @param mode game mode to be played
	 * @return File name.
	 */
	private static String getFileNameByMode(GameMode mode) {
		switch (mode) {
			case PAST:
				return PAST_FILE;
			case FUTURE:
				return FUTURE_FILE;
			default:
				return PRESENT_FILE;
		}
	}
	
	
	/**
	 * Takes a food chain line and extract names in a string array.
	 * @param line The line to process.
	 * @return returns the string array that contains names.
	 */
	private static String[] parseLine(String line) {
		try {
			String[] splitCharacthers = line.split(":"); 
			if (splitCharacthers.length < 2) {
				return null; 
				
			}
			
			String Characthers = splitCharacthers[1].trim(); 
			String[] names =  Characthers.split(",");
			
			if (names.length == 4) { 
				for (int i = 0; i < names.length; i++) {
					names[i] = names[i].trim();
				}
				
				return names;
			}
			
		} 
		catch (Exception e) {
			
		}
		
		return null;
	}
	
	
	
	/**
	 * Saves the current game informations to the "saved_game.txt" file.
	 * @param engine Current game engine.
	 * @throws IOException Throws if file writing error happens.
	 */
	public static void saveGame(GameEngine engine) throws IOException{
		File file = new File("saved_game.txt");
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
			
			// GENEL OYUN BİLGİLERİ
			writer.write("MODE:" + engine.getCurrentMode());
			writer.newLine();
			
			writer.write("ROUND:" + engine.getCurrentRaund() + "/" + engine.getTotalRaunds());
			writer.newLine();
			
			writer.write("GRID SIZE:" + engine.getGrid().getCols());
			writer.newLine();
			
			// KARAKTERLER (ANİMAL'LAR)
			for (Animal animal : engine.getAnimals()) {
				StringBuilder s = new StringBuilder();
				
				s.append("ENTITY:");
				s.append(animal.getClass().getSimpleName()).append(";");
				s.append(animal.getName()).append(";");
				s.append(animal.getX()).append(";");
				s.append(animal.getY()).append(";");
				s.append(animal.getScore()).append(";");
				s.append(animal.getAbilityCooldown());
				
				writer.write(s.toString());
				writer.newLine();
			}
			
			// FOODLAR
			Grid grid = engine.getGrid();
			for (int y = 0; y < grid.getRows(); y++) {
				for (int x = 0; x < grid.getCols(); x++) { 
					
					Entity e = grid.getEntity(x, y);
					if (e instanceof Food) {
						writer.write("FOOD:" + e.getName() + ";" + x + ";" + y);
						writer.newLine();
					}
					
				}
			}
			
		}
		catch (IOException e) {
			System.err.println("Kaydetme kısmında bi hata var " + e.getMessage());
			throw e;
		}
				
	}
	
	
	/**
	 * Loads the saved game by reading "saved_game.txt" file.
	 * @param engine Current game engine to set.
	 * @throws IOException Throws if file error happens.
	 * @throws GameLoadException Throws if file not found.
	 */
	public static void loadGame(GameEngine engine) throws IOException, GameLoadException{ 
		File file = new File("saved_game.txt");
		
		if (!file.exists()) {
			throw new GameLoadException("File not found.");
		}
		
		engine.resetGame();
		
		try (BufferedReader reader = new BufferedReader(new FileReader(file))){
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(":");
				if (parts.length < 2) {
					continue;
				}
				
				String key = parts[0].trim();
				String value = parts[1].trim();
				
				switch (key) {
					case "MODE":
						engine.setCurrentMode(GameMode.valueOf(value)); 
						break;
						
							
					case "ROUND":
						String[] rounds = value.split("/");
						engine.setCurrentRound(Integer.parseInt(rounds[0]));
						engine.setTotalRounds(Integer.parseInt(rounds[1]));
						break; 
						
					case "GRID SIZE":
						int size = Integer.parseInt(value);
						engine.resetGrid(size, size);
						break;
					
					case "ENTITY":
						String[] data = value.split(";");
						String type = data[0];
						String name = data[1];
						int x = Integer.parseInt(data[2]);
						int y = Integer.parseInt(data[3]);
						int score = Integer.parseInt(data[4]);
						int cooldown = Integer.parseInt(data[5]);
						
						GameMode mode = engine.getCurrentMode();
						
						Animal animal = null;
						if (type.equals("ApexPredator")) {
							animal = new ApexPredator(x, y, name, mode);
							animal.setCooldown(cooldown);
						}
						else if (type.equals("Predator")) {
							animal = new Predator(x, y, name, mode);
							animal.setCooldown(cooldown);
						}
						else if (type.equals("Prey")) {
							animal = new Prey(x, y, name, mode);
							animal.setCooldown(cooldown);
						}
						
						if (animal != null) {
							animal.setScore(score);
							animal.setCooldown(cooldown);
							engine.addLoadedAnimal(animal);
						}
						break;
						
					case "FOOD":
						String[] foodData = value.split(";");
						String foodName = foodData[0];
						int foodX = Integer.parseInt(foodData[1]);
						int foodY = Integer.parseInt(foodData[2]);
						
						Food food = new Food(foodX, foodY, foodName);
						engine.addLoadedFood(food);
						break;
				}
				
			}
			
			
		}
		catch (Exception e) {
			throw new GameLoadException("Load yaparken bir hata var. " + e.getMessage());	
			
		}
		
	}
	
	
	
	
	
}

