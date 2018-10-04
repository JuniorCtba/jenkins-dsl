freeStyleJob('contained_af') {
    displayName('contained.af')
    description('Build Dockerfiles in genuinetools/contained.af.')

    concurrentBuild()
    checkoutRetryCount(3)

    properties {
        githubProjectUrl('https://github.com/genuinetools/contained.af')
        sidebarLinks {
            link('https://hub.docker.com/r/jess/contained', 'Docker Hub: jess/contained', 'notepad.png')
            link('https://hub.docker.com/r/jessfraz/contained', 'Docker Hub: jessfraz/contained', 'notepad.png')
            link('https://r.j3ss.co/repo/contained/tags', 'Registry: r.j3ss.co/contained', 'notepad.png')
        }
    }

    logRotator {
        numToKeep(100)
        daysToKeep(15)
    }

    scm {
        git {
            remote {
                url('https://github.com/genuinetools/contained.af.git')
            }
            branches('*/master', '*/tags/*')
            extensions {
                wipeOutWorkspace()
                cleanAfterCheckout()
            }
        }
    }

    triggers {
        cron('H H * * *')
        githubPush()
    }

    wrappers { colorizeOutput() }

    environmentVariables(DOCKER_CONTENT_TRUST: '1')
    steps {
        shell('docker build --rm --force-rm -t r.j3ss.co/contained:latest .')
        shell('docker tag r.j3ss.co/contained:latest jess/contained:latest')
        shell('docker tag r.j3ss.co/contained:latest jessfraz/contained:latest')
        shell('docker push --disable-content-trust=false r.j3ss.co/contained:latest')
        shell('docker push --disable-content-trust=false jess/contained:latest')
        shell('docker push --disable-content-trust=false jessfraz/contained:latest')
        shell('for tag in $(git tag); do git checkout $tag; docker build  --rm --force-rm -t r.j3ss.co/contained:$tag . || true; docker push --disable-content-trust=false r.j3ss.co/contained:$tag || true; done')
        shell('git checkout master')
        shell('docker build --rm --force-rm -f Dockerfile.dind -t r.j3ss.co/docker:userns .')
        shell('docker tag r.j3ss.co/docker:userns jess/docker:userns')
        shell('docker tag r.j3ss.co/docker:userns jessfraz/docker:userns')
        shell('docker push --disable-content-trust=false r.j3ss.co/docker:userns')
    shell('docker push --disable-content-trust=false jess/docker:userns')
    shell('docker push --disable-content-trust=false jessfraz/docker:userns')
        shell('docker rm $(docker ps --filter status=exited -q 2>/dev/null) 2> /dev/null || true')
        shell('docker rmi $(docker images --filter dangling=true -q 2>/dev/null) 2> /dev/null || true')
    }

    publishers {
        retryBuild {
            retryLimit(2)
            fixedDelay(15)
        }

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
