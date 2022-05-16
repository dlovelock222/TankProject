
public class KillData
{
	private GameMode gameMode;

	private int greenTanksKilled;
	private int blueTanksKilled;
	private int redTanksKilled;
	private int blackTanksKilled;
	private int whiteTanksKilled;
	private int pinkTanksKilled;
	private int yellowTanksKilled;
	private int invisibleTanksKilled;

	public KillData(GameMode inGameMode)
	{
		gameMode = inGameMode;

		greenTanksKilled = 0;
		blueTanksKilled = 0;
		redTanksKilled = 0;
		blackTanksKilled = 0;
		whiteTanksKilled = 0;
		pinkTanksKilled = 0;
		yellowTanksKilled = 0;
		invisibleTanksKilled = 0;
	}

	public void addKill(TankType inType)
	{
		switch (inType)
		{
			case BLUE:
				blueTanksKilled++;
				break;
			case GREEN:
				greenTanksKilled++;
				break;
			case RED:
				redTanksKilled++;
				break;
			case BLACK:
				blackTanksKilled++;
				break;
			case WHITE:
				whiteTanksKilled++;
				break;
			case PINK:
				pinkTanksKilled++;
				break;
			case YELLOW:
				yellowTanksKilled++;
				break;
			case INVISIBLE:
				invisibleTanksKilled++;
				break;
		}
	}

	public int getNumKills(TankType inType)
	{
		switch (inType)
		{
			case BLUE:
				return blueTanksKilled;
			case GREEN:
				return greenTanksKilled;
			case RED:
				return redTanksKilled;
			case BLACK:
				return blackTanksKilled;
			case WHITE:
				return whiteTanksKilled;
			case PINK:
				return pinkTanksKilled;
			case YELLOW:
				return yellowTanksKilled;
			case INVISIBLE:
				return invisibleTanksKilled;
		}
		return 0;
	}

}
