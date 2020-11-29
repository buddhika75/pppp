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

import cwcdh.pppp.enums.Placeholder;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author buddhika
 */
public class Display {

    private List<DisplayPlaceholder> placeholders;

    private Placeholder stringToEnum(String str) {
        try {
            return Placeholder.valueOf(str);
        } catch (Exception e) {
            return null;
        }
    }

    public String getString(String strph) {
        System.out.println("strph = " + strph);
        Placeholder ph = stringToEnum(strph);
        System.out.println("ph = " + ph);
        if (ph == null) {
            return "";
        }
        String str = "";
        for (DisplayPlaceholder dph : getPlaceholders()) {
            Placeholder currentPh = dph.getPlaceholder();
            System.out.println("currentPh = " + currentPh);
            if (currentPh.equals(ph)) {
                System.out.println("Equal");
                for (DisplayItem di : dph.getDisplayItems()) {
                    System.out.println("di.getText() = " + di.getText());
                    str += di.getText();
                }
            }
        }
        return str;
    }

    public String getDisplayItemsAsString(String strph) {
        List<DisplayItem> dis = getDisplayItemsStr(strph);
        String s = "";

        for (DisplayItem d : dis) {

            if (d.getHtmlComponent() == null) {
                s += "<span style=\"" + d.getStyle() + "\">" + d.getText() + "</span>" + "\n";
                continue;
            }

            switch (d.getHtmlComponent()) {
                case h1:
                    s += "<h1 style=\"" + d.getStyle() + "\">" + d.getText() + "</h1>" + "\n";
                    break;
                case h2:
                    s += "<h2 style=\"" + d.getStyle() + "\">" + d.getText() + "</h2>" + "\n";
                    break;
                case h3:
                    s += "<h3 style=\"" + d.getStyle() + "\">" + d.getText() + "</h3>" + "\n";
                    break;
                case h4:
                    s += "<h4 style=\"" + d.getStyle() + "\">" + d.getText() + "</h4>" + "\n";
                    break;
                case h5:
                    s += "<h5 style=\"" + d.getStyle() + "\">" + d.getText() + "</h5>" + "\n";
                    break;
                case h6:
                    s += "<h6 style=\"" + d.getStyle() + "\">" + d.getText() + "</h6>" + "\n";
                    break;
                case p:
                    s += "<p style=\"" + d.getStyle() + "\">" + d.getText() + "</p>" + "\n";
                    break;
                case span:
                    s += "<span style=\"" + d.getStyle() + "\">" + d.getText() + "</span>" + "\n";
                    break;
                case hr:
                    s += "<hr style=\"" + d.getStyle() + "\"></hr>" + "\n";
                    break;
                case label:
                    s += "<span style=\"" + d.getStyle() + "\">" + d.getText() + "</span>" + "\n";
                    break;
                case p_opening:
                    s += "<p style=\"" + d.getStyle() + "\">";
                    break;
                case p_closing:
                    s += "</p>" + "\n";
                    break;
                case span_opening:
                    s += "<span style=\"" + d.getStyle() + "\">";
                    break;
                case span_closing:
                    s += "</span>" + "\n";
                    break;
                case ul_opening:
                    s += "<ul style=\"" + d.getStyle() + "\">";
                    break;
                case ul_closing:
                    s += "</ul>" + "\n";
                    break;
                case ol_opening:
                    s += "<ol style=\"" + d.getStyle() + "\">";
                    break;
                case ol_closing:
                    s += "</ol>" + "\n";
                    break;
                case table_opening:
                    s += "<table style=\"" + d.getStyle() + "\">";
                    break;
                case table_closing:
                    s += "</table>" + "\n";
                    break;
                case ampersand:
                    s += " & " + "\n";
                    break;
                case comma:
                    s += ", " + "\n";
                    break;
                case line_break:
                    s += "<br/>" + "\n";
                default:
                    s += d.getText() + "\n";

            }

        }
        System.out.println("s = " + s);
        return s;
    }

    public List<DisplayItem> getDisplayItemsStr(String strph) {
        Placeholder ph = stringToEnum(strph);
        List<DisplayItem> tdis = new ArrayList<>();
        if (ph == null) {
            return tdis;
        }
        for (DisplayPlaceholder dph : placeholders) {
            if (dph.getPlaceholder().equals(ph)) {
                tdis = dph.getDisplayItems();
            }
        }
        if (tdis == null) {
            tdis = new ArrayList<>();
        }
        return tdis;
    }

    public List<DisplayItem> getDisplayItems(Placeholder ph) {
        List<DisplayItem> tdis = new ArrayList<>();
        if (ph == null) {
            return tdis;
        }
        for (DisplayPlaceholder dph : placeholders) {
            if (dph.getPlaceholder().equals(ph)) {
                tdis = dph.getDisplayItems();
            }
        }
        if (tdis == null) {
            tdis = new ArrayList<>();
        }
        return tdis;
    }

    public Display() {
        placeholders = new ArrayList<>();
        for (Placeholder ph : Placeholder.values()) {
            DisplayPlaceholder dph = new DisplayPlaceholder();
            dph.setPlaceholder(ph);
            placeholders.add(dph);
        }
    }

    public List<DisplayPlaceholder> getPlaceholders() {
        if (placeholders == null) {
            placeholders = new ArrayList<>();
        }
        return placeholders;
    }

    public void setPlaceholders(List<DisplayPlaceholder> placeholders) {
        this.placeholders = placeholders;
    }

}
