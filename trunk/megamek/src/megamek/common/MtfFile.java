/*
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

/*
 * MtfFile.java
 *
 * Created on April 7, 2002, 8:47 PM
 */

package megamek.common;

import java.io.*;

/**
 *
 * @author  Ben
 * @version
 */
public class MtfFile implements MechLoader {
    
    String version;
    
    String name;
    String model;
    
    String chassisConfig;
    String techBase;
    String techYear;
    String rulesLevel;
    
    String tonnage;
    String engine;
    String internalType;
    String myomerType;
    
    String heatSinks;
    String walkMP;
    String jumpMP;
    
    String armorType;
    String larmArmor;
    String rarmArmor;
    String ltArmor;
    String rtArmor;
    String ctArmor;
    String headArmor;
    String llegArmor;
    String rlegArmor;
    String ltrArmor;
    String rtrArmor;
    String ctrArmor;
    
    String weaponCount;
    String[] weaponData;
    
    String[][] critData;
    
    
    /** Creates new MtfFile */
    public MtfFile(File file) throws EntityLoadingException {
        try {
            BufferedReader r = new BufferedReader(new FileReader(file));
            
            version = r.readLine();

            name = r.readLine();
            model = r.readLine();
            
            r.readLine();
            
            chassisConfig = r.readLine();
            techBase = r.readLine();
            techYear = r.readLine();
            rulesLevel = r.readLine();
            
            r.readLine();
            
            tonnage = r.readLine();
            engine = r.readLine();
            internalType = r.readLine();
            myomerType = r.readLine();
            
            r.readLine();
            
            heatSinks = r.readLine();
            walkMP = r.readLine();
            jumpMP = r.readLine();
            
            r.readLine();
            
            armorType = r.readLine();
            larmArmor = r.readLine();
            rarmArmor = r.readLine();
            ltArmor = r.readLine();
            rtArmor = r.readLine();
            ctArmor = r.readLine();
            headArmor = r.readLine();
            llegArmor = r.readLine();
            rlegArmor = r.readLine();
            ltrArmor = r.readLine();
            rtrArmor = r.readLine();
            ctrArmor = r.readLine();
            
            r.readLine();
            
            weaponCount = r.readLine();
            
            int a = 9;
            
            int weapons = Integer.parseInt(weaponCount.substring(8));
            weaponData = new String[weapons];
            for(int i = 0; i < weapons; i++) {
                weaponData[i] = r.readLine();
            }
            
            critData = new String[8][12];
            
            readCrits(r, Mech.LOC_LARM);
            readCrits(r, Mech.LOC_RARM);
            readCrits(r, Mech.LOC_LT);
            readCrits(r, Mech.LOC_RT);
            readCrits(r, Mech.LOC_CT);
            readCrits(r, Mech.LOC_HEAD);
            readCrits(r, Mech.LOC_LLEG);
            readCrits(r, Mech.LOC_RLEG);
            
            r.close();
        } catch (IOException ex) {
            throw new EntityLoadingException("I/O Error reading file");
        } catch (StringIndexOutOfBoundsException ex) {
            throw new EntityLoadingException("StringIndexOutOfBoundsException reading file (format error)");
        } catch (NumberFormatException ex) {
            throw new EntityLoadingException("NumberFormatException reading file (format error)");
        }
    }
    
    private void readCrits(BufferedReader r, int loc) throws IOException {
        r.readLine(); // blank line
        r.readLine(); // location name.... verify?
        
        for (int i = 0; i < 12; i++) {
            critData[loc][i] = r.readLine();
        }
    }
    
