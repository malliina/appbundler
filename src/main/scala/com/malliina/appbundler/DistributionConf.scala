package com.malliina.appbundler

import scala.xml.NodeSeq

case class DistributionConf(appIdentifier: String,
                            displayName: String,
                            name: String,
                            additionalXml: NodeSeq = NodeSeq.Empty)
