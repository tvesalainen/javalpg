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
import static org.junit.Assert.*;
import org.vesalainen.regex.Regex.Option;
import org.vesalainen.regex.RegexMatcher;
import org.vesalainen.util.Matcher;
import org.vesalainen.util.Matcher.Status;

/**
 *
 * @author tkv
 */
public class RegexMatcherTest
{
    
    public RegexMatcherTest()
    {
    }

    @Test
    public void test1()
    {
        RegexMatcher<Integer> rm = new RegexMatcher<>();
        rm.addExpression("Porche", 1, Option.CASE_INSENSITIVE);
        rm.addExpression("Audi", 2, Option.CASE_INSENSITIVE);
        rm.addExpression("Volvo", 3, Option.CASE_INSENSITIVE);
        rm.compile();
        assertEquals(Status.Ok, rm.match('a'));
        assertEquals(Status.Ok, rm.match('u'));
        assertEquals(Status.Ok, rm.match('d'));
        assertEquals(Status.Match, rm.match('i'));
        assertEquals(Integer.valueOf(2), rm.getMatched());
    }
    
}
