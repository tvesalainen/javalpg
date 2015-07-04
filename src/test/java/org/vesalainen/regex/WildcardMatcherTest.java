/*
 * Copyright (C) 2015 tkv
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.vesalainen.regex;

import org.junit.Test;
import org.vesalainen.regex.WildcardMatcher;
import static org.junit.Assert.*;
import org.vesalainen.util.Matcher;
import org.vesalainen.util.Matcher.Status;

/**
 *
 * @author tkv
 */
public class WildcardMatcherTest
{
    
    public WildcardMatcherTest()
    {
    }

    @Test
    public void test1()
    {
        WildcardMatcher<String> wm = new WildcardMatcher<>();
        wm.addExpression("$??RMC", "rmc");
        wm.addExpression("$GPGLL", "gpgll");
        wm.addExpression("$HCHDT", "hdt");
        wm.compile();
        assertEquals(Status.Ok, wm.match('$'));
        assertEquals(Status.Ok, wm.match('G'));
        assertEquals(Status.Ok, wm.match('P'));
        assertEquals(Status.Ok, wm.match('G'));
        assertEquals(Status.Ok, wm.match('L'));
        assertEquals(Status.Match, wm.match('L'));
        assertEquals("gpgll", wm.getMatched());
    }
    @Test
    public void test2()
    {
        WildcardMatcher<String> wm = new WildcardMatcher<>();
        wm.addExpression("$??RMC", "rmc");
        wm.addExpression("$??GLL", "gll");
        wm.addExpression("$HCHDT", "hdt");
        wm.compile();
        assertEquals(Status.Ok, wm.match('$'));
        assertEquals(Status.Ok, wm.match('g'));
        assertEquals(Status.Ok, wm.match('P'));
        assertEquals(Status.Ok, wm.match('G'));
        assertEquals(Status.Ok, wm.match('L'));
        assertEquals(Status.Match, wm.match('L'));
        assertEquals("gll", wm.getMatched());
    }
    
}
