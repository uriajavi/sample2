package com.mycompany.incidentmanagement.testsuites;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.mycompany.incidentmanagement.ejb.IncidentManagerLocalTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import com.mycompany.incidentmanagement.entity.IncidentTest;
import com.mycompany.incidentmanagement.jsfviews.IncidentsViewTest;
/**
 *
 * @author javi
 */
@RunWith(Suite.class)
@Suite.SuiteClasses(value={IncidentTest.class,
                           IncidentManagerLocalTest.class,
                           IncidentsViewTest.class})
public class IncidentTestSuite {
    
}
