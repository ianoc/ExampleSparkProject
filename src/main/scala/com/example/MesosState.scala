package com.example

case class FrameworkResourceState(cpus: Int, mem: Int)
case class Task(framework_id: String, id: String, name: String, resources: FrameworkResourceState, slave_id: String, state: String)
case class FrameworkState(active: Int, completed_tasks: List[Task], id: String, name: String, offers: List[Any], registered_time: Int,
  resources: FrameworkResourceState, tasks: List[Task], unregistered_time: Int = 0, user: String)
case class Slave(hostname: String, id: String, registered_time: Int, resources: FrameworkResourceState, webui_hostname: String, webui_port: Int)

case class MesosState(build_date: String, build_time: String, build_user: String, completed_frameworks: List[FrameworkState],
  frameworks: List[FrameworkState], id: String, log_dir: String, pid: String, slaves: List[Slave], start_time: Int)
