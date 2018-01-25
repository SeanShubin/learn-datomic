package com.seanshubin.learn.datomic.contract

import java.io.{BufferedReader, BufferedWriter, InputStream, OutputStream}
import java.nio.channels.SeekableByteChannel
import java.nio.charset.Charset
import java.nio.file._
import java.nio.file.attribute._
import java.util.function.BiPredicate
import java.util.stream
import java.{lang, util}

trait FilesContract {
  def newInputStream(path: Path, options: OpenOption*): InputStream

  def newOutputStream(path: Path, options: OpenOption*): OutputStream

  def newByteChannel(path: Path, options: util.Set[_ <: OpenOption], attrs: FileAttribute[_]*): SeekableByteChannel

  def newByteChannel(path: Path, options: OpenOption*): SeekableByteChannel

  def newDirectoryStream(dir: Path): DirectoryStream[Path]

  def newDirectoryStream(dir: Path, glob: String): DirectoryStream[Path]

  def newDirectoryStream(dir: Path, filter: DirectoryStream.Filter[_ >: Path]): DirectoryStream[Path]

  def createFile(path: Path, attrs: FileAttribute[_]*): Path

  def createDirectory(dir: Path, attrs: FileAttribute[_]*): Path

  def createDirectories(dir: Path, attrs: FileAttribute[_]*): Path

  def createTempFile(dir: Path, prefix: String, suffix: String, attrs: FileAttribute[_]*): Path

  def createTempFile(prefix: String, suffix: String, attrs: FileAttribute[_]*): Path

  def createTempDirectory(dir: Path, prefix: String, attrs: FileAttribute[_]*): Path

  def createTempDirectory(prefix: String, attrs: FileAttribute[_]*): Path

  def createSymbolicLink(link: Path, target: Path, attrs: FileAttribute[_]*): Path

  def createLink(link: Path, existing: Path): Path

  def delete(path: Path)

  def deleteIfExists(path: Path): Boolean

  def copy(source: Path, target: Path, options: CopyOption*): Path

  def move(source: Path, target: Path, options: CopyOption*): Path

  def readSymbolicLink(link: Path): Path

  def getFileStore(path: Path): FileStore

  def isSameFile(path: Path, path2: Path): Boolean

  def isHidden(path: Path): Boolean

  def probeContentType(path: Path): String

  def getFileAttributeView[V <: FileAttributeView](path: Path, theType: Class[V], options: LinkOption*): V

  def readAttributes[A <: BasicFileAttributes](path: Path, theType: Class[A], options: LinkOption*): A

  def setAttribute(path: Path, attribute: String, value: AnyRef, options: LinkOption*): Path

  def getAttribute(path: Path, attribute: String, options: LinkOption*): AnyRef

  def readAttributes(path: Path, attributes: String, options: LinkOption*): util.Map[String, AnyRef]

  def getPosixFilePermissions(path: Path, options: LinkOption*): util.Set[PosixFilePermission]

  def setPosixFilePermissions(path: Path, perms: util.Set[PosixFilePermission]): Path

  def getOwner(path: Path, options: LinkOption*): UserPrincipal

  def setOwner(path: Path, owner: UserPrincipal): Path

  def isSymbolicLink(path: Path): Boolean

  def isDirectory(path: Path, options: LinkOption*): Boolean

  def isRegularFile(path: Path, options: LinkOption*): Boolean

  def getLastModifiedTime(path: Path, options: LinkOption*): FileTime

  def setLastModifiedTime(path: Path, time: FileTime): Path

  def size(path: Path): Long

  def exists(path: Path, options: LinkOption*): Boolean

  def notExists(path: Path, options: LinkOption*): Boolean

  def isReadable(path: Path): Boolean

  def isWritable(path: Path): Boolean

  def isExecutable(path: Path): Boolean

  def walkFileTree(start: Path, options: util.Set[FileVisitOption], maxDepth: Int, visitor: FileVisitor[_ >: Path]): Path

  def walkFileTree(start: Path, visitor: FileVisitor[_ >: Path]): Path

  def newBufferedReader(path: Path, cs: Charset): BufferedReader

  def newBufferedReader(path: Path): BufferedReader

  def newBufferedWriter(path: Path, cs: Charset, options: OpenOption*): BufferedWriter

  def newBufferedWriter(path: Path, options: OpenOption*): BufferedWriter

  def copy(in: InputStream, target: Path, options: CopyOption*): Long

  def copy(source: Path, out: OutputStream): Long

  def readAllBytes(path: Path): Seq[Byte]

  def readAllLines(path: Path, cs: Charset): util.List[String]

  def readAllLines(path: Path): util.List[String]

  def write(path: Path, bytes: Seq[Byte], options: OpenOption*): Path

  def write(path: Path, lines: lang.Iterable[_ <: CharSequence], cs: Charset, options: OpenOption*): Path

  def write(path: Path, lines: lang.Iterable[_ <: CharSequence], options: OpenOption*): Path

  def list(dir: Path): stream.Stream[Path]

  def walk(start: Path, maxDepth: Int, options: FileVisitOption*): stream.Stream[Path]

  def walk(start: Path, options: FileVisitOption*): stream.Stream[Path]

  def find(start: Path, maxDepth: Int, matcher: BiPredicate[Path, BasicFileAttributes], options: FileVisitOption*): stream.Stream[Path]

  def lines(path: Path, cs: Charset): stream.Stream[String]

  def lines(path: Path): stream.Stream[String]
}
