/**
 * Copyright (C) 2012 Inria, University Lille 1
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA  02110-1301, USA.
 *
 * Contact: powerapi-user-list@googlegroups.com
 */
package fr.inria.powerapi.listener.cpu.file
import java.lang.management.ManagementFactory
import akka.util.duration.intToDurationInt
import org.junit.{ Test, Before, After }
import org.scalatest.junit.{ ShouldMatchersForJUnit, JUnitSuite }
import fr.inria.powerapi.core.{ Clock, Process }
import fr.inria.powerapi.formula.cpu.general.CpuFormula
import fr.inria.powerapi.library.PowerAPI
import fr.inria.powerapi.sensor.cpu.proc.CpuSensor
import scalax.file.Path

trait ConfigurationMock extends Configuration {
  override lazy val filePath = Path.createTempFile(
    prefix = "powerapi.listener-cpu-file",
    deleteOnExit = false).path
}
class CpuListenerMock extends CpuListener with ConfigurationMock

class CpuListenerSuite extends JUnitSuite with ShouldMatchersForJUnit {
  @Before
  def setUp {
    Array(classOf[CpuSensor], classOf[CpuFormula]).foreach(PowerAPI.startEnergyModule(_))
  }

  @Test
  def testCurrentPid {
    val currentPid = ManagementFactory.getRuntimeMXBean.getName.split("@")(0).toInt
    PowerAPI.startMonitoring(Process(currentPid), 500 milliseconds, classOf[CpuListenerMock])
    Thread.sleep((5 seconds).toMillis)
    PowerAPI.stopMonitoring(Process(currentPid), 500 milliseconds, classOf[CpuListenerMock])
  }

  @After
  def tearDown {
    Array(classOf[CpuSensor], classOf[CpuFormula]).foreach(PowerAPI.stopEnergyModule(_))
  }
}