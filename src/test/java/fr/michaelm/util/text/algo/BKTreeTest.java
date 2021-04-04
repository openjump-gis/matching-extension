/*
 * (C) 2011 michael.michaud@free.fr
 */

package fr.michaelm.util.text.algo;


import fr.michaelm.util.AbstractTest;

/**
 * Test class for WLevenshteinDistanceTest
 *
 * @author Micha&euml;l Michaud
 * @version 0.1 (2011-05-01)
 */
// History
// 0.1 (2011-05-01)
public class BKTreeTest extends AbstractTest {

    public static void main(String[] args) {
        new BKTreeTest();
    }

    protected void maintest() throws Exception {
        BKTree tree = new BKTree(new WLevenshteinDistance(CostFunctions.frenchCosts));
        assertNotNull(tree);
        addTest(tree);
        findBestWordTest(tree);
        findBestWordTest2(tree);
    }

    private void addTest(BKTree tree) {
        tree.add("Bard-lès-Pesmes");
        tree.add("Barlès-Pesmes");
        tree.add("Barlès-Pèmes");
        tree.add("Bard-lespesmes");
        tree.add("Bard-lespesmes");
        tree.add("Barre-les-Pesmes");
        tree.add("Bard-lès-Pennes");
        tree.add("Bar-les-Pemmes");
        tree.add("Bars-l'epesmes");
        tree.add("Bars-et-Pesmes");
        tree.add("Barré-Pesmes");
        assertEquals("Taille de l'index : ", tree.query("Bard",100).size(), 10);
    }

    private void findBestWordTest(BKTree tree) {
        assertEquals(tree.findBestWordMatch("BARD LES PESMES"), "Bard-lès-Pesmes");
    }

    private void findBestWordTest2(BKTree tree) {
        assertEquals(tree.findBestWordMatch("Bard-lespesmes"), "Bard-lespesmes");
    }

}