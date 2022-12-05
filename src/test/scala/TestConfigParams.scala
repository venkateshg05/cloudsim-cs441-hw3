import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import VaryVmAllocPolicy.config
import DCServicesSimulation.dcServicesConfig

class TestConfigParams extends AnyFlatSpec with Matchers {

  it should "obtain the host mipsCapacity" in {
    config.getLong("cloudSimulator.host.mipsCapacity") shouldBe 2000
  }

  it should "obtain the vm RAMInMBs" in {
    config.getLong("cloudSimulator.vm.RAMInMBs") shouldBe 10000
  }

  it should "obtain the dc1 vmAllocationPolicy" in {
    dcServicesConfig.getString("dcServicesSimulator.dc1.vmAllocationPolicy") shouldBe "RoundRobin"
  }

  it should "obtain the dc2 host1's vmSchedulerPolicy" in {
    dcServicesConfig.getString("dcServicesSimulator.dc2.host1.vmSchedulerPolicy") shouldBe "TimeShared"
  }

  it should "obtain the dc3 costPerSecond" in {
    dcServicesConfig.getDouble("dcServicesSimulator.dc3.costPerSecond") shouldBe 1.0
  }



}
