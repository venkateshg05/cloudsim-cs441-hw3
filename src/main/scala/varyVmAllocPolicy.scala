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

object varyVmAllocPolicy {

  val logger = CreateLogger(classOf[basicExample1Networks.type])
  val config = ObtainConfigReference("cloudSimulator") match {
    case Some(value) => value
    case None => throw new RuntimeException("Cannot obtain a reference to the config data.")
  }

//  @main
  def runSim() = {
    logger.info("Start simulation")
    val cloudsim = new CloudSim()
    val dc1 = createDatacenter(cloudsim)
//    val dc2 = createDatacenter(cloudsim)
    val broker = new DatacenterBrokerSimple(cloudsim)

//    val vm = new VmSimple(
//      config.getLong("cloudSimulator.vm.mipsCapacity"),
//      config.getInt("cloudSimulator.host.numPes")
//    )
//      .setRam(config.getLong("cloudSimulator.vm.RAMInMBs"))
//      .setBw(config.getLong("cloudSimulator.vm.BandwidthInMBps"))
//      .setSize(config.getLong("cloudSimulator.vm.StorageInMBs"))
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

  def createDatacenter(cloudsim: CloudSim): DatacenterSimple = {
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
    val vmAllocPolicy = config.getString("cloudSimulator.dc.vmAllocationPolicy") match
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
