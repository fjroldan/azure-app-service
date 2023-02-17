import groovy.json.JsonSlurper

pipeline{
    agent { label 'production_agent' }
    environment {
        ARM = credentials('azure_pipeline')
    }
    stages{
        stage('Deploy'){
            steps {
                script { 
                    
                    def resourceGroup = "myresourcegroup-64471"
                    def webAppName = "webapp-64471"
                    
                    // login Azure
                    withCredentials([usernamePassword(credentialsId: "demo-avv", passwordVariable: "${ARM_CLIENT_SECRET}", usernameVariable: "${ARM_CLIENT_ID}")]) {
                        sh '''
                        az login --service-principal -u $AZURE_CLIENT_ID -p $AZURE_CLIENT_SECRET -t $AZURE_TENANT_ID
                        az account set -s $AZURE_SUBSCRIPTION_ID
                        '''
                    }

                    // get publish settings
                    def pubProfilesJson = sh script: "az webapp deployment list-publishing-profiles -g $resourceGroup -n $webAppName", returnStdout: true
                    def ftpProfile = getFtpPublishProfile pubProfilesJson

                    // upload package
                    sh "curl -T target/calculator-1.0.war $ftpProfile.url/webapps/ROOT.war -u '$ftpProfile.username:$ftpProfile.password'"

                    // log out
                    sh 'az logout'
                }
            }
        }
    }
}

def getFtpPublishProfile(def publishProfilesJson) {
  def pubProfiles = new JsonSlurper().parseText(publishProfilesJson)
  for (p in pubProfiles)
    if (p['publishMethod'] == 'FTP')
      return [url: p.publishUrl, username: p.userName, password: p.userPWD]
}