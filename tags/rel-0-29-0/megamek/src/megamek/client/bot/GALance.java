/*
 * MegaMek - Copyright (C) 2002,2003 Ben Mazur (bmazur@sev.org)
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
 * GALance.java
 *
 * Created on May 30, 2002, 11:41 AM
 */

package megamek.client.bot;

import megamek.client.bot.ga.*;
import megamek.common.*;
import megamek.common.actions.*;

import com.sun.java.util.collections.*;

/**
 *
 * @author  Steve Hawkins
 */
public class GALance extends GA {
  
  protected Vector moves;
  protected TestBot tb;
  protected Object[] enemy_array;
  
  public GALance(TestBot tb, Vector moves, int population, int generations) throws GAException {
    super(moves.size(), population, .7, 5, generations, 0, 0, .5,
    Crossover.ctUniform, false, false);
    this.tb = tb;
    this.moves = moves;
    this.enemy_array = tb.getEnemyEntities().toArray();
    this.initPopulation();
  }
  
  protected Chromosome getNewChrom(int chromDim) {
    return new ChromVector(chromDim);
  }
  
  protected void initPopulation() {
    //promote max
    try {
      for (int iGene=0; iGene < chromosomeDim; iGene++) {
        ((ChromVector)this.chromosomes[0]).genes[iGene] = 0;
      }
      for (int i=1; i < populationDim; i++) {
        for (int iGene=0; iGene < chromosomeDim; iGene++) {
          ((ChromVector)this.chromosomes[i]).genes[iGene] =
          Compute.randomInt(((EntityState[])(moves.elementAt(iGene))).length);
        }
        this.chromosomes[i].fitness = getFitness(i);
      }
    } catch (Exception e) {
      System.out.println("Error occured with "+populationDim+" pop "+chromosomeDim+" chromDim");
      Iterator i = moves.iterator();
      while (i.hasNext()) {
        System.out.println(i.next());
      }
    }
  }

