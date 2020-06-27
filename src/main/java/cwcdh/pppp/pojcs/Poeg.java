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

import cwcdh.pppp.entity.EvaluationGroup;
import cwcdh.pppp.entity.SolutionEvaluationGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ruhunudump
 */
public class Poeg {
    private Map<Long,PoEi> poeis;
    private List<PoEi> poeisList;
    private SolutionEvaluationGroup solutionEvaluationGroup;
    private EvaluationGroup evaluationGroup;

    public Map<Long,PoEi> getPoeis() {
        if(poeis==null){
            poeis = new HashMap<>();
        }
        return poeis;
    }
    
    

    public SolutionEvaluationGroup getSolutionEvaluationGroup() {
        return solutionEvaluationGroup;
    }

    public void setSolutionEvaluationGroup(SolutionEvaluationGroup solutionEvaluationGroup) {
        this.solutionEvaluationGroup = solutionEvaluationGroup;
    }

    public EvaluationGroup getEvaluationGroup() {
        return evaluationGroup;
    }

    public void setEvaluationGroup(EvaluationGroup evaluationGroup) {
        this.evaluationGroup = evaluationGroup;
    }

    public List<PoEi> getPoeisList() {
        if(poeisList==null){
            poeisList = new ArrayList<>(getPoeis().values());
        }
        return poeisList;
    }

    public void reloadLists(){
        poeisList = null;
        getPoeisList();
    }
    
    
}
