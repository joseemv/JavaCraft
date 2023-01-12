package model;

import model.exceptions.WrongMaterialException;

/**
 * Clase para trabajar con los bloques líquidos
 * @author Jose MV
 */
public class LiquidBlock extends Block
{	
	/**
	 * Atributo que indica el daño que inflige el líquido
	 */
	private double damage;
	
	/**
	 * Constructor
	 * @param type objeto Material
	 * @throws WrongMaterialException excepción de material incorrecto
	 */
	public LiquidBlock(Material type) throws WrongMaterialException
	{
		super(type);
		damage = this.getType().getValue();
		
		if (!type.isLiquid())
		{
			throw new WrongMaterialException(type);
		}
	}
	
	/**
	 * Constructor de copiar
	 * @param lb objeto a copiar
	 */
	protected LiquidBlock(LiquidBlock lb)
	{
		super(lb);
	}
	
	/**
	 * Getter
	 * @return daño que produce al atravesarlo
	 */
	public double getDamage()
	{
		return damage;
	}
	
	/**
	 * Método que clona el objeto
	 * @return this devuelve el objeto
	 */
	public Block clone()
	{
		return this;
	}
	
	/**
	 * Método hashCode
	 * @return result resultado
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		long temp;
		temp = Double.doubleToLongBits(damage);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		if (!super.equals(obj))
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		LiquidBlock other = (LiquidBlock) obj;
		if (Double.doubleToLongBits(damage) != Double.doubleToLongBits(other.damage))
		{
			return false;
		}
		return true;
	}
}
