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

  override def mainClass = Some("glasbt.Boot")
}
