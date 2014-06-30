#!/bin/bash
ant dist

echo "copying to 'sens-o-matic-manager' ..."
cp dist/sens-o-matic-with-cassandra.jar ../sens-o-matic-manager/lib/runtime/

echo "copying to 'sens-o-matic-picker' ..."
cp dist/sens-o-matic-with-cassandra.jar ../sens-o-matic-picker/lib/runtime/

echo "copying to 'sens-o-matic-rabbit-api' ..."
cp dist/sens-o-matic.jar ../sens-o-matic-rabbit-api/lib/runtime/

echo "copying to 'sens-o-matic-demo' ..."
cp dist/sens-o-matic.jar ../sens-o-matic-demo/lib/runtime/

echo "copying to 'sens-o-matic-dashboard' ..."
cp dist/sens-o-matic.jar ../sens-o-matic-dashboard/lib/runtime/
