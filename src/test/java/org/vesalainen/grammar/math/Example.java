/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor. 
 */

package org.vesalainen.grammar.math;

import org.vesalainen.parser.annotation.GenClassname;
import org.vesalainen.parser.annotation.GrammarDef;
import org.vesalainen.parser.annotation.MathExpression;

/**
 * @author Timo Vesalainen
 */
@GenClassname("org.vesalainen.grammar.math.ExampleImpl")
public class Example 
{
    @MathExpression("i+1")
    public int plus1(int i)
    {
        throw new UnsupportedOperationException("not supported");
    }
    @MathExpression("-2*x+3*y")
    public int test1(int x, int y)
    {
        throw new UnsupportedOperationException("not supported");
    }
    @MathExpression("-2*x/y")
    public int test2(int x, int y)
    {
        throw new UnsupportedOperationException("not supported");
    }
    @MathExpression("-2*(x-y)")
    public int test3(int x, int y)
    {
        throw new UnsupportedOperationException("not supported");
    }
    @MathExpression("-2*|x-y|+x")
    public int test4(int x, int y)
    {
        throw new UnsupportedOperationException("not supported");
    }
    @MathExpression("-2^(x-y)+x")
    public int test5(int x, int y)
    {
        throw new UnsupportedOperationException("not supported");
    }
    @MathExpression("-2^(x[0]-y[0])+x[1]")
    public int test6(int[] x, int[] y)
    {
        throw new UnsupportedOperationException("not supported");
    }
    @MathExpression("-2^(x[0]-y[0])+x[1]")
    public long test6(long[] x, long[] y)
    {
        throw new UnsupportedOperationException("not supported");
    }
    @MathExpression("-2^(x[0]-y[0])+x[1]")
    public float test6(float[] x, float[] y)
    {
        throw new UnsupportedOperationException("not supported");
    }
    @MathExpression("-2^(x[0]-y[0])+x[1]")
    public double test6(double[] x, double[] y)
    {
        throw new UnsupportedOperationException("not supported");
    }
    @MathExpression("a[0][0]*a[1][1]-a[0][1]*a[1][0]")
    public int deti(int[][] a)
    {
        throw new UnsupportedOperationException("not supported");
    }
    @MathExpression("a[0][0]*a[1][1]-a[0][1]*a[1][0]")
    public long detl(long[][] a)
    {
        throw new UnsupportedOperationException("not supported");
    }
    @MathExpression("a[0][0]*a[1][1]-a[0][1]*a[1][0]")
    public float detf(float[][] a)
    {
        throw new UnsupportedOperationException("not supported");
    }
    @MathExpression("a[0][0]*a[1][1]-a[0][1]*a[1][0]")
    public double detd(double[][] a)
    {
        throw new UnsupportedOperationException("not supported");
    }
    @MathExpression(value="sin(a)", degrees=true)
    public float sinDeg(float a)
    {
        throw new UnsupportedOperationException("not supported");
    }
    @MathExpression(value="cos(a)", degrees=true)
    public float cosDeg(float a)
    {
        throw new UnsupportedOperationException("not supported");
    }
    @MathExpression("1+i%j*3")
    public int mod1(int i, int j)
    {
        throw new UnsupportedOperationException("not supported");
    }
    @MathExpression("x\u00b2")
    public int square(int x)
    {
        throw new UnsupportedOperationException("not supported");
    }
    @MathExpression("x\u00b2")
    public long square(long x)
    {
        throw new UnsupportedOperationException("not supported");
    }
    @MathExpression("x\u00b2")
    public float square(float x)
    {
        throw new UnsupportedOperationException("not supported");
    }
    @MathExpression("x\u00b2")
    public double square(double x)
    {
        throw new UnsupportedOperationException("not supported");
    }
    @MathExpression("x\u00b3")
    public int cube(int x)
    {
        throw new UnsupportedOperationException("not supported");
    }
    protected int f1 = 9;
    @MathExpression("x*f1")
    public int field1(int x)
    {
        throw new UnsupportedOperationException("not supported");
    }
    @MathExpression("x*f1")
    public long field1(long x)
    {
        throw new UnsupportedOperationException("not supported");
    }
    @MathExpression("x*f1")
    public float field1(float x)
    {
        throw new UnsupportedOperationException("not supported");
    }
    @MathExpression("x*f1")
    public double field1(double x)
    {
        throw new UnsupportedOperationException("not supported");
    }
    protected static int F1 = 9;
    @MathExpression("x*F1")
    public int field2(int x)
    {
        throw new UnsupportedOperationException("not supported");
    }
    @MathExpression("x*\u03c0")
    public double pi(double x)
    {
        throw new UnsupportedOperationException("not supported");
    }
    @MathExpression("\u221ax")
    public double sqrt1(double x)
    {
        throw new UnsupportedOperationException("not supported");
    }
    @MathExpression("\u2218(x+1)")
    public double cbrt1(double x)
    {
        throw new UnsupportedOperationException("not supported");
    }
}
