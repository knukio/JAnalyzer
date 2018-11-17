object TapperImplicits {

  implicit class tapper[A](obj: A) {
    def trace: A = {
      println(obj)
      obj
    }

    def tap(f: A => Unit): A = {
      f(obj)
      obj
    }

  }

  implicit class EachTapper[Repr](val self: Repr) extends AnyVal {
    def eachTrace[A](implicit ev: Repr => TraversableOnce[A]): Repr = {
      val traversable = ev(self)
      traversable.foreach(println(_))
      self
    }

    def eachTap[A](f: A => Unit)(implicit ev: Repr => TraversableOnce[A]): Repr = {
      val traversable = ev(self)
      traversable.foreach(obj => f(obj))
      self
    }
  }

}
