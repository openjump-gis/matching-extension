/*
 * (C) 2011 michael.michaud@free.fr
 */

package fr.michaelm.util.text.algo;

import fr.michaelm.util.*;

/**
 * Test class for WLevenshteinDistanceTest
 *
 * @author Micha&euml;l Michaud
 * @version 0.1 (2011-05-01)
 */
// History
// 0.1 (2011-05-01)
public class WLevenshteinDistanceTest extends AbstractTest {

    public static void main(String[] args) {
        new WLevenshteinDistanceTest();
    }

    protected void maintest() throws Exception {
        testDefault();
        testCaseInsensitive();
        testFrench();
        performanceTest();
    }

    private void testDefault() {
        WLevenshteinDistance WLD = new WLevenshteinDistance();
        // idem
        assertTrue(WLD.editDistance("Michael","Michael") == 0);
        // creation
        assertTrue(WLD.editDistance("Michael","Michael ") == 1);
        // suppression
        assertTrue(WLD.editDistance("Michael","Michae") == 1);
        // modification
        assertTrue(WLD.editDistance("Michael","michael") == 1);
        // creation (offset)
        assertTrue(WLD.editDistance("abcdef","0abcdef") == 1);
        // various modification
        assertTrue(WLD.editDistance("une phrase au hasard","une fraise au hazar") == 5);
    }

    private void testCaseInsensitive() {
        WLevenshteinDistance WLD = new WLevenshteinDistance(CostFunctions.caseIgnore);
        // idem
        assertTrue(WLD.editDistance("Michael","Michael") == 0);
        // modification
        assertTrue(WLD.editDistance("Michael","michael") == 0);
        // various modification
        assertTrue(WLD.editDistance("Une Phrase Au Hasard","une fraise au hazar") == 5);
    }

    private void testFrench() {
        WLevenshteinDistance WLD = new WLevenshteinDistance(CostFunctions.frenchCosts);
        // idem
        assertEquals("Michael/Michael", WLD.editDistance("Michael","Michael"), 0);
        assertEquals("Michael/michael", WLD.editDistance("Michael","michael"), 0);
        assertEquals("Michael/michaël", WLD.editDistance("Michael","michaël"), 1);
        assertEquals("Michael/nichael", WLD.editDistance("Michael","nichael"), 2);
        assertEquals("Michael/michaïl", WLD.editDistance("Michael","michaïl"), 2);
        assertEquals("Michael/mishaïl", WLD.editDistance("Michael","mishaïl"), 4);
        assertEquals("Michael/mikhaïl", WLD.editDistance("Michael","mikhaïl"), 4);
        assertEquals("Michael/Lichael", WLD.editDistance("Michael","Lichael"), 5);
        // zero instead of O
        assertEquals("Loto/L0t0", WLD.editDistance("Loto","L0t0"), 2);
    }

    private void performanceTest() {
        String[] strings = new String[10000];
        for (int i = 0 ; i < strings.length ; i++) {
            strings[i] = StringUtil.randomString(7, "abcdefghijklmnopqrstuvwxyz");
        }
        WLevenshteinDistance WLD = new WLevenshteinDistance();
        WLevenshteinDistance WLD1 = new WLevenshteinDistance(CostFunctions.caseIgnore);
        WLevenshteinDistance WLD2 = new WLevenshteinDistance(CostFunctions.frenchCosts);
        String ref = "miChael";
        long t0 = System.currentTimeMillis();
        for (int i = 0 ; i < 10000 ; i++) {
            WLD.editDistance(ref, strings[i]);
        }
        System.out.println("10000 distance avec WLD par défaut " + (System.currentTimeMillis()-t0) + "ms");
        t0 = System.currentTimeMillis();
        for (int i = 0 ; i < 10000 ; i++) {
            WLD1.editDistance(ref, strings[i]);
        }
        System.out.println("10000 distance avec WLD1 " + (System.currentTimeMillis()-t0) + "ms");
        t0 = System.currentTimeMillis();
        for (int i = 0 ; i < 10000 ; i++) {
            WLD2.editDistance(ref, strings[i]);
        }
        System.out.println("10000 distance avec WLD2 " + (System.currentTimeMillis()-t0) + "ms");
    }

}