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

import cwcdh.pppp.entity.EvaluationSchema;
import cwcdh.pppp.entity.Solution;
import cwcdh.pppp.entity.SolutionEvaluationSchema;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ruhunudump
 */
public class Poe {

    private Solution solution;
    private EvaluationSchema evaluationSchema;
    private SolutionEvaluationSchema solutionEvaluationSchema;
    private Map<Long, Poeg> poegs;
    private List<Poeg> poegsList;
    private Boolean Governance;
         private Boolean Knowledge;
    private Boolean Advocacy;
   
    private Boolean Capacity;
   
    private Boolean Protect;
   
    private Boolean Prevent;
   
    private Boolean Promote;
   
    private Boolean People;
   
    private Boolean Planet;
   
    private Boolean Place;

    public Solution getSolution() {
        return solution;
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
    }

    public EvaluationSchema getEvaluationSchema() {
        return evaluationSchema;
    }

    public void setEvaluationSchema(EvaluationSchema evaluationSchema) {
        this.evaluationSchema = evaluationSchema;
    }

    public Map<Long, Poeg> getPoegs() {
        if (poegs == null) {
            poegs = new HashMap<>();
        }
        return poegs;
    }

    public List<Poeg> getPoegsList() {
        if (poegsList == null) {
            poegsList = new ArrayList<>(poegs.values());
        }
        return poegsList;
    }

    public void reloadList() {
        poegsList = null;
        getPoegsList();
    }

    public SolutionEvaluationSchema getSolutionEvaluationSchema() {
        return solutionEvaluationSchema;
    }

    public void setSolutionEvaluationSchema(SolutionEvaluationSchema solutionEvaluationSchema) {
        this.solutionEvaluationSchema = solutionEvaluationSchema;
    }

    public Boolean getGovernance() {
        return Governance;
    }

    public void setGovernance(Boolean Governance) {
        this.Governance = Governance;
    }

    public Boolean getKnowledge() {
        return Knowledge;
    }

    public void setKnowledge(Boolean Knowledge) {
        this.Knowledge = Knowledge;
    }

    public Boolean getAdvocacy() {
        return Advocacy;
    }

    public void setAdvocacy(Boolean Advocacy) {
        this.Advocacy = Advocacy;
    }

    public Boolean getCapacity() {
        return Capacity;
    }

    public void setCapacity(Boolean Capacity) {
        this.Capacity = Capacity;
    }

    public Boolean getProtect() {
        return Protect;
    }

    public void setProtect(Boolean Protect) {
        this.Protect = Protect;
    }

    public Boolean getPrevent() {
        return Prevent;
    }

    public void setPrevent(Boolean Prevent) {
        this.Prevent = Prevent;
    }

    public Boolean getPromote() {
        return Promote;
    }

    public void setPromote(Boolean Promote) {
        this.Promote = Promote;
    }

    public Boolean getPeople() {
        return People;
    }

    public void setPeople(Boolean People) {
        this.People = People;
    }

    public Boolean getPlanet() {
        return Planet;
    }

    public void setPlanet(Boolean Planet) {
        this.Planet = Planet;
    }

    public Boolean getPlace() {
        return Place;
    }

    public void setPlace(Boolean Place) {
        this.Place = Place;
    }
    
    

}
