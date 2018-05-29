/*
 * Copyright (c) 2018, Jos <Malevolentdev@gmail.com>
 * Creation date : 26-5-2018
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.hpandprayerstatus;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import javax.inject.Inject;

@Slf4j
public class HpAndStatusOverlay extends Overlay
{
	private Client c;
	private HpAndPrayerConfig config;
	private SpriteManager sm;

	final private int HEIGHT = 224;
	final private int WIDTH = 15;
	final private int PADDING = 1;
	final private int IMAGE_SIZE = 17;
	// Static colors
	final Color DEPLETED = new Color(25, 25, 25);
	final Color PRAYER_LEFT = new Color(50, 200, 200);
	final Color BACKGROUND = new Color(0, 0 , 0);
	// Dynamic colors
	Color HEALTH_LEFT = new Color(200, 35, 0);
	Color POISONED_HEALTH_LEFT = new Color(0, 145, 0);
	Color VENOMED_HEALTH_LEFT = new Color(0, 65, 0);
	Color COUNTER_COLOR = new Color(0, 0, 0, 255);

	@Inject
	public HpAndStatusOverlay(Client c, SpriteManager sm, HpAndPrayerConfig config)
	{
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		this.c = c;
		this.sm = sm;
		this.config = config;
	}

	@Override
	public Dimension render(Graphics2D g)
	{
		renderInventoryBars(g);
		return null;
	}

	private void renderInventoryBars(Graphics2D g)
	{
		//Variables
		int max_health = c.getRealSkillLevel(Skill.HITPOINTS);
		int max_prayer = c.getRealSkillLevel(Skill.PRAYER);
		int current_health = c.getBoostedSkillLevel(Skill.HITPOINTS);
		int current_prayer = c.getBoostedSkillLevel(Skill.PRAYER);
		int poison_state = c.getVar(VarPlayer.IS_POISONED);
		int counter_health = c.getBoostedSkillLevel(Skill.HITPOINTS);
		int counter_prayer = c.getBoostedSkillLevel(Skill.PRAYER);
		//Store images
		BufferedImage healthImage = sm.getSprite(SpriteID.SKILL_HITPOINTS, 0);
		BufferedImage prayerImage = sm.getSprite(SpriteID.SKILL_PRAYER, 0);
		BufferedImage chatboxImage = sm.getSprite(SpriteID.CHATBOX, 0);

		//Code for counters
		if (config.enableCounter())
		{
			healthImage = chatboxImage;
			prayerImage = chatboxImage;
			COUNTER_COLOR = new Color ( 0, 0, 0,  255);
		}
		else
		{
			COUNTER_COLOR = new Color ( 0, 0, 0,  0);
		}

		// If the player gets inflicted with poison or venom the colors will be replaced to indicate this status.
		if (poison_state > 0 && poison_state < 37)
		{
			HEALTH_LEFT = POISONED_HEALTH_LEFT;
		}
		else if (poison_state > 1000000)
		{
			HEALTH_LEFT = VENOMED_HEALTH_LEFT;
			POISONED_HEALTH_LEFT = VENOMED_HEALTH_LEFT;
		}
		else
		{
			//reset colors if not poisoned anymore.
			HEALTH_LEFT = new Color(200, 35, 0);
			POISONED_HEALTH_LEFT = new Color(0, 145, 0);
		}

		for (Viewport viewport : Viewport.values())
		{
			Widget viewportWidget = c.getWidget(viewport.getViewport());
			if (viewportWidget != null && !viewportWidget.isHidden())
			{
				Widget widget = c.getWidget(viewport.getGroup(), viewport.getChild());
				Point location = widget.getCanvasLocation();
				Point offsetLeft = viewport.getOffsetLeft();
				Point offsetRight = viewport.getOffsetRight();

				int offsetPrayerX;
				int offsetHealthX = (location.getX() - offsetLeft.getX());

				if (viewport == Viewport.RESIZED_BOTTOM && !widget.isHidden())
				{
					offsetPrayerX = (location.getX() - offsetRight.getX());
				}
				else if (viewport == Viewport.RESIZED_BOTTOM && widget.isHidden())
				{
					return;
				}
				else
				{
					offsetPrayerX = (location.getX() + widget.getWidth() - offsetRight.getX());
				}

				// Render the HP and Prayer bar
				renderBar(g, offsetHealthX, (location.getY() - offsetLeft.getY()),
						max_health, current_health, WIDTH, HEIGHT,
						PADDING, HEALTH_LEFT, healthImage.getScaledInstance(IMAGE_SIZE + 4, IMAGE_SIZE + 3, Image.SCALE_AREA_AVERAGING), COUNTER_COLOR , counter_health);

				renderBar(g, offsetPrayerX, (location.getY() - offsetRight.getY()),
						max_prayer, current_prayer, WIDTH, HEIGHT,
						PADDING, PRAYER_LEFT, prayerImage.getScaledInstance(IMAGE_SIZE + 4, IMAGE_SIZE + 3, Image.SCALE_AREA_AVERAGING), COUNTER_COLOR , counter_prayer);
			}
		}
	}

	private void renderBar(Graphics2D graphics, int x, int y, int max, int current, int width, int height, int padding, Color filled, Image image, Color counter_color, int counter)
	{
		//draw icons
		graphics.drawImage(image, x - 4 + padding, y - 2 - image.getWidth(null), null);
		//draw background
		graphics.setColor(BACKGROUND);
		graphics.fillRect(x, y, width, height);
		//draw bar background
		graphics.setColor(DEPLETED);
		graphics.fillRect(x + padding, y + padding, width - padding * 2, height - padding * 2);
		//draw counter
		graphics.setColor(counter_color);
		graphics.drawString(Integer.toString(counter), x - 2 + padding, y - 6);
		//draw bar with current health or prayerpoints
		int filledHeight = getBarHeight(max, current, height);
		graphics.setColor(filled);
		graphics.fillRect(x + padding, y + padding + (height - filledHeight), width - padding * 2, filledHeight - padding * 2);
	}

	//calculate bar height with set size in mind
	private int getBarHeight(int base, int current, int size)
	{
		double ratio = (double) current / (double) base;

		if (ratio >= 1)
		{
			return size;
		}
		else
		{
			return (int) Math.round((ratio * size));
		}
	}
}