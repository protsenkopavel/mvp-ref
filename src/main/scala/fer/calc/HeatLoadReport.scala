package fer.calc

import io.swagger.v3.oas.annotations.media.Schema
import upickle.default.ReadWriter

/** Результат расчета по одному элементу ограждения. */
final case class EnclosureBlock(
    @Schema(description = "Элемент: «Стена А», «Потолок», «Пол», …") element: String,
    @Schema(description = "Коэффициент теплопередачи k, Вт/м²·К") heatTransferCoeffWm2K: Double,
    @Schema(description = "Площадь F, м²") areaM2: Double,
    @Schema(description = "Температура снаружи tнар, °С") outsideTemperatureC: Double,
    @Schema(description = "Температура в камере tкам, °С") roomTemperatureC: Double,
    @Schema(description = "Теплоприток Q = k·F·(tнар − tкам), Вт") heatGainW: Double
) derives ReadWriter

/** Q1 — теплопритоки через ограждающие конструкции. */
final case class EnclosureSection(
    @Schema(description = "Элементы ограждения") elements: List[EnclosureBlock],
    @Schema(description = "Солнечная радиация Qср, Вт") solarRadiationW: Double,
    @Schema(description = "Итого Q1, Вт") totalW: Double
) derives ReadWriter

/** Охлаждение груза или упаковки. */
final case class CoolingBlock(
    @Schema(description = "Масса, кг") massKg: Double,
    @Schema(description = "Энтальпия на входе iвх, кДж/кг") enthalpyInKjKg: Double,
    @Schema(description = "Энтальпия на выходе iвых, кДж/кг") enthalpyOutKjKg: Double,
    @Schema(description = "Время охлаждения τ, ч") coolingTimeHours: Double,
    @Schema(description = "Теплоприток Q = m·(iвх − iвых)/τ, Вт") heatGainW: Double
) derives ReadWriter

/** Теплопритоки от дыхания продукции. */
final case class RespirationBlock(
    @Schema(description = "Масса хранимого продукта mхр, тонн") storedMassT: Double,
    @Schema(description = "Теплота дыхания qд, Вт/тонна") respirationHeatWPerT: Double,
    @Schema(description = "Теплоприток Q = mхр·qд, Вт") heatGainW: Double
) derives ReadWriter

/** Q2 — теплопритоки от продукции. */
final case class ProductSection(
    @Schema(description = "Охлаждение продукта") productCooling: CoolingBlock,
    @Schema(description = "Охлаждение упаковки") packagingCooling: CoolingBlock,
    @Schema(description = "Дыхание продукции") respiration: RespirationBlock,
    @Schema(description = "Итого Q2, Вт") totalW: Double
) derives ReadWriter

/** Q3 — теплопритоки с наружным воздухом при вентиляции. */
final case class VentilationSection(
    @Schema(description = "Объем камеры VR, м³") roomVolumeM3: Double,
    @Schema(description = "Кратность вентилирования nв, крат/ч") airChangesPerHour: Double,
    @Schema(description = "Плотность воздуха ρ, кг/м³") airDensityKgM3: Double,
    @Schema(description = "Разность энтальпий Δh, кДж/кг") enthalpyDiffKjKg: Double,
    @Schema(description = "Итого Q3 = VR·nв·ρ·Δh/3,6, Вт") totalW: Double
) derives ReadWriter

/** Теплопритоки от освещения. */
final case class LightingBlock(
    @Schema(description = "Площадь помещения S, м²") areaM2: Double,
    @Schema(description = "Мощность освещения μ, Вт/м²") powerWPerM2: Double,
    @Schema(description = "Теплоприток Qосв = S·μ, Вт") heatGainW: Double
) derives ReadWriter

/** Теплопритоки от вентиляторов воздухоохладителей. */
final case class FanBlock(
    @Schema(description = "Мощность вентиляторов одного воздухоохладителя Nвент, Вт") fanPowerPerCoolerW: Double,
    @Schema(description = "Количество воздухоохладителей, шт") coolersCount: Int,
    @Schema(description = "Коэффициент одновременной работы ηэл") simultaneityCoeff: Double,
    @Schema(description = "Теплоприток Qвозд = Nвент·nвозд·ηэл, Вт") heatGainW: Double
) derives ReadWriter

