import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/* Tank is an abstract class with variations between the 
 implementation of certain methods in 

 */
public abstract class Tank
{

	TankType type; // AI or PlayerTank
	int height; // Dimension
	int width; // Dimension
	int speed; // Tank Speed
	int xLoc; // X Location
	int yLoc; // Y Location
	boolean alive; // Status alive or dead
	ArrayList<Projectile> stockPile = new ArrayList<Projectile>(); // Number of bullets available to fire

	double turretAngle;// Angle of turret in radians, counterclockwise (0 is
						// positive X axis, PI/2 is point up)
	int turretCenterX;
	int turretCenterY;
	int turretTopX;
	int turretTopY;
	int targetX;
	int targetY;

	Direction direction;// Direction tank is facing; will use to determine where
						// to move
	// List of specific movements in x and y directions that need to be added to
	// the tanks location
	// Depends on which key is pressed indicating which direction a tank needs
	// to move in

	int numMoveTries = 0;// Number of times the tank has tried to move
	int tankSlowMultiplier;// ex. 1 is fastest, 3 is 1/3 speed
	
	public Tank()
	{
		tankSlowMultiplier = 2;
		
		height = 28; // Fixed height for all tanks
		width = 56; // Fixed width for all tanks
	}

	// called every cycle
	public void setTurretAngleByTarget(int inTargetX, int inTargetY)
	{
		targetX = inTargetX;
		targetY = inTargetY;
		turretCenterX = xLoc + 25; // should be width / 2
		turretCenterY = yLoc + 25; // should be height / 2
		turretAngle = Math.atan2(-(inTargetY - turretCenterY), inTargetX - turretCenterX);
		turretTopX = (int) (41 * Math.cos(turretAngle)) + turretCenterX;
		turretTopY = (int) (-41 * Math.sin(turretAngle)) + turretCenterY;
	}

	
	public boolean canFire(Arena inArena)
	{
		for (Wall[] r : inArena.walls)
		{
			for (Wall w : r)
			{
				if (w != null)
				{
					if (w.getXLoc() <= turretTopX && w.getXLoc() + 50 >= turretTopX && w.getYLoc() <= turretTopY
							&& w.getYLoc() + 50 >= turretTopY)
					{
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	public void draw(Graphics g, ResourceLibrary l)
	{
		// draw projectiles

		for (Projectile p : stockPile)
		{
			p.draw(g, l);
		}

		//if (type.equals(TankType.INVISIBLE) == false)
		{
			if (alive)
			{
				// set up image based on tank type
				BufferedImage tankImage = null;
				BufferedImage turretImage = null;
				switch (type)
				{
					case GREEN:
						tankImage = l.greenTank;
						turretImage = l.greenTurret;
						break;
					case RED:
						tankImage = l.redTank;
						turretImage = l.redTurret;
						break;
					case BLUE:
						tankImage = l.blueTank;
						turretImage = l.blueTurret;
						break;
					case BLACK:
						tankImage = l.blackTank;
						turretImage = l.blackTurret;
						break;
					case WHITE:
						tankImage = l.whiteTank;
						turretImage = l.whiteTurret;
						break;
					case PINK:
						tankImage = l.pinkTank;
						turretImage = l.pinkTurret;
						break;
					case YELLOW:
						tankImage = l.yellowTank;
						turretImage = l.yellowTurret;
						break;
					case INVISIBLE:
						tankImage = l.invisibleTank;
						turretImage = l.invisibleTurret;
						break;
				}

				// draw tank
				g.drawImage(tankImage, xLoc, yLoc, null);

				{
					// draw turret
					Graphics2D g2D = (Graphics2D) g;
					AffineTransform backupAT = g2D.getTransform();
					AffineTransform theAT = new AffineTransform();

					int xTurretImageLoc = xLoc + 15;
					int yTurretImageLoc = yLoc - 15;
					int xTurretRotateOffset = 10;
					int yTurretRotateOffset = 40;

					theAT.rotate((Math.PI * 0.5) - turretAngle, xTurretImageLoc + xTurretRotateOffset,
							yTurretImageLoc + yTurretRotateOffset); // add PI/2
																	// because
																	// turret
																	// image is
																	// upwards
																	// so that
																	// starts it
																	// horizontal

					g2D.transform(theAT);
					g.drawImage(turretImage, xTurretImageLoc, yTurretImageLoc, null);

					g2D.setTransform(backupAT);

					// enemyFire

				}
			}
		}

	}

	public boolean canMoveX(Direction dir, Arena inArena)
	{
		// System.out.println("begin wall detection");
		// If no direction is indicated, movement cannot be checked in any
		// specific direction
		if (dir == null)
			return true;
		
		if ((dir == Direction.WEST) ||
			(dir == Direction.SOUTHWEST) ||
			(dir == Direction.NORTHWEST)	)
			return checkWest(inArena);
		
		if ((dir == Direction.EAST) ||
			(dir == Direction.SOUTHEAST) ||
			(dir == Direction.NORTHEAST)	)
			return checkEast(inArena);

		return true;

	}

	public boolean canMoveY(Direction dir, Arena inArena)
	{
		if (dir == null)
			return true;

		if ((dir == Direction.NORTH) ||
			(dir == Direction.NORTHWEST) ||
			(dir == Direction.NORTHEAST)	)
			return checkNorth(inArena);
			
		if ((dir == Direction.SOUTH) ||
			(dir == Direction.SOUTHWEST) ||
			(dir == Direction.SOUTHEAST)	)
			return checkSouth(inArena);
		
		return true;
	}

	private boolean checkWest(Arena inArena)
	{
		// check other tanks
		ArrayList<Tank>	tankList = inArena.tankList;
		for (int i = 1; i < tankList.size(); i++)
		{
			if (tankList.get(i).alive)
			{
				if (xLoc == tankList.get(i).xLoc + 50)
				{
					if (yLoc >= tankList.get(i).yLoc - 50 && yLoc <= tankList.get(i).yLoc + 50)
					{
						return false;
					}
				}
			}
		}
		// check walls
		Wall[][]	walls = inArena.walls;
		for (int r = 0; r < walls.length; r++)
		{
			for (int c = 0; c < walls[r].length; c++)
			{
				if (walls[r][c] != null)
				{
					// Checks if the certain wall is west of the tank by adding
					// 50 in the x direction to a walls location
					if (xLoc == (c * 50) + 50)
					{
						// Checks both edges of tank to see if they are within
						// the borders of the west wall, including the endpoints
						// of the wall
						if (yLoc > (r * 50) - 50 && yLoc < (r * 50) + 50)
						{
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	private boolean checkEast(Arena inArena)
	{
		// checking for other tanks
		ArrayList<Tank>	tankList = inArena.tankList;
		for (int i = 1; i < tankList.size(); i++)
		{
			if (tankList.get(i).alive)
			{
				if (xLoc + 50 == tankList.get(i).xLoc)
				{
					if (yLoc >= tankList.get(i).yLoc - 50 && yLoc <= tankList.get(i).yLoc + 50)
					{
						return false;
					}
				}
			}
		}
		// checking for other walls
		Wall[][]	walls = inArena.walls;
		for (int r = 0; r < walls.length; r++)
		{
			for (int c = 0; c < walls[r].length; c++)
			{
				if (walls[r][c] != null)
				{
					// Checks if the certain wall is east of the tank by adding
					// 50 in the x direction to tank's location
					if (xLoc + 50 == c * 50)
					{
						// Checks both edges of tank to see if they are within
						// the borders of the East wall, including the endpoints
						// of the wall
						if (yLoc > (r * 50) - 50 && yLoc < (r * 50) + 50)
						{
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	private boolean checkNorth(Arena inArena)
	{
		ArrayList<Tank>	tankList = inArena.tankList;
		for (int i = 1; i < tankList.size(); i++)
		{
			if (tankList.get(i).alive)
			{
				if (yLoc == tankList.get(i).yLoc + 50)
				{
					if (xLoc >= tankList.get(i).xLoc - 50 && xLoc <= tankList.get(i).xLoc + 50)
					{
						return false;
					}
				}
			}
		}
		Wall[][]	walls = inArena.walls;
		for (int r = 0; r < walls.length; r++)
		{
			for (int c = 0; c < walls[r].length; c++)
			{
				if (walls[r][c] != null)
				{
					// Checks if the certain wall is North of the tank by adding
					// 50 in the x direction to tank's location
					if (yLoc == (r * 50) + 50)
					{
						// Checks both edges of tank to see if they are within
						// the borders of the North wall, including the
						// endpoints of the wall
						if (xLoc > (c * 50) - 50 && xLoc < (c * 50) + 50)
						{
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	private boolean checkSouth(Arena inArena)
	{
		ArrayList<Tank>	tankList = inArena.tankList;
		for (int i = 1; i < tankList.size(); i++)
		{
			if (tankList.get(i).alive)
			{
				if (yLoc + 50 == tankList.get(i).yLoc)
				{
					if (xLoc >= tankList.get(i).xLoc - 50 && xLoc <= tankList.get(i).xLoc + 50)
					{
						return false;
					}
				}
			}
		}
		Wall[][]	walls = inArena.walls;
		for (int r = 0; r < walls.length; r++)
		{
			for (int c = 0; c < walls[r].length; c++)
			{
				if (walls[r][c] != null)
				{
					// Checks if the certain wall is south of the tank by adding
					// 50 in the y direction to tank's location
					if (yLoc + 50 == r * 50)
					{
						// Checks both edges of tank to see if they are within
						// the borders of the south wall, including the
						// endpoints of the wall
						if (xLoc > (c * 50) - 50 && xLoc < (c * 50) + 50)
						{
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	// Movement, aiming, firing, and draw are coded and implemented differently
	// between Player and AI Tanks
	// As a result, the abstract class has these as abstract methods and no
	// implementation provided
	/*
	 * Likely idea is that user sends inputs into the player tank In contrast AI
	 * Tank prompts its own location, aiming, and firing AI Tank will probably
	 * utilize the playerTank's location and wall locations to prompt its own
	 * movement
	 */
	abstract void move(ResourceLibrary l, Arena inArena);

	abstract void aim();

	abstract void fire(ResourceLibrary l, Arena inArena);

	public int getX()
	{
		return xLoc;
	}

	public int getY()
	{
		return yLoc;
	}

}
