package model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.lang.Character;

import org.bukkit.util.noise.CombinedNoiseGenerator;
import org.bukkit.util.noise.OctaveGenerator;
import org.bukkit.util.noise.PerlinOctaveGenerator;

import model.entities.Animal;
import model.entities.Creature;
import model.entities.LivingEntity;
import model.entities.Monster;
import model.entities.Player;
import model.exceptions.BadLocationException;
import model.exceptions.StackSizeException;
import model.exceptions.WrongMaterialException;

/**
 * Clase que trabaja con el mundo
 * @author Jose MV
 */
public class World 
{		
	/**
	 * name Nombre del mundo
	 */
	private String name;
	
	/**
	 * Atributo que establece el tamaño del mundo
	 */
	private int worldSize;
	
	/**
	 * Semilla que genera el mundo
	 */
	private long seed;
	
	/**
	 * Mapa que indica la relación entre las localizaciones y los bloques que ahi se encuentran
	 */
	private Map<Location, Block> blocks;
	
	/**
	 * Mapa que indica la relación entre las localizaciones y los items que ahi se encuentran
	 */
	private Map<Location, ItemStack> items;
	
	/**
	 * Mapa que indica la relación entre las localizaciones y las criaturas
	 */
	private Map<Location, Creature> creatures;
	
	/**
	 * Establece el jugador del mundo
	 */
	private Player player;
	
	/**
	 * @deprecated no usar a partir de la práctica 2
	 * @param name nombre del mundo
	 */
	public World(String name)
	{
		this.name = name;
	}
	
	/**
	 * Constructor
	 * @param seed semilla
	 * @param size tamaño del mundo
	 * @param name nombre del mundo
	 * @throws IllegalArgumentException excepción que indica que el parámetro size no es válido
	 */
	public World(long seed, int size, String name) throws IllegalArgumentException
	{
		if (size < 1)
		{
			throw new IllegalArgumentException();
		}
		
		else
		{
			blocks = new HashMap<Location, Block>();
			items = new HashMap<Location, ItemStack>();
			creatures = new HashMap<Location, Creature>();
			this.name = name;
			this.seed = seed;
			worldSize = size;
			generate(seed, size);
		}
	}
	
	/**
	 * Clase que trabaja con la altura del mundo
	 */
	class HeightMap 
	{
		/**
		 * Altura del mundo
		 */
		double[][] heightMap;
		
		/**
		 * Límite de coordenada positiva de altura
		 */
    	int positiveWorldLimit; 
    	
    	/**
    	 * Límite de coordenada negativa de altura
    	 */
    	int negativeWorldLimit;
    	
    	/**
    	 * Constructor
    	 * @param worldsize tamaño del mundo
    	 */
		HeightMap(int worldsize) 
		{
			heightMap = new double[worldsize][worldsize];
			positiveWorldLimit  = worldsize/2;
			negativeWorldLimit = (worldsize % 2 == 0) ? -(positiveWorldLimit-1) : -positiveWorldLimit;
		}
		
		/**
		 * Getter
		 * @param x coordenada 'x' entre 'positiveWorldLimit' y 'negativeWorldLimit'
		 * @param z coordenada 'z' entre 'positiveWorldLimit' y 'negativeWorldLimit'
		 * @return altura del mundo
		 */
		double get(double x, double z) 
		{
			return heightMap[(int)x - negativeWorldLimit][(int)z - negativeWorldLimit];
		}
		
		/**
		 * Setter
		 * @param x coordenada x
		 * @param z coordenada y
		 * @param y coordenada z
		 */
		void set(double x, double z, double y) 
		{
			heightMap[(int)x - negativeWorldLimit][(int)z - negativeWorldLimit] = y;
		}

	}	
	
	/**
	 * Coordenadas 'y' de la superficie del mundo. Se inicializa en generate() y debe actualizarse
	 * cada vez que el jugador coloca un nuevo bloque en una posición vacía
	 * Puedes usarlo para localizar el bloque de la superficie de tu mundo.
	 */
	private HeightMap heightMap;

