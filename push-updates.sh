#!/bin/bash
ant dist
cp dist/sens-o-matic.jar ../sens-o-matic-manager/lib/runtime/
cp dist/sens-o-matic.jar ../sens-o-matic-picker/lib/runtime/
cp dist/sens-o-matic.jar ../sens-o-matic-rabbit-api/lib/runtime/
