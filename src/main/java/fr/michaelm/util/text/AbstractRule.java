/*
 * (C) 2011 michael.michaud@free.fr
 */

package fr.michaelm.util.text;


/**
 * Abstract class for all rules transforming a String into another String.
 *
 * @author Micha&euml;l Michaud
 * @version 0.1 (2011-05-01)
 */
public abstract class AbstractRule implements Rule {

    abstract public String transform(String s, Object context) throws TransformationException;

    public String transform(String s) throws TransformationException {
        return transform(s, null);
    }

}