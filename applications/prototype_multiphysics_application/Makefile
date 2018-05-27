init:
	pip install noworkflow[all]
	cd ../../library/dfa-lib-python && $(MAKE) install

run-baseline:
	cd baseline && python3 prototype_multiphysics_application.py && cd ..
	
run-dfa:
	cd dfa-lib-python && python3 prototype_multiphysics_application.py && cd ..

run-noworkflow:
	cd noWorkflow && now run -v prototype_multiphysics_application.py && cd ..