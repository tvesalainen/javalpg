/*
 * Copyright (C) 2012 Timo Vesalainen
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

import java.io.IOException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tkv
 */
public class RegExTest
{

    public RegExTest()
    {
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }

    @Test
    public void test0b()
    {
        try
        {
            String Char = ".";
            String S = " ";
            String exp = "(([" + Char + "&&[^\\]>]]*)|([" + Char + "&&[^\\]]]\\]>)|(\\]\\][" + Char + "&&[^>]]))*";
            System.out.println(exp);

            Regex r = Regex.compile(exp);

            assertTrue(r.isMatch(""));
            assertTrue(r.isMatch("a]]b"));
            assertTrue(r.isMatch("c]>n"));
            assertFalse(r.isMatch("a]]>"));
        }
        catch (SyntaxErrorException ex)
        {
            fail(ex.getMessage());
        }
        catch (IOException ex)
        {
            fail(ex.getMessage());
        }
    }

    /**
     * Test of match method, of class RegEx.
     */
    @Test
    public void testMatch_String() throws Exception
    {
        System.out.println("match");
        String text = "a123";
        Regex instance = Regex.compile("if|while|[a-z][a-z0-9]*");
        String expResult = "a123";
        String result = instance.match(text);
        assertEquals(expResult, result);
    }

    /**
     * Test of find method, of class RegEx.
     */
    @Test
    public void testFind_String() throws Exception
    {
        System.out.println("find");
        String text = "a123";
        Regex instance = Regex.compile("if|while|[a-z][a-z0-9]*");
        String expResult = "a123";
        String result = instance.find(text);
        assertEquals(expResult, result);
    }

    @Test
    public void test1() throws Exception
    {
        System.out.println("test1");
        String text = "a";
        Regex instance = Regex.compile("[a-zA-Z0-9]|\\[");
        String expResult = "a";
        String result = instance.find(text);
        assertEquals(expResult, result);
    }

}
