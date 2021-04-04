/*
 * (C) 2011 michael.michaud@free.fr
 */

package fr.michaelm.util;


public class DSVFileSorterTest extends AbstractTest {
    
    public static void main(String[] args) {
        new DSVFileSorterTest();
    }

    protected void maintest() throws Exception {
        DSVUtil.createRandomDSVFile("src/test/resources/tests/m3/util/test.txt", '\t', 1000000, 5);
        DSVFileSorter sorter = new DSVFileSorter("src/test/resources/tests/m3/util/test.txt");
        sorter.setField(1).setDelimiter('\t').setChunkSize(10000).setHeader(false);
        sorter.sort();
    }


}
