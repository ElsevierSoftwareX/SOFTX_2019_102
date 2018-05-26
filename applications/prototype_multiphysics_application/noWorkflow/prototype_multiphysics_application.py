import random
from fenics import *
from dolfin import *


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


###############################################################
# Mesh Creation
##############################################################
#-------------------------------------------------------------
# Create mesh
mesh = UnitSquareMesh(96, 96)
#--------------------------------------------------------------
###############################################################
# Function Space
###############################################################
#--------------------------------------------------------------
# Define function spaces
V = FiniteElement("Lagrange", mesh.ufl_cell(), 1)
ME = FunctionSpace(mesh, V*V)
#--------------------------------------------------------------
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
#--------------------------------------------------------------
# Define Newton solver
solver = NewtonSolver()
solver.parameters["linear_solver"]         = "gmres"
solver.parameters["convergence_criterion"] = "incremental"
solver.parameters["relative_tolerance"]    = 1e-6
#--------------------------------------------------------------
###############################################################
# Time loop
###############################################################
# Output file
file = File("output.pvd", "compressed")

# Step in time
t = 0.0
T = 50*dt

i = 1
while (t < T):
    t += dt
    ###############################################################
    # Time Step
    ###############################################################
    #--------------------------------------------------------------
    # Solver execution
    u0.vector()[:] = u.vector()
    iter_count, converged_flag = solver.solve(problem, u.vector())
    #--------------------------------------------------------------
    ###############################################################
    # Visualization
    ###############################################################
    #--------------------------------------------------------------
    # Solver execution
    file << (u.split()[0], t)
    #--------------------------------------------------------------    
    ###############################################################
    i+=1
