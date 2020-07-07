package goos

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.net.URI
import java.net.URL
import java.time.LocalDate

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
) {
  val dailyCases = dailyLabConfirmedCases ?: 0
}

@Serializable
data class Cases(
  val countries: List<AreaCases>,
  val regions: List<AreaCases>,
  val ltlas: List<AreaCases>
)

val rootUri = URI("https://c19downloads.azureedge.net/downloads/json")

@ExperimentalStdlibApi
fun main() {
  val json = Json(JsonConfiguration.Default.copy(ignoreUnknownKeys = true))
//  parseDeaths(json)
//  parseCases(json)
//  parseLondonCases(json)
  parseLocalCases(json)
}

private fun parseDeaths(json: Json) {
  val deaths =
    json.parse(Deaths.serializer(), rootUri.resolve("coronavirus-deaths_latest.json").toURL().readText())
  val totalsByDate = deaths.countries.groupBy { it.reportingDate }.mapValues { (_, countries) ->
    val byName = countries.associateBy { it.areaName }
    (byName["England"]?.dailyChangeInDeaths ?: 0) + (byName["Wales"]?.dailyChangeInDeaths ?: 0)
  }
  totalsByDate.toList().sortedBy { it.first }.forEach { (date, total) ->
    println("\"$date\",$total")
  }
}

private fun parseCases(json: Json) {
  val cases = loadCases(json)
  val totalsByDate = cases.countries
    .groupByDateAndName()
    .mapValues { (_, byName) ->
      val england = byName.getCases("England")
      val wales = byName.getCases("Wales")
      val previous = (england.previouslyReportedDailyCases ?: 0) + (wales.previouslyReportedDailyCases ?: 0)
      val change = (england.changeInDailyCases ?: 0) + (wales.changeInDailyCases ?: 0)
      val confirmedCases = england.dailyCases + wales.dailyCases
      "$previous,$change,$confirmedCases"
    }
  totalsByDate.toList().sortedBy { it.first }.forEach { (date, data) ->
    println("$date,$data")
  }
}


private fun parseLondonCases(json: Json) {
  val cases = loadCases(json)
  val totalsByDate = cases.regions
    .groupByDateAndName()
    .mapValues { (_, byName) ->
      val london = byName.getCases("London")
      london.dailyCases.toString()
    }
  totalsByDate.toList().sortedBy { it.first }.forEach { (date, data) ->
    println("$date,$data")
  }
}

private fun parseLocalCases(json: Json) {
  val cases = loadCases(json)
  val totalsByDate = cases.ltlas
    .groupByDateAndName()
    .mapValues { (_, areaCasesNyName) ->
      val richmond = areaCasesNyName.getCases("Richmond upon Thames")
      val hounslow = areaCasesNyName.getCases("Hounslow")
      val kingston = areaCasesNyName.getCases("Kingston upon Thames")
      val confirmedCases = richmond.dailyCases + hounslow.dailyCases + kingston.dailyCases
      confirmedCases.toString()
    }
  totalsByDate.toList().sortedBy { it.first }.forEach { (date, data) ->
    println("$date,$data")
  }
}

private fun List<AreaCases>.groupByDateAndName(): Map<LocalDate, Map<String, AreaCases>> {
  return groupBy { LocalDate.parse(it.specimenDate) }
    .mapValues { (_, areaCases) -> areaCases.associateBy { it.areaName } }
}

private fun loadCases(json: Json) =
  json.parse(
    Cases.serializer(),
    rootUri.resolve("coronavirus-cases_latest.json").toURL().readText()
  )

private fun Map<String, AreaCases>.getCases(
  name: String
) = this[name] ?: AreaCases("", name, "", 0, 0, 0)
