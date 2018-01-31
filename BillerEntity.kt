package com.easypay.easypaypos.entities

import java.io.Serializable

/**
 * Created by ADMIN on 7/21/2016.
 */
class BillerEntity : Serializable {

    var billerCode: String? = null
    var billerAckDays: Int = 0
    var billerName: String? = null
    var billerShortName: String? = null
    var logoPath: String? = null
    var categoryLogoPath: String? = null
    var smallLogoPath: String? = null
    var isBillerLogoReceiptFlag: Boolean = false
    var isBillerBarcodeFlag: Boolean = false
    var billerBarcode: String? = null
    var isEpPromoFlag: Boolean = false
    var isBillerNationalFlag: Boolean = false
    var chargesList: List<ChargeEntity>? = null
    var cityList: List<CityEntity>? = null
    var paymentModeList: List<PaymentModeEntity>? = null
    var billerStatus: Int = 0
    var isParentBillerFlag: Boolean = false
    var isFavoriteBiller: Boolean = false
    var isReceiptConfFlag: Boolean = false
    var billerCategoryDetail: CategoryEntity? = null

}
