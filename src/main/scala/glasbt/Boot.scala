package glasbt

import java.io.{File, FileInputStream}
import java.util.{Properties, Scanner}
import org.glassfish.embeddable.{CommandRunner, Deployer, GlassFish, GlassFishRuntime}

/** http://embedded-glassfish.java.net/nonav/apidocs/org/glassfish/embeddable/GlassFish.html */
object Boot {
  def main(args: Array[String]) {
    val argv = args.size
    if (argv > 1) {
      println("Usage: glasbt [port]\n  port: default is 8080\n")
      System.exit(-1)
    }
    val port = if (argv == 0) 8080 else args(0).toInt

    val (fileName, fullFileName) = findWar
    println("\n-------- WAR file: " + fileName + " --------\n")

    val glassFish = GlassFishRuntime.bootstrap.newGlassFish
    start(port, glassFish)
    println("\n-------- GlashFish started, now deploy your WAR file --------\n")

    val deployer  = glassFish.getService(classOf[Deployer])

    val scanner = new Scanner(System.in)
    var stopped = false
    while (!stopped) {
      val deployedApp = deploy(fullFileName, deployer)
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

  private def start(port: Int, glassFish: GlassFish) {
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
      "--minthreadpoolsize=4",
      "--maxthreadpoolsize=1024",
      "my-thread-pool")

    // Associate my-thread-pool with my-http-listener
    commandRunner.run("set",
      "server.network-config.network-listeners.network-listener.my-http-listener.thread-pool=my-thread-pool")
  }

  /** Deploy to / and return a String representing the deployed app. */
  private def deploy(war: String, deployer: Deployer): String = {
    deployer.deploy(
      new File(war).toURI,
      "--contextroot=", "--force=true")
  }

  //                    fileName  fullFileName
  private def findWar: (String,   String) = {
    val currentDir = System.getProperty("user.dir")

    // See project information in build.properties
    val props = new Properties
    val fis   = new FileInputStream(currentDir + "/project/build.properties")
    props.load(fis)
    fis.close
    val scalaVersion   = props.getProperty("build.scala.versions").split(" ").last
    val projectName    = props.getProperty("project.name")
    val projectVersion = props.getProperty("project.version")

    val fileName     = projectName + "_" + scalaVersion + "-" + projectVersion + ".war"
    val fullFileName = currentDir + "/target/scala_" + scalaVersion + "/" + fileName
    (fileName, fullFileName)
  }
}
