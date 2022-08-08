pipeline{
    agent any
    tools {
        maven "MAVEN"
        jdk "JDK"
    }
    stages{
        stage('Initialization process'){
            steps{
                echo "PATH = ${M2_HOME}/bin:${PATH}"
                echo "M2_HOME = /opt/maven"
            }
        }
        stage('Cloning of git repository'){
            steps{
                git([url: 'https://github.com/eberetech/Account_microservice.git', branch: 'main', credentialsId: 'Github credentials'])

            }
        }
        stage('Building of the java source code'){
            steps{
                sh 'mvn clean test package'
            }
        }
    }
    post{
        always{
            junit(
                allowEmptyResults: false,
                testResults: '*/test-reports/.xml'
            )
        }
    }
}



