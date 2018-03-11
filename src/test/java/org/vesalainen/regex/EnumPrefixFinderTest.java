/*
 * Copyright (C) 2018 Timo Vesalainen <timo.vesalainen@iki.fi>
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

import java.time.DayOfWeek;
import static java.time.DayOfWeek.*;
import org.junit.Test;
import org.vesalainen.regex.EnumPrefixFinder;
import org.vesalainen.regex.Regex;
import static org.junit.Assert.*;
import static org.vesalainen.regex.Regex.Option.CASE_INSENSITIVE;

/**
 *
 * @author Timo Vesalainen <timo.vesalainen@iki.fi>
 */
public class EnumPrefixFinderTest
{
    
    public EnumPrefixFinderTest()
    {
    }

    @Test
    public void test1()
    {
        EnumPrefixFinder em = new EnumPrefixFinder(DayOfWeek.class, Regex.Option.CASE_INSENSITIVE);
        assertEquals(WEDNESDAY, em.find("Wed"));
        assertEquals(WEDNESDAY, em.find("Wednesday"));
        assertNull(em.find("Maanantai"));
        assertNull(em.find("LAUANTAI"));
    }
    @Test
    public void test2()
    {
        EnumPrefixFinder em = new EnumPrefixFinder(false, SATURDAY, SUNDAY);
        assertEquals(SUNDAY, em.find("SUN"));
        assertNull(em.find("MONDAY"));
    }    
}
