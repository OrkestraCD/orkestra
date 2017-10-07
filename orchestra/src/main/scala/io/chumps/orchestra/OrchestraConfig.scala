package io.chumps.orchestra

import java.nio.file.Paths
import java.util.UUID

import io.circe.parser._

object OrchestraConfig {
  def apply(envVar: String) = Option(System.getenv(s"ORCHESTRA_$envVar")).filter(_.nonEmpty)

  val workspace = OrchestraConfig("WORKSPACE").getOrElse("/opt/docker/workspace")
  val home = OrchestraConfig("DATA").getOrElse(System.getProperty("user.home"))
  lazy val port =
    OrchestraConfig("PORT").map(_.toInt).getOrElse(throw new IllegalStateException("ORCHESTRA_PORT should be set"))
  lazy val githubPort = OrchestraConfig("GITHUB_PORT")
    .map(_.toInt)
    .getOrElse(throw new IllegalStateException("ORCHESTRA_GITHUB_PORT should be set"))
  val runInfo = OrchestraConfig("RUN_INFO").map(decode[RunInfo](_).fold(throw _, identity))
  val kubeUri =
    OrchestraConfig("KUBE_URI").getOrElse(throw new IllegalStateException("ORCHESTRA_KUBE_URI should be set"))
  val podName =
    OrchestraConfig("POD_NAME").getOrElse(throw new IllegalStateException("ORCHESTRA_POD_NAME should be set"))
  val namespace =
    OrchestraConfig("NAMESPACE").getOrElse(throw new IllegalStateException("ORCHESTRA_NAMESPACE should be set"))
  lazy val jobUid =
    OrchestraConfig("JOB_UID").getOrElse(throw new IllegalStateException("ORCHESTRA_JOB_UID should be set"))

  val jobsDirName = "jobs"
  val runsDirName = "runs"
  val logsDirName = "logs"
  def logsDir(runId: UUID) = Paths.get(home, logsDirName, runId.toString)
  def logsFile(runId: UUID) = Paths.get(logsDir(runId).toString, "logs")
  def jobDir(jobId: Symbol) = Paths.get(home, jobsDirName, jobId.name)
  def runsDir(jobId: Symbol) = Paths.get(jobDir(jobId).toString, runsDirName)
  def runDir(runInfo: RunInfo) = Paths.get(runsDir(runInfo.jobId).toString, runInfo.runId.toString)
  def runsDirByDate(jobId: Symbol) = Paths.get(jobDir(jobId).toString, "runsByDate")
  def tagsDir(jobId: Symbol) = Paths.get(jobDir(jobId).toString, "tags")
  def tagDir(jobId: Symbol, tag: String) = Paths.get(tagsDir(jobId).toString, tag)
  def statusFile(runInfo: RunInfo) = Paths.get(runDir(runInfo).toString, "status")
  def paramsFile(runInfo: RunInfo) = Paths.get(runDir(runInfo).toString, "params")
}