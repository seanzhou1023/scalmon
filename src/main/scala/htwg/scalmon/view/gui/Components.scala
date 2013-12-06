package htwg.scalmon.view.gui

import htwg.scalmon.utils.ImageWrapper

class Label(val format: String, args: String*) extends swing.Label {
  def setArgs(args: String*) {
    var t = format

    for (i <- 0 until args.length)
      t = t.replaceAll("%" + i, args(i))

    text = t
  }

  setArgs(args: _*)
}

class ImageLabel(val imageWrapper: ImageWrapper) extends swing.Label {
  icon = new javax.swing.ImageIcon(imageWrapper.get(this))

  override def repaint {
    icon = new javax.swing.ImageIcon(imageWrapper.get())
  }
}