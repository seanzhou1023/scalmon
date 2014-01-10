package htwg.scalmon.utils

import scala.concurrent.future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable.HashMap
import scala.io.Source
import scala.swing.Image
import java.io.File
import java.net.{ URL, URLEncoder }
import java.security.MessageDigest
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import htwg.scalmon.BuildInfo
import scala.swing.UIElement
import org.apache.commons.codec.binary.Base64
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

class ImageWrapper {
  private var image: BufferedImage = ImageLoader.emptyImage
  private var listeners = List[UIElement]()

  def get(elem: UIElement = null) = {
    if (elem != null && !listeners.contains(elem))
      listeners = elem :: listeners

    image
  }

  def asBase64 = {
    val bos = new ByteArrayOutputStream()
    ImageIO.write(image, "png", bos)
    Base64.encodeBase64String(bos.toByteArray)
  }

  def set(img: BufferedImage) {
    image = img
    listeners.foreach(_.repaint)
  }
}

object ImageLoader {
  val width = 200
  val height = 200

  private val cache = HashMap[String, ImageWrapper]()

  val emptyImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY)

  def get(query: String) = {
    cache.synchronized {
      if (!cache.contains(query)) {
        cache += ((query, new ImageWrapper))
        load(query)
      }

      cache(query)
    }
  }

  /*
   * Source: http://stackoverflow.com/questions/244164/
   */
  def createResizedCopy(originalImage: Image,
                        scaledWidth: Int, scaledHeight: Int,
                        preserveAlpha: Boolean): BufferedImage = {
    val imageType: Int = if (preserveAlpha)
      BufferedImage.TYPE_INT_ARGB else BufferedImage.TYPE_INT_RGB
    val scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType)
    val g = scaledBI.createGraphics()
    g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
    g.dispose()
    scaledBI
  }

  private def load(query: String) {
    if (!searchForFile(query))
      future { loadExtern(query) }
  }

  private def searchForFile(query: String): Boolean = {
    val f = file(query)

    if (f.exists()) {
      try {
        addCache(query, ImageIO.read(f), false)
        return true
      } catch {
        case e: Exception => Log(e)
      }
    }

    false
  }

  private def loadExtern(query: String) {
    val urls = getImageUrls(query)

    for (url <- urls) {
      val img = tryReadImage(url)

      if (img != null) {
        addCache(query, img, true)
        return
      }
    }
  }

  private def getImageUrls(query: String) = {
    var urls = Seq[String]()

    try {
      val url =
        "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=" +
          URLEncoder.encode(query, "UTF-8")
      val json = JsonElement.parse(Source.fromURL(url).mkString)
      val results = json.get.responseData.results

      for (i <- 0 until results.length$)
        yield new URL(results.elements(i).unescapedUrl)
    } catch {
      case e: Exception =>
        Log(e)
        Seq[URL]()
    }
  }

  private def tryReadImage(url: URL) = {
    try {
      ImageIO.read(url)
    } catch {
      case e: Exception =>
        Log(e)
        null
    }
  }

  private def file(query: String) = {
    val dir = new File(System.getProperty("java.io.tmpdir") + "/" +
      BuildInfo.name)
    dir.mkdir();
    val filename = MessageDigest.getInstance("MD5").digest(query.getBytes()).map(b => Integer.toHexString(0xff & b)).mkString("")
    new File(dir.getAbsolutePath() + "/" + filename + ".png")
  }

  private def addCache(query: String, img: BufferedImage, save: Boolean) {
    cache.synchronized {
      val wrapper = cache(query)
      wrapper.set(createResizedCopy(img, width, height, true))
    }

    if (save) {
      try {
        ImageIO.write(img, "png", file(query))
      } catch {
        case e: Exception => Log(e)
      }
    }
  }
}