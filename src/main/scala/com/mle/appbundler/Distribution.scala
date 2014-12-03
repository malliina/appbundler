package com.mle.appbundler

import java.nio.file.Path

import scala.xml.Node

/**
 * @author Michael
 */
object Distribution extends XmlWriter {

  def writeDistribution(conf: DistributionConf, dest: Path) = writePretty(xml(conf), dest)

  def xml(conf: DistributionConf): Node = {
    val org = conf.organization
    val appID = conf.appIdentifier
    val displayName = conf.displayName
    val pkgName = s"${conf.name}.pkg"

    <installer-gui-script minSpecVersion="1">
      <title>{displayName}</title>
      <organization>{org}</organization>
      <domains enable_localSystem="true"/>
      <options customize="never" require-scripts="true" rootVolumeOnly="true"/>
      <!-- Define documents displayed at various steps -->
      <welcome file="welcome.html" mime-type="text/html"/>
      <license file="license.html" mime-type="text/html"/>
      <conclusion file="conclusion.html" mime-type="text/html"/>
      <!-- List all component packages -->
      <pkg-ref id={appID}
               version="0"
               auth="root">{pkgName}</pkg-ref>
      <!-- List them again here. They can now be organized
       as a hierarchy if you want. -->
      <choices-outline>
        <line choice={appID}/>
      </choices-outline>
      <!-- Define each choice above -->
      <choice
      id={appID}
      visible="false"
      title={s"$displayName choice"}
      description={s"The $displayName daemon"}
      start_selected="true">
        <pkg-ref id={appID}/>
      </choice>
    </installer-gui-script>
  }
}
case class DistributionConf(appIdentifier: String, organization:String, displayName: String, name: String)
