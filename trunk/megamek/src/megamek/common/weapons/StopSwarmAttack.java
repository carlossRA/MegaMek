/**
 * MegaMek - Copyright (C) 2004,2005 Ben Mazur (bmazur@sev.org)
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
/*
 * Created on Sep 7, 2005
 *
 */
package megamek.common.weapons;

import megamek.common.Infantry;

/**
 * @author Sebastian Brocks
 */
public class StopSwarmAttack extends InfantryAttack {

    /**
     * 
     */
    private static final long serialVersionUID = -5682796365154321224L;

    public StopSwarmAttack() {
        super();
        this.name = "Stop Swarm Attack";
        this.setInternalName(Infantry.STOP_SWARM);
    }
}
