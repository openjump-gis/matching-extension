/*
 * (c) 2011 michael.michaud@free.fr
 */
 
package fr.michaelm.util.text.algo;

/**
 * StringDistance interface has a single method returning a normalized distance
 * included in the range [0;1] between two Strings.
 *
 * @author Michael Michaud
 * @version 0.1 (2009-04-20)
 */
public interface StringDistance {
    
    /**
    * Distance between String s1 and String s2.
    * A distance of 0 means s1 equals s2 (equals may have a special meaning for
    * this distance definition).
    * A distance of 1 means s1 and s2 are totally different (what totally means
    * also depends on the distance definition).
    * @param s1 String to compare from
    * @param s2 String to compare to
    * @return a float value representing a normalized distance.
    */
    float distance(String s1, String s2);
    
}