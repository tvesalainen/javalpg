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

import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tkv
 */
public class SimpleMathExpressionTest
{

    private static final double Epsilon = 1e-10;
    
    public SimpleMathExpressionTest()
    {
    }

    @Test
    public void test1()
    {
        SimpleMathExpression sme = new SimpleMathExpression("(x+1)*y");
        sme.setVariable("x", 1);
        sme.setVariable("y", 2);
        assertEquals(4, sme.getAsDouble(), Epsilon);
        sme.setVariable("x", 10);
        sme.setVariable("y", 3);
        assertEquals(33, sme.getAsDouble(), Epsilon);
        Set<String> variables = sme.getVariables();
        assertEquals(2, variables.size());
        assertTrue(variables.contains("x"));
        assertTrue(variables.contains("y"));
    }
    @Test
    public void test2()
    {
        SimpleMathExpression sme = new SimpleMathExpression("cos(a)*c");
        sme.setVariable("a", 60);
        sme.setVariable("c", 2);
        assertEquals(1, sme.getAsDouble(), Epsilon);
    }
    
}
