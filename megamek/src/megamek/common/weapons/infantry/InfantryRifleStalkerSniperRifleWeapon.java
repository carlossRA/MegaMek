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
package megamek.common.weapons.infantry;

import megamek.common.AmmoType;

/**
 * @author Ben Grills
 */
public class InfantryRifleStalkerSniperRifleWeapon extends InfantryWeapon {

    /**
     *
     */
    private static final long serialVersionUID = -3164871600230559641L;

    public InfantryRifleStalkerSniperRifleWeapon() {
        super();

        name = "Rifle (Stalker Sniper Rifle)";
        setInternalName(name);
        addLookupName("StalkerInfantryRifle");
        ammoType = AmmoType.T_NA;
        cost = 5000;
        bv = .25;
        flags = flags.or(F_NO_FIRES).or(F_DIRECT_FIRE).or(F_BALLISTIC);
        infantryDamage = 0.25;
        infantryRange = 2;
        damage = 1;
        rulesRefs =" HBHL, 147";
		techAdvancement.setTechBase(TECH_BASE_ALL).setISAdvancement(3055, 3060, 3065, DATE_NONE, DATE_NONE)
		        .setISApproximate(false, false, false, false, false).setTechRating(RATING_C)
		        .setAvailability(RATING_X, RATING_X, RATING_D, RATING_D);

    }
}
