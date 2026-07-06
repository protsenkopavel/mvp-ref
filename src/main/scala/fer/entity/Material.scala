package fer.entity

import io.swagger.v3.oas.annotations.media.Schema
import upickle.default.ReadWriter

/** Материал ограждения или теплоизоляции. */
final case class Material(
    @Schema(description = "Наименование материала") name: String,
    @Schema(description = "Коэффициент теплопроводности λ, Вт/м·К") thermalConductivity: Double
) derives ReadWriter

object Material:
  val Absent: Material          = Material("Отсутствует", 0.0)
  val Concrete: Material        = Material("Бетон", 1.750)
  val Brick: Material           = Material("Кирпич", 0.440)
  val MineralWool: Material     = Material("Минвата", 0.046)
  val Pir: Material             = Material("ПИР", 0.024)
  val Polystyrene: Material     = Material("Пенополистирол", 0.034)
  val PpuSuperPremium: Material = Material("ППУ «Суперпремиум»", 0.018)
  val PenoplexM45: Material     = Material("Пеноплэкс М45", 0.032)
