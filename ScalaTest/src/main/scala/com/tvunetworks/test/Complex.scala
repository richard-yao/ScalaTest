package com.tvunetworks.test

class Complex(real: Double, imaginary: Double) {
  
  def re() = real //这个是零参方法，访问时需要带上()
  def im = imaginary //无参方法，访问直接以属性方式访问
  
}