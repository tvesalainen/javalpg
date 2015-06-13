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
package org.vesalainen.regex.impl;

import org.vesalainen.grammar.state.NFA;
import org.vesalainen.grammar.state.NFAState;
import org.vesalainen.grammar.state.Scope;
import org.vesalainen.parser.annotation.GrammarDef;
import org.vesalainen.parser.annotation.ParseMethod;
import org.vesalainen.parser.annotation.ParserContext;
import org.vesalainen.parser.annotation.Rule;
import org.vesalainen.parser.annotation.Rules;
import org.vesalainen.parser.annotation.Terminal;
import org.vesalainen.regex.Regex.Option;
import java.lang.Character.UnicodeBlock;
import java.util.HashSet;
import java.util.Set;
import static org.vesalainen.parser.ParserFeature.SingleThread;
import org.vesalainen.parser.annotation.GenClassname;
import org.vesalainen.regex.Quantifier;
import org.vesalainen.regex.Range;
import org.vesalainen.regex.RangeSet;
import static org.vesalainen.regex.RegexParserFactory.RegexParserClass;
import org.vesalainen.regex.RegexParserIntf;

/**
 * This Parser class parses regular expression making an NFA
 * @author tkv
 * @see <a href="doc-files/RegexParser-regexp.html#BNF">BNF Syntax for Regular expression</a>
 */
@GrammarDef()
@GenClassname(RegexParserClass)
public abstract class RegexGrammar<T> implements RegexParserIntf<T>
{
    private static final String REGEXCONTROL = "\\[\\]\\(\\)\\\\\\-\\^\\*\\+\\?\\|\\.\\{\\}\\&\\$\\,";

    /**
     * Creates a Nondeterministic finite automata from regular expression
     * @param expression
     * @param reducer Reducer marks the accepting state with unique identifier
     * @param ignoreCase
     * @return
     */
    @Override
    public NFA<T> createNFA(Scope<NFAState<T>> scope, String expression, T reducer, Option... options)
    {
        Literal literal = new Literal();
        NFA<T> nfa = parse(expression, scope, literal, options);
        if (Option.supports(options, Option.FIXED_ENDER))
        {
            NFA.modifyFixedEnder(nfa);
        }
        NFAState<T> last = nfa.getLast();
        last.setToken(reducer);
        if (literal.isLiteral())
        {
            last.changePriority(1);
        }
        if (Option.supports(options, Option.ACCEPT_IMMEDIATELY))
        {
            last.setAcceptImmediately(true);
        }
        return nfa;
    }
    @ParseMethod(start="regexp", features={SingleThread})
    protected abstract NFA parse(
            String expression,
            @ParserContext("FACTORY") Scope<NFAState<T>> factory,
            @ParserContext("LITERAL") Literal literal,
            @ParserContext("OPTION") Option... options
            );

    @Rule({"branch"})
    protected NFA regexp(NFA branch)
    {
        return branch;
    }
    @Rule({"regexp", "'\\|'", "branch"})
    protected NFA regexp(
            NFA branch,
            NFA piece,
            @ParserContext("FACTORY") Scope<NFAState<T>> factory,
            @ParserContext("LITERAL") Literal literal)
    {
        literal.setLiteral(false);
        return new NFA(factory, branch, piece);
    }

    @Rule({"branch", "piece"})
    protected NFA branch(NFA<T> branch, NFA<T> piece)
    {
        branch.concat(piece);
        return branch;
    }

