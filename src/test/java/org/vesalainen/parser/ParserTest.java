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

import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Timo Vesalainen
 */
public class ParserTest
{
    public ParserTest()
    {
    }

    @Test
    public void test1() 
    {
        Parser parser = Parser.getInstance();
        assertNotNull(parser);
        String expr = "1+1";
        CRC32 crc = new CRC32();
        crc.update(expr.getBytes(StandardCharsets.US_ASCII));
        long expCRC = crc.getValue();
        crc.reset();
        parser.setChecksum(crc);
        assertEquals(2, parser.parse(expr));
        assertEquals(expCRC, parser.getChecksum().getValue());
        System.err.println("White-space terminals\n" +
        "can have reducer. Reducers are called when white-space input is read. If such \n" +
        "reducer returns value, that value is inserted in input. Return type must match\n" +
        "one of InputReader.input method parameter.");
        assertEquals(3*255, parser.parseExt("0b11 * 0xff"));
    }
}
