KPD_SRC_ROOT = "kpd/src/main/kotlin/io/foxcapades/lib/kps/kpd"
KPI_SRC_ROOT = "kpi/src/commonMain/kotlin/io/foxcapades/lib/kps/kpi"
TYPES        = "Boolean Char Double Float Int Long Short UByte UInt ULong UShort"


.PHONY: main
main:
	@echo "WHAT ARE YOU DOING?!?!!"


gen-all-source: \
	kpd-gen-base-deques

gen-latest-docs:
	# Generate the docs
	@gradle dokkaHtml

	# Switch to the docs branch
	@git checkout docs

	# Clean out the existing "latest" dirs
	@rm -rf dokka/kpd/latest

	# Make sure the dokka dir exists
	@mkdir -p dokka

	# Move the documentation files over
	@mv build/docs/kpd dokka/kpd/latest

	# Add new files to git.
	@git add dokka/kpd/latest

	# Commit the changes
	@git commit -m 'update latest docs'

	# Push up the changes
	@git push

	# Switch back to the main branch
	@git checkout main


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
