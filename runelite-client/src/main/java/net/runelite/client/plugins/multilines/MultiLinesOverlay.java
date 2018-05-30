package net.runelite.client.plugins.multilines;

import net.runelite.api.Client;
import net.runelite.api.Constants;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;

public class MultiLinesOverlay extends Overlay
{
	private final static int MAX_LOCAL_DRAW_LENGTH = 15 * Perspective.LOCAL_TILE_SIZE;
	@Inject
	private Client client;

	@Inject
	private MultiLinesPlugin plugin;

	@Inject
	MultiLinesConfig config;

	public MultiLinesOverlay()
	{
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		setPriority(OverlayPriority.LOW);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{

		int lowX = client.getBaseX() + 1;
		int lowY = client.getBaseY() + 1;
		int highX = lowX + Constants.REGION_SIZE - 2;
		int highY = lowY + Constants.REGION_SIZE - 2;

		graphics.setColor(config.getMultiLineColor());

		LocalPoint playerLP = client.getLocalPlayer().getLocalLocation();
		plugin.getLinesToDisplay().forEach(x ->
			{
				int startWorldX = Math.max(x.getStart().getX(), lowX);
				int startWorldY = Math.max(x.getStart().getY(), lowY);
				int endWorldX = Math.max(x.getEnd().getX(), lowX);
				int endWorldY = Math.max(x.getEnd().getY(), lowY);
				startWorldX = Math.min(x.getStart().getX(), highX);
				startWorldY = Math.min(x.getStart().getY(), highY);
				endWorldX = Math.min(x.getEnd().getX(), highX);
				endWorldY = Math.min(x.getEnd().getY(), highY);

				LocalPoint startLP = LocalPoint.fromWorld(client, startWorldX, startWorldY);
				LocalPoint endLP = LocalPoint.fromWorld(client, endWorldX, endWorldY);

				int startLocalX = startLP.getX();
				int startLocalY = startLP.getY();
				int endLocalX = endLP.getX();
				int endLocalY = endLP.getY();

				startLocalX = Math.max(startLocalX, playerLP.getX() - MAX_LOCAL_DRAW_LENGTH);
				startLocalY = Math.max(startLocalY, playerLP.getY() - MAX_LOCAL_DRAW_LENGTH);
				endLocalX = Math.min(endLocalX, playerLP.getX() + MAX_LOCAL_DRAW_LENGTH);
				endLocalY = Math.min(endLocalY, playerLP.getY() + MAX_LOCAL_DRAW_LENGTH);
				startLocalX = Perspective.LOCAL_TILE_SIZE / 2;
				startLocalY = Perspective.LOCAL_TILE_SIZE / 2;
				endLocalX = Perspective.LOCAL_TILE_SIZE / 2;
				endLocalY = Perspective.LOCAL_TILE_SIZE / 2;

				Point p1 = Perspective.worldToCanvas(client, startLocalX, startLocalY, client.getPlane());
				Point p2 = Perspective.worldToCanvas(client, endLocalX, endLocalY, client.getPlane());

				if (p1 != null && p2 != null)
				{
					graphics.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
				}
			}
		);
		return null;
	}
}
