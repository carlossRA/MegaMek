/*
 * MegaMek - Copyright (C) 2005 Ben Mazur (bmazur@sev.org)
 *
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the Free
 *  Software Foundation; either version 2 of the License, or (at your option)
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 *  for more details.
 */

package megamek.common.event;

import megamek.common.Player;

/**
 * Instances of this class are sent when some Player connected
 */
public class GamePlayerConnectedEvent extends GamePlayerEvent {

    /**
     * 
     */
    private static final long serialVersionUID = -4745294587017447893L;

    /**
     * @param source
     * @param player
     */
    public GamePlayerConnectedEvent(Object source, Player player) {
        super(source, player, GAME_PLAYER_CONNECTED);
    }

}