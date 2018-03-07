freeStyleJob('mirror_contained_af') {
    displayName('mirror-contained.af')
    description('Mirror github.com/jessfraz/contained.af to g.j3ss.co/contained.af.')
    checkoutRetryCount(3)
    properties {
        githubProjectUrl('https://github.com/jessfraz/contained.af')
        sidebarLinks {
            link('https://git.j3ss.co/contained.af', 'git.j3ss.co/contained.af', 'notepad.png')
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
        shell('git clone --mirror https://github.com/jessfraz/contained.af.git repo')
        shell('cd repo && git push --mirror ssh://git@g.j3ss.co:2200/~/contained.af.git')
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
