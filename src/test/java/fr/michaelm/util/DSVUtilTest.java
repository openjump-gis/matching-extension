/*
 * (C) 2011 michael.michaud@free.fr
 */

package fr.michaelm.util;

import static fr.michaelm.util.DSVUtil.*;
import java.io.*;
import java.util.regex.Pattern;

public class DSVUtilTest extends AbstractTest {
    
    public static void main(String[] args) {
        new DSVUtilTest();
    }

    protected void maintest() throws Exception {
        guessDelimiterTest();
    }
    
    private void guessDelimiterTest() throws Exception {
        // Single line (default delimiter is TAB)
        String s = "abcdefghijklmnopqrstuvwxyz";
        assertEquals(guessDelimiterFromString(s), TAB);
        
        // Single line with whitespace separator
        s = "abc def ghi jkl";
        assertEquals(guessDelimiterFromString(s), WHITESPACE);
        
        // Single line with 3 tabulations and more whitespaces
        s = "a b\tc d e f \tg h i\t j k l";
        assertEquals(guessDelimiterFromString(s), WHITESPACE);
        
        // Irregular number of whitespace delimiters -> default tab delimiter
        s = "abc def" + "\nabc def ghi" + "\nabc def ghi jkl";
        assertEquals(guessDelimiterFromString(s), TAB);
        
        // Blank lines are ignored
        s = "\n\n\nabc def ghi jkl\n\n\n";
        assertEquals(guessDelimiterFromString(s), WHITESPACE);
        
        // Normal semicolon delimited file
        s = "abc;def;ghi" + "\nabc;def;ghi" + "\nabc;def;ghi";
        assertEquals(guessDelimiterFromString(s), SEMICOLON);
        
        // mixed semicolon (regular) and tabulation (irregular) delimited lines
        s = "ab\tc;def;ghi" + "\na\tbc;de\tf;ghi" + "\nabc\t;d\tef;ghi";
        assertEquals(guessDelimiterFromString(s), SEMICOLON);
    }
    
    private Pattern guessDelimiterFromString(String s) throws Exception {
        File file = File.createTempFile("TestDSVUtil", null);
        file.deleteOnExit();
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.append(s);
        bw.flush();
        Pattern pattern = guessDelimiter(file.getPath());
        bw.close();
        return pattern;
    }


}
