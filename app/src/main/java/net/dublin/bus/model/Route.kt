package net.dublin.bus.model

data class Route(
        var description: String? = null,
        var direction: String? = null,
        var from: String? = null,
        var inboundFrom: String? = null,
        var inboundPattern: String? = null,
        var inboundTowards: String? = null,
        var inboundVia: String? = null,
        var isMinimumFare: Boolean = false,
        var isNitelink: Boolean = false,
        var isStaged: Boolean = false,
        var isXpresso: Boolean = false,
        var number: String? = null,
        var outboundFrom: String? = null,
        var outboundPattern: String? = null,
        var outboundTowards: String? = null,
        var outboundVia: String? = null,
        var seqNumber: String? = null,
        var stops: List<Stop>? = null,
        var towards: String? = null
)