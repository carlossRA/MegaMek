/**
 * MegaMek - Copyright (C) 2000-2002 Ben Mazur (bmazur@sev.org)
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

package megamek.client;

import megamek.common.*;

public class GameEvent
	extends java.util.EventObject
{
    //public static final int		GAME_PLAYER_CONNECTED		  = 0;
    //public static final int		GAME_PLAYER_DISCONNECTED	= 1;
    public static final int		GAME_PLAYER_STATUSCHANGE	= 2;
    	
    public static final int		GAME_PLAYER_CHAT			= 3;
    	
    public static final int		GAME_PHASE_CHANGE			= 4;
    public static final int		GAME_TURN_CHANGE			= 5;

    public static final int		GAME_NEW_ENTITIES		    = 6;
    public static final int		GAME_NEW_SETTINGS           = 7;
    	
    protected int				  type;
    protected Player			player;
    protected String			message;
	
	/**
	 * Construct game event
	 */
	public GameEvent(Object source, int type, Player player, String message) {
		super(source);
		this.type = type;
		this.player = player;
		this.message = message;
	}
	
	/**
	 * Return the player associated with this event, or null if
	 * not applicable.
	 */
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * Return the message associated with this event, or null
	 * if not applicable.
	 */
	public String getMessage() {
		return message;
	}
}