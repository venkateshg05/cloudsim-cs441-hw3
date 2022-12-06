# cloudsim-cs441-hw3

Author: Venkatesh Gopalakrishnan

**About the project**<br>
<p>The project explores setting up a cloud organisation using Cloudsim Plus. I've created 3 datacenters in each simulation. The datacenters are connected in a bus topology. 
Each have N hosts with different configurations and pricing policies as defined in the resources/.conf files.
A broker receives the cloudlets to be processed and assigns them to the appropriate VM that will be run on one
of the hosts (physical machines) in the datacenters. I explore how different VM allocation policy 
(Simple vs FirstFit vs BestFit vs RoundRobin) to the hosts and cloudlet scheduling policies (Time shared vs 
Space shared) on the VMs lead to different results. I also explore how having datacenters that offer varying levels 
of SAAS, FAAS, PAAS & IAAS offerings results in different outcomes
</p>

****

**Running the project**<br>
To run the project, follow the below steps:
<ul>
<li>Download or connect to this git repo through IntelliJ</li>
<li>In IntelliJ, the main driver function is in MainDriver.scala. Running that will trigger all simulations</li>
<li>Alt, you can run _**sbt clean run**_ from terminal from the root folder of the project</li>
<li>
Guide for using configuration files:
<ul>
<li>All .conf files are under resources</li>
<li>
application.conf has the configs defined to run sims for VM alloc policy and Cloudlet sch policy. 
You can edit the utilization ratio, Host configs, VM configs & cloudlet configs
</li>
<li>
dcServicesSimulation.conf defines the configs for simulating a cloud org that offers 3 datacenters with 
varying mix of SAAS, PAAS, IAAS & FAAS. For each DC, you can edit the utilization ratio, Host configs, 
VM configs & cloudlet configs. 
</li>
<li>
The dcPaasSim.conf is assumed to be a client config for their cloudlets as part of their PAAS agreement
</li>
<li>
The dcIaasSim.conf is assumed to be a client config for their VMs & cloudlets as part of their IAAS agreement
</li>
</ul>
</li>
</ul>

****

**Simulation Results & Analysis**<br>
**Effect of VM Allocation Policies**
<p>
The results for the 4 different VM alloc policies are shown in below tables. The main observations are:<br>
</p>
<ul>
<li>RoundRobin seems to be not the most ideal choice as some cloudlets are loosing out on having the resources
needed to execute</li>
<li>Both BestFit and FirstFit seem to be performing better in this scenario</li>
</ul>

_**[Insert sim results]**_

**Effect of Cloudlet Scheduling Policies**
<p>
The results for the 2 different Cloudlet scheduling policies are shown in below tables. The main observations are:<br>
</p>
<ul>
<li>SpaceShared policy seems to be not the ideal choice as some cloudlets could not be run due to lack of
space needed to execute them leading to a longer wait time for execution
</li>
<li>
TimeSahred policy gives a better utilization of resources in this case
</li>
</ul>

_**[Insert sim results]**_

**Effect of Datacenters with varying mix of services**
<p>
The results are shown in below tables. The main observations are:<br>
</p>
<ul>
<li>
Having separate DCs that are have light machines helps in increased efficiency for processing FAAS & SAAS based 
requests as these are usually requests that don't require high processing power and RAM 
</li>
<li>
Having provisions for PAAS helps bring down the cost by being able to configure the approx resource utilization 
your program needs
</li>
<li>
Having provisions for IAAS helps in designing VMs that ensure that your resource utilization can be controlled
so that the exact virtual resources can be created and how the resources are utilized by your program can be specified
</li>
</ul>

_**[Insert sim results]**_