	/**
     * Genera un mundo nuevo del tamaño size*size en el plano (x,z). Si existían elementos anteriores en el mundo,  
     * serán eliminados. Usando la misma semilla y el mismo tamaño podemos generar mundos iguales
     * @param seed semilla para el algoritmo de generación. 
     * @param size tamaño del mundo para las dimensiones x y z
     */
    private  void generate(long seed, int size) {
    	
    	Random rng = new Random(getSeed());

    	blocks.clear();
    	creatures.clear();
    	items.clear();
    	
    	// Paso 1: generar nuevo mapa de alturas del terreno
    	heightMap = new HeightMap(size);
    	CombinedNoiseGenerator noise1 = new CombinedNoiseGenerator(this);
    	CombinedNoiseGenerator noise2 = new CombinedNoiseGenerator(this);
    	OctaveGenerator noise3 = new PerlinOctaveGenerator(this, 6);
    	
    	System.out.println("Generando superficie del mundo...");
    	for (int x=0; x<size; x++) {
    		for (int z=0; z<size; z++) {
    	    	double heightLow = noise1.noise(x*1.3, z*1.3) / 6.0 - 4.0;
    	    	double heightHigh = noise2.noise(x*1.3, z*1.3) / 5.0 + 6.0;
    	    	double heightResult = 0.0;
    	    	if (noise3.noise(x, z, 0.5, 2) / 8.0 > 0.0)
    	    		heightResult = heightLow;
    	    	else
    	    		heightResult = Math.max(heightHigh, heightLow);
    	    	heightResult /= 2.0;
    	    	if (heightResult < 0.0)
    	    		heightResult = heightResult * 8.0 / 10.0;
    	    	heightMap.heightMap[x][z] = Math.floor(heightResult + Location.SEA_LEVEL);
    		}
    	}
    	
    	// Paso 2: generar estratos
    	SolidBlock block = null;
    	Location location = null;
    	Material material = null;
    	OctaveGenerator noise = new PerlinOctaveGenerator(this, 8);
    	System.out.println("Generando terreno...");
    	for (int x=0; x<size; x++) {
    		for (int z=0; z<size; z++) {
    	    	double dirtThickness = noise.noise(x, z, 0.5, 2.0) / 24 - 4;
    	    	double dirtTransition = heightMap.heightMap[x][z];
    	    	double stoneTransition = dirtTransition + dirtThickness;
    	    	for (int y=0; y<= dirtTransition; y++) {
    	    		if (y==0) material = Material.BEDROCK;
    	    		else if (y <= stoneTransition) 
    	    			material = Material.STONE;
    	    		else // if (y <= dirtTransition)
    	    			material = Material.DIRT;
					try {
						location = new Location(this,x+heightMap.negativeWorldLimit,y,z+heightMap.negativeWorldLimit);
						block = new SolidBlock(material);
						if (rng.nextDouble() < 0.5) // los bloques contendrán item con un 50% de probabilidad
							block.setDrops(block.getType(), 1);
						blocks.put(location, block);
					} catch (WrongMaterialException | StackSizeException e) {
						// Should never happen
						e.printStackTrace();
					}
    	    	}

    		}
    	}
    	
    	// Paso 3: Crear cuevas
    	int numCuevas = size * size * 256 / 8192;
		double theta = 0.0;
		double deltaTheta = 0.0;
		double phi = 0.0;
		double deltaPhi = 0.0;

		System.out.print("Generando cuevas");
    	for (int cueva=0; cueva<numCuevas; cueva++) {
    		System.out.print("."); System.out.flush();
    		Location cavePos = new Location(this,rng.nextInt(size),rng.nextInt((int)Location.UPPER_Y_VALUE), rng.nextInt(size));
    		double caveLength = rng.nextDouble() * rng.nextDouble() * 200;
    		//cave direction is given by two angles and corresponding rate of change in those angles,
    		//spherical coordinates perhaps?
    		theta = rng.nextDouble() * Math.PI * 2;
    		deltaTheta = 0.0;
    		phi = rng.nextDouble() * Math.PI * 2;
    		deltaPhi = 0.0;
    		double caveRadius = rng.nextDouble() * rng.nextDouble();

    		for (int i=1; i <= (int)caveLength ; i++) {
    			cavePos.setX(cavePos.getX()+ Math.sin(theta)*Math.cos(phi));
    			cavePos.setY(cavePos.getY()+ Math.cos(theta)*Math.cos(phi));
    			cavePos.setZ(cavePos.getZ()+ Math.sin(phi));
    			theta += deltaTheta*0.2;
    			deltaTheta *= 0.9;
    			deltaTheta += rng.nextDouble();
    			deltaTheta -= rng.nextDouble();
    			phi /= 2.0;
    			phi += deltaPhi/4.0;
    			deltaPhi *= 0.75;
    			deltaPhi += rng.nextDouble();
    			deltaPhi -= rng.nextDouble();
    			if (rng.nextDouble() >= 0.25) {
    				Location centerPos = new Location(cavePos);
    				centerPos.setX(centerPos.getX() + (rng.nextDouble()*4.0-2.0)*0.2);
    				centerPos.setY(centerPos.getY() + (rng.nextDouble()*4.0-2.0)*0.2);
    				centerPos.setZ(centerPos.getZ() + (rng.nextDouble()*4.0-2.0)*0.2);
    				double radius = (Location.UPPER_Y_VALUE - centerPos.getY()) / Location.UPPER_Y_VALUE;
    				radius = 1.2 + (radius * 3.5 + 1) * caveRadius;
    				radius *= Math.sin(i * Math.PI / caveLength);
    				try {
    					fillOblateSpheroid( centerPos, radius, null);
    				} catch (WrongMaterialException e) {
    					// Should not occur
    					e.printStackTrace();
    				}
    			}

    		}
    	}
    	System.out.println();
    	
    	// Paso 4: crear vetas de minerales
    	// Abundancia de cada mineral
    	double abundance[] = new double[2];
    	abundance[0] = 0.5; // GRANITE
    	abundance[1] =  0.3; // OBSIDIAN
    	int numVeins[] = new int[2];
    	numVeins[0] = (int) (size * size * 256 * abundance[0]) / 16384; // GRANITE
    	numVeins[1] =  (int) (size * size * 256 * abundance[1]) / 16384; // OBSIDIAN

    	Material vein = Material.GRANITE;
    	for (int numVein=0 ; numVein<2 ; numVein++, vein = Material.OBSIDIAN) { 
    		System.out.print("Generando vetas de "+vein);
    		for (int v=0; v<numVeins[numVein]; v++) {
    			System.out.print(vein.getSymbol());
    			Location veinPos = new Location(this,rng.nextInt(size),rng.nextInt((int)Location.UPPER_Y_VALUE), rng.nextInt(size));
    			double veinLength = rng.nextDouble() * rng.nextDouble() * 75 * abundance[numVein];
    			//cave direction is given by two angles and corresponding rate of change in those angles,
    			//spherical coordinates perhaps?
    			theta = rng.nextDouble() * Math.PI * 2;
    			deltaTheta = 0.0;
    			phi = rng.nextDouble() * Math.PI * 2;
    			deltaPhi = 0.0;
    			//double caveRadius = rng.nextDouble() * rng.nextDouble();
    			for (int len=0; len<(int)veinLength; len++) {
    				veinPos.setX(veinPos.getX()+ Math.sin(theta)*Math.cos(phi));
    				veinPos.setY(veinPos.getY()+ Math.cos(theta)*Math.cos(phi));
    				veinPos.setZ(veinPos.getZ()+ Math.sin(phi));
    				theta += deltaTheta*0.2;
    				deltaTheta *= 0.9;
    				deltaTheta += rng.nextDouble();
    				deltaTheta -= rng.nextDouble();
    				phi /= 2.0;
    				phi += deltaPhi/4.0;
    				deltaPhi *= 0.9; // 0.9 for veins
    				deltaPhi += rng.nextDouble();
    				deltaPhi -= rng.nextDouble();
    				double radius = abundance[numVein] * Math.sin(len * Math.PI / veinLength) + 1;

    				try {
    					fillOblateSpheroid(veinPos, radius, vein);
    				} catch (WrongMaterialException ex) {
    					// should not ocuur
    					ex.printStackTrace();
    				}
    			}
    		}
    		System.out.println();
    	}
    	
    	System.out.println();

    	// flood-fill water     	
    	char water= Material.WATER.getSymbol();

    	int numWaterSources = size*size/800;
    	
    	System.out.print("Creando fuentes de agua subterráneas");
    	int x = 0;
    	int z = 0;
    	int y = 0;
    	for (int w=0; w<numWaterSources; w++) {
    		System.out.print(water);
    		x = rng.nextInt(size)+heightMap.negativeWorldLimit;
    		z = rng.nextInt(size)+heightMap.negativeWorldLimit;
    		y = (int)Location.SEA_LEVEL - 1 - rng.nextInt(2);
    		try {
				floodFill(Material.WATER, new Location(this,x,y,z));
			} catch (WrongMaterialException | BadLocationException e) {
				// no debe suceder
				throw new RuntimeException(e);
			}
    	}
    	System.out.println();
   
    	System.out.print("Creando erupciones de lava");
    	char lava = Material.LAVA.getSymbol();
    	// flood-fill lava
    	int numLavaSources = size*size/2000;
    	for (int w=0; w<numLavaSources; w++) {
    		System.out.print(lava);
    		x = rng.nextInt(size)+heightMap.negativeWorldLimit;
    		z = rng.nextInt(size)+heightMap.negativeWorldLimit;
    		y = (int)((Location.SEA_LEVEL - 3) * rng.nextDouble()* rng.nextDouble());
    		try {
				floodFill(Material.LAVA, new Location(this,x,y,z));
			} catch (WrongMaterialException  | BadLocationException e) {
				// no debe suceder
				throw new RuntimeException(e);			
			}
    	}
    	System.out.println();

    	// Paso 5. crear superficie, criaturas e items
    	// Las entidades aparecen sólo en superficie (no en cuevas, por ejemplo)

    	OctaveGenerator onoise1 = new PerlinOctaveGenerator(this, 8);
    	OctaveGenerator onoise2 = new PerlinOctaveGenerator(this, 8);
    	boolean sandChance = false;
    	double entitySpawnChance = 0.05;
    	double itemsSpawnChance = 0.10;
    	double foodChance = 0.8;
    	double toolChance = 0.1;
    	double weaponChance = 0.1;
    	
    	System.out.println("Generando superficie del terreno, entidades e items...");
    	for (x=0; x<size; x++) {    		
    		for (z=0; z<size; z++) {
    			sandChance = onoise1.noise(x, z, 0.5, 2.0) > 8.0;
    			y = (int)heightMap.heightMap[(int)x][(int)z];
    			Location surface = new Location(this,x+heightMap.negativeWorldLimit,y,z+heightMap.negativeWorldLimit); // la posición (x,y+1,z) no está ocupada (es AIR)
    			try {
	    			if (sandChance) {
	    				SolidBlock sand = new SolidBlock(Material.SAND);
	    				if (rng.nextDouble() < 0.5)
	    					sand.setDrops(Material.SAND, 1);
	    				blocks.put(surface, sand);
	    			}
	    			else {
	    				SolidBlock grass = new SolidBlock(Material.GRASS);
	    				if (rng.nextDouble() < 0.5)
	    					grass.setDrops(Material.GRASS, 1);
	    				blocks.put(surface, grass);
	    			}
    			} catch (WrongMaterialException | StackSizeException ex) {
    				// will never happen
    				ex.printStackTrace();
    			}
    			// intenta crear una entidad en superficie
    			try {
    				Location aboveSurface = surface.above();
    				
    				if (rng.nextDouble() < entitySpawnChance) {
    					Creature entity =null;
    					double entityHealth = rng.nextInt((int)LivingEntity.MAX_HEALTH)+1;
    					if (rng.nextDouble() < 0.75) // generamos Monster (75%) o Animal (25%) de las veces
    						entity = new Monster(aboveSurface, entityHealth);
    					else 
    						entity = new Animal(aboveSurface, entityHealth);
    					creatures.put(aboveSurface, entity);
    				} else { 
    					// si no, intentamos crear unos items de varios tipos (comida, armas, herramientas)
    					// dentro de cofres
    					Material itemMaterial = null;
    					int amount = 1; // p. def. para herramientas y armas
    					if (rng.nextDouble() < itemsSpawnChance) {
    						double rand = rng.nextDouble();
    						if (rand < foodChance) { // crear comida
    							// hay cuatro tipos de item de comida, en las posiciones 8 a 11 del array 'materiales'
    							itemMaterial = Material.getRandomItem(8, 11);
    							amount = rng.nextInt(5)+1;
    						}
    						else if (rand < foodChance+toolChance)
    							// hay dos tipos de item herramienta, en las posiciones 12 a 13 del array 'materiales'
    							itemMaterial = Material.getRandomItem(12, 13);
    						else
    							// hay dos tipos de item arma, en las posiciones 14 a 15 del array 'materiales'
    							itemMaterial = Material.getRandomItem(14, 15);
    						
    						items.put(aboveSurface, new ItemStack(itemMaterial, amount));
    					}
    				}
    			} catch (BadLocationException | StackSizeException e) {
    				// BadLocationException : no hay posiciones más arriba, ignoramos creación de entidad/item sin hacer nada 
    				// StackSizeException : no se producirá
    				throw new RuntimeException(e);    			}

    		}
    	}

    	// TODO: Crear plantas
    	    	
    	// Generar jugador
    	player = new Player("Steve",this);
    	// El jugador se crea en la superficie (posición (0,*,0)). Asegurémonos de que no hay nada más ahí
    	Location playerLocation = player.getLocation();
    	creatures.remove(playerLocation);
    	items.remove(playerLocation);
    	
    }
	
