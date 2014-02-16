/*
 * MegaMek - Copyright (C) 2003 Ben Mazur (bmazur@sev.org)
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

package megamek.common;

import megamek.common.actions.WeaponAttackAction;

/**
 * Generated by a first pass through the weapon attack list.
 */
public class WeaponResult {
    public WeaponAttackAction waa = null;
    public ToHitData toHit = null; // stored before ammo depletion, jams
    public int roll = -1;
    public boolean revertsToSingleShot = false;
    public int[] amsShotDown = new int[0];
    public int amsShotDownTotal = 0;
}