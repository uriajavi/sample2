/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.incidentmanagement.ejb;

import com.mycompany.incidentmanagement.entity.Incident;
import com.mycompany.incidentmanagement.exception.IncidentDataException;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.UserTransaction;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;

/**
 * Integration tester for IncidentManager stateless EJB
 * @author javi
 */
@RunWith(Arquillian.class)
public class IncidentManagerLocalIT {
    @Deployment
    public static WebArchive createDeployment(){
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addClass(IncidentManagerLocal.class)
                .addClass(IncidentManager.class)
                .addClass(Incident.class)
                .addClass(IncidentDataException.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsResource("META-INF/persistence.xml");
    }

    @Inject
    IncidentManagerLocal manager;
    
    @Inject
    Incident incident;
    
    @Inject
    UserTransaction utx;
    
    @PersistenceContext
    EntityManager em;
 
    @Before
    public void initData()throws Exception{
        utx.begin();
        em.joinTransaction();
    }
    @After
    public void clearData()throws Exception{
        
        utx.commit();
        utx.begin();
        em.joinTransaction();
        Query query=em.createQuery("delete from Incident");
        query.executeUpdate();
        utx.commit();
    } 
    
    /**
     * Validates incident bean for constraint violations
     * @param inc 
     */
    private void validateBean(Incident inc){
        //this code is to avoid ConstraintViolationException during the
        //bean validation in the persist loop
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        validator.validate(inc);
    }
    /**
     * Tests the creation of the incident using the manager.
     * @throws com.mycompany.incidentmanagement.exception.IncidentDataException
     */
    @Test
    public void testCreateIncident() throws IncidentDataException{
        incident.setId(1);
        incident.setDescription("New incident from IncidentManagerTest.");
        incident.setOpenedBy("Tester");
        manager.createIncident(incident);
        Incident inc=em.find(Incident.class, 1);
        Assert.assertEquals(incident, inc);
    }
    /**
     * Tests the fetching of all the incidents from the DB.
     */
    @Test
    public void testGetAllIncidentsList() {

        final int NUM_DATA=20;
        for(int i=1;i<=NUM_DATA;i++){
            Incident inc=new Incident();
            inc.setId(i);
            inc.setDescription("New incident"+i+" from IncidentManagerTest");
            inc.setOpenedBy("Tester");
            this.validateBean(inc);
            em.persist(inc);
        }

        List<Incident> incidents=manager.getAllIncidentsList();
        Assert.assertEquals("All incident data query is not the same size",
                            NUM_DATA,
                            incidents.size());
        for(Incident inc : incidents){
            Assert.assertEquals("Incident description does not match with its id.",
                                "New incident"+inc.getId().toString()+" from IncidentManagerTest",
                                inc.getDescription());
        }
        
    }
    /**
     * Tests the closing of an incident using the manager.
     * @throws com.mycompany.incidentmanagement.exception.IncidentDataException
     */
    @Test
    public void testCloseIncident() throws IncidentDataException{
        incident.setId(1);
        incident.setDescription("New incident to be closed from IncidentManagerTest.");
        incident.setOpenedBy("Tester");
        em.persist(incident);
        incident.setSolution("Solution by Tester");
        incident.setClosedBy("Tester");
        manager.closeIncident(incident);
        Incident inc=em.find(Incident.class, 1);
        Assert.assertEquals("Incident state incorrect: should be closed.",
                             Incident.State.CLOSED_STATE, inc.getState());
        Assert.assertNotNull("Incident closed date is null.", inc.getCloseDate());
        Assert.assertNotNull("Incident closedBy date is null.", inc.getClosedBy());
        Assert.assertNotNull("Incident solution is null.", inc.getSolution());
    }
    /**
     * Tests the fetching incidents by state from the DB.
     */
    @Test
    public void testGetIncidentsListByState() {

        final int NUM_DATA=20;
        for(int i=1;i<=NUM_DATA;i++){
            Incident inc=new Incident();
            inc.setId(i);
            inc.setDescription("New incident"+i+" from IncidentManagerTest");
            inc.setOpenedBy("Tester");
            if(i%2==0)inc.setState(Incident.State.CLOSED_STATE);
            this.validateBean(inc);
            em.persist(inc);
        }

        List<Incident> closedIncidents=manager.getIncidentsListByState(Incident.State.CLOSED_STATE);
        List<Incident> openedIncidents=manager.getIncidentsListByState(Incident.State.OPENED_STATE);
        Assert.assertEquals("Closed incidents list size is not correct.",
                            NUM_DATA/2,
                            closedIncidents.size());
        Assert.assertEquals("Opened incidents list size is not correct.",
                            NUM_DATA/2,
                            openedIncidents.size());
        for(Incident inc : closedIncidents){
            Assert.assertEquals("Incident state does not match with its id.",
                                0,
                                inc.getId()%2);
        }
        for(Incident inc : openedIncidents){
            Assert.assertTrue("Incident state does not match with its id.",
                                inc.getId()%2!=0);
        }
        
    }
   
}
