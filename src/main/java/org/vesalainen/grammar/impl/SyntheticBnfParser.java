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

import java.util.ArrayList;
import java.util.List;
import static org.vesalainen.grammar.GrammarConstants.*;
import static org.vesalainen.grammar.SyntheticBnfParserFactory.SyntheticBnfParserClass;
import org.vesalainen.grammar.SyntheticBnfParserIntf;
import org.vesalainen.parser.GenClassFactory;
import org.vesalainen.parser.annotation.GenClassname;
import org.vesalainen.parser.annotation.GrammarDef;
import org.vesalainen.parser.annotation.ParseMethod;
import org.vesalainen.parser.annotation.Rule;
import org.vesalainen.parser.annotation.Rules;
import org.vesalainen.parser.annotation.Terminal;

/**
 * SyntheticBnfParser parses bnf right side expressions and creates synthetic
 * rules for detected enhanced notations. Supported notations are:
 * 
 * @author Timo Vesalainen
 */
@GenClassname(SyntheticBnfParserClass)
@GrammarDef()
public abstract class SyntheticBnfParser implements SyntheticBnfParserIntf
{
    @ParseMethod(start="expression")
    @Override
    public abstract String parse(String text);
    
    @Rule(left="expression", value="symbol")
    protected String plainSymbol(String symbol)
    {
        return symbol;
    }
    
    @Rule(left="expression", value={"expression","'"+CIRCLED_ASTERISK+"'"})
    protected String plainStar(String expr)
    {
        return expr+'*';
    }
    
    @Rule(left="expression", value={"expression", "'"+CIRCLED_PLUS+"'"})
    protected String plainPlus(String expr)
    {
        return expr+'+';
    }
    
    @Rule(left="expression", value={"expression", "'"+INVERTED_QUESTION_MARK+"'"})
    protected String plainOpt(String expr)
    {
        return expr+'?';
    }
    
    @Rule(left="expression", value={"'"+SIGMA+"\\{'", "choiseList", "'\\}'"})
    protected String plainChoise(List<String> list)
    {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        boolean first = true;
        for (String choise : list)
        {
            if (first)
            {
                sb.append(choise);
                first = false;
            }
            else
            {
                sb.append('|');
                sb.append(choise);
            }
        }
        sb.append(')');
        return sb.toString();
    }
    
    @Rule({"expression"})
    protected List<String> choiseList(String type)
    {
        List<String> list = new ArrayList<>();
        list.add(type);
        return list;
    }
    @Rule({"choiseList", "pipe", "expression"})
    protected List<String> choiseList(List<String> typeList, String type)
    {
        typeList.add(type);
        return typeList;
    }
    @Rule(left="expression", value={"'"+PHI+"\\{'", "seqList", "'\\}'"})
    protected String plainSeq(List<String> list)
    {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        boolean first = true;
        for (String choise : list)
        {
            if (first)
            {
                sb.append(choise);
                first = false;
            }
            else
            {
                sb.append(' ');
                sb.append(choise);
            }
        }
        sb.append(')');
        return sb.toString();
    }
    
    @Rule({"expression"})
    protected List<String> seqList(String type)
    {
        List<String> list = new ArrayList<>();
        list.add(type);
        return list;
    }
    @Rule({"seqList", "comma", "expression"})
    protected List<String> seqList(List<String> typeList, String type)
    {
        typeList.add(type);
        return typeList;
    }
    @Terminal(expression="'[^']+'|`[^´]+´")
    protected String anonymousTerminal(String name)
    {
        name = name.substring(1, name.length()-1);
        return "'"+name+"'";
    }
    @Terminal(expression="[\\x21-\\x26\\x2d-\\x3e\\x40-\\x5f\\x61-\\x7b\\x7e-\\x7f\\xC0-\\xD6\\xD8-\\xF6]+")
    protected String symbolName(String name)
    {
        return name;
    }
    
    @Terminal(expression="\\|")
    protected void pipe()
    {
        
    }
    
    @Terminal(expression="\\,")
    protected void comma()
    {
        
    }
    
    @Rules({
    @Rule("symbolName"),
    @Rule("anonymousTerminal")
    })
    protected String symbol(String name)
    {
        return name;
    }
    
}
