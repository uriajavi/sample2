package com.mycompany.incidentmanagement.testsuites;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.mycompany.incidentmanagement.ejb.IncidentManagerLocalIT;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import com.mycompany.incidentmanagement.entity.IncidentIT;
import com.mycompany.incidentmanagement.jsfviews.IncidentsViewTest;
import com.mycompany.incidentmanagement.managedbeans.IncidentsViewBeanIT;
/**
 *
 * @author javi
 */
@RunWith(Suite.class)
@Suite.SuiteClasses(value={IncidentIT.class,
                           IncidentManagerLocalIT.class,
                           IncidentsViewBeanIT.class,
                           IncidentsViewTest.class})
public class IncidentTestSuite {
    
}
