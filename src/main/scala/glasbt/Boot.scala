package glasbt

import java.io.File
import org.glassfish.embeddable.{CommandRunner, Deployer, GlassFishRuntime}

/** http://embedded-glassfish.java.net/nonav/apidocs/org/glassfish/embeddable/GlassFish.html */
object Boot {
  def main(args: Array[String]) {
    val port = 8000
    val war = "/Users/ngoc/workspace/comy/target/scala_2.8.1/comy_2.8.1-1.2.war"

    val glassfish = GlassFishRuntime.bootstrap.newGlassFish
    glassfish.start

    // Deploy to /
    val deployer = glassfish.getService(classOf[Deployer])
    deployer.deploy(
      new File(war).toURI,
      "--contextroot=", "--force=true")

    // Run commands
    val commandRunner = glassfish.getService(classOf[CommandRunner])

    // Create my-http-listener
    commandRunner.run("create-http-listener",
      "--listenerport=" + port,
      "--listeneraddress=0.0.0.0",
      "--defaultvs=server",
      "--securityenabled=false",  // true = HTTPS, false = HTTP
      "my-http-listener")

    // Create thread pool
    commandRunner.run("create-threadpool",
      "--maxthreadpoolsize=200",
      "--minthreadpoolsize=200",
      "my-thread-pool")

    // Associate my-thread-pool with my-http-listener
    commandRunner.run("set",
      "server.network-config.network-listeners.network-listener.my-http-listener.thread-pool=my-thread-pool")
  }
}
