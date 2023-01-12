package model.exceptions;

/**
 * Excepción que se lanza cuando la cantidad del item no es válida
 * @author Jose MV
 */
public class StackSizeException extends Exception
{
	/**
	 * Método que imprime el mensaje establecido
	 */
	public StackSizeException()
	{
		super("Invalid stack size");
	}
}
