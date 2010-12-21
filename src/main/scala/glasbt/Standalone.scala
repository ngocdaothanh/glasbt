package glasbt

import java.io.{File, FileInputStream}
import java.util.{Properties, Scanner}
import org.glassfish.embeddable.{BootstrapProperties, GlassFishProperties, GlassFishRuntime}

object Standalone {
  def main(args: Array[String]) {
    val currentDir = System.getProperty("user.dir")
    val domainDir  = currentDir + "/domain1"

    val bp = new BootstrapProperties
    val gp = new GlassFishProperties
    bp.setInstallRoot(domainDir)
    gp.setInstanceRoot(domainDir)

    val glassFish = GlassFishRuntime.bootstrap(bp).newGlassFish(gp)
    glassFish.start
  }
}
