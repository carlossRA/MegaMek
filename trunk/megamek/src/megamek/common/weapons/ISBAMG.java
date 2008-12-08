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
package megamek.common.weapons;

import megamek.common.TechConstants;

/**
 * @author Sebastian Brocks
 */
public class ISBAMG extends BAMGWeapon {

    /**
     * 
     */
    private static final long serialVersionUID = -4420620461776813639L;

    /**
     * 
     */
    public ISBAMG() {
        super();
        this.techLevel = TechConstants.T_INTRO_BOXSET;
        this.name = "Machine Gun";
        this.setInternalName("BA Machine Gun");
        this.addLookupName("IS BA Machine Gun");
        this.addLookupName("ISBAMachine Gun");
        this.heat = 0;
        this.damage = 2;
        this.rackSize = 2;
        this.shortRange = 1;
        this.mediumRange = 2;
        this.longRange = 3;
        this.extremeRange = 4;
        this.tonnage = 0.5f;
        this.criticals = 1;
        this.bv = 5;
        this.cost = 5000;
        this.flags |= F_BA_WEAPON;
    }

}
