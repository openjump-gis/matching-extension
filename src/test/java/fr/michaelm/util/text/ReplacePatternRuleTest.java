/*
 * (C) 2011 michael.michaud@free.fr
 */

package fr.michaelm.util.text;

import fr.michaelm.util.*;
import java.util.regex.Pattern;

/**
 * Test class for ReplacePatternRule
 *
 * @author Micha&euml;l Michaud
 * @version 0.1 (2011-05-01)
 */
// History
// 0.1 (2011-05-01)
public class ReplacePatternRuleTest extends AbstractTest {

    public static void main(String[] args) {
        new ReplacePatternRuleTest();
    }

    protected void maintest() throws Exception {
        testReplacePatternRule();
        //testCaseInsensitive();
        //testFrench();
        //performanceTest();
    }
    
    
    private void testReplacePatternRule() throws Exception {
        ReplacePatternRule rpr = new ReplacePatternRule("^.","?");
        System.out.println(rpr.toString());
        assertEquals("?oto", rpr.transform("toto"));
        assertEquals("?oto", rpr.transform("toto", null));
        rpr = new ReplacePatternRule(".$","?");
        System.out.println(rpr.toString());
        assertEquals("tot?", rpr.transform("toto"));
        assertEquals("tot?", rpr.transform("toto", null));
        rpr = new ReplacePatternRule(Pattern.compile("^."),"?");
        System.out.println(rpr.toString());
        assertEquals("?oto", rpr.transform("toto"));
        assertEquals("?oto", rpr.transform("toto", null));
        rpr = new ReplacePatternRule(Pattern.compile(".$"),"?");
        System.out.println(rpr.toString());
        assertEquals("tot?", rpr.transform("toto"));
        assertEquals("tot?", rpr.transform("toto", null));
    }
}
