package model.exceptions;

import model.Material;

/**
 * Excepción que se lanza cuando el material no es correcto
 * @author Jose MV
 */
public class WrongMaterialException extends Exception
{
	/**
	 * Método que imprime un mensaje que indica el material de parámetro es incorrecto
	 * @param material objeto material
	 */
	public WrongMaterialException(Material material)
	{
		super("Material " + material + " is wrong");
	}
}