    /**
     * Where fillOblateSpheroid() is a method which takes a central point, a radius and a material to fill to use on the block array.
     * @param centerPos central point
     * @param radius radius around central point
     * @param material material to fill with
     * @throws WrongMaterialException if 'material' is not a block material
     */
    private void fillOblateSpheroid(Location centerPos, double radius, Material material) throws WrongMaterialException {
    	
				for (double x=centerPos.getX() - radius; x< centerPos.getX() + radius; x += 1.0) {					
					for (double y=centerPos.getY() - radius; y< centerPos.getY() + radius; y += 1.0) {
						for (double z=centerPos.getZ() - radius; z< centerPos.getZ() + radius; z += 1.0) {
							double dx = x - centerPos.getX();
							double dy = y - centerPos.getY();
							double dz = z - centerPos.getZ();
							
							if ((dx*dx + 2*dy*dy + dz*dz) < radius*radius) {
								// point (x,y,z) falls within level bounds ?
								// we don't need to check it, just remove or replace that location from the blocks map.
								Location loc = new Location(this,Math.floor(x+heightMap.negativeWorldLimit),Math.floor(y),Math.floor(z+heightMap.negativeWorldLimit));
								if (material==null)
									blocks.remove(loc);
								else try { //if ((Math.abs(x) < worldSize/2.0-1.0) && (Math.abs(z) < worldSize/2.0-1.0) && y>0.0 && y<=Location.UPPER_Y_VALUE)
									SolidBlock veinBlock = new SolidBlock(material);
									// los bloques de veta siempre contienen material
									veinBlock.setDrops(material, 1);
									blocks.replace(loc, veinBlock);
								} catch  (StackSizeException ex) {
									// will never happen
									ex.printStackTrace();
								}
							}
						}
					}
				}
	}

