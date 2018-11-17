import org.eclipse.jdt.core.dom.{ASTVisitor, MethodDeclaration}

object Visitors {

  class MethodVisitor extends ASTVisitor {
    override def visit(node: MethodDeclaration): Boolean = {
      import TapperImplicits._
      node.getName.trace
      true
    }

  }

}
