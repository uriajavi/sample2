/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.incidentmanagement.managedbeans;

import com.mycompany.incidentmanagement.ejb.IncidentManagerLocal;
import com.mycompany.incidentmanagement.entity.Incident;
import com.mycompany.incidentmanagement.exception.IncidentDataException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.Collection;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import useraccess.util.ListPager;

/**
 * Managed bean for helping IncidentsView.
 * @author javi
 */
@Named(value = "incidentsViewBean")
@SessionScoped
public class IncidentsViewBean implements Serializable {

    private String description;
    private String openedBy;
    private String closedBy;
    private String solution;
    //stores the criterion for showing incidents
    private Criterion showingCriterion;
    //id of the selected incident in the table
    private Integer selectedIncident;
    //possible values for showing incidents criterion
    public static enum Criterion { ALL, CLOSED, OPENED}
    //default data page length
    private static final Short DEFAULT_PAGE_LENGTH=5;
    //Injection of the stateless EJB
    @Inject IncidentManagerLocal ejb;
    //List pager for incident data
    @Inject private ListPager pager;
    /**
     * Creates a new instance of IncidentsViewBean
     */
    public IncidentsViewBean() {
        
    }
    @PostConstruct
    public void init(){
        this.pager.setDatosPorPagina(DEFAULT_PAGE_LENGTH);
    }

    public void setDescription(String description) {
        this.description=description;
    }

    public String getDescription() {
        return this.description;
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
    public void setShowingCriterion(Criterion showingCriterion) {
        this.showingCriterion=showingCriterion;
    }

    public Criterion getShowingCriterion() {
        return this.showingCriterion;
    }
    
    public Integer getSelectedIncident() {
        return this.selectedIncident;
    }
    
    public void setSelectedIncident(Integer id) {
        this.selectedIncident=id;
    }
    
    public void setDataPageLength(Short dataPageLength) {
        this.pager.setDatosPorPagina(dataPageLength);
    }

    /**
     * Gets a collection of incidents depending on the selected showing criterion.
     * @return A Collecton of Incident objects
     */
    public Collection<Incident> getIncidents() {
        Collection incidents = null;
        switch(this.showingCriterion){
            case ALL:
                incidents=ejb.getAllIncidentsList();
                break;
            case CLOSED:
                incidents=ejb.getIncidentsListByState(Incident.State.CLOSED_STATE);
                break;
            case OPENED:
                incidents=ejb.getIncidentsListByState(Incident.State.OPENED_STATE);
                break;
        }
        //updates data for the pager
        this.pager.actualizar(incidents);
        //returns the actual page of data 
        return this.pager.getActualPage();
    }
    /**
     * Adds an incident with the data stored in the bean properties.
     * @return An empty String meaning that the following view is the actual view.
     */
    public String addIncident() {
        Incident incident=new Incident(this.description,this.openedBy);
        try{
            ejb.createIncident(incident);
        }catch(Exception e){
            FacesContext.getCurrentInstance()
                .addMessage("openingsPanel", new FacesMessage(e.getMessage()));
        }
        return "";
    }
    /**
     * Closes the selected incident.
     * @return An empty String meaning that the following view is the actual view.
     */
    public String closeIncident(){
        Incident incident=null;
        //locates selected incident
        for(Incident inc: ejb.getIncidentsListByState(Incident.State.OPENED_STATE)){
            if(inc.getId().equals(this.selectedIncident)){
                incident=inc;
                break;
            }
        }
        try{
            incident.setClosedBy(this.closedBy);
            incident.setSolution(this.solution);
            ejb.closeIncident(incident);
        }catch(Exception e){
            FacesContext.getCurrentInstance()
                .addMessage("closingsPanel", new FacesMessage(e.getMessage()));
        }
        return "";
    }
    /**
     * Gets the first page of data for the incidents table.
     * @return A collection of Incidents containing the data.
     */
    public Collection<Incident> getFirstPage() {
        return this.pager.getFirstPage();
    }
    /**
     * Gets the last page of data for the incidents table.
     * @return A collection of Incidents containing the data.
     */
    public Collection<Incident> getLastPage() {
        return this.pager.getLastPage();
    }
    /**
     * Gets the next page of data for the incidents table.
     * @return A collection of Incidents containing the data.
     */
    public Collection<Incident> getNextPage() {
        return this.pager.getNextPage();
    }
    /**
     * Gets the previous page of data for the incidents table.
     * @return A collection of Incidents containing the data.
     */
    public Collection<Incident> getPrevPage() {
        return this.pager.getPrevPage();
    }


}
