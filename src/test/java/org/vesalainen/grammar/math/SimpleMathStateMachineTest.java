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
public class SimpleMathStateMachineTest
{
    
    public SimpleMathStateMachineTest()
    {
    }

    @Test
    public void test1() throws Exception
    {
        SimpleMathStateMachine smsm = new SimpleMathStateMachine("bulk");
        final StringBuilder state = new StringBuilder();
        
        smsm.addState("bulk", ()->{state.setLength(0);state.append("bulk");});
        smsm.addState("abs", ()->{state.setLength(0);state.append("abs");});
        smsm.addState("float", ()->{state.setLength(0);state.append("float");});
        
        smsm.addTransition("bulk", "v >= 14.1", "abs");
        smsm.addTransition("abs", "c < 1", "float");
        smsm.addTransition("float", "v < 13", "bulk");
        
        smsm.setVariable("v", 12.5);
        smsm.setVariable("c", 10);
        smsm.evaluate();
        assertEquals("bulk", state.toString());
        
        smsm.setVariable("v", 13.5);
        smsm.setVariable("c", 5);
        smsm.evaluate();
        assertEquals("bulk", state.toString());
        
        smsm.setVariable("v", 14.11);
        smsm.setVariable("c", 3);
        smsm.evaluate();
        assertEquals("abs", state.toString());
        
        smsm.setVariable("v", 14.1);
        smsm.setVariable("c", 0.5);
        smsm.evaluate();
        assertEquals("float", state.toString());
        
        smsm.setVariable("v", 12.5);
        smsm.setVariable("c", 10);
        smsm.evaluate();
        assertEquals("bulk", state.toString());
    }
    
}
