package com.seanshubin.learn.datomic.prototype

import java.util.{List => JavaList}

import datomic.Connection

trait Datomic {
  def connection: Connection

  def invokeTransactionFunction(name: String, args: AnyRef*): AnyRef

  def addTransactionFunction(transactionFunction: TransactionFunction): Unit

  def schema(): Map[String, DatomicType]

  def updateSchema(fields: Map[String, DatomicType])

  def transactionFunctionNames(): Seq[String]

  def execute(transactionFunctionName: String, parameters: String*)

  def query(datalog: String): Seq[Seq[AnyRef]]

  def query[T](datalog: String, rowConverter: JavaList[AnyRef] => T): Seq[T]

  def queryAllAttributeNames(): Seq[String]

  def queryAllValuesForAttribute(attributeName: String): Seq[RowConverter.EntityAndValueAsString]

  def queryAll(): Seq[EntityAttributeValue]
}
