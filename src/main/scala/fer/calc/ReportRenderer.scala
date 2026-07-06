package fer.calc

/** Текстовое представление отчета расчета теплопритоков. */
object ReportRenderer:

  /** Формирует построчный текстовый отчет по блокам Q1–Q4 с итогами. */
  def render(r: HeatLoadReport): String =
    val sb = new StringBuilder
    def line(s: String = "") = sb.append(s).append('\n')

    line(s"Расчет теплопритоков камеры №${r.roomNumber} «${r.roomName}»")
    line(s"Расчетные параметры: ${r.designCity}, ${r.outdoorTemperatureC} °С / ${r.outdoorHumidityPct} %")
    line()

    line("Q1. Теплопритоки через ограждающие конструкции")
    r.q1Enclosures.elements.foreach { e =>
      line(f"  ${e.element}%-10s k=${e.heatTransferCoeffWm2K}%6.2f Вт/м²К  F=${e.areaM2}%8.1f м²  " +
        f"tнар=${e.outsideTemperatureC}%5.1f °С  Q=${e.heatGainW}%9.0f Вт")
    }
    line(f"  Солнечная радиация: ${r.q1Enclosures.solarRadiationW}%.0f Вт")
    line(f"  Итого Q1 = ${r.q1Enclosures.totalW}%.0f Вт")
    line()

    line("Q2. Теплопритоки от продукции")
    val p = r.q2Product
    line(f"  Охлаждение продукта:  m=${p.productCooling.massKg}%11.0f кг  " +
      f"iвх=${p.productCooling.enthalpyInKjKg}%7.2f  iвых=${p.productCooling.enthalpyOutKjKg}%7.2f кДж/кг  " +
      f"Q=${p.productCooling.heatGainW}%9.0f Вт")
    line(f"  Охлаждение упаковки:  m=${p.packagingCooling.massKg}%11.0f кг  " +
      f"iвх=${p.packagingCooling.enthalpyInKjKg}%7.2f  iвых=${p.packagingCooling.enthalpyOutKjKg}%7.2f кДж/кг  " +
      f"Q=${p.packagingCooling.heatGainW}%9.0f Вт")
    line(f"  Дыхание продукции:    mхр=${p.respiration.storedMassT}%9.1f т  " +
      f"qд=${p.respiration.respirationHeatWPerT}%6.2f Вт/т  Q=${p.respiration.heatGainW}%9.0f Вт")
    line(f"  Итого Q2 = ${p.totalW}%.0f Вт")
    line()

    line("Q3. Теплопритоки от вентиляции")
    val v = r.q3Ventilation
    line(f"  V=${v.roomVolumeM3}%.0f м³  n=${v.airChangesPerHour}%.2f крат/ч  " +
      f"ρ=${v.airDensityKgM3}%.2f кг/м³  Δh=${v.enthalpyDiffKjKg}%.2f кДж/кг")
    line(f"  Итого Q3 = ${v.totalW}%.0f Вт")
    line()

    line("Q4. Эксплуатационные теплопритоки")
    val o = r.q4Operational
    line(f"  Освещение:    S=${o.lighting.areaM2}%8.0f м²  μ=${o.lighting.powerWPerM2}%4.1f Вт/м²          Q=${o.lighting.heatGainW}%9.0f Вт")
    line(f"  Вентиляторы:  N=${o.fans.fanPowerPerCoolerW}%8.1f Вт  n=${o.fans.coolersCount}%3d шт             Q=${o.fans.heatGainW}%9.0f Вт")
    line(f"  Люди:         n=${o.people.workersCount}%3d чел  q=${o.people.heatPerPersonW}%5.0f Вт/чел  kприс=${o.people.presenceCoeff}%4.2f  Q=${o.people.heatGainW}%9.0f Вт")
    line(f"  ПТС:          ΣN=${o.vehicles.totalPowerKw}%7.2f кВт  ψ=${o.vehicles.loadFactorPct}%4.1f %%          Q=${o.vehicles.heatGainW}%9.0f Вт")
    o.doors.foreach { d =>
      line(f"  Дверь «${d.name}» ×${d.count}: F=${d.areaM2}%5.1f м²  β=${d.openingCoefficientPct}%4.1f %%  " +
        f"φ=${d.reducedTempDiffC}%4.1f °С  θ=${d.heatFluxKwM2}%4.2f кВт/м²  Q=${d.heatGainW}%9.0f Вт")
    }
    line(f"  Итого Q4 = ${o.totalW}%.0f Вт")
    line()

    val t = r.totals
    line(f"Общий теплоприток Q = ${t.totalHeatGainW}%.0f Вт")
    line(f"Оттайка ${t.defrostHoursPerDay}%.0f ч/сут → потребная мощность ${t.requiredCoolerCapacityW}%.0f Вт")
    line(f"С запасом ${t.safetyFactorPct}%.0f %% → ${t.designCapacityW}%.0f Вт")
    line(f"На один воздухоохладитель: ${t.capacityPerCoolerW}%.1f Вт")
    line(f"Удельная нагрузка: ${t.specificLoadWPerM2}%.1f Вт/м²")
    sb.toString
