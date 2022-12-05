import HelperUtils.{CreateLogger, ObtainConfigReference}
import org.cloudbus.cloudsim.allocationpolicies.*
import org.cloudbus.cloudsim.brokers.{DatacenterBrokerBestFit, DatacenterBrokerSimple}
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletSimple}
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.DatacenterSimple
import org.cloudbus.cloudsim.vms.VmSimple
import org.cloudbus.cloudsim.hosts.HostSimple
import org.cloudbus.cloudsim.network.topologies.*
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple
import org.cloudbus.cloudsim.resources.{Pe, PeSimple}
import org.cloudbus.cloudsim.schedulers.cloudlet.{CloudletSchedulerSpaceShared, CloudletSchedulerTimeShared}
import org.cloudbus.cloudsim.schedulers.vm.*
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic
import org.cloudsimplus.autoscaling.HorizontalVmScalingSimple
import org.cloudsimplus.builders.tables.CloudletsTableBuilder

import collection.JavaConverters.*

object DCServicesSimulation {
  val logger = CreateLogger(classOf[basicExample1Networks.type])
  // datacenter config by the cloud org
  val dcServicesConfig = ObtainConfigReference("dcServicesSimulator") match {
    case Some(value) => value
    case None => throw new RuntimeException("Cannot obtain a reference to the config data.")
  }
  // Iaas config defined by the customer
  val dcIaasConfig = ObtainConfigReference("dcIaasSim") match {
    case Some(value) => value
    case None => throw new RuntimeException("Cannot obtain a reference to the config data.")
  }
  // Paas config defined by the customer
  val dcPaasConfig = ObtainConfigReference("dcPaasSim") match {
    case Some(value) => value
    case None => throw new RuntimeException("Cannot obtain a reference to the config data.")
  }
  
  def runDCServicesSim() = {
    /*
    Creates the 3 data centers with varying SAAS, FAAS, PAAS & IAAS services
      Each have their own costs and other configs defined in dataCenterServicesSim.conf
      For the IAAS the VMs & Cloudlets can be defined in dcIaasSim.conf
      For the PAAS the Cloudlets can be defined in dcPaasSim.conf
      For SAAS & FAAS all the params are defined by the cloud org. The cloud org executes the 
      incoming requests (modelled as cloudlets)
    
    Also, submits the VMs and the cloudlets to the broker and starts the simulation 
    * */
    logger.info("Start simulation")
    val cloudsim = new CloudSim()
    val broker = new DatacenterBrokerSimple(cloudsim)
    val dc1 = createDC(cloudsim,"dcServicesSimulator.dc1" )
    val dc2 = createDC(cloudsim, "dcServicesSimulator.dc2")
    val dc3 = createDC(cloudsim, "dcServicesSimulator.dc3")

    val networkTopology = new BriteNetworkTopology()
    cloudsim.setNetworkTopology(networkTopology)
    networkTopology.addLink(dc1, broker, 5000000, 1)
    networkTopology.addLink(dc2, broker, 5000000, 1)
    networkTopology.addLink(dc3, broker, 5000000, 1)

    val vmList = List(
      createVm("dcServicesSimulator.dc1.vm1"),
      createVm("dcServicesSimulator.dc1.vm2"),
      createIaasVm("dcIaasSim.vm1"),
      createIaasVm("dcIaasSim.vm2")
    )
    broker.submitVmList(vmList.asJava)

    createCloudlets(broker)
    cloudsim.start()
    new CloudletsTableBuilder(broker.getCloudletFinishedList()).build()
  }

  def createCloudlets(broker:DatacenterBrokerSimple):Unit = {
    /*
    Creates the cloudlets defined in the config files
    * */
    broker.submitCloudletList(List.concat(
      createCloudlets(
        "dcServicesSimulator.cloudlet1",
        dcServicesConfig.getDouble("dcServicesSimulator.utilizationRatio")
      ),
      createCloudlets(
        "dcServicesSimulator.cloudlet2",
        dcServicesConfig.getDouble("dcServicesSimulator.utilizationRatio")
      ),

      createIaasCloudlets(
        "dcIaasSim.cloudlet1"
      ),
      createPaasCloudlets(
        "dcPaasSim.cloudlet1"
      ),
      createPaasCloudlets(
        "dcPaasSim.cloudlet2"
      )
    ).asJava)
  }

  def createIaasVm(vmConfigPath: String): VmSimple = {
    /*
    Creates the VMs as part of the IAAS client config
    */
    val vmConfig = dcIaasConfig.getConfig(vmConfigPath)
    val vm = new VmSimple(
      vmConfig.getLong("mipsCapacity"),
      vmConfig.getInt("numPes")
    )
      .setRam(vmConfig.getLong("RAMInMBs"))
      .setBw(vmConfig.getLong("BandwidthInMBps"))
      .setSize(vmConfig.getLong("StorageInMBs"))
      .setCloudletScheduler(
        vmConfig.getString("cloudletSchedulerPolicy") match
          case "TimeShared" => new CloudletSchedulerTimeShared()
          case "SpaceShared" => new CloudletSchedulerSpaceShared()
      )
    vm.asInstanceOf[VmSimple]
  }

