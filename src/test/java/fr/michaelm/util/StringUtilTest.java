/*
 * (C) 2011 michael.michaud@free.fr
 */

package fr.michaelm.util;

import static fr.michaelm.util.StringUtil.*;

public class StringUtilTest extends AbstractTest {

    public static void main(String[] args) {
        new StringUtilTest();
    }

    protected void maintest() throws Exception {
        isNullOrEmptyTest();
        equalsTest();
        normalizeTest();
        splitTest();
        cp1252toASCIITest();
        cp1252toASCII38Test();
        inverseStringLengthSortTest();
        removeParenthesisTest();
    }

    private void isNullOrEmptyTest() {
        assertTrue(isNullOrEmpty(null));
        assertTrue(isNullOrEmpty(""));
    }

    private void equalsTest() {
        // equals
        assertTrue(StringUtil.equals(null, null));
        assertTrue(StringUtil.equals(null, ""));
        assertTrue(StringUtil.equals("", null));
        assertTrue(StringUtil.equals("", ""));
        assertTrue(StringUtil.equals("a","a"));
        // non equals
        assertFalse(StringUtil.equals(null, "a"));
        assertFalse(StringUtil.equals("a", null));
        assertFalse(StringUtil.equals("", "a"));
        assertFalse(StringUtil.equals("a", ""));
        assertFalse(StringUtil.equals("a","b"));
    }

    private void normalizeTest() {
        assertEquals(normalize(null), "");
        assertEquals(normalize(""), "");
        assertEquals(normalize("   "), "");
        assertEquals(normalize("   abc"), "abc");
        assertEquals(normalize("abc   "), "abc");
        assertEquals(normalize("a   b   c"), "a b c");
        assertEquals(normalize("   a   b   c   "), "a b c");
    }

    private void splitTest() {
        String row = "abc\tdef\t   ghi   \t";
        assertEquals(split(row, '\t', false)[0], "abc");
        assertEquals(split(row, '\t', false)[1], "def");
        assertEquals(split(row, '\t', true)[1], "def");
        assertEquals(split(row, '\t', false)[2], "   ghi   ");
        assertEquals(split(row, '\t', true)[2], "ghi");
        assertEquals(split(row, '\t', false)[3], "");
        assertEquals(split(row, '\t', true)[3], "");
    }

    private void cp1252toASCIITest() {
        assertEquals(cp1252toASCII("Michaël Michaud"), "Michael Michaud");
    }

    private void cp1252toASCII38Test() {
        assertEquals(cp1252toASCII38("Michaël Michaud"), "MICHAEL MICHAUD");
    }

    private void inverseStringLengthSortTest() {
        String[] array = new String[]{"abcd", "ab", "œ", "poiuyt", "nhfr"};
        java.util.Arrays.sort(array, INVERSE_STRING_LENGTH);
        assertEquals(array[0], "poiuyt");
        assertEquals(array[array.length-1], "œ");
    }

    private void removeParenthesisTest() {
        assertEquals(removeParenthesis("abcd (efg)"), "abcd ");
        assertEquals(removeParenthesis("abcd(efg)"), "abcd");
        assertEquals(removeParenthesis("abcd (efg) "), "abcd  ");
        assertEquals(removeParenthesis("abcd (efg) (hij)"), "abcd  ");
        assertEquals(removeParenthesis("abcd (efg) (hij) klm"), "abcd   klm");
        assertEquals(removeParenthesis("abcd (efg (hij))"), "abcd ");
    }

}