    /**
     * Método que inunda posiciones de líquido
     * @param liquid liquido
     * @param from posición
     * @throws WrongMaterialException excepción que indica material incorrecto
     * @throws BadLocationException excepción que indica posición incorrecta
     */
    private void floodFill(Material liquid, Location from) throws WrongMaterialException, BadLocationException {
    	if (!liquid.isLiquid())
    		throw new WrongMaterialException(liquid);
    	if (!blocks.containsKey(from))
    	{
    		blocks.put(from, BlockFactory.createBlock(liquid));
    		items.remove(from);
    		Set<Location> floodArea = getFloodNeighborhood(from);
    		for (Location loc : floodArea) 
    			floodFill(liquid, loc);
    	}
    }
    
	/**
	 * Obtiene las posiciones adyacentes a esta que no están por encima y están libres
	 * @param location posición 
	 * @return si esta posición pertenece a un mundo, devuelve sólo aquellas posiciones adyacentes válidas para ese mundo,  si no, devuelve todas las posiciones adyacentes
	 * @throws BadLocationException cuando la posición es de otro mundo
	 */
	private Set<Location> getFloodNeighborhood(Location location) throws BadLocationException {
		if (location.getWorld() !=null && location.getWorld() != this)
			throw new BadLocationException("Esta posición no es de este mundo");
		Set<Location> neighborhood = location.getNeighborhood();
		Iterator<Location> iter = neighborhood.iterator();
		while (iter.hasNext()) {
			Location loc = iter.next();
			try {
				if ((loc.getY() > location.getY()) || getBlockAt(loc)!=null)
					iter.remove();
			} catch (BadLocationException e) {
				throw new RuntimeException(e);
				// no sucederá
			}
		}
		return neighborhood;
	}
  
