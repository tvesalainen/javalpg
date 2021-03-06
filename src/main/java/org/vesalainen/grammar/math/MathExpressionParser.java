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
package org.vesalainen.grammar.math;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import static org.vesalainen.grammar.math.MathExpressionParserFactory.MathExpressionParserClass;
import static org.vesalainen.parser.ParserFeature.SingleThread;
import org.vesalainen.parser.annotation.GenClassname;
import org.vesalainen.parser.annotation.Terminal;
import org.vesalainen.parser.annotation.GrammarDef;
import org.vesalainen.parser.annotation.MathExpression;
import org.vesalainen.parser.annotation.ParseMethod;
import org.vesalainen.parser.annotation.ParserContext;
import org.vesalainen.parser.annotation.Rule;
import org.vesalainen.regex.Regex;

/**
 * MathExpressionParser parses math expression written in java style with some 
 * exceptions. Expressions are number or boolean valued. Most java.lang.Math
 * methods can be used in expression.
 * <p>Example:
 * <code>1.2 * cos(x1)</code>
 * <p>Variable names follow java identifier ruling. Variables starting with '$'
 * are reserved.
 * <p>Boolean valued example:
 * <code>x > y && sin(z) > 0</code>
 * @author Timo Vesalainen <timo.vesalainen@iki.fi>
 * @param <T> Type type
 * @param <M> Method type
 * @param <F> Field type
 * @param <P> Parameter type
 */
@GenClassname(MathExpressionParserClass)
@GrammarDef()
@Terminal(left="SQUARE", expression="[\u00b2\u2072]")
@Terminal(left="CUBE", expression="[\u00b3\u2073]")
@Terminal(left="PI", expression="\u03c0")
@Terminal(left="SQRT", expression="\u221a")
@Terminal(left="CBRT", expression="\u2218")
@Terminal(left="PLUS", expression="\\+")
@Terminal(left="MINUS", expression="\\-")
@Terminal(left="STAR", expression="\\*")
@Terminal(left="SLASH", expression="/")
@Terminal(left="PERCENT", expression="%")
@Terminal(left="EXP", expression="\\^")
@Terminal(left="COMMA", expression="\\,")
@Terminal(left="PIPE", expression="\\|")
@Terminal(left="EXCL", expression="!")
@Terminal(left="LBRACKET", expression="\\[")
@Terminal(left="RBRACKET", expression="\\]")
@Terminal(left="LPAREN", expression="\\(")
@Terminal(left="RPAREN", expression="\\)")
@Terminal(left="AND", expression="&&")
@Terminal(left="OR", expression="\\|\\|")
@Terminal(left="NOT", expression="!")
@Terminal(left="EQ", expression="==?")
@Terminal(left="NE", expression="!=")
@Terminal(left="LT", expression="<")
@Terminal(left="LE", expression="<=")
@Terminal(left="GT", expression=">")
@Terminal(left="GE", expression=">=")
public abstract class MathExpressionParser<T,M,F,P> implements MathExpressionParserIntf<T,M,F,P>
{
    /**
     * Parse and execute MathExpression
     * @param me
     * @param handler 
     */
    @Override
    public void parse(MathExpression me, ExpressionHandler<T,M,F,P> handler)
    {
        Set<String> variables = new HashSet<>();
        DEH expression = doParse(me.value(), me.degrees(), handler, variables);
        try
        {
            expression.execute(handler);
        }
        catch (Throwable ex)
        {
            throw new RuntimeException(ex);
        }
    }
    /**
     * Parse and return expression.
     * @param expression
     * @param degrees
     * @param handler
     * @return
     * @throws Exception 
     */
    @Override
    public DEH parse(String expression, boolean degrees, ExpressionHandler<T,M,F,P> handler) throws Exception
    {
        Set<String> variables = new HashSet<>();
        DEH deh = doParse(expression, degrees, handler, variables);
        deh.setVariables(variables);
        return deh;
    }
    @Override
    public DEH parseBoolean(String expression, boolean degrees, ExpressionHandler<T,M,F,P> handler) throws Exception
    {
        Set<String> variables = new HashSet<>();
        DEH deh = doParseBoolean(expression, degrees, handler, variables);
        deh.setVariables(variables);
        return deh;
    }
    
