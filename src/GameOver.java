import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class GameOver
{

	private int score;
	private boolean gameWon;
	private GameMode gameMode;

	public GameOver(int inScore, boolean inGameWon, GameMode inGameMode, ResourceLibrary l)
	{

		score = inScore;
		gameWon = inGameWon;
		gameMode = inGameMode;

		l.playClip(l.K_gameOver);

	}

	public void draw(Graphics g, ResourceLibrary l)
	{
		g.drawImage(l.background, 0, 0, null);

		g.setColor(Color.BLACK); // Red colored rectangle
		g.fillRect(200, 100, 1000, 300); // Makes rectangle containing start
											// button

		g.setFont(new Font("TimesRoman", Font.PLAIN, 200)); // Times New Roman
															// font; size 200
		if (gameWon)
		{
			g.setColor(Color.GREEN); // Black colored text
			g.drawString("You Won!", 280, 260); // Constructs the text and draws
												// it on panel

			g.setFont(new Font("TimesRoman", Font.PLAIN, 50));
			g.drawString("You completed all 12 levels", 470, 365); // Constructs
																	// the text
																	// and draws
																	// it on
																	// panel

			g.setColor(Color.BLACK); // Red colored rectangle
			g.fillRect(500, 500, 380, 100); // Makes rectangle containing start
											// button
			g.setFont(new Font("TimesRoman", Font.PLAIN, 50)); // Times New
																// Roman font;
																// size 200
			g.setColor(Color.GREEN); // Black colored text
			g.drawString("Return to Menu", 525, 560); // Constructs the text and
														// draws it on panel

		} else
		{
			g.setColor(Color.RED); // Black colored text
			g.drawString("Game Over", 230, 260); // Constructs the text and
													// draws it on panel

			g.setFont(new Font("TimesRoman", Font.PLAIN, 50));
			if (gameMode.equals(GameMode.SURVIVAL))
			{
				// Constructs the text and draws it on panel
				g.drawString("You defeated " + score
							+ ((score == 1) ? " wave" : " waves"), 480, 365);
			} else
			{
				g.drawString("You completed " + score
							+ ((score == 1) ? " level" : " levels"), 480, 365);
			}

			g.setColor(Color.BLACK); // Red colored rectangle
			g.fillRect(500, 500, 380, 100); // Makes rectangle containing start
											// button
			g.setFont(new Font("TimesRoman", Font.PLAIN, 50)); // Times New
																// Roman font;
																// size 200
			g.setColor(Color.RED); // Black colored text
			g.drawString("Return to Menu", 525, 560); // Constructs the text and
														// draws it on panel
		}

	}

	public boolean clickedButton1(int x, int y)
	{
		if ((x >= 500 && x <= 880) && (y >= 500 && y <= 600))
		{
			return true;
		}
		return false;
	}
}