    @Rule({"piece"})
    protected NFA<T> branch(NFA<T> piece)
    {
        return piece;
    }
    @Rule(left="piece", value={"'\\('", "regexp", "'\\)'", "quantifier"})
    protected NFA<T> piece(
            NFA<T> atom,
            Quantifier quantifier,
            @ParserContext("FACTORY") Scope<NFAState<T>> factory,
            @ParserContext("LITERAL") Literal literal)
    {
        literal.setLiteral(false);
        NFA<T> result = null;
        for (int ii=0;ii<quantifier.getMin();ii++)
        {
            NFA<T> r = new NFA<>(factory, atom);
            if (result == null)
            {
                result = r;
            }
            else
            {
                result.concat(r);
            }
        }
        if (quantifier.getMax() == Integer.MAX_VALUE)
        {
            NFA<T> r = new NFA<>(factory, atom);
            r.star();
            if (result == null)
            {
                result = r;
            }
            else
            {
                result.concat(r);
            }
        }
        else
        {
            for (int ii=quantifier.getMin();ii<quantifier.getMax();ii++)
            {
                NFA<T> r = new NFA<>(factory, atom);
                r.opt();
                if (result == null)
                {
                    result = r;
                }
                else
                {
                    result.concat(r);
                }
            }
        }
        return result;
    }
    @Rule({"atom", "quantifier"})
    protected NFA<T> piece(
            RangeSet atom,
            Quantifier quantifier,
            @ParserContext("FACTORY") Scope<NFAState<T>> factory,
            @ParserContext("LITERAL") Literal literal)
    {
        if (quantifier.getMin() != 1 || quantifier.getMax() != 1)
        {
            literal.setLiteral(false);
        }
        NFA<T> result = null;
        for (int ii=0;ii<quantifier.getMin();ii++)
        {
            NFA<T> r = new NFA<>(factory, atom);
            if (result == null)
            {
                result = r;
            }
            else
            {
                result.concat(r);
            }
        }
        if (quantifier.getMax() == Integer.MAX_VALUE)
        {
            NFA<T> r = new NFA<>(factory, atom);
            r.star();
            if (result == null)
            {
                result = r;
            }
            else
            {
                result.concat(r);
            }
        }
        else
        {
            Set<NFAState<T>> skippers = new HashSet<>();
            for (int ii=quantifier.getMin();ii<quantifier.getMax();ii++)
            {
                NFA<T> r = new NFA<>(factory, atom);
                skippers.add(r.getFirst());
                if (result == null)
                {
                    result = r;
                }
                else
                {
                    result.concat(r);
                }
            }
            for (NFAState<T> s : skippers)
            {
                s.addEpsilon(result.getLast());
            }
        }
        return result;
    }
    @Rules({
    @Rule("charRange"),
    @Rule("rangeDef"),
    @Rule("boundaryMatcher")
    })
    protected RangeSet atom(RangeSet rs)
    {
        return rs;
    }
    @Rule({"'\\['", "rs1", "'\\]'"})
    protected RangeSet rangeDef(RangeSet rs)
    {
        return rs;
    }
    @Rules({
    @Rule("posRange"),
    @Rule("negRange"),
    @Rule("inclusiveRange"),
    @Rule("intersectRange")
    })
    protected RangeSet rs1(RangeSet rs)
    {
        return rs;
    }
    @Rule({"rangeList"})
    protected RangeSet posRange(RangeSet rs)
    {
        return rs;
    }
    @Rule({"'\\^'", "rangeList"})
    protected RangeSet negRange(RangeSet rs)
    {
        return rs.complement();
    }
    @Rule({"rangeList", "'\\['", "rs1", "'\\]'"})
    protected RangeSet inclusiveRange(RangeSet rs1, RangeSet rs2)
    {
        rs1.add(rs2);
        return rs1;
    }
    @Rule({"rangeList", "'\\&\\&'", "'\\['", "rs1", "'\\]'"})
    protected RangeSet intersectRange(RangeSet rs1, RangeSet rs2)
    {
        return RangeSet.intersect(rs1, rs2);
    }
    @Rule({"rangeListEntry"})
    protected RangeSet rangeList(RangeSet rs)
    {
        return rs;
    }
    @Rule({"rangeList", "rangeListEntry"})
    protected RangeSet rangeList(RangeSet rs1, RangeSet rs2)
    {
        rs1.add(rs2);
        return rs1;
    }
    @Rules({
    @Rule("charRange"),
    @Rule("negativeCharRange"),
    @Rule("dashRange")
    })
    protected RangeSet rangeListEntry(RangeSet rs)
    {
        return rs;
    }
    @Rule({"'\\.'"})
    protected RangeSet charRange()
    {
        RangeSet rs = new RangeSet();
        rs.add(0, Integer.MAX_VALUE);
        return rs;
    }
    @Rule({"character"})
    protected RangeSet charRange(int cc, @ParserContext("OPTION") Option... options)
    {
        RangeSet rs = new RangeSet(cc);
        if (Option.supports(options, Option.CASE_INSENSITIVE))
        {
            if (Character.isLowerCase(cc))
            {
                rs.add(Character.toUpperCase(cc));
            }
            else
            {
                if (Character.isUpperCase(cc))
                {
                    rs.add(Character.toLowerCase(cc));
                }
            }
        }
        return rs;
    }
    @Rules({
    @Rule({"'\\\\'", "characterClass"}),
    @Rule({"'\\\\'", "'p'", "posixCharacterClass"})
    })
    protected RangeSet charRange(RangeSet rs)
    {
        return rs;
    }
    @Rules({
    @Rule({"'\\\\'", "'P'", "posixCharacterClass"})
    })
    protected RangeSet negativeCharRange(RangeSet rs)
    {
        return rs.complement();
    }
    @Rule({"character", "'\\-'", "character"})
    protected RangeSet dashRange(int from, int to, @ParserContext("OPTION") Option... options)
    {
        RangeSet rs = new RangeSet();
        if (Option.supports(options, Option.CASE_INSENSITIVE))
        {
            for (int cc=from;cc<=to;cc++)
            {
                rs.add(cc);
                if (Character.isLowerCase(cc))
                {
                    rs.add(Character.toUpperCase(cc));
                }
                else
                {
                    if (Character.isUpperCase(cc))
                    {
                        rs.add(Character.toLowerCase(cc));
                    }
                }
            }
        }
        else
        {
            rs.add(from, to+1);
        }
        return rs;
    }
    @Rules({
    @Rule("star"),
    @Rule("plus"),
    @Rule("opt"),
    @Rule("braceQ1"),
    @Rule("braceQ2")
    })
    protected Quantifier quantifier(Quantifier q)
    {
        return q;
    }
    @Rule()
    protected Quantifier quantifier()
    {
        return new Quantifier(1);
    }
    @Rule({"'\\{'", "digit", "'\\}'"})
    protected Quantifier braceQ1(int i)
    {
        return new Quantifier(i);
    }
    @Rule({"'\\{'", "digit", "'\\,'", "'\\}'"})
    protected Quantifier braceQ2(int i)
    {
        return new Quantifier(i, Integer.MAX_VALUE);
    }
    @Rule({"'\\{'", "digit", "'\\,'", "digit", "'\\}'"})
    protected Quantifier braceQ2(int min, int max)
    {
        return new Quantifier(min, max);
    }
    @Rules({
    @Rule("beginningOfLine"),
    @Rule("endOfLine"),
    @Rule("wordBoundary"),
    @Rule("nonWordBoundary"),
    @Rule("beginningOfInput"),
    @Rule("endOfPreviousMatch"),
    @Rule("endOfInputOrLine"),
    @Rule("endOfInput")
    })
    protected RangeSet boundaryMatcher(Range range)
    {
        RangeSet rs = new RangeSet();
        rs.add(range);
        return rs;
    }
    @Rule({"'\\^'"})
    protected Range beginningOfLine()
    {
        return new Range(Range.BoundaryType.BOL);
    }
    @Rule({"'\\$'"})
    protected Range endOfLine()
    {
        return new Range(Range.BoundaryType.EOL);
    }
    @Rule({"'\\\\'", "'b'"})
    protected Range wordBoundary()
    {
        return new Range(Range.BoundaryType.WB);
    }
    @Rule({"'\\\\'", "'B'"})
    protected Range nonWordBoundary()
    {
        return new Range(Range.BoundaryType.NWB);
    }
    @Rule({"'\\\\'", "'A'"})
    protected Range beginningOfInput()
    {
        return new Range(Range.BoundaryType.BOI);
    }
    @Rule({"'\\\\'", "'G'"})
    protected Range endOfPreviousMatch()
    {
        return new Range(Range.BoundaryType.EOPM);
    }
    @Rule({"'\\\\'", "'Z'"})
    protected Range endOfInputOrLine()
    {
        return new Range(Range.BoundaryType.EOIL);
    }
    @Rule({"'\\\\'", "'z'"})
    protected Range endOfInput()
    {
        return new Range(Range.BoundaryType.EOI);
    }
    @Terminal(expression = "\\*")
    protected Quantifier star()
    {
        return new Quantifier(0, Integer.MAX_VALUE);
    }
    @Terminal(expression = "\\?")
    protected Quantifier opt()
    {
        return new Quantifier(0, 1);
    }
    @Terminal(expression = "\\+")
    protected Quantifier plus()
    {
        return new Quantifier(1, Integer.MAX_VALUE);
    }
    @Terminal(expression = "[0-9]+")
    protected int digit(int i)
    {
        return i;
    }
    @Terminal(expression = "[1-9]")
    protected int singleDigit(int i)
    {
        return i;
    }
    @Rules({
    @Rule({"'\\\\'", "escaped"}),
    @Rule("notRegexControl")
    })
    protected int character(int cc)
    {
        return cc;
    }
    @Rules({
    @Rule("tab"),
    @Rule("nl"),
    @Rule("cr"),
    @Rule("ff"),
    @Rule("alert"),
    @Rule("esc"),
    @Rule("octal"),
    @Rule("hex"),
    @Rule("hex2"),
    @Rule({"'c'", "control"}),
    @Rule({"regexControlCharacter"})
    })
    protected int escaped(int cc)
    {
        return cc;
    }
    @Terminal(expression="[dDsSwW]")
    protected RangeSet characterClass(char cc)
    {
        RangeSet rs = new RangeSet();
        switch (cc)
        {
            case 'd':
                rs = new RangeSet();
                rs.add(new Range('0', '9'+1));
                return rs;
            case 'D':
                rs = new RangeSet();
                rs.add(new Range('0', '9'+1));
                return rs.complement();
            case 's':
                rs = new RangeSet();
                rs.add(new Range(' '));
                rs.add(new Range('\t'));
                rs.add(new Range('\n'));
                rs.add(new Range(0x0B));
                rs.add(new Range('\f'));
                rs.add(new Range('\r'));
                return rs;
            case 'S':
                rs = new RangeSet();
                rs.add(new Range(' '));
                rs.add(new Range('\t'));
                rs.add(new Range('\n'));
                rs.add(new Range(0x0B));
                rs.add(new Range('\f'));
                rs.add(new Range('\r'));
                return rs.complement();
            case 'w':
                rs = new RangeSet();
                rs.add(new Range('a', 'z'+1));
                rs.add(new Range('A', 'Z'+1));
                rs.add(new Range('0', '9'+1));
                rs.add(new Range('_'));
                return rs;
            case 'W':
                rs = new RangeSet();
                rs.add(new Range('a', 'z'+1));
                rs.add(new Range('A', 'Z'+1));
                rs.add(new Range('0', '9'+1));
                rs.add(new Range('_'));
                return rs.complement();
            default:
                throw new IllegalArgumentException(cc+ "unexpected");
        }
    }
    @Rules({
        @Rule("posixLower"),
        @Rule("posixUpper"),
        @Rule("posixASCII"),
        @Rule("posixAlpha"),
        @Rule("posixDigit"),
        @Rule("posixAlnum"),
        @Rule("posixPunct"),
        @Rule("posixGraph"),
        @Rule("posixPrint"),
        @Rule("posixBlank"),
        @Rule("posixCntrl"),
        @Rule("posixXDigit"),
        @Rule("posixSpace"),
        @Rule("javaLowerCase"),
        @Rule("javaUpperCase"),
        @Rule("javaWhitespace"),
        @Rule("javaMirrored"),
        @Rule("unicodeBlock"),
        @Rule("unicodeCategory"),
        @Rule("unicodeLetter")
    })
    protected RangeSet posixCharacterClass(RangeSet rs)
    {
        return rs;
    }
    @Terminal(expression =  "\\{Lower\\}")
    protected RangeSet posixLower()
    {
        RangeSet rs = new RangeSet();
        rs.add(new Range('a', 'z'+1));
        return rs;
    }
    @Terminal(expression =  "\\{Upper\\}")
    protected RangeSet posixUpper(@ParserContext("OPTION") Option... options)
    {
        if (Option.supports(options, Option.CASE_INSENSITIVE))
        {
            return posixLower();
        }
        RangeSet rs = new RangeSet();
        rs.add(new Range('A', 'Z'+1));
        return rs;
    }
    @Terminal(expression =  "\\{ASCII\\}")
    protected RangeSet posixASCII()
    {
        RangeSet rs = new RangeSet();
        rs.add(new Range(0, 0x80));
        return rs;
    }
    @Terminal(expression =  "\\{Alpha\\}")
    protected RangeSet posixAlpha()
    {
        RangeSet rs = new RangeSet();
        rs.add(new Range('a', 'z'+1));
        rs.add(new Range('A', 'Z'+1));
        return rs;
    }
    @Terminal(expression =  "\\{Digit\\}")
    protected RangeSet posixDigit()
    {
        RangeSet rs = new RangeSet();
        rs.add(new Range('0', '9'+1));
        return rs;
    }
    @Terminal(expression =  "\\{Alnum\\}")
    protected RangeSet posixAlnum()
    {
        RangeSet rs = new RangeSet();
        rs.add(new Range('a', 'z'+1));
        rs.add(new Range('A', 'Z'+1));
        rs.add(new Range('0', '9'+1));
        return rs;
    }
    @Terminal(expression =  "\\{Punct\\}")
    protected RangeSet posixPunct()
    {
        RangeSet rs = new RangeSet();
        rs.add("!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~".toCharArray());
        return rs;
    }
    @Terminal(expression =  "\\{Graph\\}")
    protected RangeSet posixGraph()
    {
        RangeSet rs = new RangeSet();
        rs.add(new Range('a', 'z'+1));
        rs.add(new Range('A', 'Z'+1));
        rs.add(new Range('0', '9'+1));
        rs.add("!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~".toCharArray());
        return rs;
    }
    @Terminal(expression =  "\\{Print\\}")
    protected RangeSet posixPrint()
    {
        RangeSet rs = new RangeSet();
        rs.add(new Range('a', 'z'+1));
        rs.add(new Range('A', 'Z'+1));
        rs.add(new Range('0', '9'+1));
        rs.add("!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~".toCharArray());
        rs.add(' ');
        return rs;
    }
    @Terminal(expression =  "\\{Blank\\}")
    protected RangeSet posixBlank()
    {
        RangeSet rs = new RangeSet();
        rs.add(' ');
        rs.add('\t');
        return rs;
    }
    @Terminal(expression =  "\\{Cntrl\\}")
    protected RangeSet posixCntrl()
    {
        RangeSet rs = new RangeSet();
        rs.add(new Range(0, 0x20));
        rs.add(0x7f);
        return rs;
    }
    @Terminal(expression =  "\\{XDigit\\}")
    protected RangeSet posixXDigit()
    {
        RangeSet rs = new RangeSet();
        rs.add(new Range('0', '9'+1));
        rs.add(new Range('a', 'f'+1));
        rs.add(new Range('A', 'F'+1));
        return rs;
    }
    @Terminal(expression =  "\\{Space\\}")
    protected RangeSet posixSpace()
    {
        RangeSet rs = new RangeSet();
        rs.add(new Range(' '));
        rs.add(new Range('\t'));
        rs.add(new Range('\n'));
        rs.add(new Range(0x0B));
        rs.add(new Range('\f'));
        rs.add(new Range('\r'));
        return rs;
    }
    @Terminal(expression =  "\\{javaLowerCase\\}")
    protected RangeSet javaLowerCase()
    {
        RangeSet rs = new RangeSet();
        for (int ii=0;ii<Character.MAX_VALUE;ii++)
        {
            char cc = (char) ii;
            if (Character.isLowerCase(cc))
            {
                rs.add(new Range(cc));
            }
        }
        return rs;
    }
    @Terminal(expression =  "\\{javaUpperCase\\}")
    protected RangeSet javaUpperCase(@ParserContext("OPTION") Option... options)
    {
        if (Option.supports(options, Option.CASE_INSENSITIVE))
        {
            return javaLowerCase();
        }
        RangeSet rs = new RangeSet();
        for (int ii=0;ii<Character.MAX_VALUE;ii++)
        {
            char cc = (char) ii;
            if (Character.isUpperCase(cc))
            {
                rs.add(new Range(cc));
            }
        }
        return rs;
    }
    @Terminal(expression =  "\\{javaWhitespace\\}")
    protected RangeSet javaWhitespace()
    {
        RangeSet rs = new RangeSet();
        for (int ii=0;ii<Character.MAX_VALUE;ii++)
        {
            char cc = (char) ii;
            if (Character.isWhitespace(cc))
            {
                rs.add(new Range(cc));
            }
        }
        return rs;
    }
    @Terminal(expression =  "\\{javaMirrored\\}")
    protected RangeSet javaMirrored()
    {
        RangeSet rs = new RangeSet();
        for (int ii=0;ii<Character.MAX_VALUE;ii++)
        {
            char cc = (char) ii;
            if (Character.isMirrored(cc))
            {
                rs.add(new Range(cc));
            }
        }
        return rs;
    }
    @Terminal(expression =  "\\{L\\}|\\{IsL\\}")
    protected RangeSet unicodeLetter()
    {
        RangeSet rs = new RangeSet();
        for (int ii=0;ii<Character.MAX_VALUE;ii++)
        {
            char cc = (char) ii;
            if (Character.isLetter(cc))
            {
                rs.add(new Range(cc));
            }
        }
        return rs;
    }
    @Rule({"'\\{In'", "blockName", "'\\}'"})
    protected RangeSet unicodeBlock(String blockName)
    {
        Character.UnicodeBlock b = Character.UnicodeBlock.forName(blockName);
        RangeSet rs = new RangeSet();
        for (int ii=0;ii<Character.MAX_VALUE;ii++)
        {
            char cc = (char) ii;
            if (b.equals(UnicodeBlock.of(cc)))
            {
                rs.add(new Range(cc));
            }
        }
        return rs;
    }
    @Rule({"category"})
    protected RangeSet unicodeCategory(int category)
    {
        RangeSet rs = new RangeSet();
        for (int ii=0;ii<Character.MAX_VALUE;ii++)
        {
            char cc = (char) ii;
            if (Character.getType(cc) == category)
            {
                rs.add(new Range(cc));
            }
        }
        return rs;
    }
    @Rules({
    @Rule({"combiningSpacingMark"}),
    @Rule({"connectorPunctuation"}),
    @Rule({"controlCategory"}),
    @Rule({"currencySymbol"}),
    @Rule({"dashPunctuation"}),
    @Rule({"decimalDigitNumber"}),
    @Rule({"enclosingMark"}),
    @Rule({"endPunctuation"}),
    @Rule({"finalQuotePunctuation"}),
    @Rule({"format"}),
    @Rule({"initialQuotePunctuation"}),
    @Rule({"letterNumber"}),
    @Rule({"lineSeparator"}),
    @Rule({"lowercaseLetter"}),
    @Rule({"mathSymbol"}),
    @Rule({"modifierLetter"}),
    @Rule({"modifierSymbol"}),
    @Rule({"nonSpacingMark"}),
    @Rule({"otherLetter"}),
    @Rule({"otherNumber"}),
    @Rule({"otherPunctuation"}),
    @Rule({"otherSymbol"}),
    @Rule({"paragraphSeparator"}),
    @Rule({"privateUse"}),
    @Rule({"spaceSeparator"}),
    @Rule({"startPunctuation"}),
    @Rule({"surrogate"}),
    @Rule({"titleCaseLetter"}),
    @Rule({"unassigned"}),
    @Rule({"uppercaseLetter"})
    })
    protected int category(int category)
    {
        return category;
    }
    @Terminal(expression =  "\\{Mc\\}|\\{IsMc\\}")
    protected int combiningSpacingMark()
    {
        return Character.COMBINING_SPACING_MARK;
    }
    @Terminal(expression =  "\\{Pc\\}|\\{IsPc\\}")
    protected int connectorPunctuation()
    {
        return Character.CONNECTOR_PUNCTUATION;
    }
    @Terminal(expression =  "\\{Cc\\}|\\{IsCc\\}")
    protected int controlCategory()
    {
        return Character.CONTROL;
    }
    @Terminal(expression =  "\\{Sc\\}|\\{IsSc\\}")
    protected int currencySymbol()
    {
        return Character.CURRENCY_SYMBOL;
    }
    @Terminal(expression =  "\\{Pd\\}|\\{IsPd\\}")
    protected int dashPunctuation()
    {
        return Character.DASH_PUNCTUATION;
    }
    @Terminal(expression =  "\\{Nd\\}|\\{IsNd\\}")
    protected int decimalDigitNumber()
    {
        return Character.DECIMAL_DIGIT_NUMBER;
    }
    @Terminal(expression =  "\\{Me\\}|\\{IsMe\\}")
    protected int enclosingMark()
    {
        return Character.ENCLOSING_MARK;
    }
    @Terminal(expression =  "\\{Pe\\}|\\{IsPe\\}")
    protected int endPunctuation()
    {
        return Character.END_PUNCTUATION;
    }
    @Terminal(expression =  "\\{Pf\\}|\\{IsPf\\}")
    protected int finalQuotePunctuation()
    {
        return Character.FINAL_QUOTE_PUNCTUATION;
    }
    @Terminal(expression =  "\\{Cf\\}|\\{IsCf\\}")
    protected int format()
    {
        return Character.FORMAT;
    }
    @Terminal(expression =  "\\{Pi\\}|\\{IsPi\\}")
    protected int initialQuotePunctuation()
    {
        return Character.INITIAL_QUOTE_PUNCTUATION;
    }
    @Terminal(expression =  "\\{Nl\\}|\\{IsNl\\}")
    protected int letterNumber()
    {
        return Character.LETTER_NUMBER;
    }
    @Terminal(expression =  "\\{Zl\\}|\\{IsZl\\}")
    protected int lineSeparator()
    {
        return Character.LINE_SEPARATOR;
    }
    @Terminal(expression =  "\\{Ll\\}|\\{IsLl\\}")
    protected int lowercaseLetter()
    {
        return Character.LOWERCASE_LETTER;
    }
    @Terminal(expression =  "\\{Sm\\}|\\{IsSm\\}")
    protected int mathSymbol()
    {
        return Character.MATH_SYMBOL;
    }
    @Terminal(expression =  "\\{Lm\\}|\\{IsLm\\}")
    protected int modifierLetter()
    {
        return Character.MODIFIER_LETTER;
    }
    @Terminal(expression =  "\\{Sk\\}|\\{IsSk\\}")
    protected int modifierSymbol()
    {
        return Character.MODIFIER_SYMBOL;
    }
    @Terminal(expression =  "\\{Mn\\}|\\{IsMn\\}")
    protected int nonSpacingMark()
    {
        return Character.NON_SPACING_MARK;
    }
    @Terminal(expression =  "\\{Lo\\}|\\{IsLo\\}")
    protected int otherLetter()
    {
        return Character.OTHER_LETTER;
    }
    @Terminal(expression =  "\\{No\\}|\\{IsNo\\}")
    protected int otherNumber()
    {
        return Character.OTHER_NUMBER;
    }
    @Terminal(expression =  "\\{Po\\}|\\{IsPo\\}")
    protected int otherPunctuation()
    {
        return Character.OTHER_PUNCTUATION;
    }
    @Terminal(expression =  "\\{So\\}|\\{IsSo\\}")
    protected int otherSymbol()
    {
        return Character.OTHER_SYMBOL;
    }
    @Terminal(expression =  "\\{Zp\\}|\\{IsZp\\}")
    protected int paragraphSeparator()
    {
        return Character.PARAGRAPH_SEPARATOR;
    }
    @Terminal(expression =  "\\{Co\\}|\\{IsCo\\}")
    protected int privateUse()
    {
        return Character.PRIVATE_USE;
    }
    @Terminal(expression =  "\\{Zs\\}|\\{IsZs\\}")
    protected int spaceSeparator()
    {
        return Character.SPACE_SEPARATOR;
    }
    @Terminal(expression =  "\\{Ps\\}|\\{IsPs\\}")
    protected int startPunctuation()
    {
        return Character.START_PUNCTUATION;
    }
    @Terminal(expression =  "\\{Cs\\}|\\{IsCs\\}")
    protected int surrogate()
    {
        return Character.SURROGATE;
    }
    @Terminal(expression =  "\\{Lt\\}|\\{IsLt\\}")
    protected int titleCaseLetter()
    {
        return Character.TITLECASE_LETTER;
    }
    @Terminal(expression =  "\\{Cn\\}|\\{IsCn\\}")
    protected int unassigned()
    {
        return Character.UNASSIGNED;
    }
    @Terminal(expression =  "\\{Lu\\}|\\{IsLu\\}")
    protected int uppercaseLetter(@ParserContext("OPTION") Option... options)
    {
        if (Option.supports(options, Option.CASE_INSENSITIVE))
        {
            return lowercaseLetter();
        }
        return Character.UPPERCASE_LETTER;
    }
    @Terminal(expression =  "[a-zA-Z0-9_\\- ]+")
    protected String blockName(String blockName)
    {
        return blockName;
    }
    @Terminal(expression =  "[^"+REGEXCONTROL+"]")
    protected int notRegexControl(char cc)
    {
        return cc;
    }
    @Terminal(expression =  "["+REGEXCONTROL+"]")
    protected int regexControlCharacter(char cc)
    {
        return cc;
    }
    @Terminal(expression = "t")
    protected int tab()
    {
        return '\t';
    }
    @Terminal(expression = "n")
    protected int nl()
    {
        return '\n';
    }
    @Terminal(expression = "r")
    protected int cr()
    {
        return '\r';
    }
    @Terminal(expression = "f")
    protected int ff()
    {
        return '\f';
    }
    @Terminal(expression = "a")
    protected int alert()
    {
        return '\u0007';
    }
    @Terminal(expression = "e")
    protected int esc()
    {
        return '\u001B';
    }
    @Terminal(expression = "0[0-7]|0[0-7]{2}|0[0-3][0-7]{2}")
    protected int octal(String s)
    {
        return Integer.parseInt(s.substring(1), 8);
    }
    @Terminal(expression = "x[0-9a-fA-F]{2}|u[0-9a-fA-F]{4}")
    protected int hex(String s)
    {
        return Integer.parseInt(s.substring(1), 16);
    }
    @Terminal(expression = "x\\{[0-9a-fA-F]+\\}")
    protected int hex2(String s)
    {
        return Integer.parseInt(s.substring(2, s.length()-1), 16);
    }
    @Terminal(expression = "[A-Z\\[\\]\\\\\\^_]")
    protected int control(char cc)
    {
        return cc - 'A' + 1;
    }
    public static class Literal
    {
        private boolean literal = true;

        public boolean isLiteral()
        {
            return literal;
        }

        public void setLiteral(boolean literal)
        {
            this.literal = literal;
        }

    }
}
