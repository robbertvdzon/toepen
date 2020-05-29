package model

data class CommandResult(
        var status: CommandStatus,
        var errorMessage: String
)
