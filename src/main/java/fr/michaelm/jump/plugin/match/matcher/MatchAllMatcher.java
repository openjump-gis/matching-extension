/*
 * (C) 2021 Michaël Michaud
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * For more information, contact:
 *
 * m.michael.michaud@orange.fr
 */

package fr.michaelm.jump.plugin.match.matcher;


import org.locationtech.jts.geom.Geometry;

/**
 * Abstract Matcher implementing common methods
 *
 * @author Michaël Michaud
 */
public class MatchAllMatcher extends GeometryMatcher {
    
    public static final MatchAllMatcher MATCH_ALL = 
        new MatchAllMatcher();
    
    public static MatchAllMatcher instance() {
        return MATCH_ALL;
    }

    public MatchAllMatcher() {
    }
    
    /**
     * {@inheritDoc}.
     */
    public double match(Geometry source, Geometry target, Object context) throws Exception {
        return 1.0;
    }

}
