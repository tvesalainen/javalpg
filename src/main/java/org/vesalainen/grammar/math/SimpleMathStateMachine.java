/*
 * Copyright (C) 2016 Timo Vesalainen <timo.vesalainen@iki.fi>
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

import java.time.Clock;
import java.util.Set;
import java.util.function.Supplier;
import org.vesalainen.util.DoubleMap;

/**
 * SimpleMathStateMachine implements variables by using DoubleMap.
 * @author Timo Vesalainen <timo.vesalainen@iki.fi>
 * @see org.vesalainen.util.DoubleMap
 */
public class SimpleMathStateMachine extends AbstractMathStateMachine
{
    private DoubleMap<String> map = new DoubleMap<>();

    public SimpleMathStateMachine(String start)
    {
        super(start);
    }

    public SimpleMathStateMachine(String start, boolean useDegrees)
    {
        super(start, useDegrees);
    }

    public SimpleMathStateMachine(String start, Supplier<Clock> clockSupplier, boolean useDegrees)
    {
        super(start, clockSupplier, useDegrees);
    }
    /**
     * Assign value to named variable.
     * @param identifier
     * @param value 
     */
    public void setVariable(String identifier, double value)
    {
        map.put(identifier, value);
    }
    
    @Override
    protected double getVariable(String identifier)
    {
        return map.getDouble(identifier);
    }

    @Override
    protected void register(Set<String> variables)
    {
        for (String v : variables)
        {
            if (!map.containsKey(v) && !v.startsWith("$"))
            {
                throw new IllegalArgumentException("variable "+v+" not set");
            }
        }
    }

    @Override
    protected void unregister(Set<String> variables)
    {
    }
    
}
