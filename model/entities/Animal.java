package model.entities;

import model.ItemStack;
import model.Location;
import model.Material;
import model.exceptions.StackSizeException;

/**
 * Clase que establece los animales y sus funcionalidades
 * @author Jose MV
 */
public class Animal extends Creature
{
	/**
	 * Atributo que indica el símbolo del animal
	 */
	private static char symbol = 'L';
	
	/**
	 * Constructor sobrecargado
	 * @param loc posición
	 * @param health salud
	 */
	public Animal(Location loc, double health)
	{
		super(loc, health);
	}
	
	/**
	 * Getter
	 * @return is material que proporciona el animal
	 */
	public ItemStack getDrops()
	{
		ItemStack is = null;
		try
		{
			is = new ItemStack(Material.BEEF, 1);
		} 
		
		catch (StackSizeException e)
		{
			e.printStackTrace();
		}
		
		return is;
	}
	
	/**
	 * Getter
	 * @return symbol representa al animal
	 */
	public char getSymbol()
	{
		return symbol;
	}
	
	/**
	 * Método toString
	 * @return String que imprime información del animal
	 */
	public String toString()
	{
		return "Animal [location=" + location + ", health=" + this.getHealth() + "]";
	}
}
