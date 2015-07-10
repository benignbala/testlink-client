package org.benignbala.testlink;

import org.yaml.snakeyaml.*;

class ConfigStore {
    def config = [:]
    def ops = ['AddTC', 'AddTS', 'AddTP', 'AddB', 'RunTP', 'RunTC']
    def tl = null;
    Integer projId = 0
    def testSuite = 0
    def testPlan = 0
    def tlOperation = null
    ConfigStore(configFile) {
        File cfg = new File(configFile)
        DataInputStream d = cfg.newDataInputStream()
        def yaml = new Yaml()
        config = yaml.load(d)
        println config
        tl = new TestLink(config.get('Server'), config.get('APIKey'))
        tlOperation = config.get('Operation')
        setProject()
        if (tlOperation == "AddTC") {
            setTestSuite()
        }
        // if (tlOperation in ['AddB', 'RunTP']) {
        //     setTestPlan()
        // }
    }

    def setProject() {
        config['tlProject'] = tl.getProject(config.get('Project'))
        projId = config.get('tlProject').getId()
    }

    def setTestSuite() {
        def suitePath = config.get('Testsuite')
        def suites = new LinkedList<String>();
        suitePath.tokenize('/').each() {s ->
            suites.add(s)
        }
        def topLevelSuites = tl.getFirstLevelTestSuitesForTestProject(projId)
        def s = null
        topLevelSuites.find {
            if (it.getName() == suites[0]) {
                s = it
                suites.poll()
                return true
            }
            return false
        }
        def testSuites;
        while (suites.size()) {
            testSuites = tl.getTestSuitesForTestSuite(s.getId())
            testSuites.find {
                if (it.getName() == suites[0]) {
                    s = it
                    suites.poll()
                    return true
                }
                return false
            }
        }
        testSuite = s.getId()
        config['tlSuite'] = testSuite
        // println "Test Suite: " + testSuite
    }

    def setTestPlan(planName) {
        if (planName == null) {
            println "Operation " + tlOperation + " needs a TestPlan"
            System.exit(1)
        }
        def testPlan = tl.getTestPlanByName(planName, config.get('tlProject').getName())
        config['tlPlan'] = testPlan
    }
    
    def getConfig() {
        return config
    }

    def getConfig(String cfg) {
        return config.get(cfg)
    }
}
