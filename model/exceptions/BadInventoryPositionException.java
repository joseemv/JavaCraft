package model.exceptions;

/**
 * Excepción que se lanza cuando la posición del inventario no es válida
 * @author Jose MV
 */
public class BadInventoryPositionException extends Exception
{
	/**
	 * Método que imrprime la posición dada no es válida
	 * @param pos posición del inventario
	 */
	public BadInventoryPositionException(int pos)
	{
		super("Posición " + pos + " del inventario no válida");		
	}
}
