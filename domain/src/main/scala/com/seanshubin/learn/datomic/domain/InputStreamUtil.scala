package com.seanshubin.learn.datomic.domain

import java.io.InputStream

import scala.annotation.tailrec

object InputStreamUtil {
  def inputStreamToString(inputStream: InputStream): String = {
    val stringBuilder = new StringBuilder
    @tailrec def readRemainingCharacters(currentCharacter: Int): String = {
      if (currentCharacter == -1) stringBuilder.toString()
      else {
        stringBuilder.append(currentCharacter.asInstanceOf[Char])
        readRemainingCharacters(inputStream.read())
      }
    }
    val result = readRemainingCharacters(inputStream.read())
    result
  }

  def ensureClose[T](inputStream: InputStream)(f: InputStream => T): T = {
    try {
      f(inputStream)
    } finally {
      inputStream.close()
    }
  }
}
