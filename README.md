## hostbook

<img src="https://cloud.githubusercontent.com/assets/1214075/9833710/7f8fe0f6-5956-11e5-9098-1c3f413186f5.png" />

This is a Scala library that helps you do GeoIP lookups with the
[MaxMind GeoLite2](https://www.maxmind.com/en/geoip2-services-and-databases) databases

This service includes GeoLite2 data created by MaxMind, available from [Maxmind](http://www.maxmind.com)

```scala
import io.imply.hostbook.GeoLookup
import java.io.File

object Example
{
  def main(args: Array[String]) {
    val file = new File("geodb")
    val geo = GeoLookup.fromFreeDownloadableDatabase(file)
    val result = geo.lookup("206.190.36.45")
    println(s"IP is from ${result.cityName.orNull}, ${result.regionName.orNull}, ${result.countryName.orNull}.")
  }
}
```
