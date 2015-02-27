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

/**
 *
 * @author javi
 */
@Dependent
public class Incident implements Serializable {
    
    private static final String DEFAULT_DESCRIPTION="Default description";
    private static final String DEFAULT_OPENER="nobody";
    private Date closeDate;
    private String closedBy;
    private String solution;

    public static enum State { OPENED_STATE, CLOSED_STATE}
    
    private Integer id;
    private String description;
    private String openedBy;
    private Date openDate;
    private State state;
    
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

    public Object getId() {
        return this.id;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description=description;
    }

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
    
}
