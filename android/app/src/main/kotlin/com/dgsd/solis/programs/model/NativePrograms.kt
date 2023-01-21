package com.dgsd.solis.programs.model

import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.ksol.core.programs.*
import com.dgsd.ksol.core.programs.memo.MemoProgram
import com.dgsd.ksol.core.programs.system.SystemProgram

object NativePrograms {

  val TOKEN_PROGRAM =
    PublicKey.fromBase58("TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA")
  val NATIVE_LOADER =
    PublicKey.fromBase58("NativeLoader1111111111111111111111111111111")

  val nativeProgramKeys = setOf(
    BPFLoaderProgram.PROGRAM_ID,
    ConfigProgram.PROGRAM_ID,
    Ed25519Program.PROGRAM_ID,
    Secp256k1Program.PROGRAM_ID,
    StakeProgram.PROGRAM_ID,
    SystemProgram.PROGRAM_ID,
    VoteProgram.PROGRAM_ID,
    MemoProgram.PROGRAM_ID,
    NATIVE_LOADER,
    TOKEN_PROGRAM,
  )

  fun isNativeProgram(publicKey: PublicKey): Boolean {
    return publicKey in nativeProgramKeys
  }
}