<img alt="Orkestra" src="https://raw.githubusercontent.com/orkestra-tech/orkestra/master/docs/src/main/resources/microsite/img/orkestra.png" srcset="https://raw.githubusercontent.com/orkestra-tech/orkestra/master/docs/src/main/resources/microsite/img/orkestra.png 2x">

[![Latest version](https://index.scala-lang.org/orkestra-tech/orkestra/orkestra-core/latest.svg?color=blue)](https://index.scala-lang.org/orkestra-tech/orkestra/orkestra-core)
[![Gitter](https://img.shields.io/badge/gitter-join%20chat-green.svg)](https://gitter.im/Orkestra-Tech/orkestra)

Orkestra is an Open Source Continuous Integration / Continuous Deployment server as a library running on
[Kubernetes](https://kubernetes.io).  
It leverages Kubernetes concepts such as Jobs or Secrets, and configuration as code in [Scala](https://scala-lang.org)
to take the most of compile time type safety and compatibility with Scala or Java libraries.

Key features:
* Configured completely via code which can be version controlled
* Fully scalable
* Highly Available
* Extendable via any Scala/Java libraries


# Quick start

## Requirements

- [Java](https://java.com/download)
- [SBT](https://scala-sbt.org)
- [Kubernetes](https://kubernetes.io) or [Minikube](https://github.com/kubernetes/minikube)

## Installation

*project/plugins.sbt*:
```scala
addSbtPlugin("tech.orkestra" % "sbt-orkestra" % "<latest version>")
```
*build.sbt*:
```scala
lazy val orkestra = orkestraProject("orkestra", file("orkestra"))
  .settings(
    libraryDependencies ++= Seq(
      "tech.orkestra" %%% "orkestra-github" % orkestraVersion, // Optional Github plugin
      "tech.orkestra" %%% "orkestra-cron" % orkestraVersion, // Optional Cron plugin
      "tech.orkestra" %% "orkestra-lock" % orkestraVersion // Optional Lock plugin
    )
  )
```

## Simple example

Given the above [installation](#installation), here is a minimal project with one job:

*orkestra/src/main/scala/orkestra.scala*:
```tut:silent
import tech.orkestra._
import tech.orkestra.Dsl._
import tech.orkestra.board._
import tech.orkestra.job._
import tech.orkestra.model._

// We extend OrkestraServer to create the web server
object Orkestra extends OrkestraServer {
  // Configuring the UI
  lazy val board = deployFrontendJobBoard
  // Configuring the jobs
  lazy val jobs = Set(deployFrontendJob)
  
  // Creating the job and configuring UI related settings
  lazy val deployFrontendJobBoard = JobBoard[() => Unit](JobId("deployFrontend"), "Deploy Frontend")()
  // Creating the job from the above definition (this will be compiled to JVM)
  lazy val deployFrontendJob = Job(deployFrontendJobBoard) { implicit workDir => () =>
    println("Deploying Frontend")
  }
}
```
This example is described in [Jobs & Boards](https://orkestra.tech/jobsboards.html).

[See example projects](https://github.com/orkestra-tech/orkestra/tree/master/examples)

## Deployment on Kubernetes with Minikube

We provide some basic Kubernetes Deployment in [kubernetes-dev](https://github.com/orkestra-tech/orkestra/tree/master/examples/kubernetes-dev)
that you can use to deploy on a dev environment.  
Assuming that you are in one of the [example projects](https://github.com/orkestra-tech/orkestra/tree/master/examples)
(or in your own project), here is how to deploy on Kubernetes with Minikube:
```
minikube start --memory 4096              # Start Minikube
eval `minikube docker-env`                # Make docker use the docker engine of Minikube
sbt orkestraJVM/Docker/publishLocal       # Publish the docker artifact
kubectl apply -f ../kubernetes-dev        # Apply the deployment to Kubernetes
kubectl proxy                             # Proxy the Kubernetes api
```
Visit Orkestra on `http://127.0.0.1:8001/api/v1/namespaces/orkestra/services/orkestra:http/proxy`.  
You can troubleshoot any deployment issue with `minikube dashboard`.

More on how to configure the deployment in [Config](https://orkestra.tech/config.html).


# Documentation

Find all the documentation on [https://orkestra.tech](https://orkestra.tech):
- [Jobs & Boards](https://orkestra.tech/jobsboards.html)
- [Config](https://orkestra.tech/config.html)
- [Parameters](https://orkestra.tech/parameters.html)
- [Stages](https://orkestra.tech/stages.html)
- [Shell scripts](https://orkestra.tech/shells.html)
- [Directories](https://orkestra.tech/directories.html)
- [Secrets](https://orkestra.tech/secrets.html)
- [Triggering jobs](https://orkestra.tech/triggers.html)
- [RunId](https://orkestra.tech/runid.html)
- [Containers](https://orkestra.tech/containers.html)
- [Plugins](https://orkestra.tech/plugins/)

Talks and articles:
- [*Functional DevOps with Scala and Kubernetes*](https://itnext.io/functional-devops-with-scala-a-kubernetes-3d7c91bca72f) article from [Joan Goyeau](https://twitter.com/JoanG38)
- [*Functional DevOps with Scala and Kubernetes*](https://skillsmatter.com/skillscasts/12343-orchestra-devops-with-scala-and-kubernetes) talk from [Joan Goyeau](https://twitter.com/JoanG38)


# Origins of Orkestra

<a href="https://drivetribe.com"><img alt="DriveTribe" src="https://raw.githubusercontent.com/orkestra-tech/orkestra/master/docs/src/main/resources/microsite/img/drivetribe.png" srcset="https://raw.githubusercontent.com/orkestra-tech/orkestra/master/docs/src/main/resources/microsite/img/drivetribe.png 2x"></a>

Orkestra has been created at [DriveTribe](https://drivetribe.com) by its Scala backend team that had to do DevOps. Obsessed by functional programming they decided to apply the same paradigm to their DevOps.


# Related projects

* [Jenkins](https://jenkins.io)
* [Kubernetes Plugin for Jenkins](https://github.com/jenkinsci/kubernetes-plugin)


# Contributing

Contributions to Orkestra are more than welcomed!
See [CONTRIBUTING.md](https://github.com/orkestra-tech/orkestra/tree/master/CONTRIBUTING.md) for all the information and getting help.
