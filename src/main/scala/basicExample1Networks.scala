import HelperUtils.CreateLogger
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyRoundRobin
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletSimple}
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.DatacenterSimple
import org.cloudbus.cloudsim.vms.VmSimple
import org.cloudbus.cloudsim.hosts.HostSimple
import org.cloudbus.cloudsim.network.topologies.BriteNetworkTopology
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple
import org.cloudbus.cloudsim.resources.{Pe, PeSimple}
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic
import org.cloudsimplus.autoscaling.HorizontalVmScalingSimple
import org.cloudsimplus.builders.tables.CloudletsTableBuilder

import collection.JavaConverters.*
object basicExample1Networks {
  val logger = CreateLogger(classOf[basicExample1Networks.type])


  @main
  def runSim() = {
    logger.info("Start simulation")
    val cloudsim = new CloudSim()
    val dc1 = createDatacenter(cloudsim)
    val broker = new DatacenterBrokerSimple(cloudsim)

    val networkTopology = new BriteNetworkTopology()
    cloudsim.setNetworkTopology(networkTopology)
    networkTopology.addLink(dc1, broker, 10, 10)

//    new HorizontalVmScalingSimple().setOverloadPredicate()
    val vm = new VmSimple(1000, 1).setRam(512).setBw(1000).setSize(10000)
    val vmList = List(vm, vm, vm)
    broker.submitVmList(vmList.asJava)

    val cloudletList = createCloudlets()
    broker.submitCloudletList(cloudletList.asJava)

    cloudsim.start();
    new CloudletsTableBuilder(broker.getCloudletFinishedList()).build();
  }

  def createDatacenter(cloudsim:CloudSim): DatacenterSimple = {
    val hostPes = List(new PeSimple(20000))

    val host = new HostSimple(1000, 100000, 10000, hostPes.asJava)
      .setRamProvisioner(new ResourceProvisionerSimple())
      .setBwProvisioner(new ResourceProvisionerSimple())
      .setVmScheduler(new VmSchedulerTimeShared())
    val hostList = List(host)

    val dc = new DatacenterSimple(cloudsim, hostList.asJava, new VmAllocationPolicyRoundRobin());
    dc.getCharacteristics()
      .setCostPerSecond(3.0)
      .setCostPerMem(0.05)
      .setCostPerStorage(0.1)
      .setCostPerBw(0.1)
    logger.info(s"Created one datacenter: $dc")
    dc
  }

  def createCloudlets():List[CloudletSimple] = {
    val utilizationModel = new UtilizationModelDynamic(0.6)
    val cloudlet1 = new CloudletSimple(10000, 1, utilizationModel)
    val cloudlet2 = new CloudletSimple(10000, 5, utilizationModel)
    val cloudletList = List(cloudlet1, cloudlet2)
    cloudletList
  }

}
