IP := $(shell ./scripts/findip.sh)
VERSION := $(shell ./scripts/getVersion.sh)

build:
	sbt docker:publishLocal

run:
	docker run -d --network="host" --name="alss" -e ALSHOST="https://staging.tax.service.gov.uk" auth-login-stub-stub-scala:$(VERSION)

runLocal:
	docker run -d --network="host" --name="alss" -e ALSHOST="http://$(IP):9949" auth-login-stub-stub-scala:$(VERSION)

stop:
	docker stop alss

rm:	stop
	docker container rm alss

start: build runLocal
