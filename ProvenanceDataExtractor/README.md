#### Provenance Data Extractor (PDE)

Provenance Data Extractor (PDE) is a DfAnalyzer component responsible for extracting provenance and scientific data from scientific applications. With this purpose, PDE delivers a RESTful API that users can send HTTP request with POST method in order to register data extracted from their applications. These extracted data follows a dataflow abstraction, considering file and data element flow monitoring. Then, PDE stores these extracted data into a provenance database to enable online query processing.

Since our RESTful application has been initialized, users can submit HTTP requests with POST method to PDE for extracting provenance and scientific data. Then, considering the dataflow abstraction followed by DfAnalyzer, users have to introduce a message according to this abstraction. Therefore, PDE provides a set of methods to be present in the HTTP message, as follows:

![PDE RESTful services](./img/dfa-docs-pde.png)

This web page (http://localhost:22000/dfview/help) can be accessed using a browser after the initialization of DfAnalyzer.

**Example**

**Dataflow specification**

As an example of dataflow specification to be included in our provenance database, users submit the following HTTP request to PDE, considering a dataflow with one data transformation of our libMesh application (systems_of_equations_ex2):

HTTP message send to the URL `http://localhost:22000/pde/dataflow`

```bash
dataflow(systems_of_equations_ex2)

program(systems_of_equations_ex2::solver.solve(), /root/systems_of_equations_ex2)

dataset(ocreate_equation_systems, {n_dofs}, {NUMERIC})
dataset(isolve_equation_systems, 
            {dt, timesteps, nonlinear_steps, nonlinear_tolerance, nu},
            {NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC})
dataset(osolve_equation_systems, 
            {timestep, time, nonlinear_steps, linear_iterations, final_linear_residual, norm_delta, converged},
            {NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC, TEXT})

transformation(solve_equation_systems, {ocreate_equation_systems, isolve_equation_systems}, {odeduplication}, )
```

**Task record**

After the dataflow specification, provenance and scientific data generated during the execution of scientific applications can be registered in our provenance database. In this case, PDE has to capture HTTP request in the task level (*i.e.*, execution of a data transformation).

For example, the following message should be sent in an HTTP request to the URL `http://localhost:22000/pde/task`

```bash
task(systems_of_equations,  solve_equation_systems, 1, RUNNING)

collection(ocreate_equation_systems, {{ dofs }})
collection(isolve_equation_systems, {{ timestep, time, nonlinear_steps, 
            linear_iterations, final_linear_residual, norm_delta, converged }})

dependency({create_equation_systems}, {1})
```

More details about DfAnalyzer RESTful services can be found [here](https://hpcdb.github.io/armful/dfanalyzer.html).