	// ------ GETTERS ------
	
   /**
    * Getter
    * @return worldSize tamaño del mundo
    */
	public int getSize()
	{
		return worldSize;
	}
	
	/**
	 * Getter
	 * @return seed semilla del mundo
	 */
	public long getSeed()
	{
		return seed;
	}
	
	/**
	 * Getter
	 * @return nombre del mundo
	 */
	public String getName()
	{
		return name;
    }
    
	/**
	 * Getter
	 * @return player jugador
	 */
	public Player getPlayer()
	{
		return player;
	}
	
	/**
	 * Getter
	 * @param loc posición
	 * @return creature devuelve la criatura en la posición de parámetro
	 * @throws BadLocationException Excepción que indica error en la posición
	 */
	public Creature getCreatureAt(Location loc) throws BadLocationException
	{
		Creature creature  = null;
		
		if (loc != null)
		{
			if (loc.getWorld() != null)
			{
				if (checkWorld(loc))
				{
					creature = creatures.get(loc);
				}
				
				else
				{
					throw new BadLocationException("Bad Location in getCreatureAt");
				}
			}
			
			else
			{
				throw new BadLocationException("Null World in getCreatureAt");
			}
		}
			
		else
		{
			throw new BadLocationException("Null Location in getCreatureAt");
		}
		
		return creature;		
	}
	
