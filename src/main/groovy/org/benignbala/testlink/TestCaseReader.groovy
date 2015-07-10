package org.benignbala.testlink;

import org.yaml.snakeyaml.*;

class TestCaseReader {
    def testcases = null;
    TestCaseReader(sourceFile) {
        File tc = new File(sourceFile)
        DataInputStream d = tc.newDataInputStream()
        def yaml = new Yaml()
        testcases = yaml.loadAll(d)
    }

    def getAllTestCases() {
        return testcases
    }
}

        
