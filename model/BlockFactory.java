package model;

import model.exceptions.WrongMaterialException;

/**
 * Clase para crear bloques
 * @author Jose MV
 */
public class BlockFactory
{
	/**
	 * Constructor de bloques
	 * @param type material
	 * @return b bloque creado
	 * @throws WrongMaterialException excepción de material incorrecto
	 */
	public static Block createBlock(Material type) throws WrongMaterialException
	{		
		if (type.isLiquid())
		{
			return new LiquidBlock(type);
		}
		
		else if (!type.isLiquid())
		{
			return new SolidBlock(type);
		}
		
		else
		{
			throw new WrongMaterialException(type);
		}
	}
}