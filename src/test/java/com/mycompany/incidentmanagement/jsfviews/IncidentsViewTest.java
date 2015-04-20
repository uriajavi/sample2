/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.incidentmanagement.jsfviews;

import java.io.File;
import java.util.List;
import javax.faces.component.UIColumn;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIInput;
import javax.faces.component.UIOutput;
import javax.faces.component.UISelectItem;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.model.DataModel;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.jsfunit.api.InitialPage;
import org.jboss.jsfunit.api.JSFUnitResource;
import org.jboss.jsfunit.jsfsession.JSFClientSession;
import org.jboss.jsfunit.jsfsession.JSFServerSession;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Element;

/**
 * Test class for the incidents view JSF page.
 * @author javi
 */
@RunWith(Arquillian.class)
@InitialPage("/incidents.jsf")
public class IncidentsViewTest {
    @Deployment
    public static WebArchive createDeployment(){
        return ShrinkWrap.create(WebArchive.class, "test.war")
                //.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                //.setWebXML(new File("src/main/webapp/WEB-INF/web.xml"))
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/faces-config.xml"))
                //.addPackage(Package.getPackage("com.mycompany.incidentmanagement.jsfviews"))
                .addAsWebResource(new File("src/main/webapp/incidents.xhtml"));
    }
    private @JSFUnitResource JSFServerSession server;
    private @JSFUnitResource JSFClientSession client;
    /**
    * Tests that the actual view is incidents.xhtml
    */
    @Test  
    public void testGetCurrentViewId()   {  
       Assert.assertEquals("/incidents.xhtml", server.getCurrentViewID());  
       Assert.assertEquals(server.getCurrentViewID(), 
                           server.getFacesContext().getViewRoot().getViewId());  
    }
    /**
     * Tests that the page contains a form element correctly identified.
     * NOTE: we do the test on the client side because the h:form facelets element
     * causes a UnsupportedOperationException during the test!!!
     */
    @Test
    public void testIncidentsViewContainsForm(){
        Element form=client.getElement("incidentsForm");
        Assert.assertEquals("form",form.getTagName());
    }
    /**
     * Tests that the page contains a SELECT element for searching the incidents.
     */
    @Test
    public void testIncidentsViewContainsFindIncidentsSelectComponent(){
        //tests if there is a SelectOne component with id=findIncidents
        UIComponent findIncidentsCB=server.findComponent("findIncidents");
        Assert.assertTrue(findIncidentsCB.isRendered());
        Assert.assertEquals(HtmlSelectOneMenu.class,findIncidentsCB.getClass());
        //tests if the selection component contains the appropriate selection
        //options
        List<UIComponent> options=findIncidentsCB.getChildren();
        Assert.assertTrue("findIncidents items size is incorrect!",options.size()==3);
        for(UIComponent option:options){
            Object value=((UISelectItem)option).getItemValue();
            Assert.assertNotNull("findIncidents value item is null!",value);
            Assert.assertTrue("findIncidents value option not valid!",
                              value.equals("All")||
                              value.equals("Opened")||
                              value.equals("Closed"));
        }
        //tests the label for the selection components
        UIComponent findIncidentsLabel=server.findComponent("findIncidentsLabel");
        Assert.assertTrue(findIncidentsLabel.isRendered());
        Assert.assertEquals("Label for findIncidents is wrong!", "Show:", 
                             ((UIOutput)findIncidentsLabel).getValue());
        Assert.assertEquals(HtmlOutputLabel.class,findIncidentsLabel.getClass());
        //tests if there is a panel containing the previous components
        UIComponent findIncidentsPanel=server.findComponent("findIncidentsPanel");
        Assert.assertEquals("Incidents panel does not contain findIncidents component!",
                            findIncidentsPanel,findIncidentsCB.getParent());
        Assert.assertEquals("Incidents panel does not contain findIncidentsLabel component!",
                            findIncidentsPanel,findIncidentsLabel.getParent());
    }
    /**
     * Tests that the page contains a panel with all the needed components 
     * for opening incidents.
     */
    @Test
    public void testIncidentsViewContainsOpeningIncidentsComponents(){
        //tests if there is an input component with id=description
        UIComponent description=server.findComponent("description");
        Assert.assertTrue(description.isRendered());
        Assert.assertTrue(UIInput.class.isInstance(description));
        //tests if there is an input component with id=openedBy
        UIComponent openedBy=server.findComponent("openedBy");
        Assert.assertTrue(openedBy.isRendered());
        Assert.assertTrue(UIInput.class.isInstance(openedBy));
        //tests the label for the input components
        UIComponent descriptionLabel=server.findComponent("descriptionLabel");
        Assert.assertTrue(descriptionLabel.isRendered());
        Assert.assertEquals("Label for description is wrong!", "Description:", 
                             ((UIOutput)descriptionLabel).getValue());
        Assert.assertTrue("Description label is not an HtmlOutputLabel!",
                            HtmlOutputLabel.class.isInstance(descriptionLabel));
        UIComponent openedByLabel=server.findComponent("openedByLabel");
        Assert.assertTrue(openedByLabel.isRendered());
        Assert.assertEquals("Label for openedBy is wrong!", "Opened by:", 
                             ((UIOutput)openedByLabel).getValue());
        Assert.assertTrue("openedByLabel label is not an HtmlOutputLabel!",
                            HtmlOutputLabel.class.isInstance(openedByLabel));
        //tests that there is an Add command button
        UIComponent addButton=server.findComponent("addIncident");
        Assert.assertTrue(addButton.isRendered());
        Assert.assertTrue(HtmlCommandButton.class.isInstance(addButton));
        //tests if there is a panel containing the previous components
        UIComponent openingsPanel=server.findComponent("openingsPanel");
        Assert.assertEquals("Openings panel does not contain description entry field!",
                            openingsPanel,description.getParent());
        Assert.assertEquals("Openings panel does not contain description label!",
                            openingsPanel,descriptionLabel.getParent());
        Assert.assertEquals("Openings panel does not contain openedBy entry field!",
                            openingsPanel,openedBy.getParent());
        Assert.assertEquals("Openings panel does not contain openedBy label!",
                            openingsPanel,openedByLabel.getParent());
        Assert.assertEquals("Openings panel does not contain add button!",
                            openingsPanel,addButton.getParent());

    }
    /**
     * Tests that the page contains a data table for showing 
     * al the incidents
     */
    @Test
    public void testIncidentsViewContainsDataTable(){
        //tests if there is a data table component with appropriate id.
        UIComponent dataTable=server.findComponent("incidentsTable");
        Assert.assertTrue("incidentsTable not rendered!",dataTable.isRendered());
        Assert.assertTrue("incidentsTable is not a UIData!",UIData.class.isInstance(dataTable));
        //tests if the data table has the appropriate columns.
        this.checkColumn("idColumn","Id");
        this.checkColumn("descriptionColumn", "Description");
        this.checkColumn("openingDateColumn","Opening Date");
        this.checkColumn("openByColumn", "Opened By");
        this.checkColumn("stateColumn","State");
        this.checkColumn("solutionColumn", "Solution");
        this.checkColumn("closingDateColumn","Closing Date");
        this.checkColumn("closedByColumn", "Closed By");
        //tests if the data table contains an appropriate model
        //Assert.assertTrue("incidentsTable does not have an appropriate model!",
        //                  DataModel.class.isInstance(((UIData)dataTable).getValue()));
    }
    /**
     * Checks if a column is correctly rendered.
     * @param id Id of the column to be checked
     * @param text Text of the header for the column
     */
    private void checkColumn(String id,String text){
        //tests if the data table has the appropriate columns.
        UIComponent column=server.findComponent(id);
        Assert.assertTrue(id+" is not rendered!",column.isRendered());
        Assert.assertTrue(id+" is not an UIColumn!",UIColumn.class.isInstance(column));
        List<UIComponent> colChilds=((UIColumn)column).getChildren();
        for(UIComponent child:colChilds){
            Assert.assertTrue("Column "+id+" does not contain an output text!",
                                HtmlOutputText.class.isInstance(child));
        }
        List<UIComponent> colHeaderChilds=((UIColumn)column).getHeader().getChildren();
        for(UIComponent child:colHeaderChilds){
            Assert.assertTrue("Column header for"+id+" does not contain an output text!",
                                HtmlOutputText.class.isInstance(child));
            Assert.assertTrue("Column header for "+id+" does not contain "+text+" text!",
                                ((HtmlOutputText)child).getValue().equals(text));
        }
    }
    /**
     * Tests that the page contains components to paginate incidents data
     */
    @Test
    public void testIncidentsViewContainsDataPaginationComponents(){
        //tests if there is a command component for going to the first page
        UIComponent firstPage=server.findComponent("firstPage");
        Assert.assertTrue("There is not a UICommand for going to first page!",
                            UICommand.class.isInstance(firstPage));
        //tests if there is a command component for going to the last page
        UIComponent lastPage=server.findComponent("lastPage");
        Assert.assertTrue("There is not a UICommand for going to last page!",
                            UICommand.class.isInstance(lastPage));
        //tests if there is a command component for going to the first page
        UIComponent nextPage=server.findComponent("nextPage");
        Assert.assertTrue("There is not a UICommand for going to next page!",
                            UICommand.class.isInstance(nextPage));
        //tests if there is a command component for going to the first page
        UIComponent previousPage=server.findComponent("previousPage");
        Assert.assertTrue("There is not a UICommand for going to previous page!",
                            UICommand.class.isInstance(previousPage));
    }
    /**
     * Tests that the page contains a panel with all the needed components 
     * for closing incidents.
     */
    @Test
    public void testIncidentsViewContainsClosingIncidentsComponents(){
        //tests if there is an input component with id=solution
        UIComponent solution=server.findComponent("solution");
        Assert.assertTrue(solution.isRendered());
        Assert.assertTrue(UIInput.class.isInstance(solution));
        //tests if there is an input component with id=closedBy
        UIComponent closedBy=server.findComponent("closedBy");
        Assert.assertTrue(closedBy.isRendered());
        Assert.assertTrue(UIInput.class.isInstance(closedBy));
        //tests the label for the input components
        UIComponent solutionLabel=server.findComponent("solutionLabel");
        Assert.assertTrue(solutionLabel.isRendered());
        Assert.assertEquals("Label for solution is wrong!", "Solution:", 
                             ((UIOutput)solutionLabel).getValue());
        Assert.assertTrue("Solution label is not an HtmlOutputLabel!",
                            HtmlOutputLabel.class.isInstance(solutionLabel));
        UIComponent closedByLabel=server.findComponent("closedByLabel");
        Assert.assertTrue(closedByLabel.isRendered());
        Assert.assertEquals("Label for closedBy is wrong!", "Closed by:", 
                             ((UIOutput)closedByLabel).getValue());
        Assert.assertTrue("closedByLabel label is not an HtmlOutputLabel!",
                            HtmlOutputLabel.class.isInstance(closedByLabel));
        //tests that there is an Add command button
        UIComponent closeButton=server.findComponent("closeIncident");
        Assert.assertTrue(closeButton.isRendered());
        Assert.assertTrue(HtmlCommandButton.class.isInstance(closeButton));
        //tests if there is a panel containing the previous components
        UIComponent closingsPanel=server.findComponent("closingsPanel");
        Assert.assertEquals("Closings panel does not contain solution entry field!",
                            closingsPanel,solution.getParent());
        Assert.assertEquals("Closings panel does not contain solution label!",
                            closingsPanel,solutionLabel.getParent());
        Assert.assertEquals("Openings panel does not contain closedBy entry field!",
                            closingsPanel,closedBy.getParent());
        Assert.assertEquals("Openings panel does not contain closedBy label!",
                            closingsPanel,closedByLabel.getParent());
        Assert.assertEquals("Openings panel does not contain close button!",
                            closingsPanel,closeButton.getParent());

    }
}
