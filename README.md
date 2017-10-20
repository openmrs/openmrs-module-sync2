[![Build Status](https://travis-ci.org/openmrs/openmrs-module-sync2.svg?branch=master)](https://travis-ci.org/openmrs/openmrs-module-sync2)
# openmrs-module-sync2

Description
-----------
A new implementation Sync module that uses FHIR and atom feed. Supports parent-child synchronization of multiple OpenMRS servers in one enterprise, where most (technical) management is done at a central site, but patients are seen at many clinics in a hospital network.


Installation
------------
1. Build the module to produce the .omod file.
2. Use the OpenMRS Administration > Manage Modules screen to upload and install the .omod file.
