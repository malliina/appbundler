package com.malliina

import java.nio.file.{Files, Path}

import com.malliina.storage.{StorageLong, StorageSize}

package object appbundler {

  implicit final class StorageFile(val file: Path) {
    def size: StorageSize = (Files size file).bytes

    def /(next: String): Path = file resolve next

    def /(next: Path): Path = file resolve next
  }

}
