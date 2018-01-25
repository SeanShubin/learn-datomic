package com.seanshubin.learn.datomic.contract

import java.io.{BufferedReader, BufferedWriter, InputStream, OutputStream}
import java.nio.channels.SeekableByteChannel
import java.nio.charset.Charset
import java.nio.file.DirectoryStream.Filter
import java.nio.file._
import java.nio.file.attribute._
import java.util.function.BiPredicate
import java.util.stream
import java.{lang, util}

object FilesDelegate extends FilesContract {
  override def newInputStream(path: Path, options: OpenOption*): InputStream = Files.newInputStream(path, options: _*)

  override def getLastModifiedTime(path: Path, options: LinkOption*): FileTime = Files.getLastModifiedTime(path, options: _*)

  override def move(source: Path, target: Path, options: CopyOption*): Path = Files.move(source, target, options: _*)

  override def newOutputStream(path: Path, options: OpenOption*): OutputStream = Files.newOutputStream(path, options: _*)

  override def createDirectory(dir: Path, attrs: FileAttribute[_]*): Path = Files.createDirectory(dir, attrs: _*)

  override def createTempFile(dir: Path, prefix: String, suffix: String, attrs: FileAttribute[_]*): Path = Files.createTempFile(dir, prefix, suffix, attrs: _*)

  override def createTempFile(prefix: String, suffix: String, attrs: FileAttribute[_]*): Path = Files.createTempFile(prefix, suffix, attrs: _*)

  override def isReadable(path: Path): Boolean = Files.isReadable(path)

  override def probeContentType(path: Path): String = Files.probeContentType(path)

  override def createDirectories(dir: Path, attrs: FileAttribute[_]*): Path = Files.createDirectories(dir, attrs: _*)

  override def newBufferedReader(path: Path, cs: Charset): BufferedReader = Files.newBufferedReader(path, cs)

  override def newBufferedReader(path: Path): BufferedReader = Files.newBufferedReader(path)

  override def walkFileTree(start: Path, options: util.Set[FileVisitOption], maxDepth: Int, visitor: FileVisitor[_ >: Path]): Path = Files.walkFileTree(start, options, maxDepth, visitor)

  override def walkFileTree(start: Path, visitor: FileVisitor[_ >: Path]): Path = Files.walkFileTree(start, visitor)

  override def newByteChannel(path: Path, options: util.Set[_ <: OpenOption], attrs: FileAttribute[_]*): SeekableByteChannel = Files.newByteChannel(path, options, attrs: _*)

  override def newByteChannel(path: Path, options: OpenOption*): SeekableByteChannel = Files.newByteChannel(path, options: _*)

  override def createFile(path: Path, attrs: FileAttribute[_]*): Path = Files.createFile(path, attrs: _*)

  override def isRegularFile(path: Path, options: LinkOption*): Boolean = Files.isRegularFile(path, options: _*)

  override def createSymbolicLink(link: Path, target: Path, attrs: FileAttribute[_]*): Path = Files.createSymbolicLink(link, target, attrs: _*)

  override def walk(start: Path, maxDepth: Int, options: FileVisitOption*): stream.Stream[Path] = Files.walk(start, maxDepth, options: _*)

  override def walk(start: Path, options: FileVisitOption*): stream.Stream[Path] = Files.walk(start, options: _*)

  override def getAttribute(path: Path, attribute: String, options: LinkOption*): AnyRef = Files.getAttribute(path, attribute, options: _*)

  override def createLink(link: Path, existing: Path): Path = Files.createLink(link, existing)

  override def isHidden(path: Path): Boolean = Files.isHidden(path)

  override def getPosixFilePermissions(path: Path, options: LinkOption*): util.Set[PosixFilePermission] = Files.getPosixFilePermissions(path, options: _*)

  override def size(path: Path): Long = Files.size(path)

  override def lines(path: Path, cs: Charset): stream.Stream[String] = Files.lines(path, cs)

  override def lines(path: Path): stream.Stream[String] = Files.lines(path)

  override def copy(source: Path, target: Path, options: CopyOption*): Path = Files.copy(source, target, options: _*)

  override def copy(in: InputStream, target: Path, options: CopyOption*): Long = Files.copy(in, target, options: _*)

