package com.malliina.appbundler

import scala.xml.NodeSeq

/**
 * @author mle
 */
case class DistributionConf(appIdentifier: String,
                            displayName: String,
                            name: String,
                            additionalXml: NodeSeq = NodeSeq.Empty)
