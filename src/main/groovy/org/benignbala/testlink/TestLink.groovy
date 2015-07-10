package org.benignbala.testlink;

import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionType;
import br.eti.kinoshita.testlinkjavaapi.constants.TestCaseStepAction;
import br.eti.kinoshita.testlinkjavaapi.constants.TestImportance;
import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
import br.eti.kinoshita.testlinkjavaapi.model.*;
import br.eti.kinoshita.testlinkjavaapi.util.TestLinkAPIException;

class TestLink {
    def api = null;
    TestLink(server, key) {
        try {
            def url = server.toURL()
            api = new TestLinkAPI(url, key)
        }
        catch (TestLinkAPIException e) {
            println ("Error initialising the API")
            System.exit(2)
        }
    }
  
    def getProject(String name) {
        def project = api.getTestProjectByName(name)
        return project
    }

    def getFirstLevelTestSuitesForTestProject(projId) {
        return api.getFirstLevelTestSuitesForTestProject(projId)
    }

    def getTestSuitesForTestSuite(suite) {
        return api.getTestSuitesForTestSuite(suite)
    }

    def getTestPlanByName(String testPlan, String project) {
        try {
            return api.getTestPlanByName(testPlan, project)
        } catch (TestLinkAPIException e) {
            println "Could not fetch TestPlan with name: " + testPlan
            System.exit(2)
        }
    }
    
    def createTestPlan(String planName, String projectName, String notes = null) {
        try {
            if (notes == null) {
                notes = "No notes specified"
            }
            def tp = api.createTestPlan(planName, projectName, notes, true, true)
            return tp.getId()
        } catch (TestLinkAPIException e) {
            println "Error creating a test plan"
        }
    }
    
    def createBuild(tlPlan, buildNumber) {
        def buildNotes = "No notes added"
        try {
            api.createBuild(tlPlan, buildNumber, buildNotes)
        } catch (TestLinkAPIException e) {
            println "Could not create build: " + buildNumber
        }
    }
    
    def createTestCase(testProject, testSuite, testcases) {
        testcases.each() { tc ->
            println tc
            def steps = new ArrayList<TestCaseStep>();
            int i = 1;
            tc.get('Steps').each() { stp ->
                println stp
                def step = new TestCaseStep();
                step.setNumber(i)
                i += 1
                step.setActions(stp.get('Action'))
                step.setExpectedResults(stp.get('Expected', ""))
                step.setExecutionType(ExecutionType.MANUAL)
                steps.add(step)
            }
            println "Processed all steps"
            println "Steps: " + steps
            api.createTestCase(tc.get('Title', 'Bloody hell!! No title!!!'), //Title
                               testSuite,               // TestSuite ID
                               testProject.getId(),     // TestPorject ID
                               "wstest",                // Author ID
                               tc.get('Title', 'Summary'), // Summary
                               steps,                   // Test steps 
                               "",                      // Test preconditions
                               TestImportance.HIGH,     // Test Importance
                               ExecutionType.MANUAL,    // Test Execution type
                               new Integer(10),         // Order ???
                               null,                    // Internal ID
                               null,                    // check Duplicated Name 
                               null)
        }
    }
}
