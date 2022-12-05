object MainDriver {

  @main
  def driver() = {
    /*Driver program for the simulator*/
    VaryVmAllocPolicy.driver();
    DCServicesSimulation.runDCServicesSim();
    VaryCloudletSchPolicy.driver();
  }

}
