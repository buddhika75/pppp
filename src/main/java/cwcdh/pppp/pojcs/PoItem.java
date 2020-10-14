/*
 * The MIT License
 *
 * Copyright 2020 ruhunudump.
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

import cwcdh.pppp.entity.SolutionItem;

/**
 *
 * @author ruhunudump
 */
public class PoItem implements Comparable<PoItem>{
    private SolutionItem solutionItem;
    private double score;
    private double weight;
    private PoEi poei;

    public SolutionItem getSolutionItem() {
        return solutionItem;
    }

    public void setSolutionItem(SolutionItem solutionItem) {
        this.solutionItem = solutionItem;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public PoEi getPoei() {
        return poei;
    }

    public void setPoei(PoEi poei) {
        this.poei = poei;
    }

    @Override
    public int compareTo(PoItem o) {
    
    if (o == null) {
            return 0;
        }
        if (this.getSolutionItem() == null && o.getSolutionItem() == null) {
            return 0;
        } else if (o.getSolutionItem() == null) {
            return -1;
        } else if (this.getSolutionItem() == null) {
            return 1;
        }
        if(this.getSolutionItem().getOrderNo()==null){
            this.getSolutionItem().setOrderNo(Double.valueOf(this.getSolutionItem().getId()));
        }
        if(o.getSolutionItem().getOrderNo()==null){
            o.getSolutionItem().setOrderNo(Double.valueOf(o.getSolutionItem().getId()));
        }
        if(this.getSolutionItem().getOrderNo()==null && o.getSolutionItem().getOrderNo()==null){
            return 0;
        }else if(o.getSolutionItem().getOrderNo()==null){
            return -1;
        }else if(this.getSolutionItem().getOrderNo()==null){
            return 1;
        }
        if(this.getSolutionItem().getOrderNo() > o.getSolutionItem().getOrderNo()){
            return 1;
        }else if(this.getSolutionItem().getOrderNo() < o.getSolutionItem().getOrderNo()){
            return -1;
        }else{
            return 0;
        }
    
    }
    
    
    
    
}
