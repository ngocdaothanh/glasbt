import sbt._

class Project(info: ProjectInfo) extends DefaultProject(info) {
  override def compileOptions = super.compileOptions ++
    Seq("-deprecation",
        "-Xmigration",
        "-Xcheckinit",
        "-Xwarninit",
        "-encoding", "utf8")
        .map(x => CompileOption(x))

  override def javaCompileOptions = JavaCompileOption("-Xlint:unchecked") :: super.javaCompileOptions.toList

  // Repos ---------------------------------------------------------------------

  val glassfishRepo  = "Glassfish"  at "http://download.java.net/maven/glassfish/"

  override def libraryDependencies = Set(
    "org.glassfish.extras" % "glassfish-embedded-web" % "3.1-SNAPSHOT"  //3.0.1"
  ) ++ super.libraryDependencies

  // Publish -------------------------------------------------------------------

  override def managedStyle = ManagedStyle.Maven

  val publishTo = "Sonatype Snapshot" at "https://oss.sonatype.org/content/repositories/snapshots"

  // ~/.ivy2/.credentials should be:
  //   realm=Sonatype Nexus Repository Manager
  //   host=oss.sonatype.org
  //   user=xxx
  //   password=xxx
  Credentials(Path.userHome / ".ivy2" / ".credentials", log)
}
