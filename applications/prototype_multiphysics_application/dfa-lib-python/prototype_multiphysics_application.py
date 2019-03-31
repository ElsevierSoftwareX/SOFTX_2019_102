import random
import xml.etree.ElementTree as ET
from fenics import *
from dolfin import *
from dfa_lib_python.dataflow import Dataflow
from dfa_lib_python.transformation import Transformation
from dfa_lib_python.attribute import Attribute
from dfa_lib_python.attribute_type import AttributeType
from dfa_lib_python.set import Set
from dfa_lib_python.set_type import SetType
from dfa_lib_python.task import Task
from dfa_lib_python.dataset import DataSet
from dfa_lib_python.element import Element
from dfa_lib_python.task_status import TaskStatus
from dfa_lib_python.extractor_extension import ExtractorExtension


# Class representing the intial conditions
class InitialConditions(UserExpression):
    def __init__(self, **kwargs):
        random.seed(2 + MPI.rank(mpi_comm_world()))
        if has_pybind11():
            super().__init__(**kwargs)
    def eval(self, values, x):
        values[0] = 0.63 + 0.02*(0.5 - random.random())
        values[1] = 0.0
    def value_shape(self):
        return (2,)

# Raw data extraction
def Extractor(cartridge, file):
    tree = ET.parse(file)
    root = tree.getroot()
    result = []
    for child in root:
        for c in child:
            r = []
            for v in c.attrib.values():
                r.append(v)
            result.append(r)
    return result


# Class for interfacing with the Newton solver
class CahnHilliardEquation(NonlinearProblem):
    def __init__(self, a, L):
        NonlinearProblem.__init__(self)
        self.L = L
        self.a = a
    def F(self, b, x):
        assemble(self.L, tensor=b)
    def J(self, A, x):
        assemble(self.a, tensor=A)

# Form compiler options
parameters["form_compiler"]["optimize"]     = True
parameters["form_compiler"]["cpp_optimize"] = True
parameters["form_compiler"]["representation"] = "quadrature"

dataflow_tag = "fenics-df"
df = Dataflow(dataflow_tag)

# Prospective provenance capture
# Transformation MeshCreation
tf1 = Transformation("MeshCreation")
tf1_input = Set("iMeshCreation", SetType.INPUT, 
    [Attribute("HEIGHT", AttributeType.NUMERIC), 
    Attribute("WIDTH", AttributeType.NUMERIC)])
tf1_output = Set("oMeshCreation", SetType.OUTPUT, 
    [Attribute("VERTICES", AttributeType.NUMERIC), 
    Attribute("CELLS", AttributeType.NUMERIC)])
tf1.set_sets([tf1_input, tf1_output])
df.add_transformation(tf1)
# Transformation FunctionSpace
tf2 = Transformation("FunctionSpace")
tf2_input = Set("iFunctionSpace", SetType.INPUT, 
    [Attribute("FAMILY", AttributeType.TEXT), 
    Attribute("DEGREE", AttributeType.NUMERIC)])
tf2_output = Set("oFunctionSpace", SetType.OUTPUT, 
    [Attribute("DIMENSION", AttributeType.NUMERIC)])
tf1_output.set_type(SetType.INPUT)
tf1_output.dependency=tf1._tag
tf2.set_sets([tf1_output, tf2_input, tf2_output])
df.add_transformation(tf2)
# Transformation NewtonSolver
tf3 = Transformation("NewtonSolver")
tf3_input = Set("iNewtonSolver", SetType.INPUT, 
    [Attribute("SOLVER", AttributeType.TEXT), 
    Attribute("CONVERGENCE_CRITERIA", AttributeType.TEXT), 
    Attribute("RELATIVE_TOLERANCE", AttributeType.NUMERIC)])
tf3_output = Set("oNewtonSolver", SetType.OUTPUT, 
    [Attribute("LINEAR_SOLVER", AttributeType.TEXT),
    Attribute("CONVERGENCE_CRITETION", AttributeType.TEXT),
    Attribute("RELATIVE_TOLERANCE", AttributeType.NUMERIC)])
tf2_output.set_type(SetType.INPUT)
tf2_output.dependency=tf2._tag
tf3.set_sets([tf2_output, tf3_input, tf3_output])
df.add_transformation(tf3)
# Transformation TimeStep
tf4 = Transformation("TimeStep")
tf4_input = Set("iTimeStep", SetType.INPUT, 
    [Attribute("ACTUAL_VALUE", AttributeType.NUMERIC),
    Attribute("GROWTH_RATE", AttributeType.NUMERIC)])
tf4_output = Set("oTimeStep", SetType.OUTPUT, 
    [Attribute("CONVERGENCE", AttributeType.TEXT),
    Attribute("ITERATION", AttributeType.NUMERIC),
    Attribute("RESIDUAL", AttributeType.NUMERIC)])
tf3_output.set_type(SetType.INPUT)
tf3_output.dependency=tf3._tag
tf4.set_sets([tf3_output, tf4_input, tf4_output])
df.add_transformation(tf4)
# Transformation Visualization
tf5 = Transformation("Visualization")
tf5_input = Set("iVisualization", SetType.INPUT, 
    [Attribute("OUTPUT_FILE", AttributeType.FILE)])
tf5_output = Set("oVisualization", SetType.OUTPUT, 
    [Attribute("TIMESTEP", AttributeType.TEXT),
    Attribute("FILE", AttributeType.FILE),
    Attribute("PART", AttributeType.TEXT)])
