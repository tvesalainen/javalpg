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
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.vesalainen.bcc.model.Typ;
import org.vesalainen.grammar.Grammar;
import static org.vesalainen.grammar.GrammarConstants.*;
import static org.vesalainen.grammar.SyntheticParserFactory.SyntheticParserClass;
import org.vesalainen.grammar.SyntheticParserIntf;
import static org.vesalainen.parser.ParserFeature.SingleThread;
import org.vesalainen.parser.annotation.GenClassname;
import org.vesalainen.parser.annotation.GrammarDef;
import org.vesalainen.parser.annotation.ParseMethod;
import org.vesalainen.parser.annotation.ParserContext;
import org.vesalainen.parser.annotation.Rule;
import org.vesalainen.parser.annotation.Rules;
import org.vesalainen.parser.annotation.Terminal;

/**
 * SyntheticParser parses synthetic grammar rules
 * @author Timo Vesalainen
 * @see <a href="doc-files/SyntheticParser-expression.html#BNF">BNF Syntax for synthetic expression</a>
 */
@GenClassname(SyntheticParserClass)
@GrammarDef()
public abstract class SyntheticParser implements SyntheticParserIntf
{
    @Override
    public TypeMirror parse(String text, Grammar g)
    {
        try
        {
            return parseIt(text, g);
        }
        catch (Throwable t)
        {
            throw new IllegalArgumentException("Problem with "+text, t);
        }
    }
    /**
     * 
     * @param text
     * @param g
     * @return 
     * @see <a href="doc-files/SyntheticParser-expression.html#BNF">BNF Syntax for synthetic expression</a>
     */
    @ParseMethod(start="expression", features={SingleThread})
    protected abstract TypeMirror parseIt(String text, @ParserContext("GRAMMAR") Grammar g);
    
    @Rule(left="expression", value="symbol")
    protected TypeMirror plainSymbol(String symbol, @ParserContext("GRAMMAR") Grammar g)
    {
        return g.getTypeForNonterminal(symbol);
    }
    
    @Rule(left="expression", value={"expression","'"+CIRCLED_ASTERISK+"'"})
    protected TypeMirror plainStar(TypeMirror type)
    {
        return type;
    }
    
    @Rule(left="expression", value={"expression", "'"+CIRCLED_PLUS+"'"})
    protected TypeMirror plainPlus(TypeMirror type)
    {
        return type;
    }
    
    @Rule(left="expression", value={"expression", "'"+INVERTED_QUESTION_MARK+"'"})
    protected TypeMirror plainOpt(TypeMirror type)
    {
        return type;
    }
    
    @Rule(left="expression", value={"'"+SIGMA+"\\{'", "choiseList", "'\\}'"})
    protected TypeMirror plainChoise(List<TypeMirror> typeList)
    {
        TypeMirror type = typeList.get(0);
        for (TypeMirror t : typeList)
        {
            if (!Typ.isSameType(t, type))
            {
                throw new IllegalArgumentException("all choise types not the same");
            }
        }
        return type;
    }
    
    @Rule({"expression"})
    protected List<TypeMirror> choiseList(TypeMirror type)
    {
        List<TypeMirror> list = new ArrayList<>();
        list.add(type);
        return list;
    }
    @Rule({"choiseList", "pipe", "expression"})
    protected List<TypeMirror> choiseList(List<TypeMirror> typeList, TypeMirror type)
    {
        typeList.add(type);
        return typeList;
    }
    @Rule(left="expression", value={"'"+PHI+"\\{'", "seqList", "'\\}'"})
    protected TypeMirror plainSeq(List<TypeMirror> typeList)
    {
        TypeMirror type = Typ.Void;
        for (TypeMirror t : typeList)
        {
            if (t.getKind() != TypeKind.VOID)
            {
                if (type.getKind() != TypeKind.VOID)
                {
                    throw new IllegalArgumentException("all one seq type != void allowed");
                }
                type = t;
            }
        }
        return type;
    }
    
    @Rule({"expression"})
    protected List<TypeMirror> seqList(TypeMirror type)
    {
        List<TypeMirror> list = new ArrayList<>();
        list.add(type);
        return list;
    }
    @Rule({"seqList", "comma", "expression"})
    protected List<TypeMirror> seqList(List<TypeMirror> typeList, TypeMirror type)
    {
        typeList.add(type);
        return typeList;
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
    
    @Terminal(expression="\\|")
    protected abstract void pipe();
    
    @Terminal(expression="\\,")
    protected abstract void comma();
    
    @Rules({
    @Rule("symbolName"),
    @Rule("anonymousTerminal")
    })
    protected String symbol(String name)
    {
        return name;
    }
    
}