	/**
	 * Getter
	 * @param loc Localizacion del bloque
	 * @return bloque que se encuentra en la localización de parámetro
	 * @throws BadLocationException excepción que indica que la localización no es válida
	 */
	public Block getBlockAt(Location loc) throws BadLocationException
	{
		Block block = null;
		
		if (loc.getWorld() != null)
		{
			if (checkWorld(loc))
			{
				block = blocks.get(loc);
			}
			
			else
			{
				throw new BadLocationException("Bad Location in getBlockAt");
			}
		}
		
		else
		{
			throw new BadLocationException("Null World in getBlockAt");
		}
		
		return block;
	}
	
	/**
	 * Getter
	 * @param ground Localización
	 * @return highestLoc indica el bloque en más altitud de la localización dada
	 * @throws BadLocationException excepción que indica que la localización no es válida
	 */
	public Location getHighestLocationAt(Location ground) throws BadLocationException
	{		
		Location highestLoc = new Location(ground);
		
		if (ground.getWorld() != null)
		{
			if (checkWorld(ground))
			{
				double y = heightMap.get(ground.getX(),ground.getZ());
				highestLoc.setY(y);
			}
			
			else
			{
				throw new BadLocationException("Bad Location in getHighestLocationAt");
			}
		}
		
		else
		{
			throw new BadLocationException("Null World in getHighestLocationAt");
		}
		
		return highestLoc;
	}
	
	/**
	 * Getter
	 * @param loc Localización
	 * @return item que se encuentra en la localización dada
	 * @throws BadLocationException excepción que indica que la localización no es válida
	 */
	public ItemStack getItemsAt(Location loc) throws BadLocationException
	{
		ItemStack item = null;
		
		if (this == loc.getWorld())
		{
			item = items.get(loc);
		}
		
		else
		{
			throw new BadLocationException("Bad location in getItemsAt");
		}
		
		return item;
	}
	
	/**
	 * Getter
	 * @param loc Localización
	 * @return str String con los símbolos de lo que se encuentra alrededor de la localización
	 * @throws BadLocationException excepción que indica que la localización no es válida
	 */
	public String getNeighbourhoodString(Location loc) throws BadLocationException
	{
		String str = "";
		char temp_char;
		Block temp_block;
		ItemStack temp_item;
		Creature temp_creature;
		Location temp_loc;
		
		if (!loc.getWorld().equals(this))
		{
			throw new BadLocationException("Bad Location in getNeighbourhoodString");
		}
		
		else
		{
			for (int i = -1; i < 2; i++) 			// Z
			{
				for (int j = 1; j > -2; j--)		// Y
				{
					for (int l = -1; l < 2; l++)	// X
					{
						temp_loc = new Location(loc.getWorld(), loc.getX() + l, loc.getY() + j, loc.getZ() + i);
						if (!Location.check(temp_loc))
						{
							str += "X"; // Fuera del mundo
						}					
						
						else if (player.getLocation().equals(temp_loc))
						{
							str += "P"; // Player
						}
						
						else
						{							
							temp_block = blocks.get(temp_loc); // Crea bloque con location como clave del mapa							
							temp_item = items.get(temp_loc); // Crea item con location como clave del mapa
							temp_creature = creatures.get(temp_loc); // Crea criatura con location como clave del mapa
							
							if ((temp_block != null) && (temp_item == null) && (temp_creature == null))
							{							
								str += temp_block.getType().getSymbol();							
							}
							
							else if (temp_item != null)
							{
								temp_char = temp_item.getType().getSymbol();
								temp_char = Character.toUpperCase(temp_char);
								str += temp_char;
							}
							
							else if (temp_creature != null)
							{
								temp_char = temp_creature.getSymbol();
								temp_char = Character.toUpperCase(temp_char);
								str += temp_char;
							}
							
							else
							{
								str += ".";
							}
						}
					}
					if (j > -1)
					{
						str += " ";
					}
				}
				if (i < 1)
				{
					str += "\n";					
				}
			}
		}
		
		return str;
	}
	
