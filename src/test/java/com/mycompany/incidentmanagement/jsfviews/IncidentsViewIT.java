/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.incidentmanagement.jsfviews;

import com.mycompany.incidentmanagement.ejb.IncidentManager;
import com.mycompany.incidentmanagement.ejb.IncidentManagerLocal;
import com.mycompany.incidentmanagement.entity.Incident;
import com.mycompany.incidentmanagement.exception.IncidentDataException;
import com.mycompany.incidentmanagement.managedbeans.IncidentsViewBean;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import javax.inject.Inject;
import junit.framework.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import static org.jboss.arquillian.graphene.Graphene.guardAjax;
import static org.jboss.arquillian.graphene.Graphene.guardHttp;
import static org.jboss.arquillian.graphene.Graphene.guardNoRequest;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.jsfunit.api.InitialPage;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

/**
 * Integration test for IncidentsView.
 * @author javi
 */
@RunWith(Arquillian.class)
@InitialPage("/incidents.jsf")
public class IncidentsViewIT {
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
                .addAsResource("META-INF/persistence.xml")
                .addAsWebResource(new File("src/main/webapp/resources/css/bootstrap.min.css"))
                .addAsWebResource(new File("src/main/webapp/resources/js/bootstrap.min.js"))
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/faces-config.xml"))
                .addAsWebResource(new File("src/main/webapp/incidents.xhtml"));

    }   
    //Graphene testing objects
    @Drone
    private WebDriver browser;
    @ArquillianResource
    private URL deploymentUrl;
    @FindBy(id="findIncidents")
    private WebElement findIncidents;
    @FindBy(id="description")
    private WebElement description;
    @FindBy(id="openedBy")
    private WebElement openedBy;
    @FindBy(id="addIncident")
    private WebElement addIncident;
    @FindBy(id="incidentsTable")
    private WebElement incidentsTable;
    @FindBy(id="solution")
    private WebElement solution;
    @FindBy(id="closedBy")
    private WebElement closedBy;
    @FindBy(id="closeIncident")
    private WebElement closeIncident;
    @FindBy(id="firstPage")
    private WebElement firstPage;
    @FindBy(id="previousPage")
    private WebElement previousPage;
    @FindBy(id="nextPage")
    private WebElement nextPage;
    @FindBy(id="lastPage")
    private WebElement lastPage;
    @FindBy(id="pageSize")
    private WebElement pageSize;

    @Before
    public void initTest(){
        //gets the page.
        browser.get(deploymentUrl.toExternalForm() + "incidents.jsf");
    }
    /**
     * Tests selections on find incidents combo box.
     * @throws IOException 
     */
    @Test
    @InSequence(1)
    @RunAsClient
    public void testFindIncidentsSelection() throws IOException{
        //clicks on options in findIncidents
        Select incidentsCombo=new Select(findIncidents);
        for(WebElement op:incidentsCombo.getOptions())
            if(op.getText().equals("Opened"))
                guardAjax(op).click();
        Assert.assertEquals("Opened",
                incidentsCombo.getFirstSelectedOption().getText());
        
        for(WebElement op:incidentsCombo.getOptions())
            if(op.getText().equals("All"))
                guardAjax(op).click();
        Assert.assertEquals("All",
                incidentsCombo.getFirstSelectedOption().getText());
        
        for(WebElement op:incidentsCombo.getOptions())
            if(op.getText().equals("Closed"))
                guardAjax(op).click();
        Assert.assertEquals("Closed",
                incidentsCombo.getFirstSelectedOption().getText());
    }
    /**
     * Test incidents openings.
     */
    @Test
    @InSequence(2)
    @RunAsClient
    public void testOpenIncident(){
        int newIncidentsCount=10;
        //sets page size
        //Select pageSizeCombo=new Select(pageSize);
        //guardAjax(pageSizeCombo).selectByValue("10");
        Select incidentsCombo=new Select(findIncidents);
        guardAjax(incidentsCombo).selectByValue("OPENED");
        for (int i=1;i<=newIncidentsCount;i++){
            description.clear();
            openedBy.clear();
            String descriptionValue=String.valueOf(i);
            description.sendKeys(descriptionValue);
            openedBy.sendKeys("Automated Tester");
            guardHttp(addIncident).click();
            Assert.assertTrue("New incident description not found in table!", 
                               this.findValueInTable(incidentsTable, descriptionValue));
        }
    }
    /**
     * Tests an incident closing.
     */
    @Test
    @InSequence(3)
    @RunAsClient
    public void testCloseIncident(){
        //sets page size
        //Select pageSizeCombo=new Select(pageSize);
        //guardAjax(pageSizeCombo).selectByValue("10");
        //gets opened incidents
        Select incidentsCombo=new Select(findIncidents);
        guardAjax(incidentsCombo).selectByValue("CLOSED");
        guardAjax(incidentsCombo).selectByValue("OPENED");
        //gets all the checkboxes for selecting incidents
        List<WebElement> idChecks=incidentsTable.findElements(By.tagName("input"));
        if(idChecks.size()>0){
            //selects the last incident 
            idChecks.get(idChecks.size()-1).click();
            //stores the id of the selected incident
            String id=incidentsTable.findElement(By.id("incidentsTable:"+(idChecks.size()-1)+":idValue"))
                                    .getText();
            //sends data to the closing incident entry fields
            solution.sendKeys("Solution for incident.");
            closedBy.sendKeys("Automated tester.");
            //clicks and waits for response
            guardHttp(closeIncident).click();
            //checks that incident has changed its state
            guardAjax(incidentsCombo).selectByValue("CLOSED");
            Assert.assertTrue("Closed incident not found!",
                              this.findValueInTable(incidentsTable, id));
        }
        else Assert.fail("There are no OPENED incidents that can be closed! ");
    }
    /**
     * Tests paging controls. 
     * Note that the test depends on the data previously added
     * in the testOpenIncident test method.
     */
    @Test(expected=NoSuchElementException.class)
    @InSequence(4)
    @RunAsClient
    public void testFirstPageHasNotFirstPageButton(){
        //sets page size
        Select pageSizeCombo=new Select(pageSize);
        guardAjax(pageSizeCombo).selectByValue("3");
        //shows all incidents
        Select incidentsCombo=new Select(findIncidents);
        guardAjax(incidentsCombo).selectByValue("ALL");
        //test that we are in first page
        Assert.assertTrue("Not in first page!",
                           this.findValueInTable(incidentsTable, "1"));
        //in first page, first and prev buttons are not rendered
        firstPage.click();
        Assert.fail("There is a first page button on first data page!");
    }

    @Test(expected=NoSuchElementException.class)
    @InSequence(5)
    @RunAsClient
    public void testFirstPageHasNotPrevPageButton(){
        Assert.assertTrue("Not in first page!",
                           this.findValueInTable(incidentsTable, "1"));
        //in first page, first and prev buttons are not rendered
        guardNoRequest(previousPage).click();
        Assert.fail("There is a previous page button on first data page!");
    }
    
    @Test
    @InSequence(6)
    @RunAsClient
    public void testFirstPageGoesToNextPage(){
        Assert.assertTrue("Not in first page!",
                           this.findValueInTable(incidentsTable, "1"));
        guardHttp(nextPage).click();
        //test that we are in second page
        Assert.assertTrue("Not in second page!",
                           this.findValueInTable(incidentsTable, "4"));
    }

    @Test
    @InSequence(7)
    @RunAsClient
    public void testPageGoesToLastPage(){
        guardHttp(lastPage).click();
        //test that we are in last page
        Assert.assertTrue("Not in last page!",
                           this.findValueInTable(incidentsTable, "10"));
    }

    @Test(expected=NoSuchElementException.class)
    @InSequence(8)
    @RunAsClient
    public void testLastPageHasNotLastPageButton(){
        Assert.assertTrue("Not in last page!",
                           this.findValueInTable(incidentsTable, "10"));
        guardNoRequest(lastPage).click();
        Assert.fail("There is a last page button on last data page!");
    }

    @Test(expected=NoSuchElementException.class)
    @InSequence(9)
    @RunAsClient
    public void testLastPageHasNotNextPageButton(){
        Assert.assertTrue("Not in last page!",
                           this.findValueInTable(incidentsTable, "10"));
        guardNoRequest(nextPage).click();
        Assert.fail("There is a next page button on last data page!");
    }
    
    @Test
    @InSequence(10)
    @RunAsClient
    public void testLastPageGoesToPrevPage(){
        Assert.assertTrue("Not in last page!",
                           this.findValueInTable(incidentsTable, "10"));
        guardHttp(previousPage).click();
        //test that we are in second page
        Assert.assertTrue("Not in prev page!",
                           this.findValueInTable(incidentsTable, "7"));
    }
    
    @Test
    @InSequence(11)
    @RunAsClient
    public void testPageGoesToFirstPage(){
        guardHttp(firstPage).click();
        //test that we are in first page
        Assert.assertTrue("Not in first page!",
                           this.findValueInTable(incidentsTable, "1"));
    }
    /**
     * Finds a value in a table.
     * @param table 
     * @param value
     * @return 
     */        
    private Boolean findValueInTable(WebElement table,String value){
        Boolean returnValue=false;
        List<WebElement> rows=table.findElements(By.tagName("tr"));
        for(WebElement row:rows){
            List<WebElement> cells=row.findElements(By.tagName("td"));
            for(WebElement cell:cells){
                if(cell.getText().equals(value)){
                    returnValue=true;
                    break;
                }
            }
            if(returnValue)break;
        }
        return returnValue;
    }
}