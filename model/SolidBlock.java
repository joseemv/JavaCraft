package model;

import model.exceptions.StackSizeException;
import model.exceptions.WrongMaterialException;

/**
 * Clase para trabajar con los bloques sólidos
 * @author Jose MV
 */
public class SolidBlock extends Block
{	
	/**
	 * Objeto de ItemStack
	 */
	private ItemStack drops;	
	
	/**
	 * Constructor
	 * @param type objeto Material
	 * @throws WrongMaterialException excepción de material incorrecto
	 */
	public SolidBlock(Material type) throws WrongMaterialException
	{
		super(type);
		drops = null;
		
		if (type.isLiquid())
		{
			throw new WrongMaterialException(type);			
		}
	}
	
	/**
	 * Constructor de copia
	 * @param b objeto SolidBlock a copiar
	 */
	protected SolidBlock(SolidBlock b)
	{
		super(b);
	}
	
	/**
	 * Getter
	 * @return objeto de ItemStack
	 */
	public ItemStack getDrops()
	{
		return drops;
	}
	
	/**
	 * Setter
	 * @param type tipo de Material
	 * @param amount cantidad de items
	 * @throws StackSizeException excepción por cantidad del item incorrecta
	 */
	public void setDrops(Material type, int amount) throws StackSizeException
	{
		ItemStack is = new ItemStack(type, amount);
		
		if (!is.checkAmount(type, amount))
		{
			throw new StackSizeException();
		}
		
		else
		{
			if ((this.getType() != Material.CHEST) && (amount == 1))
			{
				drops = is;
			}
			
			else if (this.getType() == Material.CHEST)
			{
				drops = is;
			}
			
			else // Si no es chest y la cantidad es mayor que 1
			{
				throw new StackSizeException();
			}
		}
	}
	
	/**
	 * Método que comprueba si se rompe el bloque
	 * @param dmg Daño infligido
	 * @return indica si se rompe o no
	 */
	public boolean breaks(double dmg)
	{
		boolean breaks = false;
		
		if (dmg >= getType().getValue())
		{
			breaks = true;
		}
		
		return breaks;
	}

	/**
	 * Método que devuelve el objecto para clonarlo
	 * @return this objeto SolidBlock
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
		result = prime * result + ((drops == null) ? 0 : drops.hashCode());
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
		SolidBlock other = (SolidBlock) obj;
		if (drops == null)
		{
			if (other.drops != null)
			{
				return false;
			}
		} else if (!drops.equals(other.drops))
		{
			return false;
		}
		return true;
	}
}