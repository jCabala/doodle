package doodle
package svg
package effect

import cats.effect.IO
import doodle.effect._
import doodle.algebra.Picture
import java.io.File
import java.nio.file.Files
import java.util.Base64

import scalatags.Text

object SvgWriter extends Writer[Algebra, Drawing, Frame, Writer.Svg] {
  val algebra = doodle.svg.algebra.Algebra(Text)
  val svg = Svg(Text)

  def write[A](file: File,
               description: Frame,
               picture: Picture[Algebra, Drawing, A]): IO[A] = {
    svg
      .render[Algebra, A](description, algebra, picture)
      .flatMap {
        case (nodes, a) =>
          IO {
            Files.write(file.toPath, nodes.getBytes())
            a
          }
      }
  }

  def write[A](file: File, picture: Picture[Algebra, Drawing, A]): IO[A] =
    write(file, Frame("").fitToPicture(), picture)

  override def base64[A](image: Picture[Algebra, Drawing, A]): IO[(A, String)] = for {
    rendered <- svg.render[Algebra, A](Frame("").fitToPicture(), algebra, image)
    (nodes, value) = rendered
  } yield (value, Base64.getEncoder.encodeToString(nodes.getBytes()))
}
