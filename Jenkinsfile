#!/usr/bin/env groovy
@Library('reboot-jenkins-shared-libs') _

pipeline {
  agent { node { label 'gradle-ansible28-jdk11' } }

  environment {
    APP_NAME = 'toepen'
    ARTIFACTORY_USERNAME = 'jenkins-reboot'
    ARTIFACTORY_PASSWORD = credentials('jenkins-artifactory-password')
  }

  stages {
    stage('Init') {
      steps {
        timestamps {
          sh "git clean -xdf"
          script {
            commitId = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
          }
        }
      }
    }

    stage('Jar') {
      steps {
        timestamps {
          sh 'unset JAVA_TOOL_OPTIONS && ./mvnw package'
        }
      }
    }

    stage('Install') {
      steps {
          runAnsible 'bld'
      }
    }

    stage('Build Image') {
      steps {
        timestamps {
          sh "mkdir -p s2i-files"
          sh "rm -rf s2i-files/*.jar"
          sh "ls -l target/*-shaded.jar"
          sh "cp target/*-shaded.jar s2i-files"
          script {
              openshift.withCluster() {openshift.startBuild( APP_NAME, '--from-dir=s2i-files/', '--wait')}
          }
        }
      }
    }

      stage('To Tst') {
          steps {
              runAnsible 'tst'
              deployApplication '${APP_NAME}', 'latest', 'tst'
          }
      }


  }
}
