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

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.Point;
import net.runelite.api.widgets.WidgetInfo;

@Getter
@AllArgsConstructor
enum Viewport //24
{
	RESIZED_BOX(WidgetInfo.RESIZABLE_VIEWPORT_OLD_SCHOOL_BOX, 161, 65, new Point(22, -8), new Point(2, - 8)),
	RESIZED_BOTTOM(WidgetInfo.RESIZABLE_VIEWPORT_BOTTOM_LINE, 164, 65, new Point(49, 0), new Point(30, 0)),
	FIXED(WidgetInfo.FIXED_VIEWPORT, 548, 65, new Point(26, -4), new Point(6, - 4));

	private WidgetInfo viewport;
	private int group;
	private int child;
	private Point offsetLeft;
	private Point offsetRight;

}