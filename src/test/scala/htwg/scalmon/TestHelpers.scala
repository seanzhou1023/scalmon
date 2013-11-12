package htwg.scalmon

import org.scalatest.matchers._

object TestHelpers {
  def anInstanceOf[T](implicit manifest: Manifest[T]) = {
    val clazz = manifest.runtimeClass.asInstanceOf[Class[T]]

    new BePropertyMatcher[AnyRef] {
      def apply(left: AnyRef) =
        BePropertyMatchResult(left.getClass.isAssignableFrom(clazz), "an instance of " + clazz.getName)
    }
  }
}