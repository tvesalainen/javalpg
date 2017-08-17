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
package org.vesalainen.grammar.impl;

/**
 * 
 * @author Timo Vesalainen <timo.vesalainen@iki.fi>
 * @see <a href="https://tools.ietf.org/html/rfc5234#appendix-B">Appendix B.  Core ABNF of ABNF</a>
 */
public class ABNFCore
{
    public static final String ALPHA = "A-Za-z";
    public static final String BIT = "01";
    public static final String CHAR = "\\x01-\\x7F";
    public static final String CR = "\\x0D";
    public static final String CRLF = "\\x0D\\x0A";
    public static final String CTL = "\\x00-\\x1F\\x7F";
    public static final String DIGIT = "0-9";
    public static final String DQUOTE = "\\x22";
    public static final String HEXDIGIT = "0-9a-fA-F";
    public static final String HTAB = "\\x09";
    public static final String LF = "\\x0A";
    public static final String OCTET = "\\x00-\\xFF";
    public static final String SP = "\\x20";
    public static final String VCHAR = "\\x21-\\x7E";
    public static final String WSP = "\\x20\\x0A";
}
