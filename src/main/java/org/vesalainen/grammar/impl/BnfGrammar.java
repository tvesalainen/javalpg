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
package org.vesalainen.grammar.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.ExecutableElement;
import org.vesalainen.bcc.model.El;
import static org.vesalainen.grammar.BnfGrammarFactory.BnfGrammarClass;
import org.vesalainen.grammar.BnfGrammarIntf;
import org.vesalainen.grammar.Grammar;
import static org.vesalainen.grammar.GrammarConstants.*;
import org.vesalainen.parser.annotation.GenClassname;
import org.vesalainen.parser.annotation.GenRegex;
import org.vesalainen.parser.annotation.GrammarDef;
import org.vesalainen.parser.annotation.ParseMethod;
import org.vesalainen.parser.annotation.ParserContext;
import org.vesalainen.parser.annotation.Rule;
import org.vesalainen.parser.annotation.Rules;
import org.vesalainen.parser.annotation.Terminal;
import org.vesalainen.parser.util.Reducers;
import org.vesalainen.regex.Regex;

/**
 * BnfGrammar is a base class for generated BnfParser. There are parse method for 
 * just right hand side and full BNF. BNF grammar consists of nonterminals, terminals
 * and anonymous terminals which are regular expressions.
 * <p>
 * Nonterminal and terminal names can contain all ascii characters except 
 * control characters 0x00 - 0x1f, space 0x20, apostrophe ' 0x27, parenthesis ( 0x28
 * ) 0x29, star * 0x2a, ´ 0x2c, plus + 0x2b, question mark ? 0x3f, ` 0x60 and 
 * vertical bar | 0x7c.
 * <p>
 * Anonymous terminals are either strings starting and ending with apostrophe. 
 * E.g '[0-9]+' or if apostrophes need to exist in regular expression it is 
 * possible to use expression `[0-9]+´. 
 * <p>
 * Note that in produced grammar anonymous terminals are always quoted with apostrophes!
 * @author Timo Vesalainen
 * @see <a href="doc-files/BnfGrammar-bnf.html#BNF">BNF Syntax All</a>
 * @see <a href="doc-files/BnfGrammar-seqs.html#BNF">BNF Syntax of right hand side</a>
 */
@GenClassname(BnfGrammarClass)
@GrammarDef()
public abstract class BnfGrammar implements BnfGrammarIntf
{
    
    @Rules({
    @Rule,
    @Rule({"bnf", "rule"})
    })
    protected void bnf()
    {
        
    }
            
    @Rule({"symbol", "products", "seqs", "ln"})
    protected void rule(String symbol, List<String> seq, @ParserContext("GRAMMAR") Grammar g)
    {
        g.addRule(symbol, seq);
    }
    /*
    @Rule("seqs")
    protected String rhs(
            List<String> seq, 
            @ParserContext("GRAMMAR") Grammar g
            )
    {
        switch (seq.size())
        {
            case 0:
                return null;
            case 1:
                return seq.get(0);
            default:
                String lhs = "seq_"+Grammar.makeName(seq);
                g.addRule(lhs, seq);
                return lhs;
        }
    }
     * 
     */
    @Rule({"choicePart"})
    protected String choice(List<String> choice, @ParserContext("GRAMMAR") Grammar g) throws IOException
    {
        String nt = makeName(choice, SIGMA, '|');
        ExecutableElement reducer = El.getMethod(Reducers.class, "get", Object.class);
        if (reducer == null)
        {
            throw new IllegalArgumentException("???");
        }
        for (String c : choice)
        {
            g.addSyntheticRule(reducer, nt, c);
        }
        return nt;
    }
    @Rule({"seqPart"})
    protected String seq(List<String> seq, @ParserContext("GRAMMAR") Grammar g) throws IOException
    {
        String nt = makeName(seq, PHI, ',');
        ExecutableElement reducer = El.getMethod(Reducers.class, "get", Object.class);
        if (reducer == null)
        {
            throw new IllegalArgumentException("???");
        }
        g.addSyntheticRule(reducer, nt, seq);
        return nt;
    }

