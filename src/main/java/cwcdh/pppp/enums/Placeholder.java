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
package cwcdh.pppp.enums;

/**
 *
 * @author buddhika
 */
public enum Placeholder {
    Short_Name,
    Full_Name,
    Summary_Top,
    Summery_Bottom,
    P4PPP,
    Image,
    Abbreviation,
    NameInOtherLanguages,
    Facebook_Link,
    Twitter_Link,
    General,
    Implementation,
    Functions,
    Technology,
    Evaluation,
    Scoring;

    public String getString() {
        switch (this) {
            case General:
                return "Tab 1";
            case Implementation:
                return "Tab 2";
            case Functions:
                return "Tab 3";
            case Technology:
                return "Tab 4";
            case Evaluation:
                return "Tab 5";
            default:
                return this.getString();
        }
    }

}
