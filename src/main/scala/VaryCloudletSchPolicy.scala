import HelperUtils.{CreateLogger, ObtainConfigReference}
import org.cloudbus.cloudsim.allocationpolicies.{VmAllocationPolicyBestFit, VmAllocationPolicyFirstFit, VmAllocationPolicyRoundRobin, VmAllocationPolicySimple}
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.cloudlets.CloudletSimple
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.DatacenterSimple
import org.cloudbus.cloudsim.hosts.HostSimple
import org.cloudbus.cloudsim.network.topologies.BriteNetworkTopology
import org.cloudbus.cloudsim.resources.PeSimple
import org.cloudbus.cloudsim.schedulers.cloudlet.{CloudletSchedulerSpaceShared, CloudletSchedulerTimeShared}
import org.cloudbus.cloudsim.schedulers.vm.{VmSchedulerSpaceShared, VmSchedulerTimeShared}
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic
import org.cloudbus.cloudsim.vms.VmSimple
import org.cloudsimplus.builders.tables.CloudletsTableBuilder
import collection.JavaConverters.*
object VaryCloudletSchPolicy {

  val logger = CreateLogger(classOf[basicExample1Networks.type])
  val config = ObtainConfigReference("cloudSimulator") match {
    case Some(value) => value
    case None => throw new RuntimeException("Cannot obtain a reference to the config data.")
  }

  def driver() = {
    runSim("TimeShared")
    runSim("SpaceShared")
  }

  //  @main
  def runSim(cloudletSchPolicyArg: String) = {
    logger.info(s"Start simulation for cloudlet sch policy $cloudletSchPolicyArg")
    val cloudsim = new CloudSim()
    val dc1 = createDatacenter(cloudsim, "RoundRobin")
    val dc2 = createDatacenter(cloudsim, "RoundRobin")
    val dc3 = createDatacenter(cloudsim, "RoundRobin")
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
          cloudletSchPolicyArg match
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

  def createDatacenter(cloudsim: CloudSim, vmAllocPolicyArg: String): DatacenterSimple = {
    val hostPes = List.fill(config.getInt("cloudSimulator.host.numPes"))(new PeSimple(config.getLong("cloudSimulator.host.mipsCapacity")))


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
