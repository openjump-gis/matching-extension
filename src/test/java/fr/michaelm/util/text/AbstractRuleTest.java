/*
 * (C) 2011 michael.michaud@free.fr
 */

package fr.michaelm.util.text;


import fr.michaelm.util.AbstractTest;

/**
 * Abstract class for all rules transforming a String into another String.
 *
 * @author Micha&euml;l Michaud
 * @version 0.1 (2011-05-01)
 */
// History
// 0.1 (2011-05-01)
public class AbstractRuleTest extends AbstractTest {

    public static void main(String[] args) {
        new AbstractRuleTest();
    }

    protected void maintest() throws Exception {
        transformTest();
    }
    
    private void transformTest() throws Exception {
        Rule rule = new AbstractRule() {
            public String transform(String s, Object context) {
                return context == null ? s : s + " (" + context + ")";
            }
        };
        assertEquals(rule.transform("Michaël"), "Michaël");
        assertEquals(rule.transform("Michaël", java.util.Locale.getDefault()), "Michaël (fr_FR)");
    }

}