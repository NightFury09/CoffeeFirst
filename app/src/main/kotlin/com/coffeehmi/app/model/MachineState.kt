package com.coffeehmi.app.model

sealed class MachineState {
    object Idle : MachineState()
    object Heating : MachineState()
    object Grinding : MachineState()
    object Brewing : MachineState()
    object Dispensing : MachineState()
    data class Error(val message: String) : MachineState()
    object Maintenance : MachineState()
}
