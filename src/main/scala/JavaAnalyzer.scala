import java.io.File

import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jdt.core.JavaCore
import org.eclipse.jdt.core.dom._
import scalaz._

object JavaAnalyzer {

  def main(args: Array[String]): Unit = {
    val sourceRoot = "/Users/hiroyuki/project/hobby/JavaAnalyzer/input/gson/"
    val jdkPath = Array("/Library/Java/JavaVirtualMachines/jdk1.8.0_144.jdk/Contents/Home/src.zip")
    val sourceFiles = getFiles(sourceRoot).map(_.filter(_.endsWith(".java")))
    val classFiles = getFiles(sourceRoot).map(_.filter(_.endsWith(".jre")))
    for {
      sourceFiles <- sourceFiles
      classFiles <- classFiles
    } yield parseLevel2(sourceRoot, sourceFiles, jdkPath ++ classFiles)
  }

  def parse(source: String): CompilationUnit = {
    val parser = ASTParser.newParser(AST.JLS9)
    parser.setSource(source.toCharArray)
    parser.createAST(new NullProgressMonitor).asInstanceOf[CompilationUnit]
  }

  private def getFiles(dirPath: String): \/[String, Array[String]] = {
    def getFilesRec(absFile: File): Array[String] = {
      absFile.listFiles().flatMap(path => {
        if (path.isFile)
          Array(path.getAbsolutePath)
        else
          getFilesRec(path.getAbsoluteFile)
      })
    }

    val dir = new File(dirPath)
    if (dir.exists()) {
      \/-(getFilesRec(dir))
    } else {
      -\/("dir is not exist")
    }
  }

  def parseLevel2(sourceRoot: String, sourceFiles: Array[String], classFiles: Array[String]) = {
    val parser = ASTParser.newParser(AST.JLS9)
    val options = JavaCore.getOptions
    JavaCore.setComplianceOptions(JavaCore.VERSION_9, options)
    parser.setCompilerOptions(options)
    parser.setKind(ASTParser.K_COMPILATION_UNIT)
    parser.setResolveBindings(true)
    parser.setBindingsRecovery(true)
    parser.setEnvironment(classFiles, Array(sourceRoot), null, true)
    parser.createASTs(sourceFiles, null, Array[String](), new FileASTRequestor() {
      override def acceptAST(sourceFilePath: String, ast: CompilationUnit): Unit = {
        println(sourceFilePath)
        import Visitors._
        ast.accept(new MethodVisitor())
        println("---------")
      }

      override def acceptBinding(bindingKey: String, binding: IBinding): Unit = {
      }
    }
      , new NullProgressMonitor()
    )
  }

}
