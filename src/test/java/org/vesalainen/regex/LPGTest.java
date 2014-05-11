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

import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static junit.framework.Assert.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.vesalainen.grammar.state.AmbiguousExpressionException;
import org.vesalainen.grammar.state.DFA;
import org.vesalainen.grammar.state.DFAState;
import org.vesalainen.grammar.state.NFA;
import org.vesalainen.grammar.state.NFAState;
import org.vesalainen.grammar.state.Scope;
import org.vesalainen.parser.util.InputReader;
import org.vesalainen.parser.util.NumMap;
import org.vesalainen.parser.util.NumSet;
import org.vesalainen.regex.Regex.Option;

/**
 *
 * @author tkv
 */
public class LPGTest
{

    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }
    
    @Test
    public void test1()
    {
        try
        {
            String exp = "a*";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertTrue(r.isMatch(""));
            assertTrue(r.isMatch("a"));
            assertTrue(r.isMatch("aa"));
            assertTrue(r.isMatch("aaa"));
            assertFalse(r.isMatch("b"));
            assertEquals("Bdsds", r.replace("aaadsds", "B"));

            assertEquals(exp, r.getExpression());
            assertEquals(0, r.getMinLength());
            assertEquals(Integer.MAX_VALUE, r.getMaxLength());
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

    @Test
    public void test1a()
    {
        try
        {
            String exp = "a.*";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch(""));
            assertTrue(r.isMatch("a"));
            assertTrue(r.isMatch("aa"));
            assertTrue(r.isMatch("aaa"));
            assertTrue(r.isMatch("abc"));
            assertFalse(r.isMatch("b"));
            assertEquals("B", r.replace("aaadsds", "B"));

            assertEquals(exp, r.getExpression());
            assertEquals(1, r.getMinLength());
            assertEquals(Integer.MAX_VALUE, r.getMaxLength());
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

    @Test
    public void test1b()
    {
        try
        {
            String exp = "LITERAL|LITERALS";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertTrue(r.isMatch("LITERALS"));
            assertTrue(r.isMatch("LITERAL"));
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

    @Test
    public void test1c()
    {
        try
        {
            String exp = "LITERAL|LITERALS";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp, Option.ACCEPT_IMMEDIATELY);
            assertTrue(r.isMatch("LITERAL"));
            assertFalse(r.isMatch("LITERALS"));
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

    @Test
    public void test2()
    {
        try
        {
            String exp = "a+";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch(""));
            assertTrue(r.isMatch("a"));
            assertTrue(r.isMatch("aa"));
            assertTrue(r.isMatch("aaa"));
            assertFalse(r.isMatch("b"));
            assertEquals("aaa", r.find("sdsdaaadsds"));
            assertEquals("sdsdBdsds", r.replace("sdsdaaadsds", "B"));

            assertEquals(exp, r.getExpression());
            assertEquals(1, r.getMinLength());
            assertEquals(Integer.MAX_VALUE, r.getMaxLength());
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

    @Test
    public void test2b()
    {
        try
        {
            String exp = "a{4}";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch(""));
            assertFalse(r.isMatch("a"));
            assertFalse(r.isMatch("aa"));
            assertFalse(r.isMatch("aaa"));
            assertTrue(r.isMatch("aaaa"));
            assertFalse(r.isMatch("aaaaa"));
            assertFalse(r.isMatch("b"));
            assertEquals("aaaa", r.find("sdsdaaaadsds"));

            assertEquals(exp, r.getExpression());
            assertEquals(4, r.getMinLength());
            assertEquals(4, r.getMaxLength());
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

    @Test
    public void test3()
    {
        try
        {
            String exp = "[acf]*";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertTrue(r.isMatch(""));
            assertTrue(r.isMatch("a"));
            assertTrue(r.isMatch("ac"));
            assertTrue(r.isMatch("cf"));
            assertTrue(r.isMatch("ff"));
            assertTrue(r.isMatch("ccccaaff"));
            assertTrue(r.isMatch("afafafafa"));
            assertFalse(r.isMatch("b"));
            assertEquals("Bdsds", r.replace("acfadsds", "B"));

            assertEquals(exp, r.getExpression());
            assertEquals(0, r.getMinLength());
            assertEquals(Integer.MAX_VALUE, r.getMaxLength());
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

    @Test
    public void test4()
    {
        try
        {
            String exp = "[^acf]*";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertTrue(r.isMatch(""));
            assertTrue(r.isMatch("b"));
            assertTrue(r.isMatch("ghjk"));
            assertTrue(r.isMatch("12312"));
            assertTrue(r.isMatch("?+*"));
            assertTrue(r.isMatch("=)((/(&/%&"));
            assertTrue(r.isMatch("    "));
            assertFalse(r.isMatch("acf"));
            assertFalse(r.isMatch("aaa"));
            assertFalse(r.isMatch("fcfcfc"));
            assertFalse(r.isMatch("aaaaffffcccc"));
            assertEquals("BacfaB", r.replace("sdsdacfadsds", "B"));

            assertEquals(exp, r.getExpression());
            assertEquals(0, r.getMinLength());
            assertEquals(Integer.MAX_VALUE, r.getMaxLength());
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

    @Test
    public void test5()
    {
        try
        {
            String exp = "[acf]{1,}";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch(""));
            assertFalse(r.isMatch("b"));
            assertFalse(r.isMatch("ghjk"));
            assertFalse(r.isMatch("12312"));
            assertFalse(r.isMatch("?+*"));
            assertFalse(r.isMatch("=)((/(&/%&"));
            assertFalse(r.isMatch("    "));
            assertTrue(r.isMatch("acf"));
            assertTrue(r.isMatch("aaa"));
            assertTrue(r.isMatch("fcfcfc"));
            assertTrue(r.isMatch("aaaaffffcccc"));
            assertEquals("acfa", r.find("sdsdacfadsds"));

            assertEquals(exp, r.getExpression());
            assertEquals(1, r.getMinLength());
            assertEquals(Integer.MAX_VALUE, r.getMaxLength());
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

    @Test
    public void test6()
    {
        try
        {
            String exp = "[acf]{2,4}";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch("a"));
            assertTrue(r.isMatch("ac"));
            assertTrue(r.isMatch("acf"));
            assertTrue(r.isMatch("acfc"));
            assertFalse(r.isMatch("acfcf"));
            assertEquals("acfa", r.find("sdsdacfadsds"));

            assertEquals(exp, r.getExpression());
            assertEquals(2, r.getMinLength());
            assertEquals(4, r.getMaxLength());
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

    @Test
    public void test7()
    {
        try
        {
            String exp = "[acf]{3}";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch("a"));
            assertFalse(r.isMatch("ac"));
            assertTrue(r.isMatch("acf"));
            assertFalse(r.isMatch("acfc"));
            assertFalse(r.isMatch("acfcf"));
            assertEquals("acf", r.find("sdsdacfadsds"));

            assertEquals(exp, r.getExpression());
            assertEquals(3, r.getMinLength());
            assertEquals(3, r.getMaxLength());
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

    @Test
    public void test8()
    {
        try
        {
            String exp = "[c-fl-n]+";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch("a"));
            assertFalse(r.isMatch("b"));
            assertTrue(r.isMatch("c"));
            assertTrue(r.isMatch("d"));
            assertTrue(r.isMatch("e"));
            assertTrue(r.isMatch("f"));
            assertFalse(r.isMatch("g"));
            assertFalse(r.isMatch("h"));
            assertFalse(r.isMatch("i"));
            assertFalse(r.isMatch("j"));
            assertFalse(r.isMatch("k"));
            assertTrue(r.isMatch("l"));
            assertTrue(r.isMatch("m"));
            assertTrue(r.isMatch("n"));
            assertFalse(r.isMatch("o"));
            assertEquals("d", r.find("sdsdceflmndsds"));
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

    @Test
    public void test9()
    {
        try
        {
            String exp = "[^c-fl-n]+";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertTrue(r.isMatch("a"));
            assertTrue(r.isMatch("b"));
            assertFalse(r.isMatch("c"));
            assertFalse(r.isMatch("d"));
            assertFalse(r.isMatch("e"));
            assertFalse(r.isMatch("f"));
            assertTrue(r.isMatch("g"));
            assertTrue(r.isMatch("h"));
            assertTrue(r.isMatch("i"));
            assertTrue(r.isMatch("j"));
            assertTrue(r.isMatch("k"));
            assertFalse(r.isMatch("l"));
            assertFalse(r.isMatch("m"));
            assertFalse(r.isMatch("n"));
            assertTrue(r.isMatch("o"));
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

    @Test
    public void test10()
    {
        try
        {
            String exp = "if|while|[a-z][a-z0-9]*";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertTrue(r.isMatch("if"));
            assertTrue(r.isMatch("while"));
            assertFalse(r.isMatch("A123"));
            assertFalse(r.isMatch("999"));
            assertFalse(r.isMatch(""));
            assertTrue(r.isMatch("ifa"));
            assertTrue(r.isMatch("while3"));
            assertTrue(r.isMatch("x2"));

            assertEquals(exp, r.getExpression());
            assertEquals(1, r.getMinLength());
            assertEquals(Integer.MAX_VALUE, r.getMaxLength());
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

    @Test
    public void test11()
    {
        try
        {
            String exp = "a?";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertTrue(r.isMatch(""));
            assertTrue(r.isMatch("a"));
            assertFalse(r.isMatch("aa"));
            assertFalse(r.isMatch("aaa"));
            assertFalse(r.isMatch("b"));

            assertEquals(exp, r.getExpression());
            assertEquals(0, r.getMinLength());
            assertEquals(1, r.getMaxLength());
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

    @Test
    public void test11b()
    {
        try
        {
            String exp = "( (a)*)?";
            System.out.println(exp);

            Regex r = Regex.compile(exp);
            assertTrue(r.isMatch(""));
            assertTrue(r.isMatch(" "));
            assertTrue(r.isMatch(" a"));
            assertFalse(r.isMatch("aa"));
            assertFalse(r.isMatch("aaa"));
            assertFalse(r.isMatch("b"));

            assertEquals(exp, r.getExpression());
            assertEquals(0, r.getMinLength());
            assertEquals(Integer.MAX_VALUE, r.getMaxLength());
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

    @Test
    public void test12()
    {
        try
        {
            String exp = "(a)|(b)";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch(""));
            assertTrue(r.isMatch("a"));
            assertTrue(r.isMatch("b"));
            assertFalse(r.isMatch("aa"));
            assertFalse(r.isMatch("aaa"));

            assertEquals(exp, r.getExpression());
            assertEquals(1, r.getMinLength());
            assertEquals(1, r.getMaxLength());
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

    @Test
    public void test13()
    {
        try
        {
            String exp = "\\t\\r\\n\\\\\\f\\a\\e";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch(""));
            assertTrue(r.isMatch("\t\r\n\\\f\u0007\u001b"));
            assertFalse(r.isMatch("\\t"));

            assertEquals(exp, r.getExpression());
            assertEquals(7, r.getMinLength());
            assertEquals(7, r.getMaxLength());
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

    @Test
    public void test14()
    {
        try
        {
            String exp = "\\040";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch(""));
            assertTrue(r.isMatch(" "));
            assertFalse(r.isMatch("\\t"));
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

    @Test
    public void test15()
    {
        try
        {
            String exp = "\\x20";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch(""));
            assertTrue(r.isMatch(" "));
            assertFalse(r.isMatch("\\t"));
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

    @Test
    public void test16()
    {
        try
        {
            String exp = "\\u0020";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch(""));
            assertTrue(r.isMatch(" "));
            assertFalse(r.isMatch("\\t"));
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

    @Test
    public void test16b()
    {
        try
        {
            String exp = "\\x{20}";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch(""));
            assertTrue(r.isMatch(" "));
            assertFalse(r.isMatch("\\t"));
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

    @Test
    public void test17()
    {
        try
        {
            String exp = "\\[\\]\\(\\)\\\\\\-\\^\\*\\+\\?\\|\\.\\{\\}\\,";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch(""));
            assertTrue(r.isMatch("[]()\\-^*+?|.{},"));
            assertFalse(r.isMatch("\\t"));
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

    @Test
    public void test18()
    {
        try
        {
            String exp = "[a-d[m-p]]";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch(""));
            assertTrue(r.isMatch("a"));
            assertTrue(r.isMatch("d"));
            assertTrue(r.isMatch("m"));
            assertTrue(r.isMatch("p"));
            assertTrue(r.isMatch("b"));
            assertTrue(r.isMatch("n"));
            assertTrue(r.isMatch("o"));
            assertFalse(r.isMatch("e"));
            assertFalse(r.isMatch("l"));
            assertFalse(r.isMatch(" "));
            assertFalse(r.isMatch(""));
            assertFalse(r.isMatch("q"));
            assertFalse(r.isMatch("abcdefg"));
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

    @Test
    public void test19()
    {
        try
        {
            String exp = "[a-z&&[def]]";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch(""));
            assertFalse(r.isMatch("a"));
            assertFalse(r.isMatch("b"));
            assertFalse(r.isMatch("c"));
            assertTrue(r.isMatch("d"));
            assertTrue(r.isMatch("e"));
            assertTrue(r.isMatch("f"));
            assertFalse(r.isMatch("g"));
            assertFalse(r.isMatch("h"));
            assertFalse(r.isMatch(" "));
            assertFalse(r.isMatch(""));
            assertFalse(r.isMatch("q"));
            assertFalse(r.isMatch("abcdefg"));
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

    @Test
    public void test20()
    {
        try
        {
            String exp = "[a-z&&[^bc]]";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch(""));
            assertTrue(r.isMatch("a"));
            assertFalse(r.isMatch("b"));
            assertFalse(r.isMatch("c"));
            assertTrue(r.isMatch("d"));
            assertTrue(r.isMatch("e"));
            assertTrue(r.isMatch("z"));
            assertFalse(r.isMatch(" "));
            assertFalse(r.isMatch(""));
            assertFalse(r.isMatch("abcdefg"));
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

    @Test
    public void test21()
    {
        try
        {
            String exp = ".";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch(""));
            assertTrue(r.isMatch("a"));
            assertTrue(r.isMatch("b"));
            assertTrue(r.isMatch("c"));
            assertTrue(r.isMatch("d"));
            assertTrue(r.isMatch("e"));
            assertTrue(r.isMatch("z"));
            assertTrue(r.isMatch(" "));
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

    @Test
    public void test22()
    {
        try
        {
            String exp = "\\d";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch(""));
            assertTrue(r.isMatch("0"));
            assertTrue(r.isMatch("1"));
            assertTrue(r.isMatch("2"));
            assertTrue(r.isMatch("3"));
            assertTrue(r.isMatch("4"));
            assertTrue(r.isMatch("5"));
            assertTrue(r.isMatch("6"));
            assertTrue(r.isMatch("7"));
            assertTrue(r.isMatch("8"));
            assertTrue(r.isMatch("9"));
            assertFalse(r.isMatch("a"));
            assertFalse(r.isMatch("/"));
            assertFalse(r.isMatch(":"));
            assertFalse(r.isMatch(" "));
            assertFalse(r.isMatch("O"));
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

    @Test
    public void test23()
    {
        try
        {
            String exp = "\\D";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch(""));
            assertTrue(r.isMatch("a"));
            assertTrue(r.isMatch("b"));
            assertTrue(r.isMatch("/"));
            assertTrue(r.isMatch(":"));
            assertTrue(r.isMatch("O"));
            assertTrue(r.isMatch("ä"));
            assertTrue(r.isMatch("å"));
            assertTrue(r.isMatch("Ö"));
            assertTrue(r.isMatch("\n"));
            assertTrue(r.isMatch("\t"));
            assertFalse(r.isMatch("0"));
            assertFalse(r.isMatch("1"));
            assertFalse(r.isMatch("2"));
            assertFalse(r.isMatch("8"));
            assertFalse(r.isMatch("9"));
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

    @Test
    public void test24()
    {
        try
        {
            String exp = "\\s";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch(""));
            assertTrue(r.isMatch(" "));
            assertTrue(r.isMatch("\t"));
            assertTrue(r.isMatch("\n"));
            assertTrue(r.isMatch("\u000B"));
            assertTrue(r.isMatch("\f"));
            assertTrue(r.isMatch("\r"));
            assertFalse(r.isMatch("a"));
            assertFalse(r.isMatch("t"));
            assertFalse(r.isMatch("n"));
            assertFalse(r.isMatch("f"));
            assertFalse(r.isMatch("r"));
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

    @Test
    public void test25()
    {
        try
        {
            String exp = "\\S";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch(" "));
            assertFalse(r.isMatch("\t"));
            assertFalse(r.isMatch("\n"));
            assertFalse(r.isMatch("\u000B"));
            assertFalse(r.isMatch("\f"));
            assertFalse(r.isMatch("\r"));

            assertTrue(r.isMatch("a"));
            assertTrue(r.isMatch("t"));
            assertTrue(r.isMatch("n"));
            assertTrue(r.isMatch("f"));
            assertTrue(r.isMatch("r"));
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

    @Test
    public void test26()
    {
        try
        {
            String exp = "\\w";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch(" "));
            assertFalse(r.isMatch("\t"));
            assertFalse(r.isMatch("\n"));
            assertFalse(r.isMatch("\u000B"));
            assertFalse(r.isMatch("\f"));
            assertFalse(r.isMatch("\r"));

            assertTrue(r.isMatch("a"));
            assertTrue(r.isMatch("z"));
            assertTrue(r.isMatch("A"));
            assertTrue(r.isMatch("Z"));
            assertTrue(r.isMatch("_"));
            assertTrue(r.isMatch("0"));
            assertTrue(r.isMatch("9"));
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

    @Test
    public void test27()
    {
        try
        {
            String exp = "\\W";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertTrue(r.isMatch(" "));
            assertTrue(r.isMatch("\t"));
            assertTrue(r.isMatch("\n"));
            assertTrue(r.isMatch("\u000B"));
            assertTrue(r.isMatch("\f"));
            assertTrue(r.isMatch("\r"));

            assertFalse(r.isMatch("a"));
            assertFalse(r.isMatch("z"));
            assertFalse(r.isMatch("A"));
            assertFalse(r.isMatch("Z"));
            assertFalse(r.isMatch("_"));
            assertFalse(r.isMatch("0"));
            assertFalse(r.isMatch("9"));
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

    @Test
    public void test28()
    {
        try
        {
            String exp = "[\\cA-\\cZ]";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertTrue(r.isMatch("\u0001"));
            assertTrue(r.isMatch("\u0002"));
            assertTrue(r.isMatch("\u0003"));
            assertTrue(r.isMatch("\u001a"));

            assertFalse(r.isMatch("\u0000"));
            assertFalse(r.isMatch("\u001b"));
            assertFalse(r.isMatch("a"));
            assertFalse(r.isMatch("z"));
            assertFalse(r.isMatch("A"));
            assertFalse(r.isMatch("Z"));
            assertFalse(r.isMatch("_"));
            assertFalse(r.isMatch("0"));
            assertFalse(r.isMatch("9"));
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

    @Test
    public void test29()
    {
        try
        {
            String exp = "\\c[\\c\\\\c]\\c^\\c_";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertTrue(r.isMatch("\u001b\u001c\u001d\u001e\u001f"));

            assertFalse(r.isMatch("abc"));
            assertFalse(r.isMatch("\u001a"));
            assertFalse(r.isMatch("\u0001"));
            assertFalse(r.isMatch(" "));
            assertFalse(r.isMatch("Z"));
            assertFalse(r.isMatch("_"));
            assertFalse(r.isMatch("0"));
            assertFalse(r.isMatch("9"));
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

    @Test
    public void test30()
    {
        try
        {
            String exp = "\\p{Lower}";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertTrue(r.isMatch("a"));
            assertTrue(r.isMatch("b"));
            assertTrue(r.isMatch("c"));
            assertTrue(r.isMatch("z"));

            assertFalse(r.isMatch("A"));
            assertFalse(r.isMatch("B"));
            assertFalse(r.isMatch("C"));
            assertFalse(r.isMatch("Z"));
            assertFalse(r.isMatch("0"));

            assertFalse(r.isMatch(""));
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

    @Test
    public void test31()
    {
        try
        {
            String exp = "\\p{Upper}";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch("a"));
            assertFalse(r.isMatch("b"));
            assertFalse(r.isMatch("c"));
            assertFalse(r.isMatch("z"));
            assertFalse(r.isMatch("0"));

            assertTrue(r.isMatch("A"));
            assertTrue(r.isMatch("B"));
            assertTrue(r.isMatch("C"));
            assertTrue(r.isMatch("Z"));

            assertFalse(r.isMatch(""));
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

    @Test
    public void test32()
    {
        try
        {
            String exp = "\\p{ASCII}";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertTrue(r.isMatch("\u0000"));
            assertTrue(r.isMatch("\u0001"));
            assertTrue(r.isMatch("\u007f"));
            assertTrue(r.isMatch("a"));
            assertTrue(r.isMatch("b"));
            assertTrue(r.isMatch("c"));
            assertTrue(r.isMatch("z"));
            assertTrue(r.isMatch("0"));

            assertTrue(r.isMatch("A"));
            assertTrue(r.isMatch("B"));
            assertTrue(r.isMatch("C"));
            assertTrue(r.isMatch("Z"));

            assertFalse(r.isMatch(""));
            assertFalse(r.isMatch("ä"));
            assertFalse(r.isMatch("ö"));
            assertFalse(r.isMatch("Å"));
            assertFalse(r.isMatch("å"));
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

    @Test
    public void test33()
    {
        try
        {
            String exp = "\\p{Alpha}";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch("\u0000"));
            assertFalse(r.isMatch("\u0001"));
            assertFalse(r.isMatch("\u007f"));
            assertTrue(r.isMatch("a"));
            assertTrue(r.isMatch("b"));
            assertTrue(r.isMatch("c"));
            assertTrue(r.isMatch("z"));

            assertTrue(r.isMatch("A"));
            assertTrue(r.isMatch("B"));
            assertTrue(r.isMatch("C"));
            assertTrue(r.isMatch("Z"));

            assertFalse(r.isMatch(""));
            assertFalse(r.isMatch("0"));
            assertFalse(r.isMatch("1"));
            assertFalse(r.isMatch("2"));
            assertFalse(r.isMatch("9"));
            assertFalse(r.isMatch("ä"));
            assertFalse(r.isMatch("ö"));
            assertFalse(r.isMatch("Å"));
            assertFalse(r.isMatch("å"));
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

    @Test
    public void test34()
    {
        try
        {
            String exp = "\\p{Digit}";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch("\u0000"));
            assertFalse(r.isMatch("\u0001"));
            assertFalse(r.isMatch("\u007f"));
            assertTrue(r.isMatch("0"));
            assertTrue(r.isMatch("1"));
            assertTrue(r.isMatch("8"));
            assertTrue(r.isMatch("9"));

            assertTrue(r.isMatch("4"));
            assertTrue(r.isMatch("5"));
            assertTrue(r.isMatch("6"));
            assertTrue(r.isMatch("7"));

            assertFalse(r.isMatch(""));
            assertFalse(r.isMatch("a"));
            assertFalse(r.isMatch("|"));
            assertFalse(r.isMatch("q"));
            assertFalse(r.isMatch("f"));
            assertFalse(r.isMatch("ä"));
            assertFalse(r.isMatch("ö"));
            assertFalse(r.isMatch("Å"));
            assertFalse(r.isMatch("å"));
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

    @Test
    public void test35()
    {
        try
        {
            String exp = "\\p{Alnum}";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch("\u0000"));
            assertFalse(r.isMatch("\u0001"));
            assertFalse(r.isMatch("\u007f"));
            assertTrue(r.isMatch("0"));
            assertTrue(r.isMatch("1"));
            assertTrue(r.isMatch("8"));
            assertTrue(r.isMatch("9"));

            assertTrue(r.isMatch("a"));
            assertTrue(r.isMatch("z"));
            assertTrue(r.isMatch("A"));
            assertTrue(r.isMatch("Z"));

            assertFalse(r.isMatch(""));
            assertFalse(r.isMatch("$"));
            assertFalse(r.isMatch("|"));
            assertFalse(r.isMatch("&"));
            assertFalse(r.isMatch("["));
            assertFalse(r.isMatch("]"));
            assertFalse(r.isMatch("}"));
            assertFalse(r.isMatch("Å"));
            assertFalse(r.isMatch("å"));
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

    @Test
    public void test36()
    {
        try
        {
            String exp = "\\p{Punct}";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch("\u0000"));
            assertFalse(r.isMatch("\u0001"));
            assertFalse(r.isMatch("\u007f"));

            assertTrue(r.isMatch("!"));
            assertTrue(r.isMatch("\""));
            assertTrue(r.isMatch("#"));
            assertTrue(r.isMatch("$"));

            assertTrue(r.isMatch("~"));
            assertTrue(r.isMatch("}"));
            assertTrue(r.isMatch("|"));
            assertTrue(r.isMatch(")"));

            assertFalse(r.isMatch(""));
            assertFalse(r.isMatch(" "));
            assertFalse(r.isMatch("a"));
            assertFalse(r.isMatch("c"));
            assertFalse(r.isMatch("1"));
            assertFalse(r.isMatch("0"));
            assertFalse(r.isMatch("9"));
            assertFalse(r.isMatch("Å"));
            assertFalse(r.isMatch("å"));
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

    @Test
    public void test37()
    {
        try
        {
            String exp = "\\p{Graph}";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch("\u0000"));
            assertFalse(r.isMatch("\u0001"));
            assertFalse(r.isMatch("\u007f"));

            assertTrue(r.isMatch("!"));
            assertTrue(r.isMatch("\""));
            assertTrue(r.isMatch("#"));
            assertTrue(r.isMatch("$"));

            assertTrue(r.isMatch("~"));
            assertTrue(r.isMatch("}"));
            assertTrue(r.isMatch("|"));
            assertTrue(r.isMatch(")"));
            assertTrue(r.isMatch("a"));
            assertTrue(r.isMatch("0"));
            assertTrue(r.isMatch("A"));
            assertTrue(r.isMatch("Z"));

            assertFalse(r.isMatch(""));
            assertFalse(r.isMatch(" "));
            assertFalse(r.isMatch("Å"));
            assertFalse(r.isMatch("å"));
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

    @Test
    public void test38()
    {
        try
        {
            String exp = "\\p{Print}";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch("\u0000"));
            assertFalse(r.isMatch("\u0001"));
            assertFalse(r.isMatch("\u007f"));

            assertTrue(r.isMatch("!"));
            assertTrue(r.isMatch("\""));
            assertTrue(r.isMatch("#"));
            assertTrue(r.isMatch("$"));

            assertTrue(r.isMatch("~"));
            assertTrue(r.isMatch("}"));
            assertTrue(r.isMatch("|"));
            assertTrue(r.isMatch(")"));
            assertTrue(r.isMatch("a"));
            assertTrue(r.isMatch("0"));
            assertTrue(r.isMatch("A"));
            assertTrue(r.isMatch("Z"));
            assertTrue(r.isMatch(" "));

            assertFalse(r.isMatch(""));
            assertFalse(r.isMatch("Å"));
            assertFalse(r.isMatch("å"));
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

    @Test
    public void test39()
    {
        try
        {
            String exp = "\\p{Blank}";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch("\u0000"));
            assertFalse(r.isMatch("\u0001"));
            assertFalse(r.isMatch("\u007f"));

            assertTrue(r.isMatch(" "));
            assertTrue(r.isMatch("\t"));

            assertFalse(r.isMatch("#"));
            assertFalse(r.isMatch("$"));

            assertFalse(r.isMatch("~"));
            assertFalse(r.isMatch("}"));
            assertFalse(r.isMatch("|"));
            assertFalse(r.isMatch(")"));
            assertFalse(r.isMatch("a"));
            assertFalse(r.isMatch("0"));
            assertFalse(r.isMatch("A"));
            assertFalse(r.isMatch("Z"));

            assertFalse(r.isMatch(""));
            assertFalse(r.isMatch("Å"));
            assertFalse(r.isMatch("å"));
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

    @Test
    public void test40()
    {
        try
        {
            String exp = "\\p{Cntrl}";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertTrue(r.isMatch("\u0000"));
            assertTrue(r.isMatch("\u0001"));
            assertTrue(r.isMatch("\u007f"));

            assertFalse(r.isMatch("#"));
            assertFalse(r.isMatch("$"));

            assertFalse(r.isMatch("~"));
            assertFalse(r.isMatch("}"));
            assertFalse(r.isMatch("|"));
            assertFalse(r.isMatch(")"));
            assertFalse(r.isMatch("a"));
            assertFalse(r.isMatch("0"));
            assertFalse(r.isMatch("A"));
            assertFalse(r.isMatch("Z"));

            assertFalse(r.isMatch(""));
            assertFalse(r.isMatch("Å"));
            assertFalse(r.isMatch("å"));
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

    @Test
    public void test41()
    {
        try
        {
            String exp = "\\p{XDigit}";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch("\u0000"));
            assertFalse(r.isMatch("\u0001"));
            assertFalse(r.isMatch("\u007f"));

            assertTrue(r.isMatch("0"));
            assertTrue(r.isMatch("1"));

            assertTrue(r.isMatch("8"));
            assertTrue(r.isMatch("9"));
            assertTrue(r.isMatch("a"));
            assertTrue(r.isMatch("A"));
            assertTrue(r.isMatch("c"));
            assertTrue(r.isMatch("d"));
            assertTrue(r.isMatch("F"));
            assertTrue(r.isMatch("f"));

            assertFalse(r.isMatch(""));
            assertFalse(r.isMatch("/"));
            assertFalse(r.isMatch(":"));
            assertFalse(r.isMatch("`"));
            assertFalse(r.isMatch("g"));
            assertFalse(r.isMatch("@"));
            assertFalse(r.isMatch("G"));
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

    @Test
    public void test42()
    {
        try
        {
            String exp = "\\p{Space}";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch("\u0000"));
            assertFalse(r.isMatch("\u0001"));
            assertFalse(r.isMatch("\u007f"));

            assertTrue(r.isMatch(" "));
            assertTrue(r.isMatch("\t"));

            assertTrue(r.isMatch("\n"));
            assertTrue(r.isMatch("\u000b"));
            assertTrue(r.isMatch("\f"));
            assertTrue(r.isMatch("\r"));

            assertFalse(r.isMatch("c"));
            assertFalse(r.isMatch("d"));
            assertFalse(r.isMatch("F"));
            assertFalse(r.isMatch("f"));

            assertFalse(r.isMatch(""));
            assertFalse(r.isMatch("/"));
            assertFalse(r.isMatch(":"));
            assertFalse(r.isMatch("`"));
            assertFalse(r.isMatch("g"));
            assertFalse(r.isMatch("@"));
            assertFalse(r.isMatch("G"));
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

    @Test
    public void test43()
    {
        try
        {
            String exp = "\\p{javaLowerCase}";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch("\u0000"));
            assertFalse(r.isMatch("\u0001"));
            assertFalse(r.isMatch("\u007f"));


            assertTrue(r.isMatch("c"));
            assertTrue(r.isMatch("d"));
            assertTrue(r.isMatch("ä"));
            assertTrue(r.isMatch("å"));

            assertFalse(r.isMatch(""));
            assertFalse(r.isMatch("/"));
            assertFalse(r.isMatch("A"));
            assertFalse(r.isMatch("Z"));
            assertFalse(r.isMatch("Ä"));
            assertFalse(r.isMatch("Ö"));
            assertFalse(r.isMatch(" "));
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

    @Test
    public void test44()
    {
        try
        {
            String exp = "\\p{javaLowerCase}";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch("\u0000"));
            assertFalse(r.isMatch("\u0001"));
            assertFalse(r.isMatch("\u007f"));


            assertTrue(r.isMatch("c"));
            assertTrue(r.isMatch("d"));
            assertTrue(r.isMatch("ä"));
            assertTrue(r.isMatch("å"));

            assertFalse(r.isMatch(""));
            assertFalse(r.isMatch("/"));
            assertFalse(r.isMatch("A"));
            assertFalse(r.isMatch("Z"));
            assertFalse(r.isMatch("Ä"));
            assertFalse(r.isMatch("Ö"));
            assertFalse(r.isMatch(" "));
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

    @Test
    public void test45()
    {
        try
        {
            String exp = "\\p{javaUpperCase}";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch("\u0000"));
            assertFalse(r.isMatch("\u0001"));
            assertFalse(r.isMatch("\u007f"));


            assertTrue(r.isMatch("C"));
            assertTrue(r.isMatch("D"));
            assertTrue(r.isMatch("Ä"));
            assertTrue(r.isMatch("Å"));

            assertFalse(r.isMatch(""));
            assertFalse(r.isMatch("/"));
            assertFalse(r.isMatch("a"));
            assertFalse(r.isMatch("z"));
            assertFalse(r.isMatch("ä"));
            assertFalse(r.isMatch("ö"));
            assertFalse(r.isMatch(" "));
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

    @Test
    public void test46()
    {
        try
        {
            String exp = "\\p{javaWhitespace}";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch("\u0000"));
            assertFalse(r.isMatch("\u0001"));
            assertFalse(r.isMatch("\u007f"));


            assertTrue(r.isMatch("\u0009"));
            assertTrue(r.isMatch("\u000b"));
            assertTrue(r.isMatch("\u000c"));
            assertTrue(r.isMatch("\u000c"));
            assertTrue(r.isMatch("\u001d"));
            assertTrue(r.isMatch("\u001e"));
            assertTrue(r.isMatch("\u001f"));
            assertTrue(r.isMatch(" "));

            assertFalse(r.isMatch(""));
            assertFalse(r.isMatch("/"));
            assertFalse(r.isMatch("a"));
            assertFalse(r.isMatch("z"));
            assertFalse(r.isMatch("ä"));
            assertFalse(r.isMatch("ö"));
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

    @Test
    public void test47()
    {
        try
        {
            String exp = "\\p{javaMirrored}";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch("\u0000"));
            assertFalse(r.isMatch("\u0001"));
            assertFalse(r.isMatch("\u007f"));


            assertTrue(r.isMatch("("));
            assertTrue(r.isMatch(")"));
            assertTrue(r.isMatch("["));
            assertTrue(r.isMatch("]"));
            assertTrue(r.isMatch("{"));
            assertTrue(r.isMatch("}"));

            assertFalse(r.isMatch(""));
            assertFalse(r.isMatch("/"));
            assertFalse(r.isMatch("a"));
            assertFalse(r.isMatch("z"));
            assertFalse(r.isMatch("ä"));
            assertFalse(r.isMatch("ö"));
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

    @Test
    public void test48()
    {
        try
        {
            String exp = "\\p{InLatin-1 Supplement}";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch("\u0000"));
            assertFalse(r.isMatch("\u0001"));
            assertFalse(r.isMatch("\u007f"));


            assertTrue(r.isMatch("Ä"));
            assertTrue(r.isMatch("Ö"));
            assertTrue(r.isMatch("Å"));
            assertTrue(r.isMatch("ä"));
            assertTrue(r.isMatch("ö"));
            assertTrue(r.isMatch("å"));

            assertFalse(r.isMatch(""));
            assertFalse(r.isMatch("/"));
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

    @Test
    public void test49()
    {
        try
        {
            String exp = "\\p{IsSc}";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch("\u0000"));
            assertFalse(r.isMatch("\u0001"));
            assertFalse(r.isMatch("\u007f"));


            assertTrue(r.isMatch("€"));
            assertTrue(r.isMatch("$"));
            assertTrue(r.isMatch("£"));

            assertFalse(r.isMatch(""));
            assertFalse(r.isMatch("/"));
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

    @Test
    public void test50()
    {
        try
        {
            String exp = "\\p{Nd}";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);
            assertFalse(r.isMatch("\u0000"));
            assertFalse(r.isMatch("\u0001"));
            assertFalse(r.isMatch("\u007f"));


            assertTrue(r.isMatch("0"));
            assertTrue(r.isMatch("1"));
            assertTrue(r.isMatch("9"));

            assertFalse(r.isMatch(""));
            assertFalse(r.isMatch("/"));
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

    @Test
    public void test51()
    {
        try
        {
            String exp = "^abc$\n";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);

            assertFalse(r.isMatch("abcd"));
            assertFalse(r.isMatch("aaa"));
            assertFalse(r.isMatch("123"));


            assertFalse(r.isMatch("abc"));
            assertTrue(r.isMatch("abc\n"));
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

    @Test
    public void test52()
    {
        try
        {
            String exp = "^abc\\Z";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);

            assertFalse(r.isMatch("abcd"));
            assertFalse(r.isMatch("aaa"));
            assertFalse(r.isMatch("123"));


            assertTrue(r.isMatch("abc"));
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

    @Test
    public void test53()
    {
        try
        {
            String exp = " \\babc\\B ";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);

            assertFalse(r.isMatch("abcd"));
            assertFalse(r.isMatch("aaa"));
            assertFalse(r.isMatch("123"));


            assertTrue(r.isMatch(" abc "));
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

    @Test
    public void test54()
    {
        try
        {
            String exp = "abc\\z";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);

            assertFalse(r.isMatch("abcd"));
            assertFalse(r.isMatch("abcd"));
            assertFalse(r.isMatch("abc "));


            assertTrue(r.isMatch("abc"));
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

    @Test
    public void test55()
    {
        try
        {
            String exp = "^abc\\Z";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);

            assertFalse(r.isMatch("abcd"));
            assertFalse(r.isMatch("aaa"));
            assertFalse(r.isMatch("123"));


            assertTrue(r.isMatch("abc"));
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

    @Test
    public void test56()
    {
        try
        {
            String exp = "^abc\\Z\n";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);

            assertFalse(r.isMatch("abcd"));
            assertFalse(r.isMatch("aaa"));
            assertFalse(r.isMatch("123"));


            assertTrue(r.isMatch("abc\n"));

            assertEquals(exp, r.getExpression());
            assertEquals(4, r.getMinLength());
            assertEquals(4, r.getMaxLength());
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

    @Test
    public void test62()
    {
        try
        {
            String exp = "abc+";
            System.out.println(exp);
            
            Regex r = Regex.compile(exp);

            PushbackReader reader = new PushbackReader(new StringReader("abccabca"));
            assertEquals("abcc", r.find(reader, 5));
            assertEquals("abc", r.find(reader, 5));
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
    @Test
    public void test64()
    {
        try
        {
            String exp = "abc+";
            System.out.println(exp);

            Regex r = Regex.compile(exp);

            PushbackReader reader = new PushbackReader(new StringReader("qwertyabcccabccchhhabchhhabcc"));
            CharArrayWriter caw = new CharArrayWriter();
            r.replace(reader, 13, caw, "X");
            assertEquals("qwertyXXhhhXhhhX", caw.toString());
        }
        catch (IOException ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void test65()
    {
        try
        {
            String exp = "[ \t]+";
            System.out.println(exp);

            Regex r = Regex.compile(exp);

            PushbackReader reader = new PushbackReader(new StringReader("asdasd sadasd \t adasdas asdda"));
            String[] expected = new String[] {"asdasd", "sadasd", "adasdas", "asdda"};
            assertTrue(Arrays.equals(expected, r.split(reader, 10)));
        }
        catch (IOException ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void test66()
    {
        try
        {
            String exp = ":";
            System.out.println(exp);

            Regex r = Regex.compile(exp);

            assertTrue(Arrays.equals(new String[] { "boo", "and:foo" }, r.split("boo:and:foo", 2)));
            assertTrue(Arrays.equals(new String[] { "boo", "and", "foo" }, r.split("boo:and:foo", 5)));
            assertTrue(Arrays.equals(new String[] { "boo", "and", "foo" }, r.split("boo:and:foo", -2)));
        }
        catch (IOException ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void test67()
    {
        try
        {
            String exp = "o";
            System.out.println(exp);

            Regex r = Regex.compile(exp);

            assertTrue(Arrays.equals(new String[] { "b", "", ":and:f", "", "" }, r.split("boo:and:foo", 5)));
            assertTrue(Arrays.equals(new String[] { "b", "", ":and:f", "", "" }, r.split("boo:and:foo", -2)));
            assertTrue(Arrays.equals(new String[] { "b", "", ":and:f" }, r.split("boo:and:foo", 0)));
        }
        catch (IOException ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void test68()
    {
        try
        {
            String txt = "[{(*+?.,|)}]";
            String exp = Regex.escape(txt);
            System.out.println(exp);

            Regex r = Regex.compile(exp);

            assertTrue(r.isMatch(txt));
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
    @Test
    public void test69()
    {
        try
        {
            String exp = "aaabc";
            System.out.println(exp);

            Regex r = Regex.compile(exp);

            try
            {
                r.find("aabc");
                fail("should have failed");
            }
            catch (SyntaxErrorException ex)
            {
            }
            assertEquals(r.find("aaabc"), "aaabc");
            assertEquals(r.find("aaaabc"), "aaabc");
            assertEquals(r.find("aaaaabc"), "aaabc");
            assertEquals(r.find("aaaaaabc"), "aaabc");
            assertEquals(r.find("aaaaaabc"), "aaabc");
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
    @Test
    public void test70()
    {
        try
        {
            String exp = "aaabc|qqqqwe";
            System.out.println(exp);

            Regex r = Regex.compile(exp);

            try
            {
                r.find("aabc");
                fail("should have failed");
            }
            catch (SyntaxErrorException ex)
            {
            }
            assertEquals(r.find("aaabc"), "aaabc");
            assertEquals(r.find("aaaabc"), "aaabc");
            assertEquals(r.find("aaaaabc"), "aaabc");
            assertEquals(r.find("aaaaaabc"), "aaabc");
            assertEquals(r.find("aaaaaabc"), "aaabc");

            assertEquals(r.find("qqqqwe"), "qqqqwe");
            assertEquals(r.find("qqqqqwe"), "qqqqwe");
            assertEquals(r.find("qqqqqqwe"), "qqqqwe");
            assertEquals(r.find("qqqqqqqwe"), "qqqqwe");
            assertEquals(r.find("qqqqqqqqwe"), "qqqqwe");
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
    @Test
    public void test71()
    {
        try
        {
            String exp = "(abc)+";
            System.out.println(exp);

            Regex r = Regex.compile(exp);

            assertEquals(r.find("abc"), "abc");
            assertEquals(r.find("aaaabc"), "abc");
            assertEquals(r.find("aaaaabcabcaaa"), "abcabc");
            assertEquals(r.find("aaaaaabcabcabcqqq"), "abcabcabc");


            assertEquals(exp, r.getExpression());
            assertEquals(3, r.getMinLength());
            assertEquals(Integer.MAX_VALUE, r.getMaxLength());
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
    @Test
    public void test72()
    {
        try
        {
            String exp = "(abc)+";
            System.out.println(exp);

            Regex r = Regex.compile(exp, Regex.Option.CASE_INSENSITIVE);

            assertEquals("ABC", r.find("ABC"));
            assertEquals("aBc", r.find("AaAaBc"));
            assertEquals("aBcAbc", r.find("aaaaaBcAbcaaa"));
            assertEquals("abcabcabc", r.find("aaaaaabcabcabcqqq"));


            assertEquals(exp, r.getExpression());
            assertEquals(3, r.getMinLength());
            assertEquals(Integer.MAX_VALUE, r.getMaxLength());
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
    @Test
    public void test73()
    {
        try
        {
            String exp = "[a-z]+";
            System.out.println(exp);

            Regex r = Regex.compile(exp, Regex.Option.CASE_INSENSITIVE);

            assertEquals("OpQ", r.find("OpQ"));
            assertEquals("AAOpQC", r.find("AAOpQC"));
            assertEquals("AaOpQc", r.find("AaOpQc"));

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

    @Test
    public void test74()
    {
        try
        {
            String Char = "\\x01-\\uD7FF\\uE000-\\uFFFD";
            String exp = "<!\\-\\-((["+Char+"&&[^\\-]])|(\\-["+Char+"&&[^\\-]]))*\\-\\->";
            System.out.println(exp);

            Regex r = Regex.compile(exp);

            assertTrue(r.isMatch("<!-- tämä on kommentti -->"));
            assertTrue(r.isMatch("<!-- - tämäkin on kommentti - -->"));
            assertFalse(r.isMatch("<!-- tämä ei ole kommentti --->"));

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
    @Test
    public void test75()
    {
        try
        {
            String exp = "<!\\-\\-.*\\-\\->";
            System.out.println(exp);

            Regex r = Regex.compile(exp, Regex.Option.FIXED_ENDER);

            assertEquals("<!-- ", r.lookingAt("<!-- --> -->"));
            assertEquals("<!-- comment ", r.lookingAt("<!-- comment -->"));
            assertEquals("<!-- <- <comment> -> ", r.lookingAt("<!-- <- <comment> -> -->"));
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
    @Test
    public void test75a()
    {
        try
        {
            String exp = "/\\*.*\\*/";
            System.out.println(exp);

            Regex r = Regex.compile(exp, Regex.Option.FIXED_ENDER);

            assertEquals("/*-- ", r.lookingAt("/*-- */ */"));
            assertEquals("/*-- comment ", r.lookingAt("/*-- comment */"));
            assertEquals("/*-- /* <comment> * ", r.lookingAt("/*-- /* <comment> * */"));
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
    @Test
    public void test76()
    {
        try
        {
            String exp = "([\\+\\*\\?])|([\\x20\\x09\\x0D\\x0A]*\\,[\\x20\\x09\\x0D\\x0A]*)|([\\x20\\x09\\x0D\\x0A]*\\)[\\x20\\x09\\x0D\\x0A]*\\|[\\x20\\x09\\x0D\\x0A]*)";
            System.out.println(exp);

            Regex r = Regex.compile(exp);

            assertTrue(r.isMatch("*"));
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
    @Test
    public void test80()
    {
        try
        {
            Scope<NFAState<Integer>> nfaScope = new Scope<NFAState<Integer>>("test");
            Scope<DFAState<Integer>> dfaScope = new Scope<DFAState<Integer>>("test");

            NFA<Integer> nfa1 = Regex.createNFA(nfaScope, "GMT|EGFT", 1);
            NFA<Integer> nfa2 = Regex.createNFA(nfaScope, "GMT", 2);
            NFA<Integer> nfau = new NFA<Integer>(nfaScope, nfa1, nfa2);
            try
            {
                DFA<Integer> dfa = nfau.constructDFA(dfaScope);
            }
            catch (AmbiguousExpressionException ex)
            {
                fail();
            }
        }
        catch (SyntaxErrorException ex)
        {
            fail(ex.getMessage());
        }
    }
    @Test
    public void test81()
    {
        try
        {
            InputReader input = new InputReader("abcdefg");
            input.read();
            for (int count=0;count < 1000;count++)
            {
                input.read();
                input.read();
                input.read();
                input.clear();
                assertEquals("efg", input.buffered());
                input.insert("1".toCharArray());
                assertEquals("1efg", input.buffered());
                input.insert("23".toCharArray());
                assertEquals("231efg", input.buffered());
                input.insert("".toCharArray());
                assertEquals("231efg", input.buffered());
            }
        }
        catch (Exception ex)
        {
            fail();
        }
    }
    @Test
    public void test82()
    {
        try
        {
            File temp = File.createTempFile("test", null);
            FileOutputStream fos = new FileOutputStream(temp);
            for (int ii=0;ii<4000;ii++)
            {
                fos.write("qwerty".getBytes());
            }
            fos.close();
            
            InputReader input = new InputReader(temp, 100);
            int index=0;
            int rc = input.read();
            while (rc != -1)
            {
                if (index % 6 == 0)
                {
                    input.clear();
                }
                rc = input.read();
                index++;
            }
            fos.close();
        }
        catch (IOException ex)
        {
            fail();
        }
    }
    @Test
    public void test83()
    {
        Num n1 = new Num(100);
        Num n2 = new Num(2);
        Num n3 = new Num(3);
        Num n4 = new Num(4);
        Set<Num> s = new NumSet<Num>();
        boolean b;
        b = s.add(n1);
        assertTrue(b);
        assertEquals(1, s.size());
        b = s.add(n1);
        assertFalse(b);
        assertEquals(1, s.size());
        for (Num n : s)
        {
            assertEquals(n1, n);
        }
        s.clear();
        assertTrue(s.isEmpty());
        assertEquals(0, s.size());
        List<Num> l = new ArrayList<Num>();
        l.add(n1);
        l.add(n2);
        l.add(n3);
        l.add(n3);
        b = s.addAll(l);
        assertTrue(b);
        assertEquals(3, s.size());
        b = s.remove(n3);
        assertTrue(b);
        assertEquals(2, s.size());
        b = s.remove(n3);
        assertFalse(b);
        assertEquals(2, s.size());
    }
    @Test
    public void test84()
    {
        Num n1 = new Num(100);
        Num n2 = new Num(2);
        Num n3 = new Num(3);
        Num n4 = new Num(4);
        Map<Num,String> m = new NumMap<Num,String>();
        String s;
        s = m.put(n1, "n1");
        assertNull(s);
        assertEquals(1, m.size());
        s = m.put(n1, "n1");
        assertEquals("n1", s);
        assertEquals(1, m.size());
        for (Num n : m.keySet())
        {
            assertEquals(n1, n);
        }
        m.clear();
        assertTrue(m.isEmpty());
        assertEquals(0, m.size());
        Map<Num,String> l = new HashMap<Num,String>();
        l.put(n1, "n1");
        l.put(n2, "n2");
        l.put(n3, "n3");
        m.putAll(l);
        assertEquals(3, m.size());
        s = m.remove(n3);
        assertEquals(2, m.size());
        s = m.remove(n3);
        assertEquals(2, m.size());
    }
    @Test
    public void test85()
    {
        try
        {
            String exp = "[01]{6,966}";
            System.out.println(exp);

            Regex r = Regex.compile(exp);

            assertTrue(r.isMatch("01010101"));
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
}