/** Теплопритоки от работающих людей. */
final case class PeopleBlock(
    @Schema(description = "Количество работающих, чел") workersCount: Int,
    @Schema(description = "Тепловыделение одного человека, Вт") heatPerPersonW: Double,
    @Schema(description = "Коэффициент присутствия") presenceCoeff: Double,
    @Schema(description = "Теплоприток, Вт") heatGainW: Double
) derives ReadWriter

/** Теплопритоки от подъемно-транспортных средств. */
final case class VehiclesBlock(
    @Schema(description = "Суммарная мощность ΣNПТС, кВт") totalPowerKw: Double,
    @Schema(description = "Коэффициент загрузки ψПТС, %") loadFactorPct: Double,
    @Schema(description = "Теплоприток QПТС = ΣNПТС·ψПТС, Вт") heatGainW: Double
) derives ReadWriter

/** Инфильтрация через одну дверь/ворота. */
final case class DoorBlock(
    @Schema(description = "Наименование двери") name: String,
    @Schema(description = "Количество, шт") count: Int,
    @Schema(description = "Площадь проема Fдв, м²") areaM2: Double,
    @Schema(description = "Коэффициент открытия β, %") openingCoefficientPct: Double,
    @Schema(description = "Эффективность занавеса ω") curtainEfficiency: Double,
    @Schema(description = "Приведенная разность температур φ = ζ·(tнар − tкам), °С") reducedTempDiffC: Double,
    @Schema(description = "Плотность теплового потока θ (по диаграмме), кВт/м²") heatFluxKwM2: Double,
    @Schema(description = "Теплоприток Qинф, Вт") heatGainW: Double
) derives ReadWriter

/** Q4 — эксплуатационные теплопритоки. */
final case class OperationalSection(
    @Schema(description = "Освещение") lighting: LightingBlock,
    @Schema(description = "Вентиляторы воздухоохладителей") fans: FanBlock,
    @Schema(description = "Люди") people: PeopleBlock,
    @Schema(description = "ПТС") vehicles: VehiclesBlock,
    @Schema(description = "Двери и ворота") doors: List[DoorBlock],
    @Schema(description = "Итого Q4, Вт") totalW: Double
) derives ReadWriter

/** Итоговые показатели расчета. */
final case class TotalsSection(
    @Schema(description = "Общий теплоприток Q = Q1+Q2+Q3+Q4, Вт") totalHeatGainW: Double,
    @Schema(description = "Время оттайки в сутки, ч") defrostHoursPerDay: Double,
    @Schema(description = "Потребная мощность с учетом оттайки Q·24/(24 − tотт), Вт") requiredCoolerCapacityW: Double,
    @Schema(description = "Коэффициент запаса K, %") safetyFactorPct: Double,
    @Schema(description = "Расчетная мощность с учетом запаса, Вт") designCapacityW: Double,
    @Schema(description = "Мощность на один воздухоохладитель, Вт") capacityPerCoolerW: Double,
    @Schema(description = "Удельная нагрузка, Вт/м²") specificLoadWPerM2: Double
) derives ReadWriter

/** Полный отчет расчета теплопритоков по блокам Q1–Q4 с итогами. */
final case class HeatLoadReport(
    @Schema(description = "Номер камеры") roomNumber: String,
    @Schema(description = "Наименование камеры") roomName: String,
    @Schema(description = "Город расчетных параметров") designCity: String,
    @Schema(description = "Расчетная температура наружного воздуха, °С") outdoorTemperatureC: Double,
    @Schema(description = "Расчетная влажность наружного воздуха, %") outdoorHumidityPct: Double,
    @Schema(description = "Q1 — ограждающие конструкции") q1Enclosures: EnclosureSection,
    @Schema(description = "Q2 — продукция") q2Product: ProductSection,
    @Schema(description = "Q3 — вентиляция") q3Ventilation: VentilationSection,
    @Schema(description = "Q4 — эксплуатационные") q4Operational: OperationalSection,
    @Schema(description = "Итоги") totals: TotalsSection
) derives ReadWriter
