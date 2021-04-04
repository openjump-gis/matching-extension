/*
 * (C) 2011 michael.michaud@free.fr
 */

package fr.michaelm.util;


import fr.michaelm.util.text.AbstractRuleTest;
import fr.michaelm.util.text.LanguageTest;
import fr.michaelm.util.text.ReplacePatternRuleSetTest;
import fr.michaelm.util.text.ReplacePatternRuleTest;
import fr.michaelm.util.text.algo.BKTreeTest;
import fr.michaelm.util.text.algo.DamarauLevenshteinDistanceTest;
import fr.michaelm.util.text.algo.WLevenshteinDistanceTest;

public class UtilTest extends AbstractTest {
    
    public static void main(String[] args) {
        new UtilTest();
    }

    protected void maintest() throws Exception {
        new CharUtilTest();
        new StringUtilTest();
        new FileUtilTest();
        new DSVUtilTest();
        new DSVFileSorterTest();
        
        new AbstractRuleTest();
        new ReplacePatternRuleTest();
        new ReplacePatternRuleSetTest();
        
        new WLevenshteinDistanceTest();
        new BKTreeTest();
        
        new DamarauLevenshteinDistanceTest();
        
        new LanguageTest();
        
    }

}
