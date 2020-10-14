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

    public String getString(Placeholder ph){
        return "";
    }
    
    
    
    public Display() {
        placeholders = new ArrayList<>();
        for(Placeholder ph:Placeholder.values()){
            DisplayPlaceholder dph = new DisplayPlaceholder();
            dph.setPlaceholder(ph);
            placeholders.add(dph);
        }
    }
    
    

    public List<DisplayPlaceholder> getPlaceholders() {
        if(placeholders==null){
            placeholders = new ArrayList<>();
        }
        return placeholders;
    }

    public void setPlaceholders(List<DisplayPlaceholder> placeholders) {
        this.placeholders = placeholders;
    }
    
    
}
