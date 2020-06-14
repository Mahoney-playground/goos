package goos

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.io.File

@Serializable
data class CountryDeaths(
  val areaCode: String,
  val areaName: String,
  val reportingDate: String,
  val dailyChangeInDeaths: Int?,
  val cumulativeDeaths: Int?
)

@Serializable
data class Deaths(
  val countries: List<CountryDeaths>
)

@Serializable
data class AreaCases(
  val areaCode: String,
  val areaName: String,
  val specimenDate: String,
  val dailyLabConfirmedCases: Int? = 0,
  val previouslyReportedDailyCases: Int? = 0,
  val changeInDailyCases: Int? = 0
)

@Serializable
data class Cases(
  val countries: List<AreaCases>,
  val regions: List<AreaCases>
)

@ExperimentalStdlibApi
fun main() {
  val json = Json(JsonConfiguration.Default.copy(ignoreUnknownKeys = true))
//  parseDeaths(json)
//  parseCases(json)
  parseLondonCases(json)
}

private fun parseDeaths(json: Json) {
  val deaths =
    json.parse(Deaths.serializer(), File("core/src/coronavirus-deaths_latest.json").readText())
  val totalsByDate = deaths.countries.groupBy { it.reportingDate }.mapValues { (_, countries) ->
    val byName = countries.associateBy { it.areaName }
    (byName["England"]?.dailyChangeInDeaths ?: 0) + (byName["Wales"]?.dailyChangeInDeaths ?: 0)
  }
  totalsByDate.toList().sortedBy { it.first }.forEach { (date, total) ->
    println("\"$date\",$total")
  }
}

private fun parseCases(json: Json) {
  val cases =
    json.parse(Cases.serializer(), File("core/src/coronavirus-cases_latest.json").readText())
  val totalsByDate = cases.countries.groupBy { it.specimenDate }.mapValues { (_, countries) ->
    val byName = countries.associateBy { it.areaName }
    val england = byName["England"] ?: AreaCases("", "", "", 0, 0, 0)
    val wales = byName["Wales"] ?: AreaCases("", "", "", 0, 0, 0)
    val previous = (england.previouslyReportedDailyCases ?: 0) + (wales.previouslyReportedDailyCases ?: 0)
    val change = (england.changeInDailyCases ?: 0) + (wales.changeInDailyCases ?: 0)
    val confirmedCases = (england.dailyLabConfirmedCases ?: 0) + (wales.dailyLabConfirmedCases ?: 0)
    "$previous,$change,$confirmedCases"
  }
  totalsByDate.toList().sortedBy { it.first }.forEach { (date, data) ->
    println("$date,$data")
  }
}


private fun parseLondonCases(json: Json) {
  val cases =
    json.parse(Cases.serializer(), File("core/src/coronavirus-cases_latest.json").readText())
  val totalsByDate = cases.regions.groupBy { it.specimenDate }.mapValues { (_, countries) ->
    val byName = countries.associateBy { it.areaName }
    val london = byName["London"] ?: AreaCases("", "", "", 0, 0, 0)
    val confirmedCases = london.dailyLabConfirmedCases ?: 0
    "$confirmedCases"
  }
  totalsByDate.toList().sortedBy { it.first }.forEach { (date, data) ->
    println("$date,$data")
  }
}