  def createPaasCloudlets(cloudletConfigPath: String): List[CloudletSimple] = {
    /*
        Creates the cloudlets as part of the PAAS client config
        */
    val clConfig = dcIaasConfig.getConfig(cloudletConfigPath)
    val utilizationModel = new UtilizationModelDynamic(
      clConfig.getDouble("utilizationRatio")
    )
    val cloudletList = List
      .fill(clConfig.getInt("numCloudlets"))(new CloudletSimple(
        clConfig.getLong("size"),
        clConfig.getInt("cPes"),
        utilizationModel
      ))
    logger.info(s"Number of cloudlets: ${cloudletList.length}")
    cloudletList
  }

  def createIaasCloudlets(cloudletConfigPath: String): List[CloudletSimple] = {
    /*
        Creates the cloudlets as part of the IAAS client config
        */
    val clConfig = dcIaasConfig.getConfig(cloudletConfigPath)
    val utilizationModel = new UtilizationModelDynamic(
      clConfig.getDouble("utilizationRatio")
    )
    val cloudletList = List
      .fill(clConfig.getInt("numCloudlets"))(new CloudletSimple(
        clConfig.getLong("size"),
        clConfig.getInt("cPes"),
        utilizationModel
      ))
    logger.info(s"Number of cloudlets: ${cloudletList.length}")
    cloudletList
  }

  def createHostPes(numPes:Int, mipsCapacity:Long):List[PeSimple] = {
    /*
        Creates the PEs for the hosts
        */
    List.fill(numPes)(new PeSimple(mipsCapacity))
  }

  def createHost(hostConfigPath:String):HostSimple = {
    /*
        Creates the Hosts as part of the cloud org config
        */
    val hostConfig = dcServicesConfig.getConfig(hostConfigPath)
    val hostPes = createHostPes(
      hostConfig.getInt("numPes"),
      hostConfig.getLong("mipsCapacity")
    )
    val host = new HostSimple(
      hostConfig.getLong("RAMInMBs"),
      hostConfig.getLong("StorageInMBs"),
      hostConfig.getLong("BandwidthInMBps"),
      hostPes.asJava
    ).setVmScheduler(
      hostConfig.getString("vmSchedulerPolicy") match
        case "TimeShared" => new VmSchedulerTimeShared()
        case "SpaceShared" => new VmSchedulerSpaceShared()
    )
    host.asInstanceOf[HostSimple]
  }

  def createVm(vmConfigPath:String):VmSimple = {
    /*
      Creates the VMs as part of the cloud org config
      */
    val vmConfig = dcServicesConfig.getConfig(vmConfigPath)
    val vm = new VmSimple(
      vmConfig.getLong("mipsCapacity"),
      vmConfig.getInt("numPes")
    )
      .setRam(vmConfig.getLong("RAMInMBs"))
      .setBw(vmConfig.getLong("BandwidthInMBps"))
      .setSize(vmConfig.getLong("StorageInMBs"))
      .setCloudletScheduler(
        vmConfig.getString("cloudletSchedulerPolicy") match
          case "TimeShared" => new CloudletSchedulerTimeShared()
          case "SpaceShared" => new CloudletSchedulerSpaceShared()
      )
    vm.asInstanceOf[VmSimple]
  }

  def createCloudlets(cloudletConfigPath:String, utilizationRatio:Double): List[CloudletSimple] = {
    /*
      Creates the Cloudlets as part of the cloud org config
      */
    val clConfig = dcServicesConfig.getConfig(cloudletConfigPath)
    val utilizationModel = new UtilizationModelDynamic(utilizationRatio)
    val cloudletList = List
      .fill(clConfig.getInt("numCloudlets"))(new CloudletSimple(
        clConfig.getLong("size"),
        clConfig.getInt("cPes"),
        utilizationModel
      ))
    logger.info(s"Number of cloudlets: ${cloudletList.length}")
    cloudletList
  }

  def createDC(cloudsim: CloudSim, dcConfigPath:String): DatacenterSimple = {
    /*
      Creates the Datacenters as part of the cloud org config
      */
    val hostList = List(
      createHost(dcConfigPath + ".host1"),
      createHost(dcConfigPath + ".host2"),
      createHost(dcConfigPath + ".host3")
    )

    val vmAllocPolicy = dcServicesConfig.getString(dcConfigPath + ".vmAllocationPolicy") match
      case "BestFit" => new VmAllocationPolicyBestFit()
      case "FirstFit" => new VmAllocationPolicyFirstFit()
      case "RoundRobin" => new VmAllocationPolicyRoundRobin()
      case _ => new VmAllocationPolicySimple()
    val dc = new DatacenterSimple(cloudsim, hostList.asJava, vmAllocPolicy);
    dc.getCharacteristics()
      .setCostPerSecond(dcServicesConfig.getDouble(dcConfigPath + ".costPerSecond"))
      .setCostPerMem(dcServicesConfig.getDouble(dcConfigPath + ".costPerMem"))
      .setCostPerStorage(dcServicesConfig.getDouble(dcConfigPath + ".costPerStorage"))
      .setCostPerBw(dcServicesConfig.getDouble(dcConfigPath + ".costPerBw"))
    logger.info(s"Created one datacenter: ${dc.getId}")
    dc
  }

}
