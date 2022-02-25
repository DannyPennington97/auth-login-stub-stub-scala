
build:
	sbt docker:publishLocal

run:
	docker run -d --network="host" --name="alss" -p 4200:4200 auth-login-stub-stub-scala:0.0.1