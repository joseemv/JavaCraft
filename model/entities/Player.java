package model.entities;

import model.Inventory;
import model.ItemStack;
import model.Location;
import model.Material;
import model.World;
import model.exceptions.BadInventoryPositionException;
import model.exceptions.BadLocationException;
import model.exceptions.EntityIsDeadException;
import model.exceptions.StackSizeException;

/**
 * Clase que establece el jugador y sus funcionalidades
 * @author Jose MV
 */
public class Player extends LivingEntity
{		
	/**
	 * Atributo que establece la orientación del jugador
	 */
	private Location orientation;
	
	/**
	 * Atributo que establece el inventario del jugador
	 */
	private Inventory inventory;
	
	/**
	 * String que contiene el nombre del jugador
	 */
	private String name;
	
	/**
	 * Valor del nivel de comida del jugador
	 */
	private double foodLevel;
	
	/**
	 * Valor máximo del nivel de comida
	 */
	public static final double MAX_FOODLEVEL = 20;
	
	/**
	 * Símbolo del jugador
	 */
	private static char symbol = 'P';
	
	/**
	 * Coste de realizar una acción que reduce el nivel de comida o, en su defecto, vida
	 */
	public static final double ACTION_COST = 0.1;
	
	/**
	 * Coste de realizar un movimiento que reduce el nivel de comida o, en su defecto, vida
	 */
	public static final double MOVE_COST = 0.05;
	
