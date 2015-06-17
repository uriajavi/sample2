/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.incidentmanagement.managedbeans;

import com.mycompany.incidentmanagement.ejb.IncidentManager;
import com.mycompany.incidentmanagement.ejb.IncidentManagerLocal;
import com.mycompany.incidentmanagement.entity.Incident;
import com.mycompany.incidentmanagement.exception.IncidentDataException;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.UserTransaction;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import junit.framework.Assert;
import org.apache.commons.lang.RandomStringUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;

/**
 * Integration Tester for IncidentsViewBean.
 * @author javi
 */
@RunWith(Arquillian.class)
public class IncidentsViewBeanIT {
    @Deployment
    public static WebArchive createDeployment(){
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addClass(IncidentManagerLocal.class)
                .addClass(IncidentManager.class)
                .addClass(Incident.class)
                .addClass(IncidentDataException.class)
                .addClass(IncidentsViewBean.class)
                .addClass(useraccess.util.ListPager.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsResource("META-INF/persistence.xml");
    }
    @Inject private IncidentsViewBean bean;
    @Inject private IncidentManagerLocal ejb;
    @Inject private UserTransaction utx;
    @PersistenceContext private EntityManager em;
    private final int NUM_DATA=20;

    @Before
    public void initData()throws Exception{
        for(int i=1;i<=NUM_DATA;i++){
            Incident inc=new Incident();
            inc.setId(i);
            inc.setDescription(RandomStringUtils.randomAlphanumeric(255));
            inc.setOpenedBy(RandomStringUtils.randomAlphanumeric(255));
            if(i%2==0)inc.setState(Incident.State.CLOSED_STATE);
            this.validateBean(inc);
            ejb.createIncident(inc);
        }
    }
    
    @After
    public void clearData()throws Exception{
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
     * Tests the method that obtains Incidents for getting all incidents
     * @throws com.mycompany.incidentmanagement.exception.IncidentDataException
     */
    @Test
    public void testGetAllIncidents() throws IncidentDataException{
        bean.setShowingCriterion(IncidentsViewBean.Criterion.ALL);
        bean.setDataPageLength(new Short("20"));
        Collection<Incident> incidents=bean.getIncidents();
        Assert.assertTrue("getIncidents() does not return a collection!",
                           Collection.class.isInstance(incidents));
        Assert.assertEquals("getIncidents does not return all incidents!", 
                            NUM_DATA, incidents.size());
        
    }
    /**
     * Tests the method that obtains Incidents for getting closed incidents
     * @throws com.mycompany.incidentmanagement.exception.IncidentDataException
     */
    @Test
    public void testGetClosedIncidents() throws IncidentDataException{
        bean.setShowingCriterion(IncidentsViewBean.Criterion.CLOSED);
        bean.setDataPageLength(new Short("20"));
        Collection<Incident> incidents=bean.getIncidents();
        Assert.assertTrue("getIncidents() does not return a collection!",
                           Collection.class.isInstance(incidents));
        Assert.assertEquals("getIncidents does not return all closed incidents!", 
                            NUM_DATA/2, incidents.size());
        for(Incident inc: incidents)
            Assert.assertEquals("Incident state is not CLOSED!", 
                                Incident.State.CLOSED_STATE, 
                                inc.getState());
        
    }
    /**
     * Tests the method that obtains Incidents for getting opened incidents
     * @throws com.mycompany.incidentmanagement.exception.IncidentDataException
     */
    @Test
    public void testGetOpenedIncidents() throws IncidentDataException{
        bean.setShowingCriterion(IncidentsViewBean.Criterion.OPENED);
        bean.setDataPageLength(new Short("20"));
        Collection<Incident> incidents=bean.getIncidents();
        Assert.assertTrue("getIncidents() does not return a collection!",
                           Collection.class.isInstance(incidents));
        Assert.assertEquals("getIncidents does not return all opened incidents!", 
                            NUM_DATA/2, incidents.size());
        for(Incident inc: incidents)
            Assert.assertEquals("Incident state is not OPENED!", 
                                Incident.State.OPENED_STATE, 
                                inc.getState());
        
        
    }
    /**
     * Tests the addition of an Incident from the Incidents managed bean.
     */
    @Test
    public void testAddIncident(){
        //sets data for the new incident
        bean.setDescription("New incident created from Incidents managed bean");
        bean.setOpenedBy("Opener from Incidents managed bean");
        //adds the new incident
        bean.addIncident();
        //looks for opened incidents and for one that has the new incident data
        bean.setShowingCriterion(IncidentsViewBean.Criterion.OPENED);
        bean.setDataPageLength(new Short("20"));
        boolean incidentFound=false;
        for(Incident inc: bean.getIncidents())
            if(inc.getDescription().equals("New incident created from Incidents managed bean")&&
               inc.getOpenedBy().equals("Opener from Incidents managed bean")) 
                    incidentFound=true;
        //tests if incident has been found and therefore added correctly 
        Assert.assertTrue("New incident not found!",incidentFound);
        
    }
    /**
     * Tests the method that closes an incident from the Incident managed bean.
     */
    @Test
    public void testCloseIncident(){
        //looks for opened incidents 
        bean.setShowingCriterion(IncidentsViewBean.Criterion.OPENED);
        //chooses the first opened incident to be closed
        Incident incident=(Incident) ((List)bean.getIncidents()).get(0);
        bean.setSelectedIncident(incident.getId());
        //closes the incident
        bean.setClosedBy("Incident closer");
        bean.setSolution("Solution for closing incident");
        bean.closeIncident();
        //gets all incidents to find the recently closed
        bean.setShowingCriterion(IncidentsViewBean.Criterion.CLOSED);
        boolean incidentFound=false;
        for(Incident inc: bean.getIncidents()){
            if(inc.getId().equals(incident.getId())){ 
                    incidentFound=true;
                    incident=inc;
                    break;
            }
        }
        //tests if incident has been found and therefore closed correctly 
        Assert.assertTrue("Closed incident not found!",incidentFound);
        Assert.assertEquals("Incident id is wrong!", incident.getId(), 
                            bean.getSelectedIncident());
        Assert.assertNotNull("Closing date is null!", incident.getCloseDate());
        
    }
    /**
     * Tests the getting of the first page of data.
     */
    @Test
    public void testGetFirstPage(){
        //sets the data selection criterion
        bean.setShowingCriterion(IncidentsViewBean.Criterion.ALL);
        //sets the number of data elements for page
        bean.setDataPageLength(new Short("5"));
        bean.getIncidents();
        //gets the last page
        Collection<Incident> dataPage=bean.getFirstPage();
        //checks the ids of the first's page elements
        int i=1;
        for(Incident inc:dataPage){
            Assert.assertEquals("First data page element id is wrong!", 
                                 i, inc.getId().intValue());
            i++;
        }
    }
    /**
     * Tests the getting of the last page of data.
     */
    @Test
    public void testGetLastPage(){
        //sets the data selection criterion
        bean.setShowingCriterion(IncidentsViewBean.Criterion.ALL);
        //sets the number of data elements for page
        bean.setDataPageLength(new Short("5"));
        bean.getIncidents();
        //gets the first page
        Collection<Incident> dataPage=bean.getLastPage();
        //checks the ids of the first's page elements
        int i=16;
        for(Incident inc:dataPage){
            Assert.assertEquals("Last data page element id is wrong!", 
                                 i, inc.getId().intValue());
            i++;
        }
    }
    /**
     * Tests the getting of the next page of data.
     */
    @Test
    public void testGetNextPage(){
        //sets the data selection criterion
        bean.setShowingCriterion(IncidentsViewBean.Criterion.ALL);
        //sets the number of data elements for page
        bean.setDataPageLength(new Short("5"));
        bean.getIncidents();
        //gets the next page
        Collection<Incident> dataPage=bean.getNextPage();
        //checks the ids of the first's page elements
        int i=6;
        for(Incident inc:dataPage){
            Assert.assertEquals("Next data page element id is wrong!", 
                                 i, inc.getId().intValue());
            i++;
        }
    }
    /**
     * Tests the getting of the previous page of data.
     */
    @Test
    public void testGetPreviousPage(){
        //sets the data selection criterion
        bean.setShowingCriterion(IncidentsViewBean.Criterion.ALL);
        //sets the number of data elements for page
        bean.setDataPageLength(new Short("5"));
        bean.getIncidents();
        //gets the last page
        bean.getLastPage();
        //gets the previous page
        Collection<Incident> dataPage=bean.getPrevPage();
        //checks the ids of the first's page elements
        int i=11;
        for(Incident inc:dataPage){
            Assert.assertEquals("Prev data page element id is wrong!", 
                                 i, inc.getId().intValue());
            i++;
        }
    }
    
    @Test
    public void testIsLastPage(){
        //sets the data selection criterion
        bean.setShowingCriterion(IncidentsViewBean.Criterion.ALL);
        //sets the number of data elements for page
        bean.setDataPageLength(new Short("20"));
        bean.getIncidents();
        Assert.assertTrue("Page is not last page!", 
                          bean.isLastPage());        
    }
    
    @Test
    public void testIsFirstPage(){
        //sets the data selection criterion
        bean.setShowingCriterion(IncidentsViewBean.Criterion.ALL);
        //sets the number of data elements for page
        bean.setDataPageLength(new Short("20"));
        bean.getIncidents();
        Assert.assertTrue("Page is not last page!", 
                          bean.isFirstPage());        
    }
}
