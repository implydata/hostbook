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

import java.io.File
import org.scalatest.FunSuite
import org.scalatest.ShouldMatchers

class GeoLookupTest extends FunSuite with ShouldMatchers
{
  test("GeoLookup, hit") {
    val lookup = GeoLookup.fromFreeDownloadableCityDatabase(new File(getClass.getClassLoader.getResource("GeoIP2-City-Test.mmdb").getPath + ".tmp"))
    val result = lookup.lookup("81.2.69.160")
    result.continentCode should be(Some("EU"))
    result.continentName should be(Some("Europe"))
    result.countryIsoCode should be(Some("GB"))
    result.countryName should be(Some("United Kingdom"))
    result.regionIsoCode should be(Some("ENG"))
    result.regionName should be(Some("England"))
    result.metroCode should be(None)
    result.cityName should be(Some("London"))
  }

  test("GeoLookup, miss") {
    val lookup = GeoLookup.fromCityFile(new File(getClass.getClassLoader.getResource("GeoIP2-City-Test.mmdb").getPath))
    val result = lookup.lookup("1.1.1.1")
    result.continentCode should be(None)
    result.continentName should be(None)
    result.countryIsoCode should be(None)
    result.countryName should be(None)
    result.regionIsoCode should be(None)
    result.regionName should be(None)
    result.metroCode should be(None)
    result.cityName should be(None)
  }
}
