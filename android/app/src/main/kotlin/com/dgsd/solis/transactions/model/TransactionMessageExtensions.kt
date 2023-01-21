package com.dgsd.solis.transactions.model

import com.dgsd.ksol.core.model.TransactionMessage
import com.dgsd.ksol.core.programs.system.SystemProgram
import com.dgsd.ksol.core.programs.system.SystemProgramInstructionData

fun TransactionMessage.getSystemProgramInstruction(): SystemProgramInstructionData? {
  return runCatching {
    val instruction =
      instructions.firstOrNull { it.programAccount == SystemProgram.PROGRAM_ID }
    if (instruction == null) {
      null
    } else {
      SystemProgram.decodeInstruction(instruction.inputData)
    }
  }.getOrNull()
}