package fer.calc

import io.swagger.v3.oas.annotations.media.Schema
import upickle.default.ReadWriter

/** Справочные параметры расчета. */
final case class CalcInputs(
    @Schema(description = "Теплоемкость продукта (смеси), кДж/кг·К") productHeatCapacityKjKgK: Double,
    @Schema(description = "Теплоемкость упаковки, кДж/кг·К") packagingHeatCapacityKjKgK: Double,
    @Schema(description = "Температура отсчета энтальпии продукта, °С") enthalpyReferenceTempC: Double,
    @Schema(description = "Теплота дыхания продукции qд, Вт/тонна") respirationHeatWPerT: Double,
    @Schema(description = "Энтальпия приточного воздуха, кДж/кг (i-d диаграмма)") supplyAirEnthalpyKjKg: Double,
    @Schema(description = "Энтальпия воздуха в камере, кДж/кг") roomAirEnthalpyKjKg: Double,
    @Schema(description = "Плотность воздуха в камере ρ, кг/м³") roomAirDensityKgM3: Double,
    @Schema(description = "Тепловыделение одного человека при температуре камеры, Вт") heatPerPersonW: Double,
    @Schema(description = "Мощность электродвигателей одного воздухоохладителя, Вт") fanMotorPowerPerCoolerW: Double,
    @Schema(description = "Коэффициент одновременной работы воздухоохладителей ηэл (1.0 = 100 %)") fanSimultaneityCoeff: Double,
    @Schema(description = "Суммарная мощность ПТС ΣNПТС, кВт; null — считается из паспортных мощностей") totalVehiclePowerKwOverride: Option[Double],
    @Schema(description = "Диаграмма: приведенная разность температур φ, °С → плотность теплового потока θ, кВт/м²") doorHeatFluxDiagramKwM2: List[(Double, Double)],
    @Schema(description = "θ по дверям, снятые с диаграммы вручную (имя двери → кВт/м²); приоритет над диаграммой") doorThetaOverridesKwM2: Map[String, Double],
    @Schema(description = "Коэффициент наличия тамбура ζ") doorTambourCoeff: Double,
    @Schema(description = "Теплоприток от солнечной радиации Qср, Вт") solarRadiationW: Double,
    @Schema(description = "Коэффициент теплоотдачи наружной поверхности αн, Вт/м²·К") outerSurfaceCoeffWm2K: Double,
    @Schema(description = "Коэффициент теплоотдачи внутренней поверхности αвн, Вт/м²·К") innerSurfaceCoeffWm2K: Double,
    @Schema(description = "Время на оттаивание испарителей в сутки, ч") defrostHoursPerDay: Double,
    @Schema(description = "Коэффициент запаса K, %") safetyFactorPct: Double,
    @Schema(description = "Город для расчетных параметров наружного воздуха") designCity: String,
    @Schema(description = "Расчетная температура наружного воздуха, °С") outdoorTemperatureC: Double,
    @Schema(description = "Расчетная влажность наружного воздуха, %") outdoorHumidityPct: Double
) derives ReadWriter
