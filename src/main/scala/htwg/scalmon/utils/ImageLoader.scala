package htwg.scalmon.utils

import scala.io.Source
import java.net.URL
import java.net.URLEncoder
import java.awt.Image
import javax.imageio.ImageIO

object ImageLoader {
  def load(query: String) = {
    var img: Image = null

    try {
      val urls = getImageUrls(query)

      for (url <- urls)
        if (img == null)
          img = ImageIO.read(url)
    } catch {
      case e: Exception => println("exception caught: " + e)
    }

    img
  }

  private def getImageUrls(query: String) = {
    val url = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=" + URLEncoder.encode(query, "UTF-8")
    val json = JsonElement.parse(Source.fromURL(url).mkString)
    val results = json.get.responseData.results

    for (i <- 0 until results.length$)
      yield new URL(results.elements(i).unescapedUrl)
  }
}