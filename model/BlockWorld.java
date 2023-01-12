package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

import model.entities.Creature;
import model.entities.Player;
import model.exceptions.BadInventoryPositionException;
import model.exceptions.BadLocationException;
import model.exceptions.EntityIsDeadException;
import model.exceptions.StackSizeException;
import model.exceptions.WrongMaterialException;

/**
 * Clase que representa el juego y sus funcionalidades
 * @author JV
 */
public class BlockWorld
{
	/**
	 * Objeto de clase World
	 */
	private World world;
	
	/**
	 * Instancia de BlockWorld
	 */
	private static BlockWorld instance;
	
	/**
	 * Getter
	 * @return instancia
	 */
	public static BlockWorld getInstance()
	{
		if (instance == null)
		{
			instance = new BlockWorld();
		}
		
		return instance;
	}
	
	/**
	 * Constructor
	 */
	private BlockWorld()
	{
		world = null;
	}
	
	/**
	 * Método para generar el mundo
	 * @param seed semilla
	 * @param size tamaño del mundo
	 * @param name nombre del mundo
	 * @return mundo creado
	 */
	public World createWorld(int seed, int size, String name)
	{
		world = new World(seed, size, name);
		
		return world;
	}
	
	/**
	 * Método que imprime la información del jugador y su alrededor
	 * @param player objeto Player
	 * @return String con la información
	 */
	public String showPlayerInfo(Player player)
	{
		String str = "";
		World temp_world;
		
		try
		{
			temp_world = player.getLocation().getWorld();
			str = player.toString() + "\n" + temp_world.getNeighbourhoodString(player.getLocation());
		} 
		
		catch (BadLocationException e)
		{
			e.printStackTrace();
		}
		
		return str;
	}
	
