package com.mle.appbundler

import java.nio.file.Path

import scala.xml.Node

/**
 * @author Michael
 */
object Distribution extends XmlWriter {

  def writeDistribution(conf: DistributionConf, dest: Path) = writePretty(xml(conf), dest)

  def xml(conf: DistributionConf): Node = {
//    val org = conf.organization
    val appID = conf.appIdentifier
    val daemonName = s"$appID.daemon"
    val displayName = conf.displayName
    val pkgName = s"${conf.name}.pkg"

    <installer-gui-script minSpecVersion="1">
      <title>{displayName}</title>
      <organization>{appID}</organization>
      <domains enable_localSystem="true"/>
      <options customize="never" require-scripts="true" rootVolumeOnly="true"/>
      <!-- Define documents displayed at various steps -->
      <welcome file="welcome.html" mime-type="text/html"/>
      <license file="license.html" mime-type="text/html"/>
      <conclusion file="conclusion.html" mime-type="text/html"/>
      <!-- List all component packages -->
      <pkg-ref id={daemonName}
               version="0"
               auth="root">{pkgName}</pkg-ref>
      <!-- List them again here. They can now be organized
       as a hierarchy if you want. -->
      <choices-outline>
        <line choice={daemonName}/>
      </choices-outline>
      <!-- Define each choice above -->
      <choice
      id={daemonName}
      visible="false"
      title={s"$displayName choice"}
      description={s"The $displayName daemon"}
      start_selected="true">
        <pkg-ref id={daemonName}/>
      </choice>
    </installer-gui-script>
  }
}