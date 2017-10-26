package io.chumps.orchestra

import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{entity, _}
import autowire.Core
import io.circe.generic.auto._

import io.chumps.orchestra.route.BackendRoutes
import io.chumps.orchestra.utils.AkkaImplicits._
import io.chumps.orchestra.job.JobRunner

trait Jobs extends JVMApp with BackendRoutes {

  def jobs: Seq[JobRunner[_, _]]

  override lazy val routes = super.routes ~
    pathPrefix(Jobs.apiSegment) {
      pathPrefix(Jobs.jobSegment) {
        jobs.map(_.apiRoute).reduce(_ ~ _)
      } ~
        path(Jobs.commonSegment / Segments) { segments =>
          entity(as[String]) { entity =>
            val body = AutowireServer.read[Map[String, String]](entity)
            val request = AutowireServer.route[CommonApi](CommonApiServer)(Core.Request(segments, body))
            onSuccess(request)(complete(_))
          }
        }
    }

  override def main(args: Array[String]): Unit = {
    super.main(args)

    OrchestraConfig.runInfo.fold[Unit] {
      Http().bindAndHandle(routes, "0.0.0.0", OrchestraConfig.port)
    } { runInfo =>
      jobs
        .find(_.job.id == runInfo.job.id)
        .getOrElse(throw new IllegalArgumentException(s"No job found for id ${runInfo.job.id}"))
        .run(runInfo)
    }
  }
}

object Jobs {
  val apiSegment = "api"
  val jobSegment = "job"
  val commonSegment = "common"
}
