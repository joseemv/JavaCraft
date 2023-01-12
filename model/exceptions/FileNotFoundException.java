package model.exceptions;

/**
 * Excepción que se lanza cuando no se puede abrir el archivo
 * @author Jose MV
 */
public class FileNotFoundException extends Exception
{
	/**
	 * Método que imprime el mensaje establecido
	 */
	public FileNotFoundException()
	{
		super("File not found");
	}
}