    @Rules({
    @Rule({"symbol", "quantifier"}),
    @Rule({"choice", "quantifier"}),
    @Rule({"seq", "quantifier"})
    })
    protected String part(String nt, char quantifier, @ParserContext("GRAMMAR") Grammar g) throws IOException
    {
        try
        {
            return Grammar.quantifierRules(nt, quantifier, g);
        }
        catch (NoSuchMethodException ex)
        {
            throw new IOException(ex);
        }
    }

    @Rule({"'\\('", "choices", "'\\)'"})
    protected List<String> choicePart(List<String> choices)
    {
        return choices;
    }

    @Rule({"part", "or", "part"})
    protected List<String> choices(String cp1, String cp2)
    {
        List<String> choices = new ArrayList<>();
        choices.add(cp1);
        choices.add(cp2);
        return choices;
    }
    @Rule({"choices", "or", "part"})
    protected List<String> choices(List<String> choices, String symbol)
    {
        choices.add(symbol);
        return choices;
    }
    @Rule({"'\\('", "seqs", "'\\)'"})
    protected List<String> seqPart(List<String> seqs)
    {
        return seqs;
    }

    @Rule({"part"})
    protected List<String> seqs(String symbol)
    {
        List<String> list = new ArrayList<>();
        list.add(symbol);
        return list;
    }
    @Rule({"seqs", "part"})
    protected List<String> seqs(List<String> seqs, String symbol)
    {
        seqs.add(symbol);
        return seqs;
    }

    @Rule
    protected char  quantifier()
    {
        return 0;
    }
    @Rule("quantifierChar")
    protected char  quantifier(char quantifier)
    {
        return quantifier;
    }
    
    @Terminal(expression="[\\+\\*\\?]")
    protected char quantifierChar(char  quantifier)
    {
        return quantifier;
    }
    
    @Terminal(expression="'[^']+'|`[^´]+´")
    protected String anonymousTerminal(String name, @ParserContext("GRAMMAR") Grammar g)
    {
        name = name.substring(1, name.length()-1);
        g.addAnonymousTerminal(name);
        return "'"+name+"'";
    }
    @Terminal(expression="[\\x21-\\x26\\x2d-\\x3e\\x40-\\x5f\\x61-\\x7b\\x7e-\\x7f\\xC0-\\xD6\\xD8-\\xF6]+")
    protected String symbolName(String name)
    {
        return name;
    }
    
    @Rules({
    @Rule("symbolName"),
    @Rule("anonymousTerminal")
    })
    protected String symbol(String name)
    {
        return name;
    }
    
    @Terminal(expression="[ \t]+")
    protected void s()
    {
    }
    
    @Rule("'[\r\n]+'")
    protected void ln()
    {
        
    }
    
    @Rule("'\\|'")
    protected void or()
    {
        
    }
    
    @Rule({"'::='"})
    protected void products()
    {
        
    }
    @ParseMethod(start="bnf", whiteSpace={"s"})
    @Override
    public void parseBnf(
            CharSequence text, 
            @ParserContext("GRAMMAR") Grammar g
            )
    {
        throw new UnsupportedOperationException();
    }
    
    @GenRegex("[\\x00-\\xff]+")
    protected static Regex inputCheck;
    
    @Override
    public List<String> parseRhs(String text, @ParserContext("GRAMMAR") Grammar g)
    {
        if (inputCheck != null)
        {
            inputCheck.match(text);
        }
        return parseRhsString(text, g);
    }
    @ParseMethod(start="seqs", whiteSpace={"s"})
    protected abstract List<String> parseRhsString(String text, @ParserContext("GRAMMAR") Grammar g);

    private static String makeName(List<String> list, char prefix, char separator)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        sb.append('{');
        for (String str : list)
        {
            if (sb.length() > 2)
            {
                sb.append(separator);
            }
            sb.append(str);
        }
        sb.append('}');
        return sb.toString();
    }
}