  //now they have a hard-coded hoard metality
  protected double getFitness(int iChromIndex) {
    ChromVector chromVector = (ChromVector)this.chromosomes[iChromIndex];
    Vector possible = new Vector();
    for (int iGene=0; iGene < chromosomeDim; iGene++) {
      possible.add(new EntityState(((EntityState[])this.moves.elementAt(iGene))[chromVector.genes[iGene]]));
    }
    Object[] move_array = possible.toArray();
    for (int e = 0; e < enemy_array.length; e++) { // for each enemy
      EntityState max = (EntityState)move_array[0];
      for(int m = 1; m < move_array.length; m++) {
        if (((EntityState)move_array[m]).threats[e] > max.threats[e]) {
          max = (EntityState)move_array[m];
        }
      }
      for(int m = 1; m < move_array.length; m++) {
        EntityState next = (EntityState)move_array[m];
        if (next.threats[e] > 0) {
          if (next.threats[e] < .25*max.threats[e]) {
            next.threats[e] = 0;
          } else {
            next.threats[e] = Math.pow(next.threats[e]/max.threats[e],2)*next.threats[e];
          }
        }
      }
    }
    //total damage delt, and rescaling of threat
    double damages[] = new double[enemy_array.length];
    for(int m = 0; m < move_array.length; m++) {
      EntityState next = (EntityState)move_array[m];
      next.threat = 0;
      for (int e = 0; e < enemy_array.length; e++) {
        next.threat += next.threats[e];
        damages[e] = (next.min_damages[e] + next.damages[e])/2;
      }
    }
    //sacrificial lamb check
    double result = 0;
    for(int m = 0; m < move_array.length; m++) {
      EntityState next = (EntityState)move_array[m];
      if (((EntityState[])moves.elementAt(m)).length > 1) {
        EntityState min = (EntityState)((EntityState[])moves.elementAt(m))[0];
        if (min.damage > 2*next.damage && min.getUtility() < .5*next.getUtility()) {
          result += next.centity.bv; //it is being endangered in the future
          if (m > 0) chromVector.genes[m]--; //go so far as to mutate the gene
        }
      }
    }
    int difference = this.tb.NumEnemies - this.tb.NumFriends;
    double distance_mod = 0;
    //if outnumbered and loosing, clump together.
    try {
      int target_distance = Math.max(8 - difference, 1);
      for(int m = 0; m < move_array.length; m++) {
        EntityState next = (EntityState)move_array[m];
        next.getUtility();
        for(int j = 0; j < move_array.length; j++) {
          EntityState other = (EntityState)move_array[j];
          if (m != j) {
            int distance = other.curPos.distance(next.curPos);
            if (distance > target_distance) {
              distance_mod += Math.pow(distance - target_distance, 2);
            } else if (distance <= 3) {
              boolean swarm = false;
              CEntity target = null;
              int target_index = 0;
              for (int e = 0; e < enemy_array.length; e++) {
                CEntity cen = tb.enemies.get((Entity)this.enemy_array[e]);
                if (!cen.canMove()) {
                  if ((cen.old.curPos.distance(next.curPos) == 1 && cen.old.curPos.distance(other.curPos) == 1)
                    || (cen.old.curPos.distance(next.curPos) <= 3 && cen.old.curPos.distance(other.curPos) <= 3 && cen.old.isProne)  && !(next.inDanger || next.isProne)) {
                    swarm = true;
                    target = cen;
                    target_index = e;
                  }
                }
              }
              if (swarm) {
                if (target.entity.isProne()) {
                  distance_mod -= target.bv/100;
                }
                distance_mod -= target.bv/50;
                next.damages[target_index] *= 1.2;
              }
            }
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    double max = 0;
    //bonuses for endangering or dooming opponent mechs
    for (int e = 0; e < enemy_array.length; e++) {
      CEntity cen = tb.enemies.get((Entity)this.enemy_array[e]);
      if (damages[e] > cen.avg_armor) {
        if (damages[e] > 4*cen.avg_armor) {
          max += cen.bv/5; //likely to die
        } else {
          max += cen.bv/50; //in danger
        }
      } else if (damages[e] > 40) {
        max += (1 - cen.base_psr_odds)*cen.entity.getWeight();
      }
    }
    //if noone is in danger at least give a bonus for clustering
    if (max == 0) {
      for (int e = 0; e < enemy_array.length; e++) {
        if (damages[e] > max) {
          max = damages[e];
        }
      }
    }
    distance_mod /= move_array.length*move_array.length;
    //less of a pull as you make moves
    distance_mod *= this.tb.my_mechs_moved/this.tb.NumFriends;
    
    for(int m = 0; m < move_array.length; m++) {
      EntityState next = (EntityState)move_array[m];
      if (next.inDanger) {
        if (next.Doomed) {
          if (next.centity.last != null && next.centity.last.Doomed) {
            result -= next.damage - .5*next.getUtility(); //should be dead
          } else if (next.centity.last != null && !next.centity.last.Doomed) {
            result += next.getUtility() + 2*next.damage; //don't like this case
          } else {
            result += next.getUtility();
          }
        } else {
          if (next.centity.last != null && !next.centity.last.inDanger) {
            result += next.getUtility() + next.damage; //not so good either
          } else {
            result += next.getUtility();
          }
        }
      } else {
        result += next.getUtility();
      }
    }
    return -result + (max - distance_mod);
  }
  
  public EntityState getResult() {
    ChromVector r = (ChromVector)this.chromosomes[bestFitnessChromIndex];
    Vector possible = new Vector();
    for (int iGene=0; iGene < chromosomeDim; iGene++) {
      possible.add(new EntityState(((EntityState[])this.moves.elementAt(iGene))[r.genes[iGene]]));
    }
    Object[] move_array = possible.toArray();
    EntityState result = null;
    for(int m = 0; m < move_array.length; m++) {
      EntityState next = (EntityState)move_array[m];
      CEntity cen = tb.enemies.get(next.entity);
      if (!cen.moved && (result == null || (next.getUtility() < result.getUtility()))) {
        result = next;
      }
    }
    /*for(int m = 0; m < move_array.length; m++) {
      EntityState next = (EntityState)move_array[m];
      CEntity cen = tb.enemies.get(next.entity);
      if (!cen.moved && ((EntityState[])this.moves.elementAt(m)).length < 6) {
        result = next;
      }
    }*/
    return result;
  }
  
  protected void doRandomMutation(int iChromIndex) {
    ChromVector c1 = (ChromVector)this.chromosomes[iChromIndex];
    // I don't think we need to mutate an empty chromosome
    if (c1.genes.length < 1) {
        return;
    }
    int r1 = (c1.genes.length > 2) ? Compute.randomInt(c1.genes.length - 1) : 0;
    if (r1%2 == 1) {
      c1.genes[r1] = Compute.randomInt(((EntityState[])this.moves.elementAt(r1)).length);
      return;
    }
    for (int i = 1; i < c1.genes.length; i++) {
      int iGene = (i + r1 - 1)%(c1.genes.length - 1);
      if (((EntityState[])this.moves.elementAt(iGene)).length > 1) {
        c1.genes[iGene] = Compute.randomInt(((EntityState[])this.moves.elementAt(iGene)).length);
        return;
      }
    }
  }
  
  protected void doUniformCrossover(Chromosome Chrom1, Chromosome Chrom2) {
    int gene = 0;
    ChromVector c1 = (ChromVector)Chrom1;
    ChromVector c2 = (ChromVector)Chrom2;
    for (int iGene=0; iGene < chromosomeDim; iGene++) {
      if (Compute.randomInt(2) == 1) {
        gene = c1.genes[iGene];
        c1.genes[iGene] = c2.genes[iGene];
        c2.genes[iGene] = gene;
      }
    }
  }
}