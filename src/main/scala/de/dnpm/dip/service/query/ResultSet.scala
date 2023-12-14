package de.dnpm.dip.service.query


import de.dnpm.dip.coding.Coding
import de.dnpm.dip.model.{
  Id,
  Patient,
  Gender,
  Interval,
  Site
}
import play.api.libs.json.{
  Json,
  OWrites
}


trait ResultSet[PatientRecord,Criteria]
{
  self =>

  type SummaryType <: ResultSet.Summary

  val id: Query.Id

  def summary(
    f: PatientRecord => Boolean = _ => true
  ): SummaryType


  def patientMatches(
    f: PatientRecord => Boolean = _ => true
  ): Seq[PatientMatch[Criteria]]


  def patientRecord(
    patId: Id[Patient]
  ): Option[PatientRecord] 

}


object ResultSet
{

  final case class Demographics
  (
    gender: Seq[ConceptCount[Coding[Gender.Value]]],
    age: Seq[ConceptCount[Interval[Int]]],
    site: Seq[ConceptCount[Coding[Site]]]
  )

  object Demographics extends ReportingOps
  {
    def on(patients: Seq[Patient]) = {


      ResultSet.Demographics(
        DistributionOf(patients.map(_.gender)),
        AgeDistribution(patients.map(_.age)),
        DistributionOf(patients.flatMap(_.managingSite))
      )
    }
  }


  trait Summary
  {
    val id: Query.Id
    val numPatients: Int
    val demographics: Demographics
  }


  implicit val writesDemographics: OWrites[Demographics] =
    Json.writes[Demographics]

}



/*
trait ResultSet[PatientRecord,Criteria]
{
  self =>

  val id: Query.Id

  def summary(
    f: PatientRecord => Boolean = _ => true
  ): ResultSet.Summary


  def patientMatches(
    f: PatientRecord => Boolean = _ => true
  ): Seq[PatientMatch[Criteria]]


  def patientRecord(
    patId: Id[Patient]
  ): Option[PatientRecord] 

}


object ResultSet
{

  final case class Demographics
  (
    gender: Seq[ConceptCount[Coding[Gender.Value]]],
    age: Seq[ConceptCount[Interval[Int]]],
    site: Seq[ConceptCount[Coding[Site]]]
  )

  final case class Summary
  (
    id: Query.Id,
    numPatients: Int,
    demographics: Demographics
  )


  implicit val writesDemographics: OWrites[Demographics] =
    Json.writes[Demographics]

  implicit val writesSummary: OWrites[Summary] =
    Json.writes[Summary]

}



trait ResultSet[PatientRecord,Criteria]
{
  self =>

  type Summary <: ResultSet.Summary


  val id: Query.Id

  def summary(
    f: PatientRecord => Boolean = _ => true
  ): Summary


  def patientMatches(
    f: PatientRecord => Boolean = _ => true
  ): Seq[PatientMatch[Criteria]]


  def patientRecord(
    patId: Id[Patient]
  ): Option[PatientRecord] 

}

object ResultSet
{

  final case class Demographics 
  (
    gender: Seq[ConceptCount[Coding[Gender.Value]]],
    age: Seq[ConceptCount[Interval[Int]]],
    site: Seq[ConceptCount[Coding[Site]]]
  )


  trait Summary
  {

    val id: Query.Id

    def numPatients: Int

    def demographics: Demographics

  }

}
*/