  override def copy(source: Path, out: OutputStream): Long = Files.copy(source, out)

  override def notExists(path: Path, options: LinkOption*): Boolean = Files.notExists(path, options: _*)

  override def delete(path: Path): Unit = Files.delete(path)

  override def newDirectoryStream(dir: Path): DirectoryStream[Path] = Files.newDirectoryStream(dir)

  override def newDirectoryStream(dir: Path, glob: String): DirectoryStream[Path] = Files.newDirectoryStream(dir, glob)

  override def newDirectoryStream(dir: Path, filter: Filter[_ >: Path]): DirectoryStream[Path] = Files.newDirectoryStream(dir, filter)

  override def write(path: Path, bytes: Seq[Byte], options: OpenOption*): Path = Files.write(path, bytes.toArray, options: _*)

  override def write(path: Path, lines: lang.Iterable[_ <: CharSequence], cs: Charset, options: OpenOption*): Path = Files.write(path, lines, cs, options: _*)

  override def write(path: Path, lines: lang.Iterable[_ <: CharSequence], options: OpenOption*): Path = Files.write(path, lines, options: _*)

  override def isDirectory(path: Path, options: LinkOption*): Boolean = Files.isDirectory(path, options: _*)

  override def isExecutable(path: Path): Boolean = Files.isExecutable(path)

  override def setAttribute(path: Path, attribute: String, value: AnyRef, options: LinkOption*): Path = Files.setAttribute(path, attribute, value, options: _*)

  override def list(dir: Path): stream.Stream[Path] = Files.list(dir)

  override def getOwner(path: Path, options: LinkOption*): UserPrincipal = Files.getOwner(path, options: _*)

  override def isWritable(path: Path): Boolean = Files.isWritable(path)

  override def readAllLines(path: Path, cs: Charset): util.List[String] = Files.readAllLines(path, cs)

  override def readAllLines(path: Path): util.List[String] = Files.readAllLines(path)

  override def isSymbolicLink(path: Path): Boolean = Files.isSymbolicLink(path)

  override def readSymbolicLink(link: Path): Path = Files.readSymbolicLink(link)

  override def readAttributes[A <: BasicFileAttributes](path: Path, theType: Class[A], options: LinkOption*): A = Files.readAttributes(path, theType, options: _*)

  override def readAttributes(path: Path, attributes: String, options: LinkOption*): util.Map[String, AnyRef] = Files.readAttributes(path, attributes, options: _*)

  override def isSameFile(path: Path, path2: Path): Boolean = Files.isSameFile(path, path2)

  override def createTempDirectory(dir: Path, prefix: String, attrs: FileAttribute[_]*): Path = Files.createTempDirectory(dir, prefix, attrs: _*)

  override def createTempDirectory(prefix: String, attrs: FileAttribute[_]*): Path = Files.createTempDirectory(prefix, attrs: _*)

  override def getFileAttributeView[V <: FileAttributeView](path: Path, theType: Class[V], options: LinkOption*): V = Files.getFileAttributeView(path, theType, options: _*)

  override def find(start: Path, maxDepth: Int, matcher: BiPredicate[Path, BasicFileAttributes], options: FileVisitOption*): stream.Stream[Path] = Files.find(start, maxDepth, matcher, options: _*)

  override def deleteIfExists(path: Path): Boolean = Files.deleteIfExists(path)

  override def setPosixFilePermissions(path: Path, perms: util.Set[PosixFilePermission]): Path = Files.setPosixFilePermissions(path, perms)

  override def setLastModifiedTime(path: Path, time: FileTime): Path = Files.setLastModifiedTime(path, time)

  override def readAllBytes(path: Path): Seq[Byte] = Files.readAllBytes(path)

  override def exists(path: Path, options: LinkOption*): Boolean = Files.exists(path, options: _*)

  override def setOwner(path: Path, owner: UserPrincipal): Path = Files.setOwner(path, owner)

  override def newBufferedWriter(path: Path, cs: Charset, options: OpenOption*): BufferedWriter = Files.newBufferedWriter(path, cs, options: _*)

  override def newBufferedWriter(path: Path, options: OpenOption*): BufferedWriter = Files.newBufferedWriter(path, options: _*)

  override def getFileStore(path: Path): FileStore = Files.getFileStore(path)
}
