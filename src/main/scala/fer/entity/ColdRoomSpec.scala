package fer.entity

import io.swagger.v3.oas.annotations.media.Schema
import upickle.default.ReadWriter

/** Слой ограждения. */
final case class MaterialLayer(
    @Schema(description = "Материал слоя") material: Material,
    @Schema(description = "Толщина слоя, мм") thicknessMm: Double
) derives ReadWriter

/** Элемент ограждения камеры. */
final case class Enclosure(
    @Schema(description = "Несущее ограждение") structure: MaterialLayer,
    @Schema(description = "Теплоизоляция") insulation: MaterialLayer,
    @Schema(description = "Температура снаружи ограждения, °С") outsideTemperatureC: Double
) derives ReadWriter

/** Внутренние размеры камеры. */
final case class RoomDimensions(
    @Schema(description = "Длина (стороны А, В), м") lengthM: Double,
    @Schema(description = "Ширина (стороны Б, Г), м") widthM: Double,
    @Schema(description = "Высота H, м") heightM: Double
) derives ReadWriter:
  def areaM2: Double = lengthM * widthM
  def volumeM3: Double = areaM2 * heightM

/** Состояние воздуха. */
final case class AirCondition(
    @Schema(description = "Температура, °С") temperatureC: Double,
    @Schema(description = "Относительная влажность, %") relativeHumidityPct: Double
) derives ReadWriter

/** Суточный оборот или загрузка продукции. */
final case class CargoTurnover(
    @Schema(description = "Количество паллет, шт") pallets: Int,
    @Schema(description = "Масса, тонн") tonnes: Double
) derives ReadWriter

/** Дверь или ворота камеры. */
final case class Door(
    @Schema(description = "Наименование двери") name: String,
    @Schema(description = "Количество, шт") count: Int,
    @Schema(description = "Время открытия ворот за один проход") openTimePerPassSec: Option[Double],
    @Schema(description = "Время открытия, часов в сутки — для дверей без учета проходов") openHoursPerDay: Option[Double],
    @Schema(description = "Ширина проема, м") widthM: Double,
    @Schema(description = "Высота проема, м") heightM: Double,
    @Schema(description = "Коэффициент открытия дверей β, %") openingCoefficientPct: Double,
    @Schema(description = "Температура снаружи двери, °С") outsideTemperatureC: Double,
    @Schema(description = "Доля оборота через дверь, %") turnoverSharePct: Option[Double],
    @Schema(description = "Занавес") curtain: Option[String]
) derives ReadWriter:
  def areaM2: Double = widthM * heightM

/** Подъемно-транспортное средство, находящееся в камере. */
final case class HandlingVehicle(
    @Schema(description = "Тип ПТС") name: String,
    @Schema(description = "Мощность ходового двигателя, кВт") travelMotorKw: Double,
    @Schema(description = "Мощность двигателя подъема, кВт") liftMotorKw: Double,
    @Schema(description = "Коэффициент использования двигателей, %") utilizationPct: Double,
    @Schema(description = "Количество, шт") count: Int,
    @Schema(description = "Время на паллету, мин") timePerPalletMin: Double,
    @Schema(description = "Запас времени, %") reservePct: Double,
    @Schema(description = "Скорость движения, км/ч") travelSpeedKmh: Double,
    @Schema(description = "Скорость подъема, м/с") liftSpeedMps: Double,
    @Schema(description = "Высота подъема, м") liftHeightM: Double,
    @Schema(description = "Время на паллету (на проезд), с") timePerPassSec: Double
) derives ReadWriter

/** Режим работы камеры. */
final case class OperatingMode(
    @Schema(description = "п. 1 — тип холодильника") refrigeratorType: String,
    @Schema(description = "п. 2 — продукт") product: String,
    @Schema(description = "п. 3 — температура входящего продукта, °С") incomingProductTempC: Double,
    @Schema(description = "п. 4 — воздух в камере") roomAir: AirCondition,
    @Schema(description = "п. 5 — конечная температура продукта, °С") finalProductTempC: Double,
    @Schema(description = "п. 6 — время процесса охлаждения, ч") coolingTimeHours: Double,
    @Schema(description = "п. 7 — время работы погрузочной техники (смена), ч") equipmentShiftHours: Double,
    @Schema(description = "п. 8 — наибольший суточный оборот: загрузка") dailyLoading: CargoTurnover,
    @Schema(description = "п. 9 — наибольший суточный оборот: выгрузка") dailyUnloading: CargoTurnover,
    @Schema(description = "Масса паллеты, тонн/ед") palletMassT: Double,
    @Schema(description = "п. 10 — масса продукта в камере") storedProduct: CargoTurnover,
    @Schema(description = "п. 11 — упаковка") packaging: String,
    @Schema(description = "п. 12 — масса охлаждаемой упаковки, тонн") packagingMassT: Double,
    @Schema(description = "п. 13 — кратность вентилирования (приток), крат/ч") ventilationAirChangesPerHour: Double,
    @Schema(description = "п. 14 — КПД рекуператора системы вентиляции, %") recuperatorEfficiencyPct: Double,
    @Schema(description = "п. 15 — воздух после рекуператора") airAfterRecuperator: AirCondition,
    @Schema(description = "п. 16 — воздух в окружающих помещениях") ambientRoomsAir: AirCondition,
    @Schema(description = "п. 17 — приточный воздух") supplyAir: AirCondition,
    @Schema(description = "п. 18 — двери и ворота камеры") doors: List[Door],
    @Schema(description = "п. 19 — занавес двери") doorCurtain: Option[String],
    @Schema(description = "п. 20 — количество работающих в камере, чел") workersCount: Int,
    @Schema(description = "п. 21 — мощность освещения, Вт/м²") lightingPowerWPerM2: Double,
    @Schema(description = "п. 22 — ПТС, находящиеся в камере одновременно") vehicles: List[HandlingVehicle],
    @Schema(description = "п. 23 — коэффициент загрузки ПТС за смену, %") vehicleLoadFactorPct: Double,
    @Schema(description = "п. 24 — количество воздухоохладителей на камеру, шт") airCoolersCount: Int,
    @Schema(description = "п. 25 — расстояние до агрегатной, м") distanceToMachineRoomM: Option[Double]
) derives ReadWriter

/** Технические условия на подбор холодильного оборудования для охлаждаемой камеры. */
final case class ColdRoomSpec(
    @Schema(description = "Номер камеры") roomNumber: String,
    @Schema(description = "Наименование камеры") name: String,
    @Schema(description = "Внутренние размеры камеры") dimensions: RoomDimensions,
    @Schema(description = "Ограждение стены А") wallA: Enclosure,
    @Schema(description = "Ограждение стены Б") wallB: Enclosure,
    @Schema(description = "Ограждение стены В") wallV: Enclosure,
    @Schema(description = "Ограждение стены Г") wallG: Enclosure,
    @Schema(description = "Ограждение потолка") ceiling: Enclosure,
    @Schema(description = "Ограждение пола") floor: Enclosure,
    @Schema(description = "Режим работы камеры") mode: OperatingMode
) derives ReadWriter
