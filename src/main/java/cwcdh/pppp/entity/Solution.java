package cwcdh.pppp.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
public class Solution implements Serializable {

// <editor-fold defaultstate="collapsed" desc="Attributes">
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private static final long serialVersionUID = 1L;

    private String name;

    @Column
    private String shortName;

    @Transient
    String solutionData;

    @Lob
    private String description;

    private long viewCount;

   

    

    private boolean featured;

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

    // </editor-fold>   
// <editor-fold defaultstate="collapsed" desc="Overrides">
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof Solution)) {
            return false;
        }
        Solution other = (Solution) object;
        if (this.id == null && (other.getId() != null || (this.id != null && !this.id.equals(other.getId())))) {
            return false;
        } else {
        }
        return true;
    }

    @Override
    public String toString() {
        return "Solution{" + "id=" + id + '}';
    }

// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="Getters & Setters">
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

// </editor-fold>

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getViewCount() {
        return viewCount;
    }

    public void setViewCount(long viewCount) {
        this.viewCount = viewCount;
    }

   

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

   

    public String getShortNameTmp() {
        if (name == null) {
            return "";
        }
        String tn = name + "                                              ";
        return tn.substring(0, 35);
    }

    public String getSolutionData() {
        return solutionData;
    }

   

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

}
