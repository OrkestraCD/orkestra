package io.chumps.orchestra.page

import io.chumps.orchestra.route.WebRouter.{BoardPageRoute, PageRoute}
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._

import io.chumps.orchestra.board.Board
import io.chumps.orchestra.css.Global
import scalacss.ScalaCssReact._

object FolderPage {
  case class Props(name: String, breadcrumb: Seq[String], childBoards: Seq[Board], ctrl: RouterCtl[PageRoute])

  val component =
    ScalaComponent
      .builder[Props](getClass.getSimpleName)
      .render_P { props =>
        <.main(
          <.h1(props.name),
          <.div(
            props.childBoards.zipWithIndex.toTagMod {
              case (board, index) =>
                <.div(
                  Global.Style.listItem(index % 2 == 0),
                  ^.display.flex,
                  ^.alignItems.center,
                  ^.padding := "3px",
                  ^.height := "22px",
                  ^.cursor.pointer,
                  props.ctrl.setOnClick(BoardPageRoute(props.breadcrumb :+ board.id.name.toLowerCase))
                )(board.name)
            }
          )
        )
      }
      .build
}