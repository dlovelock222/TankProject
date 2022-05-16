
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ArenaReader
{

	private String rootPath; // should be "data/arenas/arena"

	public ArenaReader(String inRootPath)
	{
		rootPath = inRootPath;
	}

	public Arena readArena(int inLevel, GameMode inGameMode, int inNumWallsAcross, int inNumWallsDown, KillData killData)
	{

		int level = inLevel;
		String fileName = rootPath + level + ".txt";

		Arena arena = new Arena(level, inGameMode, inNumWallsAcross, inNumWallsDown, killData);

		File file = new File(fileName);

		int numLines = inNumWallsDown;

		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int row = 0; row < numLines; row++)
		{
			String str = null;
			try
			{
				str = br.readLine();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// String[] letters = str.split("");
			for (int col = 0; col < str.length(); col++)
			{
				char c = str.charAt(col);
				switch (c)
				{
					case 'W':
						arena.addWall(row, col, false, false);
						break;
					case 'w':
						arena.addWall(row, col, false, true);
						break;
					case 'D':
						arena.addWall(row, col, true, false);
						break;
					case 'd':
						arena.addWall(row, col, true, true);
						break;
					case '0':
						arena.addTank(row, col, TankType.GREEN);
						break;
					case '1':
						arena.addTank(row, col, TankType.BLUE);
						break;
					case '2':
						arena.addTank(row, col, TankType.RED);
						break;
					case '3':
						arena.addTank(row, col, TankType.BLACK);
						break;
					case '4':
						arena.addTank(row, col, TankType.WHITE);
						break;
					case '5':
						arena.addTank(row, col, TankType.PINK);
						break;
					case '6':
						arena.addTank(row, col, TankType.YELLOW);
						break;
					case '7':
						arena.addTank(row, col, TankType.INVISIBLE);
						break;
				}
			}
		}

		return arena;
	}
}

//
// ArenaReader theReader = new ArenaReader(1);
// Arena level1 = theReader.getArena();
//
// ArenaReader theReader = new ArenaReader(2);
// Arena level2 = theReader.getArena();
//
// ArenaReader theReader = new ArenaReader("data/arenas/arena");
// Arena level1 = theReader.getArena(1);
// Arena level2 = theReader.getArena(2);
