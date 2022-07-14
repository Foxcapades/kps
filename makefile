KPD_SRC_ROOT = "kpd/src/main/kotlin/io/foxcapades/lib/kps/kpd"
KPI_SRC_ROOT = "kpi/src/commonMain/kotlin/io/foxcapades/lib/kps/kpi"
TYPES        = "Boolean Char Double Float Int Long Short UByte UInt ULong UShort"


.PHONY: main
main:
	@echo "WHAT ARE YOU DOING?!?!!"


gen-all-source: \
	kpd-gen-base-deques

rm-all-gen-source: \
	kpd-rm-base-deques


kpd-gen-base-deques:
	@for i in "$(TYPES)" ; do \
      cp $(KPD_SRC_ROOT)/base/AbstractByteDeque.kt $(KPD_SRC_ROOT)/base/Abstract$${i}Deque.kt; \
      sed -i "s/Byte/$${i}/g" $(KPD_SRC_ROOT)/base/Abstract$${i}Deque.kt; \
	done

kpd-rm-base-deques:
	@for i in "$(TYPES)" ; do \
      rm -f $(KPD_SRC_ROOT)/base/Abstract$${i}Deque.kt; \
	done
