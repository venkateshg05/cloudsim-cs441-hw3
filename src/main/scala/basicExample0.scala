import org.cloudsimplus.util.Log

import org.cloudbus.cloudsim.brokers.DatacenterBroker
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.cloudbus.cloudsim.cloudlets.CloudletSimple
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.Datacenter
import org.cloudbus.cloudsim.datacenters.DatacenterSimple
import org.cloudbus.cloudsim.hosts.Host
import org.cloudbus.cloudsim.hosts.HostSimple
import org.cloudbus.cloudsim.resources.Pe
import org.cloudbus.cloudsim.resources.PeSimple
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic
import org.cloudbus.cloudsim.vms.Vm
import org.cloudbus.cloudsim.vms.VmSimple
import org.cloudsimplus.builders.tables.CloudletsTableBuilder
import java.util.List


object basicExample0 {
  //Creates a CloudSim object to initialize the simulation.//Creates a CloudSim object to initialize the simulation.

  val simulation = CloudSim()

  //Creates a Broker that will act on behalf of a cloud user (customer).
  val broker0 = new DatacenterBrokerSimple(simulation)

  //Host configuration
  val ram = 10000 //in Megabytes
  val storage = 100000
  val bw = 100000 //in Megabits/s

  //Creates one host with a specific list of CPU cores (PEs).
  //Uses a PeProvisionerSimple by default to provision PEs for VMs
  //Uses ResourceProvisionerSimple by default for RAM and BW provisioning
  //Uses VmSchedulerSpaceShared by default for VM scheduling
  @main
  def runBasicExample():Unit = {
    val peList:List[Pe] = List.of(PeSimple(20000))
//    println(peList)
//    val peList = util.ArrayList[Pe];
    val host0 = new HostSimple(ram, bw, storage, peList)

    //Creates a Datacenter with a list of Hosts.
    //Uses a VmAllocationPolicySimple by default to allocate VMs
    val dc0 = new DatacenterSimple(simulation, List.of(host0))

    //Creates one VM to run applications.
    //Uses a CloudletSchedulerTimeShared by default to schedule Cloudlets
    val vm0 = new VmSimple(1000, 1)
    vm0.setRam(1000).setBw(1000).setSize(1000)

    //Creates Cloudlets that represent applications to be run inside a VM.
    //UtilizationModel defining the Cloudlets use only 50% of any resource all the time
    val utilizationModel = new UtilizationModelDynamic(0.8)
    val cloudlet0 = new CloudletSimple(10000, 1, utilizationModel)
    val cloudlet1 = new CloudletSimple(10000, 1, utilizationModel)
    val cloudletList = List.of(cloudlet0, cloudlet1)

    broker0.submitVmList(List.of(vm0))
    broker0.submitCloudletList(cloudletList)

    /*Starts the simulation and waits all cloudlets to be executed, automatically
    stopping when there is no more events to process.*/
    simulation.start()

    /*Prints the results when the simulation is over
    (you can use your own code here to print what you want from this cloudlet list).*/
    new CloudletsTableBuilder(broker0.getCloudletFinishedList).build()
  }



}
