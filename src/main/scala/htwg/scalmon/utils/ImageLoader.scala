package htwg.scalmon.utils

import scala.collection.mutable.HashMap
import scala.io.Source
import java.io.File
import java.net.URL
import java.net.URLEncoder
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import htwg.scalmon.BuildInfo

object ImageLoader {
  private val cache = HashMap[String, BufferedImage]();

  def get(query: String) = {
    searchForFile(query)

    if (cache.contains(query))
      cache(query)
    else
      load(query)
  }

  private def load(query: String): BufferedImage = {
    val urls = getImageUrls(query)

    for (url <- urls) {
      val img = tryReadImage(url)

      if (img != null) {
        addCache(query, img, true)
        return img
      }
    }

    null
  }

  private def getImageUrls(query: String) = {
    var urls = Seq[String]()

    try {
      val url = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=" + URLEncoder.encode(query, "UTF-8")
      val json = JsonElement.parse(Source.fromURL(url).mkString)
      val results = json.get.responseData.results

      for (i <- 0 until results.length$)
        yield new URL(results.elements(i).unescapedUrl)
    } catch {
      case e: Exception =>
        println("getImageUrls: " + e)
        Seq[URL]()
    }
  }

  private def tryReadImage(url: URL) = {
    try {
      ImageIO.read(url)
    } catch {
      case e: Exception =>
        println("tryReadImage: " + e)
        null
    }
  }

  private def file(query: String) = {
    val dir = new File(System.getProperty("java.io.tmpdir") + "/" + BuildInfo.name)
    dir.mkdir();
    new File(dir.getAbsolutePath() + "/" + query + ".png")
  }

  private def searchForFile(query: String) {
    val f = file(query)

    if (f.exists()) {
      try {
        addCache(query, ImageIO.read(f), false)
      } catch {
        case e: Exception => println("searchForFile: " + e)
      }
    }
  }

  private def addCache(query: String, img: BufferedImage, save: Boolean) {
    cache += ((query, img))

    if (save) {
      try {
        ImageIO.write(img, "png", file(query))
      } catch {
        case e: Exception => println("addCache: " + e)
      }
    }
  }
}