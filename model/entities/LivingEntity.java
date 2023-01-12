package model.entities;

import model.Location;

/**
 * Clase para trabajar con los seres vivos
 * @author Jose MV
 */
public abstract class LivingEntity
{
	/**
	 * Objeto Location
	 */
	protected Location location;
	
	/**
	 * Valor de salud
	 */
	private double health;
	
	/**
	 * Valor máximo del nivel de salud
	 */
	public static final double MAX_HEALTH = 20;
	
	/**
	 * Constructor
	 * @param loc posición
	 * @param health nivel de salud
	 */
	public LivingEntity(Location loc, Double health)
	{
		location = loc;
		
		if (health > MAX_HEALTH)
		{
			this.health = MAX_HEALTH;
		}
		
		else
		{
			this.health = health;
		}
	}
	
	/**
	 * Getter
	 * @return health salud
	 */
	public double getHealth()
	{
		return health;
	}
	
	/**
	 * Getter
	 * @return loc posición
	 */
	public Location getLocation()
	{
		return new Location(location);
	}
	
 	/**
 	 * Getter
 	 * @return symbol carácter que representa a la entidad
 	 */
	public abstract char getSymbol();
	
	/**
	 * Setter
	 * @param health establece salud de parámetro
	 */
	public void setHealth(double health)
	{
		if (health <= MAX_HEALTH) 
		{
			this.health = health;			
		}
		
		else
		{
			this.health = MAX_HEALTH;
		}
	}
	
	/**
	 * Método que inflige daño a la entidad
	 * @param dmg daño realizado
	 */
	public void damage(double dmg)
	{
		health = health - dmg;
	}
	
	/**
	 * Método que comprueba si el ser está muerto
	 * @return isDead indica si está muerto o no
	 */
 	public boolean isDead()
	{
		boolean isDead = false;
		
		if (health <= 0.0001)
		{
			isDead = true;
		}
		
		return isDead;
	}
 	

 	/**
	 * Método hashCode
	 * @return result resultado
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(health);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		return result;
	}

	
	/**
	 * Método equals
	 * @param obj objeto a comparar
	 * @return true o false
	 */
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		LivingEntity other = (LivingEntity) obj;
		if (Double.doubleToLongBits(health) != Double.doubleToLongBits(other.health))
		{
			return false;
		}
		if (location == null)
		{
			if (other.location != null)
			{
				return false;
			}
		} else if (!location.equals(other.location))
		{
			return false;
		}
		return true;
	}

	/**
	 * Método toString
	 * @return String que imprime información de la entidad
	 */
	public String toString()
	{
		return "LivingEntity [location=" + location + ", health=" + health + "]";
	}
}
