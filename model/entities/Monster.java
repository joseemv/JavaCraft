package model.entities;

import model.Location;

/**
 * Clase que establece los monstruos y sus funcionalidades
 * @author Jose MV
 */
public class Monster extends Creature
{
	/**
	 * Atributo que indica el símbolo del monstruo
	 */
	private static char symbol = 'M';
	
	/**
	 * Constructor sobrecargado
	 * @param loc posición
	 * @param health salud
	 */
	public Monster(Location loc, double health)
	{
		super(loc, health);
	}
	
	/**
	 * Getter
	 * @return symbol representa a un monstruo
	 */
	public char getSymbol()
	{
		return symbol;
	}
	
	/**
	 * Método toString
	 * @return String que imprime información del monstruo
	 */
	public String toString()
	{
		return "Monster [location=" + location + ", health=" + this.getHealth() + "]";
	}
}
