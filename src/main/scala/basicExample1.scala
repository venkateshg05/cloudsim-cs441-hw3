//object basicExample1 {
//
//  import org.cloudbus.cloudsim.brokers.DatacenterBroker
//  import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
//  import org.cloudbus.cloudsim.cloudlets.Cloudlet
//  import org.cloudbus.cloudsim.cloudlets.CloudletSimple
//  import org.cloudbus.cloudsim.core.CloudSim
//  import org.cloudbus.cloudsim.datacenters.Datacenter
//  import org.cloudbus.cloudsim.datacenters.DatacenterSimple
//  import org.cloudbus.cloudsim.hosts.Host
//  import org.cloudbus.cloudsim.hosts.HostSimple
//  import org.cloudbus.cloudsim.resources.Pe
//  import org.cloudbus.cloudsim.resources.PeSimple
//  import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic
//  import org.cloudbus.cloudsim.vms.Vm
//  import org.cloudbus.cloudsim.vms.VmSimple
//  import org.cloudsimplus.builders.tables.CloudletsTableBuilder
//  import java.util
//
//  /**
//   * A minimal but organized, structured and re-usable CloudSim Plus example
//   * which shows good coding practices for creating simulation scenarios.
//   *
//   * <p>It defines a set of constants that enables a developer
//   * to change the number of Hosts, VMs and Cloudlets to create
//   * and the number of {@link Pe}s for Hosts, VMs and Cloudlets.</p>
//   *
//   * @author Manoel Campos da Silva Filho
//   * @since CloudSim Plus 1.0
//   */
//  object BasicFirstExample {
//    private val HOSTS = 1
//    private val HOST_PES = 8
//    private val HOST_MIPS = 1000
//    private val HOST_RAM = 2048 //in Megabytes
//
//    private val HOST_BW = 10_000 //in Megabits/s
//
//    private val HOST_STORAGE = 1_000_000
//    private val VMS = 2
//    private val VM_PES = 4
//    private val CLOUDLETS = 4
//    private val CLOUDLET_PES = 2
//    private val CLOUDLET_LENGTH = 10_000
//
//    def main(args: Array[String]): Unit = {
//      new BasicFirstExample
//    }
//  }
//
//  class BasicFirstExample private() {
//    /*Enables just some level of log messages.
//             Make sure to import org.cloudsimplus.util.Log;*/
//    //Log.setLevel(ch.qos.logback.classic.Level.WARN);
//    val simulation = CloudSim()
//    datacenter0 = createDatacenter()
//    //Creates a broker that is a software acting on behalf a cloud customer to manage his/her VMs and Cloudlets
//    broker0 = new DatacenterBrokerSimple(simulation)
//    vmList = createVms
//    cloudletList = createCloudlets
//    broker0.submitVmList(vmList)
//    broker0.submitCloudletList(cloudletList)
//    simulation.start
//    val finishedCloudlets: Nothing = broker0.getCloudletFinishedList
//    new CloudletsTableBuilder(finishedCloudlets).build()
//    final private var simulation = null
//    private var broker0 = null
//    private var vmList = null
//    private var cloudletList = null
//    private var datacenter0 = null
//
//    /**
//     * Creates a Datacenter and its Hosts.
//     */
//    private def createDatacenter = {
//      val hostList = new util.ArrayList[Host](BasicFirstExample.HOSTS)
//      for (i <- 0 until BasicFirstExample.HOSTS) {
//        val host = createHost
//        hostList.add(host)
//      }
//      //Uses a VmAllocationPolicySimple by default to allocate VMs
//      new DatacenterSimple(simulation, hostList)
//    }
//
//    private def createHost = {
//      val peList = new util.ArrayList[Pe](BasicFirstExample.HOST_PES)
//      //List of Host's CPUs (Processing Elements, PEs)
//      for (i <- 0 until BasicFirstExample.HOST_PES) { //Uses a PeProvisionerSimple by default to provision PEs for VMs
//        peList.add(new PeSimple(BasicFirstExample.HOST_MIPS))
//      }
//      /*
//              Uses ResourceProvisionerSimple by default for RAM and BW provisioning
//              and VmSchedulerSpaceShared for VM scheduling.
//              */ new HostSimple(BasicFirstExample.HOST_RAM, BasicFirstExample.HOST_BW, BasicFirstExample.HOST_STORAGE, peList)
//    }
//
//    /**
//     * Creates a list of VMs.
//     */
//    private def createVms = {
//      val vmList = new util.ArrayList[Vm](BasicFirstExample.VMS)
//      for (i <- 0 until BasicFirstExample.VMS) { //Uses a CloudletSchedulerTimeShared by default to schedule Cloudlets
//        val vm = new VmSimple(BasicFirstExample.HOST_MIPS, BasicFirstExample.VM_PES)
//        vm.setRam(512).setBw(1000).setSize(10_000)
//        vmList.add(vm)
//      }
//      vmList
//    }
//
//    /**
//     * Creates a list of Cloudlets.
//     */
//    private def createCloudlets = {
//      val cloudletList = new util.ArrayList[Cloudlet](BasicFirstExample.CLOUDLETS)
//      //UtilizationModel defining the Cloudlets use only 50% of any resource all the time
//      val utilizationModel = new UtilizationModelDynamic(0.5)
//      for (i <- 0 until BasicFirstExample.CLOUDLETS) {
//        val cloudlet = new CloudletSimple(BasicFirstExample.CLOUDLET_LENGTH, BasicFirstExample.CLOUDLET_PES, utilizationModel)
//        cloudlet.setSizes(1024)
//        cloudletList.add(cloudlet)
//      }
//      cloudletList
//    }
//  }
//
//}
