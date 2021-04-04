/*
 * (C) 2011 michael.michaud@free.fr
 */

package fr.michaelm.util;

import static fr.michaelm.util.CharUtil.*;

public class CharUtilTest extends AbstractTest {

    public static void main(String[] args) {
        new CharUtilTest();
    }

    protected void maintest() throws Exception {
        currentCodePageCharactersTest();
        //System.out.println("\nPrint CP1252_TO_ASCII table");
        //printTable("windows-1252", CP1252_TO_ASCII);
        //System.out.println("\nPrint CP1252_TO_ASCII38 table");
        //printTable("windows-1252", CP1252_TO_ASCII38);
        transposeTest();
        userTableTest();
    }

    private void currentCodePageCharactersTest() {
        //System.out.println("\nTest current codepage output");
        //currentCodePageCharacters();
    }

    private void transposeTest() {
        try {
            assertEquals(transpose("abcdefghijklmnopqrstuvwxyz", "windows-1252", CP1252_TO_ASCII), "abcdefghijklmnopqrstuvwxyz");
            assertEquals(transpose("ABCDEFGHIJKLMNOPQRSTUVWXYZ", "windows-1252", CP1252_TO_ASCII), "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
            assertEquals(transpose("0123456789",                 "windows-1252", CP1252_TO_ASCII), "0123456789");
            assertEquals(transpose("&é\"'(-è_çà)=~#{[|`\\^@]}",  "windows-1252", CP1252_TO_ASCII), "&e\"'(-e_ca)=~#{[|`\\^@]}");
            assertEquals(transpose("abcdefghijklmnopqrstuvwxyz", "windows-1252", CP1252_TO_ASCII38), "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
            assertEquals(transpose("ABCDEFGHIJKLMNOPQRSTUVWXYZ", "windows-1252", CP1252_TO_ASCII38), "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
            assertEquals(transpose("0123456789",                 "windows-1252", CP1252_TO_ASCII38), "0123456789");
            assertEquals(transpose("&é\"'(-è_çà)=~#{[|`\\^@]}",  "windows-1252", CP1252_TO_ASCII38), "/E  / E CA/   /// /  //");
        } catch(java.io.UnsupportedEncodingException uee) {}
    }

    private void userTableTest() {
        try {
            String[] changeEt = setTableElement(createTable("windows-1252"), 38, "et");
            assertEquals(transpose("obélix & compagnie", "windows-1252", changeEt), "obélix et compagnie");
        } catch(java.io.UnsupportedEncodingException uee) {}
    }

}