	/**
	 * Getter
	 * @param loc posición
	 * @return colección de criaturas adyacentes a la posición dada
	 * @throws BadLocationException indica que la posición no es correcta
	 */
	public Collection<Creature> getNearbyCreatures(Location loc) throws BadLocationException
	{
    	Collection<Creature> nearbyCreatures = new HashSet<Creature>();
    	Location temp_loc;
    	double temp_x, temp_y, temp_z = 0;
    	if (loc != null)
    	{
    		if (loc.getWorld() != null)
    		{
	    		if (checkWorld(loc))
	    		{
			    	for (int i = -1; i < 2; i++) // z
			    	{
			    		for (int j = 1; j > -2; j--) // y
			    		{
			    			for (int l = -1; l < 2; l++) // x
			    			{
			    				if ((i != 0) || (j != 0) || (l != 0))
			    				{
			    					temp_x = loc.getX() + i;
			    					temp_y = loc.getY() + j;
			    					temp_z = loc.getZ() + l;
			    					temp_loc = new Location(loc.getWorld(), temp_x, temp_y, temp_z);
			    					
			    					if ((loc.getWorld() == null && (creatures.containsKey(temp_loc))))
			    					{
			    						nearbyCreatures.add(creatures.get(temp_loc));    						
			    					}
			    					
			    					else if ((checkWorld(temp_loc)) && (creatures.containsKey(temp_loc)))
			    					{
			    						nearbyCreatures.add(creatures.get(temp_loc));    						
			    					}
			    				}
			    			}
			    		}
			    	}
	    		}
	        	
	        	else
	        	{
	        		throw new BadLocationException("No es de este mundo");
	        	}
    		}
        	
        	else
        	{
        		throw new BadLocationException("World es null");
        	}
    	}
    	
    	else
    	{
    		throw new BadLocationException("Loc es null");
    	}
    	
    	return nearbyCreatures;
	}
	
	/**
	 * Método que comprueba si la localización está libre de bloques sólidos y entidades
	 * @param loc Localización
	 * @return isFree indica si está libre o no
	 * @throws BadLocationException excepción que indica que la localización no es válida
	 */
	public boolean isFree(Location loc) throws BadLocationException
	{
		boolean isFree = false;
		
		if (checkWorld(loc))
		{
			isFree = loc.isFree();
		}
		
		else
		{
			throw new BadLocationException("Bad Location in isFree");
		}
		
		return isFree;
	}

	/**
	 * Método que elimina los items en la localización dada
	 * @param loc localización
	 * @throws BadLocationException excepción que indica que la localización no es válida
	 */
    public void removeItemsAt(Location loc) throws BadLocationException
    {
    	if ((this != loc.getWorld()) || (items.get(loc) == null))
    	{
    		throw new BadLocationException("Bad Location in removeItemsAt");
    	}
    	
    	else
    	{
    		items.remove(loc);
    	}
    }
    
    /**
     * Método que compreuba si la localización dada pertenece al mismo mundo
     * @param loc localización
     * @return same indica si es el mismo o no
     */
    public boolean checkWorld(Location loc)
    {
    	boolean same = false;
    	
		if (loc.getWorld().equals(this))
		{
			same = true;
		}    	
		
		return same;
    }
	
