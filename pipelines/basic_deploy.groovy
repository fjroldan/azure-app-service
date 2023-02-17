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

                    sh "echo '[INFO]: LS'"
                    sh "ls"

                    sh "tar -cvf TodoApi.tar TodoApi"

                    sh "echo '[INFO]: LS'"
                    sh "ls"
                    
                    /*azureWebAppPublish azureCredentialsId: 'azure_pipeline', publishType: 'file',
                       resourceGroup: resourceGroup, appName: webAppName,
                       filePath: 'TodoApi.tar', sourceDirectory: '.', targetDirectory: 'webapps'*/

                    
                    // login Azure
                    sh '''
                    az login --service-principal -u ${ARM_CLIENT_ID} -p ${ARM_CLIENT_SECRET} -t ${ARM_TENANT_ID}
                    az account set -s ${ARM_SUBSCRIPTION_ID}
                    '''

                    // get publish settings
                    def pubProfilesJson = sh script: "az webapp deployment list-publishing-profiles -g $resourceGroup -n $webAppName", returnStdout: true
                    def ftpProfile = getFtpPublishProfile pubProfilesJson

                    // upload package
                    sh "curl -T TodoApi.tar $ftpProfile.url -u '$ftpProfile.username:$ftpProfile.password'"

                    
                    /*sh '''
                    ftp -n <<EOF
                    open waws-prod-am2-533.ftp.azurewebsites.windows.net
                    user '$ftpProfile.username' '$ftpProfile.password'
                    put TodoApi.tar
                    EOF
                    '''*/
                    
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