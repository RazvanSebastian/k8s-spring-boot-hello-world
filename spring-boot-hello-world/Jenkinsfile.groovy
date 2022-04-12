def tag="razvan95"

podTemplate(containers: [
    containerTemplate(name: 'maven', image: 'maven:3.3.9-jdk-8-alpine',ttyEnabled: true, command: "cat"),
    containerTemplate(image: 'docker', name: 'docker', command: 'cat', ttyEnabled: true)
    ],
    volumes: [
        hostPathVolume(hostPath: "/var/run/docker.sock", mountPath: "/var/run/docker.sock")
    ],
){
    node(POD_LABEL) {
        stage('Git pull'){
            git "https://github.com/RazvanSebastian/k8s-spring-boot-hello-world.git"
        }
        stage("Clean build and nexus deploy") {
            container("maven") {
                withMaven(globalMavenSettingsConfig: '0a19eaf9-02b5-4d79-a1ce-6e0cb8014541') {
                    sh 'mvn clean install'
                    sh 'mvn deploy'
                }
            }
        }
        stage("Docker container stage"){
            container("docker"){
                stage("Build image"){
                    imageBuild("spring-demo-hello-world", "latest")
                }
                stage("Push image"){
                    withCredentials([usernamePassword(credentialsId: "dockerhub", usernameVariable: "username", passwordVariable: "password")]) {
			            pushToImage("spring-demo-hello-world", "latest", USERNAME, PASSWORD)
                    }
                }
            }
        }
        stage("Kubernetes deploy"){
			sshagent (credentials: ['k8s-sshAgent']) {
				sh 'ssh -o StrictHostKeyChecking=no k8s-user@192.168.1.101 /home/k8s-deplyoment-files/backend-demo-deployment.sh'
			}
		}
    }
}

def imageBuild(containerName, tag){
    sh "docker build -t ${containerName}:${tag} ."
}

def pushToImage(containerName, tag, dockerUser, dockerPassword){
    sh "docker login -u ${dockerUser} -p ${dockerPassword}"
    sh "docker tag ${containerName}:${tag} ${dockerUser}/${containerName}:${tag}"
    sh "docker push ${dockerUser}/${containerName}:${tag}"
    echo "Image push complete"
}