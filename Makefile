
build:
	sbt docker:publishLocal

run:
	docker run -d --network="host" --name="alss" -e ALSHOST="https://staging.tax.service.gov.uk" auth-login-stub-stub-scala:0.0.1

runLocal:
	docker run -d --network="host" --name="alss" -e ALSHOST="http://192.168.0.2:9949" auth-login-stub-stub-scala:0.0.1

stop:
	docker stop alss

rm:	stop
	docker container rm alss

start: build runLocal