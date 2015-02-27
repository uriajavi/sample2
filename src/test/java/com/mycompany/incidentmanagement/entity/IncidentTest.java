/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.incidentmanagement.entity;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
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
public class IncidentTest {
    @Deployment
    public static WebArchive createDeployment(){
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addClass(Incident.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsResource("META-INF/persistence.xml");
        
    }
    
    @Inject Incident incident;
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
    public void testClosedBySetGet(){
        String closer="New closer";
        incident.setClosedBy(closer);
        Assert.assertEquals(closer, incident.getClosedBy());
    }
}
