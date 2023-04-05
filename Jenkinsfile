// If is a merge to master.
if (env.BRANCH_NAME == "master") {
    ciKubernetesDeploy {
        serviceNamespace = "<replace_space_name_here>"
        jobName = "circuit-breaker-poc"
        betaFeatures = [
            obk: true
        ]
        cacheVolumes = [
            'Dockerfile-tests': [
                'gradle': '/home/gradle/.gradle'
            ],
            'Dockerfile': [
                'gradle': '/home/gradle/.gradle'
            ]
        ]
    }
} 

if (env.BRANCH_NAME == "hml") {
    ciKubernetesDeploy {
        serviceNamespace = "<replace_space_name_here>"
        jobName = "circuit-breaker-poc"
        betaFeatures = [
            obk: true
        ]
        cacheVolumes = [
            'Dockerfile-tests': [
                'gradle': '/home/gradle/.gradle'
            ],
            'Dockerfile': [
                'gradle': '/home/gradle/.gradle'
            ]
        ]
    }
} 

// If is a pull request.
if (env.CHANGE_ID) {
    ciRunTests {
        serviceNamespace = "<replace_space_name_here>"
        jobName = "circuit-breaker-poc"
        cacheVolumes = [
            'Dockerfile-tests': [
                'gradle': '/home/gradle/.gradle'
            ]
        ]
    }
}
