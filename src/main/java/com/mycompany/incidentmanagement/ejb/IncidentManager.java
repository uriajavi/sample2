/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.incidentmanagement.ejb;

import com.mycompany.incidentmanagement.entity.Incident;
import com.mycompany.incidentmanagement.exception.IncidentDataException;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Stateless EJB for managing operations with Incident entities.
 * @author javi
 */
@Stateless
public class IncidentManager implements IncidentManagerLocal {
    @PersistenceContext
    private EntityManager em;
    /**
     * Creates an incident entry in the database table.
     * @param incident the incident with the data to insert
     * @throws com.mycompany.incidentmanagement.exception.IncidentDataException
     *          if there is any missing data in the Incident to be inserted.
     */
    @Override
    public void createIncident(Incident incident)throws IncidentDataException{
        //validates data before inserting
        if(incident.getDescription()== null || incident.getDescription().equals("")||
           incident.getOpenDate()==null || 
           incident.getOpenedBy()==null || incident.getOpenedBy().equals(""))
            throw new IncidentDataException("Incident not completely informed before insert.");
        //persists incident
        em.persist(incident);
    }
    /**
     * Returns a list containing all the incident entries in the database.
     * @return A List of Incident
     */
    @Override
    public List<Incident> getAllIncidentsList() {
        return em.createNamedQuery("findAllIncidents").getResultList();
    }
    /**
     * Close an open incident.
     * @param incident
     * @throws IncidentDataException
     */
    @Override
    public void closeIncident(Incident incident) throws IncidentDataException {
        //validates data before incident closing
        if(incident.getDescription()== null || incident.getDescription().equals("")||
           incident.getOpenDate()==null || 
           incident.getOpenedBy()==null || incident.getOpenedBy().equals("")||
           incident.getClosedBy()==null || incident.getClosedBy().equals("")||
           incident.getSolution()==null || incident.getSolution().equals(""))
            throw new IncidentDataException("Incident not completely informed before closing.");
        //sets close date and state
        incident.setCloseDate(new Date());
        incident.setState(Incident.State.CLOSED_STATE);
        if(! em.contains(incident))incident=em.merge(incident);
    }
    /**
     * Returns a list containing incidents corresponding to passed state.
     * @param state the state of the incidents to be obtained
     * @return A List of Incident
     */
    @Override
    public List<Incident> getIncidentsListByState(Incident.State state) {
        return em.createNamedQuery("findIncidentsByState")
                .setParameter("state", state).getResultList();
    }
}
