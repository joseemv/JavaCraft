package model;

import java.util.Random;

/**
 * Clase que enumera los materiales y sus propiedades
 * @author Jose MV
 */
public enum Material
{		
	/**
	 * Bedrock material
	 */							// Indice
	BEDROCK(-1, '*'),			// 0
	/**
	 * Chest material
	 */
	CHEST(0.1, 'C'),			// 1
	/**
	 * Sand material
	 */
	SAND(0.5, 'n'),				// 2
	/**
	 * Dirt material
	 */
	DIRT(0.5, 'd'),				// 3
	/**
	 * Grass material
	 */
	GRASS(0.6, 'g'),			// 4
	/**
	 * Stone material
	 */
	STONE(1.5, 's'),			// 5
	/**
	 * Granite material
	 */
	GRANITE(1.5, 'r'),			// 6
	/**
	 * Obsidian material
	 */
	OBSIDIAN(5, 'o'),			// 7
	/**
	 * Water bucket material
	 */
	WATER_BUCKET(1, 'W'),		// 8
	/**
	 * Apple material
	 */
	APPLE(4, 'A'),				// 9
	/**
	 * Bread material
	 */
	BREAD(5, 'B'),				// 10
	/**
	 * Beef material
	 */
	BEEF(8, 'F'),				// 11
	/**
	 * Iron shovel material
	 */
	IRON_SHOVEL(0.2, '>'),		// 12
	/**
	 * Iron pickaxe material
	 */
	IRON_PICKAXE(0.5, '^'),		// 13
	/**
	 * Wood sword material
	 */
	WOOD_SWORD(1, 'i'), 		// 14
	/**
	 * Iron sword material
	 */
	IRON_SWORD(2, 'I'),			// 15
	/**
	 * Lava material
	 */
	LAVA(1.0, '#'),				// 16
	/**
	 * Water material
	 */
	WATER(0.0, '@');			// 17

	
	/**
	 * Indica el valor del material
	 */
	private double value;
	
	/**
	 * Indica el símbolo que representa el material
	 */
	private char symbol;
	
	/**
	 * Objeto aleatorio para generar el mundo
	 */
	static Random rng = new Random(1L);
	
	/**
	 * Constructor
	 * @param value valor del material
	 * @param symbol símbolo del material
	 */
	private Material(double value, char symbol)
	{
		this.value = value;
		this.symbol = symbol;
	}
	
	// ------ GETTERS ------
	
	/**
	 * Getter
	 * @return value valor del material
	 */
	public double getValue()
	{
		return value;
	}
	
	/**
	 * Getter
	 * @return symbol símbolo del material
	 */
	public char getSymbol()
	{
		return symbol;
	}
	
	/**
	 * Método que obtiene un material aleatorio de la clase entre los índices de parámetro
	 * @param first índice primer elemento
	 * @param last índice ultimo elemento
	 * @return número aleatorio
	 */
	public static Material getRandomItem(int first, int last)
	{
		int i = rng.nextInt(last - first + 1) + first;
		
		return values()[i];
	}
	
	/**
	 * Método que comprueba si el material actual es un bloque
	 * @return isBlock indica si es bloque o no
	 */
	public boolean isBlock()
	{
		boolean isBlock = false;
		int index = this.ordinal();
		
		if ((index >= 0) && (index <= 7) || (index == 16) || (index == 17))
		{
			isBlock = true;
		}
		
		return isBlock;
	}
	
	/**
	 * Método que comprueba si el material actual es comida
	 * @return isEdible indica si es comida o no
	 */
	public boolean isEdible()
	{
		boolean isEdible = false;
		int index = this.ordinal();
		
		if ((index >= 8) && (index <= 11))
		{
			isEdible = true;
		}
		
		return isEdible;		
	}
	
	/**
	 * Método que comprueba si el material actual es un arma
	 * @return isWeapon indica si es un arma o no
	 */
	public boolean isWeapon()
	{
		boolean isWeapon = false;
		int index = this.ordinal();
		
		if ((index == 14) || (index == 15))
		{
			isWeapon = true;
		}
		
		return isWeapon;		
	}
	
	/**
	 * Método que comprueba si el material actual es una herramienta
	 * @return isTool indica si es una herramienta o no
	 */
	public boolean isTool()
	{
		boolean isTool = false;
		int index = this.ordinal();
		
		if ((index == 12) || (index == 13))
		{
			isTool = true;
		}
		
		return isTool;		
	}
	
	/**
	 * Método que comprueba si el material actual es líquido
	 * @return isLiquid indica si es líquido o no
	 */
	public boolean isLiquid()
	{
		boolean isLiquid = false;
		int index = this.ordinal();
		
		if ((index == 16) || (index == 17))
		{
			isLiquid = true;
		}
		
		return isLiquid;			
	}
}