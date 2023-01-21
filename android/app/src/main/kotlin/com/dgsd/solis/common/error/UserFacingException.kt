package com.dgsd.solis.common.error


/**
 * [RuntimeException] subclass whos message can be presented to the user
 */
class UserFacingException(
  val userVisibleMessage: String
) : RuntimeException(userVisibleMessage)