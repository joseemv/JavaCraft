package model.exceptions;

/**
 * Excepción que se lanza cuando la localización no es válida
 * @author Jose MV
 */
public class BadLocationException extends Exception
{
	/**
	 * Método que imprime el mensaje del parámetro
	 * @param str mensaje a imprimir
	 */
	public BadLocationException(String str)
	{
		super(str);
	}
}
