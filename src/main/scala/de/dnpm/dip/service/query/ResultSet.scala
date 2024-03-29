package de.dnpm.dip.service.query


import de.dnpm.dip.coding.Coding
import de.dnpm.dip.model.{
  Id,
  Patient,
  Gender,
  Interval,
  Site,
  Snapshot
}
import play.api.libs.json.{
  Json,
  OWrites
}

trait ResultSet[
  PatientRecord <: { def patient: Patient },
  Criteria
]
{
  self =>

  import scala.util.chaining._
  import scala.language.reflectiveCalls


  type SummaryType <: ResultSet.Summary


  def id: Query.Id

  def results: Seq[(Snapshot[PatientRecord],Criteria)]


  protected def records(
    f: PatientRecord => Boolean
  ): Seq[PatientRecord] =
    results
      .collect {
        case (Snapshot(patRec,_),_) if f(patRec) => patRec
      }


  def summary(
    f: PatientRecord => Boolean = _ => true
  ): SummaryType


  def patientMatches(
    f: PatientRecord => Boolean = _ => true
  ): Seq[PatientMatch[Criteria]] =
    results
      .collect {
        case (Snapshot(patRec,_),matchingCriteria) if f(patRec) =>
          PatientMatch.of(
            patRec.patient,
            matchingCriteria
          )
      }


  def patientRecord(
    patId: Id[Patient]
  ): Option[PatientRecord] =
    results
      .collectFirst {
        case (Snapshot(patRec,_),_) if patRec.patient.id == patId => patRec
      }

}



object ResultSet
{

  final case class Demographics
  (
    genderDistribution: Distribution[Coding[Gender.Value]],
    ageDistribution: Distribution[Interval[Int]],
    siteDistribution: Distribution[Coding[Site]]
  )

  object Demographics extends ReportingOps
  {
    def on(patients: Seq[Patient]) =
      ResultSet.Demographics(
        Distribution.of(patients.map(_.gender)),
        Distribution.ofAge(patients.map(_.age)),
        Distribution.of(patients.flatMap(_.managingSite))
      )
  }


  trait Summary
  {
    val id: Query.Id
    val patientCount: Int
    val demographics: Demographics
  }


  implicit val writesDemographics: OWrites[Demographics] =
    Json.writes[Demographics]

}
