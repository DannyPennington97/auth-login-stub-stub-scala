# Auth login stub stub

This repo builds upon the HMRC auth-login-stub by simplifying the login journey for DDCELS services.

Currently only a few NW services are included.
Also it only works locally atm

Future plans:
- Support all (non admin) DDCELS services
- Be able to create sessions in Staging/QA

---

### Usage
The best way to run this is in Docker by using the `make start` target (ignore the error logs that apear when building, this seems to be a weird thing with the plugin I'm using). This will build the docker container and start it under the name `alss`.
You can then go to http://localhost:4200/ to reach the home page

N.B in order for the networking to work correctly in Docker it needs to reference your local machine IP rather than `localhost` hence why the `findip.sh` script is used by the docker commands in the Makefile.

#### Disclaimer
This is **not** an official HMRC service. It does however use the same styling (minus a few things to avoid copyright infringement) in order to maintain a feel consistent with the auth-login-stub