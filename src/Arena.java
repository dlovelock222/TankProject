import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Arena
{
	int level; // Specifies which level and a specific arena is drawn based on which level is passed in
	GameMode gameMode;

	public Wall[][] walls; // List of all walls in the arena
	// Every cell in the arena is can be made into a wall
	// Remains null if no wall is created in the cell
	PlayerTank playerTank; // Tank controlled by player

	int numTanksKilled;
	int recentNumTanksKilled;

	// Note about TankList
	// New Tanklist is created with the creation of a new arena
	// This prevents tanks from previous levels from being drawn in later levels
	ArrayList<Tank> tankList; // List of all tanks to keep track of
	ArrayList<Explosion> explosionList;

	int[] inputMoveInfo; // Information for how to change x and y locations
	// Dependent on keypressed
	int numWallsAcross; // Dimensions across
	int numWallsDown; // Dimensions down

	private int timer;
	public boolean transition;
	private int timerStartTransition;
	private boolean startingTransition;
	public boolean inFreeze;
	private boolean startingFreeze;
	private int timerStartFreeze;;

	public boolean advanceLevel;
	public Wall[][] transitionWalls;

	public KillData killData;

	// Arena Constructor
	public Arena(int inLevel, GameMode inGameMode, int inNumWallsAcross, int inNumWallsDown, KillData inKillData)
	{
		level = inLevel; // Sets up which level
		gameMode = inGameMode;

		// Sets up arena dimensions
		numWallsAcross = inNumWallsAcross;
		numWallsDown = inNumWallsDown;

		// Construct 2D Area of walls
		walls = new Wall[numWallsDown][numWallsAcross];

		timer = 0;
		transition = false;
		timerStartTransition = 0;
		advanceLevel = false;
		startingTransition = false;
		inFreeze = false;
		transitionWalls = new Wall[numWallsDown][numWallsAcross];

		recentNumTanksKilled = 0;
		killData = inKillData;

		for (int r = 0; r < numWallsDown; r++)
		{
			transitionWalls[r][0] = new Wall(r, 0, false, false);
		}
		for (int r = 0; r < numWallsDown; r++)
		{
			transitionWalls[r][numWallsAcross - 1] = new Wall(r, numWallsAcross - 1, false, false);
		}
		for (int c = 0; c < numWallsAcross; c++)
		{
			transitionWalls[0][c] = new Wall(0, c, false, false);
		}
		for (int c = 0; c < numWallsAcross; c++)
		{
			transitionWalls[numWallsDown - 1][c] = new Wall(numWallsDown - 1, c, false, false);
		}

		// Constructs a player tank with location (3,10)
		playerTank = new PlayerTank(3, 10, this);
		// Constructs list of tanks
		tankList = new ArrayList<Tank>();
		// Adds player tank to arraylist of tanks to keep track of
		tankList.add(playerTank);

		explosionList = new ArrayList<Explosion>();

	}

	public void moveTanks(ResourceLibrary l)
	{
		if (!transition)
		{
			for (Tank tank : tankList)
			{
				tank.move(l, this);
				if (tank.type != TankType.GREEN)
				{
					tank.fire(l, this);
				}
			}
		}
	}

	// Arena draw method
	public void draw(Graphics g, ResourceLibrary l)
	{
		timer++;
		// draws wood panel background image
		g.drawImage(l.background, 0, 0, null);


		if(inFreeze)
		{

			if (startingFreeze == true)
			{
				timerStartFreeze = timer;// start timer so transition only
				// lasts so long
				startingFreeze = false;
			}


			if (timer - timerStartFreeze > 150)
			{ // check if transition should end
				System.out.println("timer ran out");
				inFreeze = false;
				transition = true;
				startingTransition = true;
			}

			// If a cell in the arena is not null, it is considered to be a wall
			// We call the wall's draw function here to make our wall
			for (int r = 0; r < walls.length; r++)
			{
				for (int c = 0; c < walls[r].length; c++)
				{
					if (walls[r][c] != null)
					{
						walls[r][c].draw(g, l);
					}
				}
			}

			// Draws all the tanks in the tanklist
			for (Tank tank : tankList)
			{
				tank.draw(g, l);
			}
			// Draws all the explosions in the explosionList
			for (Explosion explosion : explosionList)
			{
				explosion.draw(g, l);
			}



		}
		else if (transition)
		{

			if (startingTransition == true)
			{
				timerStartTransition = timer;// start timer so transition only
				// lasts so long
				startingTransition = false;
			}


			System.out.println(timerStartTransition);
			if (timer - timerStartTransition > 750)
			{ // check if transition should end
				System.out.println("timer ran out");
				advanceLevel = true;// tells arena to start next level
			}

			//Draw the transition screen 
			for (int r = 0; r < walls.length; r++)
			{ // draws all of the walls in transition screen
				for (int c = 0; c < walls[r].length; c++)
				{
					if (transitionWalls[r][c] != null)
					{
						transitionWalls[r][c].draw(g, l);
					}
				}
			}
			drawTransition(g, l);


		} else
		{
			// If a cell in the arena is not null, it is considered to be a wall
			// We call the wall's draw function here to make our wall
			for (int r = 0; r < walls.length; r++)
			{
				for (int c = 0; c < walls[r].length; c++)
				{
					if (walls[r][c] != null)
					{
						walls[r][c].draw(g, l);
					}
				}
			}

			// Draws all the tanks in the tanklist
			for (Tank tank : tankList)
			{
				tank.draw(g, l);
			}
			// Draws all the explosions in the explosionList
			for (Explosion explosion : explosionList)
			{
				explosion.draw(g, l);
			}

			if (level == 0)
			{
				if (numTanksKilled < 10)
				{
					g.setColor(Color.BLACK); // Black colored rectangle
					g.fillRect(650, 0, 50, 50); // Makes rectangle for text
				} else if (numTanksKilled < 100)
				{
					g.setColor(Color.BLACK); // Black colored rectangle
					g.fillRect(650, 0, 70, 50); // Makes rectangle for text
				} else if (numTanksKilled < 1000)
				{
					g.setColor(Color.BLACK); // Black colored rectangle
					g.fillRect(650, 0, 90, 50); // Makes rectangle for text
				} else
				{
					g.setColor(Color.BLACK); // Black colored rectangle
					g.fillRect(650, 0, 115, 50); // Makes rectangle for text
				}

				g.setFont(new Font("TimesRoman", Font.PLAIN, 50)); // Times New
				// Roman
				// font;
				// size 50
				g.setColor(Color.WHITE); // white colored text
				g.drawString("" + numTanksKilled, 660, 45); // displays score

				// check each tank for dead
				int numTanksAlive = 0;
				numTanksKilled = 0; // always recalc
				for (int i = 1; i < tankList.size(); i++)
				{
					if (tankList.get(i).alive != true)
					{
						numTanksKilled++;
					} else
					{
						numTanksAlive++;
					}
				}
				if (numTanksKilled < 10)
				{
					for (int i = 0; i < 1 - numTanksAlive; i++)
					{
						survivalAddTank();
					}
				} else
				{
					for (int i = 0; i < 2 - numTanksAlive; i++)
					{
						survivalAddTank();
					}
				}

				// TODO gradually delete inner walls
				if (numTanksKilled > 23 && numTanksKilled % 2 == 0 && recentNumTanksKilled != numTanksKilled)
				{
					recentNumTanksKilled = numTanksKilled;
					deleteWall();
				}

			} else
			{
				// Determines if all enemy tanks are dead
				// If condition is met, level is incremented
				// Doesn't check index 0 because it is a playertank
				boolean allDead = true;
				for (int i = 1; i < tankList.size(); i++)
				{
					if (tankList.get(i).alive == true)
					{
						allDead = false;
					}
				}
				if (allDead)
				{
					inFreeze = true;
					startingFreeze = true;
					l.playClip(l.K_progressLevel);
				}
			}
		}

	}

	private void deleteWall()
	{
		int row = (int) (Math.random() * numWallsDown);
		int col = (int) (Math.random() * numWallsAcross);

		while (walls[row][col] == null || !walls[row][col].deletable)
		{
			row = (int) (Math.random() * numWallsDown);
			col = (int) (Math.random() * numWallsAcross);
		}

		walls[row][col] = null;

	}

	public void addExplosion(int inX, int inY, ExplosionType inType)
	{
		explosionList.add(new Explosion(inX, inY, inType));
	}

	private void survivalAddTank()
	{
		// use tanklist to find quadrant to add to killed to get random
		boolean Q1Free = true;
		boolean Q2Free = true;
		boolean Q3Free = true;
		boolean Q4Free = true;
		for (Tank t : tankList)
		{
			if (t.alive)
			{
				if (t.xLoc < 700 && t.yLoc < 400)
				{
					Q2Free = false;
				}
				if (t.xLoc > 700 && t.yLoc < 400)
				{
					Q1Free = false;
				}
				if (t.xLoc < 700 && t.yLoc > 400)
				{
					Q3Free = false;
				}
				if (t.xLoc > 700 && t.yLoc > 400)
				{
					Q4Free = false;
				}
			}
		}
		int x = 0;
		int y = 0;
		// multiples if statements based on quadrant to set x and y
		int quadrant = 0;
		if (Q1Free && !Q2Free && !Q3Free && !Q4Free)
		{
			quadrant = 1;
		} else if (!Q1Free && Q2Free && !Q3Free && !Q4Free)
		{
			quadrant = 2;
		} else if (!Q1Free && !Q2Free && Q3Free && !Q4Free)
		{
			quadrant = 3;
		} else if (!Q1Free && !Q2Free && !Q3Free && Q4Free)
		{
			quadrant = 4;
		} else if (!Q1Free && !Q2Free && !Q3Free && !Q4Free)
		{
			quadrant = getRandomQuadrant();
		} else
		{
			quadrant = getRandomQuadrant();
			while (checkQuadrant(quadrant, Q1Free, Q2Free, Q3Free, Q4Free) == false)
			{
				quadrant = getRandomQuadrant();
			}
		}

		switch (quadrant)
		{
			case 1:
				y = 2;
				x = 25;
				break;
			case 2:
				y = 2;
				x = 2;
				break;
			case 3:
				y = 13;
				x = 2;
				break;
			case 4:
				y = 13;
				x = 25;
				break;
		}

		TankType type = survivalRandomType();

		tankList.add(new AITank(type, x, y, gameMode));

	}

	private boolean checkQuadrant(int quadrant, boolean q1Free, boolean q2Free, boolean q3Free, boolean q4Free)
	{
		switch (quadrant)
		{
			case 1:
				if (q1Free)
				{
					return true;
				} else
				{
					return false;
				}
			case 2:
				if (q2Free)
				{
					return true;
				} else
				{
					return false;
				}
			case 3:
				if (q3Free)
				{
					return true;
				} else
				{
					return false;
				}
			case 4:
				if (q4Free)
				{
					return true;
				} else
				{
					return false;
				}
		}
		return false;
	}

	private int getRandomQuadrant()
	{
		return ((int) (Math.random() * 4)) + 1;
	}

	private TankType survivalRandomType()
	{
		// do a bunch of ifs to get a random number (the higher the
		// numTanksKilled, the more difficult tanks included)
		// use switch case to return tanktype based on number
		int numTankType;
		if (numTanksKilled < 31)
		{
			numTankType = (int) (Math.random() * ((numTanksKilled / 5) + 1));
		} else
		{
			numTankType = (int) (Math.random() * 7);
		}

		switch (numTankType)
		{
			case 0:
				return TankType.BLUE;
			case 1:
				return TankType.RED;
			case 2:
				return TankType.BLACK;
			case 3:
				return TankType.WHITE;
			case 4:
				return TankType.PINK;
			case 5:
				return TankType.YELLOW;
			case 6:
				return TankType.INVISIBLE;
		}

		return null;
	}

	private void drawTransition(Graphics g, ResourceLibrary l)
	{
		g.setColor(Color.BLACK);
		g.setFont(new Font("TimesRoman", Font.PLAIN, 45));


		int totalScore = 0;
		totalScore += drawScoreOneLine(g, l, TankType.BLUE, 10, 272);
		totalScore += drawScoreOneLine(g, l, TankType.RED, 20, 342);
		totalScore += drawScoreOneLine(g, l, TankType.BLACK, 50, 412);  
		totalScore += drawScoreOneLine(g, l, TankType.WHITE, 60, 482); 
		totalScore += drawScoreOneLine(g, l, TankType.PINK, 75, 552);
		totalScore += drawScoreOneLine(g, l, TankType.YELLOW, 75, 622);
		totalScore += drawScoreOneLine(g, l, TankType.INVISIBLE, 125, 692);


		g.setFont(new Font("TimesRoman", Font.PLAIN, 60));
		g.drawString(Integer.toString(totalScore), 1133, 460);
		g.drawString("Total Score", 1018, 400);
		g.drawLine(1025, 410, 1305, 410);
		g.setFont(new Font("TimesRoman", Font.PLAIN, 150));
		String levelCompletion = "Level " + level + " Completed";
		g.drawString(levelCompletion, 100, 200);

	}

	private int drawScoreOneLine(Graphics g, ResourceLibrary l, TankType inType, int inScaler, int inY)
	{
		BufferedImage tankImage = null;
		BufferedImage turretImage = null;
		int imageY = inY - 42;
		switch (inType){
			case GREEN:
				tankImage = l.greenTank;
				turretImage = l.greenTurret;
				break;
			case BLUE:
				tankImage = l.blueTank;
				turretImage = l.blueTurret;
				break;
			case RED:
				tankImage = l.redTank;
				turretImage = l.redTurret;
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

		if(tankImage != null && turretImage != null)
		{
			int xTurretRotateOffset = 10;
			int yTurretRotateOffset = 40;

			g.drawImage(tankImage, 350, imageY, null);
			Graphics2D g2D = (Graphics2D) g;
			AffineTransform backupAT = g2D.getTransform();
			AffineTransform theAT = new AffineTransform();

			int xTurretImageLoc = 350 + 15;
			int yTurretImageLoc = imageY - 15;


			// add PI/2 because turret image is upwards so that starts it horizontal
			theAT.rotate((Math.PI * 0.5), xTurretImageLoc + xTurretRotateOffset, yTurretImageLoc + yTurretRotateOffset); 

			g2D.transform(theAT);
			g.drawImage(turretImage, xTurretImageLoc, yTurretImageLoc, null);

			g2D.setTransform(backupAT);
		}


		int numDestroyed = killData.getNumKills(inType);
		int score = numDestroyed * inScaler;

		g.drawString(Integer.toString(numDestroyed), 650, inY);

		g.drawString("x", 500, inY);

		g.drawString("=", 800, inY);

		g.drawString(Integer.toString(score), 930, inY);

		return score;
	}

	// Sets amount a certain tank has to move based on keypress
	// Calls the player tanks setInputMoveArr which takes the information of how
	// to move
	// Everytime tank is moved, actual x and y loc changes provided are executed
	public void setInputMoveArr(int[] inInputMoveArr)
	{
		playerTank.setInputMoveArr(inInputMoveArr);
	}

	// Returns x location of playerTank
	public int playerTankLocX()
	{
		return playerTank.getX();
	}

	// Returns y location of playerTank
	public int playerTankLocY()
	{
		return playerTank.getY();
	}

	public ArrayList<Tank> getTanks()
	{
		return tankList;
	}

	public void addTank(int inRow, int inCol, TankType inType)
	{
		if (inType.equals(TankType.GREEN))
		{
			playerTank.setX(inCol);
			playerTank.setY(inRow);
		} else
		{
			tankList.add(new AITank(inType, inCol, inRow, gameMode));
		}
	}

	public void addWall(int inRow, int inCol, boolean destructable, boolean deletable)
	{
		walls[inRow][inCol] = new Wall(inRow, inCol, destructable, deletable);
	}

}
