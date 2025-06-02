CONTIKI_PROJECT = nullnet-unicast nullnet-broadcast cooja-test sniffer transmitter
all: $(CONTIKI_PROJECT)

CONTIKI = ../..

#use this to enable TSCH: MAKE_MAC = MAKE_MAC_TSCH
# MAKE_MAC ?= MAKE_MAC_CSMA
# MAKE_NET = MAKE_NET_NULLNET

MAKE_MAC = MAKE_MAC_NULLMAC_SKY
MAKE_NET = MAKE_NET_NULLNET
MAKE_ROUTING = MAKE_ROUTING_NULLROUTING

include $(CONTIKI)/Makefile.include
