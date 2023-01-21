package com.dgsd.solis.common.ui

import android.content.Context
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.ksol.core.programs.*
import com.dgsd.ksol.core.programs.memo.MemoProgram
import com.dgsd.ksol.core.programs.system.SystemProgram
import com.dgsd.solis.R
import com.dgsd.solis.programs.model.NativePrograms

class PublicKeyFormatter(
  private val context: Context
) {

  fun format(publicKey: PublicKey): CharSequence {
    val friendlyName = getProgramDisplayName(publicKey)
    return if (friendlyName == null) {
      publicKey.toBase58String()
    } else {
      "$friendlyName (${abbreviate(publicKey)})"
    }
  }

  fun formatShort(publicKey: PublicKey): CharSequence {
    return getProgramDisplayName(publicKey) ?: publicKey.toBase58String()
  }

  fun formatLong(publicKey: PublicKey): CharSequence {
    val friendlyName = getProgramDisplayName(publicKey)
    return if (friendlyName == null) {
      publicKey.toBase58String()
    } else {
      "$friendlyName (${publicKey.toBase58String()})"
    }
  }

  fun abbreviate(publicKey: PublicKey): CharSequence {
    val base58 = publicKey.toBase58String()
    val prefix = base58.take(5)
    val suffix = base58.takeLast(5)

    return "$prefix…$suffix"
  }

  fun getProgramDisplayName(publicKey: PublicKey): String? {
    return when (publicKey) {
      BPFLoaderProgram.PROGRAM_ID -> context.getString(R.string.key_display_bpf_loader_program)
      ConfigProgram.PROGRAM_ID -> context.getString(R.string.key_display_config_program)
      Ed25519Program.PROGRAM_ID -> context.getString(R.string.key_display_ed25519_program)
      Secp256k1Program.PROGRAM_ID -> context.getString(R.string.key_display_secp256k1_program)
      StakeProgram.PROGRAM_ID -> context.getString(R.string.key_display_stake_program)
      SystemProgram.PROGRAM_ID -> context.getString(R.string.key_display_system_program)
      VoteProgram.PROGRAM_ID -> context.getString(R.string.key_display_vote_program)
      MemoProgram.PROGRAM_ID -> context.getString(R.string.key_display_memo_program)
      NativePrograms.TOKEN_PROGRAM -> context.getString(R.string.key_display_token_program)
      NativePrograms.NATIVE_LOADER -> context.getString(R.string.key_display_native_loader_program)
      else -> null
    }
  }
}
