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
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
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
    //possible values for showing incidents criterion
    public static enum Criterion { 
        ALL("All"), 
        CLOSED("Closed"), 
        OPENED("Opened");
        
        private String label;
        
        private Criterion(String label){
            this.label=label;
        }
        
        public String getLabel(){
            return this.label;
        }
    }
    //stores the criterion for showing incidents
    private Criterion showingCriterion;
    //id of the selected incident in the table
    private Integer selectedIncident;
   
    
    //default data page length
    private static final Short DEFAULT_PAGE_LENGTH=10;
    //logger for the class
    private static final Logger logger =
            Logger.getLogger("com.mycompany.incidentmanagement.managedbeans.IncidentsViewBean");
    //Injection of the stateless EJB
    @Inject IncidentManagerLocal ejb;
    //List pager for incident data
    @Inject private ListPager pager;
    /**
     * Creates a new instance of IncidentsViewBean
     */
    public IncidentsViewBean() {
        logger.setLevel(Level.ALL);
    }
    @PostConstruct
    public void init(){
        this.pager.setDatosPorPagina(DEFAULT_PAGE_LENGTH);
        this.showingCriterion=Criterion.ALL;
        this.updateIncidents();
        this.selectedIncident=0;
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
    //getter for Criterion values
    public Criterion[] getShowingCriterions(){
        return Criterion.values();
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

    public Short getDataPageLength() {
        return this.pager.getDatosPorPagina();
    }
    
    public void setDataPageLength(Short dataPageLength) {
        this.pager.setDatosPorPagina(dataPageLength);
    }

    /**
     * Gets a collection of incidents depending on the selected showing criterion.
     * @return A Collecton of Incident objects
     */
    public Collection<Incident> getIncidents() {
            return this.pager.getActualPage();
    }
    /**
     * Adds an incident with the data stored in the bean properties.
     * @return An empty String meaning that the following view is the actual view.
     */
    public String addIncident() {
        logger.info("IncidentsViewBean: adding new incident.");
        Incident incident=new Incident(this.description,this.openedBy);
        try{
            ejb.createIncident(incident);
        }catch(Exception e){
            logger.log(Level.SEVERE, "IncidentsViewBean:{0}", e.getMessage());
            FacesContext.getCurrentInstance()
                .addMessage("openingsPanel", new FacesMessage(e.getMessage()));
        }
        logger.log(Level.FINE,"showingCriterion=:{0}",this.showingCriterion);
        this.updateIncidents();
        return "";
    }
    /**
     * Closes the selected incident.
     * @return An empty String meaning that the following view is the actual view.
     */
    public String closeIncident(){
        logger.info("IncidentsViewBean: closing incident.");
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
            logger.log(Level.SEVERE, "IncidentsViewBean:{0}", e.getMessage());
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
    /** 
     * Checks if the actual page is the last one.
     * @return True if it is.
     */
    public boolean isLastPage() {
        return this.pager.isLastPage();
    }
    /** 
     * Checks if the actual page is the first one.
     * @return True if it is.
     */
    public boolean isFirstPage() {
        return this.pager.isFirstPage();
    }
    
    public void updateIncidents(){
        logger.info("IncidentsViewBean: getting incidents.");
        logger.log(Level.FINE, "showingCriterion={0}", showingCriterion);
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
    }
    
    public void changeSelectedIncident(String id){
        this.selectedIncident=Integer.parseInt(id);
        
    }
}
