/*
 * (C) 2011 michael.michaud@free.fr
 */

package fr.michaelm.util.text;

import fr.michaelm.util.AbstractTest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import static fr.michaelm.util.text.Language.*;

/**
 * Test class for Language
 *
 * @author Micha&euml;l Michaud
 * @version 0.1 (2011-08-09)
 */
// History
// 0.1 (2011-08-09)
public class LanguageTest extends AbstractTest {

    public static void main(String[] args) {
        new LanguageTest();
    }

    protected void maintest() throws Exception {
        System.out.println(Language.INDO_EUROPEEN);
        System.out.println(Language.ROMAN);
        System.out.println(Language.CORSE);
        System.out.println(Language.GASCON);
        testConstructor();
        areComparableTest();
        guessLanguageTest();
    }
    
    public void testConstructor() {
        Language lang = new Language("Langue inventée");
        assertEquals(lang.toString(), "Langue inventée");
        assertEquals(lang.getLevel(), 1);
        lang = new Language("Sartenais", Language.CORSE);
        assertEquals(lang.toString(), "Indo-européen/Roman/Italo-roman/Corse/Sartenais");
        assertEquals(lang.getLevel(), 5);
    }
    
    public void areComparableTest() {
        assertTrue(areComparable(Language.ROMAN, Language.ROMAN));
        assertTrue(areComparable(Language.ROMAN, Language.CORSE));
        assertTrue(areComparable(Language.CORSE, Language.ROMAN));
        assertFalse(areComparable(Language.ROMAN, Language.CELTE));   
        assertFalse(areComparable(Language.BRETON, Language.CORSE));
    }
    
    public void guessLanguageTest() throws Exception {
        //ReplacePatternRuleSet rprs = new ReplacePatternRuleSet("./resources/rules/phonetique.txt");
        //assertNotNull(rprs);
        BufferedReader br = new BufferedReader(new FileReader("src/test/resources/tests/m3/util/inseeCommunes.txt"));
        BufferedWriter bw = new BufferedWriter(new FileWriter("src/test/resources/tests/m3/util/inseeCommunesGuessLanguage.txt"));
        String line;
        while (null != (line = br.readLine())) {
            if (line.length() > 0 && line.matches("^[0-9].*")) {
                String[] ss = line.split("\t");
                bw.write(line + "\t");
                bw.write(Language.guessLanguage(ss[1]).getName());
                bw.newLine();
            }
            else {
                bw.write(line);
                bw.newLine();
            }
        }
        br.close();
        bw.close();
        assertTrue(new File("src/test/resources/tests/m3/util/inseeCommunesGuessLanguage.txt").exists());
    }
    
}
