package glasbt

import java.io.File
import java.util.Scanner
import org.glassfish.embeddable.{CommandRunner, Deployer, GlassFish, GlassFishRuntime}

/** http://embedded-glassfish.java.net/nonav/apidocs/org/glassfish/embeddable/GlassFish.html */
object Boot {
  def main(args: Array[String]) {
    val glassFish = GlassFishRuntime.bootstrap.newGlassFish
    start(glassFish)
    println("\n-------- GlashFish started, now deploy your WAR file --------\n")

    val deployer  = glassFish.getService(classOf[Deployer])

    val scanner = new Scanner(System.in)
    var stopped = false
    while (!stopped) {
      val deployedApp = deploy(deployer)
      println("\n------- Deployed, [R] to redeploy, [X] to exit --------\n")

      var commandValid = false
      while (!commandValid) {
        val line = scanner.nextLine
        if (line.length > 0) {
          val char = line.charAt(0).toUpper
          if (char == 'R') {
            commandValid = true
            deployer.undeploy(deployedApp)
          } else if (char == 'X') {
            commandValid = true
            glassFish.stop
            glassFish.dispose
            stopped = true
          }
        }
      }
    }
  }

  private def start(glassFish: GlassFish) {
    val port = 8080

    glassFish.start

    // Run commands
    val commandRunner = glassFish.getService(classOf[CommandRunner])

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

  /** Deploy to / and return a String representing the deployed app. */
  private def deploy(deployer: Deployer): String = {
    val currentDir = System.getProperty("user.dir")
    val war = currentDir + "/target/scala_2.8.1/comy_2.8.1-1.2.war"  // TODO: find the first .war

    val deployedApp = deployer.deploy(
      new File(war).toURI,
      "--contextroot=", "--force=true")

    deployedApp
  }
}
