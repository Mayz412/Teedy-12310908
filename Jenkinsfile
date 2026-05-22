pipeline {
    agent any

    environment {
        DOCKER_HUB_CREDENTIALS = 'dockerhub_credentials'
        DOCKER_IMAGE = 'mayz412/teedy-app'
        DOCKER_TAG = "${env.BUILD_NUMBER}"
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
                script {
                    docker.withRegistry('https://registry.hub.docker.com', DOCKER_HUB_CREDENTIALS) {
                        docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").push()
                        docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").push('latest')
                    }
                }
            }
        }
        stage('Run containers') {
            steps {
                script {
                    bat 'docker stop teedy-container-8082 || exit 0'
                    bat 'docker rm teedy-container-8082 || exit 0'
                    bat 'docker stop teedy-container-8083 || exit 0'
                    bat 'docker rm teedy-container-8083 || exit 0'
                    bat 'docker stop teedy-container-8084 || exit 0'
                    bat 'docker rm teedy-container-8084 || exit 0'
                    docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").run('--name teedy-container-8082 -d -p 8082:8080')
                    docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").run('--name teedy-container-8083 -d -p 8083:8080')
                    docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").run('--name teedy-container-8084 -d -p 8084:8080')
                }
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
