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
package cwcdh.pppp.entity;

import cwcdh.pppp.enums.DataType;
import cwcdh.pppp.enums.MultipleItemCalculationMethod;
import cwcdh.pppp.enums.RenderType;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;

/**
 *
 * @author ruhunudump
 */
@Entity
public class EvaluationItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String code;
    @ManyToOne(fetch = FetchType.EAGER)
    private Item item;
    @Lob
    private String descreption;
    private Double orderNo;

    @ManyToOne(fetch = FetchType.EAGER)
    private EvaluationSchema evaluationSchema;

    @ManyToOne(fetch = FetchType.EAGER)
    private EvaluationGroup evaluationGroup;
    
    @ManyToOne
    private EvaluationItem parent;

    @Enumerated(EnumType.STRING)
    private RenderType renderType;

    private boolean required;
    private boolean detailItem;
    private boolean scoringItem;

    
    private Double weight;
    private Double score;
    @Enumerated(EnumType.STRING)
    private MultipleItemCalculationMethod multipleItemCalculationMethod;

    private boolean multipleEntiesAllowed;

    
    @Enumerated(EnumType.STRING)
    private DataType dataType;

    @ManyToOne
    private Area parentAreaOfAvailableAreas;
    
    @ManyToOne
    private Item parentOfAvailableItems;
    
    @ManyToOne
    private Institution parentInstitutionOfAvailableInstitutions;

    @Lob
    private String css;

    /*
    Create Properties
     */
    @ManyToOne
    private WebUser createdBy;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date createdAt;
    /*
    Last Edit Properties
     */
    @ManyToOne
    private WebUser lastEditBy;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date lastEditeAt;
    /*
    Retire Reversal Properties
     */
    @ManyToOne
    private WebUser retiredReversedBy;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date retiredReversedAt;
    /*
    Retire Properties
     */
    private boolean retired;
    @ManyToOne
    private WebUser retiredBy;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date retiredAt;
    private String retireComments;


    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EvaluationItem)) {
            return false;
        }
        EvaluationItem other = (EvaluationItem) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cwcdh.pppp.entity.EvaluationItem[ id=" + id + " ]";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getDescreption() {
        return descreption;
    }

    public void setDescreption(String descreption) {
        this.descreption = descreption;
    }

    public Double getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Double orderNo) {
        this.orderNo = orderNo;
    }

    public EvaluationSchema getEvaluationSchema() {
        return evaluationSchema;
    }

    public void setEvaluationSchema(EvaluationSchema evaluationSchema) {
        this.evaluationSchema = evaluationSchema;
    }

    public EvaluationGroup getEvaluationGroup() {
        return evaluationGroup;
    }

    public void setEvaluationGroup(EvaluationGroup evaluationGroup) {
        this.evaluationGroup = evaluationGroup;
    }

    public EvaluationItem getParent() {
        return parent;
    }

    public void setParent(EvaluationItem parent) {
        this.parent = parent;
    }

    public RenderType getRenderType() {
        return renderType;
    }

    public void setRenderType(RenderType renderType) {
        this.renderType = renderType;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isDetailItem() {
        return detailItem;
    }

    public void setDetailItem(boolean detailItem) {
        this.detailItem = detailItem;
    }

    public boolean isScoringItem() {
        return scoringItem;
    }

    public void setScoringItem(boolean scoringItem) {
        this.scoringItem = scoringItem;
    }

   

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public boolean isMultipleEntiesAllowed() {
        return multipleEntiesAllowed;
    }

    public void setMultipleEntiesAllowed(boolean multipleEntiesAllowed) {
        this.multipleEntiesAllowed = multipleEntiesAllowed;
    }

    public DataType getDataType() {
        return dataType;
    }

    
    
    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public Area getParentAreaOfAvailableAreas() {
        return parentAreaOfAvailableAreas;
    }

    public void setParentAreaOfAvailableAreas(Area parentAreaOfAvailableAreas) {
        this.parentAreaOfAvailableAreas = parentAreaOfAvailableAreas;
    }

    public Item getParentOfAvailableItems() {
        return parentOfAvailableItems;
    }

    public void setParentOfAvailableItems(Item parentOfAvailableItems) {
        this.parentOfAvailableItems = parentOfAvailableItems;
    }

    public Institution getParentInstitutionOfAvailableInstitutions() {
        return parentInstitutionOfAvailableInstitutions;
    }

    public void setParentInstitutionOfAvailableInstitutions(Institution parentInstitutionOfAvailableInstitutions) {
        this.parentInstitutionOfAvailableInstitutions = parentInstitutionOfAvailableInstitutions;
    }

    public String getCss() {
        return css;
    }

    public void setCss(String css) {
        this.css = css;
    }

    public WebUser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(WebUser createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public WebUser getLastEditBy() {
        return lastEditBy;
    }

    public void setLastEditBy(WebUser lastEditBy) {
        this.lastEditBy = lastEditBy;
    }

    public Date getLastEditeAt() {
        return lastEditeAt;
    }

    public void setLastEditeAt(Date lastEditeAt) {
        this.lastEditeAt = lastEditeAt;
    }

    public WebUser getRetiredReversedBy() {
        return retiredReversedBy;
    }

    public void setRetiredReversedBy(WebUser retiredReversedBy) {
        this.retiredReversedBy = retiredReversedBy;
    }

    public Date getRetiredReversedAt() {
        return retiredReversedAt;
    }

    public void setRetiredReversedAt(Date retiredReversedAt) {
        this.retiredReversedAt = retiredReversedAt;
    }

    public boolean isRetired() {
        return retired;
    }

    public void setRetired(boolean retired) {
        this.retired = retired;
    }

    public WebUser getRetiredBy() {
        return retiredBy;
    }

    public void setRetiredBy(WebUser retiredBy) {
        this.retiredBy = retiredBy;
    }

    public Date getRetiredAt() {
        return retiredAt;
    }

    public void setRetiredAt(Date retiredAt) {
        this.retiredAt = retiredAt;
    }

    public String getRetireComments() {
        return retireComments;
    }

    public void setRetireComments(String retireComments) {
        this.retireComments = retireComments;
    }

  

    public MultipleItemCalculationMethod getMultipleItemCalculationMethod() {
        return multipleItemCalculationMethod;
    }

    public void setMultipleItemCalculationMethod(MultipleItemCalculationMethod multipleItemCalculationMethod) {
        this.multipleItemCalculationMethod = multipleItemCalculationMethod;
    }
    
}