tf4_output.set_type(SetType.INPUT)
tf4_output.dependency=tf4._tag
tf5.set_sets([tf4_output, tf5_input, tf5_output])
df.add_transformation(tf5)
df.save()

###############################################################
# Mesh Creation
##############################################################
t1 = Task(1, dataflow_tag, "MeshCreation")
t1_input = DataSet("iMeshCreation", [Element([96, 96])])
t1.add_dataset(t1_input)
t1.begin()
#-------------------------------------------------------------
# Create mesh
mesh = UnitSquareMesh(96, 96)
#--------------------------------------------------------------
t1_output= DataSet("oMeshCreation", [Element([mesh.num_vertices(), mesh.num_cells()])])
t1.add_dataset(t1_output)
t1.end()
###############################################################
# Function Space
###############################################################
t2 = Task(2, dataflow_tag, "FunctionSpace", dependency=t1)
t2_input = DataSet("iFunctionSpace", [Element(["Lagrange", 1])])
t2.add_dataset(t2_input)
t2.begin()
#--------------------------------------------------------------
# Define function spaces
V = FiniteElement("Lagrange", mesh.ufl_cell(), 1)
ME = FunctionSpace(mesh, V*V)
#--------------------------------------------------------------
t2_output= DataSet("oFunctionSpace", [Element([ME.dim()])])
t2.add_dataset(t2_output)
t2.end()
###############################################################
# Define trial and test functions
du    = TrialFunction(ME)
q, v  = TestFunctions(ME)

# Define functions
u   = Function(ME)  # current solution
u0  = Function(ME)  # solution from previous converged step

# Split mixed functions
dc, dmu = split(du)
c,  mu  = split(u)
c0, mu0 = split(u0)

# Create intial conditions and interpolate
u_init = InitialConditions(degree=1)
u.interpolate(u_init)
u0.interpolate(u_init)

# Compute the chemical potential df/dc
c = variable(c)
f    = 100*c**2*(1-c)**2
dfdc = diff(f, c)

# Model parameters
lmbda  = 1.0e-02  # surface parameter
dt     = 5.0e-06  # time step
theta  = 0.5      # time stepping family, e.g. theta=1 -> backward Euler, theta=0.5 -> Crank-Nicolson

# mu_(n+theta)
mu_mid = (1.0-theta)*mu0 + theta*mu

# Weak statement of the equations
L0 = c*q*dx - c0*q*dx + dt*dot(grad(mu_mid), grad(q))*dx
L1 = mu*v*dx - dfdc*v*dx - lmbda*dot(grad(c), grad(v))*dx
L = L0 + L1

# Compute directional derivative about u in the direction of du (Jacobian)
a = derivative(L, u, du)

# Create nonlinear problem and Newton solver
problem = CahnHilliardEquation(a, L)
###############################################################
# Newton Solver
###############################################################
t3 = Task(3, dataflow_tag, "NewtonSolver", dependency = t2)
t3_input = DataSet("iNewtonSolver", [Element(["lu", "incremental", 1e-6])])
t3.add_dataset(t3_input)
t3.begin()
#--------------------------------------------------------------
# Define Newton solver
solver = NewtonSolver()
solver.parameters["linear_solver"]         = "gmres"
solver.parameters["convergence_criterion"] = "incremental"
solver.parameters["relative_tolerance"]    = 1e-6
#--------------------------------------------------------------
t3_output = DataSet("oNewtonSolver", [Element(["gmres", "incremental", 1e-6])])
t3.add_dataset(t3_output)
t3.end()

###############################################################
# Time loop
###############################################################
# Output file
file = File("output.pvd", "compressed")

# Step in time
t = 0.0
T = 50*dt

prev = t3;
i = 1
while (t < T):
    t += dt
    ###############################################################
    # Time Step
    ###############################################################
    current_time_step = Task(int(t3._id)+i ,dataflow_tag,"TimeStep", dependency=prev)
    current_time_step_input = DataSet("iTimeStep", [Element([t,dt])])
    current_time_step.add_dataset(current_time_step_input)
    current_time_step.begin()
    #--------------------------------------------------------------
    # Solver execution
    u0.vector()[:] = u.vector()
    iter_count, converged_flag = solver.solve(problem, u.vector())
    #--------------------------------------------------------------
    current_time_step_output = DataSet("oTimeStep", [Element([converged_flag,iter_count,solver.residual()])])
    current_time_step.add_dataset(current_time_step_output)
    current_time_step.end()
    ###############################################################
    # Visualization
    ###############################################################
    visualization = Task(int(current_time_step._id) + 1, dataflow_tag, "Visualization", dependency=current_time_step)
    visualization_input = DataSet("iVisualization", [Element(["output.pvd"])])
    visualization.add_dataset(visualization_input)
    visualization.begin()
    #--------------------------------------------------------------
    # Solver execution
    file << (u.split()[0], t)
    #--------------------------------------------------------------
    # Raw data extraction
    extracted_data = Extractor(ExtractorExtension.PROGRAM, "output.pvd")
    #--------------------------------------------------------------
    visualization_output = DataSet("oVisualization", [Element(extracted_data[i-1])])
    visualization.add_dataset(visualization_output)
    visualization.end()
    ###############################################################
    i+=1
    

