package model;

import model.exceptions.StackSizeException;

/**
 * Clase que representa los items
 * @author Jose MV
 */
public class ItemStack
{	
	/**
	 * Objeto de clase Material
	 */
	private Material type;
	
	/**
	 * Cantidad del item
	 */
	private int amount;
	
	/**
	 * Cantidad máxima que puede haber en un ItemStack
	 */
	public static final int MAX_STACK_SIZE = 64;
	
	/**
	 * Constructor
	 * @param type Objeto de clase Material
	 * @param amount cantidad del item
	 * @throws StackSizeException excepción que indica que la cantidad del material no es válida
	 */
	public ItemStack(Material type, int amount) throws StackSizeException
	{
		if (!checkAmount(type, amount))
		{
			throw new StackSizeException();
		}
		
		else
		{
			this.type = type;
			this.amount = amount;
		}
	}
	
	/**
	 * Constructor de copia
	 * @param is objeto de clase ItemStack a copiar
	 */
	public ItemStack(ItemStack is)
	{
		type = is.getType();
		amount = is.getAmount();
	}
	
	// ------ GETTERS ------
	
	/**
	 * Getter
	 * @return tipo de material
	 */
	public Material getType()
	{
		return type;
	}
	
	/**
	 * Getter
	 * @return cantidad del item
	 */
	public int getAmount()
	{
		return amount;
	}
	
	// ------ SETTERS ------
	
	/**
	 * Setter
	 * @param n cantidad del item
	 * @throws StackSizeException excepción que indica que la cantidad a establecer no es válida
	 */
	public void setAmount(int n) throws StackSizeException
	{
		if (!checkAmount(type, n))
		{
			throw new StackSizeException();
		}
		
		else
		{
			amount = n;
		}
	}
	
	/**
	 * Método que comprueba que la cantidad que queremos introducir es correcta
	 * @param type tipo de Material
	 * @param amount cantidad de material
	 * @return correct indica si la cantidad a introducir es válida o no
	 */
	public boolean checkAmount(Material type, int amount)
	{
		boolean correct = true;
		
		if ((amount > MAX_STACK_SIZE) || (amount < 1))
		{
			correct = false;
		}
		
		else if (((type.isTool() || (type.isWeapon())) && (amount != 1)))
		{
			correct = false;
		}	
		
		return correct;
	}
	
	/**
	 * Método hashCode
	 * @return result
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + amount;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}	

	/**
	 * Método equals
	 * @param obj objeto a comparar
	 * @return true or false
	 */
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ItemStack other = (ItemStack) obj;
		if (amount != other.amount)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	/**
	 * Método toString que imprime el objeto
	 * @return String que contiene el material y la cantidad
	 */
	public String toString()
	{
		return "(" + type + "," + amount + ")";
	}	
}
