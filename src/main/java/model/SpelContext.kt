package model

object SpelContext {
  var spelData = SpelData()

  fun findGebruiker(spelerId: String?) = spelData
    .alleSpelers
    .firstOrNull { it.id == spelerId }

  fun findSpeler(spelerId: String?) = spelData
    .tafels
    .firstOrNull { containsSpeler(it, spelerId ?: "") }
    ?.spelers
    ?.firstOrNull { it.id == spelerId }

  private fun containsSpeler(it: Tafel, spelerId: String?) =
    it.spelers.filter { speler -> speler.id == spelerId ?: "" }.firstOrNull() != null
}
