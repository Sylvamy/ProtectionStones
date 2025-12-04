pipeline {
    agent any
    
    tools {
        // These names must match what you configured in Jenkins Global Tool Configuration
        maven 'Maven'   // Maven will be auto-downloaded by Jenkins
        jdk 'JDK17'     // JDK will be auto-downloaded by Jenkins
    }
    
    options {
        // Keep only last 10 builds
        buildDiscarder(logRotator(numToKeepStr: '10'))
        // Add timestamps to console output
        timestamps()
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                echo 'Building with Maven...'
                sh 'mvn clean package -DskipTests -Dmaven.javadoc.skip=true'
            }
        }
        
        stage('Test') {
            steps {
                echo 'Running tests...'
                sh 'mvn test'
            }
            post {
                always {
                    // Archive test results if they exist
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Archive') {
            steps {
                echo 'Archiving artifacts...'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
    }
    
    post {
        success {
            echo '✓ Build completed successfully!'
        }
        failure {
            echo '✗ Build failed!'
        }
        always {
            echo 'Cleaning up workspace...'
        }
    }
}
