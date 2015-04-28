/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.incidentmanagement.entity;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.SystemException;
import javax.transaction.Transactional;
import static javax.transaction.Transactional.TxType;
import javax.transaction.UserTransaction;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class for Incident entity.
 * @author javi
 */
@RunWith(Arquillian.class)
public class IncidentIT {
    @Deployment
    public static WebArchive createDeployment(){
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addClass(Incident.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsResource("META-INF/persistence.xml");
        
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
        Set<ConstraintViolation<Incident>> constraintViolations = validator.validate(inc);
        /*if(constraintViolations.size() > 0){
                    Iterator<ConstraintViolation<Incident>> iterator = constraintViolations.iterator();
                    while(iterator.hasNext()){
                        ConstraintViolation<Incident> cv = iterator.next();
                        System.err.println(cv.getRootBeanClass().getName()+"."+cv.getPropertyPath() + " " +cv.getMessage());
                    }    
        }else{*/
    }
    
    private final int MAX_DATA=20;
    //injected bean for testing
    @Inject Incident incident;
    //entity manager
    @PersistenceContext
    EntityManager em;
    //transaction
    @Inject UserTransaction utx;
    /**
     * Tests that a new incident is not null.
     */
    @Test
    public void testIncidentConstruction() {
        Incident i=new Incident();
        Assert.assertNotNull(i);
    }
    /**
     * Tests that a new constructed incident using parameters is not null.
     */
    @Test
    public void testIncidentParameterizedConstruction() {
        Incident i=new Incident("Test incident description.","Test OpenerName");
        Assert.assertNotNull(i);
    }
    /**
     * Tests that an new injected incident has today's date as open date.
     */
    @Test 
    public void testNewIncidentOpenDate(){
        Assert.assertEquals(DateFormat.getDateInstance().format(new Date()), 
                            DateFormat.getDateInstance().format(incident.getOpenDate()));
    }
    /**
     * Tests that a new injected incident has OPENED as state.
     */
    @Test
    public void testNewIncidentState(){
       Assert.assertEquals(Incident.State.OPENED_STATE,incident.getState());
    }
    /**
     * Tests Id setter/getter.
     * 
     */
    @Test
    //@InSequence(1)
    public void testIdSetGet(){
        Integer id=99;
        incident.setId(id);
        Assert.assertEquals(id, incident.getId());
    }
    /**
     * Tests openDate setter/getter.
     * 
     */
    @Test
    //@InSequence(2)
    public void testOpenDateSetGet(){
        Date date=new Date();
        incident.setOpenDate(date);
        Assert.assertEquals(date, incident.getOpenDate());
    }
    /**
     * Tests description setter/getter.
     * 
     */
    @Test
    //@InSequence(3)
    public void testDescriptionSetGet(){
        String description="New description";
        incident.setDescription(description);
        Assert.assertEquals(description, incident.getDescription());
    }
    /**
     * Tests solution setter/getter.
     * 
     */
    @Test
    //@InSequence(4)
    public void testSolutionSetGet(){
        String solution="New solution test data";
        incident.setSolution(solution);
        Assert.assertEquals(solution, incident.getSolution());
    }
    /**
     * Tests state setter/getter.
     * 
     */
    @Test
    //@InSequence(5)
    public void testStateSetGet(){
        Incident.State state=Incident.State.CLOSED_STATE;
        incident.setState(state);
        Assert.assertEquals(state, incident.getState());
    }
    /**
     * Tests closeDate setter/getter.
     * 
     */
    @Test
    //@InSequence(6)
    public void testCloseDateSetGet(){
        Calendar cal=Calendar.getInstance();
        cal.set(2015, 5, 14);
        Date date=cal.getTime();
        incident.setCloseDate(date);
        Assert.assertEquals(date, incident.getCloseDate());
    }
    /**
     * Tests openedBy setter/getter.
     * 
     */
    @Test
    //@InSequence(7)
    public void testOpenedBySetGet(){
        String opener="New opener";
        incident.setOpenedBy(opener);
        Assert.assertEquals(opener, incident.getOpenedBy());
    }
    /**
     * Tests closedBy setter/getter.
     * 
     */
    @Test    
    //@InSequence(8)
    public void testClosedBySetGet(){
        String closer="New closer";
        incident.setClosedBy(closer);
        Assert.assertEquals(closer, incident.getClosedBy());
    }
    /**
     * Tests persist operation over entity incident
     * @throws javax.transaction.Exception
     */
    @Test
    @InSequence(10)
    public void testIncidentPersist()throws Exception{
        //try{
            utx.begin();
            em.joinTransaction();
            //int i=2;
            for(int i=1;i<=MAX_DATA;i++){
                Incident inc=new Incident();
                inc.setDescription("Injected incident "+i);
                inc.setOpenedBy("Javi");
                inc.setId(i);
                //we do the even incidents to be closed
                if(i%2==0)inc.setState(Incident.State.CLOSED_STATE);
                Assert.assertNotNull(inc.getId());
                validateBean(inc);
                em.persist(inc);
            }
            utx.commit();
            //em.clear();
        /*}catch(Exception e){
            utx.rollback();
        }*/
    }
    /**
     * Tests to find all incidents
     * 
     * @throws java.lang.Exception
     */
    @Test
    @InSequence(11)
    public void testFindAllIncidents()throws Exception{
        utx.begin();
        em.joinTransaction();
        List data=em.createNamedQuery("findAllIncidents").getResultList();
        Assert.assertEquals(MAX_DATA,data.size());
        utx.commit();
    }
        /**
     * Tests to find incidents by state
     * 
     * @throws java.lang.Exception
     */
    @Test
    @InSequence(12)
    public void testFindIncidentsByState()throws Exception{
        utx.begin();
        em.joinTransaction();
        Query query=em.createNamedQuery("findIncidentsByState");
        List openIncidents=query.setParameter("state",Incident.State.OPENED_STATE).getResultList();
        List closedIncidents=query.setParameter("state", Incident.State.CLOSED_STATE).getResultList();
        utx.commit();
     
        Assert.assertEquals(MAX_DATA,openIncidents.size()+closedIncidents.size());
        Assert.assertEquals(MAX_DATA/2,openIncidents.size());
        Assert.assertEquals(MAX_DATA/2,closedIncidents.size());
    }
    /**Tests closing all opened incidents.
     * 
     * @throws java.lang.Exception
     */
    @Test
    @InSequence(13)
    public void testClosingIncidents()throws Exception{
        utx.begin();
        em.joinTransaction();
        Query query=em.createNamedQuery("findIncidentsByState");
        List<Incident> openIncidents=query.setParameter("state",Incident.State.OPENED_STATE).getResultList();
        Assert.assertTrue("Opened incidents do not exist.", openIncidents.size()>0);
        for(Incident inc: openIncidents){
            inc.setState(Incident.State.CLOSED_STATE);
            inc.setCloseDate(new Date());
            inc.setClosedBy("Javi");
            inc.setSolution("Solution"+inc.getId().toString());
            validateBean(inc);
        }
        openIncidents=query.setParameter("state",Incident.State.OPENED_STATE).getResultList();
        utx.commit();
        Assert.assertTrue("Opened incidents still exist.", openIncidents.size()==0);
    }
    
    /**
     * Tests to find by id and remove operations over entity incident
     * @throws javax.transaction.Exception
     */
    @Test
    @InSequence(14)
    public void testIncidentFindAndRemove()throws Exception{
        //try{
            utx.begin();
            em.joinTransaction();
            for(int i=1;i<=MAX_DATA;i++){
                Incident inc=em.find(Incident.class,i);
                Assert.assertNotNull(inc);
                Assert.assertNotNull(inc.getId());
                em.remove(inc);
            }
            utx.commit();
        /*}catch(Exception e){
            utx.rollback();
        }*/
    }
    
}