    /**
     * Método que añade un bloque en la posición
     * @param loc posición
     * @param block bloque
     * @throws BadLocationException indica que la posición de parámetro no es correcta
     */
    public void addBlock(Location loc, Block block) throws BadLocationException
    {
    	if (loc != null)
    	{
	    	if ((checkWorld(loc)) && (loc.getWorld() != null))
	    	{
	    		if (Location.check(loc) && (!player.getLocation().equals(loc)))
	    		{
		    		items.remove(loc);
		    		creatures.remove(loc);
		    		blocks.put(loc, block);
		    		heightMap.set(loc.getX(), loc.getZ(), loc.getY());
	    		}
	    		
	    		else
	        	{
	        		throw new BadLocationException("Loca no válida");
	        	}
	    	}
	    	
	    	else
	    	{
	    		throw new BadLocationException("Loc no válida");
	    	}
    	}
    	
    	else
    	{
    		throw new BadLocationException("Loc es null");
    	}
    }
    
    /**
     * Método que añade un item en la posición
     * @param loc posición
     * @param is item
     * @throws BadLocationException indica que la posició no es correcta
     */
    public void addItems(Location loc, ItemStack is) throws BadLocationException
    {
		try 
		{
			if (isFree(loc))
			{
				items.remove(loc);
				items.put(loc, is);
			}
			
			else
			{
				throw new BadLocationException("Bad location in addItems");
			}
		}
		
		catch (BadLocationException e)
		{
			e.printStackTrace();
		}
    }
    
    /**
     * Método que añade una criatura en la posición
     * @param creature criatura
     * @throws BadLocationException excepción que indica que la posición esta ocupada o fuera de los límites
     */
    public void addCreature(Creature creature) throws BadLocationException
    {    	
    	if ((creature.getLocation() != null) && (creature.getLocation().getWorld() != null))
    	{
        	Location starting_loc = new Location(creature.getLocation());
	    	if (isFree(starting_loc))
	    	{
	    		if (items.containsKey(starting_loc))
	    		{
	    			items.remove(starting_loc);
	    			creatures.put(starting_loc, creature);
	    		}
	    		
	    		else
	    		{
	    			creatures.put(starting_loc, creature);
	    		}
	    	}
	    	
	    	else
	    	{
	    		throw new BadLocationException("Location is not empty or offlimits");
	    	}
    	}
	    	
    	else
    	{
    		throw new BadLocationException("Location is null");
    	}
    }

    /**
     * Método que elimina un bloque en la posición dada
     * @param loc posición
     * @throws BadLocationException Excepción que indica error en la posición
     */
    public void destroyBlockAt(Location loc) throws BadLocationException
    {
    	if (loc != null)
    	{
			if (checkWorld(loc))
			{
				if ((blocks.containsKey(loc)) && (loc.getY() != 0))
				{
					if (blocks.get(loc).getType().isLiquid())
					{
						blocks.remove(loc);
			    		heightMap.set(loc.getX(), loc.getZ(), loc.getY() - 1);						
					}
					
					else
					{
						Block temp_block = blocks.get(loc);
						ItemStack temp_item = ((SolidBlock)temp_block).getDrops();
						items.put(loc, temp_item);
						blocks.remove(loc);
			    		heightMap.set(loc.getX(), loc.getZ(), loc.getY() - 1);							
					}
				}
				
				else
				{
		    		throw new BadLocationException("Bad location in destroyBlockAt");
		    	}    		
			}
			
			else 
			{
				throw new BadLocationException("Bad location in destroyBlockAt");
			}
    	}
		
		else 
		{
			throw new BadLocationException("Bad location in destroyBlockAt");
		}
    }
    
    /**
     * Método que elimina la criatura en la posición dada
     * @param loc posición
     * @throws BadLocationException Excepción que indica error en la posición
     */
    public void killCreature(Location loc) throws BadLocationException
    {
    	if (loc != null)
    	{
	    	if ((checkWorld(loc)) && (creatures.containsKey(loc)))
	    	{
	    		creatures.remove(loc);
	    	}
	    	
	    	else
	    	{
	    		throw new BadLocationException("Bad location in killCreature");
	    	}
    	}
    	
    	else
    	{
    		throw new BadLocationException("Bad location in killCreature");
    	}
    }
    
    /**
     * Método toString
     * @return String imprime nombre dle mundo
     */
    public String toString()
    {
        return name;
    }
	
    /**
     * Método hashCode
     * @return result resultado
     */
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (int) (seed ^ (seed >>> 32));
		result = prime * result + worldSize;
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
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		World other = (World) obj;
		if (name == null)
		{
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (seed != other.seed)
			return false;
		if (worldSize != other.worldSize)
			return false;
		return true;
	}
}