	/**
	 * Constructor
	 * @param name nombre del jugador
	 * @param world Objeto de World al que pertenece
	 */
	public Player(String name, World world)
	{
		super(new Location(world, 0, 0, 0), MAX_HEALTH);
		Location temp_loc = new Location(this.getLocation());
		
		try
		{
			Location starting_loc = new Location(world.getHighestLocationAt(temp_loc));
			starting_loc = starting_loc.above();
			super.location = starting_loc;
			this.name = name;
			foodLevel = MAX_FOODLEVEL;
			inventory = new Inventory();
			inventory.setItemInHand(new ItemStack(Material.WOOD_SWORD, 1));
			orientation = new Location(world, 0, 0, 1);
		} 
		
		catch (BadLocationException | StackSizeException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Getter
	 * @return foodLevel nivel de comida que tiene el jugador
	 */
	public double getFoodLevel() 
	{
		return foodLevel;
	}
	
	/**
	 * Getter
	 * @return orientation posición absoluta de orientación jugador
	 */
	public Location getOrientation()
	{
		Location temp_loc = new Location(this.location);
		
		return new Location(temp_loc.add(orientation));
	}
	
	/**
	 * Getter
	 * @return name nombre del jugador
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Getter
	 * @return symbol representa al jugador
	 */
	public char getSymbol()
	{
		return symbol;
	}
	
	/**
	 * Getter
	 * @return size tamaño del inventario
	 */
	public int getInventorySize()
	{
		int size = 0;
		
		if (inventory != null)
		{
			size = inventory.getSize();
		}
		
		return size;
	}
		
	/**
	 * Setter
	 * @param foodLevel establece nivel de comida de parámetro
	 */
	public void setFoodLevel(double foodLevel)
	{
		if (foodLevel <= MAX_FOODLEVEL)
		{
			this.foodLevel = foodLevel;			
		}
		
		else if (foodLevel > MAX_FOODLEVEL)
		{
			this.foodLevel = MAX_FOODLEVEL;
		}
	}	
 	 
 	/**
 	 * Método que realiza los movimientos del jugador
 	 * @param dx distancia que se mueve de coordenada x
 	 * @param dy distancia que se mueve de coordenada y
 	 * @param dz distancia que se mueve de coordenada z
 	 * @return location la posición a la que se ha desplazado
 	 * @throws EntityIsDeadException excepción que indica que el jugador está muerto
 	 * @throws BadLocationException excepción que indica que la localización no es válida
 	 */
 	public Location move(int dx, int dy, int dz) throws EntityIsDeadException, BadLocationException
 	{
 		if (isDead())
 		{
 			throw new EntityIsDeadException();
 		}
 		
 		else
 		{
 			Location temp_loc = new Location(location.getWorld(), dx, dy, dz);
 			temp_loc.add(this.location);;
 			
 			if (temp_loc.isFree() && (location.getNeighborhood().contains(temp_loc)))
			{
 				this.location = temp_loc;
 				decreaseFoodLevel(MOVE_COST);
			}
 			
 			else
 			{
 				throw new BadLocationException("Bad Location in move()");
 			}
 		}
 		
 		return location;
 	}
 	 	
 	/**
 	 * Método que permite utilizar al jugador el item que tiene en la mano
 	 * @param times número de veces que usa el item
 	 * @return itemInHand item que llevamos en la mano
 	 * @throws EntityIsDeadException excepción que indica que el jugador está muerto
 	 * @throws IllegalArgumentException excepción que indica que el parámetro times no es válido
 	 */
 	public ItemStack useItemInHand(int times) throws EntityIsDeadException, IllegalArgumentException
 	{
 		if (isDead())
 		{
 			throw new EntityIsDeadException();
 		}
 		
 		else if (inventory.getItemInHand() != null)
 		{
 	 		if (times <= 0)
 	 		{
 	 			throw new IllegalArgumentException();
 	 		}
 	 		
 	 		else
 	 		{
 	 			if (inventory.getItemInHand().getType().isEdible())
 				{
 	 				eatEdible(times);
 				}
 	 			
 	 			else if (!inventory.getItemInHand().getType().isEdible())
 	 			{
 	 				notEdible(times);
 	 			}
 	 		}
 		}
 		
 		return inventory.getItemInHand();
 	}
 	 	
 	/**
 	 * Método auxiliar para cuando el jugador utiliza el item en la mano y es comida
 	 * @param times número de veces que usa el item
 	 */
 	public void eatEdible(int times)
 	{
 		int amount = inventory.getItemInHand().getAmount();
 		
 		for (int i = 0; (amount != 0) && (i < times); i++)
 		{
 			double value = inventory.getItemInHand().getType().getValue();
			amount--;
			increaseFoodLevel(value);
		}
 		
 		if (amount != 0)
 		{
 			try
			{
				inventory.getItemInHand().setAmount(amount);
			} 
 			catch (StackSizeException e)
			{
				e.printStackTrace();
			} 			
 		} 
 		
 		else 
 		{
 			inventory.setItemInHandNull();
 		}
 	}
 	
 	/**
 	 * Método auxiliar para cuando el jugador utiliza el item en la mano y NO es comida
 	 * @param times número de veces que usa el item
 	 */
 	public void notEdible(int times)
 	{ 		
 		for (int i = 0;i < times; i++)
 		{
 			decreaseFoodLevel(ACTION_COST);
 		}
 	}
 	 
 	/**
 	 * Método que equipa el item de la posición n del inventario en la mano
 	 * @param n posición en el inventario del item a equipar
 	 * @throws EntityIsDeadException excepción que indica que el jugador está muerto
 	 * @throws BadInventoryPositionException exceción que indica que la posición del inventario no es válida
 	 */
 	public void selectItem(int n) throws EntityIsDeadException, BadInventoryPositionException
 	{
 		if (isDead()) // Comprueba si está muerto
 		{
 			throw new EntityIsDeadException();
 		} 		
 		
 		else
 		{
 			ItemStack temp_inHand = inventory.getItemInHand();
 			ItemStack temp_item = inventory.getItem(n);
 			
 			if (n < inventory.getSize()) // Comprueba si la posición es menor que el tamaño del inventario
 			{
	 			if (temp_inHand == null) // Si no tiene nada en la mano
	 			{
	 				inventory.setItemInHand(temp_item);
	 				try 
	 				{
	 					inventory.clear(n);
	 				} 				
	 				catch (BadInventoryPositionException e)
	 				{
	 					e.printStackTrace();
	 				}
	 			} 
	 			else // Si necesita intercambiar el item del inventario por el de la mano
	 			{
	 				inventory.setItemInHand(temp_item);
	 				try
	 				{
	 					inventory.setItem(n, temp_inHand);
	 				} 	
	 				
	 				catch (BadInventoryPositionException e)
	 				{
	 					e.printStackTrace();
	 				}
	 			}
 			}
 			else // Si el índice es mayor o igual que el inventario
 			{
 				throw new BadInventoryPositionException(n);
 			}
 		}
 	}
 	 	
 	/**
 	 * Método que añade items al inventario
 	 * @param items item a añadir
 	 */
 	public void addItemsToInventory(ItemStack items)
 	{ 		
 		inventory.addItem(items);
 	} 	
 	
 	/**
 	 * Método que reduce el nivel de comida del jugador o, en su defecto, la salud
 	 * @param n valor de la reducción
 	 */
	private void decreaseFoodLevel(double n)
 	{
 		double missingFoodLevel = foodLevel - n;
 		
 		if ((missingFoodLevel >= 0) && (missingFoodLevel <= MAX_FOODLEVEL))
		{
 			foodLevel = missingFoodLevel;
		}
 		
 		else if (missingFoodLevel > MAX_FOODLEVEL)
 		{
 			foodLevel = MAX_FOODLEVEL;
 		}
 		
 		else
 		{
 			foodLevel = 0;
 			this.setHealth(this.getHealth() + missingFoodLevel);
 		}
 	}
 	
	/**
 	 * Método que aumenta el nivel de comida del jugador o, en su defecto, la salud
 	 * @param n valor del incremento
 	 */
	private void increaseFoodLevel(double n)
 	{
 		double nextFoodLevel = foodLevel + n;
 		
 		if ((nextFoodLevel >= 0) && (nextFoodLevel <= MAX_FOODLEVEL))
		{
 			foodLevel = foodLevel + n;
		}
 		
 		else if (nextFoodLevel > MAX_FOODLEVEL)
 		{
			double value = nextFoodLevel - MAX_HEALTH;
 			foodLevel = MAX_FOODLEVEL;
 			if (this.getHealth() < MAX_HEALTH)
 			{
 				double missingHealth = MAX_HEALTH - this.getHealth();
 				if (missingHealth < value)
 				{
 					this.setHealth(MAX_HEALTH);
 				}
 				else
 				{
 	 				this.setHealth(getHealth() + value);			
 				}
 			}
 		}	
 	}
 
	/**
	 * Método que cambia la orientación del jugador
	 * @param x diferencia con coordenada x actual
	 * @param y diferencia con coordenada y actual
	 * @param z diferencia con coordenada z actual
	 * @return objeto Location que indica la posición hacia la que está oriendado el jugador
	 * @throws EntityIsDeadException Excepción que indica que jugador está muerto
	 * @throws BadLocationException Excepción que indica posición incorrecta
	 */
	public Location orientate(int x, int y, int z) throws EntityIsDeadException, BadLocationException
	{
		Location orientated = this.getLocation();
		if (!isDead())
		{
			if (!((x == 0) && (y == 0) && (z == 0)))
			{
				if ((x >= -1) && (x <= 1) && (y >= -1) && (y <= 1) && (z >= -1) && (z <= 1))
				{
					orientation = new Location(this.location.getWorld(), x, y, z);
				}
				
				else
				{
					throw new BadLocationException("Orientado a posición no adyacente");
				}
			}
			
			else
			{
				throw new BadLocationException("Orientado a Player");
			}
		}
		
		else
		{
			throw new EntityIsDeadException();
		}
		
		return new Location(orientated.add(orientation));
	}

	/**
	 * Método toString
	 * @return String que imprime información del jugador
	 */
	public String toString()
	{
		String linea1 = "Name=" + name + "\n";
		String linea2 = location + "\n";
		String linea3 = "Orientation=" + orientation + "\n";
		String linea4 = "Health=" + this.getHealth() + "\n";
		String linea5 = "Food level=" + foodLevel + "\n";
		String linea6 = "Inventory=" + inventory;
		
		return linea1 + linea2 + linea3 + linea4 + linea5 + linea6;
	}

	/**
	 * Método hashCode
	 * @return result resultado
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		long temp;
		temp = Double.doubleToLongBits(foodLevel);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((inventory == null) ? 0 : inventory.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((orientation == null) ? 0 : orientation.hashCode());
		result = prime * result + symbol;
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
		if (!super.equals(obj))
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		Player other = (Player) obj;
		if (Double.doubleToLongBits(foodLevel) != Double.doubleToLongBits(other.foodLevel))
		{
			return false;
		}
		if (inventory == null)
		{
			if (other.inventory != null)
			{
				return false;
			}
		} else if (!inventory.equals(other.inventory))
		{
			return false;
		}
		if (name == null)
		{
			if (other.name != null)
			{
				return false;
			}
		} else if (!name.equals(other.name))
		{
			return false;
		}
		if (orientation == null)
		{
			if (other.orientation != null)
			{
				return false;
			}
		} else if (!orientation.equals(other.orientation))
		{
			return false;
		}
		if (symbol != other.symbol)
		{
			return false;
		}
		return true;
	}
}
