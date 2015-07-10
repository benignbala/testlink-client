package org.benignbala.testlink;

import groovy.util.CliBuilder;
import org.yaml.snakeyaml.*;

class Main {
    public static void main(String[] args) {
        Client c = new Client(args)
    }
}

class Client {
    def cli = null
    def tl = null;
    def cfg = null;
    def config = null;
    Client(args) {
        cli = new CliBuilder(usage: 'tlclient -c testcases.yaml')
        cli.with {
            c longOpt: 'create', args: 1, argName: 'inputFile', 'Create testcases from the inputFile'
            f longOpt: 'file', args: 1, argName: 'configFile', 'The config file'
            b longOpt: 'build', args: 1, argName: 'buildNumber', 'The build number to be added'
            p longOpt: 'plan', args: 1, argName: 'testPlan', 'The name of the testplan'
        }
        def options = cli.parse(args)
        if (options.f) {
            println(options.f)
            cfg = new ConfigStore(options.f)
            config = cfg.getConfig()
        } else {
            println ("No configuration file provided!!!")
            System.exit(1)
        }
        tl = new TestLink(config.get('Server'), config.get('APIKey'))

        if (options.c) {
            createTC(config.get('tlProject'), config.get('tlSuite'), options.c)
        }

        if (options.b) {
            if (options.p) {
                cfg.setTestPlan(options.p)
                if (options.r) {
                    run()
                } else {
                    createBuild(options.b)
                }
            } else {
                System.println "You need to pass the -p planName param to add a build"
                System.exit(1)
            }
        }

        if (options.p) {
            if (config['tlOperation'] == 'AddTP') {
                createTP(options.p)
            }
        }
    }
    
    def createTC(testProject, testSuite, filename) {
        def tcReader = new TestCaseReader(filename)
        def testcases = tcReader.getAllTestCases()
        println testcases
        tl.createTestCase(testProject, testSuite, testcases)
        println "Done."
    }

    def createBuild(buildNumber) {
        tl.createBuild(config.get('tlPlan').getId(), buildNumber)
    }

    def createTP(planName) {
        tl.createTestPlan(planName, config.get('tlProject').getName())
    }

    def run() {
    }
}
