package com.tvunetworks.test

class Person(ageR: Int, nameR: String, ismaleR: Boolean) {
  
  var age = ageR
  var name = nameR
  var ismale = ismaleR
  
  def printPersonInfo() {
    var temp = ""
    if(ismale)
      temp = "male"
    else 
      temp = "female"
    println("This person's name is "+name+","+age+" years old, "+temp)
  }
  
  override def toString(): String = {
    return "name： " + name + ", age: " + age+",ismale: " + ismale
  }
}