/*
 * The MIT License
 *
 * Copyright 2020 buddhika.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package cwcdh.pppp.pojcs;

/**
 *
 * @author buddhika
 */
public enum HtmlComponent {
    h1,
    h2,
    h3,
    h4,
    h5,
    h6,
    label,
    p,
    span,
    ul_opening,
    ul_closing,
    ol_opening,
    ol_closing,
    table_opening,
    table_closing,
    link,
    hr,
    li,
    ampersand,
    line_break,
    comma,
    hd,
    td,
    tr;

    public String getString() {
        switch (this) {
            case h1:
                return "Heading 1";
            case h2:
                return "Heading 2";
            case h3:
                return "Heading 3";
            case h4:
                return "Heading 4";
            case h5:
                return "Heading 5";
            case h6:
                return "Heading 6";
            case hr:
                return "Horizontal Rule";
            case label:
                return "Label";
            case link:
                return "Link";
            case ol_opening:
                return "Ordered List";
            case p:
                return "Paragraphs";
            case span:
                return "Span";
            case ul_opening:
                return "Unordered List";
            case table_opening:
                return "Table";
            case comma:
                return "Comma";
            case ampersand:
                return "Ampersand";
            case line_break:
                return "Linebreak";
        }
        return this.name();
    }

}
