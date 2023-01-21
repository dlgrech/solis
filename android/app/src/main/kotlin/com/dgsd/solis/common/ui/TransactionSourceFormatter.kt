package com.dgsd.solis.common.ui

import android.content.Context
import com.dgsd.khelius.common.model.TransactionSource
import com.dgsd.solis.R

class TransactionSourceFormatter(
  private val context: Context
) {

  fun format(transactionSource: TransactionSource): CharSequence {
    val resId = when (transactionSource) {
      TransactionSource.FORM_FUNCTION -> R.string.transaction_source_form_function
      TransactionSource.EXCHANGE_ART -> R.string.transaction_source_exchange_art
      TransactionSource.CANDY_MACHINE_V2 -> R.string.transaction_source_candy_machine_v2
      TransactionSource.CANDY_MACHINE_V1 -> R.string.transaction_source_candy_machine_v1
      TransactionSource.UNKNOWN -> R.string.transaction_source_unknown
      TransactionSource.SOLANART -> R.string.transaction_source_solanart
      TransactionSource.SOLSEA -> R.string.transaction_source_solsea
      TransactionSource.MAGIC_EDEN -> R.string.transaction_source_magic_eden
      TransactionSource.HOLAPLEX -> R.string.transaction_source_holaplex
      TransactionSource.METAPLEX -> R.string.transaction_source_metaplex
      TransactionSource.OPENSEA -> R.string.transaction_source_opensea
      TransactionSource.SOLANA_PROGRAM_LIBRARY -> R.string.transaction_source_solana_program_library
      TransactionSource.ANCHOR -> R.string.transaction_source_anchor
      TransactionSource.W_SOL -> R.string.transaction_source_w_sol
      TransactionSource.PHANTOM -> R.string.transaction_source_phantom
      TransactionSource.SYSTEM_PROGRAM -> R.string.transaction_source_system_program
      TransactionSource.STAKE_PROGRAM -> R.string.transaction_source_stake_program
      TransactionSource.COINBASE -> R.string.transaction_source_coinbase
      TransactionSource.CORAL_CUBE -> R.string.transaction_source_coral_cube
      TransactionSource.HEDGE -> R.string.transaction_source_hedge
      TransactionSource.LAUNCH_MY_NFT -> R.string.transaction_source_launch_my_nft
      TransactionSource.GEM_BANK -> R.string.transaction_source_gem_bank
      TransactionSource.GEM_FARM -> R.string.transaction_source_gem_farm
      TransactionSource.DEGODS -> R.string.transaction_source_degods
      TransactionSource.BLOCKSMITH_LABS -> R.string.transaction_source_blocksmith_labs
      TransactionSource.YAWWW -> R.string.transaction_source_yawww
      TransactionSource.ATADIA -> R.string.transaction_source_atadia
      TransactionSource.SOLPORT -> R.string.transaction_source_solport
      TransactionSource.HYPERSPACE -> R.string.transaction_source_hyperspace
      TransactionSource.DIGITAL_EYES -> R.string.transaction_source_digital_eyes
      TransactionSource.TENSOR -> R.string.transaction_source_tensor
      TransactionSource.BIFROST -> R.string.transaction_source_bifrost
      TransactionSource.JUPITER -> R.string.transaction_source_jupiter
      TransactionSource.MERCURIAL_STABLE_SWAP -> R.string.transaction_source_mercurial_stable_swap
      TransactionSource.SABER -> R.string.transaction_source_saber
      TransactionSource.SERUM -> R.string.transaction_source_serum
      TransactionSource.STEP_FINANCE -> R.string.transaction_source_step_finance
      TransactionSource.CROPPER -> R.string.transaction_source_cropper
      TransactionSource.RAYDIUM -> R.string.transaction_source_raydium
      TransactionSource.ALDRIN -> R.string.transaction_source_aldrin
      TransactionSource.CREMA -> R.string.transaction_source_crema
      TransactionSource.LIFINITY -> R.string.transaction_source_lifinity
      TransactionSource.CYKURA -> R.string.transaction_source_cykura
      TransactionSource.ORCA -> R.string.transaction_source_orca
      TransactionSource.MARINADE -> R.string.transaction_source_marinade
      TransactionSource.STEPN -> R.string.transaction_source_stepn
      TransactionSource.SENCHA_EXCHANGE -> R.string.transaction_source_sencha_exchange
      TransactionSource.SAROS -> R.string.transaction_source_saros
      TransactionSource.ENGLISH_AUCTION_AUCTION -> R.string.transaction_source_english_auction_auction
      TransactionSource.FOXY -> R.string.transaction_source_foxy
      TransactionSource.FOXY_STAKING -> R.string.transaction_source_foxy_staking
      TransactionSource.ZETA -> R.string.transaction_source_zeta
      TransactionSource.HADESWAP -> R.string.transaction_source_hadeswap
    }

    return context.getString(resId)
  }
}
