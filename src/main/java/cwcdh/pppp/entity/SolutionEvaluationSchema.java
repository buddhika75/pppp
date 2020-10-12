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

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.Transient;

/**
 *
 * @author ruhunudump
 */
@Entity
public class SolutionEvaluationSchema implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Solution solution;
    
    private boolean frontEndDetail;

    @ManyToOne
    private EvaluationSchema evaluationSchema;

    private Double orderNo;
    private Double weightage;
    private Double score;

    /*
    Assign Properties
     */
    private boolean assigned;
    @ManyToOne
    private WebUser assignedBy;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date assignedAt;
    @Lob
    private String assignComments;
    /*
    Accept Properties
     */
    @Lob
    private String acceptComments;
    private boolean accepted;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date acceptedAt;
    /*
    Accept Properties
     */
    private boolean rejected;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date rejectedAt;
    @Lob
    private String rejectionComments;
    /*
    Evaluation Properties
     */
    @ManyToOne
    private WebUser evaluationBy;
    /*
    Evaluation Completion Properties
     */
    private boolean completed;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date completedAt;
    @Lob
    private String completeComments;
    /*
    Enrolled Properties
     */
    private boolean enrolled;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date enrolledAt;
    @ManyToOne
    private WebUser enrolledBy;
    @Lob
    private String enrollComments;
    private boolean enrollRemoved;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date enrolledRemovedAt;
    @ManyToOne
    private WebUser enrolledRemovedBy;
    @Lob
    private String enrollRemovedComments;
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
    private WebUser lastEditedBy;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date lastEditedAt;
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

    @Transient
    private boolean canAcccept;
    @Transient
    private boolean canReject;
    @Transient
    private boolean canComplete;
    @Transient
    private boolean canEvaluate;
    @Transient
    private boolean canEnroll;
    @Transient
    private boolean canDecline;
    
    
    

    public void formulateCanStates() {
        canAcccept = false;
        canReject = false;
        canComplete = false;
        canEvaluate = false;
        canEnroll = false;
        canDecline = false;
        if (this.retired) {
            return;
        }
        if (this.enrollRemoved) {
            return;
        }
        if (this.enrolled) {
            canDecline = true;
            return;
        }
        if (this.completed) {
            canDecline = true;
            canEnroll = true;
            return;
        }
        if (this.rejected) {
            return;
        }
        if (this.accepted) {
            canComplete = true;
            canEvaluate = true;
            return;
        }
        if (this.assigned) {
            canAcccept = true;
            canReject = false;
            return;
        }

    }

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
        if (!(object instanceof SolutionEvaluationSchema)) {
            return false;
        }
        SolutionEvaluationSchema other = (SolutionEvaluationSchema) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cwcdh.pppp.entity.SolutionEvaluationScheme[ id=" + id + " ]";
    }

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

    public Double getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Double orderNo) {
        this.orderNo = orderNo;
    }

    public Double getWeightage() {
        return weightage;
    }

    public void setWeightage(Double weightage) {
        this.weightage = weightage;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
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

    public WebUser getLastEditedBy() {
        return lastEditedBy;
    }

    public void setLastEditedBy(WebUser lastEditedBy) {
        this.lastEditedBy = lastEditedBy;
    }

    public Date getLastEditedAt() {
        return lastEditedAt;
    }

    public void setLastEditedAt(Date lastEditedAt) {
        this.lastEditedAt = lastEditedAt;
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

    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public WebUser getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(WebUser assignedBy) {
        this.assignedBy = assignedBy;
    }

    public Date getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(Date assignedAt) {
        this.assignedAt = assignedAt;
    }

    public Date getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(Date acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public Date getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Date completedAt) {
        this.completedAt = completedAt;
    }

    public Date getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(Date enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    public WebUser getEnrolledBy() {
        return enrolledBy;
    }

    public void setEnrolledBy(WebUser enrolledBy) {
        this.enrolledBy = enrolledBy;
    }

    public String getAssignComments() {
        return assignComments;
    }

    public void setAssignComments(String assignComments) {
        this.assignComments = assignComments;
    }

    public String getRejectionComments() {
        return rejectionComments;
    }

    public void setRejectionComments(String rejectionComments) {
        this.rejectionComments = rejectionComments;
    }

    public String getCompleteComments() {
        return completeComments;
    }

    public void setCompleteComments(String completeComments) {
        this.completeComments = completeComments;
    }

    public String getEnrollComments() {
        return enrollComments;
    }

    public void setEnrollComments(String enrollComments) {
        this.enrollComments = enrollComments;
    }

    public boolean isRejected() {
        return rejected;
    }

    public void setRejected(boolean rejected) {
        this.rejected = rejected;
    }

    public Date getRejectedAt() {
        return rejectedAt;
    }

    public void setRejectedAt(Date rejectedAt) {
        this.rejectedAt = rejectedAt;
    }

    public WebUser getEvaluationBy() {
        return evaluationBy;
    }

    public void setEvaluationBy(WebUser evaluationBy) {
        this.evaluationBy = evaluationBy;
    }

    public boolean isEnrolled() {
        return enrolled;
    }

    public void setEnrolled(boolean enrolled) {
        this.enrolled = enrolled;
    }

    public Date getEnrolledRemovedAt() {
        return enrolledRemovedAt;
    }

    public void setEnrolledRemovedAt(Date enrolledRemovedAt) {
        this.enrolledRemovedAt = enrolledRemovedAt;
    }

    public WebUser getEnrolledRemovedBy() {
        return enrolledRemovedBy;
    }

    public void setEnrolledRemovedBy(WebUser enrolledRemovedBy) {
        this.enrolledRemovedBy = enrolledRemovedBy;
    }

    public String getEnrollRemovedComments() {
        return enrollRemovedComments;
    }

    public void setEnrollRemovedComments(String enrollRemovedComments) {
        this.enrollRemovedComments = enrollRemovedComments;
    }

    public boolean isEnrollRemoved() {
        return enrollRemoved;
    }

    public void setEnrollRemoved(boolean enrollRemoved) {
        this.enrollRemoved = enrollRemoved;
    }

    public String getAcceptComments() {
        return acceptComments;
    }

    public void setAcceptComments(String acceptComments) {
        this.acceptComments = acceptComments;
    }

    public boolean isCanAcccept() {
        formulateCanStates();
        return canAcccept;
    }

    public boolean isCanReject() {
        formulateCanStates();
        return canReject;
    }

    public boolean isCanComplete() {
        formulateCanStates();
        return canComplete;
    }

    public boolean isCanEvaluate() {
        formulateCanStates();
        return canEvaluate;
    }

    public boolean isCanEnroll() {
        formulateCanStates();
        return canEnroll;
    }

    public boolean isCanDecline() {
        formulateCanStates();
        return canDecline;
    }

    public boolean isFrontEndDetail() {
        return frontEndDetail;
    }

    public void setFrontEndDetail(boolean frontEndDetail) {
        this.frontEndDetail = frontEndDetail;
    }

}
