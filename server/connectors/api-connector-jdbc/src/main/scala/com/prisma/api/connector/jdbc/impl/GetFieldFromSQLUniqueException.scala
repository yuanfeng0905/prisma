package com.prisma.api.connector.jdbc.impl
import java.sql.{SQLException, SQLIntegrityConstraintViolationException}

import com.prisma.shared.models.Model

object GetFieldFromSQLUniqueException {

  def getFieldOption(model: Model, e: SQLException): Option[String] = {
    model.scalarFields.filter { field =>
      val constraintNameThatMightBeTooLong = model.dbName + "." + field.dbName + "._UNIQUE"
      val constraintName                   = constraintNameThatMightBeTooLong.substring(0, Math.min(30, constraintNameThatMightBeTooLong.length))
      e.getMessage.contains(constraintName)
    } match {
      case x +: _ => Some("Field name = " + x.name)
      case _      => None
    }
  }

  def getFieldOptionMySql(fieldNames: Vector[String], e: SQLIntegrityConstraintViolationException): Option[String] = {

    fieldNames.filter(x => e.getCause.getMessage.contains("\'" + x + "_")) match {
      case x +: _ => Some("Field name = " + x)
      case _      => None
    }
  }
}
