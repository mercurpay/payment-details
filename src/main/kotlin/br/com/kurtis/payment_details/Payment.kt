package br.com.kurtis.payment_details

import java.math.BigDecimal
import java.util.*

data class Payment(var id: UUID? = null, var label: String? = null, var amount: Amount? = null)

data class Amount(var currency: String? = null, var value: BigDecimal? = null)