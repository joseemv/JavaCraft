package model;

import java.util.ArrayList;
import java.util.List;

import model.exceptions.BadInventoryPositionException;

/**
 * Clase que forma el inventario del jugador
 * @author Jose MV
 */
public class Inventory
{
	/**
	 * Objeto de ItemStack que representa el item que lleva en la mano el jugador
	 */
	private ItemStack inHand;
	
	/**
	 * Lista de ItemStack que representa los items que hay en el inventario
	 */
	private List<ItemStack> items;
	
	/**
	 * Constructor
	 */
	public Inventory()
	{
		inHand = null;
		items = new ArrayList<ItemStack>();
	}
	
	// ------ GETTERS ------
	
	/**
	 * Getter
	 * @param n posición en el inventario
	 * @return objeto clase ItemStack
	 */
	public ItemStack getItem(int n)
	{
		ItemStack item = null;
		
		if ((items != null) && (n > -1))
		{
			int size = items.size();
			
			if (n <= (size - 1))
			{
				item = items.get(n);
			}
		}
		
		return item;
	}
	
	/**
	 * Getter
	 * @return item en la mano de la clase ItemStack
	 */
	public ItemStack getItemInHand()
	{
		return inHand;
	}
	
	/**
	 * Getter
	 * @return número de items en el inventario sin contar el de la mano
	 */
	public int getSize()
	{
		int size = 0;
		
		if (items != null)
		{
			size = items.size();
		}
		
		return size;
	}
	
	// ------ SETTERS ------
	
	/**
	 * Setter
	 * Añade un item en la posición dada del inventario
	 * @param slot posicion en el inventario
	 * @param items objeto de ItemStack a introducir en el inventario
	 * @throws BadInventoryPositionException exceción que indica que la posición del ivnentario no es válida
	 */
	public void setItem(int slot, ItemStack items) throws BadInventoryPositionException
	{
		if (this.items.isEmpty())
		{
			throw new BadInventoryPositionException(slot);			
		}
		
		else
		{
			if (slot < this.items.size())
			{
				this.items.remove(slot);
				this.items.add(slot, items);
			}
			
			else
			{
				throw new BadInventoryPositionException(slot);
			}
		}
	}
	
	/**
	 * Setter
	 * Añade un item en la mano del jugador
	 * @param items objeto de clase ItemStack
	 */
	public void setItemInHand(ItemStack items)
	{
		inHand = items;
	}
	
	/**
	 * Setter
	 * Convierte el valor de inHand a null
	 */
	public void setItemInHandNull()
	{
		inHand = null;
	}
	
	/**
	 * Método que añade un item al inventario
	 * @param is item, objeto de clase ItemStack
	 * @return cantidad de items añadidos
	 */
	public int addItem(ItemStack is)
	{
		int amount = 0;
		
		if (is != null)
		{
			if ((is.getAmount() >= 0) && (is.getAmount() <= ItemStack.MAX_STACK_SIZE))
			{
				items.add(is);
				amount = is.getAmount();
			}
		}
		
		return amount;
	}
	
	/**
	 * Método que elimina los items de la lista de inventario y de la mano
	 */
	public void clear()
	{
		items.clear();
		inHand = null;
	}
	
	/**
	 * Método que elimina el item del inventario en la posición dada
	 * @param slot posición en el inventario
	 * @throws BadInventoryPositionException excepción que indica que la posición del inventario no es válida
	 */
	public void clear(int slot) throws BadInventoryPositionException
	{
		if (items != null)
		{			
			if (slot < items.size())
			{
				items.remove(slot);				
			}
			
			else
			{
				throw new BadInventoryPositionException(slot);
			}
		}
		
		else
		{
			throw new BadInventoryPositionException(slot);
		}
	}
	
	/**
	 * Método que obtiene la posición del primer item del inventario igual que el del parámetro
	 * @param mat objeto de clase Material que queremos comparar
	 * @return índice del item en el inventario, -1 si no hay ningún item igual
	 */
	public int first(Material mat)
	{
		int index = -1;
		
		for (int i = 0; (i < items.size()) && (index == -1); i++)
		{
			if (items.get(i).getType().equals(mat))
			{
				index = i;
			}
		}
		
		return index;
	}
	
	/**
	 * Método toString para imprimir objeto de la clase
	 * @return String a imprimir
	 */
	public String toString()
	{
		String strInHand;
		if (inHand != null)
		{
			strInHand = "(inHand=(" + inHand.getType() + "," + inHand.getAmount() + ")";
		}
		
		else 
		{
			strInHand = "(inHand=null";
		}
		String strItems = ",[";
		
		if (items != null)
		{
			int size = items.size();
			
			for (int i = 0; i < size; i++)
			{
				if (i != 0)
				{
					strItems += ", ";
				}
				
				strItems += "(" + items.get(i).getType() + "," + items.get(i).getAmount() + ")";
			}
		}
		
		strItems += "])";
		
		return strInHand + strItems;
	}
	
	/**
	 * Método hashCode
	 * @return result
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((inHand == null) ? 0 : inHand.hashCode());
		result = prime * result + ((items == null) ? 0 : items.hashCode());
		return result;
	}
	
	/**
	 * Método equals que compara objetos de la clase
	 * @param obj objeto a comparar
	 * @return true or false según si es igual
	 */
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Inventory other = (Inventory) obj;
		if (inHand == null)
		{
			if (other.inHand != null)
				return false;
		} else if (!inHand.equals(other.inHand))
			return false;
		if (items == null)
		{
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		return true;
	}
}
