[![Build Status](https://travis-ci.org/openmrs/openmrs-module-sync2.svg?branch=master)](https://travis-ci.org/openmrs/openmrs-module-sync2)
# openmrs-module-sync2

Description
-----------
A new implementation Sync module that uses FHIR and atom feed. Supports parent-child synchronization of multiple OpenMRS servers in one enterprise, where most (technical) management is done at a central site, but patients are seen at many clinics in a hospital network.

Development
-----------
Install latest Docker.

To create or update a sync server (parent or child) run:
mvn openmrs-skd:build-distro -Ddir=docker
cd docker
docker-compose up --build

The '--build' flag forces to rebuild a docker image, if you are updating the server.

You can adjust ports in the .env file.
If you want to remote debug add DEBUG=True in the .env file.

Test servers
------------
https://sync1.openmrs.org/
https://sync2.openmrs.org/
https://sync3.openmrs.org/
https://sync4.openmrs.org/

These servers can be redeployed through the https://ci.openmrs.org/browse/REFAPP-DS plan. Please e-mail helpdesk for a bamboo account.

Each time servers are redeployed, data is preserved. If you want to wipe out data before redeploy change the destroy.data variable at https://ci.openmrs.org/chain/admin/config/configureChainVariables.action?buildKey=REFAPP-DS

If you want to include additional modules or change versions, please update the openmrs-distro.properties file.

Installation
------------
1. Build the module to produce the .omod file.
2. Use the OpenMRS Administration > Manage Modules screen to upload and install the .omod file.

