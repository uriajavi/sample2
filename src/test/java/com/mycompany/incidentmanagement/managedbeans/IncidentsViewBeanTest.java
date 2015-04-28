/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.incidentmanagement.managedbeans;

import junit.framework.Assert;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

/**
 * Tester for IncidentsViewBean.
 * @author javi
 */
public class IncidentsViewBeanTest {
    
    private IncidentsViewBean bean;
    
    public IncidentsViewBeanTest() {
        bean=new IncidentsViewBean();
    }
    
    /**
     * Tests getters and setters for the managed bean
     */
    @Test
    public void testSetGetDescription() {
        String value=RandomStringUtils.randomAlphanumeric(255);
        bean.setDescription(value);
        Assert.assertEquals("Description getter/setter not working!", 
                            value,
                            bean.getDescription());
    }
    
    @Test
    public void testSetGetOpenedBy() {
        String value=RandomStringUtils.randomAlphanumeric(255);
        bean.setOpenedBy(value);
        Assert.assertEquals("OpenedBy getter/setter not working!", 
                            value,
                            bean.getOpenedBy());
    }
        
    @Test
    public void testSetGetClosedBy() {
        String value=RandomStringUtils.randomAlphanumeric(255);
        bean.setClosedBy(value);
        Assert.assertEquals("ClosedBy getter/setter not working!", 
                            value,
                            bean.getClosedBy());
    }
        
    @Test
    public void testSetGetSolution() {
        String value=RandomStringUtils.randomAlphanumeric(255);
        bean.setSolution(value);
        Assert.assertEquals("Solution getter/setter not working!", 
                            value,
                            bean.getSolution());
    }

    @Test
    public void testSetGetShowingCriterion() {
        bean.setShowingCriterion(IncidentsViewBean.Criterion.ALL);
        Assert.assertEquals("ShowingCriterion getter/setter not working!", 
                            IncidentsViewBean.Criterion.ALL,
                            bean.getShowingCriterion());
        bean.setShowingCriterion(IncidentsViewBean.Criterion.CLOSED);
        Assert.assertEquals("ShowingCriterion getter/setter not working!", 
                            IncidentsViewBean.Criterion.CLOSED,
                            bean.getShowingCriterion());
        bean.setShowingCriterion(IncidentsViewBean.Criterion.OPENED);
        Assert.assertEquals("ShowingCriterion getter/setter not working!", 
                            IncidentsViewBean.Criterion.OPENED,
                            bean.getShowingCriterion());
    }

}
