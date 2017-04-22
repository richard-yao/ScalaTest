package com.tvunetworks.test

/**
 * @author RichardYao
 * @date 2017?4?22?
 */
object LearnCaseClass {

  abstract class CaseTree
  //条件类在创建时会自动添加参数对应的get方法
  case class Sum(l: CaseTree, r: CaseTree) extends CaseTree
  case class Var(n: String) extends CaseTree
  case class Const(v: Int) extends CaseTree
  
  type Environment = String => Int
  
  /**
   * 树的操作，递归实现
   */
  def eval(t: CaseTree, env: Environment): Int = t match {
    case Sum(l, r) => eval(l, env) + eval(r, env)
    case Var(n) => env(n)
    case Const(v) => v
  }
  
  /**
   * 求导运算
   */
  def derive(t: CaseTree, v: String): CaseTree = t match {
    case Sum(l, r) => Sum(derive(l, v), derive(r, v))
    case Var(n) if(v == n) => Const(1)
    case _ => Const(0)
  }
  
  def main(args: Array[String]) {
    //x+x + 7+y
    val exp: CaseTree = Sum(Sum(Var("x"), Var("x")), Sum(Const(7), Var("y")))
    //Environment,相当于自定义类型
    val env: Environment = {
      case "x" => 5
      case "y" => 7
    }
    println("Expression: " + exp)
    println("Evaluation with x=5,y=7: " + eval(exp, env))
    println("Derivative relative to x:\n" + derive(exp, "x"))
    println("Derivative relative to y:\n" + derive(exp, "y"))
  }
}