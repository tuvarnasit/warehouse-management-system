#!/bin/bash
export SSO_CREDENTIALS_FILE=credentials.txt
export DB_USERNAME=admin
export DB_CONNECTION_URL=jdbc:mysql://wms.cxciwfojwted.eu-west-1.rds.amazonaws.com/dev
export DB_PASSWORD='DadK8&5x6RyfHk^x'
./gradlew run
