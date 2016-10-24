/*
 * Copyright (C) 2016 tkv
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

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tkv
 */
public class SimpleBooleanExpressionTest
{
    
    public SimpleBooleanExpressionTest()
    {
    }

    @Test
    public void test1()
    {
        SimpleBooleanExpression sbe = new SimpleBooleanExpression("x+1>y-2");
        sbe.setVariable("x", 2);
        sbe.setVariable("y", 1);
        assertTrue(sbe.getAsBoolean());
        sbe.setVariable("x", -2);
        sbe.setVariable("y", 1);
        assertFalse(sbe.getAsBoolean());
    }
    @Test
    public void test2()
    {
        SimpleBooleanExpression sbe = new SimpleBooleanExpression("1>0||x<0");  // x is not set
        assertTrue(sbe.getAsBoolean());
    }
    @Test
    public void test3()
    {
        SimpleBooleanExpression sbe = new SimpleBooleanExpression("1<0&&x<0");  // x is not set
        assertFalse(sbe.getAsBoolean());
    }
}
