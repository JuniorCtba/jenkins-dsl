freeStyleJob('mirror_azure_terraform_mesos') {
    displayName('mirror-azure-terraform-mesos')
    description('Mirror github.com/jessfraz/azure-terraform-mesos to g.j3ss.co/azure-terraform-mesos.')
    checkoutRetryCount(3)
    properties {
        githubProjectUrl('https://github.com/jessfraz/azure-terraform-mesos')
        sidebarLinks {
            link('https://git.j3ss.co/azure-terraform-mesos', 'git.j3ss.co/azure-terraform-mesos', 'notepad.png')
        }
    }
    logRotator {
        numToKeep(100)
        daysToKeep(15)
    }
    triggers {
        cron('H H * * *')
    }
    wrappers { colorizeOutput() }
    steps {
        shell('git clone --mirror https://github.com/jessfraz/azure-terraform-mesos.git repo')
        shell('cd repo && git push --mirror ssh://git@g.j3ss.co:2200/~/azure-terraform-mesos.git')
    }
    publishers {
        extendedEmail {
            recipientList('$DEFAULT_RECIPIENTS')
            contentType('text/plain')
            triggers {
                stillFailing {
                    attachBuildLog(true)
                }
            }
        }
        wsCleanup()
    }
}