    @ParseMethod(start="expression",  whiteSpace={"whiteSpace", "doubleSlashComment", "hashComment", "cComment"}, features={SingleThread})
    protected abstract DEH doParse(
            String expression, 
            @ParserContext("degrees") boolean degrees,
            @ParserContext("handler") ExpressionHandler<T,M,F,P> handler,
            @ParserContext("variables") Set<String> variables
            );
    
    @ParseMethod(start="conditionalExpression",  whiteSpace={"whiteSpace", "doubleSlashComment", "hashComment", "cComment"}, features={SingleThread})
    protected abstract DEH doParseBoolean(
            String expression, 
            @ParserContext("degrees") boolean degrees,
            @ParserContext("handler") ExpressionHandler<T,M,F,P> handler,
            @ParserContext("variables") Set<String> variables
            );
    
    @Rule("term")
    protected DEH expression(DEH term)
    {
        return term;
    }
    @Rule("factor")
    protected DEH term(DEH factor)
    {
        return factor;
    }
    @Rule("atom")
    protected DEH factor(DEH atom)
    {
        return atom;
    }
    @Rule({"LPAREN", "expression", "RPAREN"})
    protected DEH atom(DEH expression)
    {
        return expression;
    }
    @Rule
    protected List<DEH> expressionList()
    {
        return new ArrayList<>();
    }
    @Rule("expression")
    protected List<DEH> expressionList(DEH expression)
    {
        ArrayList<DEH> list = new ArrayList<>();
        list.add(expression);
        return list;
    }
    @Rule({"expressionList", "COMMA", "expression"})
    protected List<DEH> expressionList(List<DEH> list, DEH expression)
    {
        list.add(expression);
        return list;
    }
    @Rule(left="expression", value={"expression", "PLUS", "term"})
    protected DEH add(DEH expression, DEH term) throws Exception
    {
        expression.append(term);
        expression.getProxy().add();
        return expression;
    }
    @Rule(left="expression", value={"expression", "MINUS", "term"})
    protected DEH subtract(DEH expression, DEH term) throws Exception
    {
        expression.append(term);
        expression.getProxy().subtract();
        return expression;
    }
    @Rule(left="term", value={"term", "STAR", "factor"})
    protected DEH mul(DEH term, DEH factor) throws Exception
    {
        term.append(factor);
        term.getProxy().mul();
        return term;
    }
    @Rule(left="term", value={"term", "SLASH", "factor"})
    protected DEH div(DEH term, DEH factor) throws Exception
    {
        term.append(factor);
        term.getProxy().div();
        return term;
    }
    @Rule(left="term", value={"term", "PERCENT", "factor"})
    protected DEH mod(DEH term, DEH factor) throws Exception
    {
        term.append(factor);
        term.getProxy().mod();
        return term;
    }
    @Rule(left="atom", value={"number"})
    protected DEH num(String number) throws Exception
    {
        DEH atom = new DEH();
        atom.getProxy().number(number);
        return atom;
    }
    @Rule(left="atom", value={"PIPE", "expression", "PIPE"})
    protected DEH abs(
            DEH expression, 
            @ParserContext("degrees") boolean degrees,
            @ParserContext("handler") ExpressionHandler<T,M,F,P> handler
            ) throws Exception
    {
        List<DEH> args = new ArrayList<>();
        args.add(expression);
        return func("abs", args, degrees, handler);
    }
    @Rule(left="factor", value={"atom", "EXP", "factor"})
    protected DEH power(
            DEH atom, 
            DEH factor, 
            @ParserContext("degrees") boolean degrees,
            @ParserContext("handler") ExpressionHandler<T,M,F,P> handler
            ) throws Exception
    {
        List<DEH> args = new ArrayList<>();
        args.add(atom);
        args.add(factor);
        return func("pow", args, degrees, handler);
    }
    @Rule(left="atom", value={"atom", "EXCL"})
    protected DEH factorial(
            DEH atom, 
            @ParserContext("degrees") boolean degrees,
            @ParserContext("handler") ExpressionHandler<T,M,F,P> handler
            ) throws Exception
    {
        List<DEH> args = new ArrayList<>();
        args.add(atom);
        return func("factorial", args, degrees, handler);
    }
    @Rule(left="atom", value={"atom", "SQUARE"})
    protected DEH square(DEH atom) throws Exception
    {
        atom.getProxy().pow(2);
        return atom;
    }
    @Rule(left="atom", value={"atom", "CUBE"})
    protected DEH cube(DEH atom) throws Exception
    {
        atom.getProxy().pow(3);
        return atom;
    }
    @Rule(left="factor", value={"SQRT", "atom"})
    protected DEH sqrt(
            DEH atom,
            @ParserContext("handler") ExpressionHandler<T,M,F,P> handler
            ) throws Exception
    {
        List<DEH> args = new ArrayList<>();
        args.add(atom);
        return func("sqrt", args, false, handler);
    }
    @Rule(left="factor", value={"CBRT", "atom"})
    protected DEH cbrt(
            DEH atom,
            @ParserContext("handler") ExpressionHandler<T,M,F,P> handler
            ) throws Exception
    {
        List<DEH> args = new ArrayList<>();
        args.add(atom);
        return func("cbrt", args, false, handler);
    }
    @Rule(left="atom", value={"PI"})
    protected DEH pi(@ParserContext("handler") ExpressionHandler<T,M,F,P> handler) throws Exception
    {
        DEH atom = new DEH();
        atom.getProxy().loadField(handler.getField(Math.class, "PI"));
        return atom;
    }
    @Rule(left="neg")
    protected boolean none()
    {
        return false;
    }
    @Rule(left="neg", value="MINUS")
    protected boolean minus()
    {
        return true;
    }
    @Rule
    protected List<DEH> indexList() throws Exception
    {
        return new ArrayList<>();
    }
    @Rule({"indexList", "LBRACKET", "expression", "RBRACKET"})
    protected List<DEH> indexList(List<DEH> list, DEH expression) throws Exception
    {
        list.add(expression);
        return list;
    }
    @Rule(left="atom", value={"neg", "identifier", "indexList"})
    protected DEH variable(boolean neg, String identifier, List<DEH> indexList, @ParserContext("variables") Set<String> variables) throws Exception
    {
        variables.add(identifier);
        DEH atom = new DEH();
        ExpressionHandler proxy = atom.getProxy();
        proxy.loadVariable(identifier);
        if (indexList != null && !indexList.isEmpty())
        {
            Iterator<DEH> iterator = indexList.iterator();
            while (iterator.hasNext())
            {
                DEH expr = iterator.next();
                proxy.setIndex(true);
                atom.append(expr);
                proxy.setIndex(false);
                if (iterator.hasNext())
                {
                    proxy.loadArray();
                }
                else
                {
                    proxy.loadArrayItem();
                }
            }
        }
        if (neg)
        {
            proxy.neg();
        }
        return atom;
    }
    @Rule(left="atom", value={"identifier", "LPAREN", "expressionList", "RPAREN"})
    protected DEH func(
            String identifier, 
            List<DEH> funcArgs, 
            @ParserContext("degrees") boolean degrees,
            @ParserContext("handler") ExpressionHandler<T,M,F,P> handler
            ) throws Exception
    {
        DEH atom = new DEH();
        ExpressionHandler proxy = atom.getProxy();
        M method = handler.findMethod(identifier, funcArgs.size());
        List<? extends P> parameters = handler.getParameters(method);
        assert funcArgs.size() == parameters.size();
        int index = 0;
        for (DEH expr : funcArgs)
        {
            atom.append(expr);
            proxy.convertTo(handler.asType(parameters.get(index++)));
            if (degrees && handler.isRadianArgs(method))
            {
                proxy.invoke(handler.getMethod(Math.class, "toRadians", double.class));
            }
        }
        proxy.invoke(method);
        proxy.convertFrom(handler.getReturnType(method));
        if (degrees && handler.isRadianReturn(method))
        {
            proxy.invoke(handler.getMethod(Math.class, "toDegrees", double.class));
        }
        return atom;
    }
    
