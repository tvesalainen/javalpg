/*
 * Copyright (C) 2014 Timo Vesalainen
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

package org.vesalainen.parser;

import static org.vesalainen.parser.ParserFeature.*;
import org.vesalainen.parser.annotation.ParseMethod;
import org.vesalainen.parser.annotation.Rule;

/**
 *
 * @author Timo Vesalainen
 */
public abstract class BaseParser
{

    @ParseMethod(start="Goal", features={UseChecksum})
    public abstract long parse(String txt);
    
    @Rule(left = "Term", value =
    {
        "Factor"
    })
    public long factorTerm(long term)
    {
        System.err.println("factorTerm(" + term);
        return term;
    }

    @Rule(left = "Factor", value =
    {
        "LPAREN", "Expression", "RPAREN"
    })
    public long expressionFactor(long term)
    {
        return term;
    }
    
}
