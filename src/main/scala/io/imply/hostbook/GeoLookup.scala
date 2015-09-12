/*
 * Copyright 2015 Imply Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.imply.hostbook

import com.google.common.io.ByteStreams
import com.maxmind.db.Reader.FileMode
import com.maxmind.geoip2.DatabaseReader
import com.maxmind.geoip2.exception.AddressNotFoundException
import com.metamx.common.scala.Logging
import com.metamx.common.scala.Predef._
import com.twitter.util.NetUtil
import java.io.File
import java.io.FileOutputStream
import java.net.InetAddress
import java.net.URL
import java.util.UUID
import java.util.zip.GZIPInputStream

class GeoLookup(
  cityReader: DatabaseReader
) extends AutoCloseable
{
  def lookup(address: String): GeoResult = {
    val inetAddress = NetUtil.ipToOptionInt(address) map { i =>
      val bytes = Array[Byte](
        ((i >> 24) & 0xff).toByte,
        ((i >> 16) & 0xff).toByte,
        ((i >> 8) & 0xff).toByte,
        (i & 0xff).toByte
      )
      InetAddress.getByAddress(address, bytes)
    }

    inetAddress.map(lookup).getOrElse(new GeoResult(None))
  }

  def lookup(address: InetAddress): GeoResult = {
    val cityResponse = try {
      Option(cityReader.city(address))
    }
    catch {
      case e: AddressNotFoundException =>
        None
    }
    new GeoResult(cityResponse)
  }

  def close(): Unit = {
    cityReader.close()
  }
}

object GeoLookup extends Logging
{
  val freeDownloadableCityDatabase = "http://geolite.maxmind.com/download/geoip/database/GeoLite2-City.mmdb.gz"

  def fromCityFile(cityFile: File): GeoLookup = {
    val reader = new DatabaseReader.Builder(cityFile).fileMode(FileMode.MEMORY_MAPPED).build()
    new GeoLookup(reader)
  }

  def fromFreeDownloadableCityDatabase(cityFile: File): GeoLookup = {
    if (!cityFile.exists()) {
      log.info("Downloading database from[%s] to local file[%s].", freeDownloadableCityDatabase, cityFile)

      new File(cityFile.toString + ".tmp." + UUID.randomUUID()).withFinally(_.delete()) { tmpFile =>
        new FileOutputStream(tmpFile).withFinally(_.close()) { out =>
          new GZIPInputStream(new URL(freeDownloadableCityDatabase).openStream()).withFinally(_.close()) { in =>
            ByteStreams.copy(in, out)
          }
        }

        tmpFile.renameTo(cityFile)
      }
    }

    fromCityFile(cityFile)
  }
}
