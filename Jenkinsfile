pipeline {
    agent any

    environment {
        DOCKER_HUB_CREDENTIALS = 'dockerhub_credentials'
        DOCKER_IMAGE = 'mayz412/teedy-app'
        DOCKER_TAG = "${env.BUILD_NUMBER}"
        PATH = "${env.PATH};C:\\Program Files\\Tesseract-OCR"
    }

    stages {
        stage('Clean') {
            steps {
                bat 'mvn clean'
            }
        }
        stage('Compile') {
            steps {
                bat 'mvn compile'
            }
        }
        stage('Test') {
            steps {
                bat 'mvn test -Dmaven.test.failure.ignore=true'
            }
        }
        stage('PMD') {
            steps {
                bat 'mvn pmd:pmd'
            }
        }
        stage('JaCoCo') {
            steps {
                bat 'mvn jacoco:report'
            }
        }
        stage('Javadoc') {
            steps {
                bat 'mvn javadoc:javadoc -Dmaven.javadoc.failOnError=false'
            }
        }
        stage('Site') {
            steps {
                bat 'mvn site'
            }
        }
        stage('Package') {
            steps {
                bat 'mvn package -DskipTests'
            }
        }
        stage('Building image') {
            steps {
                script {
                    docker.build("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}")
                }
            }
        }
        stage('Upload image') {
            steps {
                retry(3) {
                    withCredentials([string(credentialsId: "${DOCKER_HUB_CREDENTIALS}", variable: 'DOCKER_PASS')]) {
                        bat 'docker login -u %DOCKER_USER% -p %DOCKER_PASS%'
                        bat "docker tag %DOCKER_IMAGE%:%DOCKER_TAG% %DOCKER_IMAGE%:latest"
                        bat "docker push %DOCKER_IMAGE%:%DOCKER_TAG%"
                        bat "docker push %DOCKER_IMAGE%:latest"
                    }
                }
            }
        }
        stage('Run containers') {
            steps {
                bat 'docker stop teedy-container-8082 || exit 0'
                bat 'docker rm teedy-container-8082 || exit 0'
                bat 'docker stop teedy-container-8083 || exit 0'
                bat 'docker rm teedy-container-8083 || exit 0'
                bat 'docker stop teedy-container-8084 || exit 0'
                bat 'docker rm teedy-container-8084 || exit 0'
                bat "docker run -d --name teedy-container-8082 -p 8082:8080 %DOCKER_IMAGE%:%DOCKER_TAG%"
                bat "docker run -d --name teedy-container-8083 -p 8083:8080 %DOCKER_IMAGE%:%DOCKER_TAG%"
                bat "docker run -d --name teedy-container-8084 -p 8084:8080 %DOCKER_IMAGE%:%DOCKER_TAG%"
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
            archiveArtifacts artifacts: 'docs-web/target/*.war, docs-core/target/*.jar, target/site/**, */target/site/**', allowEmptyArchive: true
        }
    }
}
