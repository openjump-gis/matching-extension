/*
 * (C) 2011 michael.michaud@free.fr
 */

package fr.michaelm.util;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import static fr.michaelm.util.FileUtil.*;
import static fr.michaelm.util.DSVUtil.*;

public class FileUtilTest extends AbstractTest {
    
    public static void main(String[] args) {
        new FileUtilTest();
    }

    protected void maintest() throws Exception {
        getNameWithoutExtensionTest();
        getExtensionTest();
        getIteratorTest();
    }
    
    private void getNameWithoutExtensionTest() {
        assertEquals("C:/TMP/toto.txt",
            getNameWithoutExtension(new File("C:/TMP/toto.txt")), "toto");
        assertEquals("C:\\TMP\\toto.txt",
            getNameWithoutExtension(new File("C:\\TMP\\toto.txt")), "toto");
        assertEquals("C:\\TMP\\toto.tmp.txt",
            getNameWithoutExtension(new File("C:\\TMP\\toto.tmp.txt")), "toto.tmp");
        assertEquals("./subdirectory/toto",
            getNameWithoutExtension(new File("./subdirectory/toto")), "toto");
        assertEquals("./subdirectory/toto/",
            getNameWithoutExtension(new File("./subdirectory/toto/")), "toto");
    }
    
    private void getExtensionTest() {
        assertEquals("C:/TMP/toto.txt",
            getExtension(new File("C:/TMP/toto.txt")), ".txt");
        assertEquals("C:\\TMP\\toto.txt",
            getExtension(new File("C:\\TMP\\toto.txt")), ".txt");
        assertEquals("C:\\TMP\\toto.tmp.txt",
            getExtension(new File("C:\\TMP\\toto.tmp.txt")), ".txt");
        assertEquals("./subdirectory/toto",
            getExtension(new File("./subdirectory/toto")), "");
        assertEquals("./subdirectory/toto/",
            getExtension(new File("./subdirectory/toto/")), "");
    }
    
    private void getIteratorTest() {
        // Create a file with 32 lines and 12 fields
        File f = null;
        try {
            f = File.createTempFile("m3_util",null);
            createRandomDSVFile(f.getPath(), '\t', 32, 12);
            int count = 0;
            for (Iterator it = iterator(f) ; it.hasNext() ; it.next()) {
                count++;
            }
            assertEquals(count, 32);
        }
        catch(IOException e) {e.printStackTrace();}
        finally {
            f.delete();
        }
    }

}
