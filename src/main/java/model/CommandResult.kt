package model

data class CommandResult(
  val status: CommandStatus,
  val errorMessage: String
)
