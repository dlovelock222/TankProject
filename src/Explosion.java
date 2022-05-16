import java.awt.Graphics;

public class Explosion
{
	private int xLoc;
	private int yLoc;
	private ExplosionType type;
	public int frameCount;
	public boolean doneDrawing;

	private int drawTries;
	private int drawSlowMultiplier;

	private int srcFrameSize;
	private int dstFrameSize;
	private int objectSize;

	public Explosion(int inX, int inY, ExplosionType inType)
	{
		srcFrameSize = 134;
	
		type = inType;

		switch (type)
		{
			case LARGE: // for tanks dying
				objectSize = 50;
				dstFrameSize = 134;
				drawSlowMultiplier = 5;
				break;
			case MEDIUM: // for projectile dying (hitting a wall or a tank)
				objectSize = 0;
				dstFrameSize = 134 / 3;
				drawSlowMultiplier = 5;
				break;
			case SMALL: // for projectile shooting. draws at tip of turret
				objectSize = 0;
				dstFrameSize = 134 / 6;
				drawSlowMultiplier = 2;
				break;
		}

		xLoc = (inX + (objectSize / 2) - (dstFrameSize / 2));
		yLoc = (inY + (objectSize / 2) - (dstFrameSize / 2));

		frameCount = 0;

		drawTries = 0;
	}

	public void draw(Graphics g, ResourceLibrary l)
	{

		if (frameCount >= 12)
		{
			doneDrawing = true;
		} else
		{

			g.drawImage(l.explosion,
						// target rect
						xLoc,
						yLoc,
						xLoc + dstFrameSize,
						yLoc + dstFrameSize,
						// source rect from the animation strip
						frameCount * srcFrameSize,
						0,
						(frameCount * srcFrameSize) + srcFrameSize,
						srcFrameSize,
						// -----------
						null);
		}

		drawTries++;
		if (drawTries % drawSlowMultiplier == 0)
		{
			frameCount++;
		}
	}

}
