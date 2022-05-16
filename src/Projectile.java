import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class Projectile
{
	double xLoc; //location of tip; translate to draw but not for hit detection
	double yLoc; //
	
	double speed; //	number of pixels per timer tick
	double angle; //Angle of projectile in radians, counterclockwise (0 is positive X axis, PI/2 is point up)
	boolean active; //for playertank to remove from stockpile once not active
	int numWallHits; //	
	int numMoveTries; //	
	int detectedWallTime;
	int projectileSlowMultiplier;
	int maxNumWallHits;

	// numDraws is used for debugging by stopping projectile early for visualizing
	//int numDraws; 

	public Projectile(int x, int y, double a, TankType inType)
	{
		xLoc = x;
		yLoc = y;
		angle = a;

		switch (inType)
		{
		case GREEN:
			speed = 3;
			maxNumWallHits = 1;
			break;
		case BLUE:
			speed = 3;
			maxNumWallHits = 0;
			break;
		case RED:
			speed = 3;
			maxNumWallHits = 1;
			break;
		case BLACK:
			speed = 5;
			maxNumWallHits = 0;
			break;
		case WHITE:
			speed = 5;
			maxNumWallHits = 2;
			break;
		case PINK:
			speed = 5;
			maxNumWallHits = 2;
			break;
		case YELLOW:
			speed = 5;
			maxNumWallHits = 2;
			break;
		case INVISIBLE:
			speed = 3;
			maxNumWallHits = 1;
			break;
		}

		active = true;
		numWallHits = 0;
		numMoveTries = 0;
		detectedWallTime = 0;
		projectileSlowMultiplier = 2;
		//	numDraws = 0;

	}

	void draw(Graphics g, ResourceLibrary l)
	{

		//draws black box projectile - use to test where image should be 
		//g.setColor(Color.BLACK);
		//g.fillRect((int)(xLoc), (int)(yLoc), 15, 15);

		if (active)
		{
			Graphics2D g2D = (Graphics2D) g;
			AffineTransform backupAT = g2D.getTransform();
			AffineTransform theAT = new AffineTransform();

			//projectile image is 20x10
			int projectileDrawX = (int) xLoc - 20;
			int projectileDrawY = (int) yLoc - 5;
			int projectileRotateOffsetX = 20;
			int projectileRotateOffsetY = 5;

			theAT.rotate(-angle, projectileDrawX + projectileRotateOffsetX, projectileDrawY + projectileRotateOffsetY);//-angle to adjust to y axis pointing down

			g2D.transform(theAT);
			if (speed < 4)
			{
				g.drawImage(l.projectile, projectileDrawX, projectileDrawY, null);
			} else
			{
				g.drawImage(l.projectileRed, projectileDrawX, projectileDrawY, null);
			}

			g2D.setTransform(backupAT);

			//	numDraws++;
		}

	}

	void move(ResourceLibrary l, Arena inArena)
	{
		if (active && !inArena.inFreeze)
		{
			numMoveTries++;
			if (!detectTanks(l, inArena))
			{
				if((numMoveTries - detectedWallTime > 1))
				{
					detectWalls(l, inArena);
				}
				if (numMoveTries % projectileSlowMultiplier == 0)
					xLoc += speed * Math.cos(-angle);//used negative angle to convert from normal math axis to screen axis
				//System.out.println(angle);
				if (numMoveTries % projectileSlowMultiplier == 0)
				{
					yLoc += speed * Math.sin(-angle);
					//ddtem.out.println(angle);

				}
			}
			checkProjectile(inArena);
		}

		if (numWallHits > maxNumWallHits)
		{ //once its hit a second wall, it dies //change back to 1
			active = false;
		}
		if (!active)
		{
			inArena.addExplosion((int) xLoc, (int) yLoc, ExplosionType.MEDIUM); //call arena addExplosion
			l.playClip(l.K_proj_to_wall);
		}

	}

	private boolean detectTanks(ResourceLibrary l, Arena inArena)
	{

		ArrayList<Tank> tankList = inArena.getTanks();
		Direction dir = getDirection();
		for (Tank t : tankList)
		{
			if (t.alive)
			{
				//works great
				if (dir == Direction.EAST || dir == Direction.NORTHEAST || dir == Direction.SOUTHEAST)
				{
					if (xLoc <= t.getX() + speed && xLoc >= t.getX() - speed)
					{
						if (yLoc >= t.getY() && yLoc <= t.getY() + 50)
						{
							active = false;
							inArena.addExplosion((int) xLoc, (int) yLoc, ExplosionType.MEDIUM); //call arena addExplosion
							t.alive = false;
							inArena.killData.addKill(t.type);
							if (inArena.level == 0 && !t.type.equals(TankType.GREEN))
							{
								l.playClip(l.K_progressLevel);
							}
							l.playClip(l.K_proj_to_aiTank);
							inArena.addExplosion(t.getX(), t.getY(), ExplosionType.LARGE); //call arena addExplosion
							return true;

						}
					}

				}
				if (dir == Direction.NORTH || dir == Direction.NORTHEAST || dir == Direction.NORTHWEST)
				{
					if (yLoc >= t.getY() - speed + 50 && yLoc <= t.getY() + speed + 50)
					{
						if (xLoc >= t.getX() - speed && xLoc <= t.getX() + 50 + speed)
						{
							active = false;
							inArena.addExplosion((int) xLoc, (int) yLoc, ExplosionType.MEDIUM); //call arena addExplosion
							t.alive = false;
							inArena.killData.addKill(t.type);
							if (inArena.level == 0 && !t.type.equals(TankType.GREEN))
							{
								l.playClip(l.K_progressLevel);
							}
							l.playClip(l.K_proj_to_aiTank);
							inArena.addExplosion(t.getX(), t.getY(), ExplosionType.LARGE); //call arena addExplosion
							return true;
						}
					}
				}
				if (dir == Direction.SOUTH || dir == Direction.SOUTHWEST || dir == Direction.SOUTHEAST)
				{
					if (yLoc >= t.getY() - speed && yLoc <= t.getY() + speed)
					{
						if (xLoc >= t.xLoc - speed && xLoc <= t.xLoc + 50 + speed)
						{
							active = false;
							inArena.addExplosion((int) xLoc, (int) yLoc, ExplosionType.MEDIUM); //call arena addExplosion
							t.alive = false;
							inArena.killData.addKill(t.type);
							if (inArena.level == 0 && !t.type.equals(TankType.GREEN))
							{
								l.playClip(l.K_progressLevel);
							}
							l.playClip(l.K_proj_to_aiTank);
							inArena.addExplosion(t.getX(), t.getY(), ExplosionType.LARGE); //call arena addExplosion
							return true;
						}
					}
				}
				//works great
				if (dir == Direction.WEST || dir == Direction.NORTHWEST || dir == Direction.SOUTHWEST)
				{
					if (xLoc <= t.getX() + speed + 50 && xLoc >= t.getX() - speed + 50)
					{
						if (yLoc >= t.yLoc && yLoc <= t.yLoc + 50)
						{
							active = false;
							inArena.addExplosion((int) xLoc, (int) yLoc, ExplosionType.MEDIUM); //call arena addExplosion
							t.alive = false;
							inArena.killData.addKill(t.type);
							if (inArena.level == 0 && !t.type.equals(TankType.GREEN))
							{
								l.playClip(l.K_progressLevel);
							}
							l.playClip(l.K_proj_to_aiTank);
							inArena.addExplosion(t.getX(), t.getY(), ExplosionType.LARGE); //call arena addExplosion
							return true;
						}
					}
				}
			}
		}


		return false;
	}

	private void detectWalls(ResourceLibrary l, Arena inArena)
	{
		//the wall detection will use the angle to find what direction. it can be any of the following: north, east, south, west, northeast, northwest, southeast, southwest
		//based on the direction, it will check a certain side of the wall. in the case of a duel direction, it will check two sides
		Direction dir = getDirection();
		Wall[][] walls = inArena.walls;

		for (int r = 0; r < walls.length; r++)
		{
			for (int c = 0; c < walls[r].length; c++)
			{
				if (walls[r][c] != null)
				{
					if (dir != null)
					{
						if ((int) yLoc > ((r * 50) - speed) && (int) yLoc < (r * 50) + 50 + speed) //adds speed because parametric movement can skip pixels 
							if ((int) xLoc > ((c * 50) - speed) && (int) xLoc < (c * 50) + 50 + speed)
							{
								numWallHits++;
								detectedWallTime = numMoveTries;
								if (numWallHits <= maxNumWallHits && !walls[r][c].destructable)
								{
									l.playClip(l.K_projRicochet);
								}
								if(dir == Direction.NORTH || dir == Direction.NORTHWEST || dir == Direction.NORTHEAST) {
									angle = angle * -1;
								}
								else if (dir == Direction.SOUTH || dir == Direction.SOUTHWEST || dir == Direction.SOUTHEAST) {
									angle = angle * -1;
								}
								else if (dir == Direction.EAST || dir == Direction.NORTHEAST || dir == Direction.SOUTHEAST) {
									angle = Math.PI - angle;
								}
								else if (dir == Direction.WEST || dir == Direction.NORTHWEST || dir == Direction.SOUTHWEST) {
									angle = Math.PI - angle;
								}
								
								if (angle < 0)
								{
									angle += 2 * Math.PI;
								}
								if (walls[r][c].destructable)
								{
									walls[r][c] = null;
									active = false;
									l.playClip(l.K_proj_to_destructableWall);
								}
								return;

							}
					}
				}
			}
		}
	}

	private Direction getDirection()
	{
		double degrees = (angle * 180) / Math.PI;
		if (degrees < 0)
		{
			degrees += 360;
		}
		if (degrees == 0)
			return Direction.EAST;
		if (degrees > 0 && degrees < 90)
			return Direction.NORTHEAST;
		if (degrees == 90)
			return Direction.NORTH;
		if (degrees > 90 && degrees < 180)
			return Direction.NORTHWEST;
		if (degrees == 180)
			return Direction.WEST;
		if (degrees > 180 && degrees < 270)
			return Direction.SOUTHWEST;
		if (degrees == 270)
			return Direction.SOUTH;
		if (degrees > 270 && degrees < 360)
			return Direction.SOUTHEAST;

		return null;
	}

	private void checkProjectile(Arena inArena)
	{
		for (Tank t : inArena.tankList)
		{
			for (Projectile p : t.stockPile)
			{
				if (p.equals(this))
				{

				} else
				{
					if (xLoc - 5 < p.xLoc && xLoc + 5 > p.xLoc)
					{
						if (yLoc - 5 < p.yLoc && yLoc + 5 > p.yLoc)
						{
							active = false;
							p.active = false;
						}

					}

				}
			}
		}
	}

}


