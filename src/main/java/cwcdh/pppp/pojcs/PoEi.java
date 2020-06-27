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


import cwcdh.pppp.entity.EvaluationItem;
import cwcdh.pppp.entity.SolutionEvaluationItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ruhunudump
 */
public class PoEi {
    private List<PoItem>  poItems;    
    private Map<Long,PoEi> subEis;
    private List<PoEi> subEisList;
    private double score;
    private double weight;
    private boolean parent;
    private boolean child;
    private EvaluationItem evaluationItem;
    private SolutionEvaluationItem solutionEvaluationItem;
            

    public List<PoItem> getPoItems() {
        if(poItems==null){
            poItems = new ArrayList<>();
        }
        return poItems;
    }

    public void setPoItems(List<PoItem> items) {
        this.poItems = items;
    }

    public Map<Long,PoEi> getSubEis() {
        if(subEis==null){
            subEis = new HashMap<>();
        }
        return subEis;
    }
    
    

    public void setSubEis(Map<Long,PoEi> subEis) {
        this.subEis = subEis;
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

    public boolean isParent() {
        parent = getSubEis().size()>0;
        return parent;
    }

   

    public boolean isChild() {
        child = evaluationItem!=null && evaluationItem.getParent()!=null;
        return child;
    }

    

    public EvaluationItem getEvaluationItem() {
        return evaluationItem;
    }

    public void setEvaluationItem(EvaluationItem evaluationItem) {
        this.evaluationItem = evaluationItem;
    }

    public SolutionEvaluationItem getSolutionEvaluationItem() {
        return solutionEvaluationItem;
    }

    public void setSolutionEvaluationItem(SolutionEvaluationItem solutionEvaluationItem) {
        this.solutionEvaluationItem = solutionEvaluationItem;
    }

    public List<PoEi> getSubEisList() {
        if(subEisList==null){
            subEisList = new ArrayList<>(subEis.values());
        }
        return subEisList;
    }
    
    public void reloadList(){
        subEisList=null;
        getSubEisList();
    }
    
    
    
}
