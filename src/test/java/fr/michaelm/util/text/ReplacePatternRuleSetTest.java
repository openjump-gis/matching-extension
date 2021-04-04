/*
 * (C) 2011 michael.michaud@free.fr
 */

package fr.michaelm.util.text;

import fr.michaelm.util.*;
import java.io.*;

/**
 * Test class for ReplacePatternRuleSet
 *
 * @author Micha&euml;l Michaud
 * @version 0.1 (2011-05-01)
 */
// History
// 0.1 (2011-05-01)
public class ReplacePatternRuleSetTest extends AbstractTest {

    public static void main(String[] args) {
        new ReplacePatternRuleSetTest();
    }

    protected void maintest() throws Exception {
        testReplacePatternRuleSet();
        testReplacePatternRuleSetFromFile();
        //testCaseInsensitive();
        //testFrench();
        //performanceTest();
    }
    
    private void testReplacePatternRuleSet() throws Exception {
        ReplacePatternRule rpr1 = new ReplacePatternRule("1","un");
        ReplacePatternRule rpr2 = new ReplacePatternRule("2","deux");
        ReplacePatternRule rpr3 = new ReplacePatternRule("3","trois");
        ReplacePatternRule rpr4 = new ReplacePatternRule("4","quatre");
        ReplacePatternRule rpr_ = new ReplacePatternRule("(\\d)(?=\\d)","$1-");
        ReplacePatternRuleSet rprs = new ReplacePatternRuleSet(rpr_, rpr1, rpr2, rpr3, rpr4);
        assertNotNull(rprs);
        assertEquals("deux-un-trois", rprs.transform("213"));
    }
    
    private void testReplacePatternRuleSetFromFile() throws Exception {
        ReplacePatternRuleSet rprs = new ReplacePatternRuleSet("src/test/resources/rules/phonetique.txt");
        assertNotNull(rprs);
        BufferedReader br = new BufferedReader(new FileReader("src/test/resources/tests/m3/util/inseeCommunes.txt"));
        BufferedWriter bw = new BufferedWriter(new FileWriter("src/test/resources/tests/m3/util/inseeCommunesPhonetic.txt"));
        String line;
        while (null != (line = br.readLine())) {
            if (line.length() > 0 && line.matches("^[0-9].*")) {
                String[] ss = line.split("\t");
                bw.write(line + "\t");
                bw.write(rprs.transform(StringUtil.cp1252toASCII38(ss[1])).toLowerCase());
                bw.newLine();
            }
            else {
                bw.write(line);
                bw.newLine();
            }
        }
        br.close();
        bw.close();
        assertTrue(new File("src/test/resources/tests/m3/util/inseeCommunesPhonetic.txt").exists());
    }
    
}
