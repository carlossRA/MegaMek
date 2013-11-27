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
 * MMRandom.java
 *
 * Created on April 27, 2003, 11:29 PM
 */

package megamek.common;

/**
 * Used by Compute to generate random numbers, usually dice rolls.
 *
 * The base class is abstract, having a number of concrete subclasses that it
 * will give using the generate() method.
 *
 * @author  Ben
 */
public abstract class MMRandom {
    
    public static final int R_DEFAULT   = 1;

    public static final int R_SUN       = 0;
    public static final int R_CRYPTO    = 1;
    public static final int R_POOL36    = 2;

    /**
     * Gives you the type asked for, defaulting to SunRandom if there are
     * any errors.
     */
    public static MMRandom generate(int type) {
        System.err.println("MMRandom: generating RNG type #" + type);
        try {
            switch (type) {
                case R_CRYPTO :
                    return new MMRandom.CryptoRandom();
                case R_POOL36 :
                    return new MMRandom.Pool36Random();
                case R_SUN :
                default :
                    return new MMRandom.SunRandom();
            }
        }
        catch (Exception ex) {
            System.err.println("MMRandom: could not create desired RNG #" + type);
            System.err.println("MMRandom: using SunRandom (#0) instead");
            
            return new MMRandom.SunRandom();
        }
    }
    
    
    /**
     * Simulates six-sided die rolls.
     */
    public int d6(int nDice) {
        int total = 0;
        for (int i = 0; i < nDice; i++) {
            total += randomInt(6) + 1;
        }
        return total;
    }

    /**
     * A single die
     */
    public int d6() {
        return d6(1);
    }
        
    /**
     * Returns a random <code>int</code> in the range from 0 to one
     * less than the supplied max value.
     *
     * @param   maxValue - the smallest <code>int</code> value which
     *          will exceed any random number returned by this method.
     * @return  a random <code>int</code> from the value set [0, maxValue).
     */
    public abstract int randomInt( int maxValue );
    
    
    /**
     * Uses com.sun.java.util.collections.Random
     */
    static class SunRandom extends MMRandom {
        com.sun.java.util.collections.Random random = new com.sun.java.util.collections.Random();

        public int randomInt(int maxValue) {
            return random.nextInt(maxValue);
        }
    }
    
    /**
     * Uses java.security.SecureRandom
     */
    static class CryptoRandom extends MMRandom {
        java.security.SecureRandom random;
        
        /**
         * Contruct, making a new thread to init the RNG
         */
        public CryptoRandom() throws ClassNotFoundException, NoSuchMethodException {
            // hack: just check to see if there's java.util.Random@nextInt(int)
            new java.util.Random().getClass().getMethod("nextInt", new Class[] {Integer.TYPE});
            
            // all clear, get on with the normal init
            random = new java.security.SecureRandom();
            
            Thread initRNG = new Thread( new Runnable() {
                    public void run() { 
                        random.nextInt(); 
                    }
                } );
            initRNG.start();
        }

        public int randomInt(int maxValue) {
            return random.nextInt(maxValue);
        }
    }
    
    /**
     * Behaves like SunRandom for everything but d6(2) calls.  Then, it takes
     * numbers from an array of the 36 possible results of two dice, shuffled.
     */
    static class Pool36Random extends SunRandom {
        public static final int NUM_SHUFFLES = 360;
        
        int[] pool = new int[36];
        int index = 0;
        
        public Pool36Random() {
            initPool();
            shufflePool();
        }
        
        /** Watches for 2 as nDice and then does its special thing. */
        public int d6(int nDice) {
            if (nDice != 2) {
                return super.d6(nDice);
            }
            // check pool
            if (index <= pool.length) {
                shufflePool();
            }
            // return next pool number
            return pool[index++];
        }
        
        /** Initializes the dice pool with the possible results of two dice. */
        void initPool() {
            index = 0;
            for (int i = 1; i <= 6; i++) {
                for (int j = 1; j <= 6; j++) {
                    pool[index++] = i + j;
                }
            }
        }
        
        /**
         * Swaps two of the numbers in the pool NUM_SHUFFLES times.  Resets
         * the index.  Uses the regular RNG to shuffle (OH NO!)
         */
        void shufflePool() {
            int temp, src, dest;
            
            // alakazam!
            for (int i = 0; i < NUM_SHUFFLES; i++) {
                src = this.randomInt(pool.length);
                dest = this.randomInt(pool.length);
                
                temp = pool[src];
                pool[src] = pool[dest];
                pool[dest] = temp;
            }
            
            // reset index
            index = 0;
        }
    }
    
}