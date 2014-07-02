/*
 * Copyright (C) 2014 Timo Vesalainen
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

package org.vesalainen.parser;

import static org.vesalainen.parser.ParserFeature.*;
import org.vesalainen.parser.annotation.GenClassname;
import org.vesalainen.parser.annotation.GrammarDef;
import org.vesalainen.parser.annotation.ParseMethod;
import org.vesalainen.parser.annotation.Rule;
import org.vesalainen.parser.annotation.Terminal;
import org.vesalainen.parser.annotation.Terminals;
import org.vesalainen.parser.util.ParserInputObserver;

/**
 *
 * @author Timo Vesalainen
 */
@GenClassname("org.vesalainen.parser.ParserImpl")
@GrammarDef()
@Terminals({
    @Terminal(left="PLUS", expression="\\+"),
    @Terminal(left="MINUS", expression="\\-"),
    @Terminal(left="STAR", expression="\\*"),
    @Terminal(left="SLASH", expression="/"),
    @Terminal(left="LPAREN", expression="\\("),
    @Terminal(left="RPAREN", expression="\\)")
})
public abstract class Parser extends BaseParser implements ParserInputObserver
{
    @ParseMethod(
            start="Goal", 
            whiteSpace={"whiteSpace", "hex", "bin"},
            features={AutoClose}    // useless here, just for testing
    )
    public abstract long parseExt(String txt);
    
    @Terminal(expression="[ \t\r\n]+")
    protected abstract void whiteSpace();
    
    @Terminal(expression="0[xX][0-9abcdefABCDEF]+")
    protected String hex(String h)
    {
        String s = h.substring(2);
        int hi = Integer.parseInt(s, 16);
        return String.valueOf(hi);
    }
    @Terminal(expression="0[bB][01]+")
    protected char[] bin(String h)
    {
        String s = h.substring(2);
        int hi = Integer.parseInt(s, 2);
        return String.valueOf(hi).toCharArray();
    }
    public static Parser getInstance()
    {
        return (Parser) GenClassFactory.loadGenInstance(Parser.class);
    }
    @Terminal(left="NUMBER", expression="[0-9]+")
    public abstract long number(long str);
    @Rule(left="Goal")
    public long goal()
    {
        return 0;
    }
    @Rule(left="Goal", value={"Expression"})
    public abstract long goal(long expr);
    @Rule(left="Expression", value={"Expression", "PLUS", "Term"})
    public long plusExpression(long expr, long term)
    {
        System.err.println(expr+" + "+term);
        return expr + term;
    }
    @Rule(left="Expression", value={"Expression", "MINUS", "Term"})
    public long minusExpression(long expr, long term)
    {
        System.err.println(expr+" - "+term);
        return expr - term;
    }
    @Rule(left="Expression", value={"Term"})
    public long termExpression(long term)
    {
        System.err.println("termExpression("+term);
        return term;
    }
    @Rule(left="Term", value={"Term", "STAR", "Factor"})
    public long starTerm(long term, long factor)
    {
        System.err.println(term+" * "+factor);
        return term * factor;
    }
    @Rule(left="Term", value={"Term", "SLASH", "Factor"})
    public long slashTerm(long term, long factor)
    {
        System.err.println(term+" / "+factor);
        return term / factor;
    }
    @Rule(left="Factor", value={"NUMBER"})
    public long numberFactor(long term)
    {
        System.err.println("numberFactor("+term);
        return term;
    }
    @Rule(left="Factor", value={"MINUS", "NUMBER"})
    public long minusFactor(long term)
    {
        return -term;
    }

    @Override
    public void parserInput(int input)
    {
    }
}
