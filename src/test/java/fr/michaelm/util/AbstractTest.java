/*
 * (C) 2011 michael.michaud@free.fr
 */

package fr.michaelm.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


/**
 * The parent class to do unit tests
 * @author Micha&euml;l Michaud
 * @version 0.3 (2011-05-01)
 */
abstract public class AbstractTest {


	  protected static final Logger LOG = Logger.getLogger("tests.m3.util");
    
	  private static final SimpleDateFormat TIMESTAMP = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
	  private static final SimpleFormatter LOGFORMAT = new SimpleFormatter() {
	      public String format(LogRecord record) {
	          return record.getMessage() + "\r\n";
	      }
	  };

    //protected GXmlEncoder encoder = null;
    long start, stop;
    
    protected int ttrue = 0;
    protected int tfalse = 0;
    
    // startchrono can be used in subclasses to check performance of a method
    protected long startchrono() {
        start = System.nanoTime();
        return start;
    }
    
    // startchrono can be used in subclasses to check performance of a method
    protected long chrono() {
        long i = System.nanoTime();
        System.out.printf("durée : %8.3f ms", (double)(i-start)/1000000.0);
        return i;
    }

    public static void main(String[] args) {
        System.out.println("eeeeeeeeeeeeeeeeeeeee");
    }
    
    public AbstractTest() {
        this(null, null);
    }
    
    public AbstractTest(String logfile, String xmlfile) {
        
        // Use LOGFORMAT in all handlers and parent handlers
        for (Handler h : LOG.getHandlers()) h.setFormatter(LOGFORMAT);
        for (Handler h : LOG.getParent().getHandlers()) h.setFormatter(LOGFORMAT);
        LOG.setLevel(Level.FINEST);
        if (logfile != null) {
            try {
                FileHandler fh = new FileHandler(logfile);
                fh.setFormatter(LOGFORMAT);
                fh.setLevel(Level.FINEST);
                LOG.addHandler(fh);
            } catch (IOException ioe) {ioe.printStackTrace();}
        }
        
        LOG.info("********************************************************");
        LOG.info("Test " + this.getClass().getName());
        LOG.info(TIMESTAMP.format(new Date()));
        LOG.info("********************************************************");
        
        try {
            maintest();
            LOG.info("");
            LOG.info("********************************************************");
            LOG.info("Test réussi : " + ttrue + "/" + (ttrue+tfalse));
            LOG.info(TIMESTAMP.format(new Date()));
            LOG.info("********************************************************");
            LOG.info("");
            LOG.info("");
        }
        catch(Exception ex) {
            LOG.severe(ex.toString());
            ex.printStackTrace();
        }
        try {
            setUp();
        } catch(Exception e) {
            e.printStackTrace();
            LOG.warning(e.getMessage());
        }
    }


    abstract protected void maintest() throws Exception;
    
    protected void setUp() {}

    protected void print(Object o) {LOG.info(o.toString());}

    protected boolean assertEquals(Object o1, Object o2) {
        if ((o1==null && o2==null) || (o1!=null && o1.equals(o2))) {
            LOG.info("TRUE : " + o1 + " = " + o2);
            ttrue++;
            return true;
        } else {
            LOG.info("FALSE : " + o1 + " <> " + o2);
            tfalse++;
            return false;
        }
    }
    
    protected boolean assertEquals(String test, Object o1, Object o2) {
        if ((o1==null && o2==null) || (o1!=null && o1.equals(o2))) {
            LOG.info("TRUE : " + test + " " + o1 + " = " + o2);
            ttrue++;
            return true;
        } else {
            LOG.info("FALSE : " + test + " " + o1 + " <> " + o2);
            tfalse++;
            return false;
        }
    }
    
    protected boolean assertEquals(String test, double o1, double o2, double tol) {
        if (Math.abs(o1-o2)<=tol) {
            LOG.info("TRUE : " + test + " " + o1 + " = " + o2 + " +/- " + tol);
            ttrue++;
            return true;
        } else {
            LOG.info("FALSE : " + test + " " + Math.abs(o1-o2) + " > " + tol);
            tfalse++;
            return false;
        }
    }
    
