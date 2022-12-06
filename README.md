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

_**Simple allocation policy**_<br>
<img width="643" alt="image" src="https://user-images.githubusercontent.com/20476080/205841212-8e2233ae-c6f6-4f3f-b6fe-29085060dbb2.png">
<br>_**FirstFit allocation policy**_<br>
<img width="646" alt="image" src="https://user-images.githubusercontent.com/20476080/205841396-d3b90955-d660-4667-baa4-a353a46cb12a.png">
<br>_**BestFit allocation policy**_<br>
<img width="632" alt="image" src="https://user-images.githubusercontent.com/20476080/205841540-95782f9c-7474-4db1-befa-09ff88e5eb67.png">
<br>_**RoundRobin allocation policy**_<br>
<img width="634" alt="image" src="https://user-images.githubusercontent.com/20476080/205841581-18024ae6-df4e-4885-ba28-aecc934bc0c4.png">



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

_**TimeShared**_<br>
<img width="635" alt="image" src="https://user-images.githubusercontent.com/20476080/205841978-5cf18d62-2d2a-4381-b750-9f835f67cab3.png">
<br>_**SpaceShared**_<br>
<img width="643" alt="image" src="https://user-images.githubusercontent.com/20476080/205842036-0e0633c6-4621-4674-94b3-820aa11e9e38.png"> <br>

**Effect of Datacenters with varying mix of services**
<br>_**Cloud Architecture**_<br>
![Cloud arch](https://user-images.githubusercontent.com/20476080/205842260-f44cc912-a49e-42c3-961a-9f770b67eaae.jpg)<br>
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

<img width="640" alt="image" src="https://user-images.githubusercontent.com/20476080/205842125-2ce51e8c-8ccd-4d11-8c28-9e2708720c8e.png">

