/*
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
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
package net.runelite.client.plugins.playerindicators;

import java.awt.Color;
import java.util.function.BiConsumer;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class PlayerIndicatorsService
{
	private final Client client;

	private final PlayerIndicatorsConfig config;

	@Inject
	private PlayerIndicatorsService(Client client, PlayerIndicatorsConfig config)
	{
		this.config = config;
		this.client = client;
	}

	public void forEachPlayer(final BiConsumer<Player, Color> consumer)
	{
		if (!config.highlightOwnPlayer() && !config.drawClanMemberNames()
			&& !config.highlightFriends() && !config.highlightNonClanMembers())
		{
			return;
		}

		final Player localPlayer = client.getLocalPlayer();

		for (Player player : client.getPlayers())
		{
			if (player == null || player.getName() == null)
			{
				continue;
			}

			boolean isClanMember = player.isClanMember();

			if (player == localPlayer)
			{
				if (config.highlightOwnPlayer())
				{
					consumer.accept(player, config.getOwnPlayerColor());
				}
			}
			else if (config.highlightFriends() && player.isFriend())
			{
				consumer.accept(player, config.getFriendColor());
			}
			else if (config.drawClanMemberNames() && isClanMember)
			{
				consumer.accept(player, config.getClanMemberColor());
			}
			else if (config.highlightTeamMembers() && localPlayer.getTeam() > 0 && localPlayer.getTeam() == player.getTeam())
			{
				consumer.accept(player, config.getTeamMemberColor());
			}
			else if (config.highlightNonClanMembers() && !isClanMember && !config.hideNAP())
			{
				consumer.accept(player, config.getNonClanMemberColor());
			}
			else if (config.highlightNonClanMembers() && !isClanMember && config.hideNAP())
			{
				if (canAttack(player.getCombatLevel()))
				{
					consumer.accept(player, config.getNonClanMemberColor());
				}
			}
		}
	}

	public boolean canAttack(Integer target)
	{
		int combat = client.getLocalPlayer().getCombatLevel();

		if (client.getWorldType().equals(2))
		{
			int minimumP = combat - 15;
			int maximumP = combat + 15;

			if (target < minimumP || target > maximumP)
			{
				return false;
			}
		}

		if (isWidgetVisible(client.getWidget(WidgetInfo.WILDERNESS_CONTAINER)))
		{
			int wildLvl = Integer.valueOf(client.getWidget(WidgetInfo.WILDERNESS_LEVEL).getText().replace("Level: ", ""));
			if (wildLvl != 0) {
				int minimumW = (combat - wildLvl) <= 3 ? 3 : combat - wildLvl;
				int maximumW = (combat + wildLvl) >= 126 ? 126 : combat + wildLvl;

				if (target < minimumW || target > maximumW) {
					return false;
				}
			}
		}

		return true;
	}

	private boolean isWidgetVisible(Widget widget)
	{
		return widget != null && !widget.isHidden();
	}
}
