package model.entities;

import model.Location;

/**
 * Clase que establece las criaturas y sus funcionalidades
 * @author Jose MV
 */
public abstract class Creature extends LivingEntity
{
	/**
	 * Constructor sobrecargado
	 * @param loc posición
	 * @param health salud
	 */
	public Creature(Location loc, double health)
	{
		super(loc, health);
	}
}