    protected boolean assertTrue(boolean val) {
        if (val) {
            LOG.info("TRUE : test evaluates to true");
            ttrue++;
            return true;
        } else {
            LOG.info("FALSE : test evaluates to false instead of true");
            tfalse++;
            return false;
        }
    }
    
    protected boolean assertTrue(String test, boolean val) {
        if (val) {
            LOG.info("TRUE : " + test + " evaluates to true");
            ttrue++;
            return true;
        } else {
            LOG.info("FALSE : " + test + " evaluates to false instead of true");
            tfalse++;
            return false;
        }
    }
    
    protected boolean assertFalse(boolean val) {
        if (!val) {
            LOG.info("TRUE : test evaluates to false");
            ttrue++;
            return true;
        } else {
            LOG.info("FALSE : test evaluates to true");
            tfalse++;
            return false;
        }
    }
    
    protected boolean assertFalse(String test, boolean val) {
        if (!val) {
            LOG.info("TRUE : " + test + " evaluates to false");
            ttrue++;
            return true;
        } else {
            LOG.info("FALSE : " + test + " evaluates to true instead of false");
            tfalse++;
            return false;
        }
    }
    
    protected boolean assertNull(Object obj) {
        if (obj==null) {
            LOG.info("TRUE : evaluates to null");
            ttrue++;
            return true;
        } else {
            LOG.info("FALSE : evaluates to '" + obj + "'");
            tfalse++;
            return false;
        }
    }
    
    protected boolean assertNull(String test, Object obj) {
        if (obj==null) {
            LOG.info("TRUE : " + test + " evaluates to null");
            ttrue++;
            return true;
        } else {
            LOG.info("FALSE : " + test + " evaluates to non null '" + obj + "'");
            tfalse++;
            return false;
        }
    }
    
    protected boolean assertNotNull(Object obj) {
        if (obj!=null) {
            LOG.info("TRUE : evaluates to non null (" + obj + ")");
            ttrue++;
            return true;
        } else {
            LOG.info("FALSE : evaluates to null");
            tfalse++;
            return false;
        }
    }
    
    protected boolean assertNotNull(String test, Object obj) {
        if (obj!=null) {
            LOG.info("TRUE : " + test + " evaluates to not null (" + obj + ")");
            ttrue++;
            return true;
        } else {
            LOG.info("FALSE : " + test + " evaluates to null");
            tfalse++;
            return false;
        }
    }
    
    protected boolean assertEquals(String test, double[] c1, double[] c2, double tol) {
        double dd = 0;
        for (int i = 0 ; i < Math.min(c1.length, c2.length) ; i++) {
            dd += (c2[i]-c1[i])*(c2[i]-c1[i]);
        }
        if (dd<tol*tol) {
            LOG.info("TRUE : " + test + " " + Arrays.toString(c1) +  " = " + Arrays.toString(c2) + " +/- " + tol);
            ttrue++;
            return true;
        } else {
            LOG.info("FALSE : " + test + " " + Arrays.toString(c1) +  " to " + Arrays.toString(c2) + " = " + Math.sqrt(dd));
            tfalse++;
            return false;
        }
    }

    protected void logCoord(double[] coord) {
        if (coord.length == 2) {
            LOG.info("P = {" + coord[0] + ", " + coord[1] + "}");
        }
        else if (coord.length == 3) {
            LOG.info("P = {" + coord[0] + ", " + coord[1] + ", " + coord[2] + "}");
        }
        else {
            LOG.info("Erreur : " + coord + " size = " + coord.length);
        }
    }

    protected String coord2string(double[] coord) {
        if (coord.length == 2) {
            return ("P = {" + coord[0] + ", " + coord[1] + "}");
        }
        else if (coord.length == 3) {
            return("P = {" + coord[0] + ", " + coord[1] + ", " + coord[2] + "}");
        }
        else {
            return("Erreur : " + coord + " size = " + coord.length);
        }
    }

}