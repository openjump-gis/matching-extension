/*
 * (C) 2011 michael.michaud@free.fr
 */

package fr.michaelm.util.text.algo;

import fr.michaelm.util.*;
import static fr.michaelm.util.text.algo.DamarauLevenshteinDistance.*;

/**
 * Test class for DamarauLevenshteinDistanceTest
 *
 * @author Micha&euml;l Michaud
 * @version 0.1 (2011-08-08)
 */
// History
// 0.1 (2011-08-08)
public class DamarauLevenshteinDistanceTest extends AbstractTest {

    public static void main(String[] args) {
        //DamarauLevenshteinDistance.main(new String[0]);
        try {
            new DamarauLevenshteinDistanceTest().maintest();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    protected void maintest() throws Exception {
        //DamarauLevenshteinDistance.main(new String[0]);
        damlevtest();
        performanceTest();
    }

    private void damlevtest() {
        assertTrue("damlev(michael, mickael) = 1", damarauLevenshtein("michael", "mickael") == 1);
        assertTrue("damlev(mikael, mickael) = 1", damarauLevenshtein("mikael", "mickael") == 1);
        assertTrue("damlev(mickael, mikael) = 1", damarauLevenshtein("mickael", "mikael") == 1);
        assertTrue("damlev(mickael, mikcael) = 1", damarauLevenshtein("mickael", "mikcael") == 1);
        assertTrue("damlev(mikael, michael) = 2", damarauLevenshtein("mikael", "michael") == 2);
        assertTrue("damlev(michael, mechail) = 2", damarauLevenshtein("michael", "mechail") == 2);
        assertTrue("damlevlim(abcdefghij, klmnopqrst, 10) = 10", damarauLevenshtein("abcdefghij", "klmnopqrst", 10) == 10);
        assertTrue("damlevlim(abcdefghij, klmnopqrst, 2) = 2", damarauLevenshtein("abcdefghij", "klmnopqrst", 2) == 2);
    }

    private void performanceTest() {
        String[] strings = new String[100000];
        for (int i = 0 ; i < strings.length ; i++) {
            strings[i] = StringUtil.randomString(7, "abcdefghijklmnopqrstuvwxyz");
        }

        String ref = "miChael";

        long t0 = System.currentTimeMillis();
        for (int i = 0 ; i < 100000 ; i++) {
            DamarauLevenshteinDistance.levenshtein(ref, strings[i]);
        }
        System.out.println("100 000 distance avec levenshtein par défaut " + (System.currentTimeMillis()-t0) + "ms");

        t0 = System.currentTimeMillis();
        for (int i = 0 ; i < 100000 ; i++) {
            DamarauLevenshteinDistance.levenshtein(ref, strings[i], 3);
        }
        System.out.println("100 000 distance avec levenshtein limité à 3 " + (System.currentTimeMillis()-t0) + "ms");

        t0 = System.currentTimeMillis();
        for (int i = 0 ; i < 100000 ; i++) {
            DamarauLevenshteinDistance.damarauLevenshtein(ref, strings[i]);
        }
        System.out.println("100 000 distance avec damarau-levenshtein par défaut " + (System.currentTimeMillis()-t0) + "ms");

        t0 = System.currentTimeMillis();
        for (int i = 0 ; i < 100000 ; i++) {
            DamarauLevenshteinDistance.damarauLevenshtein(ref, strings[i], 3);
        }
        System.out.println("100 000 distance avec damarau-levenshtein limité à 3 " + (System.currentTimeMillis()-t0) + "ms");
    }

}