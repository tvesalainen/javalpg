package org.vesalainen.grammar.math;

/*
 * Copyright (C) 2013 Timo Vesalainen
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


import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.vesalainen.bcc.model.El;
import org.vesalainen.parser.GenClassCompiler;
import org.vesalainen.parser.GenClassFactory;

/**
 *
 * @author Timo Vesalainen
 */
public class TestMath
{
    private Example test1;
    public TestMath()
    {
        test1 = (Example) GenClassFactory.getGenInstance(Example.class);
    }
    
    @BeforeClass
    public static void setUpClass()
    {
    }
    
    @AfterClass
    public static void tearDownClass()
    {
    }
    
    @Before
    public void setUp()
    {
    }
    
    @After
    public void tearDown()
    {
    }

    @Test
    public void testPlus1()
    {
        assertEquals(2, test1.plus1(1));
        assertEquals(0, test1.plus1(-1));
        assertEquals(101, test1.plus1(100));
        assertEquals(-1233, test1.plus1(-1234));
    }

    @Test
    public void test1()
    {   // -2*x+3*y
        assertEquals(-2*1+3*1, test1.test1(1, 1));
        assertEquals(-2*2+3*3, test1.test1(2, 3));
    }

    @Test
    public void test2()
    {   // -2*x/y
        assertEquals(-2*1/1, test1.test2(1, 1));
        assertEquals(-2*2/3, test1.test2(2, 3));
        assertEquals(-2*2/-3, test1.test2(2, -3));
    }

    @Test
    public void test3()
    {   // -2*(x-y)
        assertEquals(-2*(1-1), test1.test3(1, 1));
        assertEquals(-2*(2-3), test1.test3(2, 3));
        assertEquals(-2*(2+3), test1.test3(2, -3));
    }

    @Test
    public void test4()
    {   // -2*|x-y|+x
        assertEquals(-2*Math.abs(1-1)+1, test1.test4(1, 1));
        assertEquals(-2*Math.abs(2-3)+2, test1.test4(2, 3));
        assertEquals(-2*Math.abs(2+3)+2, test1.test4(2, -3));
    }

    @Test
    public void test5()
    {   // -2^(x-y)+x
        assertEquals((int)Math.pow(-2, (1-1))+1, test1.test5(1, 1));
        assertEquals((int)Math.pow(-2, (2-3))+2, test1.test5(2, 3));
        assertEquals((int)Math.pow(-2, (2+3))+2, test1.test5(2, -3));
    }

    @Test
    public void test6i()
    {   // -2^(x[0]-y[0])+x[1]
        assertEquals(3, test1.test6(new int[]{1,2}, new int[]{1}));
        assertEquals(3, test1.test6(new int[]{2,3}, new int[]{4}));
        assertEquals(67, test1.test6(new int[]{4,3}, new int[]{-2}));
    }

    @Test
    public void test6l()
    {   // -2^(x[0]-y[0])+x[1]
        assertEquals(3, test1.test6(new long[]{1,2}, new long[]{1}));
        assertEquals(3, test1.test6(new long[]{2,3}, new long[]{4}));
        assertEquals(67, test1.test6(new long[]{4,3}, new long[]{-2}));
    }

    @Test
    public void test6f()
    {   // -2^(x[0]-y[0])+x[1]
        assertEquals(3F, test1.test6(new float[]{1,2}, new float[]{1}), 0.0F);
        assertEquals(3.25F, test1.test6(new float[]{2,3}, new float[]{4}), 0.0F);
        assertEquals(67F, test1.test6(new float[]{4,3}, new float[]{-2}), 0.0F);
    }

    @Test
    public void test6d()
    {   // -2^(x[0]-y[0])+x[1]
        assertEquals(3D, test1.test6(new double[]{1,2}, new double[]{1}), 0.0D);
        assertEquals(3.25D, test1.test6(new double[]{2,3}, new double[]{4}), 0.0D);
        assertEquals(67D, test1.test6(new double[]{4,3}, new double[]{-2}), 0.0D);
    }

    @Test
    public void test7i()
    {   // a[0][0]*a[1][1]-a[0][1]*a[1][0]
        assertEquals(-2, test1.deti(new int[][]{
            new int[]{1,2}, 
            new int[]{3,4}
        }));
    }

