import HelperUtils.{CreateLogger, ObtainConfigReference}
import org.cloudbus.cloudsim.allocationpolicies.*
import org.cloudbus.cloudsim.brokers.{DatacenterBrokerBestFit, DatacenterBrokerSimple}
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletSimple}
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.DatacenterSimple
import org.cloudbus.cloudsim.vms.VmSimple
import org.cloudbus.cloudsim.hosts.HostSimple
import org.cloudbus.cloudsim.network.topologies.BriteNetworkTopology
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple
import org.cloudbus.cloudsim.resources.{Pe, PeSimple}
import org.cloudbus.cloudsim.schedulers.cloudlet.{CloudletSchedulerSpaceShared, CloudletSchedulerTimeShared}
import org.cloudbus.cloudsim.schedulers.vm.*
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic
import org.cloudsimplus.autoscaling.HorizontalVmScalingSimple
import org.cloudsimplus.builders.tables.CloudletsTableBuilder

import collection.JavaConverters.*

object VaryVmAllocPolicy {

  val logger = CreateLogger(classOf[VaryVmAllocPolicy.type])
  val config = ObtainConfigReference("cloudSimulator") match {
    case Some(value) => value
    case None => throw new RuntimeException("Cannot obtain a reference to the config data.")
  }

  def driver() = {
    /*
    Creates 4 simulations with data centers having different VM allocation policy 
    */
    runSim("Simple")
    runSim("FirstFit")
    runSim("BestFit")
    runSim("RoundRobin")
  }

  def runSim(vmAllocPolicyArg:String) = {
    /*
        Creates the 3 data centers with specified vm alloc policy
          Each have their own costs and other configs defined in application.conf        
        Also, submits the VMs and the cloudlets to the broker and starts the simulation 
        * */
    logger.info(s"Start simulation for vm alloc policy $vmAllocPolicyArg")
    val cloudsim = new CloudSim()
    val dc1 = createDatacenter(cloudsim, vmAllocPolicyArg)
    val dc2 = createDatacenter(cloudsim, vmAllocPolicyArg)
    val dc3 = createDatacenter(cloudsim, vmAllocPolicyArg)
    val broker = new DatacenterBrokerSimple(cloudsim)

    val networkTopology = new BriteNetworkTopology()
    cloudsim.setNetworkTopology(networkTopology)
    networkTopology.addLink(dc1, broker, 5000000, 1)
    networkTopology.addLink(dc2, broker, 5000000, 1)
    networkTopology.addLink(dc3, broker, 5000000, 1)

    val vmList = List.fill(config.getInt("cloudSimulator.vm.numVms"))(
      new VmSimple(
        config.getLong("cloudSimulator.vm.mipsCapacity"),
        config.getInt("cloudSimulator.vm.numPes")
      )
        .setRam(config.getLong("cloudSimulator.vm.RAMInMBs"))
        .setBw(config.getLong("cloudSimulator.vm.BandwidthInMBps"))
        .setSize(config.getLong("cloudSimulator.vm.StorageInMBs"))
        .setCloudletScheduler(
          config.getString("cloudSimulator.vm.cloudletSchedulerPolicy") match
            case "TimeShared" => new CloudletSchedulerTimeShared()
            case "SpaceShared" => new CloudletSchedulerSpaceShared()
        )
    )
    broker.submitVmList(vmList.asJava)

    val cloudletList = createCloudlets()
    broker.submitCloudletList(cloudletList.asJava)

    cloudsim.start();
    new CloudletsTableBuilder(broker.getCloudletFinishedList()).build();
  }

  def createDatacenter(cloudsim: CloudSim, vmAllocPolicyArg:String): DatacenterSimple = {
    /*
      Creates the Datacenters as part of the cloud org config
      */
    val hostPes = List.fill(config.getInt("cloudSimulator.host.numPes"))
                    (new PeSimple(config.getLong("cloudSimulator.host.mipsCapacity")))


    val hostList = List.fill(config.getInt("cloudSimulator.host.numHosts"))(
      new HostSimple(
        config.getLong("cloudSimulator.host.RAMInMBs"),
        config.getLong("cloudSimulator.host.StorageInMBs"),
        config.getLong("cloudSimulator.host.BandwidthInMBps"),
        hostPes.asJava
      ).setVmScheduler(
        config.getString("cloudSimulator.host.vmSchedulerPolicy") match
          case "TimeShared" => new VmSchedulerTimeShared()
          case "SpaceShared" => new VmSchedulerSpaceShared()
      )
    )
    val vmAllocPolicy = vmAllocPolicyArg match
      case "BestFit" => new VmAllocationPolicyBestFit()
      case "FirstFit" => new VmAllocationPolicyFirstFit()
      case "RoundRobin" => new VmAllocationPolicyRoundRobin()
      case _ => new VmAllocationPolicySimple()
    val dc = new DatacenterSimple(cloudsim, hostList.asJava, vmAllocPolicy);
    dc.getCharacteristics()
      .setCostPerSecond(3.0)
      .setCostPerMem(0.05)
      .setCostPerStorage(0.1)
      .setCostPerBw(0.1)
    logger.info(s"Created one datacenter: ${dc.getId}")
    dc
  }

  def createCloudlets(): List[CloudletSimple] = {
    /*
      Creates the cloudlets defined in the config files
      * */
    val utilizationModel = new UtilizationModelDynamic(
      config.getDouble("cloudSimulator.utilizationRatio")
    )
    val cloudletList = List
      .fill(config.getInt("cloudSimulator.cloudlet.numCloudlets"))(new CloudletSimple(
      config.getLong("cloudSimulator.cloudlet.size"),
      config.getInt("cloudSimulator.cloudlet.Pes"),
      utilizationModel
    ))
    logger.info(s"Number of cloudlets: ${cloudletList.length}")
    cloudletList
  }

}
