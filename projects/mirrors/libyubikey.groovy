freeStyleJob('mirror_libyubikey') {
    displayName('mirror-libyubikey')
    description('Mirror github.com/jessfraz/libyubikey to g.j3ss.co/libyubikey.')
    checkoutRetryCount(3)
    properties {
        githubProjectUrl('https://github.com/jessfraz/libyubikey')
        sidebarLinks {
            link('https://git.j3ss.co/libyubikey', 'git.j3ss.co/libyubikey', 'notepad.png')
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
        shell('git clone --mirror https://github.com/jessfraz/libyubikey.git repo')
        shell('cd repo && git push --mirror ssh://git@g.j3ss.co:2200/~/libyubikey.git')
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
