package com.sibel.demo.domain.gateway
import com.sibel.demo.domain.entities.FingerFeature
interface FingerprintGateway {
    fun enroll(feature: FingerFeature): Boolean
    fun verify(id: Int, feature: ByteArray): Boolean
}
