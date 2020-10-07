/*
 * The MIT License
 *
 * Copyright 2019 Dr M H B Ariyaratne<buddhika.ari@gmail.com>.
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author User
 */
@Entity
@XmlRootElement
public class Preferance implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    
    @ManyToOne
    private Item type;
    private String name;
    private String code;
    private Long areauid;
    @ManyToOne
    private Preferance parentArea;

    @ManyToOne
    private Preferance phm;
    @ManyToOne
    private Preferance phi;
    @ManyToOne
    private Preferance dsd;
    @ManyToOne
    private Preferance moh;
    @ManyToOne
    private Preferance district;
    @ManyToOne
    private Preferance province;
    @ManyToOne
    private Preferance rdhsArea;
    @ManyToOne
    private Preferance pdhsArea;

    private double centreLongitude;
    private double centreLatitude;
    private double zoomLavel;

    private Double surfaceArea;
    private Long totalPopulation;
    private Long malePopulation;
    private Long femalePopulation;
    
    private Long totalTargetPopulation;
    private Long maleTargetPopulation;
    private Long femaleTargePopulation;
    
    
    @ManyToOne
    private Institution pmci;

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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Preferance)) {
            return false;
        }
        Preferance other = (Preferance) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
    
    
    

    public Item getType() {
        return type;
    }

    public void setType(Item type) {
        this.type = type;
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

    public Preferance getParentArea() {
        return parentArea;
    }

    public void setParentArea(Preferance parentArea) {
        this.parentArea = parentArea;
    }

    public WebUser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(WebUser createdBy) {
        this.createdBy = createdBy;
    }

    public double getCentreLongitude() {
        return centreLongitude;
    }

    public void setCentreLongitude(double centreLongitude) {
        this.centreLongitude = centreLongitude;
    }

    public double getCentreLatitude() {
        return centreLatitude;
    }

    public void setCentreLatitude(double centreLatitude) {
        this.centreLatitude = centreLatitude;
    }

    public double getZoomLavel() {
        return zoomLavel;
    }

    public void setZoomLavel(double zoomLavel) {
        this.zoomLavel = zoomLavel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Preferance getRdhsArea() {
        return rdhsArea;
    }

    public void setRdhsArea(Preferance rdhsArea) {
        this.rdhsArea = rdhsArea;
    }

    public Preferance getPdhsArea() {
        return pdhsArea;
    }

    public void setPdhsArea(Preferance pdhsArea) {
        this.pdhsArea = pdhsArea;
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

    public String getRetireComments() {
        return retireComments;
    }

    public void setRetireComments(String retireComments) {
        this.retireComments = retireComments;
    }

    public Preferance getPhm() {
        return phm;
    }

    public void setPhm(Preferance phm) {
        this.phm = phm;
    }

    public Preferance getPhi() {
        return phi;
    }

    public void setPhi(Preferance phi) {
        this.phi = phi;
    }

    public Preferance getDsd() {
        return dsd;
    }

    public void setDsd(Preferance dsd) {
        this.dsd = dsd;
    }

    public Preferance getMoh() {
        return moh;
    }

    public void setMoh(Preferance moh) {
        this.moh = moh;
    }

    public Preferance getDistrict() {
        return district;
    }

    public void setDistrict(Preferance district) {
        this.district = district;
    }

    public Preferance getProvince() {
        return province;
    }

    public void setProvince(Preferance province) {
        this.province = province;
    }

    public Double getSurfaceArea() {
        return surfaceArea;
    }

    public void setSurfaceArea(Double surfaceArea) {
        this.surfaceArea = surfaceArea;
    }

    public Long getTotalPopulation() {
        return totalPopulation;
    }

    public void setTotalPopulation(Long totalPopulation) {
        this.totalPopulation = totalPopulation;
    }

    public Long getMalePopulation() {
        return malePopulation;
    }

    public void setMalePopulation(Long malePopulation) {
        this.malePopulation = malePopulation;
    }

    public Long getFemalePopulation() {
        return femalePopulation;
    }

    public void setFemalePopulation(Long femalePopulation) {
        this.femalePopulation = femalePopulation;
    }

    public Institution getPmci() {
        return pmci;
    }

    public void setPmci(Institution pmci) {
        this.pmci = pmci;
    }

    public Long getTotalTargetPopulation() {
        return totalTargetPopulation;
    }

    public void setTotalTargetPopulation(Long totalTargetPopulation) {
        this.totalTargetPopulation = totalTargetPopulation;
    }

    public Long getMaleTargetPopulation() {
        return maleTargetPopulation;
    }

    public void setMaleTargetPopulation(Long maleTargetPopulation) {
        this.maleTargetPopulation = maleTargetPopulation;
    }

    public Long getFemaleTargePopulation() {
        return femaleTargePopulation;
    }

    public void setFemaleTargePopulation(Long femaleTargePopulation) {
        this.femaleTargePopulation = femaleTargePopulation;
    }

    public Long getAreauid() {
        return areauid;
    }

    public void setAreauid(Long areauid) {
        this.areauid = areauid;
    }
    
    

}
