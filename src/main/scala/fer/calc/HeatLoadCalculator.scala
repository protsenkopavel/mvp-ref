package fer.calc

import fer.entity.*

/** Расчет теплопритоков охлаждаемой камеры: Q = Q1 + Q2 + Q3 + Q4. */
object HeatLoadCalculator:

  /** Выполняет полный расчет и формирует отчет по блокам Q1–Q4 с итогами. */
  def calculate(spec: ColdRoomSpec, in: CalcInputs): HeatLoadReport =
    val q1 = enclosureSection(spec, in)
    val q2 = productSection(spec.mode, in)
    val q3 = ventilationSection(spec, in)
    val q4 = operationalSection(spec, in)
    HeatLoadReport(
      roomNumber = spec.roomNumber,
      roomName = spec.name,
      designCity = in.designCity,
      outdoorTemperatureC = in.outdoorTemperatureC,
      outdoorHumidityPct = in.outdoorHumidityPct,
      q1Enclosures = q1,
      q2Product = q2,
      q3Ventilation = q3,
      q4Operational = q4,
      totals = totalsSection(spec, in, q1.totalW + q2.totalW + q3.totalW + q4.totalW)
    )

  /** Считает Q1 — теплопритоки через ограждающие конструкции. */
  private def enclosureSection(spec: ColdRoomSpec, in: CalcInputs): EnclosureSection =
    val d = spec.dimensions
    val tRoom = spec.mode.roomAir.temperatureC

    def layerResistance(layer: MaterialLayer): Double =
      if layer.material.thermalConductivity == 0 then 0.0
      else (layer.thicknessMm / 1000.0) / layer.material.thermalConductivity

    def block(name: String, e: Enclosure, areaM2: Double, withInner: Boolean): EnclosureBlock =
      val r = 1.0 / in.outerSurfaceCoeffWm2K +
        layerResistance(e.structure) + layerResistance(e.insulation) +
        (if withInner then 1.0 / in.innerSurfaceCoeffWm2K else 0.0)
      val k = 1.0 / r
      EnclosureBlock(name, k, areaM2, e.outsideTemperatureC, tRoom,
        heatGainW = k * areaM2 * (e.outsideTemperatureC - tRoom))

    val sideAV = d.lengthM * d.heightM
    val sideBG = d.widthM * d.heightM
    val elements = List(
      block("Стена А", spec.wallA, sideAV, withInner = true),
      block("Стена Б", spec.wallB, sideBG, withInner = true),
      block("Стена В", spec.wallV, sideAV, withInner = true),
      block("Стена Г", spec.wallG, sideBG, withInner = true),
      block("Потолок", spec.ceiling, d.areaM2, withInner = true),
      block("Пол", spec.floor, d.areaM2, withInner = false)
    )
    EnclosureSection(elements, in.solarRadiationW,
      totalW = elements.map(_.heatGainW).sum + in.solarRadiationW)

  /** Считает Q2 — теплопритоки от продукции (охлаждение груза/упаковки, дыхание). */
  private def productSection(mode: OperatingMode, in: CalcInputs): ProductSection =
    def cooling(massKg: Double, heatCapacity: Double): CoolingBlock =
      val iIn  = heatCapacity * (mode.incomingProductTempC - in.enthalpyReferenceTempC)
      val iOut = heatCapacity * (mode.finalProductTempC - in.enthalpyReferenceTempC)
      CoolingBlock(massKg, iIn, iOut, mode.coolingTimeHours,
        heatGainW = massKg * (iIn - iOut) * 1000.0 / (mode.coolingTimeHours * 3600.0))

    val product   = cooling(mode.dailyLoading.tonnes * 1000.0, in.productHeatCapacityKjKgK)
    val packaging = cooling(mode.packagingMassT * 1000.0, in.packagingHeatCapacityKjKgK)
    val respiration = RespirationBlock(
      storedMassT = mode.storedProduct.tonnes,
      respirationHeatWPerT = in.respirationHeatWPerT,
      heatGainW = mode.storedProduct.tonnes * in.respirationHeatWPerT
    )
    ProductSection(product, packaging, respiration,
      totalW = product.heatGainW + packaging.heatGainW + respiration.heatGainW)

  /** Считает Q3 — теплопритоки с наружным воздухом при вентиляции. */
  private def ventilationSection(spec: ColdRoomSpec, in: CalcInputs): VentilationSection =
    val volume = spec.dimensions.volumeM3
    val n = spec.mode.ventilationAirChangesPerHour
    val dh = in.supplyAirEnthalpyKjKg - in.roomAirEnthalpyKjKg
    VentilationSection(volume, n, in.roomAirDensityKgM3, dh,
      totalW = volume * n * in.roomAirDensityKgM3 * dh * 1000.0 / 3600.0)

  /** Считает Q4 — эксплуатационные теплопритоки (освещение, вентиляторы, люди, ПТС, двери). */
  private def operationalSection(spec: ColdRoomSpec, in: CalcInputs): OperationalSection =
    val mode = spec.mode
    val tRoom = mode.roomAir.temperatureC
    val loadFactor = mode.vehicleLoadFactorPct / 100.0

    val lighting = LightingBlock(
      areaM2 = spec.dimensions.areaM2,
      powerWPerM2 = mode.lightingPowerWPerM2,
      heatGainW = spec.dimensions.areaM2 * mode.lightingPowerWPerM2
    )

    val fans = FanBlock(
      fanPowerPerCoolerW = in.fanMotorPowerPerCoolerW,
      coolersCount = mode.airCoolersCount,
      simultaneityCoeff = in.fanSimultaneityCoeff,
      heatGainW = in.fanMotorPowerPerCoolerW * mode.airCoolersCount * in.fanSimultaneityCoeff
    )
    
    val people = PeopleBlock(
      workersCount = mode.workersCount,
      heatPerPersonW = in.heatPerPersonW,
      presenceCoeff = loadFactor,
      heatGainW = mode.workersCount * in.heatPerPersonW * loadFactor
    )

    val totalVehicleKw = in.totalVehiclePowerKwOverride.getOrElse(
      mode.vehicles.map(v =>
        (v.travelMotorKw + v.liftMotorKw) * v.utilizationPct / 100.0 * v.count).sum
    )
    val vehicles = VehiclesBlock(
      totalPowerKw = totalVehicleKw,
      loadFactorPct = mode.vehicleLoadFactorPct,
      heatGainW = totalVehicleKw * 1000.0 * loadFactor
    )

    val doors = mode.doors.map { door =>
      val phi = in.doorTambourCoeff * (door.outsideTemperatureC - tRoom)
      val omega = 0.0
      val theta = in.doorThetaOverridesKwM2.getOrElse(door.name, interpolate(in.doorHeatFluxDiagramKwM2, phi))
      DoorBlock(
        name = door.name,
        count = door.count,
        areaM2 = door.areaM2,
        openingCoefficientPct = door.openingCoefficientPct,
        curtainEfficiency = omega,
        reducedTempDiffC = phi,
        heatFluxKwM2 = theta,
        heatGainW = theta * 1000.0 * door.areaM2 *
          (door.openingCoefficientPct / 100.0) * (1.0 - omega) * door.count
      )
    }

    OperationalSection(lighting, fans, people, vehicles, doors,
      totalW = lighting.heatGainW + fans.heatGainW + people.heatGainW +
        vehicles.heatGainW + doors.map(_.heatGainW).sum)

  /** Формирует итоговые показатели: потребную и расчетную мощность, удельную нагрузку. */
  private def totalsSection(spec: ColdRoomSpec, in: CalcInputs, totalW: Double): TotalsSection =
    val required = totalW * 24.0 / (24.0 - in.defrostHoursPerDay)
    val design = required * (1.0 + in.safetyFactorPct / 100.0)
    TotalsSection(
      totalHeatGainW = totalW,
      defrostHoursPerDay = in.defrostHoursPerDay,
      requiredCoolerCapacityW = required,
      safetyFactorPct = in.safetyFactorPct,
      designCapacityW = design,
      capacityPerCoolerW = design / spec.mode.airCoolersCount,
      specificLoadWPerM2 = design / spec.dimensions.areaM2
    )

  /** Выполняет линейную интерполяцию по таблице (x → y) с ограничением по краям. */
  private def interpolate(table: List[(Double, Double)], x: Double): Double =
    val sorted = table.sortBy(_._1)
    sorted match
      case Nil => 0.0
      case _ if x <= sorted.head._1 => sorted.head._2
      case _ if x >= sorted.last._1 => sorted.last._2
      case _ =>
        val ((x1, y1), (x2, y2)) = sorted.zip(sorted.tail)
          .find((a, b) => a._1 <= x && x <= b._1)
          .getOrElse((sorted.head, sorted.last))
        y1 + (y2 - y1) * (x - x1) / (x2 - x1)