	/**
	 * Método que mueve al jugador a las coordenadas de parámetros
	 * @param p objeto Player
	 * @param dx coordenada x
	 * @param dy coordenada y
	 * @param dz coordenada z
	 * @throws EntityIsDeadException excepción porque el jugador está muerto
	 * @throws BadLocationException excepción porque la localización es incorrecta
	 */
	public void movePlayer(Player p, int dx, int dy, int dz) throws EntityIsDeadException, BadLocationException
	{
		try
		{
			Location new_loc = new Location(p.move(dx, dy, dz));
			ItemStack item = world.getItemsAt(new_loc);
			
			try		
			{
				Block block = (world.getBlockAt(new_loc));
				if (block != null)
				{
					if (block.getType().isLiquid())
					{
						double dmg = block.getType().getValue();
						world.getPlayer().setHealth(world.getPlayer().getHealth() - dmg);
					}
				}
				if (item != null)
				{
					p.addItemsToInventory(item);
					world.removeItemsAt(new_loc);
				}
			} 
			
			catch (BadLocationException e)
			{
				e.printStackTrace();
			}
		} 
		
		catch (EntityIsDeadException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Método que equipa un item del inventeario en la mano del jugador
	 * @param player objeto Player
	 * @param pos posicion del item en el inventario (indice 0)
	 * @throws BadInventoryPositionException excepción porque la posición del inventario no es válida
	 */
	public void selectItem(Player player, int pos) throws BadInventoryPositionException
	{
		try
		{
			player.selectItem(pos);
		}
		
		catch (BadInventoryPositionException | EntityIsDeadException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Método para usar el item que tiene el jugador en la mano
	 * @param p objeto Player
	 * @param times veces que usa el objeto
	 * @throws EntityIsDeadException excepción porque el jugador está muerto
	 * @throws IllegalArgumentException excepción porque el parámetro times no es válido
	 * @throws StackSizeException excepción porque crea un item con cantidad incorrecta
	 * @throws BadLocationException excepción que indica una posición incorrecta
	 * @throws WrongMaterialException excepción porque se intenta crear un bloque con material incorrecto
	 */
	public void useItem(Player p, int times) throws EntityIsDeadException, IllegalArgumentException, StackSizeException, BadLocationException, WrongMaterialException
	{
		try
		{
			ItemStack itemInHand = p.useItemInHand(times);
			if (itemInHand != null)
			{
				Location oriented = new Location(p.getOrientation());
				if (!itemInHand.getType().isEdible() && (oriented.isOffLimits()))
				{
					try
					{
						Block block = world.getBlockAt(oriented);
						Creature creature = world.getCreatureAt(oriented);
						if (block != null)
						{
							if (!block.getType().isLiquid())
							{
								hitBlock(p, times, oriented, block, itemInHand);
							}
						}
						
						else if (creature != null)
						{
							attackCreature(p, times, oriented, creature, itemInHand);							
						}
						
						else if (((block == null) || (block.getType().isLiquid())) && (creature == null) && (itemInHand.getType().isBlock()))
						{
							Block blockInHand = BlockFactory.createBlock(itemInHand.getType());
							world.addBlock(oriented, blockInHand);
						}
					} 
					
					catch (BadLocationException | WrongMaterialException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		
		catch (EntityIsDeadException | IllegalArgumentException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Método que se utiliza cuando el jugador golpea un bloque sólido
	 * @param p player
	 * @param times veces que golpea
	 * @param loc posición
	 * @param block bloque
	 * @param itemInHand objeto en mano
	 * @throws BadLocationException Excepción que indica que la posición es incorrecta
	 */
	public void hitBlock(Player p, int times, Location loc, Block block, ItemStack itemInHand) throws BadLocationException
	{
		double dmg = 0;
		
		if (itemInHand.getType().isBlock())
		{
			dmg = 0.1 * times;
		}
		
		else
		{
			dmg = itemInHand.getType().getValue() * times;
		}
		
		if (dmg >= block.getType().getValue())
		{
			try
			{
				ItemStack item = (((SolidBlock)block).getDrops());
				world.destroyBlockAt(loc);
				if (item != null)
				{
					world.addItems(loc, item);
				}
			} 
			
			catch (BadLocationException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Métood que se utiliza cuando el jugador ataca una criatura
	 * @param p player
	 * @param times veces que ataca
	 * @param loc posición
	 * @param creature criatura
	 * @param itemInHand objeto en la mano
	 * @throws StackSizeException Excepción que indica que la cantidad de items es incorrecta
	 * @throws BadLocationException Excepción que indica que la posición es incorrecta
	 */
	public void attackCreature(Player p, int times, Location loc, Creature creature, ItemStack itemInHand) throws StackSizeException, BadLocationException
	{
		double dmg = 0;
		
		if (itemInHand.getType().isBlock())
		{
			dmg = 0.1 * times;
		}
		
		else
		{
			dmg = itemInHand.getType().getValue() * times;
		}
		
		if (dmg >= creature.getHealth())
		{
			ItemStack item = null;
			if (creature.getSymbol() == 'L')
			{
				try
				{
					item = new ItemStack(Material.BEEF, 1);
				} 
				catch (StackSizeException e)
				{
					e.printStackTrace();
				}
			}
			
			try
			{
				world.killCreature(loc);
			} 
			catch (BadLocationException e)
			{
				e.printStackTrace();
			}
			
			if (item != null)
			{
				try
				{
					world.addItems(loc, item);
				} 
				catch (BadLocationException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		else
		{
			creature.damage(dmg);
			if (creature.getSymbol() == 'M')
			{
				double monster_dmg = 0.5 * times;
				p.setHealth(p.getHealth() - monster_dmg);				
			}
		}
	}
	
	/**
	 * Método que orienta la jugador a una posición
	 * @param p player
	 * @param x coordenada x
	 * @param y coordenada y
	 * @param z coordenada z
	 * @throws EntityIsDeadException excepción que indica que el jugador está muerto
	 * @throws BadLocationException excepción que indica que la posición es incorrecta
	 */
	public void orientatePlayer(Player p, int x, int y, int z) throws EntityIsDeadException, BadLocationException
	{
		try
		{
			p.orientate(x, y, z);
		} 
		catch (EntityIsDeadException | BadLocationException e)
		{
			e.printStackTrace();
		}
	}
	
	// TODO
	/**
	 * Método que ejecuta las ordenes del archivo
	 * @param sc objeto Scnaner
	 */
	
	private void play(Scanner sc)
	{
		
	}
	
	// TODO
	/**
	 * Método que abre el fichero de entrada
	 * @param str nombre fichero
	 * @throws java.io.FileNotFoundException Excepción que indica que no se encuentra el fichero
	 */
	public void playFile(String str) throws java.io.FileNotFoundException
	{
		File file = new File(str);
		FileReader fr = new FileReader(file);	
		BufferedReader reader = new BufferedReader(fr);	
		
		if (file.exists())
		{
		}
	}
	
	// TODO
	/**
	 * Método para leer las ordenes del juego
	 */
	public void playFromConsole()
	{
		
	}
}
