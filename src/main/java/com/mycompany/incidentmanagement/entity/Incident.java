/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.incidentmanagement.entity;

import java.io.Serializable;
import java.util.Date;
import javax.enterprise.context.Dependent;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Past;

/**
 *
 * @author javi
 */
@Entity
@Dependent
@Table(name="incident",schema="incidentdb")
@NamedQueries({
        @NamedQuery(name="findAllIncidents",
                    query="SELECT i FROM Incident i ORDER BY i.openDate DESC"
        ),
        @NamedQuery(name="findIncidentsByState",
                    query="SELECT i FROM Incident i WHERE i.state = :state")
})
public class Incident implements Serializable {
    
    private static final String DEFAULT_DESCRIPTION="Default description";
    private static final String DEFAULT_OPENER="nobody";

    public static enum State { OPENED_STATE, CLOSED_STATE}
    
    private Integer id;
    private String description;
    private String openedBy;
    private Date openDate;
    private State state;
    private Date closeDate;
    private String closedBy;
    private String solution;
    
    public Incident(String description, String openedBy) {
        this.state=State.OPENED_STATE;
        this.description=description;
        this.openedBy=openedBy;
        this.openDate=new Date();
    }

    public Incident() {
        this.description=DEFAULT_DESCRIPTION;
        this.openedBy=DEFAULT_OPENER;
        this.state=State.OPENED_STATE;
        this.openDate=new Date();
    }

    public void setId(Integer id) {
        this.id=id;
    }
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public Integer getId() {
        return this.id;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description=description;
    }
    @Temporal(TemporalType.DATE)
    @Past
    public Date getOpenDate() {
        return this.openDate;
    }

    public void setOpenDate(Date openDate) {
        this.openDate=openDate;
    }
    
    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state=state;
    }
    
    public void setCloseDate(Date closeDate) {
        this.closeDate=closeDate;
    }
    @Temporal(TemporalType.DATE)
    @Past
    public Date getCloseDate() {
        return this.closeDate;
    }

    public void setOpenedBy(String openedBy) {
        this.openedBy=openedBy;
    }

    public String getOpenedBy() {
        return this.openedBy;
    }

    public void setClosedBy(String closedBy) {
        this.closedBy=closedBy;
    }

    public String getClosedBy() {
        return this.closedBy;
    }

    public void setSolution(String solution) {
        this.solution=solution;
    }

    public String getSolution() {
        return this.solution;
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
        if (!(object instanceof Incident)) {
            return false;
        }
        Incident other = (Incident) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "incidentmanagement.entity.Incident[ id=" + id + " ]";
    }
    

    
}
