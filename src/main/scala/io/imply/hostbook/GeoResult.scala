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

import com.maxmind.geoip2.model.CityResponse

class GeoResult(val cityResponse: Option[CityResponse])
{
  def countryName: Option[String] = cityResponse.flatMap(x => Option(x.getCountry.getName))

  def countryIsoCode: Option[String] = cityResponse.flatMap(x => Option(x.getCountry.getIsoCode))

  def regionName: Option[String] = cityResponse.flatMap(x => Option(x.getMostSpecificSubdivision.getName))

  def regionIsoCode: Option[String] = cityResponse.flatMap(x => Option(x.getMostSpecificSubdivision.getIsoCode))

  def metroCode: Option[Int] = cityResponse.flatMap(x => Option(x.getLocation.getMetroCode).map(_.intValue()))

  def cityName: Option[String] = cityResponse.flatMap(x => Option(x.getCity.getName))
}
