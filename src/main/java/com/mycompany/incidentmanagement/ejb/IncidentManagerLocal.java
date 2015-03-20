/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.incidentmanagement.ejb;

import com.mycompany.incidentmanagement.entity.Incident;
import com.mycompany.incidentmanagement.exception.IncidentDataException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author javi
 */
@Local
public interface IncidentManagerLocal {

    public List<Incident> getAllIncidentsList();

    public void createIncident(Incident incident)throws IncidentDataException;

    public void closeIncident(Incident incident)throws IncidentDataException;

    public List<Incident> getIncidentsListByState(Incident.State state);
    
}
