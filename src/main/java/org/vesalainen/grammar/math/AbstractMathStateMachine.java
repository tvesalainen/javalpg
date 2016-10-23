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
import java.time.Clock;
import java.util.HashSet;
import java.util.Set;
import org.vesalainen.util.AbstractStateMachine;
import org.vesalainen.util.AbstractStateMachine.State;

/**
 * AbstractMathStateMachine extends AbstractStateMachine by using boolean
 * math expressions as conditions.
 * @author tkv
 */
public abstract class AbstractMathStateMachine extends AbstractStateMachine<BooleanMathExpression,State>
{
    private boolean useDegrees;
    private Set<String> variables = new HashSet<>();

    public AbstractMathStateMachine(String start)
    {
        this(start, true);
    }
    
    public AbstractMathStateMachine(String start, boolean useDegrees)
    {
        this(start, Clock.systemDefaultZone(), useDegrees);
    }

    public AbstractMathStateMachine(String start, Clock clock, boolean useDegrees)
    {
        super(start, clock);
        this.useDegrees = useDegrees;
    }

    public void addTransition(String from, String condition, String to)
    {
        Expression expression = new Expression(condition, useDegrees);
        super.addTransition(from, expression, to);
    }
    /**
     * Returns a set of variables used in current state.
     * @return 
     */
    public Set<String> getCurrentVariables()
    {
        variables.clear();
        getCurrentConditions().stream().forEach((bme) ->
        {
            variables.addAll(bme.getVariables());
        });
        return variables;
    }
    /**
     * Returns the current value of given variable.
     * @param identifier
     * @return
     * @throws IOException 
     */
    protected abstract double getVariable(String identifier) throws IOException;
 
    private class Expression extends BooleanMathExpression
    {

        public Expression(String expression, boolean degrees)
        {
            super(expression, degrees);
        }

        @Override
        protected double getVariable(String identifier) throws IOException
        {
            return AbstractMathStateMachine.this.getVariable(identifier);
        }
        
    }
}
