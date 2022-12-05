object MainDriver {

  @main
  def driver() = {
    VaryVmAllocPolicy.driver();
    DCServicesSimulation.runDCServicesSim();
    VaryCloudletSchPolicy.driver();
  }

}