    // Conditional

    @Rule("andExpression")
    protected DEH conditionalExpression(DEH andExpression)
    {
        return andExpression;
    }
    @Rule(value={"conditionalExpression", "OR", "andExpression"})
    protected DEH conditionalExpression(DEH conditionalExpression, DEH andExpression) throws Exception
    {
        conditionalExpression.getProxy().checkOr();
        conditionalExpression.append(andExpression);
        conditionalExpression.getProxy().or();
        return conditionalExpression;
    }
    @Rule("orExpression")
    protected DEH andExpression(DEH orExpression)
    {
        return orExpression;
    }
    @Rule(value={"andExpression", "AND", "orExpression"})
    protected DEH andExpression(DEH andExpression, DEH orExpression) throws Exception
    {
        andExpression.getProxy().checkAnd();
        andExpression.append(orExpression);
        andExpression.getProxy().and();
        return andExpression;
    }
    @Rule("conditionalAtom")
    protected DEH orExpression(DEH conditionalAtom)
    {
        return conditionalAtom;
    }
    @Rule(left="conditionalAtom", value={"LPAREN", "conditionalExpression", "RPAREN"})
    protected DEH parenConditional(DEH conditionalExpression)
    {
        return conditionalExpression;
    }
    @Rule(left="conditionalAtom", value={"NOT", "conditionalAtom"})
    protected DEH notConditional(DEH conditionalExpression) throws Exception
    {
        conditionalExpression.getProxy().not();
        return conditionalExpression;
    }
    @Rule(left="conditionalAtom", value={"expression", "EQ", "expression"})
    protected DEH eqConditional(DEH exp1, DEH exp2) throws Exception
    {
        exp1.append(exp2);
        exp1.getProxy().eq();
        return exp1;
    }
    @Rule(left="conditionalAtom", value={"expression", "NE", "expression"})
    protected DEH neConditional(DEH exp1, DEH exp2) throws Exception
    {
        exp1.append(exp2);
        exp1.getProxy().ne();
        return exp1;
    }
    @Rule(left="conditionalAtom", value={"expression", "LT", "expression"})
    protected DEH ltConditional(DEH exp1, DEH exp2) throws Exception
    {
        exp1.append(exp2);
        exp1.getProxy().lt();
        return exp1;
    }
    @Rule(left="conditionalAtom", value={"expression", "LE", "expression"})
    protected DEH leConditional(DEH exp1, DEH exp2) throws Exception
    {
        exp1.append(exp2);
        exp1.getProxy().le();
        return exp1;
    }
    @Rule(left="conditionalAtom", value={"expression", "GT", "expression"})
    protected DEH gtConditional(DEH exp1, DEH exp2) throws Exception
    {
        exp1.append(exp2);
        exp1.getProxy().gt();
        return exp1;
    }
    @Rule(left="conditionalAtom", value={"expression", "GE", "expression"})
    protected DEH geConditional(DEH exp1, DEH exp2) throws Exception
    {
        exp1.append(exp2);
        exp1.getProxy().ge();
        return exp1;
    }
    // -------------------
    @Terminal(expression="[a-zA-Z\\$_][a-zA-Z0-9\\$_]*")
    protected abstract String identifier(String value);

    @Terminal(expression="(0x|0b)?[\\+\\-]?[0-9]+")
    protected abstract String integer(String value);

    @Terminal(expression="(0x|0b)?[\\+\\-]?[0-9]+(\\.[0-9]+)?([eE][\\+\\-]?[0-9]+)?")
    protected abstract String number(String value);

    @Terminal(expression="[ \t\r\n]+")
    protected abstract void whiteSpace();
    
    @Terminal(expression = "\\-\\-[^\n]*\n")
    protected abstract void doubleSlashComment();

    @Terminal(expression = "#[^\n]*\n")
    protected abstract void hashComment();

    @Terminal(expression = "/\\*.*\\*/", options = {Regex.Option.FIXED_ENDER})
    protected abstract void cComment();

}
