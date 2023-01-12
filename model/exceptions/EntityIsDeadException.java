package model.exceptions;

/**
 * Excepción que se lanza cuando el jugador está muerto y se realiza una acción
 * @author Jose MV
 */
public class EntityIsDeadException extends Exception
{
	/**
	 * Método que imprime el mensaje establecido
	 */
	public EntityIsDeadException()
	{
		super("Entity is dead");
	}
}