    public Entity getEntity() throws EntityLoadingException {
        try {
            Mech mech;
            
            if (chassisConfig.indexOf("Quad") != -1) {
                mech = new QuadMech();
            } else {
                mech = new BipedMech();
            }

            if (!version.trim().equalsIgnoreCase("Version:1.0")) {
                throw new EntityLoadingException("Wrong MTF file version.");
            }

            // aarg!  those stupid sub-names in parenthesis screw everything up
            // we may do something different in the future, but for now, I'm
            // going to strip them out
            int pindex = name.indexOf("(");
            if (pindex == -1) {
                mech.setName(name.trim());
            } else {
                mech.setName(name.substring(0, pindex - 1).trim());
            }
            mech.setModel(model.trim());
            mech.setYear(Integer.parseInt(this.techYear.substring(4).trim()));
            if (chassisConfig.indexOf("Omni") != -1) {
                mech.setOmni(true);
            }

            //TODO: this ought to be a better test
            boolean innerSphere = "Inner Sphere".equals(this.techBase.substring(9).trim());
            switch (Integer.parseInt(rulesLevel.substring(12).trim())) {
                case 1 :
                    mech.setTechLevel(TechConstants.T_IS_LEVEL_1);
                    break;
                case 2 :
                    mech.setTechLevel(innerSphere ? TechConstants.T_IS_LEVEL_2
                                                  : TechConstants.T_CLAN_LEVEL_2);
                    break;
                default :
                    throw new EntityLoadingException("Unsupported tech level");
            }

            mech.weight = (float)Integer.parseInt(tonnage.substring(5));

            mech.setOriginalWalkMP(Integer.parseInt(walkMP.substring(8)));
            mech.setOriginalJumpMP(Integer.parseInt(jumpMP.substring(8)));

            boolean dblSinks = heatSinks.substring(14).equalsIgnoreCase("Double");
            mech.addEngineSinks(Integer.parseInt(heatSinks.substring(11, 14).trim()), dblSinks);

            mech.autoSetInternal();

            mech.initializeArmor(Integer.parseInt(larmArmor.substring(9)), Mech.LOC_LARM);
            mech.initializeArmor(Integer.parseInt(rarmArmor.substring(9)), Mech.LOC_RARM);
            mech.initializeArmor(Integer.parseInt(ltArmor.substring(9)), Mech.LOC_LT);
            mech.initializeArmor(Integer.parseInt(rtArmor.substring(9)), Mech.LOC_RT);
            mech.initializeArmor(Integer.parseInt(ctArmor.substring(9)), Mech.LOC_CT);
            mech.initializeArmor(Integer.parseInt(headArmor.substring(9)), Mech.LOC_HEAD);
            mech.initializeArmor(Integer.parseInt(llegArmor.substring(9)), Mech.LOC_LLEG);
            mech.initializeArmor(Integer.parseInt(rlegArmor.substring(9)), Mech.LOC_RLEG);
            mech.initializeRearArmor(Integer.parseInt(ltrArmor.substring(10)), Mech.LOC_LT);
            mech.initializeRearArmor(Integer.parseInt(rtrArmor.substring(10)), Mech.LOC_RT);
            mech.initializeRearArmor(Integer.parseInt(ctrArmor.substring(10)), Mech.LOC_CT);

            // oog, crits.
            for (int i = 0; i < mech.locations(); i++) {
                parseCrits(mech, i);
            }

            if (mech.isClan()) {
                mech.addClanCase();
            }

            return mech;
        } catch (NumberFormatException ex) {
            throw new EntityLoadingException("NumberFormatException parsing file");
        } catch (NullPointerException ex) {
            throw new EntityLoadingException("NullPointerException parsing file");
        } catch (StringIndexOutOfBoundsException ex) {
            throw new EntityLoadingException("StringIndexOutOfBoundsException parsing file");
        }
    }
    
    private void parseCrits(Mech mech, int loc) throws EntityLoadingException {
        // check for removed arm actuators
        if (!(mech instanceof QuadMech)) {
            if (loc == Mech.LOC_LARM || loc == Mech.LOC_RARM) {
                if (!critData[loc][3].equals("Hand Actuator")) {
                    mech.setCritical(loc, 3, null);
                }
                if (!critData[loc][2].equals("Lower Arm Actuator")) {
                    mech.setCritical(loc, 2, null);
                }
            }
        }
        
        // go thru file, add weapons
        for (int i = 0; i < mech.getNumberOfCriticals(loc); i++) {
            // if the slot's full already, skip it.
            if (mech.getCritical(loc, i) != null) {
                continue;
            }
            
            // parse out and add the critical
            String critName = critData[loc][i];
            boolean rearMounted = false;
            
            if (critName.equalsIgnoreCase("Fusion Engine")) {
                mech.setCritical(loc,i, new CriticalSlot(CriticalSlot.TYPE_SYSTEM, 3));
            }
            if (critName.endsWith("(R)")) {
                rearMounted = true;
                critName = critName.substring(0, critName.length() - 3).trim();
            }
            if (critName.endsWith("(Split)")) {
                throw new EntityLoadingException("Split weapons not supported (yet)");
            }
            
            EquipmentType etype = EquipmentType.getByMtfName(critName);
            if (etype != null) {
                try {
                    mech.addEquipment(etype, loc, rearMounted);
                } catch (LocationFullException ex) {
                    throw new EntityLoadingException(ex.getMessage());
                }
            }
        }
    }
}
