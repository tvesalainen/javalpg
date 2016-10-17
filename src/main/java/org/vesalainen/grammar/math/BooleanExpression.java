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

import java.io.IOException;
import java.lang.reflect.Field;

/**
 *
 * @author tkv
 */
public abstract class BooleanExpression extends DoubleMathStack
{
    private String expression;
    private boolean degrees;
    private DEH stack;
    /**
     * Creates DoubleMathExpression for given expression. 
     * 
     * <p>If degrees is true
     * parameters to sin, cos and tan are converted to radians before call. Also
     * return value of asin, acos and atan is converted to degrees.
     * @param expression
     * @param degrees 
     */
    public BooleanExpression(String expression, boolean degrees)
    {
        this.expression = expression;
        this.degrees = degrees;
    }
    
    public boolean calculate()
    {
        if (stack == null)
        {
            MathExpressionParserIntf<Class<?>,String,Field,Class<?>> parser = MathExpressionParserFactory.getInstance();
            try
            {
                stack = parser.parseBoolean(expression, degrees, this);
            }
            catch (IOException ex)
            {
                throw new RuntimeException(ex);
            }
        }
        clear();
        stack.execute(this);
        return pop() == TRUE;
    }
}
