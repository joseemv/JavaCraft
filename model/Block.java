package model;

import model.exceptions.WrongMaterialException;

/**
 * Clase para trabajar con los bloques
 * @author Jose MV
 */
public abstract class Block
{	
	/**
	 * Objeto de Material
	 */
	private Material type;
	
	/**
	 * Constructor
	 * @param type objeto Material
	 * @throws WrongMaterialException excepción de material incorrecto
	 */
	public Block(Material type) throws WrongMaterialException
	{
		if (type.isBlock())
		{
			this.type = type;
		}
		
		else
		{
			throw new WrongMaterialException(type);
		}
	}
	
	/**
	 * Constructor de copia
	 * @param b objeto Block a copiar
	 */
	protected Block(Block b)
	{
		this.type = b.getType();
	}
	
	/**
	 * Método abstracto para clonar el objeto
	 * @return Block bloque clonado
	 */
	public abstract Block clone();
	
	/**
	 * Getter
	 * @return objeto de Material
	 */
	public Material getType()
	{
		return type;
	}

	/**
	 * Imprime clase Block
	 * @return String con el material
	 */
	public String toString()
	{
		return "[" + type + "]";
	}

	/**
	 * Método hashCode
	 * @return result resultado
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Block other = (Block) obj;
		if (type != other.type)
		{
			return false;
		}
		return true;
	}
}