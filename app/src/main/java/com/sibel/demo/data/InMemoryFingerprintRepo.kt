package com.sibel.demo.data
import com.sibel.demo.domain.entities.FingerFeature
import com.sibel.demo.domain.gateway.FingerprintGateway

class InMemoryFingerprintRepo : FingerprintGateway {
    private val map = mutableMapOf<Int, ByteArray>()
    override fun enroll(feature: FingerFeature) = map.put(feature.id, feature.bytes).let { true }
    override fun verify(id: Int, feature: ByteArray) = map[id]?.contentEquals(feature) == true
}
