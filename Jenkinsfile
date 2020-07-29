#!/usr/bin/env groovy

node {
    stage('checkout') {
        checkout scm
    }

    docker.image('jhipster/jhipster:v6.10.1').inside('-u jhipster') {
        stage('check java') {
            sh "java -version"
        }

        stage('quality analysis') {
            withSonarQubeEnv('sonar') {
            }
        }
    }
}
