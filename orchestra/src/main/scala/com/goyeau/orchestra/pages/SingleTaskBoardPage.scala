package com.goyeau.orchestra.pages

import java.util.UUID

import scala.concurrent.ExecutionContext

import autowire._
import com.goyeau.orchestra._
import com.goyeau.orchestra.routes.WebRouter.{AppPage, TaskLogsPage}
import io.circe.Encoder
import io.circe.generic.auto._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import shapeless.HList

object SingleTaskBoardPage {

  def component[Params <: HList, ParamValues <: HList: Encoder](
    name: String,
    task: Task.Definition[_, ParamValues],
    params: Params,
    ctrl: RouterCtl[AppPage]
  )(implicit ec: ExecutionContext, paramGetter: ParamGetter[Params, ParamValues]) =
    ScalaComponent
      .builder[Unit](getClass.getSimpleName)
      .initialState {
        val taskInfo = RunInfo(id = UUID.randomUUID())
        (taskInfo, Map[String, Any](RunId.name -> taskInfo.id))
      }
      .render { $ =>
        def runTask = Callback.future {
          task.Api.client.run($.state._1, paramGetter.values(params, $.state._2)).call().map {
            case RunStatus.Running(_) | RunStatus.Success => ctrl.set(TaskLogsPage($.state._1.id))
            case RunStatus.Failed(e) => Callback.alert(e.getMessage)
          }
        }

        val displayState = Displayer.State(kv => $.modState(s => s.copy(_2 = s._2 + kv)), key => $.state._2.get(key))

        <.div(
          <.div(name),
          paramGetter.displays(params, displayState),
          <.button(^.onClick --> runTask, "Run")
        )
      }
      .build
      .apply()
}