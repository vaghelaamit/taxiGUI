package com.easypay.easypaypos.entities

import java.io.Serializable

/**
 * Created by ADMIN on 7/21/2016.
 */
class PaymentModeEntity : Serializable {

    var billerCode: String?=null
    var miniAmount: Int = 0
    var maxiAmount: Int = 0
    var displayName: String?=null
}
