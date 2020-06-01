package model

object SpelContext {
  var spelData = SpelData()

  fun updateSpelData(spelData_: SpelData): SpelData{
    spelData = spelData_
    return spelData_
  }

}
