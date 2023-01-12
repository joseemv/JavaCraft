package model;

import java.util.HashSet;
import java.util.Set;

import model.exceptions.BadLocationException;

/**
 * Clase para trabajar con las localizaciones
 * @author Jose MV
 */
public class Location
{
	/** 
	 * Indica elevación máxima
	 */
    public static final double UPPER_Y_VALUE = 255.0;
    
    /** 
     * Indica nivel del mar
     */
    public static final double SEA_LEVEL = 63.0;

    /**
     * Objeto de la clase World
     */
    private World world;
    
    /**
     * Coordenada de longitud
     */
    private double x;
    
    /** 
     * Coordenada de elevación
     */
    private double y;
    
    /** 
     * Coordenada de latitud 
     */
    private double z;

    /**
     * Constructor: crea la localización
     * @param w es el objeto World
     * @param x	es la coordenada de longitud
     * @param y es la coordenada de elevación
     * @param z es la coordenada de latitud
     */
    public Location(World w, double x, double y, double z)
    {
        world = w;
        setX(x);
        setY(y);
        setZ(z);
    }
    
    /**
     * Constructor de copia
     * @param loc objeto Location a copiar
     */
    public Location(Location loc)
    {
        world = loc.world;
        x = loc.x;
        y = loc.y;
        z = loc.z;
    }
    
 // ------ GETTERS ------
    
    /**
     * Getter
     * @return world objeto World
     */
    public World getWorld()
    {
        return world;
    }
    
    /**
     * Getter
     * @return x coordenada x de longitud
     */
    public double getX()
    {
        return x;
    }

    /**
     * Getter
     * @return y coordenada y de elevación
     */
    public double getY()
    {
        return y;
    }

    /**
     * Getter
     * @return z coordenada z de latitud
     */
    public double getZ()
    {
        return z;
    }
    
    /**
     * Getter
     * @return neighborhood contine las localizaciones adyacentes
     */
    public Set<Location> getNeighborhood()
    {
    	Set<Location> neighborhood = new HashSet<Location>();
    	Location temp_loc;
    	double temp_x, temp_y, temp_z = 0;
    	
    	for (int i = -1; i < 2; i++) // z
    	{
    		for (int j = 1; j > -2; j--) // y
    		{
    			for (int l = -1; l < 2; l++) // x
    			{
    				if ((i != 0) || (j != 0) || (l != 0))
    				{
    					temp_x = x + i;
    					temp_y = y + j;
    					temp_z = z + l;
    					temp_loc = new Location(world, temp_x, temp_y, temp_z);
    					
    					if (world == null)
    					{
    						neighborhood.add(temp_loc);	    						
    					}
    					
    					else if (check(temp_loc))
    					{
    						neighborhood.add(temp_loc);    						
    					}
    				}
    			}
    		}
    	}
    	
    	return neighborhood;
    }
    
 // ------ SETTERS ------
    
    /**
     * Setter
     * @param w objeto World
     */
    public void setWorld(World w)
    {
        world = w;
    }
    
    /**
     * Setter
     * @param x coordenada x de longitud
     */
    public void setX(double x)
    {
        this.x = x;
    }

    /**
     * Setter
     * @param y coordenada y de elevación
     */
    public void setY(double y)
    {
        this.y = y;
    }

    /**
     * Setter
     * @param z coordenada z de latitud
     */
    public void setZ(double z)
    {
        this.z = z;
    }

    /**
     * Método que calcula la distacia entre dos objetos Location
     * @param loc objeto Location
     * @return distancia
     */
    public Double distance(Location loc)
    {
        if (loc.getWorld() == null || getWorld() == null)
        {
            System.err.println("Cannot measure distance to a null world");
            return -1.0;
        }

        else if (loc.getWorld() != getWorld())
        {
            System.err.println("Cannot measure distance between " + world.getName() + " and " + loc.world.getName());
            return -1.0;
        }

        double dx = x - loc.x;
        double dy = y - loc.y;
        double dz = z - loc.z;
        
        return Math.sqrt(dx*dx + dy*dy + dz*dz);
    }

    /**
     * Método que calcula la longitud de una localización
     * @return longitud
     */
    public double length()
    {
        return Math.sqrt(x*x + y*y + z*z);
    }

    /**
     * Método que establece las coordenadas a 0
     * @return this devuelve el objeto Location modificado
     */
    public Location zero()
    {
        x = y = z = 0.0;
        
        return this;
    }

    /**
     * Método que obtiene la localización inferior a la actual
     * @return Location con y - 1
     * @throws BadLocationException excepción que indica que la localización no es válida
     */
    public Location below() throws BadLocationException
    {
    	double temp_y = y;
    	if ((world != null) && (y == 0))
    	{
    		throw new BadLocationException("Bad location below");
    	}
    	
    	else
    	{
        	temp_y = y - 1;
    	}
    	
    	return new Location(world, x, temp_y, z);
    }
    
    /**
     * Método que obtiene la localización superior a la actual
     * @return Location con y + 1
     * @throws BadLocationException excepción que indica que la localización no es válida
     */
    public Location above() throws BadLocationException
    {
    	double temp_y = y;
    	if ((world != null) && (y == UPPER_Y_VALUE))
    	{
    		throw new BadLocationException("Bad location above");
    	}
    	
    	else 
    	{
    		temp_y = y + 1;
    	}
    	
    	return new Location(world, x, temp_y, z);  	
    }
    
