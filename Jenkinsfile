pipeline {
    agent any

    environment {
        imagename = "rnjsxorl3075/kite_result"
        registryCredential = 'docker'
        dockerImage = '' 
    }

    stages {

        // gradle build
        stage('Bulid Gradle') {
          agent any
          steps {
            echo 'Bulid Gradle'
            dir ('.'){
                sh """
                chmod +x gradlew
                ./gradlew clean build --exclude-task compileQuerydsl test
                """
            }
          }
          post {
            failure {
              error 'This pipeline stops here...'
            }
          }
        }

        // docker build
        stage('Bulid Docker') {
          agent any
          steps {
            echo 'Bulid Docker'
			sh "echo jenkins | sudo -kS chmod 666 /var/run/docker.sock"
            script {
                  dockerImage = docker.build imagename
            }
          }
          post {
            failure {
              error 'This pipeline stops here...'
            }
          }
        }

        // docker push
        stage('Push Docker') {
          agent any
          steps {
            echo 'Push Docker'
            script {
                docker.withRegistry( '', registryCredential) {
                    dockerImage.push("${currentBuild.number}")
                }
            }
          }
          post {
            failure {
              error 'This pipeline stops here...'
            }
          }
        }

        stage('Deploy to dev') {
	      agent { label 'argocd' }
          steps {

              git credentialsId: 'github',
                      url: 'https://github.com/Guts-Gun/KITe_ArgoCD.git',
                      branch: 'main'

              sh "git pull origin main"
              sh "sed -i 's/kite_result:.*\$/kite_result:${currentBuild.number}/g' service/result-deployment.yaml"
              sh "cat service/result-deployment.yaml"
              sh "git config user.name 'Lab00700'"
              sh "git config user.email 'zerglisk123@naver.com'"
              sh "git add service/result-deployment.yaml"
              sh "git commit -m '[UPDATE] kite_result ${currentBuild.number} image versioning'"

              withCredentials([gitUsernamePassword(credentialsId: 'github')]) {
                sh "git remote set-url origin https://github.com/Guts-Gun/KITe_ArgoCD.git"
                sh "git push origin main"
              }
            }

           post {
                    failure {
                      echo 'Update ????????????'
                    }
                    success {
                      echo 'Update ??????!!!!'
                    }
            }
        }
    }
}
