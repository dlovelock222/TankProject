import java.awt.Point;

public class AITank extends Tank // AI Tank is a specific type of Tank
{

	int numMoveTries;// Number of times the tank has tried to move
	int tankSlowMultiplier;// ex. 1 is fastest, 3 is 1/3 speed
	int fireSlowMultiplier;

	Point player;// player point
	Point ai;// ai point
	Point player1;// player point
	Point ai1;// ai point
	
	boolean commit;

	public AITank(TankType inType, int inX, int inY, GameMode inGameMode)
	{
		super();

		alive = true;
		type = inType;
		xLoc = inX * 50; // Each cell is 50 pixels in wide
		yLoc = inY * 50; // Each cell is 50 pixels in length

		ai = new Point(xLoc, yLoc);
		ai1 = new Point(xLoc, yLoc);

		numMoveTries = 0;

		switch (type)
		{
			case GREEN:
				// Green is PlayerTank.  This case should never happen here
				break;
			case BLUE:
				tankSlowMultiplier = 10000;
				break;
			case RED:
				tankSlowMultiplier = 4;
				break;
			case BLACK:
				tankSlowMultiplier = 4;
				break;
			case WHITE:
				tankSlowMultiplier = 4;
				break;
			case PINK:
				if (inGameMode == GameMode.SURVIVAL)
				{
					tankSlowMultiplier = 8;
				} else
				{
					tankSlowMultiplier = 10000;
				}
				break;
			case YELLOW:
				if (inGameMode == GameMode.SURVIVAL)
				{
					tankSlowMultiplier = 8;
				} else
				{
					tankSlowMultiplier = 10000;
				}
				break;
			case INVISIBLE:
				if (inGameMode == GameMode.SURVIVAL)
				{
					tankSlowMultiplier = 8;
				} else
				{
					tankSlowMultiplier = 10000;
				}
				break;
		}

		fireSlowMultiplier = 400;

		commit = false;
	}

	// Need to figure out mechanism by which AI Tank Moves
	void move(ResourceLibrary l, Arena inArena)
	{
		numMoveTries++;
		player1 = new Point(inArena.playerTankLocX(), inArena.playerTankLocY());
		ai1 = new Point(xLoc, yLoc);
		Direction dirX;
		Direction dirY;

		if (ai1.getX() - player1.getX() > 0)
		{
			dirX = Direction.WEST;

		} else if (ai1.getX() - player1.getX() < 0)
		{
			dirX = Direction.EAST;

		} else
		{
			dirX = null;
		}

		if ((ai1.getY() - player1.getY() > 0))
		{
			dirY = Direction.NORTH;
		} else if ((ai1.getY() - player1.getY() < 0))
		{
			dirY = Direction.SOUTH;
		} else
		{
			dirY = null;
		}

		if (canMoveX(dirX, inArena) && numMoveTries % tankSlowMultiplier == 0 && dirX == Direction.WEST
				&& xLoc != player1.getX())
		{
			xLoc += -1;
		} else if (canMoveX(dirX, inArena) && numMoveTries % tankSlowMultiplier == 0 && dirX == Direction.EAST
				&& xLoc != player1.getX())
		{
			xLoc += 1;
		}
		if (canMoveY(Direction.NORTH, inArena) && numMoveTries % tankSlowMultiplier == 0
				&& dirY == Direction.NORTH && yLoc != player1.getY())
		{
			yLoc += -1;
		} else if (canMoveY(Direction.SOUTH, inArena) && numMoveTries % tankSlowMultiplier == 0
				&& dirY == Direction.SOUTH && yLoc != player1.getY())
		{
			yLoc += 1;
		}

		for (Projectile p : stockPile)
		{
			p.move(l, inArena);

		}

	}

	// Need to figure out mechanism by which AI Tank Aims
	void aim()
	{

	}

	// Need to figure out mechanism by which AI Tank Fires
	void fire(ResourceLibrary l, Arena inArena)
	{

		// tank firing sound
		for (int i = 0; i < stockPile.size(); i++)
		{
			if (!stockPile.get(i).active)
			{
				stockPile.remove(i); // Removes missile from stockpile
				i--;

			}
		}


		if (numMoveTries % fireSlowMultiplier == 0 && alive)
		{
			// if it has space, it will make a new projectile
			if (canFire(inArena))
			{
				boolean intersect = intersectHighLevel(inArena);

				if (intersect == false)
				{
					Projectile p = new Projectile(	turretTopX,
													turretTopY,
													Math.atan2(-(targetY - turretCenterY), targetX - turretCenterX),
													type);
					stockPile.add(p);
					l.playClip(l.K_tankFiring);
	
					inArena.addExplosion(turretTopX, turretTopY, ExplosionType.SMALL);
				}
			}
		}
	}

	private int orientation(Point p, Point q, Point r)
	{
		double val = (q.getY() - p.getY()) * (r.getX() - q.getX()) - (q.getX() - p.getX()) * (r.getY() - q.getY());

		if (val == 0.0)
			return 0; // colinear
		return (val > 0) ? 1 : 2; // clock or counterclock wise
	}

	private boolean intersectHighLevel(Arena inArena)
	{
		boolean intersect = false;

		player = new Point(inArena.playerTankLocX(), -inArena.playerTankLocY());	// TODO
		ai = new Point(xLoc, -yLoc);
		
		Wall[][] walls = inArena.walls;
		for (int r = 0; r < walls.length; r++)
		{
			for (int c = 0; c < walls[r].length; c++)
			{
				if (walls[r][c] != null)
				{
					Wall temp = walls[r][c];
					Point point1 = new Point(temp.getXLoc(), -temp.getYLoc());
					Point point2 = new Point(temp.getXLoc() + 50, -temp.getYLoc());
					Point point3 = new Point(temp.getXLoc(), (-temp.getYLoc() - 50));
					Point point4 = new Point(temp.getXLoc() + 50, (-temp.getYLoc() - 50));
					if (intersectLowLevel(player, ai, point1, point2) == true && walls[r][c].destructable == false)
					{
						intersect = true;
					} else if (intersectLowLevel(player, ai, point3, point4) == true
							&& walls[r][c].destructable == false)
					{
						intersect = true;
					} else if (intersectLowLevel(player, ai, point1, point3) == true
							&& walls[r][c].destructable == false)
					{
						intersect = true;
					} else if (intersectLowLevel(player, ai, point2, point4) == true
							&& walls[r][c].destructable == false)
					{
						intersect = true;
					}
				}
			}
		}
		
		return intersect;
	}
	
	
	private boolean intersectLowLevel(Point p1, Point q1, Point p2, Point q2)
	{

		int o1 = orientation(p1, q1, p2);
		int o2 = orientation(p1, q1, q2);
		int o3 = orientation(p2, q2, p1);
		int o4 = orientation(p2, q2, q1);

		if (o1 != o2 && o3 != o4)
			return true;

		return false;
	}

}