    /**
     * Método que comprueba si la posición está libre (pertenece a un mundo, no hay entidad ni bloque sólido)
     * @return isFree indica si está libre o no
     */
    public boolean isFree()
    {
    	boolean isFree = true;
    	
    	if (world == null)
    	{
    		isFree = false;
    	}
    	
    	else
    	{
	    	if (world.getPlayer().getLocation().equals(this))
	    	{
	    		isFree = false;
	    	}
	    	
	    	else if (!check(this))
	    	{
	    		isFree = false;
	    	}
	    	
	    	else
	    	{
	    		try 
	    		{
	    			if (world.getBlockAt(this) != null)	    		
		    		{
	    				if (!world.getBlockAt(this).getType().isLiquid())
	    				{	    			
	    					isFree = false;
	    				}
		    		}
	    			
	    			else if (world.getCreatureAt(this) != null)
	    			{
	    				isFree = false;
	    			}
	    		}
	    		
	    		catch (Exception e)
	    		{
	    			e.printStackTrace();
	    		}
	    	}
    	}
    	
    	return isFree;
    }
    
    /**
     * Método que indica si la posición está dentro de los límites del munod
     * @return isOffLimits indica si está dentro o no
     */
    public boolean isOffLimits()
    {
    	boolean isOffLimits = true;
    	
    	if (world == null)
    	{
    		isOffLimits = false;
    		
    		if (!check(this))
	    	{
    			isOffLimits = false;
	    	}
    	}
    	
    	return isOffLimits;
    }
    
    /**
     * Método que compreuba si los atributos del objeto World son válidos
     * @param w objeto de clase World
     * @param x coordenada x
     * @param y coordenada y
     * @param z coordenada z
     * @return correct indica si los parámetros son correctos o no
     */
    public static boolean check(World w, double x, double y, double z)
    {
    	boolean correct = true;
    	
    	if ((y < 0) || (y > UPPER_Y_VALUE))
    	{
    		correct = false;
    	}
    	
    	else
    	{    		
    		if ((w.getSize() % 2) == 0)
			{
    			correct = checkEven(w, x, z);
			}
    		
    		else
    		{
    			correct = checkOdd(w, x, z);
    		}
    	}
    	
    	return correct;
    }
    
   /**
    * Método auxiliar del anterior para mundos con tamaño de número par
    * @param w objeto de clase World
    * @param x coordenada x
    * @param z coordenada z
    * @return correct indica si los parámetros son correctos o no
    */
    public static boolean checkEven(World w, double x, double z)
    {
    	boolean correct = true;
		double neg_limit = (0 - (w.getSize() / 2)) + 1;
		double pos_limit = (w.getSize() / 2);
		
		if ((x < neg_limit) || (z < neg_limit))
		{   
			correct = false;
		}
		
		else if ((x > pos_limit) || (z > pos_limit))
		{
			correct = false;
		}
		
		return correct;
    }
    
   /**
    * Método auxiliar del anterior para mundos con tamaño de número impar
    * @param w objeto de clase World
    * @param x coordenada x
    * @param z coordenada z
    * @return correct indica si los parámetros son correctos o no
    */    
    public static boolean checkOdd(World w, double x, double z)
    {
    	boolean correct = true;
    	double neg_limit = (0 - (w.getSize() / 2));
    	double pos_limit = (w.getSize() / 2);
		
		if ((x < neg_limit) || (z < neg_limit))
		{   
			correct = false;
		}
		
		else if ((x > pos_limit) || (z > pos_limit))
		{
			correct = false;
		}
    	
    	return correct;
    }
    
    /**
     * Método que comprueba si la localización es válida
     * @param loc objeto Location
     * @return correct indica si los parámetros son correctos o no
     */
    public static boolean check(Location loc)
    {
    	boolean correct = true;
    	
    	World world = loc.getWorld();
    	double x = loc.getX();
    	double y = loc.getY();
    	double z = loc.getZ();
    	
    	correct = check(world, x, y, z);
    	
    	return correct;    	
    }    

    /**
     * Método para sumar a un objeto Location
     * @param loc objeto Location
     * @return suma
     */
    public Location add(Location loc)
    {
        if (loc.world != world)
        {
            System.err.println("Cannot add Locations of different worlds.");
        }

        else 
        {
            x += loc.x;
            setY(y + loc.y);
            z += loc.z;
        }

        return this;
    }
    
    /**
     * Método para restar a un objeto Location
     * @param loc localización
     * @return diferencia
     */
    public Location substract(Location loc)
    {
        if (loc.world != world)
        {
            System.err.println("Cannot substract Locations of different worlds.");
        }

        else
        {
            x -= loc.x;
            setY(y - loc.y);
            z -= loc.z;
        }

        return this;
    }
    
    /**
     * Método para multiplicar un objeto Location
     * @param factor  multiplicador
     * @return this producto
     */
    public Location multiply(double factor)
    {
        x *= factor;
        setY(y * factor);
        z *= factor;

        return this;
    }
    
    /**
     * Método toString para imprimir objeto
     * @return String imprime la información de Location
     */
    public String toString()
    {
        String s;

        s = "Location{world=";

        if (world == null)
        {
            s += "NULL";
        }

        else
        {
            s += world;
        }

        s += ",x=" + x + ",y=" + y + ",z=" + z + "}";

        return s;
    }

	/**
	 * Método hashCode
	 * @return result
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((world == null) ? 0 : world.hashCode());
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		Location other = (Location) obj;
		if (world == null)
		{
			if (other.world != null)
				return false;
		} else if (!world.equals(other.world))
			return false;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
			return false;
		return true;
	}
}