    @Test
    public void test7l()
    {   // a[0][0]*a[1][1]-a[0][1]*a[1][0]
        assertEquals(-2, test1.detl(new long[][]{
            new long[]{1,2}, 
            new long[]{3,4}
        }));
    }

    @Test
    public void test7f()
    {   // a[0][0]*a[1][1]-a[0][1]*a[1][0]
        assertEquals(-2F, test1.detf(new float[][]{
            new float[]{1,2}, 
            new float[]{3,4}
        }), 0.0F);
    }

    @Test
    public void test7d()
    {   // a[0][0]*a[1][1]-a[0][1]*a[1][0]
        assertEquals(-2D, test1.detd(new double[][]{
            new double[]{1,2}, 
            new double[]{3,4}
        }), 0.0D);
    }

    @Test
    public void test8()
    {   // sin(a)
        assertEquals(1F, test1.sinDeg(90F), 0.00001F);
        assertEquals(0.5F, test1.sinDeg(30F), 0.00001F);
        assertEquals(0F, test1.sinDeg(180F), 0.00001F);
        assertEquals(-1F, test1.sinDeg(270F), 0.00001F);
        assertEquals(1F, test1.cosDeg(0F), 0.00001F);
        assertEquals(0.5F, test1.cosDeg(60F), 0.00001F);
    }

    @Test
    public void test9i()
    {   // 1+i%j*3
        assertEquals(1+1%2*3, test1.mod1(1,2));
        assertEquals(1+3%2*3, test1.mod1(3,2));
        assertEquals(1+11%3*3, test1.mod1(11,3));
    }

    @Test
    public void test10i()
    {   // x\u00b2 -> square
        assertEquals(2*2, test1.square(2));
        assertEquals(3*3, test1.square(3));
        assertEquals(11*11, test1.square(11));
        assertEquals(-11*-11, test1.square(-11));
    }

    @Test
    public void test10l()
    {   // x\u00b2 -> square
        assertEquals(2*2, test1.square(2L));
        assertEquals(3*3, test1.square(3L));
        assertEquals(11*11, test1.square(11L));
    }

    @Test
    public void test10f()
    {   // x\u00b2 -> square
        assertEquals(2*2, test1.square(2F), 0F);
        assertEquals(3*3, test1.square(3F), 0F);
        assertEquals(11*11, test1.square(11F), 0F);
    }

    @Test
    public void test10d()
    {   // x\u00b2 -> square
        assertEquals(2*2, test1.square(2D), 0D);
        assertEquals(3*3, test1.square(3D), 0D);
        assertEquals(11*11, test1.square(11D), 0D);
    }

    @Test
    public void test11i()
    {   // x\u00b2 -> square
        assertEquals(2*2*2, test1.cube(2));
        assertEquals(3*3*3, test1.cube(3));
        assertEquals(11*11*11, test1.cube(11));
        assertEquals(-11*-11*-11, test1.cube(-11));
    }

    @Test
    public void test12i()
    {   // x*f1
        assertEquals(2*9, test1.field1(2));
        assertEquals(-2*9, test1.field1(-2));
    }

    @Test
    public void test12l()
    {   // x*f1
        assertEquals(2*9, test1.field1(2L));
        assertEquals(-2*9, test1.field1(-2L));
    }

    @Test
    public void test12f()
    {   // x*f1
        assertEquals(2*9, test1.field1(2F), 0F);
        assertEquals(-2*9, test1.field1(-2F), 0F);
    }

    @Test
    public void test12d()
    {   // x*f1
        assertEquals(2*9, test1.field1(2D), 0D);
        assertEquals(-2*9, test1.field1(-2D), 0D);
    }

    @Test
    public void test13i()
    {   // x*F1
        assertEquals(2*9, test1.field2(2));
        assertEquals(-2*9, test1.field2(-2));
    }

    @Test
    public void test14()
    {   // x*PI
        assertEquals(2*Math.PI, test1.pi(2), 0D);
        assertEquals(-2*Math.PI, test1.pi(-2), 0D);
    }

    @Test
    public void test15()
    {   // \u221ax
        assertEquals(Math.sqrt(2), test1.sqrt1(2), 0D);
        assertEquals(Math.sqrt(4), test1.sqrt1(4), 0D);
    }

    @Test
    public void test16()
    {   // \u2218(x+1)
        assertEquals(Math.cbrt(2+1), test1.cbrt1(2), 0D);
        assertEquals(Math.cbrt(4+1), test1.cbrt1(4), 0D);
    }

}
